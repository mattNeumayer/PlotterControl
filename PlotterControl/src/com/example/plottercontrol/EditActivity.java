package com.example.plottercontrol;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class EditActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_CONNECT = 2;

	private boolean connected = false;

	private QuSystemUIHider mQuSystemUIHider;
	private BluetoothController mBluetoothController;

	private Button printBtn;
	private PDrawingView mPDrawingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
		getWindow().setBackgroundDrawable(colorDrawable);

		setContentView(R.layout.activity_fullscreen);

		mPDrawingView = (PDrawingView) findViewById(R.id.PDrawingView);
		mPDrawingView.setEditActivityRef(this);
		printBtn = (Button) findViewById(R.id.print_button);
		final View controlsView = findViewById(R.id.fullscreen_content_controls);

		mQuSystemUIHider = new QuSystemUIHider(this, mPDrawingView,
				controlsView);

		mBluetoothController = new BluetoothController(
				mPDrawingView.getDataStream());

		changeLayout(false);

		printBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (connected) {
					if (mPDrawingView.flushData()) {
						mBluetoothController.send(EditActivity.this
								.getApplicationContext());
					}
				} else {
					initBluetoothConnection();
				}
			}
		});

		mPDrawingView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mQuSystemUIHider.hide();
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPDrawingView.startDrawing();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPDrawingView.pauseDrawing();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.test, menu);
		return true;
	}

	public QuSystemUIHider getQuSystemUIHider() {
		return mQuSystemUIHider;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				chooseBluetoothDevice(true);
			}
		} else if (requestCode == REQUEST_CONNECT) {
			chooseBluetoothDevice(false);
		}
	}

	private void chooseBluetoothDevice(boolean openSettings) {
		Set<BluetoothDevice> bluetoothDevices = BluetoothAdapter
				.getDefaultAdapter().getBondedDevices();
		if (!bluetoothDevices.isEmpty()) {
			if (mBluetoothController.connectToBluetoothDevice(bluetoothDevices
					.iterator().next())) {
				changeLayout(true);
			}
		} else if (openSettings) {
			Intent intentBluetooth = new Intent();
			intentBluetooth
					.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
			startActivityForResult(intentBluetooth, REQUEST_CONNECT);
		} else {
			Log.i(TAG, "No BluetoothDevice bonded!");
			Toast.makeText(this, "No NXT connected!", Toast.LENGTH_SHORT)
					.show();
			//TODO remove the next lines before distribution!
			Toast.makeText(this, "OutputStream redirected to Toaster! (DEBUG ONLY)", Toast.LENGTH_SHORT)
			.show();
			changeLayout(true);
		}
	}

	private void changeLayout(boolean connected) {
		this.connected = connected;
		if (connected) {
			printBtn.setText(R.string.btn_print);
		} else {
			printBtn.setText(R.string.btn_connect);
		}
	}

	private void initBluetoothConnection() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Log.w(TAG, "No Bluetooth available!");
			Toast.makeText(this, "No Bluetooth available!", Toast.LENGTH_SHORT)
					.show();
		}
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			chooseBluetoothDevice(true);
		}
	}
}
