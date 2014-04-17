package com.example.gltechdemo;

public abstract class Shape {
	//TODO Implement this class
	
	public abstract void draw(float[] mvpMatrix);
	public abstract void moveToPoint(float[] newPos);
	public abstract void moveWithDirection(float[] direction);
	
	public abstract float[] getPosition();	
	public abstract float[] getScale();
	public abstract float[] getScaleFromPivot();
	public abstract float 	getAngle();
	public abstract float 	getAngleFromPivot();
	public abstract float[] getPivot();
	public abstract float[] getColor();
	
	public abstract void setPosition(float[] position);
	public abstract void setScale(float[] scale);
	public abstract void setScaleFromPivot(float[] scaleFromPivot);
	public abstract void setAngle(float angle);
	public abstract void setAngleFromPivot(float angleFromPivot);
	public abstract void setPivot(float[] pivot);
	public abstract void setColor(float[] color);
}
