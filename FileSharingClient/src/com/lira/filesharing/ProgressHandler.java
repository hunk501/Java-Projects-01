package com.lira.filesharing;

import javax.swing.JOptionPane;

public class ProgressHandler extends Thread {

	private int rows;
	private final MainApp main;
	private final int max = 100;
	private int min = 0;
	
	public ProgressHandler(MainApp _main, int _rows){
		this.rows = _rows;
		this.main = _main;
	}
	
	@Override
	public void run(){
		try {
			while(min <= max){				
				if(min == 100){
					main.update(rows, min, "Complete");
				} else {
					main.update(rows, min, "Downloading");
				}
				min++;
				Thread.sleep(1000);
			}
			System.out.println("Download Complete.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(main, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
