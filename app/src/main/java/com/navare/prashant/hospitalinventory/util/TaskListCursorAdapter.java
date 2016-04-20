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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//extend the SimpleCursorAdapter to create a custom class where we
//can override the getView to change the row colors of the list
public class TaskListCursorAdapter extends SimpleCursorAdapter {

    public TaskListCursorAdapter(Context context, int layout, Cursor c,
                                 String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get reference to the row
        View view = super.getView(position, convertView, parent);

        // If the priority is Urgent, mark it red
        TextView textPriority = (TextView) view.findViewById(R.id.textPriority);
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        String priority = cursor.getString(cursor.getColumnIndex(Task.COL_FTS_TASK_PRIORITY));
        if (priority.equalsIgnoreCase("Urgent")) {
            textPriority.setTextColor(Color.RED);
        }

        // Show it as overdue if needed
        Calendar todayDate = Calendar.getInstance();
        Calendar taskDueDate = Calendar.getInstance();
        String taskDueDateString = cursor.getString(cursor.getColumnIndex(Task.COL_FTS_DUE_DATE));
        if (taskDueDateString != null && (taskDueDateString.isEmpty() == false)) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM, yyyy");
            try {
                taskDueDate.setTime(dateFormatter.parse(taskDueDateString));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (taskDueDate.getTimeInMillis() < todayDate.getTimeInMillis()) {
                TextView textDueDate = (TextView) view.findViewById(R.id.textDueDate);
                textDueDate.setText(taskDueDateString + " (Overdue)");
                textDueDate.setTextColor(Color.RED);
            }
        }
        return view;
    }
}

