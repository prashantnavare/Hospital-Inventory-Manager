package com.navare.prashant.hospitalinventory.util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.navare.prashant.hospitalinventory.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InventoryTaskDoneDialogFragment extends DialogFragment {

    private TextView mTextQuantityAdded;
    private TextView mTextCompletionComments;
    private Button mBtnTaskDone;
    private Button mBtnCancel;

    // The activity that creates an instance of this dialog fragment must
    // implement this interface in order to receive event callbacks.
    // Each method passes the DialogFragment in case the host needs to query it.
    public interface InventoryTaskDoneDialogListener {
        void onInventoryTaskDoneClick(InventoryTaskDoneDialogFragment dialog);
        void onInventoryTaskCancelClick(InventoryTaskDoneDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    InventoryTaskDoneDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the InventoryDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the InventoryDialogListener so we can send events to the host
            mListener = (InventoryTaskDoneDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement TaskDoneDialogListener");
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
        View rootView = inflater.inflate(R.layout.dialog_inventory_task_done, container, false);

        mTextQuantityAdded = (TextView) rootView.findViewById(R.id.textQuantityAdded);
        mTextQuantityAdded.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (mTextQuantityAdded.getText().toString().isEmpty())
                    mBtnTaskDone.setEnabled(false);
                else
                    mBtnTaskDone.setEnabled(true);
            }
        });
        mTextCompletionComments = ((TextView) rootView.findViewById(R.id.textCompletionComments));

        mBtnTaskDone = ((Button) rootView.findViewById(R.id.btnTaskDone));
        mBtnTaskDone.setOnClickListener(onTaskDone);
        // By default, disable the TaskDone button till the added quantity is set.
        mBtnTaskDone.setEnabled(false);

        mBtnCancel = ((Button) rootView.findViewById(R.id.btnCancel));
        mBtnCancel.setOnClickListener(onCancel);

        // Tweak the UI as per the type getResources().getText(R.string.main_title)
        Dialog myDialog = getDialog();
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return rootView;
    }

    View.OnClickListener onCancel=
            new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mListener.onInventoryTaskCancelClick(InventoryTaskDoneDialogFragment.this);
                    dismiss();
                }
            };

    View.OnClickListener onTaskDone=
            new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mListener.onInventoryTaskDoneClick(InventoryTaskDoneDialogFragment.this);
                    dismiss();
                }
            };

    public String getCompletionComments() {
        return mTextCompletionComments.getText().toString();
    }

    public long getAddedQuantity() {
        long addedQuantity = Long.valueOf(mTextQuantityAdded.getText().toString());
        return addedQuantity;
    }
}