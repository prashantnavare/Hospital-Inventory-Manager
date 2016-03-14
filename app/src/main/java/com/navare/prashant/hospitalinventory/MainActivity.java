package com.navare.prashant.hospitalinventory;

import com.navare.prashant.hospitalinventory.util.SimpleEula;
import com.navare.prashant.hospitalinventory.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
    private Button buttonInventory;
    private Button buttonTasks;
    private Button buttonSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To solve the documented problem of multiple instances of Main activity (see https://code.google.com/p/android/issues/detail?id=2373)
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        setContentView(R.layout.activity_main);

        new SimpleEula(this).show();

        // Buttons
        buttonInventory = (Button) findViewById(R.id.inventory_button);
        buttonTasks = (Button) findViewById(R.id.tasks_button);
        buttonSettings = (Button) findViewById(R.id.settings_button);

        // Set the title to the name of the hospital
        setTitleAndTaskandItemCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitleAndTaskandItemCount();
    }

    public void onTasksClick(View view)
    {
        startActivity(new Intent(this, TaskListActivity.class));
    }

    public void onInventoryClick(View view) {
        startActivity(new Intent(this, ItemListActivity.class));
    }

    public void onReportsClick(View view) {
        startActivity(new Intent(this, ReportListActivity.class));
    }

    public void onBackupRestoreClick(View view) {
        startActivity(new Intent(this, BackupRestoreActivity.class));
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void setTitleAndTaskandItemCount() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String titleString = preferences.getString(HospitalInventoryApp.sPrefOrganizationName, "");
        titleString = titleString + " Inventory Manager";
        setTitle(titleString);

        long taskCount = preferences.getLong(HospitalInventoryApp.sPrefTaskCount, 0);
        String taskButtonString = "Tasks (" + String.valueOf(taskCount) + ")";
        buttonTasks.setText(taskButtonString);

        long itemCount = preferences.getLong(HospitalInventoryApp.sPrefItemCount, 0);
        String itemButtonString = "Inventory (" + String.valueOf(itemCount) + ")";
        buttonInventory.setText(itemButtonString);
    }
}
