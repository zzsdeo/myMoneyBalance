package ru.zzsdeo.mymoneybalance;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements ActionBar.TabListener {

    BroadcastReceiver receiver;
    ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//<tabs
        bar = getActionBar();
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

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra("db").equals("scheduler") && getFragmentManager().findFragmentByTag("schedulerFragment") != null) {
                    getFragmentManager().findFragmentByTag("schedulerFragment").getLoaderManager().getLoader(1).forceLoad();
                    SchedulerFragment.minMaxBalance(getFragmentManager().findFragmentByTag("schedulerFragment").getView());
                }
                if (intent.getStringExtra("db").equals("mytable") && getFragmentManager().findFragmentByTag("mainFragment") != null) {
                    getFragmentManager().findFragmentByTag("mainFragment").getLoaderManager().getLoader(0).forceLoad();
                    MainFragment.myBalance(getFragmentManager().findFragmentByTag("mainFragment").getView());
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(UpdateDBIntentService.UPDATE_RESULT));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        bar.setDisplayShowCustomEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_item:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.backup_item:
                startActivity(new Intent(this, ExportImportDB.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (tab.getText().equals("История")) {
            fragmentTransaction.replace(android.R.id.content, new MainFragment(), "mainFragment");
        }
        if (tab.getText().equals("Планирование")) {
            fragmentTransaction.replace(android.R.id.content, new SchedulerFragment(), "schedulerFragment");
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
