package ru.brostudios.framework;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;

public class Graphics {

	private GLSurfaceView view; 
	private GL10 gl;
	
	public Graphics(GLSurfaceView view) {
		this.view = view; 
	}
	
	public void setGL(GL10 gl) { this.gl = gl; }
	public GL10 getGL() { return gl; }
	public int getWidth() { return view.getWidth(); }
	public int getHeight() { return view.getHeight(); }
	public GLSurfaceView getView() { return view; }
	
}