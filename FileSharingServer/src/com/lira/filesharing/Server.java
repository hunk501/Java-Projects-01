package com.lira.filesharing;

import java.util.Scanner;
import java.util.StringTokenizer;

public class Server {

	private static String tag = "LiraSoft:/> ";
	private static final String[] help = {
		"start [port] ---  This will start the Server.",
		"quit ---  This will stop the Server.",
		"help ---  List down all available commands.",
		"online  ---  Display all connected clients.",
		"exit ---  Exit terminal."
		};	
	private static Scanner scan = new Scanner(System.in);
	
	private static ServerThread server;
	
	private static void header(){
		System.out.println("************************************************************************");
		System.out.println("\t\t\t LiraSoft Server Terminal");
		System.out.println("************************************************************************");
		System.out.println(tag +"Welcome to LiraSoft Server Terminal, Type 'help' for more informations.");
	}
	
	/**
	 * Stop the Server
	 */
	public static void stopServer(){
		System.out.print("Stop Server, Are you sure.? (y/n)");
		char confirm = scan.next().charAt(0);
		if(confirm == 'y' || confirm == 'Y'){
			System.out.println("Yes");
			try {
				server.close();
			} catch (Exception e) {
				System.out.println("There is no Open Server running at this time.!");
			}
		} else if(confirm == 'n' || confirm == 'N') {
			System.out.println("No");
		} else {
			System.out.println("Invalid Choice.!");
		}
		Tag();
	}
	
	public static void exitTerminal(){
		System.out.print("Exit Terminal, Are you sure.? (y/n)");
		char confirm = scan.next().charAt(0);
		if(confirm == 'y' || confirm == 'Y'){
			System.out.println("Yes");
			System.out.println("Terminal was closed.!");
			System.exit(0);
		} else if(confirm == 'n' || confirm == 'N') {
			System.out.println("No");
		} else {
			System.out.println("Invalid Choice.!");
		}
		Tag();
	}
	
	private static void Tag(){
		System.out.print(tag);
	}
	
	/**
	 * Get Input from the user
	 */
	public static void getInput(){
		try {
			String input = scan.nextLine(); // get input from user per line
			StringTokenizer st = new StringTokenizer(input);
			// get the command first
			String cmd = st.nextToken();
			cmd.toLowerCase();
			switch(cmd){
				case "help":
					for(int i=0; i < help.length; i++){
						System.out.println(help[i]);
					}
					Tag();
					break;
				case "quit":
					stopServer();
					break;
				case "exit":
					exitTerminal();
					break;
				case "start":
					try {
						int port = Integer.parseInt(st.nextToken()); // get the port
						server = new ServerThread(port);
						server.start();
						Tag();
					} catch (Exception e) {
						System.out.println("Port was not found.!");
						Tag();
					}
					break;
				case "online":
					try {
						server.getConnected();
					} catch (Exception e) {
						System.out.println("No clients connected founds.");
					}
					Tag();
					break;
				default:
					System.out.println("Command was not recognize by the Server., for more information type 'help'.");
					Tag();
					break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		getInput();
	}
	
	private static void loop(){
		Tag();
		getInput();
	}
	
	/**
	 * Get the TAG
	 * @return
	 */
	public static void getTag(){
		System.out.print(tag);
	}
	
	public static void main(String[] args) {
		header();
		loop();
	}

}
