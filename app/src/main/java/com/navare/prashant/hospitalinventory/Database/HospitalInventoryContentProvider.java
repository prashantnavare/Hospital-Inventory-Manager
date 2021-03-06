package com.navare.prashant.hospitalinventory.Database;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class HospitalInventoryContentProvider extends ContentProvider {

    private static String SCHEME = "content://";
    private static String PROVIDER_NAME = "com.navare.prashant.HospitalInventory.provider";

    // FTS Items related
    private static final String FTS_ITEMS_SUB_SCHEME = "/fts_items";
    private static final String FTS_ITEM_URL = SCHEME + PROVIDER_NAME + FTS_ITEMS_SUB_SCHEME;
    public static final Uri FTS_ITEM_URI = Uri.parse(FTS_ITEM_URL);
    // UriMatcher stuff
    private static final int SEARCH_FTS_ITEMS = 1;
    private static final int SEARCH_SUGGEST_ITEMS = 2;

    // Actual ItemTable related
    // get_item related
    private static final String ITEM_SUB_SCHEME = "/item";
    private static final String ITEM_URL = SCHEME + PROVIDER_NAME + ITEM_SUB_SCHEME;
    public static final Uri ITEM_URI = Uri.parse(ITEM_URL);
    // UriMatcher stuff
    private static final int ITEMS = 3;
    private static final int ITEM_ID = 4;

    // ServiceCall Table related
    private static final String SERVICE_CALL_SUB_SCHEME = "/service_call";
    private static final String SERVICE_CALL_URL = SCHEME + PROVIDER_NAME + SERVICE_CALL_SUB_SCHEME;
    public static final Uri SERVICE_CALL_URI = Uri.parse(SERVICE_CALL_URL);
    // UriMatcher stuff
    private static final int SERVICE_CALLS = 5;
    private static final int SERVICE_CALL_ID = 6;

    // FTS Tasks related
    private static final String FTS_TASKS_SUB_SCHEME = "/fts_tasks";
    private static final String FTS_TASK_URL = SCHEME + PROVIDER_NAME + FTS_TASKS_SUB_SCHEME;
    public static final Uri FTS_TASK_URI = Uri.parse(FTS_TASK_URL);
    // UriMatcher stuff
    private static final int SEARCH_FTS_TASKS = 7;
    private static final int SEARCH_SUGGEST_TASKS = 8;

    // Actual TaskTable related
    // get_task related
    private static final String TASK_SUB_SCHEME = "/task";
    private static final String TASK_URL = SCHEME + PROVIDER_NAME + TASK_SUB_SCHEME;
    public static final Uri TASK_URI = Uri.parse(TASK_URL);
    // UriMatcher stuff
    private static final int TASKS = 9;
    private static final int TASK_ID = 10;

    // computeNewTasks related
    private static final String COMPUTE_NEW_TASKS_SUB_SCHEME = "/computeNewTasks";
    private static final String COMPUTE_NEW_TASKS_URL = SCHEME + PROVIDER_NAME + TASK_SUB_SCHEME;
    public static final Uri COMPUTE_NEW_TASKS_URI = Uri.parse(COMPUTE_NEW_TASKS_URL);
    // UriMatcher stuff
    private static final int COMPUTE_NEW_TASKS = 11;

    // Completed FTS Tasks related
    private static final String COMPLETED_FTS_TASKS_SUB_SCHEME = "/completed_fts_tasks";
    private static final String COMPLETED_FTS_TASK_URL = SCHEME + PROVIDER_NAME + COMPLETED_FTS_TASKS_SUB_SCHEME;
    public static final Uri COMPLETED_FTS_TASK_URI = Uri.parse(COMPLETED_FTS_TASK_URL);
    // UriMatcher stuff
    private static final int SEARCH_COMPLETED_FTS_TASKS = 12;

    private static final UriMatcher mURIMatcher = buildUriMatcher();

    /**
     * Builds up a UriMatcher for various queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);

        // to get FTS items...
        matcher.addURI(PROVIDER_NAME, FTS_ITEMS_SUB_SCHEME, SEARCH_FTS_ITEMS);

        // for item
        matcher.addURI(PROVIDER_NAME, ITEM_SUB_SCHEME , ITEMS);
        matcher.addURI(PROVIDER_NAME, ITEM_SUB_SCHEME + "/#", ITEM_ID);

        // for serviceCall
        matcher.addURI(PROVIDER_NAME, SERVICE_CALL_SUB_SCHEME , SERVICE_CALLS);
        matcher.addURI(PROVIDER_NAME, SERVICE_CALL_SUB_SCHEME + "/#", SERVICE_CALL_ID);

        // to get FTS tasks...
        matcher.addURI(PROVIDER_NAME, FTS_TASKS_SUB_SCHEME, SEARCH_FTS_TASKS);

        // to get Completed FTS tasks...
        matcher.addURI(PROVIDER_NAME, COMPLETED_FTS_TASKS_SUB_SCHEME, SEARCH_COMPLETED_FTS_TASKS);

        // for task
        matcher.addURI(PROVIDER_NAME, TASK_SUB_SCHEME , TASKS);
        matcher.addURI(PROVIDER_NAME, TASK_SUB_SCHEME + "/#", TASK_ID);

        // for compute new tasks
        matcher.addURI(PROVIDER_NAME, COMPUTE_NEW_TASKS_SUB_SCHEME , COMPUTE_NEW_TASKS);

        // to get suggestions...
        matcher.addURI(PROVIDER_NAME, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST_ITEMS);
        matcher.addURI(PROVIDER_NAME, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST_ITEMS);

        return matcher;
    }

    // MIME types used for searching items or looking up a single item
    private static final String ITEMS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
            "/vnd.navare.prashant.HospitalInventory.Item";
    private static final String ITEM_DEFINITION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
            "/vnd.navare.prashant.HospitalInventory.Item";

    private InventoryDatabase mInventoryDB;

    public HospitalInventoryContentProvider() {
    }

    @Override
    public boolean onCreate() {
        mInventoryDB = new InventoryDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor resultCursor = null;
        // Use the UriMatcher to see what kind of query we have and format the db query accordingly
        switch (mURIMatcher.match(uri)) {
            case SEARCH_FTS_ITEMS:
                if (selectionArgs == null) {
                    resultCursor = getAllFTSItems();
                }
                else {
                    resultCursor =  searchFTSItems(selectionArgs[0]);
                }
                break;
            case ITEM_ID:
                resultCursor =  getItem(uri);
                break;
            case SEARCH_SUGGEST_ITEMS:
                if (selectionArgs == null) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the Uri: " + uri);
                }
                resultCursor = getSuggestionsFTSForItems(selectionArgs[0]);
                break;
            case SEARCH_FTS_TASKS:
                if (selectionArgs == null) {
                    resultCursor = getAllFTSTasks();
                }
                else {
                    resultCursor =  searchFTSTasks(selectionArgs[0]);
                }
                break;
            case SEARCH_COMPLETED_FTS_TASKS:
                resultCursor =  searchCompletedFTSTasks(selectionArgs[0], selectionArgs[1]);
                break;
            case TASK_ID:
                resultCursor =  getTask(uri);
                break;
            case SERVICE_CALL_ID:
                resultCursor =  getServiceCall(uri);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        if (resultCursor != null)
            resultCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return resultCursor;
    }

    private Cursor getAllFTSItems() {
        return mInventoryDB.getAllFTSItems(Item.FTS_FIELDS);
    }

    private Cursor searchFTSItems(String query) {
        query = query.toLowerCase();
        return mInventoryDB.getFTSItemMatches(query, Item.FTS_FIELDS);
    }

    private Cursor getItem(Uri uri) {
        String rowId = uri.getLastPathSegment();
        return mInventoryDB.getItem(rowId, Item.FIELDS);
    }

    private Cursor getSuggestionsFTSForItems(String query) {
        query = query.toLowerCase();
        return mInventoryDB.getFTSItemMatches(query, Item.FTS_FIELDS);
    }

    private Cursor getAllFTSTasks() {
        return mInventoryDB.getAllFTSTasks(Task.FTS_FIELDS);
    }

    private Cursor searchFTSTasks(String query) {
        query = query.toLowerCase();
        return mInventoryDB.getFTSTaskMatches(query, Task.FTS_FIELDS);
    }

    private Cursor searchCompletedFTSTasks(String itemID, String query) {
        query = query.toLowerCase();
        return mInventoryDB.getCompletedFTSTaskMatches(itemID, query, Task.COMPLETED_FTS_FIELDS);
    }

    private Cursor getTask(Uri uri) {
        String rowId = uri.getLastPathSegment();
        return mInventoryDB.getTask(rowId, Task.FIELDS);
    }

    private Cursor getServiceCall(Uri uri) {
        String rowId = uri.getLastPathSegment();
        return mInventoryDB.getServiceCall(rowId, ServiceCall.FIELDS);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (mURIMatcher.match(uri)) {
            case ITEM_ID:
                return deleteItem(uri);
            case TASK_ID:
                return deleteTask(uri);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    private int deleteItem(Uri uri) {
        String rowId = uri.getLastPathSegment();
        int rowsDeleted = mInventoryDB.deleteItem(rowId);
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(FTS_ITEM_URI, null);
        }
        return rowsDeleted;
    }

    private int deleteTask(Uri uri) {
        String rowId = uri.getLastPathSegment();
        int rowsDeleted = mInventoryDB.deleteTask(rowId);
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(FTS_TASK_URI, null);
        }
        return rowsDeleted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        switch (mURIMatcher.match(uri)) {
            case ITEMS:
                return insertItem(values);
            case SERVICE_CALLS:
                return insertServiceCall(values);
            case TASKS:
                return insertTask(values);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    private Uri insertItem(ContentValues values) {

         // Add a new item
        long rowID = mInventoryDB.insertItem(values);
        // If record is added successfully
        if (rowID > 0)
        {
            Uri newItemUri = ContentUris.withAppendedId(ITEM_URI, rowID);
            getContext().getContentResolver().notifyChange(FTS_ITEM_URI, null);
            return newItemUri;
        }
        return null;
    }

    private Uri insertServiceCall(ContentValues values) {

        // Add a new service call
        long rowID = mInventoryDB.insertServiceCall(values);
        // If record is added successfully
        if (rowID > 0)
        {
            return ContentUris.withAppendedId(SERVICE_CALL_URI, rowID);
        }
        return null;
    }

    private Uri insertTask(ContentValues values) {

        // Add a new task
        long rowID = mInventoryDB.insertTask(values);
        // If record is added successfully
        if (rowID > 0)
        {
            Uri newTaskUri = ContentUris.withAppendedId(TASK_URI, rowID);
            getContext().getContentResolver().notifyChange(FTS_TASK_URI, null);
            return newTaskUri;
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        switch (mURIMatcher.match(uri)) {
            case ITEM_ID:
                return updateItem(uri, values, selection, selectionArgs);
            case TASK_ID:
                return updateTask(uri, values, selection, selectionArgs);
            case SERVICE_CALL_ID:
                return updateServiceCall(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String itemId = uri.getLastPathSegment();
        int rowsUpdated = mInventoryDB.updateItem(itemId, values, selection, selectionArgs);
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(FTS_ITEM_URI, null);
        }
        return rowsUpdated;
    }

    private int updateTask(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String taskId = uri.getLastPathSegment();
        int rowsUpdated = mInventoryDB.updateTask(taskId, values, selection, selectionArgs);
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(FTS_TASK_URI, null);
        }
        return rowsUpdated;
    }

    private int updateServiceCall(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String serviceCallId = uri.getLastPathSegment();
        int rowsUpdated = mInventoryDB.updateServiceCall(serviceCallId, values, selection, selectionArgs);
        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(FTS_TASK_URI, null);
        }
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        switch (mURIMatcher.match(uri)) {
            case SEARCH_FTS_ITEMS:
                return ITEMS_MIME_TYPE;
            case ITEMS:
                return ITEM_DEFINITION_MIME_TYPE;

            // PNTODO: Should this be different?
            case SEARCH_SUGGEST_ITEMS:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if(method.equals("computeNewTasks")) {
            computeNewTasks();
        }
        return null;
    }

    private void computeNewTasks() {
        Log.d("HIContentProvider", "starting computeNewTasks queries...");
        mInventoryDB.computeNewTasks();
        getContext().getContentResolver().notifyChange(FTS_TASK_URI, null);
    }
}
