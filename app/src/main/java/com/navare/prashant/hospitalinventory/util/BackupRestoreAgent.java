package com.navare.prashant.hospitalinventory.util;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.os.ParcelFileDescriptor;

import com.navare.prashant.hospitalinventory.Database.InventoryDatabase;
import com.navare.prashant.hospitalinventory.HospitalInventoryApp;

import java.io.IOException;

/**
 * Created by prashant on 29-Nov-15.
 */
public class BackupRestoreAgent extends BackupAgentHelper {

    // The name of the SharedPreferences file
    static final String PREFS_FILE = HospitalInventoryApp.sContext.getPackageName() + "_preferences";
    static final String DB_FILE = "../databases/" + InventoryDatabase.DATABASE_NAME;


    // A key to uniquely identify the set of preferences backup data
    static final String PREFS_BACKUP_KEY = "preferences";
    static final String DB_BACKUP_KEY = "database";

    // Allocate a helper and add it to the backup agent
    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper prefsHelper = new SharedPreferencesBackupHelper(this, PREFS_FILE);
        addHelper(PREFS_BACKUP_KEY, prefsHelper);

        FileBackupHelper dbHelper = new FileBackupHelper(this, DB_FILE);
        addHelper(DB_BACKUP_KEY, dbHelper);
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
                         ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper performs backup
        synchronized (HospitalInventoryApp.sDatabaseLock) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
                          ParcelFileDescriptor newState) throws IOException {
        // Hold the lock while the FileBackupHelper restores the file
        synchronized (HospitalInventoryApp.sDatabaseLock) {
            super.onRestore(data, appVersionCode, newState);
        }
    }
}
