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
 * Options screen allows for sound and music to be turned on and off. Credits
 * also shown.
 */
public class Options extends Activity implements OnClickListener {

	ImageButton ibMusic;
	Intent backgroundMusic;
	boolean music, sound;
	SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		settings = getSharedPreferences(Utils.PREFS_NAME, 0);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);
		ibMusic = (ImageButton) findViewById(R.id.ibmusic);
		ibMusic.setOnClickListener(this);
		backgroundMusic = new Intent(this, BackgroundSoundService.class);
		music = settings.getBoolean("pmusic", true);
		sound = settings.getBoolean("psound", true);
		setMusicButtonView();
	}

	private void setMusicButtonView() {
		if (music)
			ibMusic.setImageResource(R.drawable.ibmusicon);
		else
			ibMusic.setImageResource(R.drawable.ibmusicoff);
	}

	@Override
	public void onClick(View v) {
		SharedPreferences.Editor editor = settings.edit();
		switch (v.getId()) {
		case R.id.ibmusic:
			music = !music;
			stopService(backgroundMusic);
			editor.putBoolean("pmusic", music);
			editor.commit();
			setMusicButtonView();
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
