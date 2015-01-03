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
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

import com.easwareapps.g2l.FloatingWidgetService.LocalBinder;
import com.easwareapps.g2l.R;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GestureLauncher extends Activity implements OnGesturePerformedListener {

	GestureLibrary gestureLib = null;
	GestureOverlayView gov = null;
	String appPath = "";
	File storeFile = null;
	G2LDataBaseHandler dbh = null;
	ImageButton imgSettings = null;

	boolean mBounded = false;
	FloatingWidgetService mServer;
	ImageView imgArrow;
	TextView txtHint;
	static Camera camera = null;
	static boolean flashOn = false;

	protected void onCreate(Bundle savedInstanceState) {

		try{
			String prevPath = Environment.getExternalStorageDirectory()+"/g2l/";
			appPath = Environment.getExternalStorageDirectory()+"/.g2l/";
			final File prevAppDir = new File(prevPath);
			final File appDir = new File(appPath);
			if(!appDir.exists() && prevAppDir.exists()){
				prevAppDir.renameTo(appDir);				
			}
			else if(!(appDir.exists() && appDir.isDirectory())){
				appDir.mkdir();
			}
			super.onCreate(savedInstanceState);
			setContentView(R.layout.gesture_launcher);
			imgArrow = (ImageView)findViewById(R.id.idImgArrow);
			txtHint = (TextView)findViewById(R.id.idHint);
			gov = (GestureOverlayView)findViewById(R.id.gestureOverlayView);			
			imgSettings = (ImageButton)findViewById(R.id.idBtnSettings);
			imgSettings.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i=new Intent(GestureLauncher.this, MainActivity.class);
					startActivity(i);					


				}
			});

			




			gov.addOnGesturePerformedListener(this);			

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
			int color;
			try{
				dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
				try{
					if(dbh.getNextId()!=1){
						txtHint.setVisibility(View.INVISIBLE);
						imgArrow.setVisibility(View.INVISIBLE);
					}
				}catch(Exception E){

				}try{
					if(dbh.getSettings("enable_multiple_strokes").equals("true")){
						gov.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
						int fadetime = Integer.parseInt(dbh.getSettings("stroke_fade_time"));
						gov.setFadeOffset(fadetime);
					}else{
						gov.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_SINGLE);
					}
				}catch(Exception E){
					gov.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_SINGLE);
				}
				color = Integer.parseInt(dbh.getSettings("gesture_color"));
				dbh.close();
			}catch (Exception e) {
				// TODO: handle exception
				color = Color.YELLOW;
			}		
			gov.setGestureColor(color);

		}catch(Exception E){

		}



	}


	@Override
	protected void onStart() {
		try{
			Intent serviceIntent = new Intent(GestureLauncher.this, FloatingWidgetService.class);
			startService(serviceIntent);
			super.onStart();
		}catch(Exception E){

		}
	}




	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		super.onBackPressed();
		finish();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
		int color;
		try{
			dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			try{
				if(dbh.getNextId()!=1){
					txtHint.setVisibility(View.INVISIBLE);
					imgArrow.setVisibility(View.INVISIBLE);
				}else{
					txtHint.setVisibility(View.VISIBLE);
					imgArrow.setVisibility(View.VISIBLE);
				}
			}catch(Exception E){

			}try{
				if(dbh.getSettings("enable_multiple_strokes").equals("true")){
					gov.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
					int fadetime = Integer.parseInt(dbh.getSettings("stroke_fade_time"));
					gov.setFadeOffset(fadetime);
				}else{
					gov.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_SINGLE);
				}
			}catch(Exception E){
				gov.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_SINGLE);
			}
			color = Integer.parseInt(dbh.getSettings("gesture_color"));
			dbh.close();
		}catch (Exception e) {
			// TODO: handle exception
			color = Color.YELLOW;
		}

		gov.setGestureColor(color);
	}


	@SuppressLint("DefaultLocale")
	@Override
	public void onGesturePerformed(GestureOverlayView arg0, Gesture gesture) {
		try{
			ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
			if (predictions.size() > 0) {
				dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
				int sensitivity = 2;
				try{
					sensitivity = Integer.parseInt(dbh.getSettings("gesture_sensitivity"));
				}catch(Exception E){
					sensitivity = 2;
				}
				Prediction prediction = null;
				int k=0;
				for(;k<predictions.size();k++){
					prediction = predictions.get(k);
					Log.d("PREDICTION:",prediction.score+"");
					if(gesture.getStrokesCount() == gestureLib.getGestures(prediction.name).get(0).getStrokesCount() && prediction.score >= sensitivity){
						break;
					}
				}
				if(k == predictions.size()){
					Toast message = Toast.makeText(this, "No matching gesture found", Toast.LENGTH_SHORT);
					message.setGravity(Gravity.CENTER, 0, 0);
					message.show();
					return;
				}
				Cursor c = dbh.getGestures(Integer.parseInt(prediction.name));
				if(c.moveToFirst()){
					final int action = c.getInt(1);
					final String option = c.getString(2);					
					final String desc = c.getString(3);									
					final boolean finisAfterLaunch = dbh.getSettings("finish_activity_after_launch").equals("true");
					boolean confirmationBeforeLaunch = false;
					try{
						confirmationBeforeLaunch = c.getString(5).equals("true");
					}catch(Exception e){

					}
					dbh.closeConnection();
					Log.d("Confirmation Before Launch",""+confirmationBeforeLaunch);
					Log.d("Finish After Launch",""+finisAfterLaunch);
					if(confirmationBeforeLaunch){
						AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(
								this,R.style.DarkTheme));
						alert.setTitle("Confirmation");
						alert.setMessage(desc);					
						alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// Canceled.
								return;
							}
						}).setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// Canceled.
								launchActivity(action, option,desc);
								if(finisAfterLaunch){
									finish();
								}

							}
						});						
						alert.show();
					}else{

						launchActivity(action, option,desc);
						if(finisAfterLaunch){
							finish();
						}
					}
					return;



				}



			}
			Toast message = Toast.makeText(this, "No matching gesture found", Toast.LENGTH_SHORT);
			message.setGravity(Gravity.CENTER, 0, 0);
			message.show();
		}catch(Exception E){

		}
	}


	private void launchActivity(int action,String option,String desc){
		Toast message = Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT);
		message.setGravity(Gravity.CENTER, 0, 0);
		message.show();
		if(action == 8){
			Intent i = new Intent(Intent.ACTION_VIEW);
			if(!(option.startsWith("http://")||option.startsWith("https://")||option.startsWith("ftp://"))){
				option = "http://"+option;
			}
			i.setData(Uri.parse(option));
			startActivity(i);
		}else if(action == 7){
			File file = new File(option);
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			int index = file.getName().lastIndexOf('.')+1;
			String ext = "";
			String type = "*/*";
			try{
				ext = file.getName().substring(index).toLowerCase(Locale.getDefault());
				type = mime.getMimeTypeFromExtension(ext);
			}catch(Exception E){
				type = "*/*";
			}





			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri data = Uri.fromFile(file);

			intent.setDataAndType(data, type);

			startActivity(intent);
			dbh.closeConnection();

		}else if(action == 9){
			try{							
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"mailto", option, null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT");
				startActivity(Intent.createChooser(emailIntent, "Send email"));
			}catch(Exception E){
				//System.out.println("Error"+E);
			}
			dbh.closeConnection();

		}else if(action == 6){
			try{							
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"+option));
				startActivity(callIntent);

			}catch(Exception E){
				//System.out.println("Error"+E);
			}
			dbh.closeConnection();

		}else if(action == 5){
			sendSms(option);
			dbh.closeConnection();

		}else if(action == 4){
			try{		
				try{
					Intent launchIntent = new Intent(Intent.ACTION_MAIN, null);
					String packageName = option.split(",")[0];
					String activityName = "";
					if(option.split(",").length >= 2){
						activityName = option.split(",")[1];
					}
					launchIntent.setComponent(new ComponentName(packageName, activityName));
					launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
					launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(launchIntent);
				}catch(Exception E){

				}
			}catch(Exception E){
				System.out.println("Error"+E);
			}
			dbh.closeConnection();

		}else{

			if(option.equals("Bluetooth")){
				changeBluetoothState(action);
			}else if(option.equals("Wifi")){
				changeWifiState(action);
			}else if(option.equals("Data")){
				changeDataState(action);
			}else if(option.equals("Sync")){
				changeSyncState(action);
			}else if(option.equals("Brightness")){
				changeBrigtnessState(action);
			}else if(option.equals("Sound")){
				changeSoundState(action);
			}else if(option.equals("Music")){
				changeMusicState(action);
			}else if(option.equals("Flash")){
				changeFlashState(action);
				
			}else{
				dbh.closeConnection();
				return;
			}

		}

	}


	private void changeFlashState(int action) {
		// TODO Auto-generated method stub
		if(action == 2){
			if(flashOn){	changeFlashState(0);return; }
			else{	changeFlashState(1);return; }
		}
		PackageManager pm = this.getPackageManager();

		// if device support camera?
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Log.e("err", "Device has no camera!");
			return;
		}
		try{


				if(action==0){
					if(camera!=null){


						final Parameters p = camera.getParameters();
						p.setFlashMode(Parameters.FLASH_MODE_OFF);
						camera.setParameters(p);
						camera.stopPreview();
						camera.release();
					}
				}else if(action == 1){
					camera = Camera.open();
					final Parameters p = camera.getParameters();
					p.setFlashMode(Parameters.FLASH_MODE_TORCH);
					camera.setParameters(p);
					camera.startPreview();
				}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	private void changeMusicState(int action) {
		// TODO Auto-generated method stub
		/*
		final String CMDTOGGLEPAUSE = "togglepause";
		final String CMDPAUSE = "pause";
		final String CMDPREVIOUS = "previous";
		final String CMDNEXT = "next";

		final String CMDTOGGLEPAUSE = "togglepause";		
		final String PACKAGE = "com.android.music"; 
		final String SERVICECMD = "com.android.music.musicservicecommand";
		final String CMDNAME = "command";
		final String CMDSTOP = "stop";

		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		if(mAudioManager.isMusicActive()) {
			Intent i = new Intent(SERVICECMD);
			i.putExtra(CMDNAME , CMDTOGGLEPAUSE );
			GestureLauncher.this.sendBroadcast(i);
		}else{





			Intent intent = new Intent();
			intent.putExtra(CMDNAME, CMDTOGGLEPAUSE); 
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClassName("com.google.android.music", "com.google.android.music.MusicPlaybackService");
			startActivity(intent);

			Intent i = new Intent("com.android.music.musicservicecommand.togglepause");
			i.putExtra("command", "play");
			sendBroadcast(i);
		}
		 */


	}



	public void sendSms(String number){

		Uri uri = Uri.parse("smsto:"+number);
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", "");  
		startActivity(intent);
	}
	public void changeBluetoothState(int type){

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		boolean enabled = mBluetoothAdapter.isEnabled();
		if(type == 2){
			if(enabled){
				type = 0;
			}else{
				type = 1;
			}
		}
		switch(type){
		case 0: if(enabled) { mBluetoothAdapter.disable();} break;
		case 1:	if(!enabled) { mBluetoothAdapter.enable(); } break;	
		case 3: Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
		startActivity(discoverableIntent);break;
		}




		/*

		 */

	}




	public void changeWifiState(int type){

		WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		boolean currentState = wifiManager.isWifiEnabled();
		boolean enable = currentState;
		switch(type){
		case 0: enable = false;break;
		case 1: enable = true;break;
		case 2: enable = !currentState;break;
		}
		if(enable != currentState){
			wifiManager.setWifiEnabled(enable);			
		}
		//getApplicationContext().sendBroadcast(new Intent("com.sec.android.app.music.musicservicecommand.play"));
		//changeSoundState(type);
	}




	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void changeDataState(int type) {
		// TODO Auto-generated method stub

		ConnectivityManager connec = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		//NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		boolean currentState;

		currentState = mobile.isConnected();

		boolean enable = currentState;
		switch(type){
		case 0: enable = false;break;
		case 1: enable = true;break;
		case 2: enable = !currentState;break;
		}		
		if(enable != currentState){
			final ConnectivityManager conman = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			Class conmanClass;
			try {
				conmanClass = Class.forName(conman.getClass().getName());

				java.lang.reflect.Field iConnectivityManagerField;

				iConnectivityManagerField = conmanClass.getDeclaredField("mService");
				iConnectivityManagerField.setAccessible(true);
				final Object iConnectivityManager = iConnectivityManagerField.get(conman);
				final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
				final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
				setMobileDataEnabledMethod.setAccessible(true);

				setMobileDataEnabledMethod.invoke(iConnectivityManager, enable);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}




	private void changeSyncState(int type) {
		// TODO Auto-generated method stub

		boolean currentState = ContentResolver.getMasterSyncAutomatically();
		boolean enable = currentState;
		switch(type){
		case 0: enable = false;break;
		case 1: enable = true;break;
		case 2: enable = !currentState;break;
		}
		if( enable != currentState ){
			ContentResolver.setMasterSyncAutomatically(enable);
		}

	}

	private void changeSoundState(int type) {
		// TODO Auto-generated method stub

		int mode = 1;
		AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		switch(type){
		case 0:am.setRingerMode(AudioManager.RINGER_MODE_SILENT);break;
		case 3:am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);break;
		case 1:am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);break;
		case 2:mode = am.getRingerMode();
		if(mode == AudioManager.RINGER_MODE_SILENT ){
			am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);break;
		}else{
			am.setRingerMode(AudioManager.RINGER_MODE_SILENT);break;
		}
		}

	}

	private void changeBrigtnessState(int type) {
		// TODO Auto-generated method stub
		int brightness = 127;
		try {
			brightness = android.provider.Settings.System.getInt(
					getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch(type){
		case 0:brightness = 0;break;
		case 3:brightness = 127;break;
		case 1:brightness = 255;break;
		case 2:			
			if(brightness>=0&&brightness<127){
				brightness = 127;
			}else if(brightness>=127&&brightness<255){
				brightness = 255;
			}else if(brightness==255){
				brightness = 0;
			}
			break;

		}

		android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
				android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);

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
