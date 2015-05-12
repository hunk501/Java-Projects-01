package com.lira.filesharing;

import java.net.Socket;

public class ListItems {

	private Socket socket;
	private String username;
	
	public ListItems(String usr, Socket soc) {
		this.socket = soc;
		this.username = usr;
	}

	/**
	 * Get User name
	 * @return
	 */
	public String getUsername(){
		return username;
	}
	
	/**
	 * Get Socket
	 * @return
	 */
	public Socket getSocket(){
		return socket;
	}
}










