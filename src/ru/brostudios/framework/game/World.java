package ru.brostudios.framework.game;

public class World {

	protected int width, height, depth;
	
// ***************************************************************
	
	public World(int width, int height, int depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	public final int getWidth() { return width; }
	public final int getHeight() { return height; }
	public final int getDepth() { return depth; }
}