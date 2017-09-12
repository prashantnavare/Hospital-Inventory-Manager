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

        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        //get reference to the row
        View view = super.getView(position, convertView, parent);

        String assignedTo = cursor.getString(cursor.getColumnIndex(Task.COMPLETED_COL_FTS_ASSIGNED_TO));
        if (assignedTo == null || assignedTo.isEmpty()) {
            TextView textAssignedTo = (TextView) view.findViewById(R.id.textAssignedTo);
            textAssignedTo.setText("Unassigned");
        }

        // If the priority is Urgent, mark it red
        TextView textPriority = (TextView) view.findViewById(R.id.textPriority);
        String priority = cursor.getString(cursor.getColumnIndex(Task.COMPLETED_COL_FTS_TASK_PRIORITY));
        if (priority.equalsIgnoreCase("Urgent")) {
            textPriority.setTextColor(Color.RED);
        }
        else {
            textPriority.setTextColor(Color.BLUE);
        }

        String comments = cursor.getString(cursor.getColumnIndex(Task.COMPLETED_COL_FTS_COMPLETION_COMMENTS));
        if (comments == null || comments.isEmpty()) {
            TextView textComments = (TextView) view.findViewById(R.id.textComments);
            textComments.setText("None");
        }

        return view;
    }
}

