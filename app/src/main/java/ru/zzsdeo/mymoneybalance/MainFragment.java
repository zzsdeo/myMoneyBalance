package ru.zzsdeo.mymoneybalance;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainFragment extends Fragment implements LoaderCallbacks<Cursor> {

//<vars
    private TextView warningText;
    private MySimpleCursorAdapter scAdapter;
    private static final int CM_DELETE_ID = 1;
    private static final int CM_EDIT_ID = 2;
    private String[] cardArrayFilter = {"Все", "Наличные", "Зарплатная", "Кредитная"};
    private Spinner cardFilter;
    SharedPreferences preferences;
    ActionBar bar;
    View balanceLayout;
//vars>

//<functions
    static void myBalance (View v) {
        TextView cardInfo = (TextView) v.findViewById(R.id.cardInfo);
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor c = db.query("mytable", null, "card = 'Cash'", null, null, null, "datetime desc, _id desc");
        if (c.moveToFirst()) {
            cardInfo.setText(Double.toString(c.getDouble(c.getColumnIndex("calculatedbalance"))) + "\n");
        }
        c = db.query("mytable", null, "card = 'Debit'", null, null, null, "datetime desc, _id desc");
        if (c.moveToFirst()) {
            cardInfo.append(Double.toString(c.getDouble(c.getColumnIndex("calculatedbalance"))) + "\n");
        }
        c = db.query("mytable", null, "card = 'Credit'", null, null, null, "datetime desc, _id desc");
        if (c.moveToFirst()) {
            cardInfo.append(Double.toString(c.getDouble(c.getColumnIndex("calculatedbalance"))));
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
            case R.id.search_item:
                bar = getActivity().getActionBar();
                assert bar != null;
                bar.setDisplayShowCustomEnabled(true);
                bar.setCustomView(R.layout.search);
                item.setVisible(false);
                Animation show = AnimationUtils.loadAnimation(getActivity(), R.anim.search_anim_show);
                View searchLayout = bar.getCustomView().findViewById(R.id.searchLayout);
                searchLayout.startAnimation(show);
                balanceLayout.setVisibility(View.GONE);

                //<search
                final EditText searchText = (EditText) bar.getCustomView().findViewById(R.id.searchText);
                searchText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
                searchText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                        String s = charSequence.toString().toLowerCase();
                        scAdapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                //search>


                ImageButton clearButton = (ImageButton) bar.getCustomView().findViewById(R.id.clearButton);
                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation hide = AnimationUtils.loadAnimation(getActivity(), R.anim.search_anim_hide);
                        View searchLayout = bar.getCustomView().findViewById(R.id.searchLayout);
                        hide.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                searchText.clearFocus();
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                                balanceLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                bar.setDisplayShowCustomEnabled(false);
                                getLoaderManager().getLoader(0).forceLoad();
                                getActivity().invalidateOptionsMenu();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        searchLayout.startAnimation(hide);
                    }
                });
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
        balanceLayout = v.findViewById(R.id.balanceLayout);
        DatabaseManager.initializeInstance(new DBHelper(getActivity()));
        warningText = (TextView) v.findViewById(R.id.warningTextView);
        warningText.setTextColor(Color.RED);
        myBalance(v);


        //<card filter
        ArrayAdapter<String> cardAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cardArrayFilter);
        cardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardFilter = (Spinner) v.findViewById(R.id.cardFilter);
        cardFilter.setAdapter(cardAdapter);
        cardFilter.setPrompt("Фильтр");
        preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        cardFilter.setSelection(preferences.getInt("filter_position_history", 0));
        cardFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                SharedPreferences.Editor editor = preferences.edit();
                if (getActivity().getCurrentFocus() != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
                bar = getActivity().getActionBar();
                assert bar != null;
                bar.setDisplayShowCustomEnabled(false);
                getActivity().invalidateOptionsMenu();
                balanceLayout.setVisibility(View.VISIBLE);
                switch (position) {
                    case 0:
                        getLoaderManager().getLoader(0).forceLoad();
                        editor.putInt("filter_position_history", 0);
                        editor.apply();
                        break;
                    case 1:
                        getLoaderManager().getLoader(0).forceLoad();
                        editor.putInt("filter_position_history", 1);
                        editor.apply();
                        break;
                    case 2:
                        getLoaderManager().getLoader(0).forceLoad();
                        editor.putInt("filter_position_history", 2);
                        editor.apply();
                        break;
                    case 3:
                        getLoaderManager().getLoader(0).forceLoad();
                        editor.putInt("filter_position_history", 3);
                        editor.apply();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //card filter>

        //<list view
        ListView transactionsListView = (ListView) v.findViewById(R.id.transactionsListView);
        String[] from = new String[]{"datetime", "paymentdetails", "card", "amount", "calculatedbalance"};
        int[] to = new int[]{R.id.lvDateTime, R.id.lvDetails, R.id.lvCard, R.id.lvAmount, R.id.lvBalance};
        scAdapter = new MySimpleCursorAdapter(getActivity(), R.layout.list_item, null, from, to, 0);
        scAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence charSequence) {
                SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
                switch (cardFilter.getSelectedItemPosition()) {
                    case 0:
                        return db.query("mytable", null, "searchindex like " + '"' + "%" + charSequence + "%" + '"', null, null, null, "datetime desc, _id desc");
                    case 1:
                        return db.query("mytable", null, "card = 'Cash' and searchindex like " + '"' + "%" + charSequence + "%" + '"', null, null, null, "datetime desc, _id desc");
                    case 2:
                        return db.query("mytable", null, "card = 'Debit' and searchindex like " + '"' + "%" + charSequence + "%" + '"', null, null, null, "datetime desc, _id desc");
                    case 3:
                        return db.query("mytable", null, "card = 'Credit' and searchindex like " + '"' + "%" + charSequence + "%" + '"', null, null, null, "datetime desc, _id desc");
                    default:
                        return db.query("mytable", null, "searchindex like " + '"' + "%" + charSequence + "%" + '"', null, null, null, "datetime desc, _id desc");
                }
            }
        });
        transactionsListView.setAdapter(scAdapter);
        registerForContextMenu(transactionsListView);
        getLoaderManager().initLoader(0, null, this);

        transactionsListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("position", i);
                intent.putExtra("filter", cardFilter.getSelectedItemPosition());
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
                switch (cardFilter.getSelectedItemPosition()) {
                    case 0:
                        return db.query("mytable", null, null, null, null, null, "datetime desc, _id desc");
                    case 1:
                        return db.query("mytable", null, "card = 'Cash'", null, null, null, "datetime desc, _id desc");
                    case 2:
                        return db.query("mytable", null, "card = 'Debit'", null, null, null, "datetime desc, _id desc");
                    case 3:
                        return db.query("mytable", null, "card = 'Credit'", null, null, null, "datetime desc, _id desc");
                    default:
                        return db.query("mytable", null, null, null, null, null, "datetime desc, _id desc");
                }
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
        myBalance(this.getView());
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