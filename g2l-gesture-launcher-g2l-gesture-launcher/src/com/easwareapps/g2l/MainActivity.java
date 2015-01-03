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
import java.util.Iterator;
import java.util.Set;

import com.easwareapps.g2l.R;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView.AdapterContextMenuInfo;

@SuppressLint("NewApi")
public class MainActivity extends PreferenceActivity  implements Runnable{


	String DEBUG_TAG = "DEBUG";	
	Preference layoutGestures[] = new Preference[10000];
	Integer id[] = new Integer[10000];
	G2LDataBaseHandler dbh = null;
	ContextMenu subMenu = null;
	GestureLibrary gestureLib = null;
	ProgressDialog p;
	int layoutIndex = 0;
	boolean unreg = false;
	Context context = null; 
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {

		//setContentView(R.layout.g2l_main);
		context = this;
		dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
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
		getOverflowMenu();
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.g2l_main);
		registerForContextMenu(getListView());
		p  = ProgressDialog.show(this, "Loading ", "Loading Gesture List....", true);		
		Thread t = new Thread(this);
		t.start();	




	}


	private void getOverflowMenu() {

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			java.lang.reflect.Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}





	@Override
	public void run() {
		// TODO Auto-generated method stub
		initGestures();
		p.dismiss();



	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}



	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent i = null;
		switch (item.getItemId()){
		case R.id.action_settings:
			i=new Intent(MainActivity.this, AddGesture.class);
			startActivityForResult(i,0);
			return true;
		case R.id.action_preference:
			i=new Intent(MainActivity.this, AppPreference.class);
			startActivity(i);
			return true;
		case R.id.action_support: 
			String url = "http://g2l.easwareapps.com#donate";
			i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		Thread t = new Thread(this);
		t.start();

	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0,1,0,"Edit");
		menu.add(0,2,0,"Delete");		
		menu.add(0,3,0,"Cancel");

	}

	@SuppressWarnings("rawtypes")
	private void initGestures(){




		String appPath = Environment.getExternalStorageDirectory()+"/.g2l/";

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


		Iterator iter = null;
		try{
			Set<String> gestureNames = gestureLib.getGestureEntries();
			iter = gestureNames.iterator();
		}catch(Exception E){
			return;
		}
		layoutIndex = 0;

		@SuppressWarnings("deprecation")
		PreferenceScreen targetCategory = (PreferenceScreen)findPreference("prefScreen");
		targetCategory.removeAll();
		G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), "name",null, 1);

		//Cursor c = dbh.getActionOption(0);
		while (iter.hasNext()) {
			String gestureName = iter.next().toString();
			File imagePath = new File(appPath+"/"+gestureName+".png");
			Bitmap bitmap;
			if(imagePath.exists()){
				bitmap = BitmapFactory.decodeFile(appPath+"/"+gestureName+".png");
			}else{
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			}

			String actionName = dbh.getActionOption(Integer.parseInt(gestureName));
			if(actionName == ""){
				dbh.deleteGesture(Integer.parseInt(gestureName));
				continue;
			}
			layoutGestures[layoutIndex] = new Preference(this);
			if(android.os.Build.VERSION.SDK_INT >= 11) {				
				layoutGestures[layoutIndex].setIcon(new BitmapDrawable(getResources(),bitmap));
			}else{
				
			}
			layoutGestures[layoutIndex].setTitle(actionName);

			id[layoutIndex] = Integer.parseInt(gestureName);

			targetCategory.addPreference(layoutGestures[layoutIndex]);

			layoutIndex++;
		}	
		try{
			dbh.close();
		}catch(Exception E){

		}
		if(layoutIndex == 0){
			layoutGestures[layoutIndex] = new Preference(this);
			layoutGestures[layoutIndex].setTitle("No gestures added yet.");
			layoutGestures[layoutIndex].setSummary(" To add gestures go to Menu -> Add Gestures");			

			targetCategory.addPreference(layoutGestures[layoutIndex]);
			unregisterForContextMenu(getListView());
			unreg = true;


		}else if(unreg){
			registerForContextMenu(getListView());
		}

		//allGestures.bind(listGesturesViewList);
	}


	public boolean onContextItemSelected(MenuItem item) {
		int menuId = item.getItemId();
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch(menuId){
		case 1: editGesture(info.position);break;
		case 2: AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Confirmation");
		builder.setMessage("Do you really want to delete this ?");
		builder.setPositiveButton("Yes", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				deleteGesture(info.position);
				
			}
		}).setNegativeButton("No", null);
		builder.show();
		}
		return true;
	}




	private void editGesture(int position) {
		// TODO Auto-generated method stub
		Intent i=new Intent(MainActivity.this, AddGesture.class);
		Bundle b = new Bundle();
		Log.d("INFO.POSITION",""+id[position]);
		b.putInt("id", id[position]); //Your id
		i.putExtras(b);
		startActivityForResult(i, 0);
				


	}

	private boolean deleteGesture(int position) {
		// TODO Auto-generated method stub
		try{
			gestureLib.removeEntry(""+id[position]);
			gestureLib.save();
			dbh.deleteGesture(id[position]);
			resortFrom(position);
			dbh.close();
		}catch(Exception E){
			E.printStackTrace();
		}		
		
		@SuppressWarnings("deprecation")
		PreferenceScreen targetCategory = (PreferenceScreen)findPreference("prefScreen");
		targetCategory.removePreference(targetCategory.getPreference(position));
		//initGestures();
		return false;

	}

	private void resortFrom(int position) {

		for(int i = position; i < id.length-1; i++){
			id[i] = id[i+1];			
		}
		layoutIndex --;
		if(position == 0 && layoutIndex == 0){
			@SuppressWarnings("deprecation")			
			PreferenceScreen targetCategory = (PreferenceScreen)findPreference("prefScreen");
			layoutGestures[layoutIndex] = new Preference(this);
			layoutGestures[layoutIndex].setTitle("No gestures added yet.");
			layoutGestures[layoutIndex].setSummary(" To add gestures go to Menu -> Add Gestures");

			targetCategory.addPreference(layoutGestures[layoutIndex]);
			unregisterForContextMenu(getListView());
			unreg = true;



		}

	}




}