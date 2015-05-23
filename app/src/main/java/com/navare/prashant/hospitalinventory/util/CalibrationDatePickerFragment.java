package com.navare.prashant.hospitalinventory.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by prashant on 21-May-15.
 */
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CalibrationDatePickerFragment extends DialogFragment {

    OnDateSetListener mOnDateSetCallback;
    private int mYear, mMonth, mDay;

    public CalibrationDatePickerFragment() {
    }

    public void setCallBack(OnDateSetListener ondate) {
        mOnDateSetCallback = ondate;
    }


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mYear = args.getInt("year");
        mMonth = args.getInt("month");
        mDay = args.getInt("day");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), mOnDateSetCallback, mYear, mMonth, mDay);
    }
}