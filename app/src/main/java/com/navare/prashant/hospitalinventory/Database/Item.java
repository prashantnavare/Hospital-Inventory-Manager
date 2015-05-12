package com.navare.prashant.hospitalinventory.Database;

/**
 * Created by prashant on 18-Apr-15.
 */

import android.app.SearchManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * A class representation of a row in table "Item".
 */

public class Item {

    // Item table
    public static final String TABLE_NAME = "ItemTable";
    // These fields can be anything you want.
    public static final String COL_NAME = "name";
    public static final String COL_DESCRIPTION = "description";
    // TODO: Add other fields here.
    // TODO: When you add a field, make sure you add corresponding entries in the FIELDS array, the CREATE_TABLE string
    // TODO: and declare a member and so on. See below.

    // For database projection so order is consistent
    public static final String[] FIELDS = {
            BaseColumns._ID,
            COL_NAME,
            COL_DESCRIPTION
    };

    public static final HashMap<String, String> mColumnMap = buildColumnMap();
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
        return map;
    }
    /*
     * The SQL code that creates a Table for storing items.
     * Note that the last row does NOT end in a comma like the others.
     * This is a common source of error.
     */
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + COL_NAME + " TEXT NOT NULL DEFAULT '',"
                    + COL_DESCRIPTION + " TEXT NOT NULL DEFAULT ''"
                    + ")";

    // Fields corresponding to ItemTable columns
    public long mID = -1;
    public String mName = "";
    public String mDescription = "";

    /**
     * No need to do anything, fields are already set to default values above
     */
    public Item() {
    }

    /**
     * Convert information from the ItemTable into an Item object.
     */
    public void setContentFromCursor(final Cursor cursor) {
        // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Indices expected to match order in FIELDS!
        this.mID = cursor.getLong(0);
        this.mName = cursor.getString(1);
        this.mDescription = cursor.getString(2);
    }

    /**
     * Return the fields in a ContentValues object, suitable for insertion
     * into the database.
     */
    public ContentValues getContentValues() {
        // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        final ContentValues values = new ContentValues();
        // Note that ID is NOT included here
        values.put(COL_NAME, mName);
        values.put(COL_DESCRIPTION, mDescription);

        return values;
    }

    /**
     * sets the fields from a ContentValues object
     */
    public ContentValues setContentFromCV(final ContentValues values) {
        // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Note that ID is NOT included here
        mName = values.getAsString(COL_NAME);
        mDescription = values.getAsString(COL_DESCRIPTION);

        return values;
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
