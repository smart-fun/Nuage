package fr.arnaudguyon.nuage.model;

import androidx.annotation.NonNull;

public class ColumnModel {
    private final @NonNull String name;
    private final @NonNull ColumnType type;

    public ColumnModel(@NonNull String name, @NonNull ColumnType type) {
        this.name = name;
        this.type = type;
    }

    public @NonNull String getName() {
        return name;
    }

    public @NonNull ColumnType getType() {
        return type;
    }

}
