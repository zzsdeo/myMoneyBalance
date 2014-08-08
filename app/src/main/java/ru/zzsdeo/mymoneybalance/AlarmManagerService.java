package ru.zzsdeo.mymoneybalance;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class AlarmManagerService extends Service {
    public AlarmManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("myLogs", "Service running");
        DatabaseManager.initializeInstance(new DBHelper(this));
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues cv = new ContentValues();
        Cursor c = db.query("scheduler", null, "datetime <  " + '"' + System.currentTimeMillis() + '"', null, null, null, null);
        cv.put("label", "NotConfirmed");
        if (c.moveToFirst()) {
            do {
                String label = c.getString(c.getColumnIndex("label"));
                if (label != null) {
                    if (label.equals("NeedConfirmation")) {
                        Log.d("myLogs", "Подтверждение " + c.getString(c.getColumnIndex("paymentdetails")));
                        db.update("scheduler", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                    }
                }
            } while (c.moveToNext());
        }
        c = db.query("scheduler", null, "datetime <  " + '"' + System.currentTimeMillis() + '"' + "and label is not 'NotConfirmed'", null, null, null, null);
        if (c.moveToFirst()) {
            do {
                Log.d("myLogs", "Удаление " + c.getString(c.getColumnIndex("paymentdetails")));
                db.delete("scheduler", "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
            } while (c.moveToNext());
        }
        DatabaseManager.getInstance().closeDatabase();
        return super.onStartCommand(intent, flags, startId);
    }
}
