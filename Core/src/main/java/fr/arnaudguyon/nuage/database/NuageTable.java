package fr.arnaudguyon.nuage.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NuageTable {

    private final @NonNull String tableName;
    private final @NonNull SQLiteDatabase db;
    private final Map<String, NuageColumn.Type> columnTypes = new LinkedHashMap<>();
    private final List<TableTransaction> transactions = new ArrayList<>();

    NuageTable(@NonNull String tableName, @NonNull SQLiteDatabase db) {
        this.tableName = tableName;
        this.db = db;
        initColumnTypes();
    }

    @NonNull
    public String getTableName() {
        return tableName;
    }

    static boolean exists(@NonNull String tableName, @NonNull SQLiteDatabase db) {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (Cursor cursor = db.rawQuery(query, new String[]{tableName})) {
            return (cursor.getCount() > 0);
        }
    }

    static @Nullable NuageTable get(@NonNull String tableName, @NonNull SQLiteDatabase db) {
        if (!exists(tableName, db)) {
            return null;
        }
        return new NuageTable(tableName, db);
    }

    static @Nullable NuageTable create(@NonNull String tableName, @NonNull SQLiteDatabase db) {
        if (exists(tableName, db)) {
            return null;
        }
        String sql = "CREATE TABLE " + tableName + " (" + NuageColumn.COLUMN_UUID + " TEXT PRIMARY KEY);";
        db.execSQL(sql);
        return new NuageTable(tableName, db);
    }

    public void addColumn(@NonNull String columnName, @NonNull NuageColumn.Type type) {
        transactions.add(new TableTransaction.AddColumn(tableName, columnName, type));
    }

    public void addRecord(@NonNull NuageRecord record) {
        transactions.add(new TableTransaction.InsertRecord(tableName, record));
    }

    public @Nullable NuageRecord getRecord(@NonNull String uuid) {
        String query = "SELECT * FROM " + tableName + " WHERE " + NuageColumn.COLUMN_UUID + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{uuid})) {
            if (cursor.moveToFirst()) {
                return new NuageRecord(cursor, columnTypes);
            }
        }
        return null;
    }

    public @NonNull List<NuageRecord> request(@NonNull String columnName, @Nullable String value) {
        List<NuageRecord> results = new ArrayList<>();

        String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";
        String[] args = new String[]{(value != null) ? value : ""};

        try (Cursor cursor = db.rawQuery(query, args)) {
            if (cursor.moveToFirst()) {
                do {
                    results.add(new NuageRecord(cursor, columnTypes));
                } while (cursor.moveToNext());
            }
        }
        return results;
    }

    public void apply(@Nullable ApplyListener listener) {
        boolean success = true;
        Exception exception = null;
        db.beginTransaction();
        try {
            for (TableTransaction transaction : transactions) {
                transaction.execute(db);
                if (transaction instanceof TableTransaction.AddColumn transactionAddColumn) {
                    columnTypes.put(transactionAddColumn.getColumn().getName(), transactionAddColumn.getColumn().getType());    // put type in cache
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            success = false;
            exception = e;
        } finally {
            db.endTransaction();
            transactions.clear();
            if (listener != null) {
                listener.onApplied(success, exception);
            }
        }
    }

    private void initColumnTypes() {
        columnTypes.clear();
        try (Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null)) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndexOrThrow("name");
                int typeIndex = cursor.getColumnIndexOrThrow("type");
                do {
                    String name = cursor.getString(nameIndex);
                    String typeStr = cursor.getString(typeIndex);

                    // Map the SQL declared type back to our Enum
                    NuageColumn.Type type;
                    try {
                        type = NuageColumn.Type.valueOf(typeStr);
                    } catch (IllegalArgumentException e) {
                        type = NuageColumn.Type.STRING; // Defaulting to STRING
                    }
                    columnTypes.put(name, type);
                } while (cursor.moveToNext());
            }
        }
    }

    public Map<String, NuageColumn.Type> getColumnTypes() {
        return Collections.unmodifiableMap(columnTypes);
    }

    public interface ApplyListener {
        void onApplied(boolean success, @Nullable Exception exception);
    }

}
