package fr.arnaudguyon.nuage.core.database;

import androidx.annotation.NonNull;

public class NuageColumn {

    public static final String COLUMN_UUID = "_uuid";
    private final @NonNull String name;
    private final @NonNull Type type;

    public NuageColumn(@NonNull String name, @NonNull Type type) {
        this.name = name;
        this.type = type;
    }

    public enum Type {
        STRING,
        INTEGER,
        DOUBLE,
        BOOLEAN,
        BINARY;

        public @NonNull String toSql() {
            switch(this) {
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
    }

    public @NonNull String getName() {
        return name;
    }

    public @NonNull Type getType() {
        return type;
    }
}
