package ru.zzsdeo.mymoneybalance;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScheduleEditAllDialog extends DialogFragment {

    //<vars
    private EditText amount, tvCustom;
    private AutoCompleteTextView paymentDetails;
    private Button dateButton;
    private Button timeButton;
    private Calendar today = Calendar.getInstance();
    private int d, m, y, h, mi, repeat, customRepeat, hash;
    private String[] nameOfCard = {"Наличные", "Зарплатная", "Кредитная"};
    private String[] typeOfTransaction = {"Оплата", "Зачисление"};
    private InsertRecord ir = new InsertRecord();
    private RadioGroup radioGroup;
    private RadioButton rbEveryDayOfMonth, rbEveryDayOfWeek;
    private CheckBox repeatedCheckBox, confirmCheckBox;
    private View llCustom;
    private Bundle args;
    private String card, comment, type, confirmation;
    private double amnt;
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

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
            rbEveryDayOfMonth.setText("Каждое " + DateFormat.format("dd", today) + " число");
            rbEveryDayOfWeek.setText("По дням недели (" + DateFormat.format("E", today) + ")");
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor c = db.query("scheduler", null, "_id = " + getArguments().getLong("id"), null, null, null, null);
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
        confirmation = c.getString(c.getColumnIndex("label"));
        repeat = c.getInt(c.getColumnIndex("repeat"));
        customRepeat = c.getInt(c.getColumnIndex("customrepeatvalue"));
        hash = c.getInt(c.getColumnIndex("hash"));
    }

    //functions>
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Редактировать");
        View v = inflater.inflate(R.layout.dialog_add_schedule, null);
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
        if (card.equals("Debit")) {
            cardSpinner.setSelection(1);
        }
        if (card.equals("Credit")) {
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
                        ir.setCard("Debit");
                        break;
                    case 2:
                        ir.setCard("Credit");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (card.equals("Cash")) {
                    ir.setCard("Cash");
                }
                if (card.equals("Debit")) {
                    ir.setCard("Debit");
                }
                if (card.equals("Credit")) {
                    ir.setCard("Credit");
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
        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
                        break;
                    case 1:
                        ir.setTypeOfTransaction("Zachislenie");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (type.equals("Oplata")) {
                    ir.setTypeOfTransaction("Oplata");
                } else {
                    ir.setTypeOfTransaction("Zachislenie");
                }
            }
        });
//type of transaction>

//<amount
        amount = (EditText) v.findViewById(R.id.editAmount);
        amount.setText(Double.toString(Math.abs(amnt)));
//amount>

//<repeat checkbox
        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroupAddSchedule);
        repeatedCheckBox = (CheckBox) v.findViewById(R.id.repeatedCheckBox);
        repeatedCheckBox.setChecked(true);
        repeatedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    radioGroup.setVisibility(View.VISIBLE);
                } else {
                    radioGroup.setVisibility(View.GONE);
                }
            }
        });
//repeate checkbox>

//<radio group
        rbEveryDayOfMonth = (RadioButton) v.findViewById(R.id.rbEveryDayOfMonth);
        rbEveryDayOfMonth.setText("Каждое " + DateFormat.format("dd", today) + " число");
        rbEveryDayOfWeek = (RadioButton) v.findViewById(R.id.rbEveryDayOfWeek);
        rbEveryDayOfWeek.setText("По дням недели (" + DateFormat.format("E", today) + ")");
        RadioButton rbLastDayOfMonth = (RadioButton) v.findViewById(R.id.rbLastDayOfMonth);
        RadioButton rbEveryDay = (RadioButton) v.findViewById(R.id.rbEveryDay);
        RadioButton rbEveryWorkingDay = (RadioButton) v.findViewById(R.id.rbEveryWorkingDay);
        RadioButton rbCustom = (RadioButton) v.findViewById(R.id.rbCustom);
        llCustom = v.findViewById(R.id.llCustom);
        llCustom.setVisibility(View.INVISIBLE);
        tvCustom = (EditText) v.findViewById(R.id.tvCustom);
        switch (repeat) {
            case 1:
                rbEveryDayOfMonth.setChecked(true);
                break;
            case 2:
                rbLastDayOfMonth.setChecked(true);
                break;
            case 3:
                rbEveryDay.setChecked(true);
                break;
            case 4:
                rbEveryWorkingDay.setChecked(true);
                break;
            case 5:
                rbEveryDayOfWeek.setChecked(true);
                break;
            case 6:
                rbCustom.setChecked(true);
                llCustom.setVisibility(View.VISIBLE);
                tvCustom.setText(Integer.toString(customRepeat));
                break;
        }
        confirmCheckBox = (CheckBox) v.findViewById(R.id.confirmCheckBox);
        if (confirmation != null) {
            confirmCheckBox.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbCustom:
                        llCustom.setVisibility(View.VISIBLE);
                        break;
                    default:
                        llCustom.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });
