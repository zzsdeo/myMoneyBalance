package ru.zzsdeo.mymoneybalance;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
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
        ActionBar bar = getActionBar();
        assert bar != null;
        bar.setDisplayHomeAsUpEnabled(true);
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
                DialogFragment yesNoExportDialog = new YesNoExportDialog();
                yesNoExportDialog.show(getFragmentManager(), "yesNoExportDialog");
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
                DatabaseManager.getInstance().closeDatabase();
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
    void exportDB() {
        Bundle args = new Bundle();
        args.putString("db", "exportdb");
        Intent i = new Intent(this, UpdateDBIntentService.class);
        startService(i.putExtras(args));
        btnExportDB.setEnabled(false);
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
