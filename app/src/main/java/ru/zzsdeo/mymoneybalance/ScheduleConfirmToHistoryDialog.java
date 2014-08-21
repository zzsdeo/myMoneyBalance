package ru.zzsdeo.mymoneybalance;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScheduleConfirmToHistoryDialog extends DialogFragment {

    //<vars
    private EditText amount;
    private AutoCompleteTextView paymentDetails;
    private Button dateButton;
    private Button timeButton;
    private Calendar today = Calendar.getInstance();
    private int d, m, y, h, mi;
    private String[] nameOfCard = {"Наличные", "Зарплатная", "Кредитная"};
    private String[] typeOfTransaction = {"Оплата", "Зачисление"};
    private InsertRecord ir = new InsertRecord();
    private double amnt;
    private String card, comment, type;
    private long id;
//vars>

//<classes
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getLong("id");
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor c = db.query("scheduler", null, "_id = " + id, null, null, null, null);
        c.moveToFirst();
        today.setTimeInMillis(c.getLong(c.getColumnIndex("datetime")));
        d = today.get(Calendar.DATE);
        m = today.get(Calendar.MONTH);
        y = today.get(Calendar.YEAR);
        h = today.get(Calendar.HOUR_OF_DAY);
        mi = today.get(Calendar.MINUTE);
        card = c.getString(c.getColumnIndex("card"));
        comment = c.getString(c.getColumnIndex("paymentdetails"));
        type = c.getString(c.getColumnIndex("typeoftransaction"));
        amnt = c.getDouble(c.getColumnIndex("amount"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Добавить в историю");
        View v = inflater.inflate(R.layout.dialog_edit, null);
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
        if (card.equals("Cash")) {
            cardSpinner.setSelection(0);
        }
        if (card.equals("Card2485")) {
            cardSpinner.setSelection(1);
        }
        if (card.equals("Card0115")) {
            cardSpinner.setSelection(2);
        }
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
                if (card.equals("Cash")) {
                    ir.setCard("Cash");
                }
                if (card.equals("Card2485")) {
                    ir.setCard("Card2485");
                }
                if (card.equals("Card0115")) {
                    ir.setCard("Card0115");
                }
            }
        });
//card>

//<comment
        paymentDetails = (AutoCompleteTextView) v.findViewById(R.id.editPaymentDetails);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, AutoCompleteHelper.getArray());
        paymentDetails.setAdapter(adapter);
        paymentDetails.setText(comment);
        paymentDetails.clearFocus();
//comment>

//<type of transaction
        ArrayAdapter<String> typeOfTransactionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, typeOfTransaction);
        typeOfTransactionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner typeSpinner = (Spinner) v.findViewById(R.id.typeSpinner);
        typeSpinner.setAdapter(typeOfTransactionAdapter);
        typeSpinner.setPrompt("Транзакция");
        if (type.equals("Oplata")) {
            typeSpinner.setSelection(0);
        } else {
            typeSpinner.setSelection(1);
        }
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (type.equals("Oplata")) {
                    ir.setTypeOfTransaction("Oplata");
                    ir.setExpenceIncome("Rashod");
                } else {
                    ir.setTypeOfTransaction("Zachislenie");
                    ir.setExpenceIncome("Dohod");
                }
            }
        });
//type of transaction>

//<amount
        amount = (EditText) v.findViewById(R.id.editAmount);
        amount.setText(Double.toString(Math.abs(amnt)));
        //amount.clearFocus();
//amount>

//<save button
        Button saveButton = (Button) v.findViewById(R.id.saveButton);
        saveButton.setText("Добавить");
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
                    db.delete("scheduler", "_id = " + id, null);
                    db.insert("mytable", null, cv);
                    //обновление баланса
                    Bundle args = new Bundle();
                    args.putString("db", "mytable");
                    args.putString("card", ir.getCard());
                    Intent i = new Intent(getActivity(), UpdateDBIntentService.class);
                    getActivity().startService(i.putExtras(args));
                    //обновление баланса на запланированных транзакциях
                    args.putString("db", "scheduleronlyrecalculate");
                    getActivity().startService(i.putExtras(args));
                    //очистка формы
                    paymentDetails.setText("");
                    amount.setText("");
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