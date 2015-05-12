package com.lira.filesharing;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class TableHandler {
	
	private int index;
	private Object[][] object;
	private String[] columns;
	
	public TableHandler(Object[][] olddata, String[] cols, JTable table, String filename){
		this.columns = cols;
		int rows = olddata.length;
		Object[][] newdata = new Object[rows+1][2]; // new Object storage
		// Step through old data then add it to the new storage
		int i=0;
		for(; i < rows; i++){
			newdata[i][0] = table.getValueAt(i, 0); // first column
			newdata[i][1] = table.getValueAt(i, 1); // second column
		}
		// now add our new data to combine with the old data
		newdata[i][0] = filename;
		newdata[i][1] = "0% Complete";
		// set index
		this.index = i;
		// set Object
		object = newdata;
	}
	
	
	/**
	 * Get The index
	 * @return
	 */
	public int getIndex(){
		return index;
	}
	
	/**
	 * Get the data
	 * @return
	 */
	public Object[][] getData(){
		return object;
	}
	
	/**
	 * Get the table model
	 * @return
	 */
	public DefaultTableModel getTableModel(){
		DefaultTableModel df = new DefaultTableModel(object, columns);
		return df;
	}
	
	
}
