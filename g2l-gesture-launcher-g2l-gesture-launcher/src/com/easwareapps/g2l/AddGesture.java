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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.easwareapps.g2l.R;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

public class AddGesture extends Activity implements OnGesturePerformedListener, OnClickListener {

	GestureOverlayView gov = null;
	Button btnSelectAction = null;
	Button btnSave = null;	
	ImageView imgClear = null;
	Gesture userGesture = null;
	CheckBox chkEnableConfirmation = null;
	GestureLibrary gestureLib = null;
	int action = -4;	
	String option = null;
	String desc = "";
	String appPath = "";	
	int color = Color.YELLOW;
	boolean edit = false; 
	int gestureId = -1;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		edit = false;
		G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
		int theme = 0;
		try{
			theme = Integer.parseInt(dbh.getSettings("app_theme"));

		}catch(Exception E){

		}try{
			dbh.close();
		}catch(Exception E){

		}		

		switch(theme){

		case 0: setTheme(R.style.DarkTheme);break;
		case 1: setTheme(R.style.LightTheme);break;
		case 2: setTheme(R.style.Transparent);break;
		default: setTheme(R.style.DarkTheme);break;

		}
		super.onCreate(savedInstanceState);
		try{
			if(android.os.Build.VERSION.SDK_INT >= 11){
				setContentView(R.layout.add_gesture_11);
				btnSave = (Button)findViewById(R.id.idBtnSave);
			}else{
				setContentView(R.layout.add_gesture);
				btnSave = (Button)findViewById(R.id.idBtnSaveForOld);
			}

			gov = (GestureOverlayView)findViewById(R.id.gestureOverlayView1);			
			gov.addOnGesturePerformedListener(this);
			dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			try{
				if(dbh.getSettings("enable_multiple_strokes").equals("true")){
					gov.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);					
				}else{
					gov.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_SINGLE);
				}
			}catch(Exception E){
				E.printStackTrace();
			}
			try{

				color = Integer.parseInt(dbh.getSettings("gesture_color"));
				dbh.close();
			}catch (Exception e) {
				// TODO: handle exception
				color = Color.YELLOW;
			}
			gov.setGestureColor(color);


			btnSelectAction = (Button)findViewById(R.id.idBtnSelectAction);
			btnSelectAction.setOnClickListener(this);
			try{
				imgClear = (ImageView)findViewById(R.id.idClear);
				imgClear.setOnClickListener(this);
			}catch(Exception e){

			}
			if(theme!=1&&theme!=2){
				imgClear.setImageDrawable(getResources().getDrawable(R.drawable.clear_white));
			}
			try{
				chkEnableConfirmation = (CheckBox)findViewById(R.id.idConfirmationBeforeLaunch);
			}catch(Exception e){

			}


			btnSave.setOnClickListener(this);			
			appPath = Environment.getExternalStorageDirectory()+"/.g2l/";
			File appDir = new File(appPath);
			if(!(appDir.exists() && appDir.isDirectory())){
				appDir.mkdir();
			}

			File storeFile = new File(appPath+"/gestures");
			if(!storeFile.exists()){
				try {
					storeFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
			gestureLib = GestureLibraries.fromFile(storeFile);
			gestureLib.load();
			try{
				Bundle b = getIntent().getExtras();
				if(b==null){
					return;
				}else{
					edit = true;
				}
				int value = -1;
				value = b.getInt("id");				
				if(value != -1 ){			
					gestureId = value;
					ArrayList<Gesture> p = gestureLib.getGestures(""+value);
					if(p.size()>0){
						userGesture = p.get(0);						

						try{
							int WIDTH,HEIGHT;
							WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
							Display display = wm.getDefaultDisplay();
							Point size = new Point();	
							if (android.os.Build.VERSION.SDK_INT >= 13) {				
								display.getSize(size);
								WIDTH = size.x;
								HEIGHT = size.y;
							}else{
								WIDTH = display.getWidth();
								HEIGHT = display.getHeight();
							}

							if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
								int tmp = WIDTH;
								WIDTH = HEIGHT;
								HEIGHT = tmp;
							}
							if (android.os.Build.VERSION.SDK_INT >= 11) {
								gov.setLeft(-WIDTH);
								gov.setTop(-WIDTH);
							}
							
						}catch(Exception E){
							if (android.os.Build.VERSION.SDK_INT >= 11) {
								try{
									gov.setLeft(-320);
									gov.setTop(-320);
								}catch(Exception e){

								}
							}
						}

						onResume();
						dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
						Cursor c = dbh.getGestures(value);
						if(c.moveToFirst()){
							action = c.getInt(1);
							option = c.getString(2);
							desc = c.getString(3);
							if(c.getString(5).equals("true")){
								chkEnableConfirmation.setChecked(true);
							}else{
								chkEnableConfirmation.setChecked(false);
							}
						}						

					}
				}
			}catch(Exception e){
				Log.d("EXECPTION+++++",e.toString());
			}
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				ActionBar actionBar = getActionBar();
				actionBar.hide();
			}
		}catch(Exception E){
		}
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);			
		menu.add("Clear").setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				clearUserGesture();
				chkEnableConfirmation.setChecked(false);
				return false;
			}
		});		
		return true;
	}



	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//Toast.makeText(getApplicationContext(), "HERE", Toast.LENGTH_SHORT).show();
		if(userGesture != null){
			gov.setGesture(userGesture);
		}
	}

	@Override
	public void onGesturePerformed(GestureOverlayView arg0, Gesture gesture) {
		// TODO Auto-generated method stub
		userGesture = gesture;
		if(userGesture!=null){
			gov.setGesture(userGesture);
			gov.clear(false);			
		}
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Log.d("SAVE", "INSIDE ON CLICK");
		if(view == btnSave){
			Log.d("SAVE", "INSIDE SAVE IF");
			if(edit){
				deleteGesture(gestureId);
			}
			saveGesture();			
			return;
		}else if(view == btnSelectAction){
			userGesture = gov.getGesture();
			Intent i=new Intent(AddGesture.this, SelectAction.class);
			startActivityForResult(i, 0);	        	        
			return;
		}else if(imgClear !=null && view == imgClear){
			clearUserGesture();
		}

	}

	private void clearUserGesture(){
		userGesture = null;
		gov.cancelClearAnimation();
		gov.clear(true);
	}


	private void deleteGesture(int position) {
		// TODO Auto-generated method stub
		try{
			G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			gestureLib.removeEntry(""+position);
			gestureLib.save();
			dbh.deleteGesture(position);			
			dbh.close();
		}catch(Exception E){
			E.printStackTrace();
		}		


	}

	public void onActivityResult (int requestCode, int resultCode, Intent data){

		if(data!=null){
			action = data.getIntExtra("action", -4);
			option = data.getStringExtra("option");	
			desc = data.getStringExtra("desc");
		}else{
			action = -4;
		}
	}
	public void saveGesture(){
		Log.d("SAVE", "INSIDE SAVE");
		userGesture = gov.getGesture();
		if(userGesture != null && action != -4){			
			int orientation = gov.getOrientation();
			G2LDataBaseHandler dh = new G2LDataBaseHandler(getApplicationContext(), "name", null, 1);
			int id = dh.getNextId();			
			dh.addGestures(id, action, option, desc, orientation,chkEnableConfirmation.isChecked());
			gestureLib.addGesture(""+id, userGesture);
			gestureLib.save();

			try {
				FileOutputStream out = new FileOutputStream(appPath+id+".png");
				userGesture.toBitmap(50, 30, 1, Color.YELLOW).compress(CompressFormat.PNG, 90, out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			gov.setFadeEnabled(true);
			gov.setFadeOffset(1);
			gov.clearAnimation();
			gov.clear(true);
			gov.setFadeOffset(100000000);
			userGesture = null;
			action = -4;
			finish();
		}else if(userGesture == null){
			Toast.makeText(this, "Draw a valid gesture before saving", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "Select an action before saving", Toast.LENGTH_SHORT).show();
		}

	}

}