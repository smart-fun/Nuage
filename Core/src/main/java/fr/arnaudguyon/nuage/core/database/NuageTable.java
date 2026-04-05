package fr.arnaudguyon.nuage.core.database;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NuageTable {

    private final String tableName;
    private final SQLiteDatabase db;

    private final List<TableTransaction> transactions = new ArrayList<>();

    NuageTable(String tableName, SQLiteDatabase db) {
        this.tableName = tableName;
        this.db = db;
    }

    public void addColumn(@NonNull String columnName, NuageColumn.@NonNull Type type) {
        transactions.add(new TableTransaction.AddColumn(tableName, columnName, type));
    }

    public void apply(@Nullable ApplyListener listener) {
        boolean success = true;
        Exception exception = null;
        db.beginTransaction();
        try {
            for (TableTransaction transaction : transactions) {
                transaction.execute(db);
            }
            db.setTransactionSuccessful();
        } catch(SQLException e) {
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

    public interface ApplyListener {
        void onApplied(boolean success, @Nullable Exception exception);
    }

}
