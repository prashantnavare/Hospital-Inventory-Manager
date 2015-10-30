package com.navare.prashant.hospitalinventory;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.navare.prashant.hospitalinventory.util.ContractTaskDoneDialogFragment;
import com.navare.prashant.hospitalinventory.util.InventoryDialogFragment;
import com.navare.prashant.hospitalinventory.util.InventoryTaskDoneDialogFragment;
import com.navare.prashant.hospitalinventory.util.TaskDoneDialogFragment;


/**
 * An activity representing a single Task detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TaskListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link TaskDetailFragment}.
 */
public class TaskDetailActivity extends ActionBarActivity
        implements  TaskDetailFragment.Callbacks, TaskDoneDialogFragment.TaskDoneDialogListener,
                    ContractTaskDoneDialogFragment.ContractTaskDoneDialogListener,
                    InventoryTaskDoneDialogFragment.InventoryTaskDoneDialogListener {

    private MenuItem doneMenuItem = null;
    private MenuItem revertMenuItem = null;
    private MenuItem saveMenuItem = null;

    private boolean mbDoneMenuEnable = false;
    private boolean mbRevertMenuEnable = false;
    private boolean mbSaveMenuEnable = false;

    public final int PICK_CONTACT = 2015;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(TaskDetailFragment.ARG_TASK_ID,
                    getIntent().getStringExtra(TaskDetailFragment.ARG_TASK_ID));
            TaskDetailFragment fragment = new TaskDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.task_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_detail_actions, menu);

        saveMenuItem = menu.getItem(0);
        doneMenuItem = menu.getItem(1);
        revertMenuItem = menu.getItem(2);

        // Toggle the options menu buttons as per desired state
        // It is possible that the query has already finished loading before we get here
        // as it happens on a separate thread. Hence the boolean state keepers
        EnableSaveButton(mbSaveMenuEnable);
        EnableRevertButton(mbRevertMenuEnable);
        EnableTaskDoneButton(mbDoneMenuEnable);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, TaskListActivity.class));
                return true;
            case R.id.menu_revert:
                revertUI();
                return true;
            case R.id.menu_done:
                doneTask();
                return true;
            case R.id.menu_save:
                saveTask();
                return true;
            case R.id.menu_assign:
                assignTask();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveTask() {
        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).saveTask();
    }

    private void doneTask() {

        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).showTaskDoneDialog();
    }

    private void revertUI() {
        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).revertUI();
    }

    private void assignTask() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String assigneeName = cursor.getString(column);
            ((TaskDetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.task_detail_container)).assignTask(assigneeName);
            cursor.close();
        }
    }

    @Override
    public void EnableTaskDoneButton(boolean bEnable) {
        mbDoneMenuEnable = bEnable;
        if (doneMenuItem != null) {
            doneMenuItem.setEnabled(bEnable);
            doneMenuItem.setVisible(bEnable);
        }
    }

    @Override
    public void EnableRevertButton(boolean bEnable) {
        mbRevertMenuEnable = bEnable;
        if (revertMenuItem != null) {
            revertMenuItem.setEnabled(bEnable);
            revertMenuItem.setVisible(bEnable);
        }
    }

    @Override
    public void EnableSaveButton(boolean bEnable) {
        mbSaveMenuEnable = bEnable;
        if (saveMenuItem != null) {
            saveMenuItem.setEnabled(bEnable);
            saveMenuItem.setVisible(bEnable);
        }
    }

    @Override
    public void RedrawOptionsMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public void onTaskDone() {
        Toast toast = Toast.makeText(getApplicationContext(), "Task marked as done.", Toast.LENGTH_SHORT);
        toast.show();

        NavUtils.navigateUpTo(this, new Intent(this, TaskListActivity.class));
    }

    @Override
    public void onTaskDoneClick(TaskDoneDialogFragment dialog) {
        String completionComments = dialog.getCompletionComments();
        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).markTaskAsDone(completionComments);
    }

    @Override
    public void onCancelClick(TaskDoneDialogFragment dialog) {

    }

    @Override
    public void onContractTaskDoneClick(ContractTaskDoneDialogFragment dialog) {
        long contractValidTillDate = dialog.getContractValidTillDate();
        String completionComments = dialog.getCompletionComments();
        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).markContractTaskAsDone(contractValidTillDate, completionComments);

    }

    @Override
    public void onContractTaskCancelClick(ContractTaskDoneDialogFragment dialog) {

    }

    @Override
    public void onInventoryTaskDoneClick(InventoryTaskDoneDialogFragment dialog) {
        long addedQuantity = dialog.getAddedQuantity();
        String completionComments = dialog.getCompletionComments();
        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).markInventoryTaskAsDone(addedQuantity, completionComments);

    }

    @Override
    public void onInventoryTaskCancelClick(InventoryTaskDoneDialogFragment dialog) {

    }
}
