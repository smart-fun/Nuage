package fr.arnaudguyon.nuage.json;

import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public abstract class JsonUtils {

    private static final int INDENT_SPACES = 4;

    @NonNull
    public static byte[] toBytes(@NonNull JSONObject json) throws JSONException {
        String jsonString = json.toString(INDENT_SPACES);
        return jsonString.getBytes(StandardCharsets.UTF_8);
    }

    @NonNull
    public static JSONObject fromBytes(@NonNull byte[] data) throws JSONException {
        String jsonString = new String(data, StandardCharsets.UTF_8);
        return new JSONObject(jsonString);
    }
}
