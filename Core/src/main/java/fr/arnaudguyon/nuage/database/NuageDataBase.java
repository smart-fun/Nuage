package fr.arnaudguyon.nuage.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import fr.arnaudguyon.nuage.model.DatabaseSchema;

public class NuageDataBase {

    private final @NonNull NuageSQLiteHelper helper;
    private final @NonNull Map<String, NuageTable> tables = new HashMap<>();

    public NuageDataBase(@NonNull Context context, @NonNull String baseName, int version) {
        helper = new NuageSQLiteHelper(context, baseName, version);
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

    public DatabaseSchema getSchema() {
        DatabaseSchema dbSchema = new DatabaseSchema();
        for (NuageTable table : tables.values()) {
            dbSchema.addTable(table.getTableSchema());
        }
        return dbSchema;
    }

}
