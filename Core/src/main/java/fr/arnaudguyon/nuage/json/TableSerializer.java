package fr.arnaudguyon.nuage.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.nuage.model.ColumnModel;
import fr.arnaudguyon.nuage.model.ColumnType;
import fr.arnaudguyon.nuage.model.TableSchema;

public abstract class TableSerializer {

    public static final String KEY_TABLE_NAME = "table_name";
    public static final String KEY_COLUMNS = "columns";
    public static final String KEY_COLUMN_NAME = "name";
    public static final String KEY_COLUMN_TYPE = "type";
    public static final String KEY_COLUMN_IS_PRIMARY_KEY = "primary_key";
    public static final String KEY_COLUMN_IS_NULLABLE = "nullable";

    /**
     * Converts a table structure to JSON.
     * Format: { "table_name": "users", "columns": [ {"name": "uuid", "type": "STRING"}, ... ] }
     */
    public static JSONObject serializeDefinition(TableSchema schema) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_TABLE_NAME, schema.getTableName());

        JSONArray columnsArray = new JSONArray();
        for (ColumnModel column : schema.getColumns()) {
            JSONObject columnJson = new JSONObject();
            columnJson.put(KEY_COLUMN_NAME, column.getName());
            columnJson.put(KEY_COLUMN_TYPE, column.getType().name());
            if (column.isPrimaryKey()) {
                columnJson.put(KEY_COLUMN_IS_PRIMARY_KEY, true);
            } else {
                // a column can't be nullable + primary. Default nullable value is true
                if (!column.isNullable()) {
                    columnJson.put(KEY_COLUMN_IS_NULLABLE, false);
                }
            }
            columnsArray.put(columnJson);
        }

        json.put(KEY_COLUMNS, columnsArray);
        return json;
    }


    public static TableSchema deserializeTableDefinition(JSONObject json) throws JSONException {
        String tableName = json.getString(KEY_TABLE_NAME);
        TableSchema tableSchema = new TableSchema(tableName);

        JSONArray columnsArray = json.optJSONArray(KEY_COLUMNS);
        if (columnsArray != null) {
            for (int i = 0; i < columnsArray.length(); i++) {
                JSONObject colJson = columnsArray.getJSONObject(i);
                String name = colJson.getString(KEY_COLUMN_NAME);
                String typeStr = colJson.getString(KEY_COLUMN_TYPE);
                boolean isPrimaryKey = colJson.optBoolean(KEY_COLUMN_IS_PRIMARY_KEY, false);
                boolean isNullable = colJson.optBoolean(KEY_COLUMN_IS_NULLABLE, true);
                // If the type in JSON is not defined in the Enum, default to STRING
                ColumnType type;
                try {
                    type = ColumnType.valueOf(typeStr);
                } catch (IllegalArgumentException e) {
                    type = ColumnType.STRING;
                }

                tableSchema.addColumn(new ColumnModel(name, type, isPrimaryKey, isNullable));
            }
        }
        return tableSchema;
    }

}
