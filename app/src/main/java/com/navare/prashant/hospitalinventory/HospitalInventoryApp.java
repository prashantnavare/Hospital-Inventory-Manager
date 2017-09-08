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

    public static Context mAppContext;

    private static String sPrefTaskAlarmInitialized = "TaskAlarmInitialized";
    public static String sPrefOrganizationName = "OrganizationName";
    public static String sPrefTaskRefreshTime = "TaskRefreshTime";
    public static String sPrefTaskCount = "TaskCount";
    public static String sPrefItemCount = "ItemCount";
    private static String sPrefPurchaseValue = "PurchaseValue";

    public static long APP_PURCHASED = 0xdeadbeef;

    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mEditor;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppContext = getApplicationContext();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPrefs.edit();
        if (!mPrefs.getBoolean(sPrefTaskAlarmInitialized, false)) {
            // Set the preferences flag to true
            mEditor.putBoolean(sPrefTaskAlarmInitialized, true);
            mEditor.putString(HospitalInventoryApp.sPrefTaskRefreshTime, "01:00");
            mEditor.commit();

            ComputeNewTasksAlarmReceiver alarmReceiver = new ComputeNewTasksAlarmReceiver();
            // Set up the daily alarm for computing new tasks
            alarmReceiver.setAlarm(mAppContext, true);

        }
    }

    static public void incrementTaskCount() {
        changeTaskCount(1);
    }

    static public void decrementTaskCount() {
        changeTaskCount(-1);
    }

    static private void changeTaskCount(long numTasks) {
        long taskCount = mPrefs.getLong(sPrefTaskCount, 0);
        taskCount = taskCount + numTasks;
        mEditor.putLong(sPrefTaskCount, taskCount);
        mEditor.commit();
    }

    static public void incrementItemCount() {
        changeItemCount(1);
    }

    static public void decrementItemCount() {
        changeItemCount(-1);
    }

    static private void changeItemCount(long numItems) {
        long itemCount = mPrefs.getLong(sPrefItemCount, 0);
        itemCount = itemCount + numItems;
        mEditor.putLong(sPrefItemCount, itemCount);
        mEditor.commit();
    }

    static public void setPurchaseValue(long purchaseValue) {
        mEditor.putLong(sPrefPurchaseValue, purchaseValue);
        mEditor.commit();
    }

    static public boolean isAppPurchased() {
        long purchaseValue = mPrefs.getLong(sPrefPurchaseValue, 0);
        return purchaseValue == APP_PURCHASED;
    }

    static public String getOrgName() {
        return mPrefs.getString(HospitalInventoryApp.sPrefOrganizationName, "");
    }

    static public void setOrgName(String orgName) {
        mEditor.putString(HospitalInventoryApp.sPrefOrganizationName, orgName);
    }

    static public long getTaskCount() {
        return mPrefs.getLong(HospitalInventoryApp.sPrefTaskCount, 0);
    }

    static public void setTaskCount(long taskCount) {
        mEditor.putLong(HospitalInventoryApp.sPrefTaskCount, taskCount);
    }

    static public long getItemCount() {
        return mPrefs.getLong(HospitalInventoryApp.sPrefItemCount, 0);
    }

    static public void setItemCount(long itemCount) {
        mEditor.putLong(HospitalInventoryApp.sPrefItemCount, itemCount);
    }

}