package com.lira.filesharing;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class ReceivingFile extends Thread {

	private String myusername;
	private String host;
	private int port;
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private boolean keepGoing = false;
	private MainApp main;
	private int BUFFER_SIZE = 1024;
	private int filesize;
	
	public ReceivingFile(String myusr, String _host, int _port, MainApp _main){
		this.myusername = myusr;
		this.host = _host;
		this.port = _port;
		this.main = _main;
		/**
		 * Connect to Server
		 */
		try {
			socket = new Socket(host, port);
			// initialize streams
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			/**
			 * Write our user name
			 */
			output.writeUTF("cmd_join_filesharing "+ myusername); // format: ([cmd_join_filesharing] [username])
			keepGoing = true;
			System.out.println("Receiving Thread started...");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(main, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			keepGoing = false;
		}
	}
	
	/**
	 * This will handle the Receiving File and
	 * Display in the Tables and update progress
	 * @param st
	 */
	private void receiving(StringTokenizer st){
		// format: ([cmd_receivefile] [filename] [size])
		String filename = st.nextToken();
		int size = Integer.parseInt(st.nextToken());
		filesize = size;
		// Add to the table
		int row_index =  main.receiveFile(filename);	
		/**
		 * Read bytes from Streams
		 */
		InputStream is = null;
		OutputStream os = null;
		try {
			is = socket.getInputStream();
			os = new FileOutputStream(main.getFolder() +""+ filename);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buf = new byte[BUFFER_SIZE];
			int count, reads=0;
			while((count = bis.read(buf)) != -1){
				os.write(buf, 0, count);
				reads = reads + count;
				int p = (reads / filesize) / 11;
				main.update(row_index, p, "Downloading");
			}
		} catch (Exception e) {
			System.out.println("Receiving: " +e.getMessage());
		} finally {
			try {
				main.update(row_index, 100, "Complete");
				keepGoing = false;
				os.close();
				is.close();
				System.out.println("Receiving: Done");
			} catch (Exception e2) {
				System.out.println("Receiving: " +e2.getMessage());
			}
		}
	}
	
	/**
	 * This will Send a response to a client that send a request
	 * informing that we are ready to receive a file right now.
	 * @param sock
	 * @param cmd
	 */
	public void sendResponse(Socket sock, String cmd){
		try {
			DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
			dos.writeUTF(cmd);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(main, e.getMessage(), "Send Response", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void run(){
		while(keepGoing){
			try {
				/**
				 * REad the incoming data
				 */
				String data = input.readUTF();
				StringTokenizer st = new StringTokenizer(data);
				String cmd = st.nextToken(); // get the Command
				switch(cmd){
				
					/**
					 * Receiving File
					 */
					case "cmd_receivefile": // format: ([cmd_receivefile] [filename] [size])
						receiving(st);
						break;
					default:
						JOptionPane.showMessageDialog(main, "Unknown Command '"+ cmd +"' in ReceivingFile\nAction: "+ st.nextToken() +"\nFrom: "+ st.nextToken(), "Unknown", JOptionPane.ERROR_MESSAGE);
						break;
						
				}
			} catch (Exception e) {
				//JOptionPane.showMessageDialog(main, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				keepGoing = false;
			}
		}
		System.out.println(myusername +" ReceivingFile was Closed.");
	}
}
















