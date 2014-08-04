package ru.zzsdeo.mymoneybalance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 8;

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
                + "label text" + ");");
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
                    break;
                case 2:
                    updateToV3(db);
                    updateToV4(db);
                    updateToV5(db);
                    updateToV6(db);
                    updateToV7(db);
                    updateToV8(db);
                    break;
                case 3:
                    updateToV4(db);
                    updateToV5(db);
                    updateToV6(db);
                    updateToV7(db);
                    updateToV8(db);
                    break;
                case 4:
                    updateToV5(db);
                    updateToV6(db);
                    updateToV7(db);
                    updateToV8(db);
                    break;
                case 5:
                    updateToV6(db);
                    updateToV7(db);
                    updateToV8(db);
                    break;
                case 6:
                    updateToV7(db);
                    updateToV8(db);
                    break;
                case 7:
                    updateToV8(db);
                    break;
            }

        }
    }
}
