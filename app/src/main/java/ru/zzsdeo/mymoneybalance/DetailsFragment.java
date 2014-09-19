package ru.zzsdeo.mymoneybalance;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailsFragment extends Fragment {
    String _id, card, datetime, date, time, paymentdetails, typeoftransaction, amount, balance, comission, indebtedness, calculatedbalance, expenceincome, label;
    double difference;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _id = getArguments().getString("_id");
        card = getArguments().getString("card");
        datetime = getArguments().getString("datetime");
        paymentdetails = getArguments().getString("paymentdetails");
        typeoftransaction = getArguments().getString("typeoftransaction");
        amount = getArguments().getString("amount");
        balance = getArguments().getString("balance");
        comission = getArguments().getString("comission");
        indebtedness = getArguments().getString("indebtedness");
        calculatedbalance = getArguments().getString("calculatedbalance");
        expenceincome = getArguments().getString("expenceincome");
        label = getArguments().getString("label");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, null);

        TextView tvCard = (TextView)view.findViewById(R.id.cardDetails);
        if (card.equals("Cash")) {
            card = "Наличные";
        }
        if (card.equals("Debit")) {
            card = "Зарплатная";
        }
        if (card.equals("Credit")) {
            card = "Кредитная";
        }
        tvCard.setText(card);

        date = (String) DateFormat.format("dd.MM.yyyy", Long.parseLong(datetime));
        TextView tvDate = (TextView) view.findViewById(R.id.dateDetails);
        tvDate.setText(date);

        time = (String) DateFormat.format("HH:mm", Long.parseLong(datetime));
        TextView tvTime = (TextView) view.findViewById(R.id.timeDetails);
        tvTime.setText(time);

        TextView tvPaymentdetails = (TextView)view.findViewById(R.id.paymentDetails);
        tvPaymentdetails.setText(paymentdetails);

        TextView tvTypeoftransaction = (TextView)view.findViewById(R.id.typeOfTransactionDetails);
        tvTypeoftransaction.setText(typeoftransaction);

        TextView tvAmount = (TextView)view.findViewById(R.id.amountDetails);
        if (expenceincome.equals("Rashod")) {
            tvAmount.setTextColor(Color.RED);
            tvAmount.setText("-"+amount);
        } else {
            tvAmount.setText(amount);
        }

        TextView tvBalance = (TextView)view.findViewById(R.id.balanceDetails);
        tvBalance.setText(balance);

        TextView tvComission = (TextView)view.findViewById(R.id.comissionDetails);
        tvComission.setTextColor(Color.RED);
        if (comission!=null) {
            tvComission.setText("-" + comission);
        }

        TextView tvIndebtedness = (TextView)view.findViewById(R.id.indebtednessDetails);
        tvIndebtedness.setText(indebtedness);

        TextView tvCalculatedbalance = (TextView)view.findViewById(R.id.calculatedBalanceDetails);
        if (Double.parseDouble(calculatedbalance)<0) {
            tvCalculatedbalance.setTextColor(Color.RED);
        }
        tvCalculatedbalance.setText(calculatedbalance);

        TextView tvLabel = (TextView)view.findViewById(R.id.labelDetails);
        tvLabel.setText(label);

        if (balance!=null && calculatedbalance!=null) {
            difference = Double.parseDouble(balance) - Double.parseDouble(calculatedbalance);
        }
        TextView tvDifference = (TextView) view.findViewById(R.id.balanceWarning);
        if (difference!=0) {
            tvDifference.setText("Баланс отличается от рассчитанного на " + Round.roundedDouble(difference));
        }

        return view;
    }
}
