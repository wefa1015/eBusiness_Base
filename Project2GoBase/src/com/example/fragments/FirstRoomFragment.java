package com.example.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gltechdemo.MyGLSurfaceView;
import com.example.project2Go.FirstRoomActivity;
import com.example.project2Go.MainActivity;
import com.example.project2Go.R;

@SuppressLint("ValidFragment") public class FirstRoomFragment extends Fragment {
	
	public static final String ARG_SECTION_TEXT = "section_text";
	private MainActivity mainActivity;
	private MyGLSurfaceView mGLView;
	
	@SuppressLint("ValidFragment") public FirstRoomFragment(MainActivity ma) {
		mainActivity = ma;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
//		final Intent intent = new Intent(mainActivity, FirstRoomActivity.class);
//        final Button button = (Button) mainActivity.findViewById(R.id.button1);
//        button.setOnClickListener( new View.OnClickListener() {
//            public void onClick(View v) {
//                startActivity(intent);
//            }
//        });
		
        // TODO: Add Tobis MyGLSurfaceView
		mGLView = new MyGLSurfaceView(inflater.getContext());
		View rootView = inflater.inflate(com.example.project2Go.R.layout.fragment_first_room, container, false);
		//TextView firstFragmentTextView = (TextView) rootView.findViewById(R.id.section_label);
		//firstFragmentTextView.append(getArguments().getString(ARG_SECTION_TEXT));
		
		return mGLView;
	}
}
