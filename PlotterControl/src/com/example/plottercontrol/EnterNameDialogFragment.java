package com.example.plottercontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class EnterNameDialogFragment extends DialogFragment {
	private static final String TAG = EnterNameDialogFragment.class
			.getSimpleName();

	public interface EnterNameDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog,
				String enteredText);

		public void onDialogNegativeClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	EnterNameDialogListener mListener;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View view = inflater.inflate(R.layout.enter_name_dialog_layout, null);

		// Tastatur automatisch einblenden
		EditText myEditText = (EditText) view
				.findViewById(R.id.editText_filename);
		myEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getDialog()
							.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		// 'OK'-Button aktivieren / deaktivieren
		myEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				((AlertDialog) getDialog()).getButton(
						AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});

		builder.setView(view);
		builder.setPositiveButton(R.string.enter_name_dialog_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener.onDialogPositiveClick(
								EnterNameDialogFragment.this,
								((EditText) ((AlertDialog) dialog)
										.findViewById(R.id.editText_filename))
										.getText().toString());
					}
				}).setNegativeButton(R.string.enter_name_dialog_abort,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mListener
								.onDialogNegativeClick(EnterNameDialogFragment.this);
					}
				});

		// Create the AlertDialog object and return it //und 'OK'-Button
		// standardmaessig deaktivieren
		Dialog dialog = builder.create();
		dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE)
						.setEnabled(false);
			}
		});
		return dialog;
	}

	// Override the Fragment.onAttach() method to instantiate the
	// NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (EnterNameDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}
}
