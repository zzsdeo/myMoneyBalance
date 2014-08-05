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
        if (c.moveToFirst()) {
            do {
                Log.d("myLogs", c.getString(c.getColumnIndex("paymentdetails")));
                cv.put("label", "NotConfirmed");
                db.update("scheduler", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                cv.clear();
            } while (c.moveToNext());
        }
        DatabaseManager.getInstance().closeDatabase();
        return super.onStartCommand(intent, flags, startId);
    }
}
