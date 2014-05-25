package ru.brostudios.framework;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ru.brostudios.framework.interfaces.FrameworkInterface;
import ru.brostudios.framework.interfaces.ScreenInterface;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public abstract class AndroidGame extends Activity implements FrameworkInterface, GLSurfaceView.Renderer {
	
	protected final boolean DEBUG = false; // отображение инфы в лог (тэг BroStudios)
	private ScreenInterface screen;	
	private Graphics graphics;
	private Input input;

// ********************************************************************************
	public AndroidGame() {
		Log.d("BroStudios", "AndroidGame.AndroidGame()");
	}
	
	// Activity methods
	// ------------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("BroStudios", "AndroidGame.onCreate()");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		
		GLSurfaceView view = new GLSurfaceView(this);
		view.setRenderer(this);
		view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		setContentView(view);
		
		graphics = new Graphics(view);
		input = new Input(this);
	}

	@Override
	public void onResume() {
		Log.d("BroStudios", "FrameworkActivity.onResume()");
		if(screen!=null) screen.resume();
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.d("BroStudios", "FrameworkActivity.onPause()");
		if(screen!=null) screen.pause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.d("BroStudios", "FrameworkActivity.onDestroy()");
		if(screen!=null) screen.destroy();
		super.onDestroy();	
	}
	
	// ------------------------------------------------------------
	public abstract ScreenInterface getStartScreen();
	public abstract void loadGameTextures();
	
	public ScreenInterface getCurrentScreen() {
		return screen;
	}
	
	public void setScreen(ScreenInterface screen) {
		if(screen == null) { Log.d("BroStudios", "Unable to set screen = null"); return; }
		if(this.screen != null) {
			this.screen.pause();
			this.screen.destroy();
		}
		screen.resume();
		screen.update(input.getTouchEvents());
		this.screen = screen;
	}
	

	public Graphics getGraphics() {
		return graphics;
	}
	
	public Input getInput() {
		return input;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		screen.update(input.getTouchEvents());
		screen.present(gl);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		graphics.setGL(gl);
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-width/2.0f, width/2.0f, -height/2.0f, height/2.0f, -1.0f, 1.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		graphics.setGL(gl);
		loadGameTextures();
		if(screen==null) screen = getStartScreen();
	}
	
}