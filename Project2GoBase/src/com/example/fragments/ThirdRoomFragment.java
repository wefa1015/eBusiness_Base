package com.example.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ThirdRoomFragment extends Fragment {
	
	public static final String ARG_SECTION_TEXT = "section_text";

	public ThirdRoomFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(com.example.project2Go.R.layout.fragment_third_room, container, false);
		//TextView firstFragmentTextView = (TextView) rootView.findViewById(R.id.section_label);
		//firstFragmentTextView.append(getArguments().getString(ARG_SECTION_TEXT));
		return rootView;
	}
}
