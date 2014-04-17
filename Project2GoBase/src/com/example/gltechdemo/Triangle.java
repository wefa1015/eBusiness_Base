package com.example.gltechdemo;

public class Triangle extends BasicShape {
	public Triangle(float x, float y, float z, float sc, float[] col) {
		super(x, y, z, sc, col);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void defineShape() {
		float height = 0.3f;
    	float xOffset = (float) Math.sqrt(height * height - (height/2) * (height/2));
		this.vertexCoords = new float[] {
				 0.0f,     height,   0.0f,   			// top
				-xOffset, -height/2, 0.0f,   // bottom left
				 xOffset, -height/2, 0.0f};   // bottom right
		
		drawOrder = new short[]{0, 1, 2};
	}

}
