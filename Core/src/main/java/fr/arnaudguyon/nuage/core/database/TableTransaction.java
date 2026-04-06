package fr.arnaudguyon.nuage.core.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

abstract class TableTransaction {

    protected final @NonNull String tableName;

    TableTransaction(@NonNull String tableName) {
        this.tableName = tableName;
    }
    public abstract void execute(@NonNull SQLiteDatabase db);

    static class AddColumn extends TableTransaction {

        private final @NonNull NuageColumn column;

        AddColumn(@NonNull String tableName, @NonNull String columnName, @NonNull NuageColumn.Type columnType) {
            super(tableName);
            this.column = new NuageColumn(columnName, columnType);
        }

        @Override
        public void execute(@NonNull SQLiteDatabase db) {
            String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + column.getName() + " " + column.getType().toSql() + ";";
            db.execSQL(sql);
        }

    }

    static class InsertRecord extends TableTransaction {
        private final @NonNull NuageRecord record;

        InsertRecord(@NonNull String tableName, @NonNull NuageRecord record) {
            super(tableName);
            this.record = record;
        }

        @Override
        public void execute(@NonNull SQLiteDatabase db) {
            ContentValues values = record.toContentValues();
            db.insert(tableName, null, values);
        }
    }

}
