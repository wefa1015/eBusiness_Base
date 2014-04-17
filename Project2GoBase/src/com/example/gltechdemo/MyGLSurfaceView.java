/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.gltechdemo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {
	String TAG = this.getClass().toString();

    private final MyGLRenderer mRenderer;
    private MotionEvent lastMotionEvent;
    private boolean shapeClicked = false;
    private boolean touched = false;
    
    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    public boolean onTouchEvent(MotionEvent event) {
//    	Log.d(TAG, "Generic Touch Event");
    	lastMotionEvent = event;
    	
    	
    	if (touched == false)
    		onTouchEventDown(event);
    	else
    		onTouchEventMove(event);
    	
    	touched = true;
    	
        return super.onTouchEvent(event);
    };	
    
    /**
     * Triggered when no more touchEvents are registered
     * 
     * @param event - Motion Event 
     */
    public void onTouchEventRelease() {
    	Log.d(TAG, "Touch Event >>> Release");
    	touched = false;
    	shapeClicked = false;
    }
    
    public void onTouchEventDown(MotionEvent event) {
    	Log.d(TAG, "Touch Event >>> Down");
    	float[] fingerPos = viewCoordsToVector(event.getX(), event.getY());
    	if (mRenderer.getMeshes().get(0).isPointInShape(fingerPos)) {
    		shapeClicked = true;
    	}
    }
    
    public void onTouchEventMove(MotionEvent event) {
    	Log.d(TAG, "Touch Event >>> Move");
    	
    	if (shapeClicked) {
	    	float[] fingerPos = viewCoordsToVector(event.getX(), event.getY());
	    	mRenderer.getMeshes().get(0).setPosition(fingerPos);
	    	requestRender();
    	}
    }
    
//        float x = e.getX();
//        float y = e.getY();
//        float[] fingerPos = viewCoordsToVector(x, y); 
//
//        
//        	
//        
//        return super.onTouchEvent(e);
        
//        switch (e.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                float dx = x - mPreviousX;
//                float dy = y - mPreviousY;

//                // reverse direction of rotation above the mid-line
//                if (y > getHeight() / 2) {
//                    dx = dx * -1 ;
//                }
//
//                // reverse direction of rotation to left of the mid-line
//                if (x < getWidth() / 2) {
//                    dy = dy * -1 ;
//                }

//                mRenderer.getMeshes().get(0).setAngleFromPivot(
//                		mRenderer.getMeshes().get(0).getAngleFromPivot() +
//                        ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
//                
//                mRenderer.getMeshes().get(0).setAngle(
//                		mRenderer.getMeshes().get(0).getAngle() +
//                        ((dx + dy) * TOUCH_SCALE_FACTOR));
                
//                requestRender();            	
//        }
        
//        mPreviousX = x;
//        mPreviousY = y;
//        return true;
//    }
    
    
    /**
     * maps view coordinates to an OPENGL vector
     * 
     * @param x - coordinate of x-axis
     * @param y - coordinate of y-axis
     * @return - a vector as float array
     */
    public float[] viewCoordsToVector(float x, float y) {
    	float[] mTransformationMatrix = new float[16];
    	
    	float newX = x - getWidth() / 2;
        newX = newX / (getWidth() / 2);
        
        float newY = y - getHeight() / 2;
        newY = newY / (getHeight() / (-2));
    	
        float[] vector = new float[]{newX, newY, 0.0f, 1.0f};
        
        Matrix.invertM(mTransformationMatrix, 0, mRenderer.getmMVPMatrix(), 0);
        
        Matrix.multiplyMV(vector, 0, mTransformationMatrix, 0, vector.clone(), 0);
        
    	return new float[] {vector[0], vector[1], vector[2]};
    }
}
