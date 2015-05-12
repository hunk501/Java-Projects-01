package com.lira.filesharing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientThread extends Thread {

	private final Socket socket;
	private final String myusername;
	private final ServerThread serverthread;
	
	private DataInputStream input;
	private DataOutputStream output;
	private boolean keepGoing = false;
	
	public ClientThread(Socket s, String usr, ServerThread ss) {
		this.socket = s;
		this.myusername = usr;
		this.serverthread = ss;
		// Initialize Stream
		try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			keepGoing = true;
		} catch (Exception e) {
			e.printStackTrace();
			keepGoing = false;
		}
	}

	/**
	 * Write Data to OutputStream
	 * @param s
	 * @param cmd
	 */
	private void writeData(Socket s, String cmd){
		try {
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Receive File Response
	 * @param st
	 */
	private void getFileResponse(StringTokenizer st){
		// format: ([cmd_sendresponse_file] [to] [action] [from])
		String to = st.nextToken();
		String action = st.nextToken();
		String from = st.nextToken();
		Socket s = serverthread.getFileSharingSocket(to);
		String cmd = "cmd_receive_response_file "+ action +" "+ from; // format: ([cmd_receive_response_file] [action] [from]) example.(error, rejected, accepted, fatal)
		writeData(s, cmd); // write data
	}
	
	@Override
	public void run(){
		/**
		 * This infinite loop will
		 * Read the data from input Stream
		 */
		while(keepGoing){
			try {
				String data = input.readUTF();
				StringTokenizer st = new StringTokenizer(data);
				String cmd = st.nextToken();
				switch(cmd){
					
					/**
					 * Receive a File Response
					 */
					case "cmd_sendresponse_file": // format: ([cmd_sendresponse_file] [to] [action] [from])
						getFileResponse(st);
						break;
				}
				
				// Sleep
				//Thread.sleep(200);
			} catch (Exception e) {
				keepGoing = false;
			}
		}
	}

}
