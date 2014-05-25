package ru.brostudios.framework.gui;

import java.util.List;

import ru.brostudios.framework.Application;
import ru.brostudios.framework.interfaces.InputInterface.TouchEvent;

public abstract class UserInterface {
	
	protected Application application;
	protected boolean active;
	protected double x, y;	// float теряет точность
	
	public UserInterface(Application app) {
		this.application = app;
		active = true;
		this.x = 0; this.y = 0;
	}
	public void activate() { active = true; }
	public void deactivate() { active = false; }
	public final boolean isActive() { return active; }

	public abstract void present();
	public abstract void update(List<TouchEvent> touchEvents);
}