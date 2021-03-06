package com.navare.prashant.hospitalinventory;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.navare.prashant.hospitalinventory.util.AssignTaskDialogFragment;
import com.navare.prashant.hospitalinventory.util.ContractTaskDoneDialogFragment;
import com.navare.prashant.hospitalinventory.util.InventoryTaskDoneDialogFragment;
import com.navare.prashant.hospitalinventory.util.TaskDoneDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An activity representing a single Task detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link TaskListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link TaskDetailFragment}.
 */
public class TaskDetailActivity extends AppCompatActivity
        implements  TaskDetailFragment.Callbacks, TaskDoneDialogFragment.TaskDoneDialogListener,
                    ContractTaskDoneDialogFragment.ContractTaskDoneDialogListener,
                    InventoryTaskDoneDialogFragment.InventoryTaskDoneDialogListener,
                    AssignTaskDialogFragment.AssignTaskDialogListener {

    private MenuItem assignMenuItem = null;
    private MenuItem doneMenuItem = null;
    private MenuItem saveMenuItem = null;
    private MenuItem revertMenuItem = null;

    private  boolean mbAssignMenuEnable = true;
    private boolean mbDoneMenuEnable = true;
    private boolean mbSaveMenuEnable = false;
    private boolean mbRevertMenuEnable = false;

    private Activity mThisActivity;

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
        mThisActivity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_detail_actions, menu);

        saveMenuItem = menu.getItem(0);
        revertMenuItem = menu.getItem(1);
        assignMenuItem = menu.getItem(2);
        doneMenuItem = menu.getItem(3);

        // Toggle the options menu buttons as per desired state
        // It is possible that the query has already finished loading before we get here
        // as it happens on a separate thread. Hence the boolean state keepers
        EnableAssignButton(mbAssignMenuEnable);
        EnableTaskDoneButton(mbDoneMenuEnable);
        EnableSaveButton(mbSaveMenuEnable);
        EnableRevertButton(mbRevertMenuEnable);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (mbSaveMenuEnable) {
            promptUserForSavingTask();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_assign:
                assignTask();
                return true;
            case R.id.menu_done:
                doneTask();
                return true;
            case R.id.menu_save:
                saveTask();
                return true;
            case R.id.menu_revert:
                revertUI();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void assignTask() {
        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).assignTask();
    }

    private void doneTask() {

        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).showTaskDoneDialog();
    }

    private void promptUserForSavingTask() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Save Changes");

        // Setting Dialog Message
        alertDialog.setMessage("Would you like to save the changes to this task?");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_menu_save);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                saveTask();
                NavUtils.navigateUpTo(mThisActivity, new Intent(mThisActivity, TaskListActivity.class));
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                NavUtils.navigateUpTo(mThisActivity, new Intent(mThisActivity, TaskListActivity.class));
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void saveTask() {
        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).saveTask();
    }

    private void revertUI() {
        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).revertUI();
    }

    @Override
    public void EnableAssignButton(boolean bEnable) {
        mbAssignMenuEnable = bEnable;
        if (assignMenuItem != null) {
            assignMenuItem.setEnabled(bEnable);
            assignMenuItem.setVisible(bEnable);
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
    public void EnableSaveButton(boolean bEnable) {
        mbSaveMenuEnable = bEnable;
        if (saveMenuItem != null) {
            saveMenuItem.setEnabled(bEnable);
            saveMenuItem.setVisible(bEnable);
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
    public void setTitleString(String titleString) {
        setTitle(titleString);
    }

    @Override
    public void onTaskDoneClick(TaskDoneDialogFragment dialog) {
        String completionComments = dialog.getCompletionComments();
        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).markTaskAsDone(completionComments);
    }

    @Override
    public void onAssignTaskOKClick(AssignTaskDialogFragment dialog) {
        String assigneeName = dialog.getAssigneeName();
        String assigneeNumber = dialog.getAssigneePhone();
        ((TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.task_detail_container)).setTaskAssigneeInfo(assigneeName, assigneeNumber);
    }

    @Override
    public void onCancelClick(AssignTaskDialogFragment dialog) {

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
