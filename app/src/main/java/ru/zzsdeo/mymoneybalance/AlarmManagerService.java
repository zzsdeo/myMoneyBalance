package ru.zzsdeo.mymoneybalance;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;

public class AlarmManagerService extends Service {
    public AlarmManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DatabaseManager.initializeInstance(new DBHelper(this));
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues cv = new ContentValues();
        Cursor c = db.query("scheduler", null, "datetime <  " + '"' + System.currentTimeMillis() + '"', null, null, null, null);
        cv.put("label", "NotConfirmed");
        db.beginTransaction();
        try {
            if (c.moveToFirst()) {
                do {
                    String label = c.getString(c.getColumnIndex("label"));
                    if (label != null) {
                        if (label.equals("NeedConfirmation")) {
                            db.update("scheduler", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                        }
                    }
                } while (c.moveToNext());
            }
            c = db.query("scheduler", null, "datetime <  " + '"' + System.currentTimeMillis() + '"' + "and label is not 'NotConfirmed'", null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    db.delete("scheduler", "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                } while (c.moveToNext());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        DatabaseManager.getInstance().closeDatabase();
        //обновление баланса запланированных транзакций
        Bundle args = new Bundle();
        args.putString("db", "recalculateallscheduler");
        Intent i = new Intent(AlarmManagerService.this, UpdateDBIntentService.class);
        i.putExtras(args);
        startService(i);
        return START_NOT_STICKY;
    }
}
