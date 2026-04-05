package fr.arnaudguyon.nuage.core.database;

import android.content.Context;

    import org.jspecify.annotations.NonNull;

public class NuageDataBase {

    private final @NonNull NuageSQLiteHelper helper;

    public NuageDataBase(Context context, String baseName, int version) {
        helper = new NuageSQLiteHelper(context, baseName, version);
    }

    public NuageTable table(@NonNull String tableName) {
        return new NuageTable(tableName, helper.getWritableDatabase());
    }

}
