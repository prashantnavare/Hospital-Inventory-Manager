package com.navare.prashant.hospitalinventory.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.navare.prashant.hospitalinventory.R;

/**
 * Created by prashant on 24-May-15.
 */
public class InventoryDialogFragment extends DialogFragment {

    public enum InventoryDialogType {ADD, SUBTRACT};

    InventoryDialogType mType;
    private TextView mTextMessage;
    private TextView mTextQuantity;
    private Button mBtnYes;
    private Button mBtnCancel;

    // The activity that creates an instance of this dialog fragment must
    // implement this interface in order to receive event callbacks.
    // Each method passes the DialogFragment in case the host needs to query it.
    public interface InventoryDialogListener {
        public void onInventoryDialogPositiveClick(InventoryDialogFragment dialog);
        public void onInventoryDialogNegativeClick(InventoryDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    InventoryDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the InventoryDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the InventoryDialogListener so we can send events to the host
            mListener = (InventoryDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement InventoryDialogListener");
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
        View rootView = inflater.inflate(R.layout.dialog_inventory_add_subtract, container, false);
        mTextMessage = ((TextView) rootView.findViewById(R.id.textMessage));
        mTextQuantity = ((TextView) rootView.findViewById(R.id.textQuantity));
        mTextQuantity.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (mTextQuantity.getText().toString().isEmpty())
                    mBtnYes.setEnabled(false);
                else
                    mBtnYes.setEnabled(true);
            }
        });
        mBtnYes = ((Button) rootView.findViewById(R.id.btnYes));
        mBtnYes.setOnClickListener(onYes);
        // By default, disable the Yes button till Quamtity is non empty.
        mBtnYes.setEnabled(false);

        mBtnCancel = ((Button) rootView.findViewById(R.id.btnCancel));
        mBtnCancel.setOnClickListener(onCancel);

        // Tweak the UI as per the type getResources().getText(R.string.main_title)
        Dialog myDialog = getDialog();
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mType == InventoryDialogType.ADD) {
            mTextMessage.setText(getResources().getText(R.string.add_quantity));
            mBtnYes.setText(getResources().getText(R.string.add));
        }
        else if (mType == InventoryDialogType.SUBTRACT) {
            mTextMessage.setText(getResources().getText(R.string.subtract_quantity));
            mBtnYes.setText(getResources().getText(R.string.subtract));
        }
        return rootView;
    }

    View.OnClickListener onCancel=
            new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mListener.onInventoryDialogNegativeClick(InventoryDialogFragment.this);
                    dismiss();
                }
            };

    View.OnClickListener onYes=
            new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    mListener.onInventoryDialogPositiveClick(InventoryDialogFragment.this);
                    dismiss();
                }
            };

    public InventoryDialogType getDialogType() {
        return mType;
    }
    public String getQuantity() {
        return mTextQuantity.getText().toString();
    }
    public  void setDialogType(InventoryDialogType type) {
        mType = type;
    }
}