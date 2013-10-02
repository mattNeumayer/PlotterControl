package com.example.plottercontrol;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;


public class EditActivity extends Activity {
	Button printBtn;
	
	QuSystemUIHider mQuSystemUIHider;
	
	PDrawingView mPDrawingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		printBtn = (Button) findViewById(R.id.print_button);
		
		mPDrawingView = (PDrawingView) findViewById(R.id.PDrawingView);
		mPDrawingView.setEditActivityRef(this);
		
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.PDrawingView);
		mQuSystemUIHider = new QuSystemUIHider(this, contentView, controlsView);
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.test, menu);
	    return true;
	}
	
	public QuSystemUIHider getQuSystemUIHider() {
		return mQuSystemUIHider;
	}
	
//	@Override
//	protected void onPause() {
//		super.onPause();
//		mPDrawingView.pauseThread();
//	}
//	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		mPDrawingView.resumeThread();
//	}
}
