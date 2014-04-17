package com.example.gltechdemo;

public class Square extends BasicShape {
	public Square(float x, float y, float z, float sc, float[] col) {
		super(x, y, z, sc, col);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void defineShape() {
		this.vertexCoords = new float[] {
	            -0.5f,  0.5f, 0.0f,   // top left
	            -0.5f, -0.5f, 0.0f,   // bottom left
	             0.5f, -0.5f, 0.0f,   // bottom right
	             0.5f,  0.5f, 0.0f }; // top right
		
		drawOrder = new short[]{ 0, 1, 2, 0, 2, 3 };
		
	}

}
