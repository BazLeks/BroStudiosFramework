package ru.brostudios.framework.interfaces;

public interface FrameworkInterface {
	
	public void setScreen(ScreenInterface screen);
	public ScreenInterface getStartScreen();
	public ScreenInterface getCurrentScreen();
	
}