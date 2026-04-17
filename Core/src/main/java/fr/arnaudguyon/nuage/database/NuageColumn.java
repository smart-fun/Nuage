package fr.arnaudguyon.nuage.database;

import androidx.annotation.NonNull;

import fr.arnaudguyon.nuage.model.ColumnModel;
import fr.arnaudguyon.nuage.model.ColumnType;

public class NuageColumn {

    public static final String COLUMN_UUID = "_uuid";
    private final @NonNull ColumnModel model;

    public NuageColumn(@NonNull ColumnModel model) {
        this.model = model;
    }

    public NuageColumn(@NonNull String name, @NonNull ColumnType type) {
        this(new ColumnModel(name, type));
    }

    public @NonNull String getSqlType() {
        ColumnType type = model.getType();
        switch (type) {
            case STRING:
                return "TEXT";
            case INTEGER:
                return "INTEGER";
            case DOUBLE:
                return "REAL";
            case BOOLEAN:
                return "BOOLEAN"; // Type Affinity fallbacks to "INTEGER" but we store "BOOLEAN"
            case BINARY:
                return "BLOB";
            default:
                return "ERROR";
        }
    }

    public @NonNull String getName() {
        return model.getName();
    }

    public @NonNull ColumnType getType() {
        return model.getType();
    }
}
