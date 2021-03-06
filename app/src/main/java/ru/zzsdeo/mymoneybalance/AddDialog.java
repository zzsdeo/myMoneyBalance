package ru.zzsdeo.mymoneybalance;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddDialog extends DialogFragment {

    //<vars
    private EditText paymentDetails, amount;
    private Button dateButton;
    private Button timeButton;
    private Calendar today = Calendar.getInstance();
    private int d = today.get(Calendar.DATE),
            m = today.get(Calendar.MONTH),
            y = today.get(Calendar.YEAR),
            h = today.get(Calendar.HOUR_OF_DAY),
            mi = today.get(Calendar.MINUTE);
    private String[] nameOfCard = {"Наличные", "Зарплатная", "Кредитная"};
    private String[] typeOfTransaction = {"Оплата", "Зачисление", "Снятие наличных"};
    private InsertRecord ir = new InsertRecord();
    private double balance, am;
    OnAddRecordListener addRecordListener;
//vars>

//<classes
    public interface OnAddRecordListener {
        public void recordAdded();
    }

    private class InsertRecord {

        private String mCard, mPaymentDetails, mTypeOfTransaction, mExpenceIncome;
        private long mDateTime;
        private double mAmount;

        public String getCard() {
            return mCard;
        }

        public void setCard(String card) {
            mCard = card;
        }

        public String getPaymentDetails() {
            return mPaymentDetails;
        }

        public void setPaymentDetails(String paymentDetails) {
            mPaymentDetails = paymentDetails;
        }

        public String getTypeOfTransaction() {
            return mTypeOfTransaction;
        }

        public void setTypeOfTransaction(String typeOfTransaction) {
            mTypeOfTransaction = typeOfTransaction;
        }

        public String getExpenceIncome() {
            return mExpenceIncome;
        }

        public void setExpenceIncome(String expenceIncome) {
            mExpenceIncome = expenceIncome;
        }

        public long getDateTime() {
            return mDateTime;
        }

        public void setDateTime(String dateTime) {
            SimpleDateFormat dfm = new SimpleDateFormat("dd.MM.yyyy_HH:mm");
            try {
                mDateTime = dfm.parse(dateTime).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public double getAmount() {
            return mAmount;
        }

        public void setAmount(double amount) {
            mAmount = amount;
        }

    }
//classes>

    //<functions
    OnDateSetListener callBackDPicker = new OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            today.set(year, monthOfYear, dayOfMonth);
            dateButton.setText(DateFormat.format("dd.MM.yyyy", today));
        }
    };
    OnTimeSetListener callBackTPicker = new OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            today.set(Calendar.HOUR_OF_DAY, hourOfDay);
            today.set(Calendar.MINUTE, minute);
            timeButton.setText(DateFormat.format("HH:mm", today));
        }
    };
//functions>

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            addRecordListener = (OnAddRecordListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnAddRecordListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Добавить");
        View v = inflater.inflate(R.layout.dialog_add, null);
//<date
        dateButton = (Button) v.findViewById(R.id.dateButton);
        dateButton.setText(DateFormat.format("dd.MM.yyyy", today));
        final DatePickerDialog datePicker = new DatePickerDialog(getActivity(), callBackDPicker, y, m, d);
        dateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePicker.show();
            }
        });
//date>

//<time
        timeButton = (Button) v.findViewById(R.id.timeButton);
        timeButton.setText(DateFormat.format("HH:mm", today));
        final TimePickerDialog timePicker = new TimePickerDialog(getActivity(), callBackTPicker, h, mi, true);
        timeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                timePicker.show();
            }
        });
//time>

//<card
        ArrayAdapter<String> cardAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, nameOfCard);
        cardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner cardSpinner = (Spinner) v.findViewById(R.id.cardSpinner);
        cardSpinner.setAdapter(cardAdapter);
        cardSpinner.setPrompt("Счет");
        cardSpinner.setSelection(0);
        cardSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        ir.setCard("Cash");
                        break;
                    case 1:
                        ir.setCard("Card2485");
                        break;
                    case 2:
                        ir.setCard("Card0115");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ir.setCard("Cash");
            }
        });
//card>

//<comment
        paymentDetails = (EditText) v.findViewById(R.id.editPaymentDetails);
        //paymentDetails.clearFocus();
//comment>

//<type of transaction
        ArrayAdapter<String> typeOfTransactionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, typeOfTransaction);
        typeOfTransactionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner typeSpinner = (Spinner) v.findViewById(R.id.typeSpinner);
        typeSpinner.setAdapter(typeOfTransactionAdapter);
        typeSpinner.setPrompt("Транзакция");
        typeSpinner.setSelection(0);
        typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        ir.setTypeOfTransaction("Oplata");
                        ir.setExpenceIncome("Rashod");
                        break;
                    case 1:
                        ir.setTypeOfTransaction("Zachislenie");
                        ir.setExpenceIncome("Dohod");
                        break;
                    case 2:
                        ir.setTypeOfTransaction("Nalichnie");
                        ir.setExpenceIncome("Rashod");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ir.setTypeOfTransaction("Oplata");
                ir.setExpenceIncome("Rashod");
            }
        });
//type of transaction>

//<amount
        amount = (EditText) v.findViewById(R.id.editAmount);
        //amount.clearFocus();
//amount>

//<save button
        Button saveButton = (Button) v.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (amount.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Необходимо ввести сумму!", Toast.LENGTH_LONG).show();
                } else {
                    ir.setDateTime(dateButton.getText().toString() + "_" + timeButton.getText().toString());
                    ir.setPaymentDetails(paymentDetails.getText().toString());
                    ir.setAmount(Double.parseDouble(amount.getText().toString().trim()));
                    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("card", ir.getCard());
                    cv.put("datetime", ir.getDateTime());
                    cv.put("paymentdetails", ir.getPaymentDetails());
                    cv.put("typeoftransaction", ir.getTypeOfTransaction());
                    cv.put("amount", Round.roundedDouble(ir.getAmount()));
                    cv.put("expenceincome", ir.getExpenceIncome());
                    cv.put("label", "Manual");
                    db.insert("mytable", null, cv);
                    //обновление баланса
                    cv.clear();
                    Cursor c = db.query("mytable", null, "card = " + '"' + ir.getCard() + '"', null, null, null, "datetime asc");
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
                    paymentDetails.setText("");
                    amount.setText("");
                    //обновление listview
                    addRecordListener.recordAdded();
                    dismiss();
                }
            }
        });
//save button>

//<cancel
        Button cancelButton = (Button) v.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                paymentDetails.setText("");
                amount.setText("");
                dismiss();
            }
        });
//cancel>

        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        paymentDetails.setText("");
        amount.setText("");
    }
}