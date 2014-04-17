package com.example.gltechdemo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.example.gltechdemo.shader.BasicGraphics;

public abstract class BasicShape {
	protected final String TAG = this.getClass().toString();

	protected final FloatBuffer vertexBuffer;
    protected final int mProgram;
    protected int mPositionHandle;
    protected int mColorHandle;
    protected int mMVPMatrixHandle;
    
    protected final float[] mModelMatrix = new float[16];
    protected final float[] mRotationMatrix = new float[16];
    
    
    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;
    
    protected float vertexCoords[]; // = new float[] {
//            -0.5f,  0.5f, 0.0f,   // top left
//            -0.5f, -0.5f, 0.0f,   // bottom left
//             0.5f, -0.5f, 0.0f,   // bottom right
//             0.5f,  0.5f, 0.0f }; // top right
    protected short[] drawOrder;// = new short[]{ 0, 1, 2, 0, 2, 3 };
    
    protected final int vertexCount = 3;							//A Triangle has 3 points...obviously
    protected final int vertexStride = COORDS_PER_VERTEX * 4; 	//4 bytes per vertex
    
    protected float position[] = {0.0f, 0.0f, 0.0f};				//{0, 0, 0} is the default position
    protected float pivot[] = position.clone();
    protected boolean positionIsPivot = true;
    protected float scale[];
    protected float scaleFromPivot[];
    protected float angle;
    protected float angleFromPivot;
    
