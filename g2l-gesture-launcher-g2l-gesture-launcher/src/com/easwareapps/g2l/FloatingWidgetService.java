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
import com.easwareapps.g2l.R;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;


/*
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.SystemClock;
 */



public class FloatingWidgetService extends Service implements OnTouchListener{


	int qt = 0; 
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//super.onDestroy();
	}

	WindowManager.LayoutParams floatingWidgetParams = null;
	WindowManager.LayoutParams sideBarParams = null;
	WindowManager.LayoutParams closeIconParams = null;
	ImageView floatingIcon = null;
	ImageView swipeLaunch = null;
	ImageView closeIcon= null;
	WindowManager wmgr = null;

	boolean touchconsumedbyMove = false;
	int recButtonLastX;
	int recButtonLastY;
	int recButtonFirstX;
	int recButtonFirstY;
	int statusBarHeight;
	int uposx;
	int uposy;

	long lastTouchedTime = 0;
	int ALLOWDTIMEDIFF = 150;
	int sizes[] = {32, 48, 72, 96, 144};

	Thread t = null;
	int prevaction = -1;

	int WIDTH;
	int HEIGHT;	









	IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public FloatingWidgetService getServerInstance() {
			return FloatingWidgetService.this;
		}


	}




	public boolean stopFloatingWidget(){
		if(floatingIcon != null){
			try{
				wmgr.removeView(floatingIcon);			
				floatingIcon = null;
				if(swipeLaunch == null){
					stopSelf();
					return true;
				}
			}catch(Exception e){}

		}
		return false;
	}



	public void adjustQuickLauncherTypeAndPosition(){

		try{
			G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			try{							
				if(wmgr == null)	wmgr = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
				if(dbh.getSettings("enable_quick_launch").equals("true")){

					startFloatingWidget();
				}if(dbh.getSettings("enable_swipe_launch").equals("true")){
					if(!checkForCrashes()){
						startSwipeLaunch();
					}

				}


			}catch (Exception e) {			

			}
			if(floatingIcon == null && swipeLaunch == null){
				stopSelf();
			}
			dbh.close();

		}catch(Exception E){

		}

	}


	private boolean checkForCrashes() {
		// TODO Auto-generated method stub
		String appPath = Environment.getExternalStorageDirectory()+"/.g2l/";

		File appDir = new File(appPath);
		if(!(appDir.exists() && appDir.isDirectory())){
			appDir.mkdir();
		}

		File storeFile = new File(appPath+"/crash");
		if(storeFile.exists()){
			return true;
		}

		return false;
	}



	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void startSwipeLaunch(){
		if(swipeLaunch != null){
			try{			
				wmgr.removeView(swipeLaunch);
				sideBarParams = null;
				swipeLaunch = null;
			}catch(Exception E){

			}		
		}
		try{
			WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();	
			if (android.os.Build.VERSION.SDK_INT >= 13) {				
				display.getSize(size);
				WIDTH = size.x/2;
				HEIGHT = size.y/2;
			}else{
				WIDTH = display.getWidth()/2;
				HEIGHT = display.getHeight()/2;
			}


		}catch(Exception E){

		}
		statusBarHeight = (int)Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);

		try{			
			swipeLaunch = new ImageView(getApplicationContext());			
			sideBarParams =  new WindowManager.LayoutParams();			
			sideBarParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SCALED | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;																
			sideBarParams.format = PixelFormat.RGBA_8888;
			swipeLaunch.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_sidebar_portrait));
			sideBarParams.type = WindowManager.LayoutParams.TYPE_PHONE;			
			sideBarParams.width = 10;
			sideBarParams.height = (HEIGHT - statusBarHeight)*2;
			G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			int pos = 0;
			try{
				pos = Integer.parseInt(dbh.getSettings("swipe_launch_position"));
			}catch(Exception E){
				pos = 0;
			}
			switch (pos) {
			case 0:
				sideBarParams.x = -WIDTH;
				sideBarParams.y = -HEIGHT + statusBarHeight;
				swipeLaunch.setOnTouchListener(new OnSwipeTouchListener() {			    
					public void onSwipeRight() {
						startGestureDrawingScreen();
					}

				});
				break;
			case 1:
				sideBarParams.x = +WIDTH;
				sideBarParams.y = -HEIGHT + statusBarHeight;;
				swipeLaunch.setOnTouchListener(new OnSwipeTouchListener() {			    
					public void onSwipeLeft() {
						startGestureDrawingScreen();
					}

				});
				break;
			case 2:
				sideBarParams.width = WIDTH * 2;
				sideBarParams.height = 10;
				sideBarParams.x = -WIDTH;
				sideBarParams.y = +HEIGHT-statusBarHeight;
				swipeLaunch.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_sidebar_landscape));
				swipeLaunch.setOnTouchListener(new OnSwipeTouchListener() {			    
					public void onSwipeTop() {
						startGestureDrawingScreen();
					}

				});
				break;
			case 3:
				sideBarParams.width = WIDTH * 2;
				sideBarParams.height = 10;
				sideBarParams.x = +WIDTH;
				sideBarParams.y = -HEIGHT + statusBarHeight;;
				swipeLaunch.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_sidebar_landscape));
				swipeLaunch.setOnTouchListener(new OnSwipeTouchListener() {			    
					public void onSwipeBottom() {
						startGestureDrawingScreen();
					}

				});
				break;
			default:
				
				sideBarParams.x = -WIDTH;
				sideBarParams.y = -HEIGHT + statusBarHeight;;
				swipeLaunch.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_sidebar_portrait));
				swipeLaunch.setOnTouchListener(new OnSwipeTouchListener() {			    
					public void onSwipeRight() {
						startGestureDrawingScreen();
					}

				});
				break;
			}	
			wmgr.addView(swipeLaunch, sideBarParams);			
		}catch(Exception E){
			
		}

	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void startFloatingWidget(){

		if(floatingIcon != null){
			try{			
				wmgr.removeView(floatingIcon);				
				floatingIcon = null;				
				closeIcon = null;
			}catch(Exception E){

			}		
		}
		try{			
			closeIcon = new ImageView(getApplicationContext());
			closeIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_trash_closed));			
			closeIconParams = new WindowManager.LayoutParams();
			closeIconParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SCALED;
			closeIconParams.format = PixelFormat.RGBA_8888;
			closeIconParams.type = WindowManager.LayoutParams.TYPE_PHONE;
			closeIconParams.x = 0;
			closeIconParams.y = -180;

			floatingWidgetParams = new WindowManager.LayoutParams();			
			floatingWidgetParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SCALED | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;		
			floatingIcon = new ImageView(getApplicationContext());												

			floatingWidgetParams.format = PixelFormat.RGBA_8888;
			floatingWidgetParams.type = WindowManager.LayoutParams.TYPE_PHONE;


			DisplayMetrics displayMetrics = new DisplayMetrics();
			((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

			int displayType = 0;

			switch (displayMetrics.densityDpi) {


			case DisplayMetrics.DENSITY_XHIGH:
				statusBarHeight = 38;
				closeIconParams.width = 175;
				closeIconParams.height = 175;
				displayType = 3;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				statusBarHeight = 38;
				closeIconParams.width = 150;
				closeIconParams.height = 150;
				displayType = 2;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				statusBarHeight = 25;
				closeIconParams.width = 100;
				closeIconParams.height = 100;
				displayType = 1;
				break;
			case DisplayMetrics.DENSITY_LOW:
				statusBarHeight = 19;
				closeIconParams.width = 75;
				closeIconParams.height = 75;
				displayType = 0;
				break;
			case DisplayMetrics.DENSITY_XXHIGH:
				statusBarHeight = 38;
				closeIconParams.width = 200;
				closeIconParams.height = 200;
				displayType = 4;
				break;

			default:
				closeIconParams.width = 100;
				closeIconParams.height = 100;
				statusBarHeight = 25;
				displayType = 1;
			}

			int pos = 6;
			int alpha = 255;
			int iconSizeIndex = displayType;
			G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			try{							
				alpha = Integer.parseInt(dbh.getSettings("quicklauncher_alpha").toString());				
				pos = Integer.parseInt(dbh.getSettings("quicklauncher_position").toString());



			}catch (Exception e) {
				pos = 9;
			}
			try{
				iconSizeIndex = Integer.parseInt(dbh.getSettings("quicklauncher_size").toString());
				if(iconSizeIndex == -1){
					iconSizeIndex = displayType;
					dbh.setSettings("quicklauncher_size", displayType+"");
				}
			}catch(Exception E){
				iconSizeIndex = displayType;
				dbh.setSettings("quicklauncher_size", displayType+"");
			}


			floatingWidgetParams.width = sizes[iconSizeIndex];
			floatingWidgetParams.height = sizes[iconSizeIndex];


			WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();	
			if (android.os.Build.VERSION.SDK_INT >= 13) {				
				display.getSize(size);
				WIDTH = size.x/2;
				HEIGHT = size.y/2;
			}else{
				WIDTH = display.getWidth()/2;
				HEIGHT = display.getWidth()/2;
			}

			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
				int tmp = WIDTH;
				WIDTH = HEIGHT;
				HEIGHT = tmp;
			}

			switch(pos){
			case 0:floatingWidgetParams.x = -WIDTH;floatingWidgetParams.y = -HEIGHT + statusBarHeight;break;
			case 1:floatingWidgetParams.x = 0;floatingWidgetParams.y = -HEIGHT + statusBarHeight;break;
			case 2:floatingWidgetParams.x = WIDTH;floatingWidgetParams.y = -HEIGHT + statusBarHeight;break;
			case 3:floatingWidgetParams.x = -WIDTH;floatingWidgetParams.y = 0;break;
			case 4:floatingWidgetParams.x = 0;floatingWidgetParams.y = 0;break;
			case 5:floatingWidgetParams.x = WIDTH;floatingWidgetParams.y = 0;break;
			case 6:floatingWidgetParams.x = -WIDTH;floatingWidgetParams.y = HEIGHT;break;
			case 7:floatingWidgetParams.x = 0;floatingWidgetParams.y = HEIGHT;break;
			case 8:floatingWidgetParams.x = WIDTH;floatingWidgetParams.y = HEIGHT;break;
			default:


				try{
					floatingWidgetParams.x = Integer.parseInt(dbh.getSettings("quicklauncher_position").split(",")[0]);
					floatingWidgetParams.y = Integer.parseInt(dbh.getSettings("quicklauncher_position").split(",")[1]);
				}catch (Exception e) {
					floatingWidgetParams.x = -WIDTH;
					floatingWidgetParams.y = HEIGHT;
				}
			}
			try{
				dbh.close();
			}catch(Exception E){

			}
			uposx = floatingWidgetParams.x;
			uposy = floatingWidgetParams.y;


			floatingIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_quick_launch));
			floatingIcon.setAlpha(alpha);
			floatingIcon.setOnTouchListener(this);
			



			floatingIcon.setOnTouchListener(this);			
			wmgr.addView(floatingIcon,floatingWidgetParams);		

		}catch(Exception E){

		}





	}


	private void startGestureDrawingScreen() {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
					Intent i=new Intent(FloatingWidgetService.this, GestureLauncher.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					i.addCategory("android.intent.category.LAUNCHER");
					startActivity(i);
				}catch(Exception E){

				}
			}
		});
		t.start();
		

	}			    


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub		
		super.onConfigurationChanged(newConfig);
		try{
			adjustQuickLauncherTypeAndPosition();
		}catch(Exception e){}


	}













	public boolean onTouch(View v, MotionEvent event){
		if(floatingIcon == null){
			return true;
		}

		int totalDeltaX = recButtonLastX - recButtonFirstX;
		int totalDeltaY = recButtonLastY - recButtonFirstY;		
		switch(event.getActionMasked())
		{

		case MotionEvent.ACTION_DOWN:		
			prevaction = MotionEvent.ACTION_MOVE;
			lastTouchedTime = getSystemTime();
			recButtonLastX = (int) event.getRawX();
			recButtonLastY = (int) event.getRawY();
			recButtonFirstX = recButtonLastX;
			recButtonFirstY = recButtonLastY; 
			prevaction = MotionEvent.ACTION_DOWN;

			break;
		case MotionEvent.ACTION_UP:		
			prevaction = MotionEvent.ACTION_UP;
			Log.d("TIME DIFF", ""+(getSystemTime() - lastTouchedTime));
			if((getSystemTime() - lastTouchedTime) < ALLOWDTIMEDIFF){
				try{
					wmgr.removeView(closeIcon);
					G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
					dbh.setSettings("enable_quick_launch", "false");
					dbh.close();
				}catch(Exception E){

				}
				startGestureDrawingScreen();
				return false;
			}			
			int deltaX = (int) event.getRawX() - recButtonLastX;
			int deltaY = (int) event.getRawY() - recButtonLastY;
			recButtonLastX = (int) event.getRawX();
			recButtonLastY = (int) event.getRawY();
			if (Math.abs(totalDeltaX) >= 5  || Math.abs(totalDeltaY) >= 5) {
				if (event.getPointerCount() == 1) {
					floatingWidgetParams.x += deltaX;
					floatingWidgetParams.y += deltaY;									
					int cx = closeIconParams.x;
					int cy = closeIconParams.y;
					int cs = closeIconParams.width;
					if(floatingWidgetParams.x > (cx-cs) && floatingWidgetParams.x < (cx+cs) && floatingWidgetParams.y > (cy-cs) && floatingWidgetParams.y < (cy+cs)){

						if(swipeLaunch == null){
							stopSelf();
							System.exit(0);
						}else{
							try{
								G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
								dbh.setSettings("enable_quick_launch", "false");
								dbh.close();
								wmgr.removeView(floatingIcon);
								floatingIcon = null;
								wmgr.removeView(closeIcon);
								closeIcon = null;
							}catch(Exception e){}
						}


					}
				}
				else{
					touchconsumedbyMove = false;
				}
			}else{
				touchconsumedbyMove = false;
			}
			if(closeIcon!=null){
				try{
					wmgr.removeView(closeIcon);
				}catch(Exception E){

				}

			}
			uposx = floatingWidgetParams.x;
			uposy = floatingWidgetParams.y;



			G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			try{

				if(Integer.parseInt(dbh.getSettings("quicklauncher_position"))<9){
					dbh.close();
					break;
				}

			}catch (Exception e) {
				// TODO: handle exception

			}
			dbh.setSettings("quicklauncher_position", uposx+","+uposy);
			dbh.close();
			break;	
		case MotionEvent.ACTION_MOVE:			
			prevaction = MotionEvent.ACTION_MOVE;
			if((getSystemTime() - lastTouchedTime) < ALLOWDTIMEDIFF){
				prevaction = MotionEvent.ACTION_MOVE;
				return false;
			}else{				

			}
			if(prevaction != MotionEvent.ACTION_MOVE){
				floatingWidgetParams.x += (int)event.getX()+5;
				floatingWidgetParams.y += (int)event.getY()+5;					
				touchconsumedbyMove = true;              
				try{
					wmgr.updateViewLayout(floatingIcon, floatingWidgetParams);
				}catch(Exception e){}
				return true;
			}
			prevaction = MotionEvent.ACTION_MOVE;
			deltaX = (int) event.getRawX() - recButtonLastX;
			deltaY = (int) event.getRawY() - recButtonLastY;
			recButtonLastX = (int) event.getRawX();
			recButtonLastY = (int) event.getRawY();
			if (Math.abs(totalDeltaX) >= 5  || Math.abs(totalDeltaY) >= 5) {
				if (event.getPointerCount() == 1) {
					floatingWidgetParams.x += deltaX;
					floatingWidgetParams.y += deltaY;					
					touchconsumedbyMove = true;       
					try{
						wmgr.updateViewLayout(floatingIcon, floatingWidgetParams);
					}catch(Exception e){}
					int cx = closeIconParams.x;
					int cy = closeIconParams.y;
					int cs = closeIconParams.width;
					if(getSystemTime() - lastTouchedTime > ALLOWDTIMEDIFF){
						if(floatingWidgetParams.x > (cx-cs) && floatingWidgetParams.x < (cx+cs) && floatingWidgetParams.y > (cy-cs) && floatingWidgetParams.y < (cy+cs)){
							closeIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_trash_open));
						}else{
							closeIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_trash_closed));
						}
						try{
							wmgr.addView(closeIcon, closeIconParams);
						}catch(Exception E){

						}
					}
				}
				else{
					touchconsumedbyMove = false;
				}
			}else{
				touchconsumedbyMove = false;
			}
			break;
			//case MotionEvent.ACTION_
		default:
			try{
				wmgr.removeView(closeIcon);
			}catch(Exception E){

			}
			break;
		}
		return touchconsumedbyMove;
	}

	private long getSystemTime(){
		return System.currentTimeMillis(); 


	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		adjustQuickLauncherTypeAndPosition();
		return START_STICKY;	
	}


	public void saveQuickLauncherPosition() {
		// TODO Auto-generated method stub
		G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
		try{

			dbh.setSettings("quicklauncher_position", uposx+","+uposy);
		}catch (Exception e) {
			// TODO: handle exception

		}
		dbh.close();

	}






	public void setQuickLaunchSize(int index) {
		// TODO Auto-generated method stub
		if(floatingIcon != null){
			floatingWidgetParams.width = sizes[index];
			floatingWidgetParams.height = sizes[index];
			try{
				wmgr.updateViewLayout(floatingIcon, floatingWidgetParams);
			}catch(Exception e){}
		}

	}
	@SuppressWarnings("deprecation")
	public void setTransperancy(int alpha){
		if(alpha == -1){
			G2LDataBaseHandler dbh = new G2LDataBaseHandler(getApplicationContext(), null, null, 1);
			try{

				alpha = Integer.parseInt(dbh.getSettings("quicklauncher_alpha"));
			}catch (Exception e) {
				// TODO: handle exception

			}
			dbh.close();

		}
		if(floatingIcon!=null){
			floatingIcon.setAlpha(alpha);
		}

	}



	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub		
		super.onLowMemory();
	}



	public boolean stopSwipeLaunch() {
		// TODO Auto-generated method stub
		if(swipeLaunch != null){
			try{
				wmgr.removeView(swipeLaunch);
				swipeLaunch = null;
				if(floatingIcon == null){
					stopSelf();
					return true;
				}
			}catch(Exception E){
				E.printStackTrace();
			}
		}
		return false;
	}



	public void setQuickLaunchPosition() {
		// TODO Auto-generated method stub
		if(floatingIcon!=null){
			adjustQuickLauncherTypeAndPosition();
		}

	}



}