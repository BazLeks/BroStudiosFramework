package ru.brostudios.framework.gui;

import java.util.List;

import ru.brostudios.framework.Application;
import ru.brostudios.framework.game.Sprite;
import ru.brostudios.framework.game.Texture;
import ru.brostudios.framework.interfaces.InputInterface.TouchEvent;

public class Button extends UserInterface {
	
	protected static enum STATE { BUTTON_UP, BUTTON_DOWN, BUTTON_RELEASE }
	protected STATE state;
	protected float scale;
	protected Sprite sprite;
	protected float minScale;

// ************************************************************
	
	public Button(Application app, Texture texture, float scale) {
		super(app);
		state = STATE.BUTTON_UP;
		sprite = new Sprite(app, texture, 1, 1);
		this.scale = scale;
		this.minScale = scale/10f;	
		//this.texture = texture;
		//int screenW = game.getGraphics().getWidth();
		//int screenH = game.getGraphics().getHeight();
		//float scaleNew = 1.0f;
//		if(screenW <= screenH) scaleNew = scale*screenW/texture.width;
//		else scaleNew = scale*screenH/texture.height;
//		this.scale = scaleNew;
	}
	
	public void update(List<TouchEvent> touchEvents) {
		
		for(int i=0; i<touchEvents.size(); i++) {
			TouchEvent touchEvent = touchEvents.get(i);
			
			float tx = touchEvent.x - application.getGraphics().getWidth()/2.0f;
			float ty = application.getGraphics().getHeight()/2.0f - touchEvent.y;
			float width = sprite.getFrameWidth()*scale;
			float height = sprite.getFrameHeight()*scale;
			
			if( tx >= x-width/2f && tx <= x+width/2f && ty >= y-height/2 && ty <= y+height/2f) {
				if(touchEvent.type == TouchEvent.TOUCH_DOWN) {
					if(state == STATE.BUTTON_UP) {
						state = STATE.BUTTON_DOWN;
						scale -= minScale;
						return;
					}
				}
				if(touchEvent.type == TouchEvent.TOUCH_UP) {
					if(state == STATE.BUTTON_DOWN) {
						state = STATE.BUTTON_RELEASE;
						scale += minScale;
					}
				}
			} else {
				if(state == STATE.BUTTON_DOWN) {
					state = STATE.BUTTON_UP;
					scale += minScale;
				}
			}
		}
	}
	
	@Override
	public void present() {
		
		sprite.present(application.getGraphics().getGL(), (float)x, (float)y, scale);
		
	}
	
	public void MoveInScreenCoords(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void MoveInScreenPercent(double x, double y) {
		// x=[-1, 1], y=[-1, 1]
		int screenW = application.getGraphics().getWidth();
		int screenH = application.getGraphics().getHeight();
		this.x = x*(screenW/2-sprite.getFrameWidth()*scale/2);
		this.y = y*(screenH/2-sprite.getFrameHeight()*scale/2);
	}
	
	public void setScale(float scale) { this.scale = scale; }
	
	public void setState(STATE state) { this.state = state; }
	
	public void setActive(boolean active) { this.active = active; }
	
	public final boolean isTouchDown() {
		if(state == STATE.BUTTON_DOWN) return true;
		else return false;
	}
	public boolean isTouchReleased() {
		if(state == STATE.BUTTON_RELEASE) {
			state = STATE.BUTTON_UP;
			return true;
		}
		else return false;
	}
	
	public final double getX() { return x; }
	public final double getY() { return y; }
	public final STATE getState() { return state; }
	public final float getScale() { return scale; }	
	public final boolean getActive() { return active; }
	
	

}