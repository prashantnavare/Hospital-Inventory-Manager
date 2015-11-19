package com.navare.prashant.hospitalinventory.Database;

/**
 * Created by prashant on 18-Apr-15.
 */

import android.app.SearchManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.sql.Date;
import java.util.HashMap;

/**
 * A class representation of a row in table "Item".
 */

public class Item {

    // Item table
    public static final String TABLE_NAME = "ItemTable";
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Add other fields here.
    // When you add a field, make sure you add corresponding entries in the FIELDS array, the CREATE_TABLE string
    // and declare a member and so on. See all ++++++++ comment lines below.
    // These fields can be anything you want.
    public static final String COL_NAME = "name";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_TYPE = "type";

    public static final String COL_CALIBRATION_REMINDERS = "calibrationReminders";
    public static final String COL_CALIBRATION_FREQUENCY = "calibrationFrequency";
    public static final String COL_LAST_CALIBRATION_DATE = "lastCalibrationDate";
    public static final String COL_CALIBRATION_INSTRUCTIONS = "calibrationInstructions";

    public static final String COL_MAINTENANCE_REMINDERS = "maintenanceReminders";
    public static final String COL_MAINTENANCE_FREQUENCY = "maintenanceFrequency";
    public static final String COL_LAST_MAINTENANCE_DATE = "lastMaintenanceDate";
    public static final String COL_MAINTENANCE_INSTRUCTIONS = "maintenanceInstructions";

    public static final String COL_CONTRACT_REMINDERS = "contractReminders";
    public static final String COL_CONTRACT_VALID_TILL_DATE = "contractValidTillDate";
    public static final String COL_CONTRACT_INSTRUCTIONS = "contractInstructions";

    public static final String COL_INVENTORY_REMINDERS = "inventoryReminders";
    public static final String COL_MIN_REQUIRED_QUANTITY = "minRequiredQuantity";
    public static final String COL_CURRENT_QUANTITY = "currentQuantity";

    public static final String COL_IMAGE_PATH = "imagePath";

    // Defines related to item type
    public static final long InstrumentType = 1;
    public static final long ConsummableType = 2;
    public static final String InstrumentString = "Instrument";
    public static final String ConsummableString = "Consummable";


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // For database projection so order is consistent
    public static final String[] FIELDS = {
            BaseColumns._ID,
            COL_NAME,
            COL_DESCRIPTION,
            COL_TYPE,

            COL_CALIBRATION_REMINDERS,
            COL_CALIBRATION_FREQUENCY,
            COL_LAST_CALIBRATION_DATE,
            COL_CALIBRATION_INSTRUCTIONS,

            COL_MAINTENANCE_REMINDERS,
            COL_MAINTENANCE_FREQUENCY,
            COL_LAST_MAINTENANCE_DATE,
            COL_MAINTENANCE_INSTRUCTIONS,

            COL_CONTRACT_REMINDERS,
            COL_CONTRACT_VALID_TILL_DATE,
            COL_CONTRACT_INSTRUCTIONS,

            COL_INVENTORY_REMINDERS,
            COL_MIN_REQUIRED_QUANTITY,
            COL_CURRENT_QUANTITY,
            COL_IMAGE_PATH
    };

    public static final HashMap<String, String> mColumnMap = buildColumnMap();
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * Builds a map for all Item FTS table columns that may be requested, which will be given to the
     * SQLiteQueryBuilder. This is a good way to define aliases for column names, but must include
     * all columns, even if the value is the key. This allows the ContentProvider to request
     * columns w/o the need to know real column names and create the alias itself.
     */
    private static HashMap<String,String> buildColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();

        map.put(BaseColumns._ID, BaseColumns._ID);
        map.put(COL_NAME, COL_NAME);
        map.put(COL_DESCRIPTION, COL_DESCRIPTION);
        map.put(COL_TYPE, COL_TYPE);

        map.put(COL_CALIBRATION_REMINDERS, COL_CALIBRATION_REMINDERS);
        map.put(COL_CALIBRATION_FREQUENCY, COL_CALIBRATION_FREQUENCY);
        map.put(COL_LAST_CALIBRATION_DATE, COL_LAST_CALIBRATION_DATE);
        map.put(COL_CALIBRATION_INSTRUCTIONS, COL_CALIBRATION_INSTRUCTIONS);

        map.put(COL_MAINTENANCE_REMINDERS, COL_MAINTENANCE_REMINDERS);
        map.put(COL_MAINTENANCE_FREQUENCY, COL_MAINTENANCE_FREQUENCY);
        map.put(COL_LAST_MAINTENANCE_DATE, COL_LAST_MAINTENANCE_DATE);
        map.put(COL_MAINTENANCE_INSTRUCTIONS, COL_MAINTENANCE_INSTRUCTIONS);

        map.put(COL_CONTRACT_REMINDERS, COL_CONTRACT_REMINDERS);
        map.put(COL_CONTRACT_VALID_TILL_DATE, COL_CONTRACT_VALID_TILL_DATE);
        map.put(COL_CONTRACT_INSTRUCTIONS, COL_CONTRACT_INSTRUCTIONS);

