package com.example.plottercontrol;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.plottercontrol.EnterNameDialogFragment.EnterNameDialogListener;

public class MainActivity extends Activity implements EnterNameDialogListener {
	private static final String TAG = MainActivity.class.getSimpleName();

	ListView myListView;
	ArrayAdapter<String> adapter;

	ImageButton btnNew;

	String[] myStringArray = { "example1.pc", "example2.pc", "example3.pc",
			"example4.pc", "example5.pc", "example6.pc", "example7.pc",
			"example8.pc", "example9.pc", "example10.pc", "example11.pc",
			"example12.pc", "endlichisdielistevoll.boner" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);

		myListView = (ListView) findViewById(R.id.listView1);
		myListView.setAdapter(adapter);

		btnNew = (ImageButton) findViewById(R.id.imageButton1);
		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new EnterNameDialogFragment();
				newFragment.show(getFragmentManager(), "name");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, String enteredText) {
		Log.v(TAG, "positive click: '" + enteredText + "'");

		Intent intent = new Intent(this, EditActivity.class);
		startActivity(intent);
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		Log.v(TAG, "negative click");
	}

}
