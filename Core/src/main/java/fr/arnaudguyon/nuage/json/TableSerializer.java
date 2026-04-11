package fr.arnaudguyon.nuage.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import fr.arnaudguyon.nuage.database.NuageColumn;
import fr.arnaudguyon.nuage.database.NuageTable;

public abstract class TableSerializer {

    /**
     * Converts a table structure to JSON.
     * Format: { "table_name": "users", "columns": [ {"name": "uuid", "type": "STRING"}, ... ] }
     */
    public static JSONObject serializeDefinition(NuageTable table) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("table_name", table.getTableName());

        JSONArray columnsArray = new JSONArray();
        Map<String, NuageColumn.Type> types = table.getColumnTypes();

        for (Map.Entry<String, NuageColumn.Type> entry : types.entrySet()) {
            JSONObject columnJson = new JSONObject();
            columnJson.put("name", entry.getKey());
            columnJson.put("type", entry.getValue().name());
            columnsArray.put(columnJson);
        }

        json.put("columns", columnsArray);
        return json;
    }
}
