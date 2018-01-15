package com.jookovjook.base.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String TEXT_COLUMN = "text";
    public static final String TYPE_COLUMN = "type";
    //0 - VOC, 1 - DLG_LEFT, 2 - DLG_RIGHT
    public static final String SETT1_COLUMN = "sett1";
    public static final String SETT2_COLUMN = "sett2";
    public static final String SETT3_COLUMN = "sett3";
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    DatabaseHelper(Context context) {super(context, DATABASE_NAME, null, DATABASE_VERSION);}
    private static final String DATABASE_TABLE = "database";
    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + TEXT_COLUMN
            + " text not null, " + TYPE_COLUMN + " integer, " + SETT1_COLUMN
            + " integer, " + SETT2_COLUMN+ " integer, " + SETT3_COLUMN
            + " integer);";

    public DatabaseHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //*
    //public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
    //    super(context, name, factory, version, errorHandler);
    //}
    //*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(Query, null);
            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {
                alc.set(0,c);
                c.moveToFirst();
                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
