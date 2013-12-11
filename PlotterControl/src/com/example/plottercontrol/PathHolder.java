package com.example.plottercontrol;

import java.util.LinkedList;

import android.graphics.Path;
import android.graphics.Point;
import android.view.SurfaceHolder;

public class PathHolder implements SurfaceHolder.Callback {
	private static final String TAG = PDrawingView.class.getSimpleName();

	private BluetoothController mBluetoothController;

	private final int minOffset;
	private final int DIN_A4_Y = 2970;
	private final int DIN_A4_X = 2100;

	private float xMultiplier;
	private float yMultiplier;

	private Path unflushedPaths = new Path();
	private Path flushedPaths = new Path();

	private LinkedList<LinkedList<Point>> unflushedPoints = new LinkedList<LinkedList<Point>>();
	private LinkedList<LinkedList<Point>> flushedPoints = new LinkedList<LinkedList<Point>>();

	private boolean closed;
	private LinkedList<Point> curPathPoints;
	private float lastX;
	private float lastY;

	public PathHolder(int minOffset, BluetoothController bluetoothController) {
		mBluetoothController = bluetoothController;
		this.minOffset = minOffset;
		this.closed = true;
	}

	public void moveTo(float x, float y) {
		if (!closed) {
			close();
		}
		curPathPoints = new LinkedList<Point>();
		closed = false;

		add(x, y);
		unflushedPaths.moveTo(x, y);
	}

	public void lineTo(float x, float y) {
		if (closed) {
			moveTo(x, y);
			return;
		} else {
			if (Math.abs(lastX - x) > minOffset
					|| Math.abs(lastY - y) > minOffset) {
				add(x, y);
				unflushedPaths.lineTo(x, y);
			}
		}
	}

	public void add(float x, float y) {
		lastX = x;
		lastY = y;
		curPathPoints.add(new Point(Math.round(x * xMultiplier), Math.round(y
				* yMultiplier)));
	}

	public void close() {
		if (curPathPoints.size() > 1) {
			unflushedPoints.add(curPathPoints);
		}
		curPathPoints = null;
		closed = true;
	}

	public Path getUnflushedPaths() {
		return unflushedPaths;
	}

	public Path getFlushedPaths() {
		return flushedPaths;
	}

	public void flushData() {
		for (LinkedList<Point> line : unflushedPoints) {
			mBluetoothController.sendDown();
			for (Point p : line) {
				mBluetoothController.sendPoint(p.x, p.y);
			}
			mBluetoothController.sendUp();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		xMultiplier = (float) (DIN_A4_X) / width;
		yMultiplier = (float) (DIN_A4_Y) / height;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

}
