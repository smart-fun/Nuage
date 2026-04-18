package fr.arnaudguyon.nuage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.arnaudguyon.nuage.json.SQLiteConst;
import fr.arnaudguyon.nuage.model.DatabaseSchema;
import fr.arnaudguyon.nuage.model.TableSchema;

public class NuageDataBase {

    private final @NonNull NuageSQLiteHelper helper;
    private final @NonNull Map<String, NuageTable> tables = new HashMap<>();

    public NuageDataBase(@NonNull Context context, @NonNull String baseName, int version) {
        helper = new NuageSQLiteHelper(context, baseName, version);
        loadExistingTables();
    }

    public @NonNull String getDatabaseName() {
        return helper.getDatabaseName();
    }

    public boolean tableExists(@NonNull String tableName) {
        return NuageTable.exists(tableName, helper.getReadableDatabase());
    }

    @NonNull
    public Collection<NuageTable> getTables() {
        return Collections.unmodifiableCollection(tables.values());
    }

    public @Nullable NuageTable getTable(@NonNull String tableName) {
        if (tables.containsKey(tableName)) {
            return tables.get(tableName);
        }
        NuageTable table = NuageTable.get(tableName, helper.getWritableDatabase());
        if (table != null) {
            tables.put(tableName, table);
        }
        return table;
    }

    public @Nullable NuageTable createTable(@NonNull String tableName) {
        if (!tableExists(tableName)) {
            return NuageTable.create(tableName, helper.getWritableDatabase());
        } else {
            return null;
        }
    }

    private void loadExistingTables() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String query = "SELECT "+ SQLiteConst.SCHEMA_COLUMN_NAME +
                " FROM " + SQLiteConst.SCHEMA_TABLE_LIST +
                " WHERE " + SQLiteConst.SCHEMA_COLUMN_TYPE + "='table' " +
                "AND " + SQLiteConst.SCHEMA_COLUMN_NAME + " NOT LIKE 'android_%' " +
                "AND " + SQLiteConst.SCHEMA_COLUMN_NAME + " NOT LIKE 'sqlite_%'";

        try (android.database.Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndexOrThrow(SQLiteConst.SCHEMA_COLUMN_NAME);
                do {
                    String tableName = cursor.getString(nameIndex);
                    getTable(tableName);
                } while (cursor.moveToNext());
            }
        }
    }

    public void importSchema(@NonNull DatabaseSchema dbSchema) {
        SQLiteDatabase db = helper.getWritableDatabase();
        for (TableSchema tableSchema : dbSchema.getTables()) {
            String tableName = tableSchema.getTableName();
            if (!tables.containsKey(tableName) && !tableExists(tableName)) {
                NuageTable table = NuageTable.createFromSchema(tableSchema, db);
                tables.put(tableSchema.getTableName(), table);
            }
        }
    }

    public DatabaseSchema getSchema() {
        DatabaseSchema dbSchema = new DatabaseSchema();
        for (NuageTable table : tables.values()) {
            dbSchema.addTable(table.getTableSchema());
        }
        return dbSchema;
    }

}
