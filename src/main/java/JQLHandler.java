import JiraModel.ChangeLog;
import JiraModel.Issue;
import JiraModel.IssueValue;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityExistsException;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Takes a JQL string and Hibernate session, queries the Jira API, then persists the resulting data into the session
 * database.
 */

public class JQLHandler {

    /**
     * Takes a JQL string and Hibernate session, queries the Jira API, then persists the resulting data into the session
     * database.
     * @param jql The JQL query.
     * @param session The Hibernate session.
     * @throws UnirestException
     * @throws ParseException
     */
    public static void handleJQL(String jql, Session session) throws UnirestException, ParseException {
        JSONObject node = getPage(0, 1, jql, "schema, names").getBody().getObject();
        JSONObject valueNames = node.getJSONObject("names");
        Map<String, String> valueMap = new HashMap<>();
        valueNames.keySet().forEach(keyStr ->
        {
            String keyValue = valueNames.getString(keyStr);
            valueMap.put(keyStr, keyValue);
        });

        int total = (int) node.get("total");
        int resultSize = 1;
        System.out.println("Total = " + total);
        for (int i = 0; i < total; i += resultSize) {
            JSONObject page;
            while(true){
                try {
                    page = getPage(i, 100, jql, "changelog").getBody().getObject();
                    break;
                } catch (UnirestException e) {
                    System.out.println("trying again...");
                }
            }
            resultSize = page.getInt("maxResults");
            JSONArray issues = page.getJSONArray("issues");
            System.out.println("page: " + i);
            for (int j = 0; j < 100; j++) {
                JSONObject issue;
                try {
                    issue = issues.getJSONObject(j);
                } catch (JSONException e) {continue;}
                Issue newIssue = new Issue(issue.getString("self"), issue.getInt("id"), issue.getString("key"));
                try {
                    session.persist(newIssue);
                    session.flush();
                } catch (EntityExistsException e) {
                    return;
                }
                addValues(newIssue.getId(), issue.getJSONObject("fields"), valueMap, session, "");
                addChangelogs(newIssue.getId(), issue.getJSONObject("changelog"), session);
            }
        }
    }

    /**
     * Helper method that queries the Jira API with a GET request, and returns an HttpResponse with a maximum of
     * 100 tickets.
     * @param start Offset for the result set.  A start of 0 will return the first 100 tickets, a start of 100 will return
     *              the second 100 tickets, etc.
     * @param jql The JQL query.
     * @param expand Any extra related data objects that should be included in the search results, e.g. changelogs.
     * @return An HttpResponse with the resulting JSON.
     * @throws UnirestException
     */

    public static HttpResponse<JsonNode> getPage(int start, int max, String jql, String expand) throws UnirestException {

        System.out.println("Sending request..." + start);
        return Unirest.get("https://extron.atlassian.net/rest/api/2/search")
                .basicAuth("jkinder@extron.com", Dotenv.configure().load().get("TOKEN"))
                .header("Accept", "application/json")
                .header("Timeout", "30000")
                .queryString("jql", jql)
                .queryString("maxResults", 100)
                .queryString("startAt", start)
                .queryString("expand", expand)
                .asJson();
    }

    /**
     * Adds the changelogs JSON object to the database with the associated issue primary key.
     * @param issueId The primary key ID of the related issue.
     * @param changelogs The changelogs JSON object
     * @param session The Hibernate session.
     * @throws ParseException
     */
    public static void addChangelogs(int issueId, JSONObject changelogs, Session session) throws ParseException {
        JSONArray histories = changelogs.getJSONArray("histories");

        for (int i = 0; i < changelogs.getInt("maxResults"); i++) {
            JSONObject history = histories.getJSONObject(i);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            int id = history.getInt("id");
            String displayName = history.getJSONObject("author").getString("displayName");
            JSONArray items = history.getJSONArray("items");
            String field = "";
            String fromString = "";
            String toString = "";
            if (items.length() != 0) {
                field = history.getJSONArray("items").getJSONObject(0).getString("field");
                fromString = history.getJSONArray("items").getJSONObject(0).get("fromString") instanceof String ? history.getJSONArray("items").getJSONObject(0).getString("fromString") : "null";
                toString = history.getJSONArray("items").getJSONObject(0).get("toString") instanceof String ? history.getJSONArray("items").getJSONObject(0).getString("toString") : "null";
            }
            session.persist(new ChangeLog(id, displayName, field, fromString, toString, new Timestamp(formatter.parse(history.getString("created")).getTime()), issueId));
            session.flush();
        }
    }

    /**
     * Recursively adds the values JSON object to the database with the associated issue primary key.
     * @param issueId The primary key ID of the related issue.
     * @param values The values JSON object.
     * @param valueMap A mapping of custom value IDs to human-readable values.
     * @param session The Hibernate session.
     * @param parentName The value of the parent JSON object in the case of nested JSON values, blank otherwise.
     */
    public static void addValues(int issueId, JSONObject values, Map<String,String> valueMap, Session session, String parentName) {
        values.keySet().forEach(keyStr -> {
            if (values.get(keyStr) == JSONObject.NULL) return;
            Object keyValue = values.get(keyStr);
            String valueName = valueMap.get(keyStr) == null ? keyStr : valueMap.get(keyStr);
            if (keyValue instanceof JSONObject) {
                IssueValue parent = new IssueValue(valueName, "Parent", parentName, issueId);
                try{
                    session.persist(parent);
                    session.flush();
                } catch (EntityExistsException ignored) {}
                addValues(issueId, (JSONObject) keyValue, valueMap, session, parent.getValueName());
            } else {
                IssueValue parent = new IssueValue(valueName, keyValue.toString(), parentName, issueId);
                try{
                    session.persist(parent);
                    session.flush();
                } catch (EntityExistsException ignored) {}
            }
        });
    }
}
