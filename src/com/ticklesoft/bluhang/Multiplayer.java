package com.ticklesoft.bluhang;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ticklesoft.wordapult.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Multiplayer screen shows all the avilable lobbies to join. Players can choose
 * the name that they want to show to other users.
 */
public class Multiplayer extends Activity implements OnClickListener {


	Handler handler;
	BluetoothAdapter bluetoothAdapter;
	Button CreateGame, refresh;
	ListView hostList;
	ProgressBar spinner;
	TextView playerName;
	ArrayList<String> hostAddress;
	ArrayAdapter<String> listAdapter;
	ArrayList<String> hosts;
	BroadcastReceiver mReceiver;
	Intent backgroundMusic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiplayer);
		playerName = (TextView) findViewById(R.id.tvMultiName);
		CreateGame = (Button) findViewById(R.id.bCreateGame);
		refresh = (Button) findViewById(R.id.bRefresh);
		spinner = (ProgressBar) findViewById(R.id.progressBar1);
		backgroundMusic = new Intent(this, BackgroundSoundService.class);
		hosts = new ArrayList<String>();
		hostAddress = new ArrayList<String>();
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, hosts);
		hostList = (ListView) findViewById(R.id.listhost);
		hostList.setAdapter(listAdapter);
		handler = new Handler(Looper.getMainLooper());

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // gets adapter
		setUpBluetooth();

		populateHostList();

		CreateGame.setOnClickListener(this);
		refresh.setOnClickListener(this);
	}
	

	/**
	 * Sets up the bluetoothadapter. If returns null phone does not
	 * support bluetooth. If !enabled needs to be turned on. 
	 * When successful, add device name.
	 */
	private void setUpBluetooth() {
		if (bluetoothAdapter == null) {
			// Device does not support Bluetooth
			Utils.toastMessage(this, "bluetooth not supported");
			Intent openMenu = new Intent("com.ticklesoft.wordapult.MENU");
			startActivity(openMenu);
			finish();
		}
		if (!bluetoothAdapter.isEnabled()) { // if bluetooth not on
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, Utils.STARTACTIVITY);
		}
		// set the device name
		String deviceName = bluetoothAdapter.getName();
		playerName.setText(playerName.getText() + deviceName);
	}

	@Override
	/**
	 * Returns the result of the attempt to turn on bluetooth.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) { // if bluetooth was turned on
			Toast.makeText(this, "Bluetooth turned on", Toast.LENGTH_SHORT)
					.show();
			populateHostList();
		} else {
			Toast.makeText(this, "Bluetooth is required for Multiplayer",
					Toast.LENGTH_SHORT).show();
			Intent openMenu = new Intent("com.ticklesoft.wordapult.MENU");
			startActivity(openMenu);
			finish();
		}
	}

	/**
	 * Populates the list with nearby avilable lobbies to join
	 */
	private void populateHostList() {

		spinner.setVisibility(View.VISIBLE);
		hostList.setVisibility(View.GONE);
		Utils.toastMessage(this, "Searching..");
		bluetoothAdapter.startDiscovery(); // starts discovery
		
		
		
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	if(!hosts.contains(device.getName())){
			        hosts.add(device.getName());
			        hostAddress.add(device.getAddress());
		    	}
		    }
		}

		/**
		 * Finds new devices.
		 */
		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// Get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent
							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// Add the name and address to an array adapter to show
					// in a ListView
					if (!hosts.contains(device.getName())) {
						hosts.add(device.getName());
						hostAddress.add(device.getAddress());
					}

				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
						.equals(action)) {

					hostList.setVisibility(View.VISIBLE);
					spinner.setVisibility(View.GONE);
				}
			}
		};
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter); // Don't forget to
													// unregister
													// during onDestroy

		/**
		 * List of all the items are clickable.
		 */
		hostList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Bundle basket = new Bundle();
				basket.putBoolean("player1", false);
				basket.putString("hostAddress", hostAddress.get(position));
				basket.putString("hostName", hosts.get(position));
				Intent openLobby = new Intent(
						"com.ticklesoft.wordapult.MULTILOBBY");
				openLobby.putExtras(basket);
				startActivity(openLobby);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bCreateGame:
			Bundle basket = new Bundle();
			basket.putBoolean("player1", true);
			Intent openLobby = new Intent("com.ticklesoft.wordapult.MULTILOBBY");
			openLobby.putExtras(basket);
			startActivity(openLobby);
			break;
		case R.id.bRefresh:
			hosts.clear();
			hostAddress.clear();
			populateHostList();
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

		bluetoothAdapter.cancelDiscovery();
	}

	protected void onResume() {
		startService(backgroundMusic);
		super.onResume();
	}

	protected void onDestroy() {
		this.unregisterReceiver(mReceiver); // Don't forget to unregister
		super.onDestroy();
	}

}
