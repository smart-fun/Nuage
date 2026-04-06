package fr.arnaudguyon.nuage.core.database;

import static fr.arnaudguyon.nuage.core.database.NuageColumn.COLUMN_UUID;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a single row in a NuageTable.
 * Manages data mapping between the application and SQLite/JSON storage.
 */
public class NuageRecord {

    private final @NonNull String uuid;
    private final @NonNull Map<String, Object> values = new HashMap<>();

    public NuageRecord() {
        this.uuid = UUID.randomUUID().toString();
    }

    public NuageRecord(@NonNull Cursor cursor) {
        int uuidIndex = cursor.getColumnIndex(COLUMN_UUID);
        if (uuidIndex != -1) {
            this.uuid = cursor.getString(uuidIndex);
        } else {
            this.uuid = UUID.randomUUID().toString();
        }

        // TODO: handle types according to schema (ie JSON BOOLEAN vs SQL INTEGER)
        String[] columnNames = cursor.getColumnNames();
        for (String name : columnNames) {
            if (name.equals(COLUMN_UUID)) continue;

            int index = cursor.getColumnIndex(name);
            switch (cursor.getType(index)) {
                case Cursor.FIELD_TYPE_STRING:
                    put(name, cursor.getString(index));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    put(name, cursor.getLong(index));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    put(name, cursor.getDouble(index));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    values.put(name, null);
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    // TODO: not handled yet
                    break;
            }
        }
    }

    public @NonNull NuageRecord put(@NonNull String columnName, @Nullable String value) {
        values.put(columnName, value);
        return this;
    }

    public @NonNull NuageRecord put(@NonNull String columnName, long value) {
        values.put(columnName, value);
        return this;
    }

    public @NonNull NuageRecord put(@NonNull String columnName, double value) {
        values.put(columnName, value);
        return this;
    }

    public @NonNull NuageRecord put(@NonNull String columnName, boolean value) {
        values.put(columnName, value);
        return this;
    }

    @Nullable
    public Object get(@NonNull String columnName) {
        return values.get(columnName);
    }

    /**
     * Converts the record data into Android's ContentValues for SQLite insertion.
     */
    @NonNull
    ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_UUID, uuid);
        Set<String> keys = values.keySet();
        for (String key : keys) {
            Object value = values.get(key);
            if (value == null) {
                contentValues.putNull(key);
            } else if (value instanceof String) {
                contentValues.put(key, (String) value);
            } else if (value instanceof Long) {
                contentValues.put(key, (Long) value);
            } else if (value instanceof Double) {
                contentValues.put(key, (Double) value);
            } else if (value instanceof Boolean) {
                contentValues.put(key, (Boolean) value);
            }
        }
        return contentValues;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("NuageRecord{");
        sb.append(COLUMN_UUID).append("='").append(uuid).append("'");
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            sb.append(", ").append(entry.getKey()).append("=");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("'").append(value).append("'");
            } else {
                sb.append(value);
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
