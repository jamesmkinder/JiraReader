import JiraModel.ChangeLog;
import JiraModel.Issue;
import JiraModel.IssueValue;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
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

public class JQLHandler
{

    public static void handleJQL(String jql, Session session) throws UnirestException, ParseException {
        JSONObject valueNames = getPage(0, jql, "schema, names").getBody().getObject().getJSONObject("names");
        Map<String, String> valueMap = new HashMap<>();
        valueNames.keySet().forEach(keyStr ->
        {
            String keyValue = valueNames.getString(keyStr);
            valueMap.put(keyStr, keyValue);
        });

        int total = (int) getPage(0, jql, "").getBody().getObject().get("total");
        int resultSize = 1;
        System.out.println("Total = " + total);
        for (int i = 0; i < total; i += resultSize) {
            JSONObject page;
            try {
                page = getPage(i, jql, "changelog").getBody().getObject();
            } catch (UnirestException e) {
                continue;
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


    public static HttpResponse<JsonNode> getPage(int start, String jql, String expand) throws UnirestException {

        return Unirest.get("https://extron.atlassian.net/rest/api/2/search")
                .basicAuth("jkinder@extron.com", "cmTvpvaP4iUTfTVp0Xc33090")
                .header("Accept", "application/json")
                .queryString("jql", jql)
                .queryString("maxResults", 100)
                .queryString("startAt", start)
                .queryString("expand", expand)
                .asJson();
    }

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
