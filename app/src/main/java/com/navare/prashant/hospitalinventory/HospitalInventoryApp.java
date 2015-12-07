package com.navare.prashant.hospitalinventory;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.navare.prashant.hospitalinventory.util.ComputeNewTasksAlarmReceiver;

/**
 * Created by prashant on 22-Nov-15.
 */
public class HospitalInventoryApp extends Application {

    // Object for intrinsic database lock
    public static final Object sDatabaseLock = new Object();

    public static Context sContext;

    public static String sPrefTaskAlarmInitialized = "TaskAlarmInitialized";
    public static String sPrefOrganizationName = "OrganizationName";
    public static String sPrefTaskRefreshTime = "TaskRefreshTime";
    public static String sPrefTaskCount = "TaskCount";

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(sPrefTaskAlarmInitialized, false)) {

            ComputeNewTasksAlarmReceiver alarmReceiver = new ComputeNewTasksAlarmReceiver();
            // Set up the daily alarm for computing new tasks
            alarmReceiver.setAlarm(getApplicationContext(), true);

            // Set the preferences flag to true
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(sPrefTaskAlarmInitialized, true);
            editor.commit();
        }
    }

    static public void incrementTaskCount() {
        changeTaskCount(1);
    }

    static public void decrementTaskCount() {
        changeTaskCount(-1);
    }

    static private void changeTaskCount(long numTasks) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        long taskCount = prefs.getLong(sPrefTaskCount, 0);
        taskCount = taskCount + numTasks;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(sPrefTaskCount, taskCount);
        editor.commit();
    }
}