package fr.arnaudguyon.nuage.core.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NuageDataBase {

    private final @NonNull NuageSQLiteHelper helper;

    public NuageDataBase(Context context, String baseName, int version) {
        helper = new NuageSQLiteHelper(context, baseName, version);
    }

    public boolean tableExists(@NonNull String tableName) {
        return NuageTable.exists(tableName, helper.getReadableDatabase());
    }

    public @Nullable NuageTable getTable(@NonNull String tableName) {
        return NuageTable.get(tableName, helper.getWritableDatabase());
    }

    public @Nullable NuageTable createTable(@NonNull String tableName) {
        if (!tableExists(tableName)) {
            return NuageTable.create(tableName, helper.getWritableDatabase());
        } else {
            return null;
        }
    }

}
