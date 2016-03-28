package com.navare.prashant.hospitalinventory;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;import android.support.v4.app.LoaderManager;import android.support.v4.content.CursorLoader;import android.support.v4.content.Loader;
import android.telephony.SmsManager;
import android.text.Editable;import android.text.TextWatcher;import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.navare.prashant.hospitalinventory.Database.HospitalInventoryContentProvider;import com.navare.prashant.hospitalinventory.Database.Item;
import com.navare.prashant.hospitalinventory.Database.ServiceCall;
import com.navare.prashant.hospitalinventory.Database.Task;
import com.navare.prashant.hospitalinventory.util.ContractTaskDoneDialogFragment;
import com.navare.prashant.hospitalinventory.util.InventoryTaskDoneDialogFragment;
import com.navare.prashant.hospitalinventory.util.TaskDoneDialogFragment;

import java.text.ParseException;import java.text.SimpleDateFormat;import java.util.Calendar;
import java.util.Date;

/**
 * A fragment representing a single Task detail screen.
 * This fragment is either contained in a {@link TaskListActivity}
 * in two-pane mode (on tablets) or a {@link TaskDetailActivity}
 * on handsets.
 */
public class TaskDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOADER_ID_TASK_DETAILS = 12;
    public static final int LOADER_ID_ITEM_DETAILS = 13;
    public static final int LOADER_ID_SERVICE_CALL_DETAILS = 14;
    /**
     * The fragment argument representing the task ID that this fragment
     * represents.
     */
    public static final String ARG_TASK_ID = "task_id";

    private Context mContext = null;
    private int mSpinnerPosition = -1;

    /**
     * The task this fragment is presenting.
     */
    private String mTaskID;
    private Task mTask = null;
    private Item mItem = null;
    private ServiceCall mServiceCall = null;

    private TextView mTextItemType;
    private TextView mTextItemName;
    private TextView mTextItemLocation;
    private TextView mTextDueDate;
    private TextView mTextAssignedTo;
    private TextView mTextTaskType;
    private TextView mTextInstructionsLabel;
    private TextView mTextInstructions;
    private Spinner mSpinnerPriority;

    private TableRow mContractExpiryDateRow;
    private TextView mTextContractExpiryDate;
    private TableRow mRequiredQuantityRow;
    private TextView mTextRequiredQuantity;

    private AdView mAdView;

    String mCurrentAssignee = "";
    String mNewAssignee = "";

    public interface Callbacks {
        /**
         * Callbacks for when a task has been selected.
         */
        void EnableAssignButton(boolean bEnable);
        void EnableTaskDoneButton(boolean bEnable);
        void EnableCallButton(boolean bEnable);
        void EnableSaveButton(boolean bEnable);
        void EnableRevertButton(boolean bEnable);
        void RedrawOptionsMenu();
        void onTaskDone();
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {

        @Override
        public void EnableAssignButton(boolean bEnable) {

        }

        @Override
        public void EnableTaskDoneButton(boolean bEnable) {

        }

        @Override
        public void EnableCallButton(boolean bEnable) {

        }

        @Override
        public void EnableRevertButton(boolean bEnable) {

        }

        @Override
        public void EnableSaveButton(boolean bEnable) {

        }

        @Override
        public void RedrawOptionsMenu() {

        }

        @Override
        public void onTaskDone() {

    }};

    /**
     * The fragment's current callback object, which is notified of changes to the item
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TaskDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_TASK_ID)) {
            mTaskID = getArguments().getString(ARG_TASK_ID);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement task detail fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
        mContext = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    // Called when returning to the activity
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    // Called before the activity is destroyed
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_detail, container, false);

        mTextItemType = ((TextView) rootView.findViewById(R.id.textItemType));
        mTextItemName = ((TextView) rootView.findViewById(R.id.textItemName));
        mTextItemLocation = ((TextView) rootView.findViewById(R.id.textItemLocation));
        mTextDueDate = ((TextView) rootView.findViewById(R.id.textDueDate));

        mTextAssignedTo = ((TextView) rootView.findViewById(R.id.textAssignedTo));

        mTextTaskType = ((TextView) rootView.findViewById(R.id.textTaskType));

        mTextInstructionsLabel = ((TextView) rootView.findViewById(R.id.textInstructionsLabel));
        mTextInstructions = ((TextView) rootView.findViewById(R.id.textInstructions));

        mSpinnerPriority = (Spinner) rootView.findViewById(R.id.spinnerPriority);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.priorities_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPriority.setAdapter(adapter);
        mSpinnerPosition = 0;
        mSpinnerPriority.setSelection(0, false);
        mSpinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                if (position != mSpinnerPosition) {
                    mSpinnerPosition = position;
                    mCallbacks.EnableRevertButton(true);
                    mCallbacks.EnableSaveButton(true);
                    mCallbacks.RedrawOptionsMenu();
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        mContractExpiryDateRow = (TableRow) rootView.findViewById(R.id.contractExpiryDateRow);
        mTextContractExpiryDate = ((TextView) rootView.findViewById(R.id.textContractExpiryDate));
        mRequiredQuantityRow = (TableRow) rootView.findViewById(R.id.requiredQuantityRow);
        mTextRequiredQuantity = ((TextView) rootView.findViewById(R.id.textRequiredQuantity));

        // Banner Ad
        mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if ((mTaskID != null) && (mTaskID.isEmpty() == false)) {
            getLoaderManager().initLoader(LOADER_ID_TASK_DETAILS, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == LOADER_ID_TASK_DETAILS) {
            Uri taskURI = Uri.withAppendedPath(HospitalInventoryContentProvider.TASK_URI,
                    mTaskID);

            return new CursorLoader(getActivity(),
                    taskURI, Task.FIELDS, null, null,
                    null);
        }
        else if (id == LOADER_ID_ITEM_DETAILS) {
            Uri itemURI = Uri.withAppendedPath(HospitalInventoryContentProvider.ITEM_URI,
                    String.valueOf(mTask.mItemID));

            return new CursorLoader(getActivity(),
                    itemURI, Item.FIELDS, null, null,
                    null);
        }
        else if (id == LOADER_ID_SERVICE_CALL_DETAILS) {
            Uri serviceCallURI = Uri.withAppendedPath(HospitalInventoryContentProvider.SERVICE_CALL_URI,
                    String.valueOf(mTask.mServiceCallID));

            return new CursorLoader(getActivity(),
                    serviceCallURI, ServiceCall.FIELDS, null, null,
                    null);
        }
        else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {

        if (dataCursor != null) {
            int loaderID = loader.getId();
            if (loaderID == LOADER_ID_TASK_DETAILS) {
                if (mTask == null)
                    mTask = new Task();

                mTask.setContentFromCursor(dataCursor);
                updateUIFromTask();

                if (mTask.mTaskType == Task.ServiceCall) {
                    // Get the service call details.
                    getLoaderManager().initLoader(LOADER_ID_SERVICE_CALL_DETAILS, null, this);
                }
                else {
                    // Get the item details
                    getLoaderManager().initLoader(LOADER_ID_ITEM_DETAILS, null, this);
                }
            }
            else if (loaderID == LOADER_ID_ITEM_DETAILS) {
                if (mItem == null)
                    mItem = new Item();
                mItem.setContentFromCursor(dataCursor);
                updateUIFromTask();
            }
            else if (loaderID == LOADER_ID_SERVICE_CALL_DETAILS) {
                if (mServiceCall == null)
                    mServiceCall = new ServiceCall();
                mServiceCall.setContentFromCursor(dataCursor);
                updateUIFromTask();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void enableRevertAndSaveButtons() {
        mCallbacks.EnableRevertButton(true);
        mCallbacks.EnableSaveButton(true);
        mCallbacks.RedrawOptionsMenu();
    }

    public void revertUI() {
        updateUIFromTask();
    }

    public void markTaskAsDone(String completionComments) {
        mTask.mCompletionComments = completionComments;
        mTask.mCompletedTimeStamp = Calendar.getInstance().getTimeInMillis();
        mTask.mStatus = Task.CompletedStatus;
        Uri taskURI = Uri.withAppendedPath(HospitalInventoryContentProvider.TASK_URI,
                mTaskID);
        int result = getActivity().getContentResolver().update(taskURI, mTask.getContentValues(), null, null);

        // if this is a calibration task, then set the calibration date in the item.
        boolean bUpdateItem = false;
        if (mTask.mTaskType == Task.Calibration) {
            bUpdateItem = true;
            mItem.mCalibrationDate = Calendar.getInstance().getTimeInMillis();
        }
        // if this is a maintenance task, then set the maintenance date in the item.
        if (mTask.mTaskType == Task.Maintenance) {
            bUpdateItem = true;
            mItem.mMaintenanceDate = Calendar.getInstance().getTimeInMillis();
        }
        if (bUpdateItem) {
            Uri itemURI = Uri.withAppendedPath(HospitalInventoryContentProvider.ITEM_URI,
                    String.valueOf(mItem.mID));
            result = getActivity().getContentResolver().update(itemURI, mItem.getContentValues(), null, null);
        }

        // if this is a service call task, then mark the service call as closed.
        if (mTask.mTaskType == Task.ServiceCall) {
            mServiceCall.mStatus = ServiceCall.ClosedStatus;
            mServiceCall.mClosedTimeStamp = Calendar.getInstance().getTimeInMillis();
            Uri serviceCallURI = Uri.withAppendedPath(HospitalInventoryContentProvider.SERVICE_CALL_URI,
                    String.valueOf(mServiceCall.mID));
            result = getActivity().getContentResolver().update(serviceCallURI, mServiceCall.getContentValues(), null, null);
        }
        if (result > 0)
            mCallbacks.onTaskDone();
    }

    public void markContractTaskAsDone(long contractValidTillDate, String completionComments) {
        mTask.mCompletionComments = completionComments;
        mTask.mCompletedTimeStamp = Calendar.getInstance().getTimeInMillis();
        mTask.mStatus = Task.CompletedStatus;
        Uri taskURI = Uri.withAppendedPath(HospitalInventoryContentProvider.TASK_URI,
                mTaskID);
        int result = getActivity().getContentResolver().update(taskURI, mTask.getContentValues(), null, null);

        // Next update the item with the new contractValidTillDate
        mItem.mContractValidTillDate = contractValidTillDate;
        Uri itemURI = Uri.withAppendedPath(HospitalInventoryContentProvider.ITEM_URI,
                String.valueOf(mItem.mID));
        result = getActivity().getContentResolver().update(itemURI, mItem.getContentValues(), null, null);
        if (result > 0)
            mCallbacks.onTaskDone();
    }

    public void markInventoryTaskAsDone(long addedQuantity, String completionComments) {
        mTask.mCompletionComments = completionComments;
        mTask.mCompletedTimeStamp = Calendar.getInstance().getTimeInMillis();
        mTask.mStatus = Task.CompletedStatus;
        Uri taskURI = Uri.withAppendedPath(HospitalInventoryContentProvider.TASK_URI,
                mTaskID);
        int result = getActivity().getContentResolver().update(taskURI, mTask.getContentValues(), null, null);

        // Next update the item with the new added quantity
        long newCurrentQuantity = mItem.mCurrentQuantity + addedQuantity;
        mItem.mCurrentQuantity = newCurrentQuantity;
        Uri itemURI = Uri.withAppendedPath(HospitalInventoryContentProvider.ITEM_URI,
                String.valueOf(mItem.mID));
        result = getActivity().getContentResolver().update(itemURI, mItem.getContentValues(), null, null);
        if (result > 0)
            mCallbacks.onTaskDone();
    }

    public void saveTask() {
        updateTaskFromUI();
        Uri taskURI = Uri.withAppendedPath(HospitalInventoryContentProvider.TASK_URI,
                mTaskID);
        int result = getActivity().getContentResolver().update(taskURI, mTask.getContentValues(), null, null);
        if (result > 0) {

            // If the task was unassigned to a person, send that person an SMS.
            // If the task was assigned to a new person, send that person an SMS.
            sendTaskSMSs();
            mCallbacks.EnableSaveButton(false);
            mCallbacks.EnableRevertButton(false);
            mCallbacks.RedrawOptionsMenu();
        }
    }

    private void sendTaskSMSs() {
        if (mNewAssignee.isEmpty() == false) {
            // If the current and new assignees are the same, don't do anytrhing.
            if (mNewAssignee.equalsIgnoreCase(mCurrentAssignee) == true)
                return;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String titleString = preferences.getString(HospitalInventoryApp.sPrefOrganizationName, "");
            titleString = titleString + " Inventory Manager: ";
            // First send SMS to the new assignee
            String smsAssignMessage = titleString + "You have been assigned a " + mTask.getTaskTypeString() + " task for " + mTask.mItemName;
            if (mTask.mItemLocation.isEmpty() == false) {
                smsAssignMessage = smsAssignMessage + " located at " + mTask.mItemLocation;
            }
            String assigneePhoneNumber = getPhoneNumber(mNewAssignee);
            if (assigneePhoneNumber.isEmpty() == false) {
                sendAssigneeSMS(assigneePhoneNumber, smsAssignMessage);
            }
        }
    }

    private void sendAssigneeSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";

        PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0, new Intent(SENT), 0);
        final SmsManager sms = SmsManager.getDefault();

        // when the SMS has been sent, send the deAssignee an SMS about unassignment
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        if (mCurrentAssignee.isEmpty() == false) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                            String titleString = preferences.getString(HospitalInventoryApp.sPrefOrganizationName, "");
                            titleString = titleString + " Inventory Manager: ";
                            String smsUnAssignMessage = titleString + "You have been unassigned from a " + mTask.getTaskTypeString() + " task for " + mTask.mItemName;
                            if (mTask.mItemLocation.isEmpty() == false) {
                                smsUnAssignMessage = smsUnAssignMessage + " located at " + mTask.mItemLocation;
                            }
                            String unAssigneePhoneNumber = getPhoneNumber(mCurrentAssignee);
                            if (unAssigneePhoneNumber.isEmpty() == false) {
                                sms.sendTextMessage(unAssigneePhoneNumber, null, smsUnAssignMessage, null, null);
                            }
                        }
                        break;
                }
            }
        }, new IntentFilter(SENT));

        sms.sendTextMessage(phoneNumber, null, message, sentPI, null);
    }

    private String getPhoneNumber(String name) {
        String phoneNumber = "";
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            phoneNumber = c.getString(0);
        }
        c.close();
        return phoneNumber;
    }

    public final int PICK_CONTACT = 2015;

    public void assignTask() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT);
    }

    public void callAssignee() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mTask.mAssignedToContactNumber));
        startActivity(callIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = mContext.getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int columnDisplayName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String assigneeName = cursor.getString(columnDisplayName);
            cursor.close();

            mCurrentAssignee = mTask.mAssignedTo;
            mNewAssignee = assigneeName;
            mTextAssignedTo.setText(assigneeName);
            enableRevertAndSaveButtons();
        }
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void updateTaskFromUI() {
        if (mTask == null)
            mTask = new Task();

        mTask.mAssignedTo = mTextAssignedTo.getText().toString();
        if (mTask.mAssignedTo.isEmpty() == false)
            mTask.mAssignedToContactNumber = getPhoneNumber(mTask.mAssignedTo);
        if (mSpinnerPriority.getSelectedItemId() == 0) {
            mTask.mPriority = Task.NormalPriority;
        }
        else {
            mTask.mPriority = Task.UrgentPriority;
        }
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void updateUIFromTask() {
        if (mTask.mTaskType == Task.Inventory)
            mTextItemType.setText(getResources().getText(R.string.consummable));
        else
            mTextItemType.setText(getResources().getText(R.string.instrument));

        mTextItemName.setText(mTask.mItemName);
        mTextItemLocation.setText(mTask.mItemLocation);

        if (mTask.mDueDate > 0) {
            Date dueDate = new Date();
            dueDate.setTime(mTask.mDueDate);
            SimpleDateFormat dueDateFormat = new SimpleDateFormat("dd MMMM, yyyy");
            String dueDateString = dueDateFormat.format(dueDate);
            mTextDueDate.setText(dueDateString);
        }

        mTextAssignedTo.setText(mTask.mAssignedTo);

        mTextTaskType.setText(mTask.getTaskTypeString());

        // The following table rows are visible only if task is contract or inventory
        mContractExpiryDateRow.setVisibility(View.GONE);
        mRequiredQuantityRow.setVisibility(View.GONE);
        if (mTask.mTaskType == Task.Inventory) {
            if (mItem != null) {
                mRequiredQuantityRow.setVisibility(View.VISIBLE);
                long requiredQuantity = mItem.mMinRequiredQuantity - mItem.mCurrentQuantity;
                mTextRequiredQuantity.setText(String.valueOf(requiredQuantity));
            }
        }
        if (mTask.mTaskType == Task.Contract) {
            if (mItem != null) {
                mContractExpiryDateRow.setVisibility(View.VISIBLE);
                if (mItem.mContractValidTillDate > 0) {
                    Calendar contractDate = Calendar.getInstance();
                    contractDate.setTimeInMillis(mItem.mContractValidTillDate);
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM, yyyy");
                    mTextContractExpiryDate.setText(dateFormatter.format(contractDate.getTime()));
                }
                else {
                    mTextContractExpiryDate.setText("No date set");
                }
            }
        }

        if (mItem != null) {
            if (mTask.mTaskType == Task.Calibration)
                mTextInstructions.setText(mItem.mCalibrationInstructions);
            else if (mTask.mTaskType == Task.Contract)
                mTextInstructions.setText(mItem.mContractInstructions);
            else if (mTask.mTaskType == Task.Maintenance)
                mTextInstructions.setText(mItem.mMaintenanceInstructions);
        }
        if (mServiceCall != null) {
            mTextInstructionsLabel.setText(getResources().getText(R.string.description));
            mTextInstructions.setText(mServiceCall.mDescription);
        }

        if (mTask.mPriority == Task.NormalPriority) {
            mSpinnerPosition = 0;
            mSpinnerPriority.setSelection(0, false);
        }
        else if (mTask.mPriority == Task.UrgentPriority) {
            mSpinnerPosition = 1;
            mSpinnerPriority.setSelection(1, false);
        }

        // Toggle the action bar buttons appropriately
        mCallbacks.EnableAssignButton(true);

        if (mTask.mAssignedTo.isEmpty() == false) {
            if (mTask.mAssignedToContactNumber.isEmpty() == false) {
                mCallbacks.EnableCallButton(true);
            }
        }
        mCallbacks.EnableTaskDoneButton(true);
        mCallbacks.EnableSaveButton(false);
        mCallbacks.EnableRevertButton(false);
        mCallbacks.RedrawOptionsMenu();
    }

    public void showTaskDoneDialog() {
        if (mTask.mTaskType == Task.Contract) {
            ContractTaskDoneDialogFragment dialog = new ContractTaskDoneDialogFragment();
            dialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "ContractTaskDoneDialogFragment");
        }
        else if (mTask.mTaskType == Task.Inventory) {
            InventoryTaskDoneDialogFragment dialog = new InventoryTaskDoneDialogFragment();
            dialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "InventoryTaskDoneDialogFragment");
        }
        else {
            TaskDoneDialogFragment dialog = new TaskDoneDialogFragment();
            dialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "TaskDoneDialogFragment");
        }
    }
}
