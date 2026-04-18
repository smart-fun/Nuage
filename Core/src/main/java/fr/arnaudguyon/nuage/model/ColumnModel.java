package fr.arnaudguyon.nuage.model;

import androidx.annotation.NonNull;

public class ColumnModel {
    private final @NonNull String name;
    private final @NonNull ColumnType type;
    private final boolean isPrimaryKey;
    private final boolean isNullable;

    public ColumnModel(@NonNull String name, @NonNull ColumnType type, boolean isPrimaryKey, boolean isNullable) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
        this.isNullable = isNullable;
    }

    public @NonNull String getName() {
        return name;
    }

    public @NonNull ColumnType getType() {
        return type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isNullable() {
        return isNullable;
    }

}
