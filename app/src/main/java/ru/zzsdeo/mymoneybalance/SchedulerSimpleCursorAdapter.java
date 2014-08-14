package ru.zzsdeo.mymoneybalance;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class SchedulerSimpleCursorAdapter extends SimpleCursorAdapter {

    public SchedulerSimpleCursorAdapter(Context context, int layout, Cursor c,
                                        String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String card = cursor.getString(cursor.getColumnIndex("card")),
                details = cursor.getString(cursor.getColumnIndex("paymentdetails")),
                date = (String) DateFormat.format("dd.MM.yyyy, HH:mm", cursor.getLong(cursor.getColumnIndex("datetime"))),
                label = cursor.getString(cursor.getColumnIndex("label"));
        double amount = cursor.getDouble(cursor.getColumnIndex("amount")),
                cBalance = cursor.getDouble(cursor.getColumnIndex("calculatedbalance"));
        TextView lvCard = (TextView) view.findViewById(R.id.lvCard),
                lvDetails = (TextView) view.findViewById(R.id.lvDetails),
                lvDateTime = (TextView) view.findViewById(R.id.lvDateTime),
                lvAmount = (TextView) view.findViewById(R.id.lvAmount),
                lvBalance = (TextView) view.findViewById(R.id.lvBalance);
        if (card!=null && card.equals("Card2485")) {
            lvCard.setText("Зарплатная");
        }
        if (card!=null && card.equals("Card0115")) {
            lvCard.setText("Кредитная");
        }
        if (card!=null && card.equals("Cash")) {
            lvCard.setText("Наличные");
        }
        if (label != null) {
            if (label.equals("NotConfirmed")) {
                lvCard.setTextColor(Color.BLUE);
                lvCard.append(" - транзакция не подтверждена!");
            } else {
                lvCard.setTextColor(Color.BLACK);
            }
        } else {
            lvCard.setTextColor(Color.BLACK);
        }
        lvDetails.setText(details);
        lvDateTime.setText(date);
        if (amount < 0) {
            lvAmount.setTextColor(Color.RED);
        } else {
            lvAmount.setTextColor(Color.GREEN);
        }
        lvAmount.setText(Double.toString(amount));
        if (cBalance < 0) {
            lvBalance.setTextColor(Color.RED);
        } else {
            lvBalance.setTextColor(Color.BLACK);
        }
        lvBalance.setText(Double.toString(cBalance));
    }

}
