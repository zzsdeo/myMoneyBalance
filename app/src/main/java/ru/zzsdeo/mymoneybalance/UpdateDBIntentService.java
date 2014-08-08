package ru.zzsdeo.mymoneybalance;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Calendar;

public class UpdateDBIntentService extends IntentService {

    LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
    private static final Long END_OF_TIME = 1419984000000L;
    private Calendar today = Calendar.getInstance();
    static final public String UPDATE_RESULT = "ru.zzsdeo.mymoneybalance.updatedbintentservice.OK";

    public UpdateDBIntentService() {
        super("UpdateDBIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        today.setTimeInMillis(intent.getLongExtra("datetime", today.getTimeInMillis()));
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues cv = new ContentValues();
        cv.put("card", intent.getStringExtra("card"));
        cv.put("paymentdetails", intent.getStringExtra("paymentdetails"));
        cv.put("typeoftransaction", intent.getStringExtra("typeoftransaction"));
        cv.put("amount", intent.getDoubleExtra("amount", 0));
        cv.put("label", intent.getStringExtra("label"));
        do {
            cv.put("datetime", today.getTimeInMillis());
            db.insert("scheduler", null, cv);
            today.add(Calendar.DAY_OF_MONTH, 1);
        } while (END_OF_TIME > today.getTimeInMillis());
        cv.clear();
        Cursor c = db.query("scheduler", null, "card = " + '"' + intent.getStringExtra("card") + '"', null, null, null, "datetime asc");
        if (c.moveToFirst()) {
            double balance = 0;
            do {
                double am = c.getDouble(c.getColumnIndex("amount"));
                balance = balance + am;
                Log.d("myLogs", Double.toString(balance));
                cv.put("calculatedbalance", Round.roundedDouble(balance));
                db.update("scheduler", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                cv.clear();
            } while (c.moveToNext());
        }
        Intent i = new Intent(UPDATE_RESULT);
        i.putExtra("message", "refresh");
        broadcaster.sendBroadcast(i);

    }
}
