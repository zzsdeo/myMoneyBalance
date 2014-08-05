package ru.zzsdeo.mymoneybalance;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerService extends Service {
    public AlarmManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service Running ", Toast.LENGTH_SHORT).show();
        Log.d("myLogs", "Service running");
        return super.onStartCommand(intent, flags, startId);
    }
}
