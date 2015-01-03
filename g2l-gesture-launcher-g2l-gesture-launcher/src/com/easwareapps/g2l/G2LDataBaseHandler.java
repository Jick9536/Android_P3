/*
*   G2L Gesture launcher launches apps, change settings, open files, make phone calls etc based on pre defined gesture
*   Copyright (C) 2014  Vishnu V g2l@easwareapps.com
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.easwareapps.g2l;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class G2LDataBaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;

	private static final String DATABASE_NAME = "db_g2l";

	private static final String TABLE_GESTURES = "g2l_gestures";
	private static final String TABLE_SETTINGS = "g2l_settings";

	private static final String KEY_ID = "gesture_id";
	private static final String KEY_ACTION = "gesture_action";
	private static final String KEY_OPTION = "gesture_option";
	private static final String KEY_DESC = "gesture_description";
	private static final String KEY_ORIENTATION = "gesture_orientation";
	private static final String KEY_CONFIRMATION = "gesture_confirmation";

	private static final String KEY_SETTINGS_ID = "settings_id";
	private static final String KEY_SETTINGS_NAME = "settings_name";
	private static final String KEY_SETTINGS_VALUE = "settings_value";


	SQLiteDatabase db = null;
	public G2LDataBaseHandler(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try{
			String CREATE_TABLE = "CREATE TABLE " + TABLE_GESTURES + "("
					+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_ACTION + " INT,"
					+ KEY_OPTION + " TEXT," + KEY_DESC + " TEXT, " + KEY_ORIENTATION + " INTEGER," + KEY_CONFIRMATION + " VARCHAR(10))";
			db.execSQL(CREATE_TABLE);
		}catch(Exception E){

		}
		try{
			String CREATE_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
					+ KEY_SETTINGS_ID + " INTEGER PRIMARY KEY," + KEY_SETTINGS_NAME + " TEXT,"
					+  KEY_SETTINGS_VALUE + " TEXT)";
			db.execSQL(CREATE_TABLE);
		}catch(Exception E){			
		}
		try{
			initValues();
		}catch(Exception E){			
		}



	}

	private void initValues() {
		// TODO Auto-generated method stub
		db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SETTINGS_ID, 1);
		values.put(KEY_SETTINGS_NAME, "enable_quick_launch");
		values.put(KEY_SETTINGS_VALUE, "true");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}

		values.clear();
		values.put(KEY_SETTINGS_ID, 2);
		values.put(KEY_SETTINGS_NAME, "app_theme");
		values.put(KEY_SETTINGS_VALUE, "1");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}

		values.clear();
		values.put(KEY_SETTINGS_ID, 3);
		values.put(KEY_SETTINGS_NAME, "finish_activity_after_launch");
		values.put(KEY_SETTINGS_VALUE, "true");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}

		values.clear();
		values.put(KEY_SETTINGS_ID, 4);
		values.put(KEY_SETTINGS_NAME, "gesture_color");
		values.put(KEY_SETTINGS_VALUE, "-256");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}

		values.clear();
		values.put(KEY_SETTINGS_ID, 5);
		values.put(KEY_SETTINGS_NAME, "do_not_show_donate");
		values.put(KEY_SETTINGS_VALUE, "false");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}

		values.clear();
		values.put(KEY_SETTINGS_ID, 6);
		values.put(KEY_SETTINGS_NAME, "quicklauncher_position");
		values.put(KEY_SETTINGS_VALUE, "6");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}		

		values.clear();
		values.put(KEY_SETTINGS_ID, 7);
		values.put(KEY_SETTINGS_NAME, "gesture_sensitivity");
		values.put(KEY_SETTINGS_VALUE, "1");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}
		
		values.clear();
		values.put(KEY_SETTINGS_ID, 8);
		values.put(KEY_SETTINGS_NAME, "quicklauncher_alpha");
		values.put(KEY_SETTINGS_VALUE, "255");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}
				
		
		values.clear();
		values.put(KEY_SETTINGS_ID, 9);
		values.put(KEY_SETTINGS_NAME, "quicklauncher_size");
		values.put(KEY_SETTINGS_VALUE, "-1");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}
		
		values.clear();
		values.put(KEY_SETTINGS_ID, 10);
		values.put(KEY_SETTINGS_NAME, "enable_multiple_strokes");
		values.put(KEY_SETTINGS_VALUE, "true");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}
		
		
		values.clear();
		values.put(KEY_SETTINGS_ID, 11);
		values.put(KEY_SETTINGS_NAME, "stroke_fade_time");
		values.put(KEY_SETTINGS_VALUE, "500");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}
		
		
		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}
		
		values.clear();
		values.put(KEY_SETTINGS_ID, 12);
		values.put(KEY_SETTINGS_NAME, "enable_swipe_launch");
		values.put(KEY_SETTINGS_VALUE, "true");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}
		
		
		values.clear();
		values.put(KEY_SETTINGS_ID, 13);
		values.put(KEY_SETTINGS_NAME, "swipe_launch_position");
		values.put(KEY_SETTINGS_VALUE, "0");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}
		
		values.clear();
		values.put(KEY_SETTINGS_ID, 14);
		values.put(KEY_SETTINGS_NAME, "ask_for_confirmation_before_launch");
		values.put(KEY_SETTINGS_VALUE, "true");


		try{
			db.insert(TABLE_SETTINGS, null, values);	    	
		}catch(Exception E){

		}
		
		
				
		db.close(); 
		
		

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db = arg0;
		db.execSQL("ALTER TABLE " + TABLE_GESTURES + " ADD " + KEY_CONFIRMATION + " varchar(10)");
		
	}

	public boolean addGestures(int id, int action, String option, String desc, int orientation, boolean b) {
		db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, id);
		values.put(KEY_ACTION, action);
		values.put(KEY_OPTION, option);
		values.put(KEY_DESC, desc);
		values.put(KEY_ORIENTATION, orientation);
		values.put(KEY_CONFIRMATION, b+"");

		try{
			db.insert(TABLE_GESTURES, null, values);
			return true;
		}catch(Exception E){

		}
		db.close(); // Closing database connection
		return false;

	}


	public Cursor getGestures(int id) {
		db = this.getReadableDatabase();
		String option = "";
		if(id != 0){
			option = " where "+KEY_ID+" = "+id;
		}
		String selectQuery = "SELECT  * FROM " + TABLE_GESTURES + option ;
		Cursor cursor = db.rawQuery(selectQuery, null);
		return cursor;


	}

	public int getGestureOrientation(int id){
		db = this.getReadableDatabase();		
		String selectQuery = "SELECT  " + KEY_ORIENTATION + " FROM " + TABLE_GESTURES + " where "+ KEY_ID + " = "+id;
		Cursor cursor = db.rawQuery(selectQuery, null);
		int val = -4;
		if(cursor.moveToFirst()){;			
		val = cursor.getInt(0);
		}
		cursor.close();
		return val;
	}
	public void closeConnection(){
		db.close();
	}

	public int getNextId(){
		db = this.getReadableDatabase();		
		String selectQuery = "SELECT  max(" + KEY_ID + ") FROM " + TABLE_GESTURES;
		Cursor cursor = db.rawQuery(selectQuery, null);
		int next = 0;
		if(cursor.moveToFirst()){;
		next = cursor.getInt(0);
		}
		cursor.close();
		next++;
		db.close();
		return next;
	}

	public String getActionOption(int id) {
		// TODO Auto-generated method stub
		db = this.getReadableDatabase();		
		String selectQuery = "SELECT  " + KEY_DESC + " FROM " + TABLE_GESTURES + " where "+ KEY_ID + " = "+id;
		Cursor cursor = db.rawQuery(selectQuery, null);
		String val = "";
		if(cursor.moveToFirst()){;			
		val = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return val;

	}

	public void deleteGesture(int id) {
		// TODO Auto-generated method stub
		db = this.getWritableDatabase();
		String query = "DELETE FROM " + TABLE_GESTURES + " where "+KEY_ID+" = "+id;
		//System.out.print(query);
		db.execSQL(query);
		db.close();


	}

	public void setSettings(String name, String val){		

		db = this.getWritableDatabase();
		String query = "UPDATE " + TABLE_SETTINGS + " set " + KEY_SETTINGS_VALUE + " = '" + val + "' where "+KEY_SETTINGS_NAME+" = '" + name + "'";		
		db.execSQL(query);
		db.close();
	}
	public String  getSettings(String name ){

		db = this.getReadableDatabase();		
		String selectQuery = "SELECT  " + KEY_SETTINGS_VALUE + " FROM " + TABLE_SETTINGS + " where "+ KEY_SETTINGS_NAME + " = '"+name+"'";

		Cursor cursor = db.rawQuery(selectQuery, null);
		String val = "";
		if(cursor.moveToFirst()){		
			val = cursor.getString(0);
		}else{
			try{
				initValues();
				cursor.close();
				selectQuery = "SELECT  " + KEY_SETTINGS_VALUE + " FROM " + TABLE_SETTINGS + " where "+ KEY_SETTINGS_NAME + " = '"+name+"'";

				cursor = db.rawQuery(selectQuery, null);
				val = "";
				if(cursor.moveToFirst()){	
					val = cursor.getString(0);
				}
			}catch(Exception E){

			}
		}
		cursor.close();
		db.close();
		return val;


	}
}
