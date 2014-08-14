package ru.zzsdeo.mymoneybalance;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class ScheduleConfirmSelectionDialog extends DialogFragment {

//<vars
    private RadioGroup radioGroup;
    Bundle args;
    private long id;
//vars>

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Подтвердить");
        View v = inflater.inflate(R.layout.dialog_selection_confirm_schedule, null);
        id = getArguments().getLong("id");

//<radio group
        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroupConfirmSchedule);
        RadioButton rbConfirm = (RadioButton) v.findViewById(R.id.rbConfirm);
        rbConfirm.setChecked(true);
//radio group>


//<save button
        Button saveButton = (Button) v.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                args = new Bundle();
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.rbConfirm:
                        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
                        Cursor c = db.query("scheduler", null, "_id = " + id, null, null, null, null);
                        c.moveToFirst();
                        args.putString("card", c.getString(c.getColumnIndex("card")));
                        db.delete("scheduler", "_id = " + id, null);
                        args.putString("db", "scheduleronlyrecalculate");
                        Intent i = new Intent(getActivity(), UpdateDBIntentService.class);
                        getActivity().startService(i.putExtras(args));
                        break;
                    case R.id.rbConfirmHistory:
                        args.putLong("id", id);
                        DialogFragment scheduleConfirmToHistoryDialog = new ScheduleConfirmToHistoryDialog();
                        scheduleConfirmToHistoryDialog.setArguments(args);
                        scheduleConfirmToHistoryDialog.show(getFragmentManager(), "scheduleConfirmToHistoryDialog");
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