        map.put(COL_INVENTORY_REMINDERS, COL_INVENTORY_REMINDERS);
        map.put(COL_MIN_REQUIRED_QUANTITY, COL_MIN_REQUIRED_QUANTITY);
        map.put(COL_CURRENT_QUANTITY, COL_CURRENT_QUANTITY);
        map.put(COL_IMAGE_PATH, COL_IMAGE_PATH);

        return map;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /*
     * The SQL code that creates a Table for storing items.
     * Note that the last row does NOT end in a comma like the others.
     * This is a common source of error.
     */
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + COL_NAME + " TEXT NOT NULL DEFAULT '',"
                    + COL_DESCRIPTION + " TEXT NOT NULL DEFAULT '',"
                    + COL_TYPE + " INTEGER, "

                    + COL_CALIBRATION_REMINDERS + " INTEGER,"
                    + COL_CALIBRATION_FREQUENCY + " INTEGER,"
                    + COL_LAST_CALIBRATION_DATE + " INTEGER,"
                    + COL_CALIBRATION_INSTRUCTIONS + " TEXT DEFAULT '',"

                    + COL_MAINTENANCE_REMINDERS + " INTEGER,"
                    + COL_MAINTENANCE_FREQUENCY + " INTEGER,"
                    + COL_LAST_MAINTENANCE_DATE + " INTEGER,"
                    + COL_MAINTENANCE_INSTRUCTIONS + " TEXT DEFAULT '',"

                    + COL_CONTRACT_REMINDERS + " INTEGER,"
                    + COL_CONTRACT_VALID_TILL_DATE + " INTEGER,"
                    + COL_CONTRACT_INSTRUCTIONS + " TEXT DEFAULT '',"

                    + COL_INVENTORY_REMINDERS + " INTEGER,"
                    + COL_MIN_REQUIRED_QUANTITY + " INTEGER,"
                    + COL_CURRENT_QUANTITY + " INTEGER,"
                    + COL_IMAGE_PATH + " TEXT DEFAULT ''"

                    + ")";

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Fields corresponding to ItemTable columns
    public long mID = -1;
    public String mName = "";
    public String mDescription = "";
    public long mType = 0;

    public long mCalibrationReminders = 0;
    public long mCalibrationFrequency = 0;
    public long mCalibrationDate = 0;
    public String mCalibrationInstructions = "";

    public long mMaintenanceReminders = 0;
    public long mMaintenanceFrequency = 0;
    public long mMaintenanceDate = 0;
    public String mMaintenanceInstructions = "";

    public long mContractReminders = 0;
    public long mContractValidTillDate = 0;
    public String mContractInstructions = "";

    public long mInventoryReminders = 0;
    public long mMinRequiredQuantity = 0;
    public long mCurrentQuantity = 0;

    public String mImagePath = "";

