package com.example.plottercontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.plottercontrol.GestureRecognizer.GestureListener;

public class PDrawingView extends SurfaceView implements GestureListener {

	// _______________________________________________________________________________________________________________________________________________
	private class PDrawingViewThread extends Thread {
		final String TAG = PDrawingViewThread.class.getSimpleName();

		SurfaceHolder mSurfaceHolder;

		boolean mRun = false;
		boolean hasFocus = false;

		PathHolder curPath;
		Paint linePaint;

		public PDrawingViewThread(SurfaceHolder holder, PathHolder curPath) {
			mSurfaceHolder = holder;
			this.curPath = curPath;
			setName("DrawingThread");

			linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			linePaint.setColor(Color.rgb(0x00, 0x99, 0xCC));
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setStrokeWidth(5);

		}

		@Override
		public void run() {
			while (!isInterrupted())
				while (mRun && hasFocus) {
					Canvas c = null;
					try {
						c = mSurfaceHolder.lockCanvas(null);
						synchronized (mSurfaceHolder) {
							doDraw(c);
						}
					} finally {
						// do this in a finally so that if an exception is
						// thrown
						// during the above, we don't leave the Surface in an
						// inconsistent state
						if (c != null) {
							mSurfaceHolder.unlockCanvasAndPost(c);
						}

					}
				}
		}

		private void doDraw(Canvas c) {
			if (c == null) {
				Log.w(TAG, "canvas is null");
				return;
			}

			c.drawColor(Color.WHITE);

			if (curPath == null)
				Log.wtf(TAG, "curPath is null!?!?");
			else
				c.drawPath(curPath.getUnflushedPaths(), linePaint);
		}
	}

	// _______________________________________________________________________________________________________________________________________________

	private static final String TAG = PDrawingView.class.getSimpleName();

	private EditActivity mEditActivity;

	private GestureRecognizer gestureRecognizer;
	private PDrawingViewThread thread;
	private PathHolder mPath;

	boolean drawing = false;

	public PDrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void init(BluetoothController bluetoothController, EditActivity editActivity){
		mEditActivity = editActivity;
		
		gestureRecognizer = new GestureRecognizer(this);
		mPath = new PathHolder(20, bluetoothController); //TODO min Offset
		
		getHolder().addCallback(gestureRecognizer);
		getHolder().addCallback(mPath);

		thread = new PDrawingViewThread(getHolder(), mPath);
		thread.start();
	}


	@Override
	public boolean onNonGestureTouchEvent(MotionEvent event) {
		boolean handled = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPath.moveTo(event.getRawX(), event.getRawY());
			handled = true;
			break;
		case MotionEvent.ACTION_MOVE:
			mPath.lineTo(event.getRawX(), event.getRawY());
			handled = true;
			break;
		case MotionEvent.ACTION_UP:
			mPath.close();
			handled = true;
			break;
		}
		return handled;
	}

	public void startDrawing() {
		thread.mRun = true;
	}

	public void pauseDrawing() {
		thread.mRun = false;
	}

	public void stopDrawing() {
		thread.interrupt();
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		Log.v(TAG, "onWindowFocusChanged(" + hasWindowFocus + ")");
		if (!hasWindowFocus) {
			thread.hasFocus = false;
		} else {
			thread.hasFocus = true;
		}
	}

	@Override
	public void onGesture(int gesture) {
		mEditActivity.getQuSystemUIHider().show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureRecognizer.recognizeGesture(event);
	}

	public void flushData() {
		mPath.flushData();
	}
}
