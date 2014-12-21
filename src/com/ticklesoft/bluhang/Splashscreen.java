package com.ticklesoft.bluhang;

import com.ticklesoft.wordapult.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Splash screen loads at launch showing company logo                     
 */
public class Splashscreen extends Activity {
	MediaPlayer introSong;
	SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		introSong = MediaPlayer.create(Splashscreen.this, R.raw.djsound);
		settings = getSharedPreferences(Utils.PREFS_NAME, 0);
		boolean music = settings.getBoolean("pmusic", true);
		if (music)
			introSong.start();

		Thread timer = new Thread() {
			@Override
			public void run() {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent openMenu = new Intent(
							"com.ticklesoft.wordapult.MENU");
					startActivity(openMenu);
				}

			}
		};
		timer.start();

	}

	@Override
	protected void onPause() {
		super.onPause();
		introSong.release();
		finish();
	}

}
