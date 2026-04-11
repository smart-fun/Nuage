package fr.arnaudguyon.nuage.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileSyncProvider implements SyncProvider.Internal {

    private final File baseFolder;

    public static SyncProvider create(@NonNull Context context, @NonNull String appName, @NonNull String dbName) {
        return new FileSyncProvider(context, appName, dbName);
    }

    private FileSyncProvider(@NonNull Context context, @NonNull String appName, @NonNull String dbName) {
        // Root folder : /Android/data/package/files/appName/dbName/
        File syncRoot = context.getExternalFilesDir(appName);
        this.baseFolder = new File(syncRoot, dbName);
        if (!baseFolder.exists()) {
            baseFolder.mkdirs();
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void upload(@NonNull String relativePath, @NonNull byte[] data) throws IOException {
        File targetFile = new File(baseFolder, relativePath);
        File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(data);
        }
    }

    @Override
    public @Nullable byte[] download(@NonNull String relativePath) throws IOException {
        return new byte[0];
    }
}
