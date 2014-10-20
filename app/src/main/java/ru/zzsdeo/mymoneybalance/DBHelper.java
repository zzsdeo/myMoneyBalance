package ru.zzsdeo.mymoneybalance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 12;

    private void updateToV2(SQLiteDatabase db) {
        db.execSQL("alter table mytable add column date integer;");
    }

    private void updateToV3(SQLiteDatabase db) {
        db.beginTransaction();
        try {

            //change names of columns (date to datetime, sms to paymentdetails)
            db.execSQL("create temporary table mytable_tmp ("
                    + "id integer,"
                    + "date integer,"
                    + "sms text" + ");");
            db.execSQL("insert into mytable_tmp select id, date, sms from mytable;");
            db.execSQL("drop table mytable;");
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "datetime integer,"
                    + "paymentdetails text" + ");");
            db.execSQL("insert into mytable select id, date, sms from mytable_tmp;");
            db.execSQL("drop table mytable_tmp;");

            //add new columns
            db.execSQL("alter table mytable add column card text;");
            db.execSQL("alter table mytable add column typeoftransaction text;");
            db.execSQL("alter table mytable add column amount real;");
            db.execSQL("alter table mytable add column balance real;");
            db.execSQL("alter table mytable add column indebtedness real;");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void updateToV4(SQLiteDatabase db) {
        db.execSQL("alter table mytable add column comission real;");
    }

    private void updateToV5(SQLiteDatabase db) {
        db.beginTransaction();
        try {

            //change id to _id
            db.execSQL("create temporary table mytable_tmp ("
                    + "id integer,"
                    + "card text,"
                    + "datetime integer,"
                    + "paymentdetails text,"
                    + "typeoftransaction text,"
                    + "amount real,"
                    + "balance real,"
                    + "comission real,"
                    + "indebtedness real" + ");");
            db.execSQL("insert into mytable_tmp select id, card, datetime, paymentdetails, typeoftransaction, amount, balance, comission, indebtedness from mytable;");
            db.execSQL("drop table mytable;");
            db.execSQL("create table mytable ("
                    + "_id integer primary key autoincrement,"
                    + "card text,"
                    + "datetime integer,"
                    + "paymentdetails text,"
                    + "typeoftransaction text,"
                    + "amount real,"
                    + "balance real,"
                    + "comission real,"
                    + "indebtedness real" + ");");
            db.execSQL("insert into mytable select id, card, datetime, paymentdetails, typeoftransaction, amount, balance, comission, indebtedness from mytable_tmp;");
            db.execSQL("drop table mytable_tmp;");

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void updateToV6(SQLiteDatabase db) {
        db.execSQL("alter table mytable add column calculatedbalance real;");
    }

    private void updateToV7(SQLiteDatabase db) {
        db.execSQL("alter table mytable add column expenceincome text;");
        db.execSQL("alter table mytable add column label text;");
    }

    private void updateToV8(SQLiteDatabase db) {
        db.execSQL("create table scheduler ("
                + "_id integer primary key autoincrement,"
                + "card text,"
                + "datetime integer,"
                + "paymentdetails text,"
                + "typeoftransaction text,"
                + "amount real,"
                + "calculatedbalance real,"
                + "label text" + ");");
    }

    private void updateToV9(SQLiteDatabase db) {
        db.execSQL("alter table scheduler add column hash integer;");
    }

    private void updateToV10(SQLiteDatabase db) {
        db.execSQL("alter table scheduler add column repeat integer;");
        db.execSQL("alter table scheduler add column customrepeatvalue integer;");
    }

    private void updateToV11(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            //rewrite CardXXXX to Credit/Debit
            ContentValues cv = new ContentValues();
            Cursor c = db.query("mytable", null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    cv.put("card", "Debit");
                    db.update("mytable", cv, "card = 'Card2485'", null);
                    cv.put("card", "Credit");
                    db.update("mytable", cv, "card = 'Card0115'", null);
                } while (c.moveToNext());
            }

            c = db.query("scheduler", null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    cv.put("card", "Debit");
                    db.update("scheduler", cv, "card = 'Card2485'", null);
                    cv.put("card", "Credit");
                    db.update("scheduler", cv, "card = 'Card0115'", null);
                } while (c.moveToNext());
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void updateToV12(SQLiteDatabase db) {
        db.execSQL("alter table mytable add column searchindex text;");
        db.execSQL("alter table scheduler add column searchindex text;");
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            Cursor c = db.query("mytable", null, null, null, null, null, null);
            String index;
            if (c.moveToFirst()) {
                do {
                    index = c.getString(c.getColumnIndex("paymentdetails")).toLowerCase() + " " + DateFormat.format("dd.MM.yyyy", c.getLong(c.getColumnIndex("datetime")));
                    cv.put("searchindex", index);
                    db.update("mytable", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                } while (c.moveToNext());
            }

            c = db.query("scheduler", null, null, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    index = c.getString(c.getColumnIndex("paymentdetails")).toLowerCase() + " " + DateFormat.format("dd.MM.yyyy", c.getLong(c.getColumnIndex("datetime")));
                    cv.put("searchindex", index);
                    db.update("scheduler", cv, "_id = " + '"' + c.getInt(c.getColumnIndex("_id")) + '"', null);
                } while (c.moveToNext());
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public DBHelper(Context context) {
        super(context, "myDB", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table mytable ("
                + "_id integer primary key autoincrement,"
                + "card text,"
                + "datetime integer,"
                + "paymentdetails text,"
                + "typeoftransaction text,"
                + "amount real,"
                + "balance real,"
                + "comission real,"
                + "indebtedness real,"
                + "calculatedbalance real,"
                + "expenceincome text,"
                + "label text,"
                + "searchindex text" + ");");
        db.execSQL("create table scheduler ("
                + "_id integer primary key autoincrement,"
                + "card text,"
                + "datetime integer,"
                + "paymentdetails text,"
                + "typeoftransaction text,"
                + "amount real,"
                + "calculatedbalance real,"
                + "label text,"
                + "hash integer,"
                + "repeat integer,"
                + "customrepeatvalue integer,"
                + "searchindex text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion == DB_VERSION) {
            switch (oldVersion) {
                case 1:
                    updateToV2(db);
                    updateToV3(db);
                    updateToV4(db);
                    updateToV5(db);
                    updateToV6(db);
                    updateToV7(db);
                    updateToV8(db);
                    updateToV9(db);
                    updateToV10(db);
                    updateToV11(db);
                    updateToV12(db);
                    break;
                case 2:
                    updateToV3(db);
                    updateToV4(db);
                    updateToV5(db);
                    updateToV6(db);
                    updateToV7(db);
                    updateToV8(db);
                    updateToV9(db);
                    updateToV10(db);
                    updateToV11(db);
                    updateToV12(db);
                    break;
                case 3:
                    updateToV4(db);
                    updateToV5(db);
                    updateToV6(db);
                    updateToV7(db);
                    updateToV8(db);
                    updateToV9(db);
                    updateToV10(db);
                    updateToV11(db);
                    updateToV12(db);
                    break;
                case 4:
                    updateToV5(db);
                    updateToV6(db);
                    updateToV7(db);
                    updateToV8(db);
                    updateToV9(db);
                    updateToV10(db);
                    updateToV11(db);
                    updateToV12(db);
                    break;
                case 5:
                    updateToV6(db);
                    updateToV7(db);
                    updateToV8(db);
                    updateToV9(db);
                    updateToV10(db);
                    updateToV11(db);
                    updateToV12(db);
                    break;
                case 6:
                    updateToV7(db);
                    updateToV8(db);
                    updateToV9(db);
                    updateToV10(db);
                    updateToV11(db);
                    updateToV12(db);
                    break;
                case 7:
                    updateToV8(db);
                    updateToV9(db);
                    updateToV10(db);
                    updateToV11(db);
                    updateToV12(db);
                    break;
                case 8:
                    updateToV9(db);
                    updateToV10(db);
                    updateToV11(db);
                    updateToV12(db);
                    break;
                case 9:
                    updateToV10(db);
                    updateToV11(db);
                    updateToV12(db);
                    break;
                case 10:
                    updateToV11(db);
                    updateToV12(db);
                    break;
                case 11:
                    updateToV12(db);
                    break;
            }

        }
    }
}
