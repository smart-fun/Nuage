package fr.arnaudguyon.nuage.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.nuage.model.ColumnModel;
import fr.arnaudguyon.nuage.model.ColumnType;
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


    public static TableSchema deserializeTableDefinition(JSONObject json) throws JSONException {
        String tableName = json.getString("table_name");
        TableSchema tableSchema = new TableSchema(tableName);

        JSONArray columnsArray = json.optJSONArray("columns");
        if (columnsArray != null) {
            for (int i = 0; i < columnsArray.length(); i++) {
                JSONObject colJson = columnsArray.getJSONObject(i);
                String name = colJson.getString("name");
                String typeStr = colJson.getString("type");

                // Sécurité : si le type dans le JSON n'existe pas dans l'Enum, on met STRING par défaut
                ColumnType type;
                try {
                    type = ColumnType.valueOf(typeStr);
                } catch (IllegalArgumentException e) {
                    type = ColumnType.STRING;
                }

                tableSchema.addColumn(new ColumnModel(name, type));
            }
        }
        return tableSchema;
    }

}
