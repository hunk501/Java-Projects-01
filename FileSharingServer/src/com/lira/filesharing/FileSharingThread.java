package com.lira.filesharing;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class FileSharingThread extends Thread {

	private final Socket socket;
	private final ServerThread serverthread;
	private String myusername;
	private DataInputStream input;
	private DataOutputStream output;
	private boolean keepGoing = false;
	
	public FileSharingThread(Socket s, String usr, ServerThread ss) {
		this.socket = s;
		this.myusername = usr;
		this.serverthread = ss;
		// initialize streams
		try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			keepGoing = true;
		} catch (Exception e) {
			keepGoing = false;
		}
	}
	
	/**
	 * This will write data to a Socket
	 * @param s
	 * @param cmd
	 */
	private void writeOutput(Socket s, String cmd){
		try {
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This will send a response to the client
	 * @param action example.(error, rejected, accepted, fatal)
	 */
	private void sendResponse(String action, String from){
		try {
			// format: ([cmd_receive_response_file] [action] [from])
			output.writeUTF("cmd_receive_response_file "+ action +" "+ from);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * This will send the file from the sender client
	 * to the Receiver client
	 * @param st
	 */
	private void sendFile(StringTokenizer st){
		// format: ([cmd_sendfile] [to] [filename] [size])
		String to = st.nextToken();
		String filename = st.nextToken();
		int size = Integer.parseInt(st.nextToken());
		Socket s = serverthread.getFileSharingSocket(to); // get the Receiver Client Socket in File Sharing mode
		String cmd = "cmd_receivefile "+ filename +" "+ size; // format: ([cmd_receivefile] [filename] [size])
		// write to stream
		writeOutput(s, cmd);
		/**
		 * Read bytes from Stream and Send it
		 */
		try {
			InputStream is = socket.getInputStream();
			OutputStream os = s.getOutputStream();
			BufferedInputStream b = new BufferedInputStream(is);
			byte[] buf = new byte[1024];
			int c;
			while((c = b.read(buf)) != -1){
				os.write(buf, 0, c);
			}
			os.flush();
			b.close();
			is.close();
			os.close();
			keepGoing = false;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void run(){
		/**
		 * This infinite loop will read all the input data from this connection socket
		 */
		while(keepGoing){
			try {
				String data = input.readUTF(); // get input data from stream
				StringTokenizer st = new StringTokenizer(data);
				// get the command
				String cmd = st.nextToken();
				switch(cmd){
				
					/**
					 * Send a request for file sharing
					 */
					case "cmd_sendrequest_file": // format: ([cmd_sendrequest_file] [to] [from] [filename] [size])
						String r_to = st.nextToken(); // receiver client
						String r_from = st.nextToken(); // sender client
						String r_filename = st.nextToken();
						String r_size = st.nextToken();
						// Get Client Receiver socket
						Socket s = serverthread.getClientSocket(r_to);
						if(s != null){
							String str1 = "cmd_receiverequest_file "+ r_from +" "+ r_filename +" "+ r_size; // format: ([cmd_receiverequest_file] [from] [filename] [size])   
							writeOutput(s, str1);
						} else {
							// Send a Response
							sendResponse("error", r_to); // format: ([cmd_receive_response_file] [action] [from])
						}
						break;
					
					/**
					 * This will send the file
					 * to the respective clients
					 */
					case "cmd_sendfile": // format: ([cmd_sendfile] [to] [filename] [size])
						sendFile(st);
						break;
						
				}
			} catch (Exception e) {
				keepGoing = false;
			}
		}
		
		// If we got here, it means were connected anymore.
		// release the connection
		try {
			socket.close();
			System.out.println("Client '"+myusername+"'  was closed.");
			serverthread.removeClient(myusername); // remove this client from the list
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
