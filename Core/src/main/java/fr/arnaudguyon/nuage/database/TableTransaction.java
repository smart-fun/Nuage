package fr.arnaudguyon.nuage.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import fr.arnaudguyon.nuage.model.ColumnType;

abstract class TableTransaction {

    protected final @NonNull String tableName;

    TableTransaction(@NonNull String tableName) {
        this.tableName = tableName;
    }
    public abstract void execute(@NonNull SQLiteDatabase db);

    static class AddColumn extends TableTransaction {

        private final @NonNull NuageColumn column;

        AddColumn(@NonNull String tableName, @NonNull String columnName, @NonNull ColumnType columnType) {
            super(tableName);
            this.column = new NuageColumn(columnName, columnType);
        }

        @Override
        public void execute(@NonNull SQLiteDatabase db) {
            String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + column.getName() + " " + column.getSqlType() + ";";
            db.execSQL(sql);
        }

        @NonNull
        public NuageColumn getColumn() {
            return column;
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
