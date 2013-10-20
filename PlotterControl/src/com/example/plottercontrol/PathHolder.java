package com.example.plottercontrol;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

public class PathHolder {
	private static final String TAG = PDrawingView.class.getSimpleName();

	private PipedOutputStream mDataStream;

	private final int minOffset;
	private Path unflushedPaths = new Path();
	private Path flushedPaths = new Path();

	private ArrayList<ArrayList<Point>> unflushedPoints = new ArrayList<ArrayList<Point>>();
	private ArrayList<ArrayList<Point>> flushedPoints = new ArrayList<ArrayList<Point>>();

	private boolean closed;
	private ArrayList<Point> curPathPoints;
	private int lastX;
	private int lastY;

	public PathHolder(int minOffset) {
		mDataStream = new PipedOutputStream();
		this.minOffset = minOffset;
		this.closed = true;

	}

	public void moveTo(float x, float y) {
		if (!closed) {
			close();
		}
		curPathPoints = new ArrayList<Point>();
		closed = false;

		lastX = Math.round(x);
		lastY = Math.round(y);
		curPathPoints.add(new Point(lastX, lastY));
		unflushedPaths.moveTo(x, y);
	}

	public void lineTo(float x, float y) {
		if (closed) {
			moveTo(x, y);
			return;
		} else {
			int xTemp = Math.round(x);
			int yTemp = Math.round(y);
			if (Math.abs(lastX - xTemp) > minOffset
					|| Math.abs(lastY - yTemp) > minOffset) {
				lastX = xTemp;
				lastY = yTemp;
				curPathPoints.add(new Point(lastX, lastY));
				unflushedPaths.lineTo(x, y);
			}
		}
	}

	public void close() {
		if(curPathPoints.size() > 1){
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

	public PipedOutputStream getDataStream() {
		return mDataStream;
	}

	public boolean flushData() {
		/*
		 * Format Documentation:
		 * - Number of lines (4 Byte)
		 * -- Number of points in the line (4 Byte)
		 * --- Coordinates first point (4 Byte xPos, 4 Byte yPos)
		 * --- Coordinates next points
		 * -- Number of points in the next line
		 * --- Coordinates first point
		 * 
		 * 
		 * Repeat until finished.
		 */
		
		try {
			mDataStream.write(intToBytes(unflushedPoints.size()));
			for (ArrayList<Point> line : unflushedPoints) {
				byte[] buffer = new byte[line.size()];
			
				mDataStream.write(intToBytes(buffer.length));				
				for (Point p : line) {
					byte[] b = intToBytes(p.x);
					byte[] c = intToBytes(p.y);
					mDataStream.write(b);
					mDataStream.write(c);
				}
				mDataStream.flush();
			}
			
		} catch (IOException e) {
			Log.e(TAG, "Failed writing to pipedStream!");
			return false;
		}
		return true;

	}

	private static byte[] intToBytes(int i) {
		return ByteBuffer.allocate(4).putInt(i).array();
	}
}
