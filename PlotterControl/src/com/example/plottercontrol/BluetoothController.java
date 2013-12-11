package com.example.plottercontrol;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothController {
	private static final String TAG = BluetoothController.class.getSimpleName();

	private BluetoothSocket mBluetoothSocket;
	private static final String UUID_NXT = "00001101-0000-1000-8000-00805F9B34FB";

	private BufferedOutputStream mBTOutputStream;

	public BluetoothController() {
	}

	public boolean connectToBluetoothAddress(String bluetoothAdress) {
		try {
			mBluetoothSocket = BluetoothAdapter
					.getDefaultAdapter()
					.getRemoteDevice(bluetoothAdress)
					.createRfcommSocketToServiceRecord(
							UUID.fromString(UUID_NXT));
			if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
				BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
			}
			mBluetoothSocket.connect();
			mBTOutputStream = new BufferedOutputStream(
					mBluetoothSocket.getOutputStream());
		} catch (IOException e) {
			Log.w(TAG, "Unable to create a socket!");
			return false;
		}
		return true;
	}

	public boolean sendUp() {
		Log.i(TAG, "up");

		byte[] data = { 0x00, 0x00, 0x00, (byte) 0xFF };
		return sendBytes(data);
	}

	public boolean sendDown() {
		Log.i(TAG, "down");

		byte[] data = { 0x00, 0x00, 0x01, (byte) 0xFF };
		return sendBytes(data);
	}

	public boolean sendPoint(int x, int y) {
		Log.i(TAG, "moveTo " + x + " " + y);

		byte[] data = new byte[4];
		for (int count = 0; count <= 1; count++) {
			data[count] = (byte) x;
			x >>= 8;
		}
		for (int count = 2; count <= 3; count++) {
			data[count] = (byte) y;
			y >>= 8;
		}
		return sendBytes(data);
	}

	private boolean sendBytes(byte[] data) {
		try {
			mBTOutputStream.write(0x09);
			mBTOutputStream.write(0x00);

			mBTOutputStream.write(0x80);
			mBTOutputStream.write(0x09);
			mBTOutputStream.write(0x00);
			mBTOutputStream.write(0x05);

			mBTOutputStream.write(data);
			mBTOutputStream.write(0x00);

			mBTOutputStream.flush();

		} catch (IOException e) {
			Log.w(TAG, "Couldn't write to stream!");
			return false;
		}
		return true;
	}
}
