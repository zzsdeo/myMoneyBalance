package ru.zzsdeo.mymoneybalance;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;

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
                if (c.getString(c.getColumnIndex("paymentdetails")) != null) {
                    paymentDetailsArrayC[i] = c.getString(c.getColumnIndex("paymentdetails"));
                } else {
                    paymentDetailsArrayC[i] = "";
                }
                i++;
            } while (c.moveToNext());
        }

        String[] paymentDetailsArrayB = new String[b.getCount()];
        if (b.moveToFirst()) {
            int i = 0;
            do {
                if (b.getString(b.getColumnIndex("paymentdetails")) != null) {
                    paymentDetailsArrayB[i] = b.getString(b.getColumnIndex("paymentdetails"));
                } else {
                    paymentDetailsArrayB[i] = "";
                }
                i++;
            } while (b.moveToNext());
        }
        System.arraycopy(paymentDetailsArrayC, 0, paymentDetailsArray, 0, paymentDetailsArrayC.length);
        System.arraycopy(paymentDetailsArrayB, 0, paymentDetailsArray, paymentDetailsArrayC.length, paymentDetailsArrayB.length);
        /*for(int i = 0; i < paymentDetailsArray.length; i++) {
            Log.d("myLogs", paymentDetailsArray[i]+"\n");
        }*/
        Arrays.sort(paymentDetailsArray);
        /*Log.d("myLogs", "---отсортировано---\n");
        for(int i = 0; i < paymentDetailsArray.length; i++) {
            Log.d("myLogs", paymentDetailsArray[i]+"\n");
        }
        Log.d("myLogs", "---удалено---\n");*/

        ArrayList<String> paymentDetailsArrayList = new ArrayList<String>();
        String item;
        for (int i = 0; i < paymentDetailsArray.length; i++) {
            item = paymentDetailsArray[i];
            if (i != paymentDetailsArray.length - 1) {
                if (!item.equals(paymentDetailsArray[i + 1])) {
                    paymentDetailsArrayList.add(item);
                    //Log.d("myLogs", item + "\n");
                }
            } else {
                paymentDetailsArrayList.add(item);
                //Log.d("myLogs", item + "\n");
            }
        }
        //Log.d("myLogs", "кол " + paymentDetailsArrayList.size() + "\n");
        paymentDetailsArray = paymentDetailsArrayList.toArray(new String[paymentDetailsArrayList.size()]);
        return paymentDetailsArray;
    }
}
