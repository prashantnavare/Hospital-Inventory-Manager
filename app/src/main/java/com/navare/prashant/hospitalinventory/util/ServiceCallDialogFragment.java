package com.navare.prashant.hospitalinventory.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.navare.prashant.hospitalinventory.Database.Item;
import com.navare.prashant.hospitalinventory.Database.Task;
import com.navare.prashant.hospitalinventory.R;

/**
 * Created by prashant on 24-May-15.
 */
public class ServiceCallDialogFragment extends DialogFragment {

    private Context mContext = null;

    private Item mItem;
    private TextView mTextInstrument;
    private TextView mTextDescription;
    private Spinner mSpinnerPriority;
    private Button mBtnReport;
    private Button mBtnCancel;

    // The activity that creates an instance of this dialog fragment must
    // implement this interface in order to receive event callbacks.
    // Each method passes the DialogFragment in case the host needs to query it.
    public interface ServiceCallDialogListener {
        public void onServiceCallDialogReportClick(ServiceCallDialogFragment dialog);
        public void onServiceCallDialogCancelClick(ServiceCallDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ServiceCallDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the InventoryDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the ServiceCallDialogListener so we can send events to the host
            mListener = (ServiceCallDialogListener) activity;
            mContext = activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ServiceCallDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_service_call, container, false);

        mTextInstrument = ((TextView) rootView.findViewById(R.id.textInstrument));
        mTextInstrument.setText(mItem.mName);

        mTextDescription = ((TextView) rootView.findViewById(R.id.textDescription));

        mSpinnerPriority = (Spinner) rootView.findViewById(R.id.spinnerPriority);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, getResources().getStringArray(R.array.priorities_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPriority.setAdapter(adapter);
        mSpinnerPriority.setSelection(0, false);

        mBtnReport = ((Button) rootView.findViewById(R.id.btnReport));
        mBtnReport.setOnClickListener(onReport);
        mBtnCancel = ((Button) rootView.findViewById(R.id.btnCancel));
        mBtnCancel.setOnClickListener(onCancel);

        Dialog myDialog = getDialog();
        myDialog.setTitle(getResources().getText(R.string.dialog_service_call_title));
        return rootView;
    }

    View.OnClickListener onCancel=
            new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mListener.onServiceCallDialogCancelClick(ServiceCallDialogFragment.this);
                    dismiss();
                }
            };

    View.OnClickListener onReport=
            new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mListener.onServiceCallDialogReportClick(ServiceCallDialogFragment.this);
                    dismiss();
                }
            };

    public void setItem(Item item) {
        mItem = item;
    }

    public String getDescription() {
        return mTextDescription.getText().toString();
    }

    public long getItemID() {
        return mItem.mID;
    }

    public String getPriorityString() {
        if (mSpinnerPriority.getSelectedItemId() == 0)
            return getResources().getStringArray(R.array.priorities_array)[0];
        else if (mSpinnerPriority.getSelectedItemId() == 1)
            return getResources().getStringArray(R.array.priorities_array)[1];
        else return "Unknown";
    }

    public long getPriority() {
        if (mSpinnerPriority.getSelectedItemId() == 0)
            return Task.NormalPriority;
        else if (mSpinnerPriority.getSelectedItemId() == 1)
            return Task.UrgentPriority;
        else return 0;
    }
    public String getItemName() {
        return mItem.mName;
    }
}