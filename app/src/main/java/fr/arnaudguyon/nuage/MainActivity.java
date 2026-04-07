package fr.arnaudguyon.nuage;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import fr.arnaudguyon.nuage.core.database.NuageColumn;
import fr.arnaudguyon.nuage.core.database.NuageDataBase;
import fr.arnaudguyon.nuage.core.database.NuageRecord;
import fr.arnaudguyon.nuage.core.database.NuageTable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_TABLE_NAME = "clients";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nuageTest();
    }

    private void nuageTest() {
        NuageDataBase db = new NuageDataBase(this, "app.db", 1);

        if (!db.tableExists(CLIENT_TABLE_NAME)) {
            Log.i(TAG, "Table " + CLIENT_TABLE_NAME + " does not exist -> create it!");
            NuageTable clients = db.createTable(CLIENT_TABLE_NAME);
            if (clients != null) {
                clients.addColumn("firstName", NuageColumn.Type.STRING);
                clients.addColumn("lastName", NuageColumn.Type.STRING);
                clients.addColumn("isCompany", NuageColumn.Type.BOOLEAN);

                NuageRecord record = new NuageRecord()
                        .put("firstName", "Jean")
                        .put("lastName", "Bon")
                        .put("isCompany", false);

                clients.addRecord(record);

                clients.apply((success, exception) -> {
                    if (exception == null) {
                        Log.i(TAG, "clients apply success: " + success);
                    } else {
                        Log.i(TAG, "clients apply success: " + success + ", error: " + exception.getMessage());
                    }
                });
            } else {
                Log.i(TAG, "failed to create the table");
            }
        } else {
            Log.i(TAG, "Table " + CLIENT_TABLE_NAME + " already exists");
            NuageTable clients = db.getTable(CLIENT_TABLE_NAME);
            if (clients != null) {
                List<NuageRecord> records = clients.request("firstName", "Jean");
                for(NuageRecord record : records) {
                    Log.i(TAG, "record: " + record.toString());
                }
            } else {
                Log.i(TAG, "failed to get the table");
            }
        }

    }
}
