package ru.zzsdeo.mymoneybalance;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScheduleDeleteDialog extends DialogFragment {

//<vars
    private Calendar today = Calendar.getInstance();
    private RadioGroup radioGroup;
    Bundle args;
//vars>

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Удалить");
        View v = inflater.inflate(R.layout.dialog_delete_schedule, null);

//<radio group
        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroupDeleteSchedule);
        RadioButton rbDeleteAll = (RadioButton) v.findViewById(R.id.rbDeleteAll);
        rbDeleteAll.setChecked(true);
//radio group>


//<save button
        Button saveButton = (Button) v.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                args = new Bundle();
                args.putString("db", "schedulerdelete");
                args.putLong("id", getArguments().getLong("id"));
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.rbDeleteAll:
                        args.putInt("rbDeletePos", 1);
                        break;
                    case R.id.rbDeleteThis:
                        args.putInt("rbDeletePos", 2);
                        break;
                }
                Intent i = new Intent(getActivity(), UpdateDBIntentService.class);
                getActivity().startService(i.putExtras(args));
                dismiss();
            }
        });
//save button>

//<cancel
        Button cancelButton = (Button) v.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//cancel>
        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}