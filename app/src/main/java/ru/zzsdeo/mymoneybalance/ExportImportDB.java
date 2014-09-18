package ru.zzsdeo.mymoneybalance;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


public class ExportImportDB extends Activity {
    Button btnExportDB, btnImportDB, btnImportSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_import_db);
        getActionBar().setDisplayHomeAsUpEnabled(true);
//creating a new folder for the database to be backuped to
        File direct = new File(Environment.getExternalStorageDirectory() + "/MyMoneyBalance/database");
        if (!direct.exists()) {
            if (direct.mkdirs()) {
                //directory is created;
            }

        }
        btnExportDB = (Button) findViewById(R.id.btnExportDB);
        btnExportDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportDB();
            }
        });

        btnImportDB = (Button) findViewById(R.id.btnImportDB);
        btnImportDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment yesNoDialog = new YesNoDialog();
                yesNoDialog.show(getFragmentManager(), "yesNoDialog");
            }
        });

        btnImportSMS = (Button) findViewById(R.id.btnImportSms);
        btnImportSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment pickSmsDialog = new PickSmsDialog();
                pickSmsDialog.show(getFragmentManager(), "pickSmsDialog");
            }
        });

    }

    //importing database
    void importDB() {

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
                Toast.makeText(getBaseContext(), "База данных успешно импортирована",
                        Toast.LENGTH_LONG).show();
                btnImportDB.setEnabled(false);
            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), "Ошибка!/n" + e.toString(), Toast.LENGTH_LONG)
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
                Toast.makeText(getBaseContext(), "База данных успешно экспортирована",
                        Toast.LENGTH_LONG).show();
                btnExportDB.setEnabled(false);
            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), "Ошибка!/n" + e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}
