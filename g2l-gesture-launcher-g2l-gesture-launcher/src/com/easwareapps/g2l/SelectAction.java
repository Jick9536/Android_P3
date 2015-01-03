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

import java.util.Collections;
import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Browser;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings( "deprecation" )
public class SelectAction extends PreferenceActivity implements OnPreferenceClickListener, Runnable {


	String packageList[] = new String[10000];
	String packageNames[] = new String[10000];
	String numbers[] = new String[10000];
	String typeName[] = new String[10000];
	Integer type[] = new Integer[10000];
	String contactName = "";
	Intent databackIntent = null;

	int TYPE_PHONE = 0;
	int TYPE_EMAIL = 1;

	PreferenceScreen prefScreenApps = null;
	PreferenceScreen prefScreenLED = null;

	Preference toggleBt = null;
	Preference turnOffBt = null;
	Preference turnOnBt = null;
	Preference turnBtVisibility = null;
	Preference btSettings = null;

	Preference toggleWifi = null;
	Preference turnOffWifi = null;
	Preference turnOnWifi = null;
	Preference wifiSettings = null;

	Preference prefBookmarks[] = new Preference[10000];

	int FILESELECT = 1;
	int NUMSELECT = 2;




	Preference toggleDataConnectivity = null;
	Preference turnOffDataConnectivity = null;
	Preference turnOnDataConnectivity = null;
	Preference dataConnectivitySettings = null;

	Preference toggleSync = null;
	Preference turnOffSync = null;
	Preference turnOnSync = null;
	Preference syncSettings = null;

	Preference toggleBrightness = null;
	Preference minBrightness = null;
	Preference medBrightness = null;
	Preference maxBrightness = null;
	Preference displaySettings = null;

	Preference togggleSound = null;
	Preference muteSound = null;
	Preference vibrateSound = null;
	Preference normalSound = null;
	Preference soundSettings = null;
	/*
	Preference togglePlayPause = null;
	Preference playMusic = null;
	Preference pauseMusic = null;
	 */

	//Preference toggleFlash = null;
	Preference turnOffFlash = null;
	Preference turnOnFlash = null;

	Preference openGPSSettings;
	Preference openFlightModeSettings;
	Preference openBatteryInfo;
	Preference openTether = null;
	Preference openFile = null;

	PreferenceScreen prefOpenURL;
	Preference callMailMsg = null;
	AlertDialog alertDialog = null;

	int selectedIndex = -1;

	ProgressDialog p;

	Thread t;
	Thread threadApp;
	Thread threadBookmarks;
	int lastState = -1;
	



	public void onCreate(Bundle savedInstanceState) {

		try{
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
			addPreferencesFromResource(R.layout.select_action);					
			t = new Thread(this);
			t.start();	





		}catch(Exception E){

		}
	}







