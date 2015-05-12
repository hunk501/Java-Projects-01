package com.lira.filesharing;

import java.io.File;

public class Files {
	
	private File file;
	private final int READ_SIZE = 1024;
	
	public Files(String _file){
		try {
			File files = new File(_file);
			this.file = files;
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * Get the filename only
	 * @return
	 */
	public String getName(){
		String fname = file.getName();
		return fname.replace(" ", "_");
	}
	
	/**
	 * Get the file size
	 * Read by READ_SIZE length
	 * @return
	 */
	public int getSize(){
		int f = (int)file.length();
		int size = (int) Math.ceil(f / READ_SIZE);
		return size;
	}
	
	/**
	 * Get the Absolute path
	 * @return
	 */
	public String getAbsolutePath(){
		return file.getAbsolutePath();
	}
}
