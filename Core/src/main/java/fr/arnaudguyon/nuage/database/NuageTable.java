package fr.arnaudguyon.nuage.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import fr.arnaudguyon.nuage.json.SQLiteConst;
import fr.arnaudguyon.nuage.model.ColumnModel;
import fr.arnaudguyon.nuage.model.ColumnType;
import fr.arnaudguyon.nuage.model.TableSchema;

public class NuageTable {

    private final @NonNull SQLiteDatabase db;
    private final @NonNull TableSchema tableSchema;
    private final List<TableTransaction> transactions = new ArrayList<>();

    NuageTable(@NonNull String tableName, @NonNull SQLiteDatabase db) {
        this.tableSchema = new TableSchema(tableName);
        this.db = db;
        initColumnTypes();
    }

    NuageTable(@NonNull TableSchema schema, @NonNull SQLiteDatabase db) {
        this.tableSchema = schema;
        this.db = db;
    }

    @NonNull
    public String getTableName() {
        return tableSchema.getTableName();
    }

    @NonNull
    public TableSchema getTableSchema() {
        return tableSchema;
    }

    static boolean exists(@NonNull String tableName, @NonNull SQLiteDatabase db) {
        String query = "SELECT name FROM " + SQLiteConst.SCHEMA_TABLE_LIST + " WHERE type='table' AND name=?";
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
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + NuageColumn.COLUMN_UUID + " TEXT PRIMARY KEY);";
        db.execSQL(sql);
        return new NuageTable(tableName, db);
    }

    public static NuageTable createFromSchema(@NonNull TableSchema schema, @NonNull SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(schema.getTableName()).append(" (");

        boolean first = true;
        for (ColumnModel colModel : schema.getColumns()) {
            if (!first) sb.append(", ");

            NuageColumn nuageCol = new NuageColumn(colModel);
            sb.append(nuageCol.getName()).append(" ").append(nuageCol.getSqlType());
            if (colModel.isPrimaryKey()) {
                sb.append(" PRIMARY KEY");
            } else if (!colModel.isNullable()) {
                sb.append(" NOT NULL");
            }
            first = false;
        }
        sb.append(");");

        db.execSQL(sb.toString());

        return new NuageTable(schema, db);
    }

    public void addColumn(@NonNull String columnName, @NonNull ColumnType type, boolean isNullable) {
        transactions.add(new TableTransaction.AddColumn(getTableName(), columnName, type, isNullable));
    }

    public void addRecord(@NonNull NuageRecord record) {
        transactions.add(new TableTransaction.InsertRecord(getTableName(), record));
    }

    public @Nullable NuageRecord getRecord(@NonNull String uuid) {
        String query = "SELECT * FROM " + getTableName() + " WHERE " + NuageColumn.COLUMN_UUID + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{uuid})) {
            if (cursor.moveToFirst()) {
                return new NuageRecord(cursor, tableSchema.getColumnMap());
            }
        }
        return null;
    }

    public @NonNull List<NuageRecord> request(@NonNull String columnName, @Nullable String value) {
        List<NuageRecord> results = new ArrayList<>();

        String query = "SELECT * FROM " + getTableName() + " WHERE " + columnName + " = ?";
        String[] args = new String[]{(value != null) ? value : ""};

        try (Cursor cursor = db.rawQuery(query, args)) {
            if (cursor.moveToFirst()) {
                do {
                    results.add(new NuageRecord(cursor, tableSchema.getColumnMap()));
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
                    NuageColumn column = transactionAddColumn.getColumn();
                    tableSchema.addColumn(new ColumnModel(column.getName(), column.getType(), column.isPrimaryKey(), column.isNullable()));
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
        try (Cursor cursor = db.rawQuery("PRAGMA table_info(" + getTableName() + ")", null)) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndexOrThrow(SQLiteConst.INSPECT_COLUMN_NAME);
                int typeIndex = cursor.getColumnIndexOrThrow(SQLiteConst.INSPECT_COLUMN_SQL_TYPE);
                int pkIndex = cursor.getColumnIndexOrThrow(SQLiteConst.INSPECT_COLUMN_IS_PRIMARY_KEY);
                int notNullIndex = cursor.getColumnIndexOrThrow(SQLiteConst.INSPECT_COLUMN_IS_NOT_NULL);
                do {
                    String name = cursor.getString(nameIndex);
                    String typeStr = cursor.getString(typeIndex);
                    ColumnType type = ColumnType.fromSqlType(typeStr);
                    boolean isPrimaryKey = cursor.getInt(pkIndex) == 1;
                    boolean isNullable = cursor.getInt(notNullIndex) == 0; // notnull=1 equals nullable=false
                    tableSchema.addColumn(new ColumnModel(name, type, isPrimaryKey, isNullable));
                } while (cursor.moveToNext());
            }
        }
    }

    public interface ApplyListener {
        void onApplied(boolean success, @Nullable Exception exception);
    }

}
