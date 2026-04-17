package fr.arnaudguyon.nuage.model;

import androidx.annotation.NonNull;

public enum ColumnType {
    STRING,
    INTEGER,
    DOUBLE,
    BOOLEAN,
    BINARY;

    @NonNull
    public static ColumnType fromSqlType(@NonNull String sqlType) {
        switch (sqlType.toUpperCase()) {
            case "TEXT":
            case "STRING":
                return STRING;
            case "INTEGER":
            case "INT":
                return INTEGER;
            case "REAL":
            case "DOUBLE":
            case "FLOAT":
                return DOUBLE;
            case "BOOLEAN":
            case "BOOL":
                return BOOLEAN;
            case "BLOB":
            case "BINARY":
                return BINARY;
            default:
                return STRING;
        }
    }
}
