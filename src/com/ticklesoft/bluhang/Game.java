package com.ticklesoft.bluhang;

import java.util.List;
import java.util.Random;

import com.ticklesoft.wordapult.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Plays the hangman game. Used for Singleplayer and Multiplayer
 */
public class Game extends Activity implements OnClickListener {

	ImageButton keyB[] = new ImageButton[26];
	ImageButton freeLetter, surrender;
	ImageView Catapult;
	TextView tvWord;
	TextView Category;
	MediaPlayer gameSong;
	SharedPreferences settings;
	boolean perfectGame = true;
	int streak;
	String theWord;
	boolean wordLegal = false;
	char[] displayWord;
	WordBank wordBank;
	Bundle multiplayerSettings;
	int guesses = 1; // number of guesses taken

	char alphabet[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
			'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z' };

	public int xmlInput[] = { R.id.ibA, R.id.ibB, R.id.ibC, R.id.ibD, R.id.ibE,
			R.id.ibF, R.id.ibG, R.id.ibH, R.id.ibI, R.id.ibJ, R.id.ibK,
			R.id.ibL, R.id.ibM, R.id.ibN, R.id.ibO, R.id.ibP, R.id.ibQ,
			R.id.ibR, R.id.ibS, R.id.ibT, R.id.ibU, R.id.ibV, R.id.ibW,
			R.id.ibX, R.id.ibY, R.id.ibZ };

	public int catapultImage[] = { R.drawable.ib1, R.drawable.ib2,
			R.drawable.ib3, R.drawable.ib4, R.drawable.ib5, R.drawable.ib6,
			R.drawable.ib7, R.drawable.ib8, };

	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = getSharedPreferences(Utils.PREFS_NAME, 0);
		setContentView(R.layout.game);
		multiplayerSettings = getIntent().getExtras();
		playMusic();
		Catapult = (ImageView) findViewById(R.id.ivcatapult);
		freeLetter = (ImageButton) findViewById(R.id.ibFreeLetter);
		surrender = (ImageButton) findViewById(R.id.ibSurrender);
		Category = (TextView) findViewById(R.id.tvcategory);
		tvWord = (TextView) findViewById(R.id.tvanswer);
		Catapult.setImageResource(R.drawable.ib1);
		freeLetter.setOnClickListener(this);
		surrender.setOnClickListener(this);

		for (int i = 0; i < xmlInput.length; i++) {
			keyB[i] = (ImageButton) findViewById(xmlInput[i]);
			keyB[i].setOnClickListener(this);
		}

		// Singleplayer
		if (multiplayerSettings != null) {
			String multiWord = multiplayerSettings.getString("word", "ERROR");
			multiWord = multiWord.toLowerCase();
			createMultiGame(multiWord,
					multiplayerSettings.getString("hint", "error"));
			// multiplayer
		} else {
			wordBank = new WordBank();
			Toast.makeText(this, "Singleplayer", Toast.LENGTH_SHORT).show();
			theWord = wordBank.setWord(getRandomWord());
			Category.setText(wordBank.getCategory());
		}

