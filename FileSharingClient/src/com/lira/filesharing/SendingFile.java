package com.lira.filesharing;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class SendingFile extends Thread {

	private final MainApp main;
	private String toUsername;
	private String myUsername;
	
	private Socket socket;
	
	private DataInputStream input;
	private DataOutputStream output;
	private final int BUFFER_SIZE = 1024;
	private boolean keepGoing = false;
	
	private Files files;
	
	public SendingFile(MainApp _main, String file, String user1, String user2, String _host, int _port){
		this.main = _main;
		this.toUsername = user1;
		this.myUsername = user2; // user2
		files = new Files(file);
		// connect to Server
		try {
			socket = new Socket(_host, _port);
			// initialize Streams
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());
			// write our username
			output.writeUTF("cmd_join_filesharing "+ myUsername); // format: ([cmd_join_filesharing] [username])
			keepGoing = true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(main, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			keepGoing = false;
		}
	}
	
	/**
	 * Send Request
	 */
	private void sendRequest(int size){
		try {
			// format: ([cmd_sendrequest] [to] [from] [filename] [size])
			output.writeUTF("cmd_sendrequest_file "+ toUsername +" "+ myUsername +" "+ files.getName() + " "+ size);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(main, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * This will Send a file to the
	 * Respective Clients
	 */
	private void sendFile(String to){
		try {
			/**
			 * First: Send the filename and Size of the file
			 */
			// format: ([cmd_sendfile] [to] [filename] [size])
			output.writeUTF("cmd_sendfile "+ to +" "+ files.getName() +" "+ files.getSize());
			/**
			 * Second: Send now the binary data
			 */
			InputStream input = new FileInputStream(new File(files.getAbsolutePath()));
			BufferedInputStream bis = new BufferedInputStream(input);
			byte[] buf = new byte[BUFFER_SIZE];
			int count, read_bytes=0;
			while((count = bis.read(buf)) != -1){
				read_bytes = read_bytes + count;
				int p = (read_bytes / files.getSize()) / 11;
				main.updateProgress(p);
				output.write(buf, 0, count); // write bytes to Output stream
			}
			output.flush();
			main.enableGUI();
			keepGoing = false;
			bis.close();
			input.close();
			output.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(main, e.getMessage(), "SendFile", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void run(){
		/**
		 * Prepare the file for sending
		 * Then send a Request to the client
		 */
		sendRequest(files.getSize());
		
		/**
		 * 	this infinite loop will
		 *  Read data from input stream
		 */
		while(keepGoing){
			try {
				String data = input.readUTF(); // get data from input stream
				StringTokenizer st = new StringTokenizer(data);
				// get the command
				String cmd = st.nextToken();
				switch(cmd){
					/**
					 * Receive file response
					 */
					case "cmd_receive_response_file": // format: ([cmd_receive_response_file] [action] [from]) example.(error, rejected, accepted, fatal)
						String action = st.nextToken();
						String r_from = st.nextToken();
						if(action.equalsIgnoreCase("error") || action.equalsIgnoreCase("fatal")){
							JOptionPane.showMessageDialog(main, "Client is not online at this time.!, please try again later.", "Response", JOptionPane.ERROR_MESSAGE);
							keepGoing = false;
						} else if(action.equalsIgnoreCase("rejected")){
							JOptionPane.showMessageDialog(main, "Client Rejected the request.!", "Response", JOptionPane.WARNING_MESSAGE);
							keepGoing = false;
						} else if(action.equalsIgnoreCase("accepted")){
							/**
							 * Client Accepted the Request
							 * now were ready to send the file.
							 */
							JOptionPane.showMessageDialog(main, "Client Accepted the request.!", "Response", JOptionPane.INFORMATION_MESSAGE);
							main.disableGUI();
							sendFile(r_from);
						}
						break;
					default:
						JOptionPane.showMessageDialog(main, "Unknown Command '"+ cmd +"' in SendingFile", "Unknown", JOptionPane.ERROR_MESSAGE);
						break;
				}
			} catch (Exception e) {
				//JOptionPane.showMessageDialog(main, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				keepGoing = true;
			}
		}
		
		System.out.println("'"+myUsername +"' SendingFile Thread was closed.");
		
	}
}















