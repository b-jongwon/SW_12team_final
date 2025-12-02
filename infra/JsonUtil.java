
package infra;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class JsonUtil {

    private static final Gson gson = new Gson();

    public static <T> T readJson(String path, Type typeOfT) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, typeOfT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeJson(String path, Object data) {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
