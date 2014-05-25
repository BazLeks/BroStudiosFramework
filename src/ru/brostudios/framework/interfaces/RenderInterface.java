package ru.brostudios.framework.interfaces;

import java.nio.FloatBuffer;

import ru.brostudios.framework.game.Texture;

public interface RenderInterface {
	
	public abstract void drawInScreenCoords(FloatBuffer buffer, float x, float y, Texture texId);
	public abstract void drawInWorldCoords(FloatBuffer buffer, float x, float y, float z, float size, Texture texId);
	
}