	private void initSettings(){






		prefScreenApps = (PreferenceScreen)findPreference("keyAppPreferencescreen");
		prefScreenLED = (PreferenceScreen)findPreference("flash_preferencescreen");

		toggleWifi = (Preference)findPreference("keyToggleWifi");
		turnOnWifi = (Preference)findPreference("keyTurnOnWifi");
		turnOffWifi = (Preference)findPreference("keyTurnOffWifi");
		wifiSettings = (Preference)findPreference("keyWifiSettings");

		toggleBt = (Preference)findPreference("keyToggleBlueTooth");
		turnOnBt = (Preference)findPreference("keyTurnOnBlueTooth");
		turnOffBt = (Preference)findPreference("keyTurnOffBlueTooth");
		turnBtVisibility = (Preference)findPreference("keyTmpBluetoothVisible");
		btSettings = (Preference)findPreference("keyBluetoothSettings");



		toggleDataConnectivity = (Preference)findPreference("keyToggleData");
		turnOffDataConnectivity = (Preference)findPreference("keyTurnOnData");
		turnOnDataConnectivity = (Preference)findPreference("keyTurnOffData");
		dataConnectivitySettings = (Preference)findPreference("keyDataSettings");

		toggleSync = (Preference)findPreference("keyToggleSync");
		turnOffSync = (Preference)findPreference("keyTurnOnSync");
		turnOnSync = (Preference)findPreference("keyTurnOffSync");
		syncSettings = (Preference)findPreference("keySyncSettings");

		toggleBrightness = (Preference)findPreference("keyToggleBrightness");
		minBrightness = (Preference)findPreference("keyMinBrightness");
		medBrightness = (Preference)findPreference("keyMediumBrightness");
		maxBrightness = (Preference)findPreference("keyMaxBrightness");
		displaySettings = (Preference)findPreference("keyDisplaySettings");

		togggleSound = (Preference)findPreference("keyToggleSound");
		muteSound = (Preference)findPreference("keyMuteSound");
		vibrateSound = (Preference)findPreference("keyVibrateSound");
		normalSound = (Preference)findPreference("keyNormalSound");
		soundSettings = (Preference)findPreference("keySoundSettings");
		/*
		togglePlayPause = (Preference)findPreference("keyTogglePlayPause");
		playMusic = (Preference)findPreference("keyPlayMusic");
		pauseMusic = (Preference)findPreference("keyPauseMusic");
		 */
		//toggleFlash = (Preference)findPreference("keyToggleFlash");
		turnOnFlash = (Preference)findPreference("keyTurnOnFlash");
		turnOffFlash = (Preference)findPreference("keyTurnOffFlash");

		openGPSSettings = (Preference)findPreference("keyOpenGPSSettings");
		openFlightModeSettings = (Preference)findPreference("keyOpenFlightModeSettings");
		openBatteryInfo = (Preference)findPreference("keyOpenBatteryInfo");
		openFile = (Preference)findPreference("keyOpenFile");
		openTether = (Preference)findPreference("keyOpenTether");

		prefOpenURL = (PreferenceScreen)findPreference("keyOpenURL");
		callMailMsg = (Preference)findPreference("keyCallMailMsgSettings");
		try{
		if (hasFlash()) {

			prefScreenLED.setEnabled(true);

		}else{
			prefScreenLED.setEnabled(false);
		}
		}catch(Exception e){
			
		}



		prefScreenApps.setOnPreferenceClickListener(this);

		toggleBt.setOnPreferenceClickListener(this);
		turnOffBt.setOnPreferenceClickListener(this);
		turnOnBt.setOnPreferenceClickListener(this);
		turnBtVisibility.setOnPreferenceClickListener(this);
		btSettings.setOnPreferenceClickListener(this);

		toggleWifi.setOnPreferenceClickListener(this);
		turnOffWifi.setOnPreferenceClickListener(this);
		turnOnWifi.setOnPreferenceClickListener(this);
		wifiSettings.setOnPreferenceClickListener(this);

		toggleDataConnectivity.setOnPreferenceClickListener(this);
		turnOffDataConnectivity.setOnPreferenceClickListener(this);
		turnOnDataConnectivity.setOnPreferenceClickListener(this);
		dataConnectivitySettings.setOnPreferenceClickListener(this);

		toggleSync.setOnPreferenceClickListener(this);
		turnOffSync.setOnPreferenceClickListener(this);
		turnOnSync.setOnPreferenceClickListener(this);
		syncSettings.setOnPreferenceClickListener(this);

		toggleBrightness.setOnPreferenceClickListener(this);
		minBrightness.setOnPreferenceClickListener(this);
		medBrightness.setOnPreferenceClickListener(this);
		maxBrightness.setOnPreferenceClickListener(this);
		displaySettings.setOnPreferenceClickListener(this);

		togggleSound.setOnPreferenceClickListener(this);
		muteSound.setOnPreferenceClickListener(this);
		vibrateSound.setOnPreferenceClickListener(this);
		normalSound.setOnPreferenceClickListener(this);
		soundSettings.setOnPreferenceClickListener(this);
		/*
		togglePlayPause.setOnPreferenceClickListener(this);
		playMusic.setOnPreferenceClickListener(this);
		pauseMusic.setOnPreferenceClickListener(this);
		 */
		//toggleFlash.setOnPreferenceClickListener(this);
		turnOffFlash.setOnPreferenceClickListener(this);
		turnOnFlash.setOnPreferenceClickListener(this);

		openBatteryInfo.setOnPreferenceClickListener(this);
		openFlightModeSettings.setOnPreferenceClickListener(this);
		openGPSSettings.setOnPreferenceClickListener(this);
		openTether.setOnPreferenceClickListener(this);
		openFile.setOnPreferenceClickListener(this);

		prefOpenURL.setOnPreferenceClickListener(this);
		callMailMsg.setOnPreferenceClickListener(this);

		//toggleGps.setOnPreferenceClickListener(this);
	}



