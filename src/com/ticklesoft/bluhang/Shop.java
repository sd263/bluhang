package com.ticklesoft.bluhang;

import java.util.List;

import com.ticklesoft.wordapult.R;
import com.ticklesoft.wordapult.R.id;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The shop screen allows for users to unlock more words inside of categories
 * They can also buy free letter guesses.
 */
public class Shop extends Activity implements OnClickListener {

	SharedPreferences settings;
	int goldCoins, freeLetters;
	String categories[] = { "cat1", "cat2", "cat3", "cat4", "cat5", "cat6",
			"cat7", "cat8", "cat9", "cat10" };
	ImageButton catButtons[] = new ImageButton[10];
	TextView tvGoldCoins, tvFreeLetters;
	Intent backgroundMusic;
	Button bFreeGold;

	public int xmlInput[] = { R.id.ibSports, R.id.ibCompanies,
			R.id.ibLandmarks, R.id.ibBooks, R.id.ibStates, R.id.ibFruits,
			R.id.ibActors, R.id.ibCapitals, R.id.ibBands, R.id.ibAnimals, };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		settings = getSharedPreferences(Utils.PREFS_NAME, 0);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shop);

		backgroundMusic = new Intent(this, BackgroundSoundService.class);
		ImageButton ibbuyfive = (ImageButton) findViewById(R.id.ibfivelet);
		ImageButton ibbuyten = (ImageButton) findViewById(R.id.ibtenlet);
		bFreeGold = (Button) findViewById(R.id.bfreegold);
		ImageButton ibbuytwentyFive = (ImageButton) findViewById(R.id.ibtwofivelet);
		ibbuyfive.setOnClickListener(this);
		ibbuyten.setOnClickListener(this);
		ibbuytwentyFive.setOnClickListener(this);
		bFreeGold.setOnClickListener(this);

		tvGoldCoins = (TextView) findViewById(R.id.tvSCoins);
		tvFreeLetters = (TextView) findViewById(R.id.tvFreeNum);
		goldCoins = settings.getInt("coins", 0);
		freeLetters = settings.getInt("freeLet", 3);
		for (int i = 0; i < categories.length; i++) {
			catButtons[i] = (ImageButton) findViewById(xmlInput[i]);
			catButtons[i].setOnClickListener(this);
		}

		changeView();
	}

	private void changeView() {
		tvGoldCoins.setText(goldCoins + " coins");
		tvFreeLetters.setText(freeLetters + " free letters");
		for (int n = 0; n < categories.length; n++) {
			int categoryLevel = settings.getInt(categories[n], 0);
			String imageLocation = imageLocationBuilder(categoryLevel, n);
			int resID = getResources().getIdentifier(imageLocation, "drawable",
					getPackageName());
			catButtons[n].setImageResource(resID);
		}
	}

	private String imageLocationBuilder(int categoryLevel, int n) {
		String wordBuilder = "cat" + (n + 1) + "lv" + categoryLevel;
		return wordBuilder;
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < categories.length; i++) {
			if (catButtons[i] == v) {
				boolean enoughCoins = checkCoins(i);
				if (enoughCoins) {
					int categoryLevel = settings.getInt(categories[i], 0);
					categoryLevel++;
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt(categories[i], categoryLevel);

					editor.commit();
					changeView();
				}
			}
		}

		switch (v.getId()) {
		case R.id.ibfivelet:
			buyLetters(5, 25);
			break;
		case R.id.ibtenlet:;
			buyLetters(10, 40);
			break;
		case R.id.ibtwofivelet:
			buyLetters(25, 90);
			break;
			case R.id.bfreegold:
				SharedPreferences.Editor editor = settings.edit();
				goldCoins = goldCoins += 100;
				editor.putInt("coins", goldCoins);
				editor.commit();
				changeView();
		}

	}

	/**
	 * The player attempts to buy letters.
	 * @param amount the amount of letters
	 * @param cost the cost of the letters
	 */
	private void buyLetters(int amount, int cost) {
		if (goldCoins >= cost) {
			SharedPreferences.Editor editor = settings.edit();
			goldCoins = goldCoins - cost;
			freeLetters = freeLetters + amount;
			editor.putInt("coins", goldCoins);
			editor.putInt("freeLet", freeLetters);
			editor.commit();
			Utils.toastMessage(this,"You bought " + amount + " Free Letters!");
			changeView();
		} else
			Utils.toastMessage(this,"You don't have enough coins. :(");
	}

	/**
	 * Checks if player can afford. If they can unlock category and take away coins.
	 * @param i the category.
	 */
	private boolean checkCoins(int i) {
		int categoryLevel = settings.getInt(categories[i], 0);
		SharedPreferences.Editor editor = settings.edit();
		if (categoryLevel == 0) {
			if (goldCoins >= 25) {
				goldCoins = goldCoins - 25;
				editor.putInt("coins", goldCoins);
				for (int n = 30; n <= 170;) {
					int unlockWord = n + i;
					String database = "d" + unlockWord;
					editor.putBoolean(database, true);
					editor.commit();
					n += 10;
				}
				Utils.toastMessage(this,"New Words Unlocked!");
				return true;
			}
		} else if (categoryLevel == 1) {
			if (goldCoins >= 50) {
				goldCoins = goldCoins - 50;
				editor.putInt("coins", goldCoins);
				for (int n = 180; n <= 320;) {
					int unlockWord = n + i;
					String database = "d" + unlockWord;
					editor.putBoolean(database, true);
					editor.commit();
					n += 10;
				}
				Utils.toastMessage(this,"New Words Unlocked!");
				return true;
			}
		}
		return false;
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
