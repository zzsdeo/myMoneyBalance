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

public class ScheduleEditSelectionDialog extends DialogFragment {

//<vars
    private RadioGroup radioGroup;
    Bundle args;
//vars>

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Редактировать");
        View v = inflater.inflate(R.layout.dialog_selection_edit_schedule, null);
        args = new Bundle();
        args.putLong("id", getArguments().getLong("id"));

//<radio group
        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroupEditSchedule);
        RadioButton rbEditAll = (RadioButton) v.findViewById(R.id.rbEditAll);
        rbEditAll.setChecked(true);
//radio group>


//<save button
        Button saveButton = (Button) v.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.rbEditAll:
                        DialogFragment scheduleEditAllDialog = new ScheduleEditAllDialog();
                        scheduleEditAllDialog.setArguments(args);
                        scheduleEditAllDialog.show(getFragmentManager(), "scheduleEditAllDialog");
                        break;
                    case R.id.rbEditThis:
                        DialogFragment scheduleEditThisDialog = new ScheduleEditThisDialog();
                        scheduleEditThisDialog.setArguments(args);
                        scheduleEditThisDialog.show(getFragmentManager(), "scheduleEditThisDialog");
                        break;
                }
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