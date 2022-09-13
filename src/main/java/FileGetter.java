import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FileGetter {

    public static File getFile(String url) throws IOException, URISyntaxException {
        Desktop desktop = java.awt.Desktop.getDesktop();
        URI oURL = new URI(url);
        desktop.browse(oURL);



        return new File("\\\\usa-rnc-home3\\home\\James Kinder\\GCP Projects\\Automation\\James Kinder.gcplus");
    }


    public static void main(String[] args) {
        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI oURL = new URI("http://intranet.extron.com/inc/uploadfiles/FileStream/DisplayFileStream.aspx?tablename=Techsheet&guid=3cfe17a9-4d9d-4648-a622-ef21ce5441d0");
            desktop.browse(oURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
