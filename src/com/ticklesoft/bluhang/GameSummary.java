package com.ticklesoft.bluhang;

import com.ticklesoft.wordapult.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Prompts the player the result of their game. Informs them of how many coins
 * earned and which achievements were earned.
 */
public class GameSummary extends Activity implements OnClickListener {

	ImageButton ibShop, ibMenu;
	TextView tvWordOutput, tvCoinsWon;
	ImageView resultPicture, ivAnswered, ivFirst, ivStreak, ivPerfect;
	Bundle gotBasket;
	int coins;
	SharedPreferences settings;
	MediaPlayer outcomeSound;
	boolean win;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		settings = getSharedPreferences(Utils.PREFS_NAME, 0);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamesummary);
	
		ibShop = (ImageButton) findViewById(R.id.ibgoShop);
		ibMenu = (ImageButton) findViewById(R.id.ibgoMenu);
		ibShop.setOnClickListener(this);
		ibMenu.setOnClickListener(this);
		resultPicture = (ImageView) findViewById(R.id.ivwinpic);
		tvCoinsWon = (TextView) findViewById(R.id.tvcoinswon);
		tvWordOutput = (TextView) findViewById(R.id.tvword);
		ivAnswered = (ImageView) findViewById(R.id.ivAnswered);
		ivFirst = (ImageView) findViewById(R.id.ivFirst);
		ivStreak = (ImageView) findViewById(R.id.ivStreak);
		ivPerfect = (ImageView) findViewById(R.id.ivPerfect);

		gotBasket = getIntent().getExtras();
		win = gotBasket.getBoolean("win");
		setOutcome();
		playOutcomeSound();
		saveGoldCoins();
	}

	/**
	 * Plays sound according to wheter player has won or lost.
	 */
	private void playOutcomeSound() {
		if (settings.getBoolean("pmusic", true)) {
			if (win)
				outcomeSound = MediaPlayer.create(this, R.raw.gamewin);
			else
				outcomeSound = MediaPlayer.create(this, R.raw.gameloss);

			outcomeSound.start();
		}
	}

	/**
	 * Player wins without guess wrong once.
	 */
	private void setPerfect() {
		boolean perfectGame = gotBasket.getBoolean("perfectGame");
		if (perfectGame) {
			ivPerfect.setVisibility(View.VISIBLE);
			coins = coins + 10;
		}

	}

	/**
	 * Saves the coins that the player has earned.
	 */
	private void saveGoldCoins() {
		if (win) {
			tvCoinsWon.setText("You Earnt " + coins + " coins!");
			int totalCoins = settings.getInt("coins", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("coins", totalCoins + coins);
			editor.commit();
		} else {
			tvCoinsWon.setText("Solve the word to get coins");
			resultPicture.setImageResource(R.drawable.ivloosepic);
		}
	}

	/**
	 * If won 5 in a row give points.
	 */
	private void setStreak() {
		int thestreak = gotBasket.getInt("streak");
		if (thestreak % 5 == 0 && thestreak != 0) {
			coins += 5;
			ivStreak.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Give points if the word is guessed correctly for first time.
	 */
	private void setFirstTime() {
		boolean first = gotBasket.getBoolean("firstTime");
		if (first) {
			coins += 10;
			ivFirst.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Outputs the word, formmated correctly.
	 */
	private void setString() {
		String theWord = gotBasket.getString("theWord");
		theWord = Utils.convertToUpper(theWord);
		tvWordOutput.setText(theWord);
	}

	/**
	 * Perfroms list of operations
	 */
	private void setOutcome() {
		setString();
		setFirstTime();
		setStreak();
		setPerfect();
		setWin();

	}

	/**
	 * Has player won?
	 */
	private void setWin() {
		if (win) {
			coins += 5;
			ivAnswered.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibgoMenu:
			Intent openPlay = new Intent("com.ticklesoft.wordapult.MENU");
			startActivity(openPlay);
			finish();
			break;
		case R.id.ibgoShop:
			Intent openShop = new Intent("com.ticklesoft.wordapult.SHOP");
			startActivity(openShop);
			finish();
			break;
		}
	}
	
	public void onPause(){
		outcomeSound.stop();
		super.onPause();
	}

}
