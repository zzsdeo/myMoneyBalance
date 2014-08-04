package ru.zzsdeo.mymoneybalance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        Object[] messages = (Object[]) bundle.get("pdus");
        SmsMessage[] sms = new SmsMessage[messages.length];

        for (int n = 0; n < messages.length; n++) {
            sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
        }

        for (SmsMessage msg : sms) {
            if (msg.getOriginatingAddress().equals("Telecard")) {
                Bundle smsBundle = new Bundle();
                smsBundle.putString("SMS", msg.getMessageBody());
                smsBundle.putLong("DATE_IN_MILL", msg.getTimestampMillis());
                context.startService(new Intent(context, SmsService.class).putExtras(smsBundle));
            }
        }
    }

}
