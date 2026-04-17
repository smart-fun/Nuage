package fr.arnaudguyon.nuage.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TableSchema {

    private final @NonNull String tableName;

    private final Map<String, ColumnModel> columns = new LinkedHashMap<>();

    public TableSchema(@NonNull String tableName) {
        this.tableName = tableName;
    }

    public @NonNull String getTableName() {
        return tableName;
    }

    public void addColumn(@NonNull ColumnModel model) {
        columns.put(model.getName(), model);
    }

    public @NonNull Collection<ColumnModel> getColumns() {
        return Collections.unmodifiableList(new ArrayList<>(columns.values()));
    }

    public @NonNull Map<String, ColumnModel> getColumnMap() {
        return Collections.unmodifiableMap(columns);
    }

}