	public boolean hasFlash() {


		PackageManager pm = this.getPackageManager();
		if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return false;
		}
		Camera camera = Camera.open();
		Parameters p = camera.getParameters();
		List<String> flashModes = p.getSupportedFlashModes();
		if(flashModes==null){
			camera.release();
			return false;
		}else
		{
			camera.release();
			return true;
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(Thread.currentThread() == t){
			initSettings();
		}else if(Thread.currentThread() == threadApp){			
			loadApplicationList();
			p.dismiss();
		}else if(Thread.currentThread() == threadBookmarks){			
			loadBookmarks();
			p.dismiss();
		}

	}


	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if(preference == prefBookmarks[0]){
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("URL");
			alert.setMessage("Enter URL");

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {


					String url = input.getText().toString();
					databackIntent = new Intent();
					databackIntent.putExtra("action", 8);
					databackIntent.putExtra("option", url);
					databackIntent.putExtra("desc", "Open "+url);
					setResult(0, databackIntent);
					finish();
				}
			});
			alert.show();
			return false;
		}
		if(preference == prefScreenApps){
			lastState = 0;
			p  = ProgressDialog.show(this, "Loading ", "Loading Application List....", true);
			threadApp = new Thread(this);
			threadApp.start();			
			return false;
		}
		if(preference == prefOpenURL){
			lastState = 0;
			p  = ProgressDialog.show(this, "Loading ", "Loading Bookmarks....", true);
			threadBookmarks = new Thread(this);
			threadBookmarks.start();			
			return false;
		}
		lastState = 1;
		databackIntent = new Intent();
		if(preference == toggleBt){

			databackIntent.putExtra("action", 2);
			databackIntent.putExtra("option", "Bluetooth");
			databackIntent.putExtra("desc", "Toggle Bluetooth");
		}else if(preference == turnOffBt){

			databackIntent.putExtra("action", 0);
			databackIntent.putExtra("option", "Bluetooth");
			databackIntent.putExtra("desc", "Turn Off Bluetooth");

		}else if(preference == turnOnBt){

			databackIntent.putExtra("action", 1);
			databackIntent.putExtra("option", "Bluetooth");
			databackIntent.putExtra("desc", "Turn On Bluetooth");

		}else if(preference == turnBtVisibility){
			databackIntent.putExtra("action", 3);
			databackIntent.putExtra("option", "Bluetooth");
			databackIntent.putExtra("desc", "Bluetooth Visible for 2 minutes");

		}else if(preference == toggleWifi){

			databackIntent.putExtra("action", 2);
			databackIntent.putExtra("option", "Wifi");
			databackIntent.putExtra("desc", "Toggle Wifi");
		}else if(preference == turnOffWifi){

			databackIntent.putExtra("action", 0);
			databackIntent.putExtra("option", "Wifi");
			databackIntent.putExtra("desc", "Turn Off Wifi");

		}else if(preference == turnOnWifi){

			databackIntent.putExtra("action", 1);
			databackIntent.putExtra("option", "Wifi");
			databackIntent.putExtra("desc", "Turn On Wifi");

		}else if(preference == toggleDataConnectivity){	

			databackIntent.putExtra("action", 2);
			databackIntent.putExtra("option", "Data");
			databackIntent.putExtra("desc", "Toggle Data Connectivity");

		}else if(preference == turnOffDataConnectivity){

			databackIntent.putExtra("action", 1);
			databackIntent.putExtra("option", "Data");
			databackIntent.putExtra("desc", "Turn On Data Connectivity");

		}else if(preference == turnOffDataConnectivity){

			databackIntent.putExtra("action", 0);
			databackIntent.putExtra("option", "Data");
			databackIntent.putExtra("desc", "Turn Off Data Connectivity");

		}else if(preference == toggleSync){

			databackIntent.putExtra("action", 2);
			databackIntent.putExtra("option", "Sync");
			databackIntent.putExtra("desc", "Toggle Sync");

		}else if(preference == turnOffSync){
			databackIntent.putExtra("action", 0);
			databackIntent.putExtra("option", "Sync");
			databackIntent.putExtra("desc", "Turn Off Sync");
		}else if(preference == turnOnSync){

			databackIntent.putExtra("action", 1);
			databackIntent.putExtra("option", "Sync");
			databackIntent.putExtra("desc", "Turn On Sync");

		}else if(preference == toggleBrightness){

			databackIntent.putExtra("action", 2);
			databackIntent.putExtra("option", "Brightness");
			databackIntent.putExtra("desc", "Toggle Brightnese level");

		}else if(preference == minBrightness){

			databackIntent.putExtra("action", 0);
			databackIntent.putExtra("option", "Brightness");
			databackIntent.putExtra("desc", "Minimum Brightness");

		}else if(preference == medBrightness){

			databackIntent.putExtra("action", 3);
			databackIntent.putExtra("option", "Brightness");
			databackIntent.putExtra("desc", "Medium Brightness");

		}else if(preference == maxBrightness){

			databackIntent.putExtra("action", 1);
			databackIntent.putExtra("option", "Brightness");
			databackIntent.putExtra("desc", "Maximum Brightness");

		}else if(preference == togggleSound){

			databackIntent.putExtra("action", 2);
			databackIntent.putExtra("option", "Sound");
			databackIntent.putExtra("desc", "Toggle Sound Settings");

		}else if(preference == muteSound){

			databackIntent.putExtra("action", 0);
			databackIntent.putExtra("option", "Sound");
			databackIntent.putExtra("desc", "Mute Phone");

		}else if(preference == vibrateSound){

			databackIntent.putExtra("action", 3);
			databackIntent.putExtra("option", "Sound");
			databackIntent.putExtra("desc", "Vibrate Mode");

		}else if(preference == normalSound){

			databackIntent.putExtra("action", 1);
			databackIntent.putExtra("option", "Sound");
			databackIntent.putExtra("desc", "Normal Mode");

		}else if(preference == turnOffFlash){
			databackIntent.putExtra("action", 0);
			databackIntent.putExtra("option", "Flash");
			databackIntent.putExtra("desc", "Turn Off Flash");
		}else if(preference == turnOnFlash){

			databackIntent.putExtra("action", 1);
			databackIntent.putExtra("option", "Flash");
			databackIntent.putExtra("desc", "Turn On Flash");

		}
