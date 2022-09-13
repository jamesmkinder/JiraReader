import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.*;

public class JSONHelpers {

    public static List<Map<String, String>> getJSONValues(JSONObject json) throws JSONException {
        List<Map<String, String>> solution = new ArrayList<>();

        JSONObject fileInfo = json.getJSONObject("FileInfo");
        JSONObject projectInfo = json.getJSONObject("ProjectInfo");
        JSONArray drivers = json.getJSONArray("Drivers");
        JSONArray config = json.getJSONArray("Configuration");

        Map<String, String> fileInfoValues = new HashMap<>();
        Map<String, String> projectInfoValues = new HashMap<>();
        Map<String, String> driverValues = new HashMap<>();
        Map<String, String> configValues = new HashMap<>();

        parse(fileInfo, fileInfoValues);
        parse(projectInfo, projectInfoValues);
        parseArray(drivers, driverValues);
        parseArray(config, configValues);

        solution.add(fileInfoValues);
        solution.add(projectInfoValues);
        solution.add(driverValues);
        solution.add(configValues);
        return solution;
    }

    public static void parse(JSONObject jsonObject, Map<String, String> target) throws JSONException {
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (jsonObject.get(key).getClass() == JSONObject.class) parse(jsonObject.getJSONObject(key), target);
            else if (jsonObject.get(key).getClass() == JSONArray.class) parseArray(jsonObject.getJSONArray(key), target);
            else {
                if (target.get(key) == null) target.put(key, jsonObject.getString(key));
                else target.put(key + "dupe", jsonObject.getString(key));
            }
        }
    }
    public static void parseArray(JSONArray jsonArray, Map<String, String> target) throws JSONException {

        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.get(i).getClass() == JSONObject.class) parse(jsonArray.getJSONObject(i), target);
            else if (jsonArray.get(i).getClass() == JSONArray.class) parseArray(jsonArray.getJSONArray(i), target);
            else {
                target.put(jsonArray.toString(), jsonArray.getString(i));
            }
        }
    }

}
