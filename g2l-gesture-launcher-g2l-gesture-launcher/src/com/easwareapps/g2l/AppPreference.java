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

import com.easwareapps.g2l.FloatingWidgetService.LocalBinder;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class AppPreference extends PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener {

	CheckBoxPreference chkEnableQuickLauncher = null;
	Preference prefQuickLauncherPosition = null;
	CheckBoxPreference chkDisposeQuickLauncherAfterLaunch = null;
	Preference prefQuickLauncherTransperancy = null;
	Preference prefQuickLauncherSize = null;
	CheckBoxPreference chkEnableSwipeLaunch = null;
	Preference prefSwipeLaunchPosition;
	CheckBoxPreference chkEnableMultipleStrokes = null;
	Preference prefTimeMultipleStrokes = null;
	Preference prefGesturePrecision = null;	
	Preference prefGestureColor = null;
	Preference prefAppTheme = null;
	Preference prefRateUs = null;
	Preference prefFeedback = null;
	Preference prefDonate = null;

	Preference prefBlue = null;
	Preference prefRed = null;
	Preference prefGreen = null;
	Preference prefYellow = null;
	Preference prefMagenta = null;
	Preference prefCyan = null;
	Preference prefBlack = null;
	Preference prefCustom = null;

	static final String ENABLE_QUIK_LAUNCH = "enable_quick_launch";
	static final String ENABLE_SWIPE_LAUNCH = "enable_swipe_launch";
	static final String ENABLE_MULTIPLE_STROKE = "enable_multiple_strokes";
	static final String APP_THEME = "app_theme";
	static final String FINISH_ACTIVITY = "finish_activity_after_launch";
	static final String GESTURE_COLOR = "gesture_color";
	static final String DONOT_SHOW_DONATE = "do_not_show_donate";


	boolean mBounded;
	FloatingWidgetService mServer;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		Intent mIntent = new Intent(this, FloatingWidgetService.class);
		bindService(mIntent, mConnection, BIND_AUTO_CREATE);

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
		addPreferencesFromResource(R.layout.g2l_app_preference);
		chkEnableQuickLauncher = (CheckBoxPreference)findPreference("keyChkEnableQuickLauncher");
		prefQuickLauncherPosition = (Preference)findPreference("keyQuickLauncherPosition");
		prefQuickLauncherTransperancy = (Preference)findPreference("keyQuickLauncherAlpha");
		prefQuickLauncherSize = (Preference)findPreference("keyQuickLauncherSize");
		chkEnableSwipeLaunch = (CheckBoxPreference)findPreference("keyChkEnableSwipeLauncher");
		prefSwipeLaunchPosition = (Preference)findPreference("keySwipeLaunchPosition");
		chkEnableMultipleStrokes = (CheckBoxPreference)findPreference("keyChkEnableMultipleStroke");
		prefTimeMultipleStrokes = (Preference)findPreference("keyWaitTime");
		prefGesturePrecision = (Preference)findPreference("keyGestureSensitivity");		
		
		chkDisposeQuickLauncherAfterLaunch = (CheckBoxPreference)findPreference("keyChkQuitAferLaunching");
		prefGestureColor = (Preference)findPreference("keyGestureColor");
		prefAppTheme = (Preference)findPreference("keyAppTheme");
		prefRateUs = (Preference)findPreference("keyRateUs");
		prefDonate = (Preference)findPreference("keyDonate");

		prefBlue = (Preference)findPreference("keyColorBlue");
		prefRed = (Preference)findPreference("keyColorRed");
		prefGreen = (Preference)findPreference("keyColorGreen");
		prefYellow = (Preference)findPreference("keyColorYellow");
		prefMagenta = (Preference)findPreference("keyColorMagenta");
		prefCyan = (Preference)findPreference("keyColorCyan");
		prefBlack = (Preference)findPreference("keyColorBlack");
		prefCustom = (Preference)findPreference("keyColorCustom");
		prefCustom.setTitle("Custom");


		loadSettings();

		chkEnableQuickLauncher.setOnPreferenceChangeListener(this);
		chkDisposeQuickLauncherAfterLaunch.setOnPreferenceChangeListener(this);
		prefQuickLauncherTransperancy.setOnPreferenceClickListener(this);
		prefQuickLauncherSize.setOnPreferenceClickListener(this);
		chkEnableSwipeLaunch.setOnPreferenceChangeListener(this);
		prefSwipeLaunchPosition.setOnPreferenceClickListener(this);
		chkEnableMultipleStrokes.setOnPreferenceChangeListener(this);
		prefTimeMultipleStrokes.setOnPreferenceClickListener(this);
		prefGesturePrecision.setOnPreferenceClickListener(this);
		prefQuickLauncherPosition.setOnPreferenceClickListener(this);
		prefGestureColor.setOnPreferenceClickListener(this);
		prefAppTheme.setOnPreferenceClickListener(this);
		prefDonate.setOnPreferenceClickListener(this);
		prefRateUs.setOnPreferenceClickListener(this);

		prefBlue.setOnPreferenceClickListener(this);
		prefRed.setOnPreferenceClickListener(this);
		prefGreen.setOnPreferenceClickListener(this);
		prefYellow.setOnPreferenceClickListener(this);
		prefMagenta.setOnPreferenceClickListener(this);
		prefCyan.setOnPreferenceClickListener(this);
		prefBlack.setOnPreferenceClickListener(this);
		prefCustom.setOnPreferenceClickListener(this);

		if(android.os.Build.VERSION.SDK_INT >= 11){
			prefBlue.setIcon(getDrwawable(Color.BLUE));
			prefRed.setIcon(getDrwawable(Color.RED));
			prefGreen.setIcon(getDrwawable(Color.GREEN));
			prefYellow.setIcon(getDrwawable(Color.YELLOW));
			prefMagenta.setIcon(getDrwawable(Color.MAGENTA));
			prefCyan.setIcon(getDrwawable(Color.CYAN));
			prefBlack.setIcon(getDrwawable(Color.BLACK));
			dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			int color;
			try{
				color = Integer.parseInt(dbh.getSettings("gesture_color"));
			}catch(Exception E){
				color = Color.YELLOW;
			}
			dbh.close();
			if(!(color == Color.BLUE || color == Color.RED || color == Color.GREEN || color == Color.YELLOW ||
					color == Color.CYAN || color == Color.BLACK || color == Color.MAGENTA)){
				if(android.os.Build.VERSION.SDK_INT >= 11){
					prefCustom.setIcon(getDrwawable(color));
				}

			}
		}		
		prefCustom.setTitle("Custom");
		if(android.os.Build.VERSION.SDK_INT < 14){
			prefAppTheme.setEnabled(false);
			prefAppTheme.setSummary("Sorry this option is not available on your android version");
		}


	}

	private void loadSettings() {

		G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
		if(dbh.getSettings(ENABLE_QUIK_LAUNCH).equals("true")){
			chkEnableQuickLauncher.setChecked(true);
		}else{
			chkEnableQuickLauncher.setChecked(false);
		}
		if(dbh.getSettings(FINISH_ACTIVITY).equals("true")){
			chkDisposeQuickLauncherAfterLaunch.setChecked(true);
		}else{
			chkDisposeQuickLauncherAfterLaunch.setChecked(false);
		}
		if(dbh.getSettings(ENABLE_SWIPE_LAUNCH).equals("true")){

			chkEnableSwipeLaunch.setChecked(true);
		}else{
			chkEnableSwipeLaunch.setChecked(false);
		}			
		if(dbh.getSettings(ENABLE_MULTIPLE_STROKE).equals("true")){		
			chkEnableMultipleStrokes.setChecked(true);
		}else{
			chkEnableMultipleStrokes.setChecked(false);
		}
		dbh.close();

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
		if(preference == chkEnableQuickLauncher){

			dbh.setSettings(ENABLE_QUIK_LAUNCH, newValue.toString());
			if(newValue.toString().equals("true")){				
				chkEnableQuickLauncher.setChecked(true);
				//mServer.startFloatingWidget();
				if(mServer == null){
					Intent i = new Intent(AppPreference.this, FloatingWidgetService.class);
					startService(i);
					bindService(i, mConnection, Context.BIND_AUTO_CREATE);
					if(mServer != null){
						mServer.adjustQuickLauncherTypeAndPosition();
					}
				}else{
					mServer.adjustQuickLauncherTypeAndPosition();
				}

			}else{
				chkEnableQuickLauncher.setChecked(false);
				if(mServer!=null){
					mServer.stopFloatingWidget();					
				}

			}

		}else if(preference == chkDisposeQuickLauncherAfterLaunch){
			dbh.setSettings(FINISH_ACTIVITY, newValue.toString());
			if(newValue.toString().equals("true")){
				chkDisposeQuickLauncherAfterLaunch.setChecked(true);
			}else{
				chkDisposeQuickLauncherAfterLaunch.setChecked(false);
			}
		}else if(preference == chkEnableSwipeLaunch){
			dbh.setSettings(ENABLE_SWIPE_LAUNCH, newValue.toString());
			if(newValue.toString().equals("true")){
				chkEnableSwipeLaunch.setChecked(true);
				if(mServer == null){
					Intent i = new Intent(AppPreference.this, FloatingWidgetService.class);
					startService(i);
					bindService(i, mConnection, Context.BIND_AUTO_CREATE);
					mServer.adjustQuickLauncherTypeAndPosition();
				}else{
					mServer.adjustQuickLauncherTypeAndPosition();
				}
			}else{
				chkEnableSwipeLaunch.setChecked(false);
				if(mServer!=null){
					mServer.stopSwipeLaunch();
				}
			}
		}else if(preference == chkEnableMultipleStrokes){
			dbh.setSettings(ENABLE_MULTIPLE_STROKE, newValue.toString());
			if(newValue.toString().equals("true")){
				chkEnableMultipleStrokes.setChecked(true);				
			}else{
				chkEnableMultipleStrokes.setChecked(false);
			}
		} 
		dbh.close();
		return false;
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mBounded){
			unbindService(mConnection);
		}
		super.onDestroy();
	}

	@SuppressLint("InlinedApi")
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if(preference == prefDonate){
			String url = "http://g2l.easwareapps.com#donate";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}else if(preference == prefRateUs){
			
			/************************************Play store*******************************************************/
			
			String url = "https://play.google.com/store/apps/details?id=com.easwareapps.g2l";
			try {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + url)));
			} catch (android.content.ActivityNotFoundException anfe) {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + url)));
			}
			/**************************************************Samsung********************************************/
			/*
			try{
				String url = "samsungapps://ProductDetail/com.easwareapps.g2l";
				Intent intent = new Intent(); // set data 
				intent.setData(Uri.parse(url)); // The string_of_uri is an String object including a URI like """. // add flags
				if(android.os.Build.VERSION.SDK_INT >= 11){
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
				}else{
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				}
				startActivity(intent);
			}catch(Exception E){
				String url = "https://g2l.easwareapps.com?rateus=samsung";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			*/
			/**************************************************Slide ME********************************************/
			/*
			String url = "sam://details?id=com.easwareapps.g2l";
			try{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
				
			}catch(Exception e){
				url = "http://slideme.org/app/com.easwareapps.g2l";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
			*/
			/*************************************LG***********************************/
			/*
			try{
			Intent intent = new Intent("com.lge.lgworld.intent.action.VIEW");
			intent.setClassName("com.lge.lgworld", "com.lge.lgworld.LGReceiver");
			intent.putExtra("lgworld.receiver", "LGSW_INVOKE_DETAIL" );
			intent.putExtra("APP_PID", "PID14030517043" );
			sendBroadcast(intent);
			}catch(Exception e){
				Uri mUri = Uri.parse("http://www.lgworld.com/applicationId=PID14030517043");
				Intent mIntent = new Intent(Intent.ACTION_VIEW, mUri);
				startActivity(mIntent);
			}
			*/
			/*********************************NOKIA********************************************/
			//String url = "https://g2l.easwareapps.com?rateus=nokia";
			/*********************************Android PIT********************************************/
			//String url = "http://www.androidpit.com/developer/6167307/g2l";
			/*********************************Getjar********************************************/
			//String url = "http://www.getjar.mobi/mobile/808029";
			/*********************************1Mobile******************************************/
			/*
			String url = "http://www.1mobile.com/g2l-gesture-launcher-1497026.html";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			*/
			/************************************Opera Mobile Store****************************/
			/*
			String url = "http://apps.opera.com/en_in/g2l_gesture_launcher.html";
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			*/
			

		}else if(preference == prefTimeMultipleStrokes){

			showSeekAlertDialog("stroke_fade_time","Wait Time", "Time : ", 2, 250, 250, 20, 0, " ms ");		    
			return false;

		}else if(preference == prefQuickLauncherTransperancy){

			showSeekAlertDialog("quicklauncher_alpha","Quick Launcher Opacity", "Opacity: ", 100, (float)(2.55),1, 100, 0, " % ");		    
			return false;

		}else if(preference == prefQuickLauncherSize){

			final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			int index = -1; 
			try{
				index = Integer.parseInt(dbh.getSettings("quicklauncher_size"));
			}catch(Exception E){

			}
			final String[] size = {"32", "48", "72", "96", "144"};
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			alertDialogBuilder.setTitle("Select Quicklauncher Size");

			alertDialogBuilder

			.setSingleChoiceItems(size, index,  new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int index) {

					dbh.setSettings("quicklauncher_size", index+"");
					if(mConnection!=null){
						mServer.setQuickLaunchSize(index);
					}


				}
			}).setPositiveButton("OK", null).setCancelable(true);			
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

			dbh.close();
			return false;

		}else if(preference == prefGesturePrecision){
			showSeekAlertDialog("gesture_sensitivity","Gesture Sensitivity", "Sensitivity: ", 2, 1, 1, 9, 1, "");		    
			return false;

		}else if(preference == prefQuickLauncherPosition){

			final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			int posindex = -1; 
			try{
				posindex = Integer.parseInt(dbh.getSettings("quicklauncher_position"));
			}catch(Exception E){
				posindex = 9;
			}
			String[] positions = {"Top Left", "TopCenter", "Top Right", "Middle Left", "Middle Center", "Middle Right", "Bottom Left", "Bottom Center", "Bottom Right", "Last position"};
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			alertDialogBuilder.setTitle("Select Default Position");

			alertDialogBuilder

			.setSingleChoiceItems(positions, posindex,  new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int index) {

					dbh.setSettings("quicklauncher_position", index+"");
					if(index == 9){
						if(mServer!=null){
							mServer.saveQuickLauncherPosition();
						}
					}
					if(mServer!=null){
						mServer.setQuickLaunchPosition();
					}


				}
			}).setPositiveButton("OK", null).setCancelable(true);			
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

			dbh.close();
			return false;

		}else if(preference == prefSwipeLaunchPosition){

			final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			int posindex = -1; 
			try{
				posindex = Integer.parseInt(dbh.getSettings("swipe_launch_position"));
			}catch(Exception E){
				posindex = 1;
			}
			String[] positions = {"Left",  "Right", "Bottom"};
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			alertDialogBuilder.setTitle("Select Position of Swipe Launch");

			alertDialogBuilder

			.setSingleChoiceItems(positions, posindex,  new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int index) {

					dbh.setSettings("swipe_launch_position", index+"");					
					if(mServer!=null){
						mServer.adjustQuickLauncherTypeAndPosition();
					}					


				}
			}).setPositiveButton("OK", null).setCancelable(true);			
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

			dbh.close();
			return false;

		}else if(preference == prefAppTheme){

			final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			int themeindex = -1; 
			try{
				themeindex = Integer.parseInt(dbh.getSettings("app_theme"));
			}catch(Exception E){

			}
			String[] themes = {"Dark", "Light"};
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			alertDialogBuilder.setTitle("Select Theme");

			alertDialogBuilder

			.setSingleChoiceItems(themes, themeindex,  new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int index) {

					dbh.setSettings("app_theme", index+"");


				}
			}).setPositiveButton("OK", null).setCancelable(true);			
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();

			dbh.close();
			return false;
		}else {
			if(preference == prefBlue){		
				final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
				dbh.setSettings("gesture_color", Color.BLUE+"");
				dbh.close();
			}else if(preference == prefRed){
				final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
				dbh.setSettings("gesture_color", Color.RED+"");
				dbh.close();
			}else if(preference == prefGreen){
				final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
				dbh.setSettings("gesture_color", Color.GREEN+"");
				dbh.close();
			}else if(preference == prefMagenta){
				final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
				dbh.setSettings("gesture_color", Color.MAGENTA+"");
				dbh.close();
			}else if(preference == prefYellow){
				final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
				dbh.setSettings("gesture_color", Color.YELLOW+"");
				dbh.close();
			}else if(preference == prefCyan){
				final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
				dbh.setSettings("gesture_color", Color.CYAN+"");
				dbh.close();
			}else if(preference == prefBlack){
				final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
				dbh.setSettings("gesture_color", Color.BLACK+"");
				dbh.close();				
			}else if(preference == prefCustom){
				AlertDialog.Builder alert = new AlertDialog.Builder(this);

				alert.setTitle("Choose Color Code");
				alert.setMessage("Enter Color Code in Hex (# not needed)");

				// Set an EditText view to get user input 
				final EditText input = new EditText(this);
				alert.setView(input);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						int color = Color.YELLOW;
						try{
							String hex = input.getText().toString().replace("#", "");
							if(hex.length()>6){
								Toast.makeText(getApplicationContext(), "Color is not valid.", Toast.LENGTH_SHORT).show();
								return;
							}

							color = Color.parseColor("#"+hex);
						}catch(Exception E){
							Toast.makeText(getApplicationContext(), "Color is not valid.", Toast.LENGTH_SHORT).show();
							return;
						}
						// Do something with value!
						final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
						dbh.setSettings("gesture_color", color+"");
						dbh.close();
						startActivity(new Intent(AppPreference.this, AppPreference.class));
						finish();
					}
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

				alert.show();
				return false;
			}
			startActivity(new Intent(AppPreference.this, AppPreference.class));
			finish();
		}

		return false;
	}



	private BitmapDrawable getDrwawable(int color){

		int x = 50;
		int y = 50;
		int r = 50;

		Paint mPaint;
		Bitmap bitmap;
		Canvas mCanvas;
		BitmapDrawable drawable;

		mPaint= new Paint();
		mPaint.setColor(color);

		bitmap = Bitmap.createBitmap( 100, 100, Bitmap.Config.ARGB_8888);

		mCanvas = new Canvas(bitmap);
		mCanvas.drawCircle(x,y,r,mPaint);

		drawable = new BitmapDrawable(getResources(), bitmap);

		return drawable;
	}


	private void showSeekAlertDialog(final String settings, String title, final String desc, int initVal, final float mult,final float multText, int max, final int adjust, final String postfix){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this); 

		builder.setTitle(title); 
		//builder.setMessage("Select "); 
		LinearLayout linear=new LinearLayout(this); 

		linear.setOrientation(1); 

		final TextView txtVal = new TextView(this);
		txtVal.setText(desc);

		final SeekBar seek=new SeekBar(this); 
		seek.setMax(max);

		int val = initVal;
		try{
			G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			val = Integer.parseInt(dbh.getSettings(settings));
			dbh.close();
		}catch(Exception E){
			val = initVal;
		}	    
		txtVal.setText(desc + (int)(val*1/mult*multText + adjust) + postfix + "\n");
		seek.setProgress((int)(val*1/mult));
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub

				txtVal.setText(desc + ((int)(seek.getProgress() + adjust)*multText) +  postfix + "\n");
				if(settings.equals("quicklauncher_alpha")){
					if(mServer!=null){
						mServer.setTransperancy((int)(seek.getProgress()*mult));
					}
				}

			}
		});
		linear.addView(txtVal);
		linear.addView(seek); 


		builder.setView(linear); 

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {					
				try{
					final G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
					dbh.setSettings(settings, ""+((int)(seek.getProgress()*mult)));
					dbh.close();
					if(settings.equals("quicklauncher_alpha")){
						if(mServer!=null){
							mServer.setTransperancy((int)(seek.getProgress()*mult));
						}
						//stopService(new Intent(getApplicationContext(), FloatingWidget.class));
						//startService(new Intent(AppPreference.this, FloatingWidgetService.class));
						//startActivity(new Intent(AppPreference.this, FloatingWidget.class));
						/*Intent i=new Intent(AppPreference.this, FloatingWidgetService.class);
						i.putExtra("TRANSP", "NEW TRANSP"+ ((int)(seek.getProgress()*mult)));
						startService(i);

						Intent intent = new Intent(AppPreference.this, FloatingWidgetService.class);
						LocalBinder binder = (LocalBinder)service;
						 */
						//new FloatingWidgetService().setTrasperancy();
						//System.exit(0);
						//this.bindService(new Intent(AppPreference.this,FloatingWidgetService.class), );

					}
				}catch(Exception E){
					Toast.makeText(getApplicationContext(), "Failed to save.", Toast.LENGTH_SHORT).show();
					return;
				}



			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				if(mServer!=null){
					mServer.setTransperancy(-1);
				}
			}
		});

		AlertDialog alertDialog = builder.create();

		// show it
		alertDialog.show();

		//return false;



	}


	ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {

			mBounded = false;
			mServer = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			//Toast.makeText(FloatingWidgetService.this, "Service is connected", 1000).show();
			mBounded = true;
			LocalBinder mLocalBinder = (LocalBinder)service;
			mServer = mLocalBinder.getServerInstance();
		}



	};










}
