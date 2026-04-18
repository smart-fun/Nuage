package fr.arnaudguyon.nuage.json;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.nuage.model.DatabaseSchema;
import fr.arnaudguyon.nuage.model.TableSchema;

public class DatabaseSerializer {

    private static final String KEY_TABLES = "tables";
    private static final String KEY_TIMESTAMP = "timestamp";

    public static JSONObject serialize(@NonNull DatabaseSchema schema) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_TIMESTAMP, schema.getLastUpdateTimestamp());

        JSONArray tablesArray = new JSONArray();
        for (TableSchema tableSchema : schema.getTables()) {
            tablesArray.put(TableSerializer.serializeDefinition(tableSchema));
        }
        json.put(KEY_TABLES, tablesArray);

        return json;
    }
}
