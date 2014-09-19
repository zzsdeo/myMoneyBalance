package ru.zzsdeo.mymoneybalance;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//<start stop service
        PackageManager pm = getActivity().getPackageManager();
        ComponentName component = new ComponentName(getActivity(), SmsReceiver.class);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (!settings.getBoolean("start_service", true)) {
            pm.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        if ((settings.getBoolean("start_service", true)) & (pm.getComponentEnabledSetting(component) != PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)) {
            pm.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
        }
//start stop service>
    }
}
