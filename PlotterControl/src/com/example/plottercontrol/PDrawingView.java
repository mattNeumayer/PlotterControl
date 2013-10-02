package com.example.plottercontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.plottercontrol.GestureRecognizer.GestureListener;

public class PDrawingView extends SurfaceView implements GestureListener {
	class PDrawingViewThread extends Thread {
		private final String TAG = PDrawingViewThread.class.getSimpleName();
		
		SurfaceHolder mSurfaceHolder;
		
		boolean mRun;
		
		Path curPath;
		Paint linePaint;
		
		public PDrawingViewThread(SurfaceHolder holder, Path curPath) {
			mSurfaceHolder = holder;
			mRun = true;
			this.curPath = curPath;
			
			linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			linePaint.setColor(Color.rgb(0x00, 0x99, 0xCC));
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setStrokeWidth(5);

		}
		
		@Override
		public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                    	doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
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
				Log.v(TAG, "canvas is null");
				return;
			}
			
			c.drawColor(Color.WHITE);
			
			if (curPath == null)
				Log.v(TAG, "curPath is null!?!?");
			else
				c.drawPath(curPath, linePaint);
		}
		
		public void setMRun(boolean b) {
			mRun = b;
		}

		public void pause() {
			mRun = false;
		}
	}
	
	//_______________________________________________________________________________________________________________________________________________
	
	private static final String TAG = PDrawingView.class.getSimpleName();
	
	private PDrawingViewThread thread;
	private EditActivity mEditActivity;
	private GestureRecognizer gestureRecognizer;
	
	private Path curPath;
	boolean drawing = false;
	
	public PDrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Log.v(TAG, "PDrawingView()");
		gestureRecognizer = new GestureRecognizer(getHolder(), this, this);
		
		curPath = new Path();
		thread = new PDrawingViewThread(getHolder(), curPath);
		thread.start();
		
	}

	@Override
	public boolean onNonGestureTouchEvent(View v, MotionEvent event) {
		boolean handled = false;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			curPath.moveTo(event.getRawX(), event.getRawY());
			drawing = true;
			handled = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if(drawing)
				curPath.lineTo(event.getRawX(), event.getRawY());
			handled = true;
			break;
		case MotionEvent.ACTION_UP:
			drawing = false;
			handled = true;
			break;
		}
		return handled;
	}

	public void setEditActivityRef(EditActivity editActivity) {
		mEditActivity = editActivity;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		Log.v(TAG, "onWindowFocusChanged(" + hasWindowFocus + ")");
		
		if (!hasWindowFocus) {
			thread.pause();
		} else {
//			if (!thread.isAlive())
//				thread.start();
		}
	}

	@Override
	public void onGesture(int gesture) {
		mEditActivity.getQuSystemUIHider().show();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureRecognizer.onTouch(this, event);
	}
	
//	public void pauseThread() {
//		thread.setMRun(false);
//		
//		boolean retry = true;
//		
//		while(retry) {
//			try {
//				thread.join();
//				retry = false;
//			} catch (InterruptedException e) {
//				Log.v(TAG, "thread.join() failed");
//			}
//		}
//	}
//	
//	public void resumeThread() {
//		thread.setMRun(true);
//		thread.start();
//	}
}
