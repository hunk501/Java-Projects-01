package com.lira.filesharing;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class MainApp extends JFrame {

	private JPanel contentPane;
	private JTextField txtFilePath;
	private JTextField txtUsername;
	private JTable table;
	private JProgressBar progressBar;
	private JButton btnBrowse;
	private JButton btnSendFile;
	private JFileChooser filechooser = new JFileChooser();
	private String folder = "D:\\";
	private final String[] columns = {"Receive Files", "Status"};
	private Object[][] rowData = { };
	
	private static String myusername = "No Username";
	
	private int lop = 0;
	
	/**
	 * Update Progress Bar
	 * @param val
	 */
	public void updateProgress(int val){
		progressBar.setString(val +"% Uploading");
		progressBar.setValue(val);
	}
	
	/**
	 * This will update the progress of receiving file
	 * @param rows
	 * @param val
	 */
	public synchronized void update(int rows, int percent, String str){
		table.setValueAt(percent+"% "+ str, rows, 1);
	}
	
	/**
	 * Disable GUI
	 */
	public void disableGUI(){
		btnSendFile.setText("Uploading");
		Component[] comp = {txtFilePath, txtUsername, btnBrowse, btnSendFile};
		for(Component c : comp){
			c.setEnabled(false);
		}
	}
	
	/**
	 * Enabled GUI
	 */
	public void enableGUI(){
		Component[] comp = {txtFilePath, txtUsername, btnBrowse, btnSendFile};
		for(Component c : comp){
			c.setEnabled(true);
		}
		btnSendFile.setText("Send File");
		progressBar.setValue(0);
		progressBar.setString("0%");
	}
	
	/**
	 * This will handle the display in the table and
	 * progress of the file that was receive.
	 * @param filename
	 * @return
	 */
	public int receiveFile(String filename){
		String fname = filename;
		TableHandler th = new TableHandler(rowData, columns, table, filename);
		table.setModel(th.getTableModel());
		// update old data
		rowData = th.getData();
		// Start Thread
//		ProgressHandler ph = new ProgressHandler(MainApp.this, th.getIndex());
//		Thread tt = new Thread(ph);
//		tt.start();
		return th.getIndex();
	}
	
	/**
	 * Get Folder
	 * @return
	 */
	public String getFolder(){
		return folder;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
			for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()){
				if("Nimbus".equals(info.getName())){
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					int r = 1 +(int)(Math.random() * 10);
					String u = JOptionPane.showInputDialog("Please Enter your Username: ");
					if(u != null && u.length() >= 1){					
						myusername = u.replace(" ", "_");
					} else {
						myusername = "user"+ r;
					}
					
					MainApp frame = new MainApp();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainApp() {
		setResizable(false);
		setTitle("FileSharing: "+ myusername);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 607, 466);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menubar = new JMenu("File");
		menuBar.add(menubar);
		
		JMenuItem itemDownloadFolder = new JMenuItem("Download Folder");
		itemDownloadFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int retval = filechooser.showDialog(MainApp.this, "Browse Folder");
				if(retval == JFileChooser.APPROVE_OPTION){
					folder = filechooser.getSelectedFile().getPath();
				}
			}
		});
		menubar.add(itemDownloadFolder);
		
		JMenu mnNewMenu_1 = new JMenu("Menu 2");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Item 2");
		mnNewMenu_1.add(mntmNewMenuItem_1);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel1 = new JPanel();
		panel1.setForeground(Color.WHITE);
		panel1.setBackground(SystemColor.controlShadow);
		panel1.setBounds(6, 6, 588, 178);
		contentPane.add(panel1);
		panel1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Select File:");
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(21, 32, 62, 16);
		panel1.add(lblNewLabel);
		
		txtFilePath = new JTextField();
		txtFilePath.setEditable(false);
		txtFilePath.setBounds(95, 26, 372, 28);
		panel1.add(txtFilePath);
		txtFilePath.setColumns(10);
		
		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = filechooser.showDialog(rootPane, "Browse File");
				if(returnVal == JFileChooser.APPROVE_OPTION){
					txtFilePath.setText(filechooser.getSelectedFile().toString());
					btnSendFile.setEnabled(true);
					txtUsername.setEnabled(true);
					progressBar.setEnabled(true);
				}
			}
		});
		btnBrowse.setBounds(479, 26, 88, 28);
		panel1.add(btnBrowse);
		
		JLabel lblNewLabel_1 = new JLabel("Send To:");
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(21, 72, 62, 16);
		panel1.add(lblNewLabel_1);
		
		txtUsername = new JTextField();
		txtUsername.setEnabled(false);
		txtUsername.setBounds(95, 66, 372, 28);
		panel1.add(txtUsername);
		txtUsername.setColumns(10);
		
		btnSendFile = new JButton("Send File");
		btnSendFile.setEnabled(false);
		btnSendFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String u = txtUsername.getText().trim();
				if( (u.length() > 0) && (txtFilePath.getText().length() > 0) ){
					//disableGUI();
					lop++;
					String usr = lop+"_"+myusername;					
					SendingFile sf = new SendingFile(MainApp.this, txtFilePath.getText(), txtUsername.getText(), usr, Host.getHost(), Host.getPort());
					sf.start();					
				} else {
					JOptionPane.showMessageDialog(rootPane, "Some fields must be filled.!" ,"Incomplete", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSendFile.setBounds(479, 66, 88, 28);
		panel1.add(btnSendFile);
		
		progressBar = new JProgressBar();
		progressBar.setEnabled(false);
		progressBar.setFont(new Font("SansSerif", Font.BOLD, 12));
		progressBar.setForeground(UIManager.getColor("ArrowButton.disabledText"));
		progressBar.setStringPainted(true);
		progressBar.setBounds(21, 119, 549, 39);		
		panel1.add(progressBar);
		
		JPanel panel2 = new JPanel();
		panel2.setBackground(SystemColor.control);
		panel2.setBounds(6, 196, 588, 210);
		contentPane.add(panel2);
		panel2.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 6, 576, 198);
		panel2.add(scrollPane);
		
		table = new JTable();
		table.setFont(new Font("SansSerif", Font.PLAIN, 12));
		scrollPane.setViewportView(table);
		table.setShowVerticalLines(true);
		table.setModel(new DefaultTableModel(rowData,columns));
		table.getColumnModel().getColumn(0).setPreferredWidth(140);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		
		
		
		// Connect to Server
		MainThread mt = new MainThread(this, myusername);
		Thread t = new Thread(mt);
		t.start();
	}
	
}














