package ru.brostudios.framework;

public class Coords {
	
	public static enum COORDS { SCREEN_COORDS, SCREEN_PERCENT };
	private double x, y;
	
	public Coords(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
}