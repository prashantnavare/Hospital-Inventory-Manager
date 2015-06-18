package com.navare.prashant.hospitalinventory.Database;

import android.app.SearchManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import com.navare.prashant.hospitalinventory.R;

import java.util.HashMap;

/**
 * A class representation of a row in table "Task".
 */

public class Task {

    // Task table
    public static final String TABLE_NAME = "TaskTable";
    // TODO: Add other fields here.
    // TODO: When you add a field, make sure you add corresponding entries in the FIELDS array, the CREATE_TABLE string
    // TODO: and declare a member and so on. See all ++++++++ comment lines below.
    // These fields can be anything you want.
    public static final String COL_TASK_TYPE = "taskType";
    public static final String COL_ITEM_ID = "itemID";
    public static final String COL_ITEM_NAME = "itemName";
    public static final String COL_STATUS = "status";
    public static final String COL_ASSIGNED_TO = "assignedTo";
    public static final String COL_ASSIGNED_TO_CONTACT = "assignedToContact";
    public static final String COL_DUE_DATE = "dueDate";
    public static final String COL_COMPLETED_TIME_STAMP = "completedTimeStamp";
    public static final String COL_COMPLETION_COMMENTS = "completionComments";

    public static final int Calibration = 1;
    public static final int Contract = 2;
    public static final int Inventory = 3;
    public static final int Maintenance = 4;
    public static final int ServiceCall = 5;

    public static final long OpenStatus = 1;
    public static final long CompletedStatus = 2;

    // For database projection so order is consistent
    public static final String[] FIELDS = {
            BaseColumns._ID,
            COL_TASK_TYPE,
            COL_ITEM_ID,
            COL_ITEM_NAME,
            COL_STATUS,
            COL_ASSIGNED_TO,
            COL_ASSIGNED_TO_CONTACT,
            COL_DUE_DATE,
            COL_COMPLETED_TIME_STAMP,
            COL_COMPLETION_COMMENTS
    };

    public static final HashMap<String, String> mColumnMap = buildColumnMap();
    private static HashMap<String,String> buildColumnMap() {

        HashMap<String,String> map = new HashMap<String,String>();

        map.put(BaseColumns._ID, BaseColumns._ID);
        map.put(COL_TASK_TYPE, COL_TASK_TYPE);
        map.put(COL_ITEM_ID, COL_ITEM_ID);
        map.put(COL_ITEM_NAME, COL_ITEM_NAME);
        map.put(COL_STATUS, COL_STATUS);
        map.put(COL_ASSIGNED_TO, COL_ASSIGNED_TO);
        map.put(COL_ASSIGNED_TO_CONTACT, COL_ASSIGNED_TO_CONTACT);
        map.put(COL_DUE_DATE, COL_DUE_DATE);
        map.put(COL_COMPLETED_TIME_STAMP, COL_COMPLETED_TIME_STAMP);
        map.put(COL_COMPLETION_COMMENTS, COL_COMPLETION_COMMENTS);

        return map;
    }

    /*
     * The SQL code that creates a Table for storing service calls.
     * Note that the last row does NOT end in a comma like the others.
     * This is a common source of error.
     */
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + COL_TASK_TYPE + " INTEGER,"
                    + COL_ITEM_ID + " INTEGER,"
                    + COL_ITEM_NAME + " TEXT NOT NULL DEFAULT '',"
                    + COL_STATUS + " INTEGER,"
                    + COL_ASSIGNED_TO + " TEXT NOT NULL DEFAULT '',"
                    + COL_ASSIGNED_TO_CONTACT + " TEXT NOT NULL DEFAULT '',"
                    + COL_DUE_DATE + " INTEGER,"
                    + COL_COMPLETED_TIME_STAMP + " INTEGER,"
                    + COL_COMPLETION_COMMENTS + " TEXT NOT NULL DEFAULT '' "
                    + ")";

    // Fields corresponding to ServiceCallTable columns
    public long mID = -1;
    public long mTaskType = -1;
    public long mItemID = -1;
    public String mItemName = "";
    public long mStatus = 0;
    public String mAssignedTo = "";
    public String mAssignedToContact = "";
    public long mDueDate = 0;
    public long mCompletedTimeStamp = 0;
    public String mCompletionComments = "";

    /**
     * No need to do anything, fields are already set to default values above
     */
    public Task() {
    }

    /**
     * Convert information from the ItemTable into an Item object.
     */
    public void setContentFromCursor(final Cursor cursor) {
        // Indices expected to match order in FIELDS!
        this.mID = cursor.getLong(0);
        this.mTaskType = cursor.getLong(1);
        this.mItemID = cursor.getLong(2);
        this.mItemName = cursor.getString(3);
        this.mStatus = cursor.getLong(4);
        this.mAssignedTo = cursor.getString(5);
        this.mAssignedToContact = cursor.getString(6);
        this.mDueDate = cursor.getLong(7);
        this.mCompletedTimeStamp = cursor.getLong(8);
        this.mCompletionComments = cursor.getString(9);
    }

