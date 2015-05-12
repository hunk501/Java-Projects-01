package com.lira.filesharing;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class MainThread extends Thread {

	private final MainApp main;
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private String myusername;
	private boolean keepGoing = false;
	private int lop = 0;
	
	public MainThread(MainApp main, String usr) {
		this.main = main;
		this.myusername = usr;
		int port = Host.getPort();
		String host = Host.getHost();
		// Connect to server
		try {
			socket = new Socket(host, port);
			// initialize Streams
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			// write our username
			output.writeUTF("cmd_join "+ myusername);
			keepGoing = true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(main, "Unable to Connect to Server in Port \""+ port +"\"", "Error", JOptionPane.ERROR_MESSAGE);
			keepGoing = false;
			System.exit(0);
		}
	}
	
	/**
	 * Write data to OutputStream
	 * @param s
	 * @param cmd
	 */
	private void writeData(Socket s, String cmd){
		try {
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF(cmd);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(main, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	
	/**
	 * Receive file Request
	 * @param st
	 */
	private void getReceiveFileRequest(StringTokenizer st){
		// format: ([cmd_receiverequest_file] [from] [filename] [size]) 
		String r_from = st.nextToken();
		String r_filename = st.nextToken();
		String r_size = st.nextToken();
		
		lop++;
		String usr = lop+"_"+myusername;
		
		Object[] option = {"Reject", "Accept"};
		String msg = "A filesharing request has been received.!\nFrom: "+ r_from +"\nFile: "+ r_filename +"\nSize: "+ r_size +" kb";
		int confirm = JOptionPane.showOptionDialog(main, msg, "FileSharing", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, option, option[1]);
		if(confirm == 1){ // Accepted
			
			System.out.println(myusername +" Accepted");
			/**
			 * Now that we accepted the request
			 * we need to join first for file sharing socket/Connection
			 * then start the thread for I/O Streams
			 */
			ReceivingFile rth = new ReceivingFile(usr, Host.getHost(), Host.getPort(), main);
			rth.start();
			/**
			 * After we joined the file sharing connection
			 * send back a response to client that sent the request
			 * telling that i accept the request now send me the file.
			 */
			// format: ([cmd_sendresponse_file] [to] [action] [from])
			String cmd = "cmd_sendresponse_file "+ r_from +" "+ "accepted" +" "+ usr;
			//writeData(socket, cmd); // write data
			rth.sendResponse(socket, cmd);
		} 
		else { // Rejected
			System.out.println(myusername+ " Rejected");
			// format: ([cmd_sendresponse_file] [to] [action])
			String cmd = "cmd_sendresponse_file "+ r_from +" "+ "rejected" +" "+ usr;
			writeData(socket, cmd); // write data
		}
	}

	@Override
	public void run(){
		/**
		 * This infinite loop will
		 * read the input data from stream
		 */
		while(keepGoing){
			try {
				String data = input.readUTF();
				StringTokenizer st = new StringTokenizer(data);
				String cmd = st.nextToken();
				switch(cmd){
				
					/**
					 * Receive a File Sharing request
					 */
					case "cmd_receiverequest_file": // format: ([cmd_receiverequest_file] [from] [filename] [size]) 
						getReceiveFileRequest(st);
						break;
					default:
						JOptionPane.showMessageDialog(main, "Unknown Command '"+ cmd +"' in MainThread", "Unknown", JOptionPane.ERROR_MESSAGE);
						break;
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(main, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				keepGoing = false;
			}
		}
		
		System.out.println("MainThread was closed.!");
	}
}
