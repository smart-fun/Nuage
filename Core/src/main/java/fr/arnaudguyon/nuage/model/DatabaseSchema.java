package fr.arnaudguyon.nuage.model;

import androidx.annotation.NonNull;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseSchema {

    private final Map<String, TableSchema> tables = new LinkedHashMap<>();
    private long lastUpdateTimestamp;

    public DatabaseSchema() {
        this.lastUpdateTimestamp = System.currentTimeMillis();
    }

    public void addTable(@NonNull TableSchema tableSchema) {
        tables.put(tableSchema.getTableName(), tableSchema);
    }

    public void setLastUpdateTimestamp(long timestamp) {
        this.lastUpdateTimestamp = timestamp;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    @NonNull
    public Collection<TableSchema> getTables() {
        return Collections.unmodifiableCollection(tables.values());
    }
}

