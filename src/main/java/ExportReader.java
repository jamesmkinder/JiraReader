import org.codehaus.jettison.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class ExportReader {
    public static void runExport(String raw, int issue_id) throws IOException, URISyntaxException {
        if (raw.equals("null")) return;
        String[] files = raw.split("- ");
        Map<String, String> filesMap = new HashMap<>();

        for (String file : files) {
            String[] split = file.split("\\|");
            if (split.length != 2) continue;
            split[0] = split[0].trim();
            split[1] = split[1].trim();
            filesMap.put(split[0], split[1]);
        }

        SimpleDateFormat insertFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        for (Map.Entry<String, String> file : filesMap.entrySet()) {
            String[] split = file.getKey().split("\\.");
            if (split.length < 2) continue;
            if (split[1].equals("gcpro") || split[1].equals("gcplus")) {
                System.out.println(file.getKey());
                File gcpFile = FileGetter.getFile(file.getValue());
                GCPDriver.exportJSON(gcpFile);
            }
        }

    }
}