//			else if(preference == toggleFlash){
//
//			databackIntent.putExtra("action", 2);
//			databackIntent.putExtra("option", "Flash");
//			databackIntent.putExtra("desc", "Toggle Flash");
//
//		}
	/*else if(preference == togglePlayPause){

			databackIntent.putExtra("action", 2);
			databackIntent.putExtra("option", "Music");
			databackIntent.putExtra("desc", "Toggle Play Pause");

		}else if(preference == playMusic){

			databackIntent.putExtra("action", 1);
			databackIntent.putExtra("option", "Music");
			databackIntent.putExtra("desc", "Play Music");

		}else if(preference == pauseMusic){
			databackIntent.putExtra("action", 0);
			databackIntent.putExtra("option", "Music");
			databackIntent.putExtra("desc", "Pause Music");
		}*/else if(preference == wifiSettings){
			databackIntent.putExtra("action", 4);
			databackIntent.putExtra("option", "com.android.settings,com.android.settings.wifi.WifiSettings");
			databackIntent.putExtra("desc", "Open Wifi Settings");
		}else if(preference == btSettings){
			databackIntent.putExtra("action", 4);
			databackIntent.putExtra("option", "com.android.settings,com.android.settings.bluetooth.BluetoothSettings");
			databackIntent.putExtra("desc", "Open Bluetooth Settings");
		}else if(preference == dataConnectivitySettings){
			databackIntent.putExtra("action", 4);
			databackIntent.putExtra("option",  "com.android.settings,com.android.settings.Settings$DataUsageSummaryActivity");
			databackIntent.putExtra("desc", "Open Data Settings");
		}else if(preference == syncSettings){
			databackIntent.putExtra("action", 4);
			databackIntent.putExtra("option", "com.android.settings,com.android.settings.accounts.SyncSettingsActivity");
			databackIntent.putExtra("desc", "Open Sync Settings");
		}else if(preference == displaySettings){
			databackIntent.putExtra("action", 4);
			databackIntent.putExtra("option", "com.android.settings,com.android.settings.Settings$DisplaySettingsActivity");
			databackIntent.putExtra("desc", "Open Display Settings");
		}else if(preference == soundSettings){
			databackIntent.putExtra("action", 4);
			databackIntent.putExtra("option", "com.android.settings,com.android.settings.Settings$SoundSettingsActivity");
			databackIntent.putExtra("desc", "Open Sound Settings");
		}else if(preference == openGPSSettings){
			databackIntent.putExtra("action", 4);
			databackIntent.putExtra("option", "com.android.settings,com.android.settings.Settings$LocationSettingsActivity");
			databackIntent.putExtra("desc",  "Open Location Settings");
		}else if(preference == openFlightModeSettings){
			databackIntent.putExtra("action", 4);
			databackIntent.putExtra("option", "com.android.settings,com.android.settings.Settings$WirelessSettingsActivity");
			databackIntent.putExtra("desc", "Open Airplane Mode Settings");
		}else if(preference == openBatteryInfo){
			databackIntent.putExtra("action", 4);
			databackIntent.putExtra("option", "com.android.settings,com.android.settings.fuelgauge.PowerUsageSummary");
			databackIntent.putExtra("desc", "Open Battery Settings");
		}else if(preference == openTether){
			databackIntent.putExtra("action", 4);
			databackIntent.putExtra("option", "com.android.settings,com.android.settings.TetherSettings");
			databackIntent.putExtra("desc", "Open Tethering & portable hotspot Settings");
		}else if(preference == openFile){
			Intent fileSelect = new Intent(Intent.ACTION_GET_CONTENT);
			fileSelect.setType("file/*");
			startActivityForResult(fileSelect, FILESELECT);
			return false;
		}else if(preference == callMailMsg){

			initContacts();
			return false;
		}
		try{
			setResult(0, databackIntent);
			finish();
		}catch(Exception E){

		}		
		return false;
	}






	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		if (requestCode == FILESELECT){
			try{
				String filePath = "";

				/************************************************************************************
				 * Try to getRealfilePathFromUri not working for some file manager
				 */
				try{					
					filePath = getRealPathFromUri(this, data.getData());
				}catch(Exception E){
					filePath = data.getData().getPath();	
					if(filePath.startsWith("file://")){
						filePath = filePath.replace("file://", "");
					}
				}
				databackIntent = new Intent();
				databackIntent.putExtra("action", 7);
				databackIntent.putExtra("option", filePath);
				databackIntent.putExtra("desc", "Open File");
				setResult(0, databackIntent);
				finish();
			}catch(Exception E){
				E.printStackTrace();
			}

		}else if ( requestCode == NUMSELECT) {
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c =  managedQuery(contactData, null, null, null, null);
				if (c.moveToFirst()) {


					String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

					//String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
					int i =0;
					//if (hasPhone.equalsIgnoreCase("1")) 
					{
						Cursor phones = getContentResolver().query( 
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id, 
								null, null);

						if(phones.moveToFirst()){							
							do{
								try{
									numbers[i] = new String();
									typeName[i] = new String();
									type[i] = Integer.valueOf(TYPE_PHONE); 
									typeName[i] = "Unknown";
									numbers[i] = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									int phonetype = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
									String customLabel = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
									String phoneLabel = (String) ContactsContract.CommonDataKinds.Email.getTypeLabel(this.getResources(), phonetype, customLabel);                       
									typeName[i] = phoneLabel;




									i++;
								}catch(Exception E){

								}
							}while(phones.moveToNext());							
						}
						Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
								null,
								ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);
						if(emailCursor.moveToFirst()){

							do
							{

								numbers[i] = new String();
								typeName[i] = new String();
								type[i] = Integer.valueOf(TYPE_EMAIL);
								numbers[i] = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
								int emailType = emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
								typeName[i] = (String) ContactsContract.CommonDataKinds.Email.getTypeLabel(this.getResources(), emailType, "");

								//Log.d("Email"m )
								i++;

							}while (emailCursor.moveToNext());
						}

						emailCursor.close();

					}
					try{
						String userNumbers[]= new String[i];
						for(int j = 0; j < i;j++){
							userNumbers[j] = numbers[j]+"\n"+typeName[j];

						}

						final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
						// set title
						alertDialogBuilder.setTitle("Select");

						// set dialog message
						alertDialogBuilder
						.setSingleChoiceItems(userNumbers, -1,  new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								selectedIndex = arg1;

							}
						}).setCancelable(true);

						alertDialogBuilder.setPositiveButton("Message", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								seletPhoneNumber(arg1);
							}
						}).setNegativeButton("Call", new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								seletPhoneNumber(arg1);

							}
						}).setNeutralButton("Email", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								seletPhoneNumber(which);

							}
						});

						alertDialog = alertDialogBuilder.create();

						// show it
						alertDialog.show();


						contactName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						//Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();



					}catch(Exception E){
						System.out.print("EXCEPTION="+E);
					}

				}
			}
			return;
		}



	}

	@SuppressLint("NewApi")
	private void loadApplicationList(){

		PreferenceCategory targetCategory = (PreferenceCategory)findPreference("catApp");
		Preference prefPackage[] = new Preference[10000];
		final PackageManager pm = getPackageManager();
		/*
		List<ApplicationInfo> installedApps = getApplicationContext().getPackageManager().getInstalledApplications(PackageManager.PERMISSION_GRANTED);
		List<ApplicationInfo> launchableInstalledApps = new ArrayList<ApplicationInfo>();
		for(int i =0; i<installedApps.size(); i++){
			if(getApplicationContext().getPackageManager().getLaunchIntentForPackage(installedApps.get(i).packageName) != null){
				//If you're here, then this is a launch-able app
				launchableInstalledApps.add(installedApps.get(i));
			}
		}
		 */



		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> apps = pm.queryIntentActivities(
				mainIntent, 0);
		Collections.sort(apps, new ResolveInfo.DisplayNameComparator(
				pm));

		//List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		int i=0;
		for (ResolveInfo packageInfo : apps) {
			prefPackage[i] = new Preference(this);
			try{
				if(android.os.Build.VERSION.SDK_INT >= 11){
					prefPackage[i].setIcon(pm.getApplicationIcon(packageInfo.activityInfo.applicationInfo.packageName));
				}
			}catch(Exception E){

			}
			try{
				packageList[i] = packageInfo.activityInfo.packageName + "," + packageInfo.activityInfo.name;
				prefPackage[i].setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						System.out.println(packageList[preference.getOrder()]);
						Intent databackIntent = new Intent();
						databackIntent.putExtra("action", 4 );
						databackIntent.putExtra("option", packageList[preference.getOrder()]);
						databackIntent.putExtra("desc", "Open "+packageNames[preference.getOrder()]);
						setResult(0, databackIntent);
						finish();		
						return false;
					}
				});
				try {
					packageNames[i] = new String(packageInfo.loadLabel(pm).toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				prefPackage[i].setTitle(packageNames[i]);			
				targetCategory.addPreference(prefPackage[i]);

				i++;
			}catch(Exception E){

			}

		}


	}

	private void loadBookmarks(){

		PreferenceCategory targetCategory = (PreferenceCategory)findPreference("catURL");		
		Cursor mCur = this.managedQuery(Browser.BOOKMARKS_URI,
				Browser.HISTORY_PROJECTION, null, null, null);
		int i = 0;
		prefBookmarks[i] = new Preference(this);		    
		prefBookmarks[i].setTitle("Enter URL");
		prefBookmarks[i].setSummary("Enter custom URL");
		prefBookmarks[i].setOnPreferenceClickListener(this);
		targetCategory.addPreference(prefBookmarks[i]);		
		i++;
		final String urls[] = new String[10000];
		final String bookmark[] = new String[10000];
		if (mCur.moveToFirst()) {
			while (mCur.isAfterLast() == false) {

				prefBookmarks[i] = new Preference(this);
				targetCategory.addPreference(prefBookmarks[i]);
				bookmark[i] = mCur.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX);
				urls[i] = mCur.getString(Browser.HISTORY_PROJECTION_URL_INDEX);
				prefBookmarks[i].setTitle(mCur.getString(Browser.HISTORY_PROJECTION_TITLE_INDEX));
				prefBookmarks[i].setSummary(mCur.getString(Browser.HISTORY_PROJECTION_URL_INDEX));
				prefBookmarks[i].setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						databackIntent = new Intent();
						databackIntent.putExtra("action", 8);
						databackIntent.putExtra("option", urls[arg0.getOrder()]);
						databackIntent.putExtra("desc", "Open "+ bookmark[arg0.getOrder()]);
						setResult(0, databackIntent);
						finish();
						return false;
					}
				});
				mCur.moveToNext();
				targetCategory.addPreference(prefBookmarks[i]);
				i++;
			}
		}

	}

	private void seletPhoneNumber(int which) {
		// TODO Auto-generated method stub
		int action = -4;
		String desc = "";
		if(which == -1){
			action = 5;
			desc = "Text ";
		}else if(which == -2){
			action = 6;
			desc = "Call ";
		}else if(which == -3){
			if(type[selectedIndex] != TYPE_EMAIL){
				Toast errorToast = Toast.makeText(this, "Please Select an Email to continue", Toast.LENGTH_SHORT);
				errorToast.setGravity(Gravity.CENTER, 0, 0);
				errorToast.show();
				return;
			}
			action = 9;
			desc = "Email ";
		}
		if(selectedIndex!=-1){
			databackIntent = new Intent();
			databackIntent.putExtra("action", action);
			databackIntent.putExtra("option", numbers[selectedIndex]);
			databackIntent.putExtra("desc", desc+contactName+" "+typeName[selectedIndex]);
			setResult(0, databackIntent);
			finish();
		}

	}










	public void initContacts(){


		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, NUMSELECT);
	}

	public String getRealPathFromUri(Activity activity, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}





}

