package com.ticklesoft.bluhang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.ticklesoft.wordapult.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Used to connect two players together as either a Server or client. Allows for
 * a word to be created by the Server then sent to the client to solve.
 */
public class MultiLobby extends Activity implements OnClickListener {

	TextView playerOne;
	TextView playerTwo;
	TextView waiting;
	EditText word;
	EditText hint;
	Button startGame;
	Intent backgroundMusic;
	ServerThread serverThread;
	ClientThread clientThread;
	/**
	 * Contains player name and whether he is host or client
	 * 
	 */
	Bundle settings;
	BluetoothAdapter bluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multilobby);
		playerOne = (TextView) findViewById(R.id.playerOne);
		playerTwo = (TextView) findViewById(R.id.playerTwo);
		waiting = (TextView) findViewById(R.id.tvWait);
		startGame = (Button) findViewById(R.id.bStartGame);
		startGame.setOnClickListener(this);
		word = (EditText) findViewById(R.id.etMultiWord);
		hint = (EditText) findViewById(R.id.etMultiHint);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		WordBank wordBank = new WordBank();
		word.setText(wordBank.chooseRandomWord());
		hint.setText(wordBank.getCategory());
		backgroundMusic = new Intent(this, BackgroundSoundService.class);
		settings = getIntent().getExtras();

		// SERVER
		if (settings.getBoolean("player1")) {
			playerOne.setText(bluetoothAdapter.getName());
			setupServer();
			// CLIENT
		} else {
			TextView wordText = (TextView) findViewById(R.id.tvmultiWordText);
			TextView hintText = (TextView) findViewById(R.id.tvmultiWordHint);
			wordText.setVisibility(View.GONE);
			hintText.setVisibility(View.GONE);
			word.setVisibility(View.GONE);
			hint.setVisibility(View.GONE);
			playerOne.setText(settings.getString("hostName"));
			playerTwo.setText(bluetoothAdapter.getName());
			waiting.setText("ERROR: Not hosting or connection problem");
			setupClient();
		}
	}

	/**
	 * Recieves messages sent from MessageThread so that they can be 
	 * decrypted then update the UI, or launch activities accordingly.
	 */
	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String data = (String) msg.obj;
			int action = data.charAt(0) - 48;
			data = data.substring(1);

			// performed on SERVER recieves clients information
			if (action == 1) {
				waiting.setText("player has connected");
				playerTwo.setText(data);
				startGame.setVisibility(View.VISIBLE);
				serverThread.informConnected();

				// performed on CLIENT informs connected to server
			} else if (action == 2) {
				waiting.setText("Connected! Waiting for word to set.");

				// performed on CLIENT reads word and hint and launches activity
			} else if (action == 3) {
				int wordBreak = data.indexOf('_');
				String recievedWord = data.substring(0, wordBreak);
				String recievedHint = data.substring(wordBreak - 1,
						data.length() - 1);
				clientThread.tellServerGameJoined();
				Bundle gameOptions = new Bundle();
				gameOptions.putBoolean("multi", true);
				gameOptions.putString("word", recievedWord);
				gameOptions.putString("hint", recievedHint);
				Intent openGame = new Intent("com.ticklesoft.wordapult.GAME");
				openGame.putExtras(gameOptions);
				clientThread.cancel(); // new code
				startActivity(openGame);

				// performed on SERVER informs client has launched game.
			} else if (action == 4) {
				waiting.setText("The game has launched!");
				serverThread.cancel(); // new code
				startGame.setVisibility(View.GONE);
			}
		}
	};

	/**
	 * Sets up the client. Passes the HostAddress String that was selected from
	 * the according List to go with the ListView name. Launches ClientThread
	 */
	private void setupClient() {	
		BluetoothDevice bd = bluetoothAdapter.getRemoteDevice(settings
				.getString("hostAddress"));
		clientThread = new ClientThread(bd);
		clientThread.start();
	}

	private void setupServer() {
		serverThread = new ServerThread();
		makeDiscoverable();
		serverThread.start();
	}

	private void makeDiscoverable() {
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
		startActivity(discoverableIntent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bStartGame:
			String sendWord = word.getText().toString();
			String sendHint = hint.getText().toString();
			serverThread.sendWord(sendWord, sendHint);
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

	/** Used by Server to find a client to connect to. */
	class ServerThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;
		private BluetoothSocket socket;
		private MessageThread messageThread;

		public ServerThread() {
			// Use a temporary object that is later assigned to mmServerSocket,
			// because mmServerSocket is final
			BluetoothServerSocket tmp = null;

			try {
				// MY_UUID is the app's UUID string, also used by the client
				// code
				tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
						"BluHang", Utils.myUUID);
			} catch (IOException e) {
			}
			mmServerSocket = tmp;
		}

		public void run() {
			socket = null;
			// Keep listening until exception occurs or a socket is returned
			waiting.setText("Waiting for player to join..");
			while (true) {

				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					break;
				}
				// If a connection was accepted
				if (socket != null) {
					// Do work to manage the connection (in a separate thread)
					manageConnectedSocket(socket);
					try {
						mmServerSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
			}
		}

		/** Tells the client that they are connected. */
		private void informConnected() {
			String data = "2Inform";
			byte[] bytes = data.getBytes();
			messageThread.write(bytes);
		}

		/**
		 * Send the word to the client
		 * 
		 * @param word
		 *            the word
		 * @param hint
		 *            the hint for the word.
		 */
		private void sendWord(String word, String hint) {
			String data = 3 + word + "_" + hint;
			byte[] bytes = data.getBytes();
			messageThread.write(bytes);
		}

		/**
		 * Called when connection created. Starts messageThread for messages to
		 * be sent.
		 */
		private void manageConnectedSocket(BluetoothSocket socket) {
			messageThread = new MessageThread(socket);
			messageThread.start();
		}
	}

	/**
	 * Launched by upon creating activity. Attempts to connect to server using
	 * the UUID.
	 */
	private class ClientThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		MessageThread messageThread;

		/**
		 * @param device
		 *            remote device to connect to.
		 */
		public ClientThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				tmp = device.createRfcommSocketToServiceRecord(Utils.myUUID);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			bluetoothAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}

			// Do work to manage the connection (in a separate thread)
			manageConnectedSocket(mmSocket);
		}

		/**
		 * Usses socket to create a new messageThread for sending message. Sends
		 * first message informing the server of the connection.
		 */
		private void manageConnectedSocket(BluetoothSocket aSocket) {
			messageThread = new MessageThread(aSocket);
			messageThread.start();
			String alertJoin = '1' + bluetoothAdapter.getName();
			byte[] connectedMessage = alertJoin.getBytes();
			messageThread.write(connectedMessage);
		}

		/** Informs the Server that game is being joined to change their UI */
		private void tellServerGameJoined() {
			String alertJoin = '4' + "GameStarted";
			byte[] bytes = alertJoin.getBytes();
			messageThread.write(bytes);
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	/** Thread used to send messages between Client and Server */
	private class MessageThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public MessageThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		/**
		 * Constantly reads for messages being sent. If a message was recieved.
		 * A message object is created that passes the message to the UI thread
		 * using a handler.
		 */
		public void run() {
			byte[] buffer = new byte[1024]; // buffer store for the stream
			while (true) {
				try {
					mmInStream.read(buffer);
					String reconstructed = new String(buffer);
					Message msg = Message.obtain();
					msg.obj = reconstructed;
					msg.setTarget(handler);
					msg.sendToTarget();
				} catch (IOException e) {
					break;
				}
			}
		}

		/* Call this from the main activity to send data to the remote device */
		public void write(byte[] bytes) {
			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
			}
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}
}
