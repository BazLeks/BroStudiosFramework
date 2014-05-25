package ru.brostudios.framework;

import java.util.ArrayList;
import java.util.List;

import ru.brostudios.framework.Pool.PoolObjectFactory;
import ru.brostudios.framework.interfaces.InputInterface;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Input implements InputInterface {

	public class InputAccelHandler implements SensorEventListener {
		
		final float alpha = 0.8f;
		// private float gravity[];
		private float accelX, accelY, accelZ;
		private float centerX, centerY, centerZ;
		// **************************************************
		
		public InputAccelHandler(Context context) {
			SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
			if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
				Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
			}
			// gravity = new float[3];
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			/*
			// Spizzheno s http://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-accel
			// Isolate the force of gravity with the low-pass filter.
			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
			
			// Remove the gravity contribution with the high-pass filter.
			accelX = event.values[0] - gravity[0];
			accelY = event.values[1] - gravity[1];
			accelZ = event.values[2] - gravity[2];*/
			
			accelX = event.values[0]-centerX;
			accelY = event.values[1]-centerY;
			accelZ = event.values[2]-centerZ;
		}

		public float getAccelX() { return accelX; }
		public float getAccelY() { return accelY; }
		public float getAccelZ() { return accelZ; }

		public void centralize() {
			centerX = accelX;
			centerY = accelY;
			centerZ = accelZ;
		}
	}
	
	public class InputTouchHandler implements OnTouchListener {

		private static final int MAX_TOUCHPOINTS = 10;
		private boolean[] isTouched = new boolean[MAX_TOUCHPOINTS];
		private int[] touchX = new int[MAX_TOUCHPOINTS];
		private int[] touchY = new int[MAX_TOUCHPOINTS];
		private Pool<TouchEvent> touchEventsPool;
		private List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
		private List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();

		// ****************************************************************************

		public InputTouchHandler(View view) {
			PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
				@Override
				public TouchEvent createObject() {
					return new TouchEvent();
				}
			};
			touchEventsPool = new Pool<TouchEvent>(factory, 100);
			view.setOnTouchListener(this);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			synchronized (this) {
				int action = event.getAction() & MotionEvent.ACTION_MASK;
				int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				int pointerId = event.getPointerId(pointerIndex);
				TouchEvent touchEvent;
				switch (action) {
				
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					touchEvent = touchEventsPool.newObject();
					touchEvent.type = TouchEvent.TOUCH_DOWN;
					touchEvent.pointer = pointerId;
					touchEvent.x = touchX[pointerId] = (int) (event.getX(pointerIndex));
					touchEvent.y = touchY[pointerId] = (int) (event.getY(pointerIndex));
					isTouched[pointerId] = true;
					touchEventsBuffer.add(touchEvent);
					break;
					
				case MotionEvent.ACTION_MOVE:
					int pointerCount = event.getPointerCount();
					for (int i = 0; i < pointerCount; i++) {
						pointerIndex = i;
						pointerId = event.getPointerId(pointerIndex);
						touchEvent = touchEventsPool.newObject();
						touchEvent.type = TouchEvent.TOUCH_DRAGGED;
						touchEvent.pointer = pointerId;
						touchEvent.x = touchX[pointerId] = (int) (event.getX(pointerIndex));
						touchEvent.y = touchY[pointerId] = (int) (event.getY(pointerIndex));
						touchEventsBuffer.add(touchEvent);
					}
					break;
					
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					touchEvent = touchEventsPool.newObject();
					touchEvent.type = TouchEvent.TOUCH_UP;
					touchEvent.pointer = pointerId;
					touchEvent.x = touchX[pointerId] = (int) (event.getX(pointerIndex));
					touchEvent.y = touchY[pointerId] = (int) (event.getY(pointerIndex));
					isTouched[pointerId] = false;
					touchEventsBuffer.add(touchEvent);
					break;
					
				}
				return true;
			}
		}

		public boolean isTouchDown(int pointer) {
			synchronized (this) {
				if (pointer < 0 || pointer >= 20) return false;
				else return isTouched[pointer];
			}
		}

		public int getTouchX(int pointer) {
			synchronized (this) {
				if (pointer < 0 || pointer >= 20) return 0;
				else return touchX[pointer];
			}
		}

		public int getTouchY(int pointer) {
			synchronized (this) {
				if (pointer < 0 || pointer >= 20) return 0;
				else return touchY[pointer];
			}
		}

		public List<TouchEvent> getTouchEvents() {
			synchronized (this) {
				int len = touchEvents.size();
				for (int i = 0; i < len; i++) {
					touchEventsPool.free(touchEvents.get(i));
				}
				touchEvents.clear();
				touchEvents.addAll(touchEventsBuffer);
				touchEventsBuffer.clear();
				return touchEvents;
			}
		}

	}
	
	private InputTouchHandler touchHandler;
	private InputAccelHandler accelHandler;
	
	public Input(AndroidGame game) {
		Log.d("BroStudios", "Input.Input()");
		touchHandler = new InputTouchHandler(game.getGraphics().getView());
		accelHandler = new InputAccelHandler(game);
	}
	
	@Override
	public boolean isTouchDown(int pointer) {
		return touchHandler.isTouchDown(pointer);
	}

	@Override
	public int getTouchX(int pointer) {
		return touchHandler.getTouchX(pointer);
	}

	@Override
	public int getTouchY(int pointer) {
		return touchHandler.getTouchY(pointer);
	}

	@Override
	public List<TouchEvent> getTouchEvents() {
		return touchHandler.getTouchEvents();
	}

	@Override
	public float getAccelX() {
		return accelHandler.getAccelX();
	}

	@Override
	public float getAccelY() {
		return accelHandler.getAccelY();
	}

	@Override
	public float getAccelZ() {
		return accelHandler.getAccelZ();
	}
	
	public void setAccelCentralize() {
		accelHandler.centralize();
	}

}