package ru.brostudios.framework;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.util.Log;

public class File {
	
	private String path;
	private FileInputStream in;
	private FileOutputStream out;
	
// ******************************************************************
	
	public File() {
		in = null; out = null;
	}
	
	public boolean open(String path) {
		this.path = path;
		try {
			in = new FileInputStream(path);
			out = new FileOutputStream(path);
		} catch(Exception e) {
			Log.d("BroStudios", "Unable to open file: "+path);
			return false;
		} 
		return true;
	}
// ---------------------
	public boolean create(String path) {
		try {
			java.io.File file = new java.io.File(path);
			return file.createNewFile();
		} catch(Exception ex) {
			Log.d("BroStudios", "Unable to create file: "+path);
		}
		return false;
	}
// ---------------------
	public boolean delete(String path) {
		try {
			java.io.File file = new java.io.File(path);
			return file.delete();
		} catch(Exception ex) {
			Log.d("BroStudios", "Unable to delete file: "+path);
		}
		return false;
	}

	public String read() {
		// TODO: correct this method
		if(in == null) return "";
		try {
			if(in==null) in = new FileInputStream(path);
			byte[] buffer = new byte[in.available()];		 // number of bytes equals our buffer
			in.read(buffer);
			in.close();
			return new String(buffer);
		}
		catch(Exception ex) {}
		return "";
	}

	public void write(String text) {
		// TODO: correct this method, bleat'
		try {
			out = new FileOutputStream(path);
			out.write(text.getBytes());
			out.close();
			out = null;
		}
		catch(Exception ex) {
			
		}
	}

	public static InputStream LoadFileFromAssets(AssetManager assets, String fileName) throws IOException {
		return assets.open(fileName);
	}
}