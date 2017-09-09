package com.navare.prashant.hospitalinventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.navare.prashant.hospitalinventory.InAppBilling.IabHelper;
import com.navare.prashant.hospitalinventory.InAppBilling.IabResult;
import com.navare.prashant.hospitalinventory.InAppBilling.IabBroadcastReceiver;
import com.navare.prashant.hospitalinventory.InAppBilling.IabBroadcastReceiver.IabBroadcastListener;
import com.navare.prashant.hospitalinventory.InAppBilling.IabHelper;
import com.navare.prashant.hospitalinventory.InAppBilling.IabHelper.IabAsyncInProgressException;
import com.navare.prashant.hospitalinventory.InAppBilling.IabResult;
import com.navare.prashant.hospitalinventory.InAppBilling.Inventory;
import com.navare.prashant.hospitalinventory.InAppBilling.Purchase;
import com.navare.prashant.hospitalinventory.util.SimpleEula;
import com.navare.prashant.hospitalinventory.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends AppCompatActivity {
    private GridView    mGridView;
    private AdView mAdView;

    private InterstitialAd mInterstitialAdForTasks;
    private InterstitialAd mInterstitialAdForInventory;
    private InterstitialAd mInterstitialAdForReports;
    private InterstitialAd mInterstitialAdForBackupRestore;
    private IabHelper mHelper;
    private Activity mThisActivity;

    // TODO: replace this with the real SKU
    static final String SKU_INVENTORY_MANAGER = "android.test.purchased";
    // (arbitrary) request code for the purchase flow
    static final int PURCHASE_REQUEST = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // TODO: Remove this after testing
        // HospitalInventoryApp.setPurchaseValue(HospitalInventoryApp.APP_PURCHASED);

        super.onCreate(savedInstanceState);

        mThisActivity = this;

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

        setTitle(HospitalInventoryApp.getOrgName() + " Inventory Manager");

        mGridView =(GridView)findViewById(R.id.grid);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        onTasksClick();
                        break;
                    case 1:
                        onInventoryClick();
                        break;
                    case 2:
                        onReportsClick();
                        break;
                    case 3:
                        onBackupRestoreClick();
                        break;
                    case 4:
                        onSettingsClick();
                        break;
                    case 5:
                        onRemoveAdsClick();
                        break;
                }
            }
        });

        initGridAdapater();

        mAdView = (AdView) findViewById(R.id.adView);

        doAdsInit();
    }

    private void initGridAdapater() {
        int numButtons = 0;
        if (HospitalInventoryApp.isAppPurchased()) {
            numButtons = 5;
        }
        else {
            numButtons = 6;
        }
        String[]    tileTextArray = new String[numButtons];
        int[]       tileImageArray = new int[numButtons];

        tileTextArray[0] = getString(R.string.tasks) + " (" + String.valueOf(HospitalInventoryApp.getTaskCount()) + ")";
        tileTextArray[1]=getString(R.string.inventory) + " (" + String.valueOf(HospitalInventoryApp.getItemCount()) + ")";;
        tileTextArray[2]=getString(R.string.reports);
        tileTextArray[3]=getString(R.string.backup_restore);
        tileTextArray[4]=getString(R.string.settings);

        tileImageArray[0] = R.drawable.ic_tasks;
        tileImageArray[1] = R.drawable.ic_inventory;
        tileImageArray[2] = R.drawable.ic_reports;
        tileImageArray[3] = R.drawable.ic_backup;
        tileImageArray[4] = R.drawable.ic_settings;

        if (numButtons == 6) {
            tileTextArray[5] = getString(R.string.remove_ads);
            tileImageArray[5] = R.drawable.ic_remove_ads;
        }

        NavigationGridAdapter adapter = new NavigationGridAdapter(this, tileTextArray, tileImageArray);
        mGridView.setAdapter(adapter);
    }

    private void removeAdStuff() {
        mAdView.setVisibility(View.GONE);
    }
    private void doAdsInit() {

        if (HospitalInventoryApp.isAppPurchased()) {
            removeAdStuff();
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
                onTasksClick();
            }
        });

        // Inventory related interstitial ad
        mInterstitialAdForInventory = new InterstitialAd(this);
        mInterstitialAdForInventory.setAdUnitId(getString(R.string.interstitial_inventory_ad_unit_id));

        mInterstitialAdForInventory.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitialForInventory();
                onInventoryClick();
            }
        });

        // Reports related interstitial ad
        mInterstitialAdForReports = new InterstitialAd(this);
        mInterstitialAdForReports.setAdUnitId(getString(R.string.interstitial_reports_ad_unit_id));

        mInterstitialAdForReports.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitialForReports();
                onReportsClick();
            }
        });

        // BackupRestore related interstitial ad
        mInterstitialAdForBackupRestore = new InterstitialAd(this);
        mInterstitialAdForBackupRestore.setAdUnitId(getString(R.string.interstitial_backuprestore_ad_unit_id));

        mInterstitialAdForBackupRestore.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitialForBackupRestore();
                onBackupRestoreClick();
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
        initGridAdapater();
        if (mAdView != null) {
            mAdView.resume();
        }
        doAdsReload();
    }

    // Called before the activity is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
            mAdView = null;
        }
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
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

    public void onTasksClick()
    {
        if (mInterstitialAdForTasks != null && mInterstitialAdForTasks.isLoaded()) {
            mInterstitialAdForTasks.show();
        }
        else {
            startActivity(new Intent(this, TaskListActivity.class));
        }
    }

    public void onInventoryClick() {
        if (mInterstitialAdForInventory != null && mInterstitialAdForInventory.isLoaded()) {
            mInterstitialAdForInventory.show();
        }
        else {
            startActivity(new Intent(this, ItemListActivity.class));
        }
    }

    public void onReportsClick() {
        if (mInterstitialAdForReports != null && mInterstitialAdForReports.isLoaded()) {
            mInterstitialAdForReports.show();
        }
        else {
            startActivity(new Intent(this, ReportListActivity.class));
        }
    }

    public void onBackupRestoreClick() {
        if (mInterstitialAdForBackupRestore != null && mInterstitialAdForBackupRestore.isLoaded()) {
            mInterstitialAdForBackupRestore.show();
        }
        else {
            startActivity(new Intent(this, BackupRestoreActivity.class));
        }
    }

    public void onSettingsClick() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onRemoveAdsClick() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Remove Ads");

        // Setting Dialog Message
        alertDialog.setMessage("Would you like to purchase Inventory Manager and remove the ads?");

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_hospital_inventory);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Purchase", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {

                initiatePurchase();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void initiatePurchase() {

        // TODO: compute your public key and store it in base64EncodedPublicKey
        String base64EncodedPublicKey = "Foo";

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d("initiatePurchase()", "Problem setting up In-app Billing: " + result);
                    // TODO: Show error message to the user
                    return;
                }
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) {
                    // TODO: Show error message to the user
                    return;
                }
                // Hooray, IAB is fully set up!
                Log.d("initiatePurchase()", "Launching purchase flow...");
                setWaitScreen(true);

                String payload = "";
                try {
                    mHelper.launchPurchaseFlow(mThisActivity, SKU_INVENTORY_MANAGER, PURCHASE_REQUEST,
                            mPurchaseFinishedListener, payload);
                } catch (IabAsyncInProgressException e) {
                    showPurchaseErrorAlert("Another purchase operation may be in progress. Please try again later.");
                    setWaitScreen(false);
                }
            }
        });
    }

    private void showPurchaseErrorAlert(String message) {
        showPurchaseAlertInternal(message, true);
    }

    private void showPurchaseSuccessAlert(String message) {
        showPurchaseAlertInternal(message, false);
    }

    private void showPurchaseAlertInternal(String message, boolean bError) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        if (bError) {
            alertDialog.setTitle("Purchase Error");
            alertDialog.setIcon(R.drawable.ic_error);
        }
        else {
            alertDialog.setTitle("Purchase Successful");
            alertDialog.setIcon(R.drawable.ic_success);
        }
        alertDialog.setMessage(message);
        alertDialog.setNeutralButton("OK", null);
        alertDialog.create().show();
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d("initiatePurchase()", "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null)
                return;

            if (result.isFailure()) {
                showPurchaseErrorAlert("There was an error while completing the purchase. Please try and again later.");
                setWaitScreen(false);
                return;
            }

            Log.d("initiatePurchase()", "Purchase successful.");

            if (purchase.getSku().equals(SKU_INVENTORY_MANAGER)) {
                // bought the Inventory Manager app!
                Log.d("initiatePurchase()", "Purchase is premium upgrade. Congratulating user.");
                showPurchaseSuccessAlert("Thank you for for the purchase.");
                HospitalInventoryApp.setPurchaseValue(HospitalInventoryApp.APP_PURCHASED);
                removeAdStuff();
                initGridAdapater();
                setWaitScreen(false);
            }
        }
    };

    void setWaitScreen(boolean set) {
        // TODO: Implement purchase Wait screen
    }
}

