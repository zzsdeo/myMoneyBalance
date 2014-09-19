package ru.zzsdeo.mymoneybalance;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class MySimpleCursorAdapter extends SimpleCursorAdapter {

    public MySimpleCursorAdapter(Context context, int layout, Cursor c,
                                 String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO Auto-generated method stub
        String card = cursor.getString(cursor.getColumnIndex("card")),
                details = cursor.getString(cursor.getColumnIndex("paymentdetails")),
                date = (String) DateFormat.format("dd.MM.yyyy, HH:mm", cursor.getLong(cursor.getColumnIndex("datetime"))),
                expenceIncome = cursor.getString(cursor.getColumnIndex("expenceincome"));
        double amount = cursor.getDouble(cursor.getColumnIndex("amount")),
                cBalance = cursor.getDouble(cursor.getColumnIndex("calculatedbalance")),
                balance = cursor.getDouble(cursor.getColumnIndex("balance"));
        TextView lvCard = (TextView) view.findViewById(R.id.lvCard),
                lvDetails = (TextView) view.findViewById(R.id.lvDetails),
                lvDateTime = (TextView) view.findViewById(R.id.lvDateTime),
                lvAmount = (TextView) view.findViewById(R.id.lvAmount),
                lvBalance = (TextView) view.findViewById(R.id.lvBalance);
        if (balance!=0 && balance!=cBalance) {
            lvCard.setTextColor(Color.RED);
        } else {
            lvCard.setTextColor(Color.BLACK);
        }
        if (card!=null && card.equals("Debit")) {
            lvCard.setText("Зарплатная");
        }
        if (card!=null && card.equals("Credit")) {
            lvCard.setText("Кредитная");
        }
        if (card!=null && card.equals("Cash")) {
            lvCard.setText("Наличные");
        }
        lvDetails.setText(details);
        lvDateTime.setText(date);
        if (expenceIncome!=null && expenceIncome.equals("Rashod")) {
            lvAmount.setTextColor(Color.RED);
            lvAmount.setText("-" + Double.toString(amount));
        } else {
            lvAmount.setTextColor(Color.GREEN);
            lvAmount.setText(Double.toString(amount));
        }
        if (cBalance < 0) {
            lvBalance.setTextColor(Color.RED);
        } else {
            lvBalance.setTextColor(Color.BLACK);
        }
        lvBalance.setText(Double.toString(cBalance));
    }

}
