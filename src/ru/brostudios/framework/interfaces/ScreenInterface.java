package ru.brostudios.framework.interfaces;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import ru.brostudios.framework.AndroidGame;
import ru.brostudios.framework.interfaces.InputInterface.TouchEvent;

public abstract class ScreenInterface {
	
	protected AndroidGame game;
	
	public ScreenInterface(AndroidGame game) {
		this.game = game;
	}
	
	public abstract void present(GL10 gl);
	public abstract void update(List<TouchEvent> touchEvents);
	public abstract void resume();
	public abstract void pause();
	public abstract void destroy();
}