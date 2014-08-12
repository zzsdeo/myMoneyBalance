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
    public static final String UPDATE_RESULT = "ru.zzsdeo.mymoneybalance.updatedbintentservice.OK";

    public UpdateDBIntentService() {
        super("UpdateDBIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent i = new Intent(UPDATE_RESULT);
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues cv = new ContentValues();
        double balance, am;
        if (intent.getStringExtra("db").equals("scheduler")) {
            i.putExtra("db", "scheduler");
            today.setTimeInMillis(intent.getLongExtra("datetime", today.getTimeInMillis()));
            cv.put("card", intent.getStringExtra("card"));
            cv.put("paymentdetails", intent.getStringExtra("paymentdetails"));
            cv.put("typeoftransaction", intent.getStringExtra("typeoftransaction"));
            cv.put("amount", intent.getDoubleExtra("amount", 0));
            cv.put("label", intent.getStringExtra("label"));
            cv.put("hash", intent.getIntExtra("hash", 0));
            db.beginTransaction();
            try {
                switch (intent.getIntExtra("rbPos", 0)) {
                    case 0:
                        cv.put("datetime", today.getTimeInMillis());
                        db.insert("scheduler", null, cv);
                        break;
                    case 1:
                        do {
                            cv.put("datetime", today.getTimeInMillis());
                            db.insert("scheduler", null, cv);
                            today.add(Calendar.MONTH, 1);
                        } while (END_OF_TIME > today.getTimeInMillis());
                        break;
                    case 2:
                        do {
                            today.set(Calendar.DAY_OF_MONTH, today.getActualMaximum(Calendar.DAY_OF_MONTH));
                            cv.put("datetime", today.getTimeInMillis());
                            db.insert("scheduler", null, cv);
                            today.add(Calendar.MONTH, 1);
                        } while (END_OF_TIME > today.getTimeInMillis());
                        break;
                    case 3:
                        do {
                            cv.put("datetime", today.getTimeInMillis());
                            db.insert("scheduler", null, cv);
                            today.add(Calendar.DAY_OF_MONTH, 1);
                        } while (END_OF_TIME > today.getTimeInMillis());
                        break;
                    case 4:
                        do {
                            if (today.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && today.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                                cv.put("datetime", today.getTimeInMillis());
                                db.insert("scheduler", null, cv);
                            }
                            today.add(Calendar.DAY_OF_WEEK, 1);
                        } while (END_OF_TIME > today.getTimeInMillis());
                        break;
                    case 5:
                        do {
                            cv.put("datetime", today.getTimeInMillis());
                            db.insert("scheduler", null, cv);
                            today.add(Calendar.DAY_OF_MONTH, 7);
                        } while (END_OF_TIME > today.getTimeInMillis());
                        break;
                    case 6:
                        do {
                            cv.put("datetime", today.getTimeInMillis());
                            db.insert("scheduler", null, cv);
                            today.add(Calendar.DAY_OF_MONTH, Integer.parseInt(intent.getStringExtra("custom")));
                        } while (END_OF_TIME > today.getTimeInMillis());
                }
                cv.clear();
                Cursor c = db.query("scheduler", null, "card = " + '"' + intent.getStringExtra("card") + '"', null, null, null, "datetime asc");
                if (c.moveToFirst()) {
                    Cursor b = db.query("mytable", null, "card = " + '"' + intent.getStringExtra("card") + '"', null, null, null, "_id desc");
                    if (b.moveToFirst()) {
                        balance = b.getDouble(b.getColumnIndex("calculatedbalance"));
                    } else {
                        balance = 0;
                    }
                    do {
                        am = c.getDouble(c.getColumnIndex("amount"));
                        balance = balance + am;
                        Log.d("myLogs", Double.toString(balance));
                        cv.put("calculatedbalance", Round.roundedDouble(balance));
                        db.update("scheduler", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                        cv.clear();
                    } while (c.moveToNext());
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        if (intent.getStringExtra("db").equals("mytable")) {
            i.putExtra("db", "mytable");
            Cursor c = db.query("mytable", null, "card = " + '"' + intent.getStringExtra("card") + '"', null, null, null, "datetime asc");
            db.beginTransaction();
            try {
                if (c.moveToFirst()) {
                    balance = 0;
                    do {
                        if (c.getString(c.getColumnIndex("expenceincome")).equals("Rashod")) {
                            am = -c.getDouble(c.getColumnIndex("amount"));
                        } else {
                            am = c.getDouble(c.getColumnIndex("amount"));
                        }
                        balance = balance + am - c.getDouble(c.getColumnIndex("comission"));
                        Log.d("myLogs", Double.toString(balance));
                        cv.put("calculatedbalance", Round.roundedDouble(balance));
                        db.update("mytable", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                        cv.clear();
                    } while (c.moveToNext());
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        if (intent.getStringExtra("db").equals("scheduleronlyrecalculate")) {
            i.putExtra("db", "scheduler");
            Cursor c = db.query("scheduler", null, "card = " + '"' + intent.getStringExtra("card") + '"', null, null, null, "datetime asc");
            db.beginTransaction();
            try {
                if (c.moveToFirst()) {
                    Cursor b = db.query("mytable", null, "card = " + '"' + intent.getStringExtra("card") + '"', null, null, null, "_id desc");
                    if (b.moveToFirst()) {
                        balance = b.getDouble(b.getColumnIndex("calculatedbalance"));
                    } else {
                        balance = 0;
                    }
                    do {
                        am = c.getDouble(c.getColumnIndex("amount"));
                        balance = balance + am;
                        Log.d("myLogs", Double.toString(balance));
                        cv.put("calculatedbalance", Round.roundedDouble(balance));
                        db.update("scheduler", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                        cv.clear();
                    } while (c.moveToNext());
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        if (intent.getStringExtra("db").equals("schedulerdelete")) {
            i.putExtra("db", "scheduler");
            Cursor c = db.query("scheduler", null, "_id = " + '"' + intent.getLongExtra("id", 0) + '"', null, null, null, "datetime asc");
            db.beginTransaction();
            try {
                if (c.moveToFirst()) {
                    switch (intent.getIntExtra("rbDeletePos", 2)) {
                        case 1:
                            int hash = c.getInt(c.getColumnIndex("hash"));
                            db.delete("scheduler", "hash = " + '"' + hash + '"', null);
                            break;
                        case 2:
                            db.delete("scheduler", "_id = " + '"' + intent.getLongExtra("id", 0) + '"', null);
                            break;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        if (intent.getStringExtra("db").equals("recalculateallscheduler")) {
            i.putExtra("db", "scheduler");

        }

        DatabaseManager.getInstance().closeDatabase();
        broadcaster.sendBroadcast(i);
    }

    private void recalculateAllScheduler (String card) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues cv = new ContentValues();
        Cursor c = db.query("scheduler", null, "card = " + '"' + card + '"', null, null, null, "datetime asc");
        db.beginTransaction();
        try {
            if (c.moveToFirst()) {
                Cursor b = db.query("mytable", null, "card = " + '"' + card + '"', null, null, null, "_id desc");
                double balance, am;
                if (b.moveToFirst()) {
                    balance = b.getDouble(b.getColumnIndex("calculatedbalance"));
                } else {
                    balance = 0;
                }
                do {
                    am = c.getDouble(c.getColumnIndex("amount"));
                    balance = balance + am;
                    Log.d("myLogs", Double.toString(balance));
                    cv.put("calculatedbalance", Round.roundedDouble(balance));
                    db.update("scheduler", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                    cv.clear();
                } while (c.moveToNext());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
