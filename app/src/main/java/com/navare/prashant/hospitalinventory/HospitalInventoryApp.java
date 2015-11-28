package com.navare.prashant.hospitalinventory;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.navare.prashant.hospitalinventory.util.ComputeNewTasksAlarmReceiver;

/**
 * Created by prashant on 22-Nov-15.
 */
public class HospitalInventoryApp extends Application {

    // Object for intrinsic database lock
    public static final Object sDatabaseLock = new Object();

    static String sTaskAlarmInitialized = "TaskAlarmInitialized";
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(sTaskAlarmInitialized, false)) {

            ComputeNewTasksAlarmReceiver alarmReceiver = new ComputeNewTasksAlarmReceiver();
            // Set up the daily alarm for computing new tasks
            alarmReceiver.setAlarm(getApplicationContext(), true);

            // Set the preferences flag to true
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(sTaskAlarmInitialized, true);
            editor.commit();
        }
    }
}