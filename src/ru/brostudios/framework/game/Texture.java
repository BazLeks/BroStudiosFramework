package ru.brostudios.framework.game;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import ru.brostudios.framework.AndroidGame;
import ru.brostudios.framework.File;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class Texture {
	
	public AndroidGame game;
	public int glID, width, height;
	public String filePath;
	
// ***************************************************
	
	public Texture(AndroidGame game, String path) {
		this.game = game;
		this.filePath = path;
		load();
	}
	
	public void load() {
		GL10 gl = game.getGraphics().getGL();
		gl.glEnable(GL10.GL_TEXTURE_2D);
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		try {
			// исходный поток
			InputStream in = File.LoadFileFromAssets(game.getAssets(), filePath);
			// грузим только размеры картинки
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, options);
			in.close();
			int origWidth = options.outWidth; // исходная ширина 
			int origHeight = options.outHeight; // исходная высота 
			int bytesPerPixel = 4; // 4 байта на пиксел (argb-модель)
			int maxSize = 480 * 800 * bytesPerPixel; // максимально разрешенный размер Bitmap
			int desiredWidth = 512;	// нужная ширина 
			int desiredHeight = 512; // нужная высота
			int desiredSize = desiredWidth * desiredHeight * bytesPerPixel; //����������� ����������� ������ Bitmap ��� �������� width � height
			if (desiredSize < maxSize) maxSize = desiredSize;
			int scale = 1; // кратность уменьшения
			int origSize = origWidth * origHeight * bytesPerPixel;
			// высчитываем кратность уменьшения
			if (origWidth > origHeight) {
				scale = Math.round((float) origHeight / (float) desiredHeight);
			} else {
				scale = Math.round((float) origWidth / (float) desiredWidth);
			}
			options = new BitmapFactory.Options();
			options.inSampleSize = scale;
			options.inPreferredConfig = Config.ARGB_8888;
			
			// обяхательно открываем поток
			in = File.LoadFileFromAssets(game.getAssets(), filePath);
			Bitmap bitmap = BitmapFactory.decodeStream(in, null, options); 
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			this.width = bitmap.getWidth(); this.height = bitmap.getHeight();
			bitmap.recycle();
		} catch(Exception e) {
			Log.d("Load texture", e.getMessage());
		}
		glID = textures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	public final int getWidth() { return width; }
	public final int getHeight() { return height; }
}