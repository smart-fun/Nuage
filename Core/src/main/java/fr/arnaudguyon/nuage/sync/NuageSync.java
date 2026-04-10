package fr.arnaudguyon.nuage.sync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.arnaudguyon.nuage.database.NuageDataBase;

public class NuageSync {

    private static @Nullable NuageSync instance;
    private final @NonNull NuageDataBase dataBase;
    private final @NonNull SyncProvider syncProvider;
    private final @NonNull SyncStrategy syncStrategy;

    public static synchronized @NonNull NuageSync create(@NonNull NuageDataBase dataBase,
                                                         @NonNull SyncProvider syncProvider,
                                                         @NonNull SyncStrategy syncStrategy) {
        if (instance != null) {
            throw new IllegalStateException("NuageSync instance already exists. Use getInstance() instead.");
        }
        instance = new NuageSync(dataBase, syncProvider, syncStrategy);
        return instance;
    }

    public static @NonNull NuageSync getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NuageSync must be created via create() before use.");
        }
        return instance;
    }

    private NuageSync(@NonNull NuageDataBase dataBase, @NonNull SyncProvider syncProvider, @NonNull SyncStrategy syncStrategy) {
        this.dataBase = dataBase;
        this.syncProvider = syncProvider;
        this.syncStrategy = syncStrategy;
    }

    void start() {
        syncProvider.start();
    }

    void stop() {
        syncProvider.stop();
    }

    void syncNow() {
        // get json files, database records, create new json files
        // request syncProvider to save the new files
    }

}
