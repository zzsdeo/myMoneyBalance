package ru.zzsdeo.mymoneybalance;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


public class ExportImportDB extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_import_db);
//creating a new folder for the database to be backuped to
        File direct = new File(Environment.getExternalStorageDirectory() + "/MyMoneyBalance/database");
        if (!direct.exists()) {
            if (direct.mkdirs()) {
                //directory is created;
            }

        }
        Button btnExportDB = (Button) findViewById(R.id.btnExportDB);
        btnExportDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportDB();
            }
        });

        Button btnImportDB = (Button) findViewById(R.id.btnImportDB);
        btnImportDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                importDB();
            }
        });

    }

    //importing database
    private void importDB() {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "ru.zzsdeo.mymoneybalance"
                        + "//databases//" + "myDB";
                String backupDBPath = "/MyMoneyBalance/database/myDB";
                File backupDB = new File(data, currentDBPath);
                File currentDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

    //exporting database
    private void exportDB() {

        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "ru.zzsdeo.mymoneybalance"
                        + "//databases//" + "myDB";
                String backupDBPath = "/MyMoneyBalance/database/myDB";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

}
