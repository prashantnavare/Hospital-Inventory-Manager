package com.navare.prashant.hospitalinventory.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.navare.prashant.hospitalinventory.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InventoryDatabase extends SQLiteOpenHelper {
    private static final String TAG = "InventoryDatabase";
    private static final String DATABASE_NAME = "HospitalInventory";
    private static final int DATABASE_VERSION = 1;

    private final Context mHelperContext;

    /**
     * Constructor
     * @param context The Context within which to work, used to create the DB
     */
    InventoryDatabase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mHelperContext = context;
    }

    // TODO: ==========================When new table is added =========================================================
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Item.CREATE_TABLE);
        db.execSQL(Item.CREATE_FTS_TABLE);
        db.execSQL(ServiceCall.CREATE_TABLE);
        db.execSQL(Task.CREATE_TABLE);
        db.execSQL(Task.CREATE_FTS_TABLE);

        // PNTODO: Delete this eventually
        loadInventory();
        loadTasksTable();
    }

    /**
     * Starts a thread to load the database table with pre-defined inventory
     */
    private void loadInventory() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadItems();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * Starts a thread to load the database table with pre-defined tasks
     */
    private void loadTasksTable() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    loadTasks();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void loadItems() throws IOException {
        Log.d(TAG, "Loading items...");
        final Resources resources = mHelperContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.items);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strings = TextUtils.split(line, "-");
                if (strings.length < 2) continue;
                Item item = new Item();
                item.mName = strings[0].trim();
                item.mDescription = strings[1].trim();
                String type = strings[2].trim();
                if (type.compareToIgnoreCase("Instrument")== 0)
                    item.mType = Item.InstrumentType;
                else if (type.compareToIgnoreCase("Consummable")==0)
                    item.mType = Item.ConsummableType;
                long newID = addItem(item);
                if (newID == -1) {
                    Log.e(TAG, "unable to add item: " + strings[0].trim());
                }
            }
        } finally {
            reader.close();
        }
        Log.d(TAG, "DONE loading items.");
    }

    private void loadTasks() throws IOException {
        Log.d(TAG, "Loading tasks...");
        final Resources resources = mHelperContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.tasks);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strings = TextUtils.split(line, "-");
                if (strings.length < 7)
                    continue;
                Task task = new Task();
                task.mTaskType = Long.valueOf(strings[0].trim());
                task.mItemID = Long.valueOf(strings[1].trim());
                task.mItemName = strings[2].trim();
                task.mStatus = Long.valueOf(strings[3].trim());
                task.mAssignedTo = strings[4].trim();
                task.mAssignedToContact = strings[5].trim();
                task.mDueDate = Long.valueOf(strings[6].trim());
                task.mPriority = Long.valueOf(strings[7].trim());
                long newID = addTask(task);
                if (newID == -1) {
                    Log.e(TAG, "unable to add task: " + strings[0].trim());
                }
            }
        } finally {
            reader.close();
        }
        Log.d(TAG, "DONE loading tasks.");
    }

    /**
     * Add an item.
     * @return rowId or -1 if failed
     */
    public long addItem(Item item) {
        final SQLiteDatabase db = this.getWritableDatabase();
        long realID = db.insert(Item.TABLE_NAME, null, item.getContentValues());
        if (realID > -1) {
            // Also add an entry to the Item FTS table
            ContentValues ftsValues = new ContentValues();
            ftsValues.put(Item.COL_FTS_ITEM_NAME, item.mName);
            ftsValues.put(Item.COL_FTS_ITEM_DESCRIPTION, item.mDescription);
            ftsValues.put(Item.COL_FTS_ITEM_REALID, Long.toString(realID));

            long ftsID =  db.insert(Item.FTS_TABLE_NAME, null, ftsValues);
            if (ftsID == -1) {
                deleteItem(String.valueOf(realID));
                return ftsID;
            }
        }
        return realID;
    }

    public int deleteItem(String itemID) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final int result = db.delete(Item.TABLE_NAME,
                BaseColumns._ID + " IS ?",
                new String[]{itemID});

        if (result > 0) {
            int ftsResult = db.delete(Item.FTS_TABLE_NAME,
                    Item.COL_FTS_ITEM_REALID + " MATCH ? ", new String[]{itemID});
            notifyProviderOnItemChange();
            return ftsResult;
        }
        return result;
    }

    private void notifyProviderOnItemChange() {
        mHelperContext.getContentResolver().notifyChange(
                HospitalInventoryContentProvider.FTS_ITEM_URI, null, false);
    }

    /**
     * Returns a Cursor positioned at the item specified by id
     *
     * @param rowID id of item to retrieve
     * @param columns The columns to include, if null then all are included
     * @return Cursor positioned to matching item, or null if not found.
     */
    public Cursor getItem(String rowID, String[] columns) {
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = new String[] {rowID};

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE _id = <rowID>
         */
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Item.TABLE_NAME);
        builder.setProjectionMap(Item.mColumnMap);

        Cursor cursor = builder.query(this.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        }
        else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    /**
     * Returns a Cursor over all FTS items
     *
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all items that match, or null if none found.
     */
    public Cursor getAllFTSItems(String[] columns) {

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table>
         */
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Item.FTS_TABLE_NAME);
        builder.setProjectionMap(Item.mFTSColumnMap);

        Cursor cursor = builder.query(this.getReadableDatabase(),
                columns, null, null, null, null, Item.COL_FTS_ITEM_NAME);

        if (cursor == null) {
            return null;
        }
        else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
    /**
     * Returns a Cursor over all FTS items that match the given searchString
     *
     * @param searchString The string to search for
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all items that match, or null if none found.
     */
    public Cursor getFTSItemMatches(String searchString, String[] columns) {
        //String selection = Item.COL_FTS_ITEM_NAME + " MATCH ?";
        String selection = Item.FTS_TABLE_NAME + " MATCH ?";
        String[] selectionArgs = new String[] {searchString + "*"};

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE <COL_FTS_ITEM_NAME> MATCH 'query*'
         * which is an FTS3 search for the query text (plus a wildcard) inside the item_name column.
         *
         * - "rowid" is the unique id for all rows but we need this value for the "_id" column in
         *    order for the Adapters to work, so the columns need to make "_id" an alias for "rowid"
         * - "rowid" also needs to be used by the SUGGEST_COLUMN_INTENT_DATA alias in order
         *   for suggestions to carry the proper intent data.
         *   These aliases are defined in the InventoryProvider when queries are made.
         * - This can be revised to also search the item description text with FTS3 by changing
         *   the selection clause to use FTS_ITEM_TABLE instead of COL_FTS_ITEM_NAME (to search across
         *   the entire table, but sorting the relevance could be difficult.
         */
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Item.FTS_TABLE_NAME);
        builder.setProjectionMap(Item.mFTSColumnMap);

        Cursor cursor = builder.query(this.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, Item.COL_FTS_ITEM_NAME);

        if (cursor == null) {
            return null;
        }
        else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public long insertItem(ContentValues values) {
        Item item = new Item();
        item.setContentFromCV(values);
        return addItem(item);
    }

    public int updateItem(String itemId, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = this.getWritableDatabase();
        int rowsUpdated = db.update(Item.TABLE_NAME, values, BaseColumns._ID + "=" + itemId, null);
        if (rowsUpdated > 0) {
            ContentValues ftsValues = new ContentValues();
            ftsValues.put(Item.COL_FTS_ITEM_NAME, values.getAsString(Item.COL_NAME));
            ftsValues.put(Item.COL_FTS_ITEM_DESCRIPTION, values.getAsString(Item.COL_DESCRIPTION));

            long ftsRowsUpdated =  db.update(Item.FTS_TABLE_NAME, ftsValues, Item.COL_FTS_ITEM_REALID + " MATCH " + itemId, null);
        }
        notifyProviderOnItemChange();
        return rowsUpdated;
    }

    public long insertServiceCall(ContentValues values) {
        final SQLiteDatabase db = this.getWritableDatabase();
        long realID = db.insert(ServiceCall.TABLE_NAME, null, values);
        return realID;
    }

    /**
     * Add a task.
     * @return rowId or -1 if failed
     */
    public long addTask(Task task) {
        final SQLiteDatabase db = this.getWritableDatabase();
        long realID = db.insert(Task.TABLE_NAME, null, task.getContentValues());
        if (realID > -1) {
            // Also add an entry to the Task FTS table
            ContentValues ftsValues = new ContentValues();
            ftsValues.put(Task.COL_FTS_ITEM_NAME, task.mItemName);
            ftsValues.put(Task.COL_FTS_TASK_TYPE, task.getTaskType());
            ftsValues.put(Task.COL_FTS_ASSIGNED_TO, task.mAssignedTo);

            if (task.mDueDate > 0) {
                Date dueDate = new Date();
                dueDate.setTime(task.mDueDate);

                SimpleDateFormat dueDateFormat = new SimpleDateFormat("dd MMMM, yyyy");
                String dueDateString = dueDateFormat.format(dueDate);
                ftsValues.put(Task.COL_FTS_DUE_DATE, dueDateString);
            }
            ftsValues.put(Task.COL_FTS_TASK_REALID, Long.toString(realID));
            ftsValues.put(Task.COL_FTS_TASK_PRIORITY, task.getTaskPriority());

            long ftsID =  db.insert(Task.FTS_TABLE_NAME, null, ftsValues);
            if (ftsID == -1) {
                deleteTask(String.valueOf(realID));
                return ftsID;
            }
        }
        return realID;
    }

    public int deleteTask(String taskID) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final int result = db.delete(Task.TABLE_NAME,
                BaseColumns._ID + " IS ?",
                new String[]{taskID});

        if (result > 0) {
            int ftsResult = db.delete(Task.FTS_TABLE_NAME,
                    Task.COL_FTS_TASK_REALID + " MATCH ? ", new String[]{taskID});
            notifyProviderOnTaskChange();
            return ftsResult;
        }
        return result;
    }

    private void notifyProviderOnTaskChange() {
        mHelperContext.getContentResolver().notifyChange(
                HospitalInventoryContentProvider.FTS_TASK_URI, null, false);
    }

    /**
     * Returns a Cursor positioned at the task specified by id
     *
     * @param rowID id of task to retrieve
     * @param columns The columns to include, if null then all are included
     * @return Cursor positioned to matching item, or null if not found.
     */
    public Cursor getTask(String rowID, String[] columns) {
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = new String[] {rowID};

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE _id = <rowID>
         */
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Task.TABLE_NAME);
        builder.setProjectionMap(Task.mColumnMap);

        Cursor cursor = builder.query(this.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        }
        else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public Cursor getServiceCall(String rowID, String[] columns) {
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = new String[] {rowID};

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE _id = <rowID>
         */
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(ServiceCall.TABLE_NAME);
        builder.setProjectionMap(ServiceCall.mColumnMap);

        Cursor cursor = builder.query(this.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        }
        else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    /**
     * Returns a Cursor over all FTS tasks
     *
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all items that match, or null if none found.
     */
    public Cursor getAllFTSTasks(String[] columns) {

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table>
         */
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Task.FTS_TABLE_NAME);
        builder.setProjectionMap(Task.mFTSColumnMap);

        Cursor cursor = builder.query(this.getReadableDatabase(),
                columns, null, null, null, null, Item.COL_FTS_ITEM_NAME);

        if (cursor == null) {
            return null;
        }
        else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
    /**
     * Returns a Cursor over all FTS tasks that match the given searchString
     *
     * @param searchString The string to search for
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all items that match, or null if none found.
     */
    public Cursor getFTSTaskMatches(String searchString, String[] columns) {
        //String selection = Item.COL_FTS_ITEM_NAME + " MATCH ?";
        String selection = Task.FTS_TABLE_NAME + " MATCH ?";
        String[] selectionArgs = new String[] {searchString + "*"};

        /* This builds a query that looks like:
         *     SELECT <columns> FROM <table> WHERE <COL_FTS_ITEM_NAME> MATCH 'query*'
         * which is an FTS3 search for the query text (plus a wildcard) inside the item_name column.
         *
         * - "rowid" is the unique id for all rows but we need this value for the "_id" column in
         *    order for the Adapters to work, so the columns need to make "_id" an alias for "rowid"
         * - "rowid" also needs to be used by the SUGGEST_COLUMN_INTENT_DATA alias in order
         *   for suggestions to carry the proper intent data.
         *   These aliases are defined in the InventoryProvider when queries are made.
         * - This can be revised to also search the item description text with FTS3 by changing
         *   the selection clause to use FTS_ITEM_TABLE instead of COL_FTS_ITEM_NAME (to search across
         *   the entire table, but sorting the relevance could be difficult.
         */
        /* The SQLiteBuilder provides a map for all possible columns requested to
         * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Task.FTS_TABLE_NAME);
        builder.setProjectionMap(Task.mFTSColumnMap);

        Cursor cursor = builder.query(this.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, Task.COL_FTS_ITEM_NAME);

        if (cursor == null) {
            return null;
        }
        else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public long insertTask(ContentValues values) {
        Task task = new Task();
        task.setContentFromCV(values);
        return addTask(task);
    }

    public int updateTask(String taskId, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = this.getWritableDatabase();
        int rowsUpdated = db.update(Task.TABLE_NAME, values, BaseColumns._ID + "=" + taskId, null);
        if (rowsUpdated > 0) {
            ContentValues ftsValues = new ContentValues();
            Task task = new Task();
            task.setContentFromCV(values);
            ftsValues.put(Task.COL_FTS_ASSIGNED_TO, values.getAsString(Task.COL_ASSIGNED_TO));
            ftsValues.put(Task.COL_FTS_TASK_PRIORITY, task.getTaskPriority());

            long ftsRowsUpdated =  db.update(Task.FTS_TABLE_NAME, ftsValues, Task.COL_FTS_TASK_REALID + " MATCH " + taskId, null);
        }
        notifyProviderOnTaskChange();
        return rowsUpdated;
    }

    public void completeTask(String taskId) {
        // This task has been completed. Delete it from the FTS table of open tasks
        final SQLiteDatabase db = this.getWritableDatabase();
        int ftsResult = db.delete(Task.FTS_TABLE_NAME,
                Task.COL_FTS_TASK_REALID + " MATCH ? ", new String[]{taskId});
        notifyProviderOnTaskChange();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + Item.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Item.FTS_TABLE_NAME);
        onCreate(db);
    }

}
