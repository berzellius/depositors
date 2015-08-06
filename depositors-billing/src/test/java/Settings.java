import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by berz on 12.01.15.
 */
public class Settings {
    public String getPathToUploads() {
        return "C:\\projects\\uploads";
    }

    public HashMap<String, String> getDatabaseConnectionConfig() {
        HashMap<String, String> dbConnect = new LinkedHashMap<String, String>();
        dbConnect.put("path","jdbc:postgresql://localhost:5432/depositors");
        dbConnect.put("database", "postgres");
        dbConnect.put("password", "postgres");

        return dbConnect;
    }
}
