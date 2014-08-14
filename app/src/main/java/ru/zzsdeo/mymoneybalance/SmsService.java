package ru.zzsdeo.mymoneybalance;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class SmsService extends Service {
    public SmsService() {
    }

    private String card;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String sms = intent.getExtras().getString("SMS");
        Long dateInMill = intent.getExtras().getLong("DATE_IN_MILL");
        if (sms.startsWith("Telecard") || sms.contains("TELECARD")) {
            DatabaseManager.initializeInstance(new DBHelper(this));
            SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
            ContentValues cv = new ContentValues();
            double amount;
            if (sms.contains("TELECARD")) {
                card = "Card2485";
                cv.put("card", card);
                cv.put("paymentdetails", "TELECARD");
                cv.put("typeoftransaction", "Oplata");
                cv.put("expenceincome", "Rashod");
                cv.put("amount", 50);
            } else {
                String[] parsedSms = sms.split(";");
                for (String str : parsedSms) {
                    str = str.trim();
                    if (str.matches("Card\\d\\d\\d\\d")) {
                        cv.put("card", str);
                        card = str;
                    }
                    if (str.matches("[a-zA-Z0-9\\.\\,\\-\\_\\s]+") & !str.endsWith("RUR") & !str.matches("Oplata|Cash\\-in|Snyatie\\snalichnih|Zachislenie|Oplata\\sv\\sI\\-net|Predauth|Perevod|Poluchen\\sperevod|Card\\d\\d\\d\\d|\\d\\d\\.\\d\\d\\.\\d\\d\\s\\d\\d\\:\\d\\d\\:\\d\\d|Telecard")) {
                        cv.put("paymentdetails", str);
                    }
                    if (str.matches("Oplata|Cash\\-in|Snyatie\\snalichnih|Zachislenie|Oplata\\sv\\sI\\-net|Predauth|Perevod|Poluchen\\sperevod")) {
                        cv.put("typeoftransaction", str);
                        if (str.matches("Oplata|Snyatie\\snalichnih|Oplata\\sv\\sI\\-net|Predauth|Perevod")) {
                            cv.put("expenceincome", "Rashod");
                        }
                        if (str.matches("Cash\\-in|Zachislenie|Poluchen\\sperevod")) {
                            cv.put("expenceincome", "Dohod");
                        }
                    }
                    if (str.matches("([0-9\\.]+(\\sRUR))|(Summa\\s[0-9\\.]+(\\sRUR))")) {
                        amount = Double.parseDouble(str.replaceAll("\\sRUR", "").replaceAll("Summa\\s", "").trim());
                        cv.put("amount", amount);
                    }
                    if (str.matches("dostupno:\\s[0-9\\.]+(\\sRUR)")) {
                        cv.put("balance", Double.parseDouble(str.replaceAll("dostupno:\\s|\\sRUR", "").trim()));
                    }
                    if (str.matches("zadoljennost:\\s[0-9\\.]+(\\sRUR)")) {
                        cv.put("indebtedness", Double.parseDouble(str.replaceAll("zadoljennost:\\s|\\sRUR", "").trim()));
                    }
                    if (str.matches("komissiya:\\s[0-9\\.]+(\\sRUR)")) {
                        cv.put("comission", Double.parseDouble(str.replaceAll("Komissiya:\\s|\\sRUR", "").trim()));
                    }

                }
            }
            cv.put("label", "Auto");
            cv.put("datetime", dateInMill);
            db.beginTransaction();
            try {
                db.insert("mytable", null, cv);
                //обновляем баланс
                cv.clear();
                Cursor c = db.query("mytable", null, "card = " + '"' + card + '"', null, null, null, "datetime asc");
                if (c.moveToFirst()) {
                    double balance = 0;
                    do {
                        if (c.getString(c.getColumnIndex("expenceincome")).equals("Rashod")) {
                            amount = -c.getDouble(c.getColumnIndex("amount"));
                        } else {
                            amount = c.getDouble(c.getColumnIndex("amount"));
                        }
                        Log.d("myLogs", "Из SMS balance " + Double.toString(balance));
                        balance = balance + amount - c.getDouble(c.getColumnIndex("comission"));
                        Log.d("myLogs", "Из SMS amount " + Double.toString(amount));
                        Log.d("myLogs", "Из SMS " + Double.toString(balance));
                        cv.put("calculatedbalance", Round.roundedDouble(balance));
                        db.update("mytable", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                        cv.clear();
                    } while (c.moveToNext());
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            DatabaseManager.getInstance().closeDatabase();

            //обновление listview в MainFragment
            Bundle args = new Bundle();
            Intent i = new Intent(SmsService.this, UpdateDBIntentService.class);
            args.putString("db", "updatemainfragment");
            i.putExtras(args);
            startService(i);

            //обновление баланса запланированных транзакций
            args.putString("db", "scheduleronlyrecalculate");
            args.putString("card", card);
            i.putExtras(args);
            startService(i);

        }
        return START_NOT_STICKY;
    }
}