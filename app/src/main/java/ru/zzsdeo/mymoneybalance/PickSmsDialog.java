package ru.zzsdeo.mymoneybalance;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public class PickSmsDialog extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Uri uriSms = Uri.parse("content://sms/inbox");
        String[] from = new String[]{"_id", "body", "date"};
        final Cursor c = getActivity().getContentResolver().query(uriSms, from, "address = 'Telecard'", null, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setCursor(c, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (c.moveToPosition(i)) {
                            Bundle smsBundle = new Bundle();
                            smsBundle.putString("SMS", c.getString(c.getColumnIndex("body")));
                            smsBundle.putLong("DATE_IN_MILL", c.getLong(c.getColumnIndex("date")));
                            getActivity().startService(new Intent(getActivity(), SmsService.class).putExtras(smsBundle));
                        }
                    }
                }, "body")
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        return adb.create();
    }
}
