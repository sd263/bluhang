package com.ticklesoft.bluhang;

import com.ticklesoft.wordapult.R;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Used to play background music that continues through activies
 */
public class BackgroundSoundService extends Service {
	MediaPlayer player;
	SharedPreferences settings ;
	boolean music;
	
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {
		settings = getSharedPreferences(Utils.PREFS_NAME, 0);
		music = settings.getBoolean("pmusic", true);
		player = MediaPlayer.create(this, R.raw.menusound);
		player.setLooping(true); // Set looping
		player.setVolume(50, 50);
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		if(music)
		player.start();
		return 1;
	}

	public IBinder onUnBind(Intent arg0) {
		player.stop();
		return null;
	}

	public void onStop() {
		 player.stop();
		 player.release();
	}

	public void onPause() {
		player.pause();
		player.release();
	}

	@Override
	public void onDestroy() {
		player.stop();
		player.release();
	}

}