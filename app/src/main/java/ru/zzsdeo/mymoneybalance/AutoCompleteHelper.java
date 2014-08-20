package ru.zzsdeo.mymoneybalance;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AutoCompleteHelper {

    public AutoCompleteHelper() {
    }

    public static String[] getArray() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String[] columns = new String[]{"distinct paymentdetails"};
        Cursor c = db.query("mytable", columns, null, null, null, null, null);
        Cursor b = db.query("scheduler", columns, null, null, null, null, null);
        String[] paymentDetailsArray = new String[c.getCount() + b.getCount()];
        String[] paymentDetailsArrayC = new String[c.getCount()];
        if (c.moveToFirst()) {
            int i = 0;
            do {
                paymentDetailsArrayC[i] = c.getString(c.getColumnIndex("paymentdetails"));
                i++;
            } while (c.moveToNext());
        }

        String[] paymentDetailsArrayB = new String[b.getCount()];
        if (b.moveToFirst()) {
            int i = 0;
            do {
                paymentDetailsArrayB[i] = b.getString(b.getColumnIndex("paymentdetails"));
                i++;
            } while (b.moveToNext());
        }
        System.arraycopy(paymentDetailsArrayC, 0, paymentDetailsArray, 0, paymentDetailsArrayC.length);
        System.arraycopy(paymentDetailsArrayB, 0, paymentDetailsArray, paymentDetailsArrayC.length, paymentDetailsArrayB.length);
        return paymentDetailsArray;
    }
}
