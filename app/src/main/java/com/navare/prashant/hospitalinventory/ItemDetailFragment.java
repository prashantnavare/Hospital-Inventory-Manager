package com.navare.prashant.hospitalinventory;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.app.DatePickerDialog;

import com.navare.prashant.hospitalinventory.Database.HospitalInventoryContentProvider;
import com.navare.prashant.hospitalinventory.Database.Item;
import com.navare.prashant.hospitalinventory.Database.ServiceCall;
import com.navare.prashant.hospitalinventory.util.CalibrationDatePickerFragment;
import com.navare.prashant.hospitalinventory.util.InventoryDialogFragment;
import com.navare.prashant.hospitalinventory.util.ServiceCallDialogFragment;

import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher {

    public static final int LOADER_ID_ITEM_DETAILS = 2;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private Context mContext = null;
    private int mSpinnerPosition = -1;

    /**
     * The item this fragment is presenting.
     */
    private String mItemID;
    private Item mItem = null;

    String [] mItemTypes = {
            "Instrument",
            "Consummable"
    };

    // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * The UI elements showing the details of the item
     */
    private TextView mTextName;
    private TextView mTextDescription;
    private Spinner mSpinnerType;

    private LinearLayout mLayoutInstrument;

    private CheckBox mCalibrationCheckBox;
    private LinearLayout mLayoutCalibration;
    private TextView mTextCalibrationFrequency;
    private Button mBtnChangeCalibrationDate;
    private TextView mTextCalibrationInstructions;

    private CheckBox mMaintenanceCheckBox;
    private LinearLayout mLayoutMaintenance;
    private TextView mTextMaintenanceFrequency;
    private Button mBtnChangeMaintenanceDate;
    private TextView mTextMaintenanceInstructions;

    private CheckBox mContractCheckBox;
    private LinearLayout mLayoutContract;
    private TextView mTextContractFrequency;
    private Button mBtnChangeContractDate;
    private TextView mTextContractInstructions;

    private LinearLayout mLayoutConsummable;
    private CheckBox mInventoryCheckBox;
    private LinearLayout mLayoutInventory;
    private TextView mTextMinRequiredQuantity;
    private TextView mTextCurrentQuantity;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void EnableDeleteButton(boolean bEnable);
        public void EnableRevertButton(boolean bEnable);
        public void EnableSaveButton(boolean bEnable);
        public void EnableInventoryAddButton(boolean bEnable);
        public void EnableInventorySubtractButton(boolean bEnable);
        public void RedrawOptionsMenu();
        public void EnableServiceCallButton(boolean bEnable);
        public void onItemDeleted();
    }
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void EnableDeleteButton(boolean bEnable) {
        }
        @Override
        public void EnableRevertButton(boolean bEnable) {
        }
        @Override
        public void EnableSaveButton(boolean bEnable) {
        }
        @Override
        public void EnableInventoryAddButton(boolean bEnable) {
        }
        @Override
        public void EnableInventorySubtractButton(boolean bEnable) {
        }
        @Override
        public void EnableServiceCallButton(boolean bEnable) {
        }
        @Override
        public void RedrawOptionsMenu() {
        }
        @Override
        public void onItemDeleted() {
        }
    };

    /**
     * The fragment's current callback object, which is notified of changes to the item
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            mItemID = getArguments().getString(ARG_ITEM_ID);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
        mContext = (Context)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }


    // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        mTextName = ((TextView) rootView.findViewById(R.id.textName));
        mTextName.addTextChangedListener(this);

        mTextDescription = ((TextView) rootView.findViewById(R.id.textDescription));
        mTextDescription.addTextChangedListener(this);

        mSpinnerType = (Spinner) rootView.findViewById(R.id.spinnerType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mItemTypes);
        mSpinnerType.setAdapter(adapter);
        mSpinnerPosition = 0;
        mSpinnerType.setSelection(0, false);
        mSpinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                if (position != mSpinnerPosition) {
                    // If it is an instrument, show the instrument layout
                    mSpinnerPosition = position;
                    if (position == 0) {
                        mLayoutInstrument.setVisibility(View.VISIBLE);
                        mLayoutConsummable.setVisibility(View.GONE);
                    }
                    else {
                        mLayoutInstrument.setVisibility(View.GONE);
                        mLayoutConsummable.setVisibility(View.VISIBLE);
                    }
                    mCallbacks.EnableRevertButton(true);
                    mCallbacks.EnableSaveButton(true);
                    mCallbacks.RedrawOptionsMenu();
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Instrument related
        mLayoutInstrument = (LinearLayout) rootView.findViewById(R.id.layoutInstrument);

        // Calibration
        mCalibrationCheckBox = (CheckBox) rootView.findViewById(R.id.chkCalibration);
        mCalibrationCheckBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    mLayoutCalibration.setVisibility(View.VISIBLE);
                }
                else {
                    mLayoutCalibration.setVisibility(View.GONE);
                }
                enableRevertAndSaveButtons();
            }
        });
        mLayoutCalibration = (LinearLayout) rootView.findViewById(R.id.layoutCalibration);

        mTextCalibrationFrequency = (TextView) rootView.findViewById(R.id.textCalibrationFrequency);
        mTextCalibrationFrequency.addTextChangedListener(this);

        mBtnChangeCalibrationDate = (Button) rootView.findViewById(R.id.btnChangeCalibrationDate);
        mBtnChangeCalibrationDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePicker(DatePickerType.CALIBRATION);
            }
        });

        mTextCalibrationInstructions = (TextView) rootView.findViewById(R.id.textCalibrationInstructions);
        mTextCalibrationInstructions.addTextChangedListener(this);

        // Maintenance
        mMaintenanceCheckBox = (CheckBox) rootView.findViewById(R.id.chkMaintenance);
        mMaintenanceCheckBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    mLayoutMaintenance.setVisibility(View.VISIBLE);
                }
                else {
                    mLayoutMaintenance.setVisibility(View.GONE);
                }
                enableRevertAndSaveButtons();
            }
        });
        mLayoutMaintenance = (LinearLayout) rootView.findViewById(R.id.layoutMaintenance);

        mTextMaintenanceFrequency = (TextView) rootView.findViewById(R.id.textMaintenanceFrequency);
        mTextMaintenanceFrequency.addTextChangedListener(this);

        mBtnChangeMaintenanceDate = (Button) rootView.findViewById(R.id.btnChangeMaintenanceDate);
        mBtnChangeMaintenanceDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePicker(DatePickerType.MAINTENANCE);
            }
        });

        mTextMaintenanceInstructions = (TextView) rootView.findViewById(R.id.textMaintenanceInstructions);
        mTextMaintenanceInstructions.addTextChangedListener(this);

        // Contract
        mContractCheckBox = (CheckBox) rootView.findViewById(R.id.chkContract);
        mContractCheckBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    mLayoutContract.setVisibility(View.VISIBLE);
                }
                else {
                    mLayoutContract.setVisibility(View.GONE);
                }
                enableRevertAndSaveButtons();
            }
        });
        mLayoutContract = (LinearLayout) rootView.findViewById(R.id.layoutContract);

        mTextContractFrequency = (TextView) rootView.findViewById(R.id.textContractFrequency);
        mTextContractFrequency.addTextChangedListener(this);

        mBtnChangeContractDate = (Button) rootView.findViewById(R.id.btnChangeContractDate);
        mBtnChangeContractDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDatePicker(DatePickerType.CONTRACT);
            }
        });

        mTextContractInstructions = (TextView) rootView.findViewById(R.id.textContractInstructions);
        mTextContractInstructions.addTextChangedListener(this);

        // Consummable related
        mLayoutConsummable = (LinearLayout) rootView.findViewById(R.id.layoutConsummable);

        // Inventory related
        mLayoutInventory = (LinearLayout) rootView.findViewById(R.id.layoutInventory);

        mTextCurrentQuantity = (TextView) rootView.findViewById(R.id.textCurrentQuantity);
        mTextCurrentQuantity.addTextChangedListener(this);

        mInventoryCheckBox = (CheckBox) rootView.findViewById(R.id.chkInventory);
        mInventoryCheckBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    mLayoutInventory.setVisibility(View.VISIBLE);
                } else {
                    mLayoutInventory.setVisibility(View.GONE);
                }
                enableRevertAndSaveButtons();
            }
        });

        mTextMinRequiredQuantity = (TextView) rootView.findViewById(R.id.textMinRequiredQuantity);
        mTextMinRequiredQuantity.addTextChangedListener(this);

        return rootView;
    }

    enum DatePickerType {CALIBRATION, MAINTENANCE, CONTRACT};

    private void showDatePicker(final DatePickerType pickerType) {
        Calendar dateToShow = Calendar.getInstance();
        if (mItem != null) {
            switch (pickerType) {
                case CALIBRATION:
                    if (mItem.mCalibrationDate > 0) {
                        dateToShow.setTimeInMillis(mItem.mCalibrationDate);
                    }
                    break;
                case MAINTENANCE:
                    if (mItem.mMaintenanceDate > 0) {
                        dateToShow.setTimeInMillis(mItem.mMaintenanceDate);
                    }
                    break;
                case CONTRACT:
                    break;
            }
        }
        CalibrationDatePickerFragment datePicker = new CalibrationDatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("year", dateToShow.get(Calendar.YEAR));
        args.putInt("month", dateToShow.get(Calendar.MONTH));
        args.putInt("day", dateToShow.get(Calendar.DAY_OF_MONTH));
        datePicker.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        OnDateSetListener onDateChangeCallback = new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                switch (pickerType) {
                    case CALIBRATION:
                        mItem.mCalibrationDate = newDate.getTimeInMillis();
                        mBtnChangeCalibrationDate.setText(dateFormatter.format(newDate.getTime()));
                        break;
                    case MAINTENANCE:
                        mItem.mMaintenanceDate = newDate.getTimeInMillis();
                        mBtnChangeMaintenanceDate.setText(dateFormatter.format(newDate.getTime()));
                        break;
                    case CONTRACT:
                        break;
                }
                enableRevertAndSaveButtons();
            }
        };
        datePicker.setCallBack(onDateChangeCallback);
        datePicker.show(((FragmentActivity)mContext).getSupportFragmentManager(), "Instrument Date Picker");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if ((mItemID != null) && (mItemID.isEmpty() == false)) {
            getLoaderManager().initLoader(LOADER_ID_ITEM_DETAILS, null, this);
        }
        else {
            displayUIForNewItem();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == LOADER_ID_ITEM_DETAILS) {
            Uri itemURI = Uri.withAppendedPath(HospitalInventoryContentProvider.ITEM_URI,
                    mItemID);

            return new CursorLoader(getActivity(),
                    itemURI, Item.FIELDS, null, null,
                    null);
        }
        else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {

        if (dataCursor != null) {
            int loaderID = loader.getId();
            if (loaderID == LOADER_ID_ITEM_DETAILS) {
                if (mItem == null)
                    mItem = new Item();

                mItem.setContentFromCursor(dataCursor);
                updateUIFromItem();

                // Toggle the action bar buttons appropriately
                mCallbacks.EnableDeleteButton(true);
                mCallbacks.EnableRevertButton(false);
                mCallbacks.EnableSaveButton(false);
                mCallbacks.RedrawOptionsMenu();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        enableRevertAndSaveButtons();
    }

    private void enableRevertAndSaveButtons() {
        mCallbacks.EnableRevertButton(true);
        mCallbacks.EnableSaveButton(true);
        mCallbacks.RedrawOptionsMenu();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void revertUI() {
        if (mItem == null) {
            displayUIForNewItem();
        }
        else {
            updateUIFromItem();
        }
        mCallbacks.EnableRevertButton(false);
        mCallbacks.EnableSaveButton(false);
        mCallbacks.RedrawOptionsMenu();
    }

    public void deleteItem() {
        Uri itemURI = Uri.withAppendedPath(HospitalInventoryContentProvider.ITEM_URI,
                mItemID);
        int result = getActivity().getContentResolver().delete(itemURI, null, null);
        if (result > 0)
            mCallbacks.onItemDeleted();
    }

    public void saveItem() {
        updateItemFromUI();
        boolean bSuccess = false;
        if ((mItemID == null) || (mItemID.isEmpty())) {
            // a new item is being inserted.
            Uri uri = getActivity().getContentResolver().insert(HospitalInventoryContentProvider.ITEM_URI, mItem.getContentValues());
            if (uri != null) {
                mItemID = uri.getLastPathSegment();
                bSuccess = true;
            }
        }
        else {
            Uri itemURI = Uri.withAppendedPath(HospitalInventoryContentProvider.ITEM_URI,
                    mItemID);
            int result = getActivity().getContentResolver().update(itemURI, mItem.getContentValues(), null, null);
            if (result > 0)
                bSuccess = true;
        }
        if (bSuccess) {
            mCallbacks.EnableSaveButton(false);
            mCallbacks.EnableRevertButton(false);
            mCallbacks.RedrawOptionsMenu();
        }

    }

    // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void updateItemFromUI() {
        if (mItem == null)
            mItem = new Item();

        mItem.mName = mTextName.getText().toString();
        mItem.mDescription = mTextDescription.getText().toString();
        if (mSpinnerType.getSelectedItemId() == 0) {
            // Instrument
            mItem.mType = Item.InstrumentType;

            // Calibration related
            if (mCalibrationCheckBox.isChecked()) {
                mItem.mCalibrationReminders = 1;
                if (mTextCalibrationFrequency.getText().toString().isEmpty() == false)
                    mItem.mCalibrationFrequency = Long.valueOf(mTextCalibrationFrequency.getText().toString());

                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                Calendar calibrationDate = Calendar.getInstance();
                String uiCalibrationDate = mBtnChangeCalibrationDate.getText().toString();
                if (uiCalibrationDate.compareToIgnoreCase("Set") != 0) {
                    try {
                        calibrationDate.setTime(dateFormatter.parse(uiCalibrationDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mItem.mCalibrationDate = calibrationDate.getTimeInMillis();
                }

                mItem.mCalibrationInstructions = mTextCalibrationInstructions.getText().toString();
            }
            else {
                mItem.mCalibrationReminders = 0;
            }

            // Maintenance related
            if (mMaintenanceCheckBox.isChecked()) {
                mItem.mMaintenanceReminders = 1;
                if (mTextMaintenanceFrequency.getText().toString().isEmpty() == false)
                    mItem.mMaintenanceFrequency = Long.valueOf(mTextMaintenanceFrequency.getText().toString());

                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                Calendar maintenanceDate = Calendar.getInstance();
                String uiMaintenanceDate = mBtnChangeMaintenanceDate.getText().toString();
                if (uiMaintenanceDate.compareToIgnoreCase("Set") != 0) {
                    try {
                        maintenanceDate.setTime(dateFormatter.parse(uiMaintenanceDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mItem.mMaintenanceDate = maintenanceDate.getTimeInMillis();
                }

                mItem.mMaintenanceInstructions = mTextMaintenanceInstructions.getText().toString();
            }
            else {
                mItem.mMaintenanceReminders = 0;
            }

            // Contract related
            if (mContractCheckBox.isChecked()) {
                mItem.mContractReminders = 1;
                if (mTextContractFrequency.getText().toString().isEmpty() == false)
                    mItem.mContractFrequency = Long.valueOf(mTextContractFrequency.getText().toString());

                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                Calendar contractDate = Calendar.getInstance();
                String uiContractDate = mBtnChangeContractDate.getText().toString();
                if (uiContractDate.compareToIgnoreCase("Set") != 0) {
                    try {
                        contractDate.setTime(dateFormatter.parse(uiContractDate));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mItem.mContractDate = contractDate.getTimeInMillis();
                }

                mItem.mContractInstructions = mTextContractInstructions.getText().toString();
            }
            else {
                mItem.mContractReminders = 0;
            }
        }
        else {
            // Consummable
            mItem.mType = Item.ConsummableType;
            // Inventory related
            if (mTextCurrentQuantity.getText().toString().isEmpty() == false)
                mItem.mCurrentQuantity = Long.valueOf(mTextCurrentQuantity.getText().toString());
            if (mInventoryCheckBox.isChecked()) {
                mItem.mInventoryReminders = 1;
                if (mTextMinRequiredQuantity.getText().toString().isEmpty() == false)
                    mItem.mMinRequiredQuantity = Long.valueOf(mTextMinRequiredQuantity.getText().toString());
            }
            else {
                mItem.mInventoryReminders = 0;
            }
        }
    }

    // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void updateUIFromItem() {
        mTextName.setText(mItem.mName);
        mTextDescription.setText(mItem.mDescription);
        if (mItem.mType == Item.InstrumentType) {

            mSpinnerPosition = 0;
            mSpinnerType.setSelection(0, false);

            // Enable the instrument layout
            mLayoutInstrument.setVisibility(View.VISIBLE);
            mLayoutConsummable.setVisibility(View.GONE);

            // Turn on the Instrument action bar menu items
            mCallbacks.EnableServiceCallButton(true);

            // Turn off the Consummable action bar menu items
            mCallbacks.EnableInventoryAddButton(false);
            mCallbacks.EnableInventorySubtractButton(false);

            // Set the calibration UI elements
            if (mItem.mCalibrationReminders > 0) {
                mCalibrationCheckBox.setChecked(true);
                mLayoutCalibration.setVisibility(View.VISIBLE);
                if (mItem.mCalibrationFrequency > 0)
                    mTextCalibrationFrequency.setText(String.valueOf(mItem.mCalibrationFrequency));
                if (mItem.mCalibrationDate > 0) {
                    Calendar calibrationDate = Calendar.getInstance();
                    calibrationDate.setTimeInMillis(mItem.mCalibrationDate);
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                    mBtnChangeCalibrationDate.setText(dateFormatter.format(calibrationDate.getTime()));
                }
                else {
                    mBtnChangeCalibrationDate.setText("Set");
                }
                mTextCalibrationInstructions.setText(mItem.mCalibrationInstructions);
            }
            else {
                mCalibrationCheckBox.setChecked(false);
                mLayoutCalibration.setVisibility(View.GONE);
            }

            // Set the maintenance UI elements
            if (mItem.mMaintenanceReminders > 0) {
                mMaintenanceCheckBox.setChecked(true);
                mLayoutMaintenance.setVisibility(View.VISIBLE);
                if (mItem.mMaintenanceFrequency > 0)
                    mTextMaintenanceFrequency.setText(String.valueOf(mItem.mMaintenanceFrequency));
                if (mItem.mMaintenanceDate > 0) {
                    Calendar maintenanceDate = Calendar.getInstance();
                    maintenanceDate.setTimeInMillis(mItem.mMaintenanceDate);
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                    mBtnChangeMaintenanceDate.setText(dateFormatter.format(maintenanceDate.getTime()));
                }
                else {
                    mBtnChangeMaintenanceDate.setText("Set");
                }
                mTextMaintenanceInstructions.setText(mItem.mMaintenanceInstructions);
            }
            else {
                mMaintenanceCheckBox.setChecked(false);
                mLayoutMaintenance.setVisibility(View.GONE);
            }

            // Set the contract UI elements
            if (mItem.mContractReminders > 0) {
                mContractCheckBox.setChecked(true);
                mLayoutContract.setVisibility(View.VISIBLE);
                if (mItem.mContractFrequency > 0)
                    mTextContractFrequency.setText(String.valueOf(mItem.mContractFrequency));
                if (mItem.mContractDate > 0) {
                    Calendar contractDate = Calendar.getInstance();
                    contractDate.setTimeInMillis(mItem.mMaintenanceDate);
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                    mBtnChangeContractDate.setText(dateFormatter.format(contractDate.getTime()));
                }
                else {
                    mBtnChangeContractDate.setText("Set");
                }
                mTextContractInstructions.setText(mItem.mContractInstructions);
            }
            else {
                mContractCheckBox.setChecked(false);
                mLayoutContract.setVisibility(View.GONE);
            }
        }
        else if (mItem.mType == Item.ConsummableType) {

            mSpinnerPosition = 1;
            mSpinnerType.setSelection(1, false);
            // Turn off the Instrument specific views
            mLayoutInstrument.setVisibility(View.GONE);
            mLayoutConsummable.setVisibility(View.VISIBLE);

            // Turn off the Instrument action bar menu items
            mCallbacks.EnableServiceCallButton(false);

            // Turn on the Consummable action bar menu items
            mCallbacks.EnableInventoryAddButton(true);
            mCallbacks.EnableInventorySubtractButton(true);

            if (mItem.mCurrentQuantity > 0)
                mTextCurrentQuantity.setText(String.valueOf(mItem.mCurrentQuantity));

            // Set the Inventory UI elements
            if (mItem.mInventoryReminders > 0) {
                mInventoryCheckBox.setChecked(true);
                mLayoutInventory.setVisibility(View.VISIBLE);
                if (mItem.mMinRequiredQuantity > 0)
                    mTextMinRequiredQuantity.setText(String.valueOf(mItem.mMinRequiredQuantity));
            }
            else {
                mInventoryCheckBox.setChecked(false);
                mLayoutInventory.setVisibility(View.GONE);
            }
        }
    }

    // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void displayUIForNewItem() {
        mTextName.setText("");
        mTextDescription.setText("");
        mSpinnerPosition = 0;
        mSpinnerType.setSelection(0, false);
        mLayoutInstrument.setVisibility(View.VISIBLE);

        mCalibrationCheckBox.setChecked(false);
        mLayoutCalibration.setVisibility(View.GONE);

        mMaintenanceCheckBox.setChecked(false);
        mLayoutMaintenance.setVisibility(View.GONE);

        mContractCheckBox.setChecked(false);
        mLayoutContract.setVisibility(View.GONE);

        mLayoutConsummable.setVisibility(View.GONE);
    }

    public void showInventoryAddDialog() {
        InventoryDialogFragment dialog = new InventoryDialogFragment();
        dialog.setDialogType(InventoryDialogFragment.InventoryDialogType.ADD);
        dialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "InventoryDialogFragment");
    }

    public void showInventorySubtractDialog() {
        InventoryDialogFragment dialog = new InventoryDialogFragment();
        dialog.setDialogType(InventoryDialogFragment.InventoryDialogType.SUBTRACT);
        dialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "InventoryDialogFragment");
    }

    public void showServiceCallDialog() {
        ServiceCallDialogFragment dialog = new ServiceCallDialogFragment();
        dialog.setItem(mItem);
        dialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "ServiceCallDialogFragment");
    }

    public void addToInventory(long quantity) {
        long newCurrrentQuantity =  mItem.mCurrentQuantity + quantity;
        mTextCurrentQuantity.setText(String.valueOf(newCurrrentQuantity));
        mCallbacks.EnableRevertButton(true);
        mCallbacks.EnableSaveButton(true);
        mCallbacks.RedrawOptionsMenu();
    }

    public void subtractFromInventory(long quantity) {
        long newCurrrentQuantity =  mItem.mCurrentQuantity - quantity;
        mTextCurrentQuantity.setText(String.valueOf(newCurrrentQuantity));
        mCallbacks.EnableRevertButton(true);
        mCallbacks.EnableSaveButton(true);
        mCallbacks.RedrawOptionsMenu();
    }
    public void createServiceCall(long itemID, String description) {
        ServiceCall sc = new ServiceCall();
        sc.mItemID = itemID;
        sc.mDescription = description;
        sc.mStatus = ServiceCall.OpenStatus;
        sc.mOpenTimeStamp = Calendar.getInstance().getTimeInMillis();
        // a new service call is being inserted.
        Uri uri = getActivity().getContentResolver().insert(HospitalInventoryContentProvider.SERVICE_CALL_URI, sc.getContentValues());
        if (uri != null) {
            Toast toast = Toast.makeText(mContext, "Problem report created.", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            Toast toast = Toast.makeText(mContext, "Failed to create problem report.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
