package ru.zzsdeo.mymoneybalance;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class DetailsActivity extends FragmentActivity {

    ViewPager pager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActionBar bar = getActionBar();
        assert bar != null;
        bar.setDisplayHomeAsUpEnabled(true);

        pager = (ViewPager) findViewById(R.id.pager);
        String[] projection = {"_id", "card", "datetime", "paymentdetails", "typeoftransaction", "amount", "balance", "comission", "indebtedness", "calculatedbalance", "expenceincome", "label"};
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor c = db.query("mytable", null, null, null, null, null, "datetime asc, _id asc");;
        switch (getIntent().getIntExtra("filter", 0)) {
            case 1:
                c = db.query("mytable", null, "card = 'Cash'", null, null, null, "datetime asc, _id asc");
                break;
            case 2:
                c = db.query("mytable", null, "card = 'Debit'", null, null, null, "datetime asc, _id asc");
                break;
            case 3:
                c = db.query("mytable", null, "card = 'Credit'", null, null, null, "datetime asc, _id asc");
                break;
        }
        pagerAdapter = new CursorPagerAdapter<DetailsFragment>(getSupportFragmentManager(), DetailsFragment.class, projection, c);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(pagerAdapter.getCount() - getIntent().getIntExtra("position", 0) - 1);
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
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.removeItem(R.id.add_item);
        menu.removeItem(R.id.settings_item);
        return super.onPrepareOptionsMenu(menu);
    }
}