		makeCharArray();
		tvWord.setText(displayToString());
	}

	private void playMusic() {
		if (settings.getBoolean("pmusic", true)) {
			gameSong = MediaPlayer.create(this, R.raw.gamesound);
			gameSong.start();
		}
	}

	/**
	 * Chooses a random word for the player to guess
	 */
	private int getRandomWord() {
		int tempNum;
		do {
			Random r = new Random();
			tempNum = r.nextInt(wordBank.sizeOfList());
			wordLegal = checkWordUnlock(tempNum);
		} while (!wordLegal);
		return tempNum;
	}

	/**
	 * Verifies that the word picked has been unlocked by the player
	 * 
	 * @param The
	 *            postion in array of the word
	 */
	private boolean checkWordUnlock(int tempNum) {
		String database = "d" + tempNum;
		boolean wordUnlocked = settings.getBoolean(database, false);
		if (tempNum <= 29) {
			return true;
		} else if (tempNum >= 30) {
			if (wordUnlocked)
				return true;
		}
		return false;
	}

	/**
	 * Converts the word to guess into a char array to be guessed
	 */
	private void makeCharArray() {
		displayWord = new char[theWord.length()];
		for (int i = 0; i < theWord.length(); i++) {
			if (theWord.charAt(i) == ' ')
				displayWord[i] = ' ';
			else
				displayWord[i] = '-';
		}
	}

	/**
	 * Sets up the word for multiplayer game
	 */
	private void createMultiGame(String word, String hint) {
		theWord = word;
		Category.setText(hint);
	}

	/**
	 * The player guesses a letter.
	 * 
	 * @param letter
	 *            guessed
	 * @param numerical
	 *            position of letter in alphabet
	 * 
	 */
	private void enterLetter(char letter, int numAlphabet) {
		boolean found = false;
		for (int i = 0; i < theWord.length(); i++) {
			if (letter != displayWord[i] && theWord.charAt(i) == letter) {
				found = true;
				displayWord[i] = letter;
			}
		}
		if (found) {
			boolean winState = checkForWin();
			String upperCaseWord = displayToString();
			upperCaseWord = Utils.convertToUpper(upperCaseWord);
			tvWord.setText(upperCaseWord);
			if (winState) {
				endGame(true);
			}
		} else {
			perfectGame = false;
			guesses++;
			boolean lossState = checkForLoss();
			if (lossState) {
				endGame(false);
			}
			changeImageView();
		}
		keyB[numAlphabet].setOnClickListener(null);
		keyB[numAlphabet].setVisibility(View.INVISIBLE);

	}

	/**
	 * Called after incorrect guess check for game loss
	 */
	private boolean checkForLoss() {
		if (guesses >= 7)
			return true;
		return false;
	}

	/**
	 * Called after correct guess. Check for game win.
	 */
	private boolean checkForWin() {
		if (displayToString().equals(theWord))
			return true;
		return false;
	}

	/**
	 * Called when game is over. Saves player win or lose Starts game summary
	 * screen
	 * 
	 * @param setWin
	 *            Win or lose
	 */
	private void endGame(boolean setWin) {
		if (settings.getBoolean("pmusic", true))
			gameSong.stop();
		Bundle basket = new Bundle();
		SharedPreferences.Editor editor = settings.edit();

		basket.putBoolean("win", setWin);

		if (setWin) {
			int streak = settings.getInt("streak", 0);
			editor.putInt("streak", streak + 1);
		} else {
			editor.putInt("streak", 0);
			perfectGame = false;
		}
		basket.putBoolean("perfectGame", perfectGame);
		basket.putInt("streak", streak);

		if (multiplayerSettings == null) { // excludes word unique from
											// multiplayer
			if (setWin
					&& settings.getBoolean(wordBank.getWord(), false) == false) {
				editor.putBoolean(wordBank.getWord(), true);
				basket.putBoolean("firstTime", setWin);
			}
		}

		editor.commit();

		basket.putString("theWord", theWord);
		Intent postGame = new Intent(Game.this, GameSummary.class);
		postGame.putExtras(basket);
		startActivity(postGame);
		finish();
	}

	private void changeImageView() {
		Catapult.setImageDrawable(getResources().getDrawable(
				catapultImage[guesses]));
	}

	@Override
	public void onClick(View m) {
		for (int i = 0; i < xmlInput.length; i++) {
			if (keyB[i] == m)
				enterLetter(alphabet[i], i);
		}

		switch (m.getId()) {
		case R.id.ibFreeLetter:
			int numOfFreeLetter = settings.getInt("freeLet", 3);
			if (numOfFreeLetter > 0) {
				char rightLetter = giveFreeLetter();
				SharedPreferences.Editor editor = settings.edit();
				numOfFreeLetter--;
				editor.putInt("freeLet", numOfFreeLetter);
				editor.commit();
				int numAlphabet = giveNumInAlphabet(rightLetter);
				enterLetter(rightLetter, numAlphabet);
				freeLetter.setOnClickListener(null);
			}
			break;
		case R.id.ibSurrender:
			endGame(false);
			break;
		}

	}

	private int giveNumInAlphabet(char rightLetter) {
		for (int i = 0; i <= alphabet.length; i++) {
			if (rightLetter == alphabet[i])
				return i;
		}
		return 0;
	}

	/**
	 * Called when player presses the free letter button A letter that has not
	 * been guessed is found and added
	 * 
	 */
	private char giveFreeLetter() {
		boolean letterFound = false;
		while (letterFound == false) {
			Random s = new Random();
			int n = s.nextInt(26);
			char tempChar = alphabet[n];
			for (int i = 0; i < theWord.length(); i++) {
				if (tempChar != displayWord[i] && tempChar == theWord.charAt(i)) {
					letterFound = true;
					return tempChar;
				}
			}
		}
		return 'a'; // error condition
	}

	public String displayToString() {
		return new String(displayWord);
	}

	@Override
	protected void onPause() {
		if (this.isFinishing()) { // basically BACK was pressed from this
									// activity
			gameSong.stop();
		}
		Context context = getApplicationContext();
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		if (!taskInfo.isEmpty()) {
			ComponentName topActivity = taskInfo.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				gameSong.stop();
			} else {
			}
		}
		super.onPause();
	}

	protected void onResume() {
		if (settings.getBoolean("pmusic", true))
			gameSong.start();
		super.onResume();
	}
}
