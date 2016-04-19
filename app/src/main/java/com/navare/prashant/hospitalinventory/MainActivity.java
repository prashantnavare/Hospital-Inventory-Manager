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
    private Button buttonRemoveAds;

    private AdView mAdView;
    private InterstitialAd mInterstitialAdForTasks;
    private InterstitialAd mInterstitialAdForInventory;
    private InterstitialAd mInterstitialAdForReports;
    private InterstitialAd mInterstitialAdForBackupRestore;

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

        // TODO: Remove this after testing
        HospitalInventoryApp.setPurchaseValue(HospitalInventoryApp.APP_PURCHASED);

        // Ads related
        doAdsInit();
    }

    private void doAdsInit() {
        buttonRemoveAds = (Button) findViewById(R.id.removeads_button);
        // Banner Ad
        mAdView = (AdView) findViewById(R.id.adView);

        if (HospitalInventoryApp.isAppPurchased() == true) {
            buttonRemoveAds.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Tasks related interstitial ad
        mInterstitialAdForTasks = new InterstitialAd(this);
        mInterstitialAdForTasks.setAdUnitId(getString(R.string.interstitial_tasks_ad_unit_id));

        mInterstitialAdForTasks.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitialForTasks();
                onTasksClick(null);
            }
        });

        // Inventory related interstitial ad
        mInterstitialAdForInventory = new InterstitialAd(this);
        mInterstitialAdForInventory.setAdUnitId(getString(R.string.interstitial_inventory_ad_unit_id));

        mInterstitialAdForInventory.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitialForInventory();
                onInventoryClick(null);
            }
        });

        // Reports related interstitial ad
        mInterstitialAdForReports = new InterstitialAd(this);
        mInterstitialAdForReports.setAdUnitId(getString(R.string.interstitial_reports_ad_unit_id));

        mInterstitialAdForReports.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitialForReports();
                onReportsClick(null);
            }
        });

        // BackupRestore related interstitial ad
        mInterstitialAdForBackupRestore = new InterstitialAd(this);
        mInterstitialAdForBackupRestore.setAdUnitId(getString(R.string.interstitial_backuprestore_ad_unit_id));

        mInterstitialAdForBackupRestore.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitialForBackupRestore();
                onBackupRestoreClick(null);
            }
        });
    }

    private void doAdsReload() {
        if (mInterstitialAdForTasks != null && !mInterstitialAdForTasks.isLoaded()) {
            requestNewInterstitialForTasks();
        }
        if (mInterstitialAdForInventory != null && !mInterstitialAdForInventory.isLoaded()) {
            requestNewInterstitialForInventory();
        }
        if (mInterstitialAdForReports != null && !mInterstitialAdForReports.isLoaded()) {
            requestNewInterstitialForReports();
        }
        if (mInterstitialAdForBackupRestore != null && !mInterstitialAdForBackupRestore.isLoaded()) {
            requestNewInterstitialForBackupRestore();
        }
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
        doAdsReload();
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

    private void requestNewInterstitialForInventory() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAdForInventory.loadAd(adRequest);
    }

    private void requestNewInterstitialForReports() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAdForReports.loadAd(adRequest);
    }

    private void requestNewInterstitialForBackupRestore() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAdForBackupRestore.loadAd(adRequest);
    }

    public void onTasksClick(View view)
    {
        if (mInterstitialAdForTasks != null && mInterstitialAdForTasks.isLoaded()) {
            mInterstitialAdForTasks.show();
        }
        else {
            startActivity(new Intent(this, TaskListActivity.class));
        }
    }

    public void onInventoryClick(View view) {
        if (mInterstitialAdForInventory != null && mInterstitialAdForInventory.isLoaded()) {
            mInterstitialAdForInventory.show();
        }
        else {
            startActivity(new Intent(this, ItemListActivity.class));
        }
    }

    public void onReportsClick(View view) {
        if (mInterstitialAdForReports != null && mInterstitialAdForReports.isLoaded()) {
            mInterstitialAdForReports.show();
        }
        else {
            startActivity(new Intent(this, ReportListActivity.class));
        }
    }

    public void onBackupRestoreClick(View view) {
        if (mInterstitialAdForBackupRestore != null && mInterstitialAdForBackupRestore.isLoaded()) {
            mInterstitialAdForBackupRestore.show();
        }
        else {
            startActivity(new Intent(this, BackupRestoreActivity.class));
        }
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onRemoveAdsClick(View view) {
        // TODO: Remove Ads button logic - In app purchase logic
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

// TODO: Reorder instructions need to be added to item detail fragment
// TODO: Fix up all buttons with the new style
// TODO: Change all font sizes to 12sp or higher

