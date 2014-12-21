package com.ticklesoft.bluhang;

import java.util.List;

import com.ticklesoft.wordapult.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * Menu shows Avilable activities to launch
 */
public class Menu extends Activity implements OnClickListener {

	SoundPool sp;
	int pressSound;
	SharedPreferences settings;
	boolean currentSong;
	Intent backgroundMusic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		settings = getSharedPreferences(Utils.PREFS_NAME, 0);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		ImageButton ibPlay = (ImageButton) findViewById(R.id.ibplay);
		ImageButton ibMulti = (ImageButton) findViewById(R.id.ibmulti);
		ImageButton ibShop = (ImageButton) findViewById(R.id.ibshop);
		ImageButton ibOptions = (ImageButton) findViewById(R.id.iboptions);
		
		ibPlay.setOnClickListener(this);
		ibShop.setOnClickListener(this);
		ibMulti.setOnClickListener(this);
		ibOptions.setOnClickListener(this);
		
		sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		pressSound = sp.load(this, R.raw.crow, 1);
		currentSong = settings.getBoolean("pcurrent", false);
		backgroundMusic = new Intent(this, BackgroundSoundService.class);
		startService(backgroundMusic);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibplay:
			Intent openGame = new Intent("com.ticklesoft.wordapult.GAME");
			stopService(backgroundMusic);
			startActivity(openGame);
			break;
		case R.id.ibmulti:
			Intent openMulti = new Intent(
					"com.ticklesoft.wordapult.MULTIPLAYER");
			startActivity(openMulti);
			break;
		case R.id.ibshop:
			Intent openShop = new Intent("com.ticklesoft.wordapult.SHOP");
			startActivity(openShop);
			break;
		case R.id.iboptions:
			Intent openOptions = new Intent("com.ticklesoft.wordapult.OPTIONS");
			startActivity(openOptions);
			break;
		}
	}
	
	@Override
	protected void onPause() {
		if (this.isFinishing()) { // basically BACK was pressed from this
									// activity
			stopService(backgroundMusic);
		}
		Context context = getApplicationContext();
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		if (!taskInfo.isEmpty()) {
			ComponentName topActivity = taskInfo.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				stopService(backgroundMusic);
			} else {
			}
		}
		super.onPause();
	}

	protected void onResume() {
		startService(backgroundMusic);
		super.onResume();
	}

}
