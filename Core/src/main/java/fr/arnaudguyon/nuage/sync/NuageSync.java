package fr.arnaudguyon.nuage.sync;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.Collection;

import fr.arnaudguyon.nuage.database.NuageDataBase;
import fr.arnaudguyon.nuage.database.NuageTable;
import fr.arnaudguyon.nuage.json.DatabaseSerializer;
import fr.arnaudguyon.nuage.json.JsonUtils;
import fr.arnaudguyon.nuage.json.TableSerializer;
import fr.arnaudguyon.nuage.model.DatabaseSchema;

public class NuageSync {

    private static final String TAG = NuageSync.class.getSimpleName();

    private static volatile @Nullable NuageSync instance;
    private final @NonNull NuageDataBase dataBase;
    private final @NonNull SyncProvider.Internal syncProvider;
    private final @NonNull SyncStrategy syncStrategy;

    public static synchronized @NonNull NuageSync create(@NonNull NuageDataBase dataBase,
                                                         @NonNull SyncProvider syncProvider,
                                                         @NonNull SyncStrategy syncStrategy) {
        if (instance != null) {
            throw new IllegalStateException("NuageSync instance already exists. Use getInstance() instead.");
        }
        if (syncProvider instanceof SyncProvider.Internal internalSyncProvider) {
            instance = new NuageSync(dataBase, internalSyncProvider, syncStrategy);
        } else {
            throw new IllegalArgumentException("syncProvider must implement SyncProvider.Internal");
        }
        return instance;
    }

    public static @NonNull NuageSync getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NuageSync must be created via create() before use.");
        }
        return instance;
    }

    private NuageSync(@NonNull NuageDataBase dataBase, @NonNull SyncProvider.Internal internalSyncProvider, @NonNull SyncStrategy syncStrategy) {
        this.dataBase = dataBase;
        this.syncProvider = internalSyncProvider;
        this.syncStrategy = syncStrategy;
    }

    public void start() {
        syncProvider.start();
    }

    public void stop() {
        syncProvider.stop();
    }

    public void syncNow() {
        // get json files, database records, create new json files
        // request syncProvider to save the new files
        Collection<NuageTable> tables = dataBase.getTables();
        for(NuageTable table : tables) {
            try {
                JSONObject json = TableSerializer.serializeDefinition(table.getTableSchema());
                byte[] bytes = JsonUtils.toBytes(json);
                syncProvider.upload(table.getTableName(), bytes);
            } catch (Exception e) {
                Log.e(TAG, "Error while saving " + table.getTableName() + " -> " + e.getMessage());
            }
        }

        DatabaseSchema dbSchema = dataBase.getSchema();
        try {
            JSONObject json = DatabaseSerializer.serialize(dbSchema);
            byte[] bytes = JsonUtils.toBytes(json);
            syncProvider.upload("nuage_schema.json", bytes);    // TODO: do not hardcode the file name here
        } catch (Exception e) {
            Log.e(TAG, "Error while saving database schema " + e.getMessage());
        }
    }

}
