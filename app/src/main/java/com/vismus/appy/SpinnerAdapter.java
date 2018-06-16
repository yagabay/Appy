package com.vismus.appy;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Arrays;

public class SpinnerAdapter extends ArrayAdapter<CharSequence> {

    boolean[] _itemEnabled;

    public SpinnerAdapter(Context context, int resource, CharSequence[] objects) {
        super(context, resource, objects);
        _itemEnabled = new boolean[objects.length];
        Arrays.fill(_itemEnabled, true);
    }

    @Override
    public boolean isEnabled(int position) {
        return _itemEnabled[position];
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txvItem = (TextView) super.getDropDownView(position, convertView, parent);
        if (isEnabled(position)) {
            txvItem.setTextColor(Color.BLACK);
        } else {
            txvItem.setTextColor(Color.GRAY);
        }
        return txvItem;
    }

    public void setItemEnabled(int position, boolean enabled){
        _itemEnabled[position] = enabled;
    }
}