    /**
     * No need to do anything, fields are already set to default values above
     */
    public Item() {
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * Convert information from the ItemTable into an Item object.
     */
    public void setContentFromCursor(final Cursor cursor) {
        // Indices expected to match order in FIELDS!
        this.mID = cursor.getLong(0);
        this.mName = cursor.getString(1);
        this.mDescription = cursor.getString(2);
        this.mType = cursor.getLong(3);

        this.mCalibrationReminders = cursor.getLong(4);
        this.mCalibrationFrequency = cursor.getLong(5);
        this.mCalibrationDate = cursor.getLong(6);
        this.mCalibrationInstructions = cursor.getString(7);

        this.mMaintenanceReminders = cursor.getLong(8);
        this.mMaintenanceFrequency = cursor.getLong(9);
        this.mMaintenanceDate = cursor.getLong(10);
        this.mMaintenanceInstructions = cursor.getString(11);

        this.mContractReminders = cursor.getLong(12);
        this.mContractValidTillDate = cursor.getLong(13);
        this.mContractInstructions = cursor.getString(14);

        this.mInventoryReminders = cursor.getLong(15);
        this.mMinRequiredQuantity = cursor.getLong(16);
        this.mCurrentQuantity = cursor.getLong(17);

        this.mImagePath = cursor.getString(18);
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * Return the fields in a ContentValues object, suitable for insertion
     * into the database.
     */
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        // Note that ID is NOT included here
        values.put(COL_NAME, mName);
        values.put(COL_DESCRIPTION, mDescription);
        values.put(COL_TYPE, mType);

        values.put(COL_CALIBRATION_REMINDERS, mCalibrationReminders);
        values.put(COL_CALIBRATION_FREQUENCY, mCalibrationFrequency);
        values.put(COL_LAST_CALIBRATION_DATE, mCalibrationDate);
        values.put(COL_CALIBRATION_INSTRUCTIONS, mCalibrationInstructions);

        values.put(COL_MAINTENANCE_REMINDERS, mMaintenanceReminders);
        values.put(COL_MAINTENANCE_FREQUENCY, mMaintenanceFrequency);
        values.put(COL_LAST_MAINTENANCE_DATE, mMaintenanceDate);
        values.put(COL_MAINTENANCE_INSTRUCTIONS, mMaintenanceInstructions);

        values.put(COL_CONTRACT_REMINDERS, mContractReminders);
        values.put(COL_CONTRACT_VALID_TILL_DATE, mContractValidTillDate);
        values.put(COL_CONTRACT_INSTRUCTIONS, mContractInstructions);

        values.put(COL_INVENTORY_REMINDERS, mInventoryReminders);
        values.put(COL_MIN_REQUIRED_QUANTITY, mMinRequiredQuantity);
        values.put(COL_CURRENT_QUANTITY, mCurrentQuantity);

        values.put(COL_IMAGE_PATH, mImagePath);

        return values;
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * sets the fields from a ContentValues object
     */
    public void setContentFromCV(final ContentValues values) {
        // Note that ID is NOT included here
        mName = values.getAsString(COL_NAME);
        mDescription = values.getAsString(COL_DESCRIPTION);
        mType = values.getAsLong(COL_TYPE);

        mCalibrationReminders = values.getAsLong(COL_CALIBRATION_REMINDERS);
        mCalibrationFrequency = values.getAsLong(COL_CALIBRATION_FREQUENCY);
        mCalibrationDate = values.getAsLong(COL_LAST_CALIBRATION_DATE);
        mCalibrationInstructions = values.getAsString(COL_CALIBRATION_INSTRUCTIONS);

        mMaintenanceReminders = values.getAsLong(COL_MAINTENANCE_REMINDERS);
        mMaintenanceFrequency = values.getAsLong(COL_MAINTENANCE_FREQUENCY);
        mMaintenanceDate = values.getAsLong(COL_LAST_MAINTENANCE_DATE);
        mMaintenanceInstructions = values.getAsString(COL_MAINTENANCE_INSTRUCTIONS);

        mContractReminders = values.getAsLong(COL_CONTRACT_REMINDERS);
        mContractValidTillDate = values.getAsLong(COL_CONTRACT_VALID_TILL_DATE);
        mContractInstructions = values.getAsString(COL_CONTRACT_INSTRUCTIONS);

        mInventoryReminders = values.getAsLong(COL_INVENTORY_REMINDERS);
        mMinRequiredQuantity = values.getAsLong(COL_MIN_REQUIRED_QUANTITY);
        mCurrentQuantity = values.getAsLong(COL_CURRENT_QUANTITY);

        mImagePath = values.getAsString(COL_IMAGE_PATH);
    }

    // Item FTS Table
    public static final String FTS_TABLE_NAME = "FTSItemTable";
    public static final String COL_FTS_ITEM_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String COL_FTS_ITEM_DESCRIPTION = SearchManager.SUGGEST_COLUMN_TEXT_2;
    public static final String COL_FTS_ITEM_REALID = "realID";

    // For database projection so order is consistent
    public static final String[] FTS_FIELDS = {
            BaseColumns._ID,
            COL_FTS_ITEM_NAME,
            COL_FTS_ITEM_DESCRIPTION,
            COL_FTS_ITEM_REALID
    };

    /* Note that FTS3 does not support column constraints and thus, you cannot
     * declare a primary key. However, "rowid" is automatically used as a unique
     * identifier, so when making requests, we will use "_id" as an alias for "rowid"
     */
    public static final String CREATE_FTS_TABLE =
            "CREATE VIRTUAL TABLE " + FTS_TABLE_NAME +
                    " USING fts3 (" +
                    COL_FTS_ITEM_NAME + ", " +
                    COL_FTS_ITEM_DESCRIPTION + "," +
                    COL_FTS_ITEM_REALID +
                    ");";

    // Fields corresponding to FTSItemTable columns
    public String mRowID = "";
    public String mFTSName = "";
    public String mFTSDescription = "";
    public String mFTSRealID = "";

    /**
     * Set information from the FTSItemTable into an Item object.
     */
    public void setFTSContent(final Cursor cursor) {
        // Indices expected to match order in FIELDS!
        this.mRowID = cursor.getString(0);
        this.mFTSName = cursor.getString(1);
        this.mFTSDescription = cursor.getString(2);
        this.mFTSRealID = cursor.getString(3);
    }

    public static final HashMap<String, String> mFTSColumnMap = buildFTSColumnMap();
    /**
     * Builds a map for all Item FTS table columns that may be requested, which will be given to the
     * SQLiteQueryBuilder. This is a good way to define aliases for column names, but must include
     * all columns, even if the value is the key. This allows the ContentProvider to request
     * columns w/o the need to know real column names and create the alias itself.
     */
    private static HashMap<String,String> buildFTSColumnMap() {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put(COL_FTS_ITEM_NAME, COL_FTS_ITEM_NAME);
        map.put(COL_FTS_ITEM_DESCRIPTION, COL_FTS_ITEM_DESCRIPTION);
        map.put(COL_FTS_ITEM_REALID, COL_FTS_ITEM_REALID);
        map.put(BaseColumns._ID, "rowid AS " +
                BaseColumns._ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }

}
