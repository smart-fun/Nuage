package fr.arnaudguyon.nuage.sync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public interface SyncProvider {

    interface Internal extends SyncProvider {
        void start();
        void stop();
        @Nullable
        byte[] download(@NonNull String relativePath) throws IOException;
        void upload(@NonNull String relativePath, @NonNull byte[] data) throws IOException;
    }
}