    /**
     * Return the fields in a ContentValues object, suitable for insertion
     * into the database.
     */
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();

        // Note that ID is NOT included here
        values.put(COL_TASK_TYPE, mTaskType);
        values.put(COL_ITEM_ID, mItemID);
        values.put(COL_ITEM_NAME, mItemName);
        values.put(COL_STATUS, mStatus);
        values.put(COL_ASSIGNED_TO, mAssignedTo);
        values.put(COL_ASSIGNED_TO_CONTACT, mAssignedToContact);
        values.put(COL_DUE_DATE, mDueDate);
        values.put(COL_COMPLETED_TIME_STAMP, mCompletedTimeStamp);
        values.put(COL_COMPLETION_COMMENTS, mCompletionComments);

        return values;
    }

    /**
     * sets the fields from a ContentValues object
     */
    public void setContentFromCV(final ContentValues values) {
        // Note that ID is NOT included here
        mTaskType = values.getAsLong(COL_TASK_TYPE);
        mItemID = values.getAsLong(COL_ITEM_ID);
        mItemName = values.getAsString(COL_ITEM_NAME);
        mStatus = values.getAsLong(COL_STATUS);
        mAssignedTo = values.getAsString(COL_ASSIGNED_TO);
        mAssignedToContact = values.getAsString(COL_ASSIGNED_TO_CONTACT);
        mDueDate= values.getAsLong(COL_DUE_DATE);
        mCompletedTimeStamp= values.getAsLong(COL_COMPLETED_TIME_STAMP);
        mCompletionComments = values.getAsString(COL_COMPLETION_COMMENTS);
    }
    // Task FTS Table
    public static final String FTS_TABLE_NAME = "FTSTaskTable";
    public static final String COL_FTS_ITEM_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String COL_FTS_TASK_TYPE = SearchManager.SUGGEST_COLUMN_TEXT_2;
    public static final String COL_FTS_ASSIGNED_TO = "assignedTo";
    public static final String COL_FTS_DUE_DATE = "ftsDueDate";
    public static final String COL_FTS_TASK_REALID = "realID";

    // For database projection so order is consistent
    public static final String[] FTS_FIELDS = {
            BaseColumns._ID,
            COL_FTS_ITEM_NAME,
            COL_FTS_TASK_TYPE,
            COL_FTS_ASSIGNED_TO,
            COL_FTS_DUE_DATE,
            COL_FTS_TASK_REALID
    };

    /* Note that FTS3 does not support column constraints and thus, you cannot
     * declare a primary key. However, "rowid" is automatically used as a unique
     * identifier, so when making requests, we will use "_id" as an alias for "rowid"
     */
    public static final String CREATE_FTS_TABLE =
            "CREATE VIRTUAL TABLE " + FTS_TABLE_NAME +
                    " USING fts3 (" +
                    COL_FTS_ITEM_NAME + ", " +
                    COL_FTS_TASK_TYPE + "," +
                    COL_FTS_ASSIGNED_TO + "," +
                    COL_FTS_DUE_DATE + "," +
                    COL_FTS_TASK_REALID +
                    ");";

    // Fields corresponding to FTSItemTable columns
    public String mRowID = "";
    public String mFTSItemName = "";
    public String mFTSTaskType = "";
    public String mFTSAssignedTo = "";
    public String mFTSDueDate = "";
    public String mFTSRealID = "";

    /**
     * Set information from the FTSItemTable into an Item object.
     */
    public void setFTSContent(final Cursor cursor) {
        // Indices expected to match order in FIELDS!
        this.mRowID = cursor.getString(0);
        this.mFTSItemName = cursor.getString(1);
        this.mFTSTaskType = cursor.getString(2);
        this.mFTSAssignedTo = cursor.getString(3);
        this.mFTSDueDate = cursor.getString(4);
        this.mFTSRealID = cursor.getString(5);
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
        map.put(COL_FTS_TASK_TYPE, COL_FTS_TASK_TYPE);
        map.put(COL_FTS_ASSIGNED_TO, COL_FTS_ASSIGNED_TO);
        map.put(COL_FTS_DUE_DATE, COL_FTS_DUE_DATE);
        map.put(COL_FTS_TASK_REALID, COL_FTS_TASK_REALID);
        map.put(BaseColumns._ID, "rowid AS " +
                BaseColumns._ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
                SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
        return map;
    }

    public String getTaskType() {
        switch ((int)mTaskType) {
            case Calibration:
                return "Calibration";
            case Contract:
                return "Contract Renewal";
            case Inventory:
                return "Inventory";
            case Maintenance:
                return "Maintenance";
            case ServiceCall:
                return "Service Call";
        }
        return "Unknown";
    }
}
