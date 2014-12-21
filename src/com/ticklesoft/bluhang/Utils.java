package com.ticklesoft.bluhang;

import java.util.UUID;

import android.content.Context;
import android.widget.Toast;

/**
 * A class of variables and methods that are used across several activities.
 */
public class Utils {

	public static final String PREFS_NAME = "MyPrefsFile";
	public static final int STARTACTIVITY = 1;
	public static final UUID myUUID = UUID.fromString("64ce4e30-de0d-11e3-8b68-0800200c9a66");
	public static final int ASCIIUNDERSCORE = 137;

	
	/**
	 * Words have to be stored in lowercase to be compared to letters. This
	 * method reformats the word for the user to see.
	 * 
	 */	
	static String convertToUpper(String upperCaseWord) {
		char[] charArray = upperCaseWord.toCharArray();
		for(int i = 0 ; i <= charArray.length; i++){
			if(i == 0 || charArray[i-1] == ' '){
				charArray[i] = Character.toUpperCase(charArray[i]);
			}
		}
		return new String(charArray);
	}
	
	/**
	 * Creates a toast message.
	 * @param context the app. Always called as this
	 * @param message the message to be toasted.
	 */
	static void toastMessage(Context context, String message) {
		Toast toast = Toast.makeText(context, message,
				Toast.LENGTH_SHORT);
		toast.show();
	}
}
