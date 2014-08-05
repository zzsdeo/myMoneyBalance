package ru.zzsdeo.mymoneybalance;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements
        AddDialog.OnAddRecordListener,
        EditDialog.OnEditRecordListener,
        ScheduleAddDialog.OnScheduleAddRecordListener,
        ActionBar.TabListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//<tabs
        ActionBar bar = getActionBar();

        assert bar != null;
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = bar.newTab();
        tab.setText("История");
        tab.setTabListener(this);
        bar.addTab(tab);

        tab = bar.newTab();
        tab.setText("Планирование");
        tab.setTabListener(this);
        bar.addTab(tab);
//tabs>

//<check DB
        Intent i = new Intent(this, AlarmManagerService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 10000, pi);
//check DB>

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_item:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void recordAdded() {
       getFragmentManager().beginTransaction().replace(android.R.id.content, new MainFragment(), "mainFragment").commit();
    }

    @Override
    public void recordEdited() {
       getFragmentManager().beginTransaction().replace(android.R.id.content, new MainFragment(), "mainFragment").commit();
    }

    @Override
    public void scheduleRecordAdded() {
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SchedulerFragment(), "schedulerFragment").commit();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (tab.getText().equals("История")) {
            fragmentTransaction.add(android.R.id.content, new MainFragment(), "mainFragment");
        }
        if (tab.getText().equals("Планирование")) {
            fragmentTransaction.add(android.R.id.content, new SchedulerFragment(), "schedulerFragment");
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (tab.getText().equals("История")) {
            fragmentTransaction.remove(getFragmentManager().findFragmentByTag("mainFragment"));
        }
        if (tab.getText().equals("Планирование")) {
            fragmentTransaction.remove(getFragmentManager().findFragmentByTag("schedulerFragment"));
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (tab.getText().equals("История")) {
            fragmentTransaction.add(android.R.id.content, new MainFragment(), "mainFragment");
        }
        if (tab.getText().equals("Планирование")) {
            fragmentTransaction.add(android.R.id.content, new SchedulerFragment(), "schedulerFragment");
        }
    }
}
