package ru.zzsdeo.mymoneybalance;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.format.DateFormat;

public class CursorPagerAdapter<F extends Fragment> extends FragmentStatePagerAdapter {
    private final Class<F> fragmentClass;
    private final String[] projection;
    private Cursor cursor;

    public CursorPagerAdapter(FragmentManager fm, Class<F> fragmentClass, String[] projection, Cursor cursor) {
        super(fm);
        this.fragmentClass = fragmentClass;
        this.projection = projection;
        this.cursor = cursor;
    }

    @Override
    public F getItem(int position) {
        if (cursor == null) // shouldn't happen
            return null;

        cursor.moveToPosition(position);
        F frag;
        try {
            frag = fragmentClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        Bundle args = new Bundle();
        for (int i = 0; i < projection.length; ++i) {
            args.putString(projection[i], cursor.getString(i));
        }
        frag.setArguments(args);
        return frag;
    }

    @Override
    public int getCount() {
        if (cursor == null)
            return 0;
        else
            return cursor.getCount();
    }

    public void swapCursor(Cursor c) {
        if (cursor == c)
            return;

        this.cursor = c;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        cursor.moveToPosition(position);
        CharSequence date = DateFormat.format("dd.MM.yyyy, HH:mm", cursor.getLong(cursor.getColumnIndex("datetime")));
        return date;
    }
}