package com.ibook;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProgressFragment extends DialogFragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		      Bundle savedInstanceState) {
		    getDialog().setTitle("Выполнение...");
		    View v = inflater.inflate(R.layout.progress_fragment, null);
		    return v;
		  }
}
