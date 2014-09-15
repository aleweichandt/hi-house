package com.web.ones.hihouse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "HiHouse.db";
	public static final String TABLE_NAME_DEVICES = "devices";
	public static final String DEVICES_COLUMN_ID = "id";
	public static final String DEVICES_COLUMN_DESC_VOICE = "desc_voice";
	
	public DBHelper(Context context){
		super(context, DATABASE_NAME , null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// create table
		db.execSQL(	"CREATE TABLE " + TABLE_NAME_DEVICES + " (" +
					DEVICES_COLUMN_ID + " INTEGER PRIMARY KEY, "+
					DEVICES_COLUMN_DESC_VOICE + " text)");
	}
	
	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DEVICES);
 
        // create fresh table
        this.onCreate(db);
    }
	
	public long insertDevice(int id, String desc_voice){

		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(DEVICES_COLUMN_ID, id);
		values.put(DEVICES_COLUMN_DESC_VOICE, desc_voice);
		
		long ret_id = db.insert(TABLE_NAME_DEVICES, null, values);
		
		db.close();
		
		return ret_id;
	}
	
	public String getDevice(String desc_voice){
		
		SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.rawQuery( 	"SELECT "+ DEVICES_COLUMN_ID +
        							" FROM " + TABLE_NAME_DEVICES +
        							" WHERE "+ DEVICES_COLUMN_DESC_VOICE + "='" + desc_voice + "'", null );
        
        if (cursor.moveToFirst()){
        	return cursor.getString(cursor.getColumnIndex(DBHelper.DEVICES_COLUMN_ID));
        }
        else{
        	return "";
        }
	}
}
