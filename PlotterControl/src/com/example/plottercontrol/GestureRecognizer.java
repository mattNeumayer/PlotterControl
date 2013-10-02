package com.example.plottercontrol;

import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;

public class GestureRecognizer implements OnTouchListener, SurfaceHolder.Callback {
	interface GestureListener {
		void onGesture(int gesture);
		boolean onNonGestureTouchEvent(View v, MotionEvent event);
	}
	
	private static final String TAG = GestureRecognizer.class.getSimpleName();
	
	final int MIN_Y_SLIDE_OFFSET = 20;
	
	private Rect slideArea;
	private PointF slideStart;
	
	private int surfaceHeight = 1;
	private int surfaceWidth = 1;
	static final int SWIPE_FROM_TOP = 0;
	
	private GestureListener mListener;
	
	public GestureRecognizer(SurfaceHolder holder, View view, GestureListener listener) {
		Log.v(TAG, "GestureRecognizer()");
		slideArea = new Rect();
		holder.addCallback(this);
//		view.setOnTouchListener(this); shit funzt ned aus unerklaerlichem grund
		mListener = listener;
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		//Log.v(TAG, "onTouch()");
		
		boolean handled = false;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (slideArea.contains((int) event.getRawX(), (int) event.getRawY())) {
				slideStart = new PointF(event.getRawX(), event.getRawY());
				handled = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (slideStart != null) {
				if ((event.getRawY() - slideStart.y) >= MIN_Y_SLIDE_OFFSET) {
					mListener.onGesture(SWIPE_FROM_TOP);
					handled = true;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			slideStart = null;
			break;
		}
		
		if (!handled)
			handled = mListener.onNonGestureTouchEvent(v, event);
		
		return handled;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		surfaceWidth = width;
		surfaceHeight = height;
		
		Log.v(TAG, "surfaceChanged()");
		
		slideArea.set(0, 0, surfaceWidth, 100);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}
}
