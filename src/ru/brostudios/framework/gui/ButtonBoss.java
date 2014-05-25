package ru.brostudios.framework.gui;

import java.util.ArrayList;
import java.util.List;
import ru.brostudios.framework.Application;
import ru.brostudios.framework.game.Texture;
import ru.brostudios.framework.interfaces.InputInterface.TouchEvent;

public class ButtonBoss extends Button implements Runnable {
	
	private enum ButtonActivity { BUTTON_HIDE, BUTTON_RUN_ON, BUTTON_SHOW, BUTTON_RUN_OFF };
	private ButtonActivity buttonActivity;
	
	private List<Button> list;
	private Thread thread;	// поток раздвижения кнопок
	private volatile boolean run = false;
	private float speed, irad = 0, radius, min, max;	// раздвижение кнопок на radius от min-угла до max-угла
// ******************************************************
	
	public ButtonBoss(Application app, Texture texture, float scale, 
						float speed, float radius, float min, float max) {
		super(app, texture, scale);
		this.radius = radius;
		this.min = min; this.max = max;
		this.speed = speed;
		
		list = new ArrayList<Button>();
		buttonActivity = ButtonActivity.BUTTON_HIDE;
	}

	public void addButton(Button button) {
		button.MoveInScreenCoords(x, y);
		list.add(button);
	}
	public final Button getButton(int i) {
		return list.get(i);
	}
	
	public void removeButton(int i) {
		if(i>=0 && i<list.size()) list.remove(i);
	}
	
	public void removeButton(Button button) {
		if(button!=null) list.remove(button);
	}
	
	@Override
	public void present() {
		if(buttonActivity!=ButtonActivity.BUTTON_HIDE) {
			for(int i=0;i<list.size();i++) list.get(i).present();
		}
		super.present();
	}
	
	public void updateButtons(float rad) {
		float fi = (max-min);
		if(list.size()>2) fi /= list.size();
		float angle = min;
		for(int i=0;i<list.size();i++) {
			list.get(i).MoveInScreenCoords(x+rad*Math.cos(angle*Math.PI/180f), y+rad*Math.sin(angle*Math.PI/180f));
			angle+=fi;
		}
	}
	
	@Override
	public void MoveInScreenCoords(double x, double y) {
		super.MoveInScreenCoords(x, y);
		for(int i=0;i<list.size();i++) {
			updateButtons(irad);
		}
	}
	
	@Override
	public void update(List<TouchEvent> touchEvents) {
		super.update(touchEvents);
		if(buttonActivity != ButtonActivity.BUTTON_HIDE) {
			for(int i=0;i<list.size();i++) { list.get(i).update(touchEvents); }			
		}
		
		if(isTouchReleased()) {
			if(thread!=null) { run = false; while(thread.isAlive()) {} }
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {
		run = true;
		if(buttonActivity == ButtonActivity.BUTTON_HIDE || buttonActivity == ButtonActivity.BUTTON_RUN_OFF) {
			buttonActivity = ButtonActivity.BUTTON_RUN_ON;
			while(irad<radius && run) { irad+=speed; updateButtons(irad); }
			if(irad>=radius) { irad = radius; buttonActivity = ButtonActivity.BUTTON_SHOW; }
		} else {
			buttonActivity = ButtonActivity.BUTTON_RUN_OFF;
			while(irad>0 && run) { irad-=speed; updateButtons(irad); }
			if(irad<0) { irad = 0; buttonActivity = ButtonActivity.BUTTON_HIDE; }
		}
		run = false;
	}
	
}