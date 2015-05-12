package com.lira.filesharing;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class ServerThread extends Thread {

	private int port;
	
	private ServerSocket serverSocket;
	private boolean keepGoing = false;
	
	//  File sharing client username and socket
	private final ArrayList<ListItems> filesharinglist = new ArrayList<ListItems>();
	
	//  Client list username and socket
	private final ArrayList<ListItems> clientlist = new ArrayList<ListItems>();
	
	public ServerThread(int p){
		this.port = p;
		try {
			System.out.println("Starting Server in Port '"+port+"'");
			serverSocket = new ServerSocket(port);
			System.out.println("Server is now started.");
			System.out.println("waiting from clients to connect.!");
			keepGoing = true;
		} catch (Exception e) {
			keepGoing = false;
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Close The Server
	 */
	public void close(){
		try {
			if(keepGoing){
				keepGoing = false;
				System.out.println("Closing Server....");
				serverSocket.close();
				System.out.println("Server in Port '"+port+"' was terminated.!");
			} else {
				System.out.println("Server is already closed.!");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Display all Connected Client
	 */
	public void getConnected(){
		try {
			int x=0;
			System.out.println("--- Client List --- ");
			Iterator<ListItems> i = clientlist.iterator();
			while(i.hasNext()){
				ListItems li = i.next();
				System.out.println("Username: "+ li.getUsername());
				System.out.println("Socket: "+ li.getSocket().toString());
				x++;
			}
			System.out.println("\n--- File Sharing List--- ");
			Iterator<ListItems> i2 = filesharinglist.iterator();
			while(i2.hasNext()){
				ListItems li = i2.next();
				System.out.println("Username: "+ li.getUsername());
				System.out.println("Socket: "+ li.getSocket().toString());
				x++;
			}
			if(x <= 0){
				System.out.println("No client was found at this time.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Remove client from the list
	 * @param usr
	 */
	public void removeClient(String usr){
		Iterator<ListItems> i = filesharinglist.iterator();
		while(i.hasNext()){
			ListItems li = i.next();
			if(li.getUsername().equalsIgnoreCase(usr)){
				i.remove(); // remove from the list
				break;
			}
		}
		System.out.println("Done.");
		Server.getTag();
	}
	
	/**
	 * Get File sharing client socket
	 * @param usr
	 * @return
	 */
	public Socket getFileSharingSocket(String usr){
		Socket s = null;
		Iterator<ListItems> i = filesharinglist.iterator();
		while(i.hasNext()){
			ListItems li = i.next();
			if(li.getUsername().equalsIgnoreCase(usr)){
				s = li.getSocket();
				break;
			}
		}
		return s;
	}
	
	/**
	 * Get the Client Socket
	 * @param usr
	 * @return
	 */
	public Socket getClientSocket(String usr){
		Socket s = null;
		Iterator<ListItems> i = clientlist.iterator();
		while(i.hasNext()){
			ListItems li = i.next();
			if(li.getUsername().equalsIgnoreCase(usr)){
				s = li.getSocket();
				break;
			}
		}
		return s;
	}
	
	@Override
	public void run(){
		while(keepGoing){
			try {
				Socket socket = serverSocket.accept();
				// client connected
				// first get the connection socket and username then stored it on list for later use.
				DataInputStream input = new DataInputStream(socket.getInputStream());
				StringTokenizer st = new StringTokenizer(input.readUTF());
				String cmd = st.nextToken(); // get the command tokens
				switch(cmd){
					/**
					 * Join Client
					 */
					case "cmd_join": // format: ([cmd_join] [username])
						String usr1 = st.nextToken();
						ListItems li1 = new ListItems(usr1, socket);
						clientlist.add(li1);
						// Start Thread
						ClientThread ct = new ClientThread(socket, usr1, this);
						Thread t_ct = new Thread(ct);
						t_ct.start();
						break;
						
					/**
					 * Join File sharing
					 */
					case "cmd_join_filesharing": // format: ([cmd_join_filesharing] [username])
						String usr = st.nextToken(); // client user name
						ListItems li = new ListItems(usr, socket);
						filesharinglist.add(li);
						// start thread
						FileSharingThread fst = new FileSharingThread(socket, usr, this);
						Thread t_fst = new Thread(fst);
						t_fst.start();
						break;
						
					default:
						System.out.println("Unknown Command '"+cmd+"' ");
						System.out.println("From: "+ socket.toString());
						Server.getTag();
						break;
				}
				
				// sleep for a while
				//Thread.sleep(200);
			} catch (Exception e) {
				keepGoing = false;
			}
		}
	}
}
