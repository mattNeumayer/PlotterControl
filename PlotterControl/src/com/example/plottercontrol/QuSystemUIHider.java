package com.example.plottercontrol;

import android.app.Activity;
import android.view.View;

public class QuSystemUIHider {
	private int mShowFlags = View.SYSTEM_UI_FLAG_VISIBLE;
	private int mHideFlags = View.SYSTEM_UI_FLAG_FULLSCREEN;

	// Cached values.
	private int mControlsHeight;
	private int mShortAnimTime;

	private boolean uiVisible;

	private View controlsView;
	private View contentView;

	private Activity context;

	public QuSystemUIHider(Activity context, View contentView, View controlsView) {
		this.context = context;
		this.contentView = contentView;
		this.controlsView = controlsView;
		show();
	}

	public void toggleUI() {
		if (uiVisible)
			hide();
		else
			show();
	}

	public void hide() {
		if (uiVisible) {
			contentView.setSystemUiVisibility(mHideFlags);
			context.getActionBar().hide();
			animateControlsView(false);
		}
		uiVisible = false;
	}

	public void show() {
		if (!uiVisible) {
			contentView.setSystemUiVisibility(mShowFlags);
			context.getActionBar().show();
			animateControlsView(true);
		}
		uiVisible = true;
	}

	private void animateControlsView(boolean show) {
		// If the ViewPropertyAnimator API is available
		// (Honeycomb MR2 and later), use it to animate the
		// in-layout UI controls at the bottom of the
		// screen.
		if (mControlsHeight == 0) {
			mControlsHeight = controlsView.getHeight();
		}
		if (mShortAnimTime == 0) {
			mShortAnimTime = context.getResources().getInteger(
					android.R.integer.config_shortAnimTime);
		}
		controlsView.animate().translationY(show ? 0 : mControlsHeight)
				.setDuration(mShortAnimTime);
	}
}
