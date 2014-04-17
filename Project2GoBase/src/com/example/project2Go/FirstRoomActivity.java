package com.example.project2Go;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.gltechdemo.MyGLSurfaceView;

public class FirstRoomActivity extends Activity {
	
	private MyGLSurfaceView mGLView;
	
	private OnClickListener mTouchListener = new OnClickListener() {
	    public void onClick(View v) {
	      mGLView.onTouchEventRelease();
	    }
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
        mGLView.setOnClickListener(mTouchListener);
        //mGLView.set
        setContentView(mGLView);
//        setContentView(layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.e("FYI", "TOUCH EVENT");
		return super.onTouchEvent(event);
	}
	
	public View getTheDamnView(){
		return this.findViewById(android.R.id.content);
	}
	
	
}

