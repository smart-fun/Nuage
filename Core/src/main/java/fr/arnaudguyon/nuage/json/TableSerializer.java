package fr.arnaudguyon.nuage.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.nuage.model.ColumnModel;
import fr.arnaudguyon.nuage.model.TableSchema;

public abstract class TableSerializer {

    /**
     * Converts a table structure to JSON.
     * Format: { "table_name": "users", "columns": [ {"name": "uuid", "type": "STRING"}, ... ] }
     */
    public static JSONObject serializeDefinition(TableSchema schema) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("table_name", schema.getTableName());

        JSONArray columnsArray = new JSONArray();
        for (ColumnModel column : schema.getColumns()) {
            JSONObject columnJson = new JSONObject();
            columnJson.put("name", column.getName());
            columnJson.put("type", column.getType().name());
            columnsArray.put(columnJson);
        }

        json.put("columns", columnsArray);
        return json;
    }

}
