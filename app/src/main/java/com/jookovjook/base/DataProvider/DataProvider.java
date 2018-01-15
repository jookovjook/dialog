
package com.jookovjook.base.DataProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.jookovjook.base.Database.DatabaseHelper;
import com.jookovjook.base.R;

import java.util.LinkedList;
import java.util.List;

public class DataProvider extends AbstractDataProvider {
    private List<ConcreteData> mData;
    private ConcreteData mLastRemovedData;
    private int mLastRemovedPosition = -1;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    private Context context;

    public DataProvider(Context context) {

        this.context=context;
        mData = new LinkedList<>();

        mDatabaseHelper = new DatabaseHelper(this.context, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = mSqLiteDatabase.query("database", new String[]{DatabaseHelper._ID,DatabaseHelper.TEXT_COLUMN,
                DatabaseHelper.TYPE_COLUMN, DatabaseHelper.SETT1_COLUMN,
                DatabaseHelper.SETT2_COLUMN, DatabaseHelper.SETT3_COLUMN}, null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String txt = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEXT_COLUMN));
            int _id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
            int type = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TYPE_COLUMN));
            int sett_2 = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SETT2_COLUMN));
            if(type == 0) {
                final long id = mData.size();
                final int viewType = 0;
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
                switch (sett_2){
                    case 1:
                        txt = context.getResources().getString(R.string.main_db_1);
                        break;
                    case 2:
                        txt = context.getResources().getString(R.string.main_db_2);
                        break;
                    case 3:
                        txt = context.getResources().getString(R.string.main_db_3);
                        break;
                    case 4:
                        txt = context.getResources().getString(R.string.main_db_4);
                        break;
                    case 5:
                        txt = context.getResources().getString(R.string.main_db_5);
                        break;
                    case 6:
                        txt = context.getResources().getString(R.string.main_db_6);
                        break;
                    case 7:
                        txt = context.getResources().getString(R.string.main_db_7);
                        break;
                    case 8:
                        txt = context.getResources().getString(R.string.main_db_8);
                        break;
                    case 9:
                        txt = context.getResources().getString(R.string.main_db_9);
                        break;
                    case 10:
                        txt = context.getResources().getString(R.string.main_db_10);
                        break;
                }
                mData.add(new ConcreteData(id, viewType, txt, _id, swipeReaction));
            }
        }
        cursor.close();

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mData.get(index);
    }

    @Override
    public int undoLastRemoval() {
        if (mLastRemovedData != null) {
            int insertedPosition;
            if (mLastRemovedPosition >= 0 && mLastRemovedPosition < mData.size()) {
                insertedPosition = mLastRemovedPosition;
            } else {
                insertedPosition = mData.size();
            }

            mData.add(insertedPosition, mLastRemovedData);
            //restore data
            ConcreteData dataa = mData.get(insertedPosition);
            final int _id = dataa.get_id();
            mDatabaseHelper = new DatabaseHelper(this.context, "database.db", null, 1);
            mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.TYPE_COLUMN, "0");
            mSqLiteDatabase.update("database", values, "_id = ?", new String[] {Integer.toString(_id)});
            ///
            mLastRemovedData = null;
            mLastRemovedPosition = -1;

            return insertedPosition;
        } else {
            return -1;
        }
    }

    @Override
    public void editItem(int position, String Text) {
        final long id = mData.size();
        final int viewType = 0;
        ConcreteData dataa = mData.get(position);
        final int _id = dataa.get_id();
        //save idited data to database
        mDatabaseHelper = new DatabaseHelper(this.context, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TEXT_COLUMN, Text);
        mSqLiteDatabase.update("database", values, "_id = ?", new String[] {Integer.toString(_id)});
        //dfhjdfh
        final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
        mData.set(position, new ConcreteData(id, viewType, Text, _id, swipeReaction));
    }

    @Override
    public void addItem(String Text){
        //вставка данных
        mDatabaseHelper = new DatabaseHelper(this.context, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TEXT_COLUMN, Text);
        values.put(DatabaseHelper.TYPE_COLUMN, "0");
        values.put(DatabaseHelper.SETT1_COLUMN, "0");
        values.put(DatabaseHelper.SETT1_COLUMN, "0");
        values.put(DatabaseHelper.SETT2_COLUMN, "0");
        values.put(DatabaseHelper.SETT3_COLUMN, "0");
        mSqLiteDatabase.insert("database", null, values);
        //вывод элемента
        mDatabaseHelper = new DatabaseHelper(this.context, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = mSqLiteDatabase.query("database", new String[]{DatabaseHelper._ID,DatabaseHelper.TEXT_COLUMN,
                DatabaseHelper.TYPE_COLUMN, DatabaseHelper.SETT1_COLUMN,
                DatabaseHelper.SETT2_COLUMN, DatabaseHelper.SETT3_COLUMN}, null, null, null, null, null);
        cursor.moveToLast();
        int _id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper._ID));
        final long id = mData.size();
        final int viewType = 0;
        final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
        mData.add(new ConcreteData(id, viewType, Text, _id, swipeReaction));
        cursor.close();
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final ConcreteData item = mData.remove(fromPosition);

        mData.add(toPosition, item);
        mLastRemovedPosition = -1;
    }

    @Override
    public void removeItem(int position) {
        ConcreteData dataa = mData.get(position);
        final int _id = dataa.get_id();
        //noinspection UnnecessaryLocalVariable
        final ConcreteData removedItem = mData.remove(position);
        mLastRemovedData = removedItem;
        mLastRemovedPosition = position;
        //
        mDatabaseHelper = new DatabaseHelper(this.context, "database.db", null, 1);
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TYPE_COLUMN, "-1");
        mSqLiteDatabase.update("database", values, "_id = ?", new String[] {Integer.toString(_id)});
    }

    public static final class ConcreteData extends Data {

        private final long mId;
        private final String mText;
        private final int m_id;
        private final int mViewType;
        private boolean mPinned;

        ConcreteData(long id, int viewType, String text, int _id, int swipeReaction) {
            mId = id;
            mViewType = viewType;
            mText = makeText(id, text, swipeReaction);
            m_id = _id;
        }



        private static String makeText(long id, String text, int swipeReaction) {
            final StringBuilder sb = new StringBuilder();
            //sb.append(id);
            //sb.append("");
            sb.append(text);
            return sb.toString();
        }

        @Override
        public boolean isSectionHeader() {
            return false;
        }

        @Override
        public int getViewType() {
            return mViewType;
        }

        @Override
        public long getId() {
            return mId;
        }

        @Override
        public String toString() {
            return mText;
        }

        @Override
        public String getText() {
            return mText;
        }

        @Override
        public int get_id() {
            return m_id;
        }

        @Override
        public boolean isPinned() {
            return mPinned;
        }

        @Override
        public void setPinned(boolean pinned) {
            mPinned = pinned;
        }
    }
}
