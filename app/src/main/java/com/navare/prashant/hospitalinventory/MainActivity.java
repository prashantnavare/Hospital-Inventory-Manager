package com.navare.prashant.hospitalinventory;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.navare.prashant.hospitalinventory.util.SimpleEula;
import com.navare.prashant.hospitalinventory.util.SystemUiHider;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    private AdView mAdView;
    private InterstitialAd mInterstitialAdForTasks;

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

        // Ads related
        // Banner Ad
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Tasks related interstitial ad
        mInterstitialAdForTasks = new InterstitialAd(this);
        mInterstitialAdForTasks.setAdUnitId(getString(R.string.test_interstitial_ad_unit_id));

        // [START create_interstitial_ad_listener]
        mInterstitialAdForTasks.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitialForTasks();
                onTasksClick(null);
            }
        });
    }

    // Called when leaving the activity
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    // Called when returning to the activity
    @Override
    protected void onResume() {
        super.onResume();
        setTitleAndTaskandItemCount();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (!mInterstitialAdForTasks.isLoaded()) {
            requestNewInterstitialForTasks();
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

    private void requestNewInterstitialForTasks() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAdForTasks.loadAd(adRequest);
    }

    public void onTasksClick(View view)
    {
        if (mInterstitialAdForTasks.isLoaded()) {
            mInterstitialAdForTasks.show();
        }
        else {
            startActivity(new Intent(this, TaskListActivity.class));
        }
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

// TODO: Add interstitials for Inventory, Reports, Backup & Restore
// TODO: Add banners for Tasks, Inventory, Reports, Backup & Restore
// TODO: Add Remove Ads button + relevant logic for removing ads + Remove Ads button
// TODO: Remove Ads logic - In app purchase logic
// TODO: Register with admob.com, generate ad unit IDs for all the interstitials and banners, use them in code