//radio group>


//<save button
        Button saveButton = (Button) v.findViewById(R.id.saveButton);
        saveButton.setText("Редактировать");
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (amount.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Необходимо ввести сумму!", Toast.LENGTH_LONG).show();
                } else {
                    boolean flag = true;
                    ir.setDateTime(dateButton.getText().toString() + "_" + timeButton.getText().toString());
                    ir.setPaymentDetails(paymentDetails.getText().toString());
                    if (ir.getTypeOfTransaction().equals("Oplata")) {
                        ir.setAmount(-Double.parseDouble(amount.getText().toString().trim()));
                    } else {
                        ir.setAmount(Double.parseDouble(amount.getText().toString().trim()));
                    }
                    args = new Bundle();
                    args.putString("db", "scheduler");
                    args.putString("card", ir.getCard());
                    args.putString("paymentdetails", ir.getPaymentDetails());
                    args.putString("typeoftransaction", ir.getTypeOfTransaction());
                    args.putDouble("amount", Round.roundedDouble(ir.getAmount()));
                    if (confirmCheckBox.isChecked()) {
                        args.putString("label", "NeedConfirmation");
                    }
                    if (repeatedCheckBox.isChecked()) {
                        int hashCode = (ir.getPaymentDetails() + ir.getCard() + ir.getDateTime()).hashCode();
                        args.putInt("hash", hashCode);
                        args.putLong("datetime", today.getTimeInMillis());
                        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                        switch (checkedRadioButtonId) {
                            case R.id.rbEveryDayOfMonth:
                                args.putInt("rbPos", 1);
                                break;
                            case R.id.rbLastDayOfMonth:
                                args.putInt("rbPos", 2);
                                break;
                            case R.id.rbEveryDay:
                                args.putInt("rbPos", 3);
                                break;
                            case R.id.rbEveryWorkingDay:
                                args.putInt("rbPos", 4);
                                break;
                            case R.id.rbEveryDayOfWeek:
                                args.putInt("rbPos", 5);
                                break;
                            case R.id.rbCustom:
                                args.putInt("rbPos", 6);
                                if (!tvCustom.getText().toString().equals("")) {
                                    args.putString("custom", tvCustom.getText().toString());
                                } else {
                                    flag = false;
                                }
                                break;
                        }
                    } else {
                        args.putInt("rbPos", 0);
                        args.putLong("datetime", ir.getDateTime());
                        args.putString("label", "NeedConfirmation");
                    }
                    if (flag) {
                        db.delete("scheduler", "hash = " + '"' + hash + '"', null);
                        Intent i = new Intent(getActivity(), UpdateDBIntentService.class);
                        getActivity().startService(i.putExtras(args));
                        //обновление всех карт если изменен счет
                        if (!card.equals(ir.getCard())) {
                            args.putString("db", "recalculateallscheduler");
                            getActivity().startService(i.putExtras(args));
                        }
                        //очистка формы
                        paymentDetails.setText("");
                        amount.setText("");
                        tvCustom.setText("");
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Необходимо ввести количество дней!", Toast.LENGTH_LONG).show();
                    }
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