    protected float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    protected ShortBuffer drawListBuffer;

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public BasicShape(float x, float y, float z, float sc, float[] col) {
    	//	Setup Attributes
    	this.scale = new float[]{sc, sc, sc};
    	this.scaleFromPivot = new float[]{0, 0, 0};
    	angle = 0.0f;
    	this.angleFromPivot = 0.0f;
    	position = new float[]{x, y, z};
    	
    	defineShape();

    	if (color.length == 4) 
    		color = col.clone();
    	else if (color.length == 3)
    		color = new float[] {col[0], col[1], col[2], 1.0f};
    	else
    		Log.e(TAG , "Tried to instantiate Object but failed due to an invalid Parameter!\n @Param: float[] col");
    	
    	
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                vertexCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        
        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(vertexCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
     // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = BasicGraphics.loadShader(
                GLES20.GL_VERTEX_SHADER, BasicGraphics.vertexShaderCode);
        int fragmentShader = BasicGraphics.loadShader(
                GLES20.GL_FRAGMENT_SHADER, BasicGraphics.fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }
    
    /**
     * This function must set the basic Coordinates of the shape
     * and its drawing order...
     * 
     * - define coords in "float[] vertexCoords"
     * - define the drawing order in  "short[] drawOrder"
     */
    public abstract void defineShape();
    
    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
	public void draw(float[] mMVPMatrix) {    	
    	//	Prepare matrices
    	Matrix.setIdentityM(mModelMatrix, 0);	//Initialize Identity Matrix
    	
    	//	Add Translation
    	float x = this.getPosition()[0];
    	float y = this.getPosition()[1];
    	float z = this.getPosition()[2];
    	Matrix.translateM(mModelMatrix, 0, x, y, z);
        
        //	Add Rotation (rotation is first transformation here)
        Matrix.setRotateM(mRotationMatrix, 0, this.getAngle(), 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, mRotationMatrix, 0);    	
    	
        //	Add Rotation around its pivot (rotation is second transformation here)
        Matrix.setRotateM(mRotationMatrix, 0, this.getAngleFromPivot(), 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(mModelMatrix, 0, mRotationMatrix.clone(), 0, mModelMatrix.clone(), 0);   
        
        //	Add Scale
        Matrix.scaleM(mModelMatrix, 0, scale[0], scale[1], scale[2]);
        
        // Combine the world Matrix with project and view matrix	        
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix.clone(), 0, mModelMatrix, 0);
        
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
    
    public void translateFromPivot(float[] mModelMatrix) {
    	float[] vTargetPos = new float[16];

    	for (int coord = 0; coord < 3; ++coord) {
    		vTargetPos[coord] = position[coord]; 
    		vTargetPos[coord] *= this.scaleFromPivot[coord];
    		position[coord] = pivot[coord] + vTargetPos[coord];
    	}

    }
    
    /**
     * Maps the point accordingly to the model matrix
     * by multiplying the point vector with the inverse of the Shapes Modelmatrix
     * 
     * @param point - the point vector to map accordingly to the shapes transformations
     * @return float[] - the mapped point vector
     */
    private float[] mapPoint(float[] point) {
    	float[] result = new float[] {point[0], point[1], 0.0f, 1.0f};
    	float[] mTransformationMatrix = new float[16];
    	Matrix.invertM(mTransformationMatrix, 0, this.mModelMatrix.clone(), 0);
    	
    	Matrix.multiplyMV(result, 0, mTransformationMatrix, 0, result.clone(), 0);
    	
    	return result;
    }
    
    /**
     * Checks if a given point is inside the shape
     * - the check is performed for 2 dimensions only 
     *
     * @param point
     * @return boolean
     */
    public boolean isPointInShape(float[] point) {
    	int intsAbove = 0;
    	
    	float[] vector = mapPoint(point);
    	
    	float[] s;
    	float[] t;
    	
    	for (int it = 0; it < this.vertexCoords.length; it += COORDS_PER_VERTEX) {
    		//s and t define a wall of the shape
    		s = new float[] {	vertexCoords[it],
								vertexCoords[it + 1],
								vertexCoords[it + 2]	};
    		
    		if (it == this.vertexCoords.length - COORDS_PER_VERTEX) {
    			t = new float[] {	vertexCoords[0],
									vertexCoords[1],
									vertexCoords[2]	};
    		} else {
          	  	t = new float[] {	vertexCoords[it + COORDS_PER_VERTEX],
          	  						vertexCoords[it + COORDS_PER_VERTEX + 1],
          	  						vertexCoords[it + COORDS_PER_VERTEX + 2]	};                
            }
    		
    		if (t[0] != s[0]) {
	    		float[] intersection = new float[] {vector[0], s[1] + (t[1] - s[1]) * (vector[0] - s[0]) / (t[0] - s[0])};
	            if ((intersection[1] > vector[1]) 
	            			&& isBetween(s, intersection, t) 
	            				&& (!VectorEquals(s[0] < t[0] ? s: t, intersection)))
	            {
	                intsAbove++;
	            }
    		}
    	}
        
        return (intsAbove % 2 == 1);
    }
    
    /**
     * Helper function
     * 
     * @param a
     * @param b
     * @param c
     * @return
     */
    private static boolean isBetween(float[] a, float[] b, float[] c)	{
    	float u = Matrix.length(a[0] - c[0], a[1] - c[1], 0);
    	float v = Matrix.length(a[0] - b[0], a[1] - b[1], 0)
    				+ Matrix.length(b[0] - c[0], b[1] - c[1], 0);
        return floatEquals(u, v);
    }
    
    /**
     * Helper function
     * 
     * @param a
     * @param b
     * @return
     */
	private static boolean floatEquals(float a, float b)	{
		float epsilon = (float) Math.exp(-10);
        return Math.abs(a - b) < epsilon;
    }
	
	/**
	 * Helper function
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static boolean VectorEquals(float[] a, float[] b)
    {
        return floatEquals(a[0], b[0]) && floatEquals(a[1], b[1]);
    }
	
    /**
     * Moves the shape to the given destination
     * 
     * @param newPos
     */
	public void moveToPoint(float[] newPos) {
		if (newPos.length != position.length) {
			Log.d(TAG, "Unable to execute method due to an invalid parameter" + 
					"\nMethod: moveToPoint(float[] newPos)");
			return;
		}
		for (int it = 0; it < position.length; ++it) {
			this.position[it] = newPos[it];
		}
	}

	public void moveWithDirection(float[] direction) {
		if (direction.length != position.length) {
			Log.d(TAG, "Unable to execute method due to an invalid parameter" + 
					"\nMethod: moveToPoint(float[] newPos)");
			return;
		}
		for (int it = 0; it < position.length; ++it) {
			this.position[it] += direction[it];
		}
	}
	
    
	/**
	 * @return a copy of the position coords
	 */
	public float[] getPosition() {
		return position.clone();
	}
	
	/**
	 * @return the scale
	 */
	public float[] getScale() {
		return scale.clone();
	}
	
	/**
	 * @return the angle
	 */
	public float getAngle() {
		return angle;
	}

	/**
	 * @return the color
	 */
	public float[] getColor() {
		return color;
	}

	/**
	 * @return the pivot
	 */
	public float[] getPivot() {
		return pivot;
	}

	/**
	 * @return the scaleFromPivot
	 */
	public float[] getScaleFromPivot() {
		return scaleFromPivot;
	}

	/**
	 * @return the angleFromPivot
	 */
	public float getAngleFromPivot() {
		return angleFromPivot;
	}

	/**
	 * Setting the pivot manually will cause the shapes pivot to 
	 * not stick to the position anymore!
	 * 
	 * @param pivot - the pivot Vector
	 */
	public void setPivot(float[] pivot) {
		this.pivot = pivot;
		positionIsPivot = false;
	}

	/**
	 * Copies the array paste as parameter and assigns it to the position attribute
	 * 
	 * @param position - the position to set
	 */
	public void setPosition(float[] position) {
		this.position = position.clone();
		if (positionIsPivot) {
			for (int it = 0; it < this.pivot.length; ++it) {
				pivot[it] = position[it];
			}
		}
	}
	
	/**
	 * @param scale the scale to set
	 */
	public void setScale(float[] scale) {
		this.scale = scale;
	}

	/**
	 * @param scaleFromPivot the scaleFromPivot to set
	 */
	public void setScaleFromPivot(float[] scaleFromPivot) {
		this.scaleFromPivot = scaleFromPivot;
	}

	/**
	 * @param angleFromPivot the angleFromPivot to set
	 */
	public void setAngleFromPivot(float angleFromPivot) {
		this.angleFromPivot = angleFromPivot;
	}

	/**
	 * @param angle the angle to set
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(float[] color) {
		this.color = color;
	}
}
