package ru.zzsdeo.mymoneybalance;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

public class MainFragment extends Fragment implements LoaderCallbacks<Cursor> {

//<vars
    private TextView warningText;
    private MySimpleCursorAdapter scAdapter;
    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
//vars>

//<functions
    static void myBalance (View v) {
        TextView cardInfo = (TextView) v.findViewById(R.id.cardInfo);
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor c = db.query("mytable", null, "card = 'Cash'", null, null, null, "datetime desc, _id desc");
        if (c.moveToFirst()) {
            cardInfo.setText("Наличные: " + Double.toString(c.getDouble(c.getColumnIndex("calculatedbalance"))) + "\n");
        }
        c = db.query("mytable", null, "card = 'Card2485'", null, null, null, "datetime desc, _id desc");
        if (c.moveToFirst()) {
            cardInfo.append("Зарплатная: " + Double.toString(c.getDouble(c.getColumnIndex("calculatedbalance"))) + "\n");
        }
        c = db.query("mytable", null, "card = 'Card0115'", null, null, null, "datetime desc, _id desc");
        if (c.moveToFirst()) {
            cardInfo.append("Кредитная: " + Double.toString(c.getDouble(c.getColumnIndex("calculatedbalance"))));
        }
    }
//functions>

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                DialogFragment addDialog = new AddDialog();
                addDialog.show(getFragmentManager(), "addDialog");
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Bundle args;
        // получаем из пункта контекстного меню данные по пункту списка
        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case CM_DELETE_ID:
                // извлекаем id записи и удаляем соответствующую запись в БД
                SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
                Cursor c = db.query("mytable", null, "_id = " + acmi.id, null, null, null, null);
                c.moveToFirst();
                String card = c.getString(c.getColumnIndex("card"));
                db.delete("mytable", "_id = " + acmi.id, null);
                //обновляем баланс
                args = new Bundle();
                args.putString("db", "mytable");
                args.putString("card", card);
                Intent i = new Intent(getActivity(), UpdateDBIntentService.class);
                getActivity().startService(i.putExtras(args));
                args.putString("db", "scheduleronlyrecalculate");
                getActivity().startService(i.putExtras(args));
                return true;
            case CM_EDIT_ID:
                Log.d("myLogs", "edit "+acmi.id);
                args = new Bundle();
                args.putLong("id", acmi.id);
                DialogFragment editDialog = new EditDialog();
                editDialog.setArguments(args);
                editDialog.show(getFragmentManager(), "editDialog");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
        menu.add(0, CM_EDIT_ID, 1, R.string.edit);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, parent, false);
        DatabaseManager.initializeInstance(new DBHelper(getActivity()));
        warningText = (TextView) v.findViewById(R.id.warningTextView);
        warningText.setTextColor(Color.RED);
        myBalance(v);

        //<list view
        ListView transactionsListView = (ListView) v.findViewById(R.id.transactionsListView);
        String[] from = new String[]{"datetime", "paymentdetails", "card", "amount", "calculatedbalance"};
        int[] to = new int[]{R.id.lvDateTime, R.id.lvDetails, R.id.lvCard, R.id.lvAmount, R.id.lvBalance};
        scAdapter = new MySimpleCursorAdapter(getActivity(), R.layout.list_item, null, from, to, 0);
        transactionsListView.setAdapter(scAdapter);
        registerForContextMenu(transactionsListView);
        getLoaderManager().initLoader(0, null, this);

        transactionsListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });
        //list view>
        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), null, null, null, null, null) {
            @Override
            public Cursor loadInBackground() {
                // You better know how to get your database.
                SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
                // You can use any query that returns a cursor.
                return db.query("mytable", null, null, null, null, null, "datetime desc, _id desc");
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        scAdapter.swapCursor(arg1);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DatabaseManager.getInstance().closeDatabase();
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().getLoader(0).forceLoad();
//<warning
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!settings.getBoolean("start_service", true)) {
            warningText.setText("Перехват SMS от банка отключен");
        } else {
            warningText.setText("");
        }
//warning>
    }
}