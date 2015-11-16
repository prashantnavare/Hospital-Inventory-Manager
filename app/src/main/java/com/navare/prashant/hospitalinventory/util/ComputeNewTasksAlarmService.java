package com.navare.prashant.hospitalinventory.util;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.navare.prashant.hospitalinventory.Database.HospitalInventoryContentProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by prashant on 16-Nov-15.
 */
public class ComputeNewTasksAlarmService extends IntentService {
    public ComputeNewTasksAlarmService() {
        super("ComputeNewTasksAlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // BEGIN_INCLUDE(service_onhandle)
        Log.i("NewTasksAlarmService", "Starting computeNewTasks()");
        getContentResolver().call(HospitalInventoryContentProvider.COMPUTE_NEW_TASKS_URI, "computeNewTasks", null, null);
        // Release the wake lock provided by the BroadcastReceiver.
        ComputeNewTasksAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
}
