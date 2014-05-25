package ru.brostudios.framework.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import ru.brostudios.framework.AndroidGame;

public class Sprite {

	protected AndroidGame game;
	
	protected Texture texture;
	protected FloatBuffer floatBuffer;
	private int m, n, i;	// m-строка, n-столбец, i-текущий кадр
	private float texWidth, texHeight;
// *********************************************************************************
	
	public Sprite(AndroidGame game, Texture texture, int mFrames, int nFrames) {
		m = mFrames; n = nFrames; i = 0;
		floatBuffer = ByteBuffer.allocateDirect((2+2)*4*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		this.game = game;
		this.texture = texture;
		
		// размер кадра текстуры
		float texWidth = texture.getWidth()/n;
		float texHeight = texture.getHeight()/m;
		
		// нормализуем, относительно пропорций кадра текстуры
		if(texWidth>texHeight) { texHeight /= texWidth;	texWidth /= texWidth;
		} else { texWidth /= texHeight; texHeight /= texHeight; }
		this.texWidth = texWidth;
		this.texHeight = texHeight;
		
		floatBuffer.put(new float[] {-texWidth/2f, +texHeight/2f, 0, 0,
									 -texWidth/2f, -texHeight/2f, 0, 1,
									 +texWidth/2f, +texHeight/2f, 1, 0,
									 +texWidth/2f, -texHeight/2f, 1, 1 });
		
	}
	public final float getFrameWidth() { return texWidth; }
	public final float getFrameHeight() { return texHeight; }
	
	public void setFrame(int frameNum) {
		if(frameNum>m*n) Log.d("BroStudios", "Number of frame is too big (i="+i+"), max="+m*n);
		else i = frameNum;
	}
	
	public void setStart() {
		i = 0;
	}
	public void nextFrame() {
		i++; if(i>m*n) i=0;
	}
	public boolean isFinish() {
		if(i==m*n) return true;
		else return false;
	}
	
	public void present(GL10 gl, float x, float y, float scale) {
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		floatBuffer.position(0);
		gl.glVertexPointer(2, GL10.GL_FLOAT, 4*4, floatBuffer);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		floatBuffer.position(2);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 4*4, floatBuffer);
		
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glTranslatef((i%n)/(float)n, (i/n)/(float)m, 0.0f);
		gl.glScalef(1.0f/n, 1.0f/m, 1.0f);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glTranslatef(x, y, 0);
		gl.glScalef(scale, scale, 1.0f);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.glID);
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glPopMatrix();
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	public final Texture getTexture() { return texture; }
}