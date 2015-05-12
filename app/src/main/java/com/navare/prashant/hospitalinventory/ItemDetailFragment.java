package com.navare.prashant.hospitalinventory;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;

import com.navare.prashant.hospitalinventory.Database.HospitalInventoryContentProvider;
import com.navare.prashant.hospitalinventory.Database.Item;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, TextWatcher {

    public static final int LOADER_ID_ITEM_DETAILS = 2;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The item this fragment is presenting.
     */
    private String mItemID;
    private Item mItem = null;

    /**
     * The UI elements showing the details of the item
     */
    private TextView mTextName;
    private TextView mTextDescription;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void EnableDeleteButton(boolean bEnable);
        public void EnableRevertButton(boolean bEnable);
        public void EnableSaveButton(boolean bEnable);
        public void RedrawOptionsMenu();
        public void onItemDeleted();
    }
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void EnableDeleteButton(boolean bEnable) {
        }
        @Override
        public void EnableRevertButton(boolean bEnable) {
        }
        @Override
        public void EnableSaveButton(boolean bEnable) {
        }
        @Override
        public void RedrawOptionsMenu() {
        }

        @Override
        public void onItemDeleted() {

        }
    };

    /**
     * The fragment's current callback object, which is notified of changes to the item
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            mItemID = getArguments().getString(ARG_ITEM_ID);
            if ((mItemID != null) && (mItemID.isEmpty() == false)) {
                getLoaderManager().initLoader(LOADER_ID_ITEM_DETAILS, null, this);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }


    // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        mTextName = ((TextView) rootView.findViewById(R.id.textName));
        mTextName.addTextChangedListener(this);

        mTextDescription = ((TextView) rootView.findViewById(R.id.textDescription));
        mTextDescription.addTextChangedListener(this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == LOADER_ID_ITEM_DETAILS) {
            Uri itemURI = Uri.withAppendedPath(HospitalInventoryContentProvider.ITEM_URI,
                    mItemID);

            return new CursorLoader(getActivity(),
                    itemURI, Item.FIELDS, null, null,
                    null);
        }
        else
            return null;
    }

    // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor dataCursor) {

        if (dataCursor != null) {
            int loaderID = loader.getId();
            if (loaderID == LOADER_ID_ITEM_DETAILS) {
                if (mItem == null)
                    mItem = new Item();

                mItem.setContentFromCursor(dataCursor);

                mTextName.setText(mItem.mName);
                mTextDescription.setText(mItem.mDescription);

                // Toggle the action bar buttons appropriately
                mCallbacks.EnableDeleteButton(true);
                mCallbacks.EnableRevertButton(false);
                mCallbacks.EnableSaveButton(false);
                mCallbacks.RedrawOptionsMenu();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCallbacks.EnableRevertButton(true);
        mCallbacks.EnableSaveButton(true);
        mCallbacks.RedrawOptionsMenu();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void revertUI() {
        if (mItem == null) {
            mTextName.setText("");
            mTextDescription.setText("");

        }
        else {
            mTextName.setText(mItem.mName);
            mTextDescription.setText(mItem.mDescription);

        }
        mCallbacks.EnableRevertButton(false);
        mCallbacks.EnableSaveButton(false);
    }

    public void deleteItem() {
        Uri itemURI = Uri.withAppendedPath(HospitalInventoryContentProvider.ITEM_URI,
                mItemID);
        int result = getActivity().getContentResolver().delete(itemURI, null, null);
        if (result > 0)
            mCallbacks.onItemDeleted();
    }

    public void saveItem() {
        updateItemFromUI();
        boolean bSuccess = false;
        if ((mItemID == null) || (mItemID.isEmpty())) {
            // a new item is being inserted.
            Uri uri = getActivity().getContentResolver().insert(HospitalInventoryContentProvider.ITEM_URI, mItem.getContentValues());
            if (uri != null) {
                mItemID = uri.getLastPathSegment();
                bSuccess = true;
            }
        }
        else {
            // TODO: implement the update case
            Uri itemURI = Uri.withAppendedPath(HospitalInventoryContentProvider.ITEM_URI,
                    mItemID);
            int result = getActivity().getContentResolver().update(itemURI, mItem.getContentValues(), null, null);
            if (result > 0)
                bSuccess = true;
        }
        if (bSuccess) {
            mCallbacks.EnableSaveButton(false);
            mCallbacks.EnableRevertButton(false);
            mCallbacks.RedrawOptionsMenu();
        }

    }

    // TODO: ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void updateItemFromUI() {
        if (mItem == null)
            mItem = new Item();

        mItem.mName = mTextName.getText().toString();
        mItem.mDescription = mTextDescription.getText().toString();
    }


}
