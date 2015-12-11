package com.navare.prashant.hospitalinventory.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.navare.prashant.hospitalinventory.Database.Task;
import com.navare.prashant.hospitalinventory.R;

//extend the SimpleCursorAdapter to create a custom class where we
//can override the getView to change the row colors of the list
public class ReportDetailCursorAdapter extends SimpleCursorAdapter {

    public ReportDetailCursorAdapter(Context context, int layout, Cursor c,
                                     String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get reference to the row
        View view = super.getView(position, convertView, parent);
        //check for odd or even to set alternate colors to the row background
        if(position % 2 == 0){
            view.setBackgroundColor(Color.rgb(225, 225, 225));
        }
        else {
            view.setBackgroundColor(Color.rgb(245, 245, 245));
        }

        return view;
    }
}

