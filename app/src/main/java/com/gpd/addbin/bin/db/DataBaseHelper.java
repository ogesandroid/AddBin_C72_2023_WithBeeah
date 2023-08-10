package com.gpd.addbin.bin.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ProgrammingKnowledge on 4/3/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "binApp-db";
    public static final String TABLE_NAME = "BIN_DETAILS";
    public static final String TABLE_NAME_WILLAYAT = "WILLAYAT";

    public static final String COL_1 = "governorate_id";
    public static final String COL_2 = "willayat_id";
    public static final String COL_3 = "capacity_id";
    public static final String COL_4 = "manufacturer_id";
    public static final String COL_5 = "scan_type";
    public static final String COL_6 = "bin_rfid";
    public static final String COL_7 = "bin_lat";
    public static final String COL_8 = "bin_longi";
    public static final String COL_9 = "manual_entry";
    public static final String COL_10 = "bin_status";
    public static final String COL_11 = "bin_time";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public Cursor getAllData() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;

//        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
//
//        if (c.moveToFirst()) {
//            while ( !c.isAfterLast() ) {
//
//                Log.d("BinApp", "Table Name=> "+c.getString(0));
//                c.moveToNext();
//            }
//        }
    }

    public boolean getAllScanCodeData(String scanCodeCurrent){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + COL_6 + " = '" + scanCodeCurrent +"'";

        Log.e("AddBin" , "inside getAllScanCodeData query is" + Query);

        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();

            Log.e("AddBin" , " count < == 0");

            return false;
        }
        Log.e("AddBin" , " count > 0");
        cursor.close();
        return true;
    }


    public Cursor getWillayatDataByGovernorateId() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME_WILLAYAT + " where GOVERNORATE_ID = '1' ",null);
        return res;



//        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
//
//        if (c.moveToFirst()) {
//            while ( !c.isAfterLast() ) {
//
//                Log.d("BinApp", "Table Name=> "+c.getString(0));
//                c.moveToNext();
//            }
//        }
    }


    public void updateUploadStatus(){

        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
    }

}