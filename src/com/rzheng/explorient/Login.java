/*
 * This file is part of MineSweeper
 * Copyright (C) 2015-2019 Richard R. Zheng
 *
 * https://github.com/rzheng95/MineSweeper
 * 
 * All Right Reserved.
 */

package com.rzheng.explorient;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPasswordField;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Login {

	private JFrame frame;
	private JTextField textFieldUsername;
	private JPasswordField passwordField;
	private Image logo, icon;
	private JLabel lblUsernameWarning, lblPasswordWarning, lblLoginMessage, lblClock;
	private Connection connection = null;
	private JButton btnLogin;
	private HintTextField textFieldDatabaseLocation;
	private JButton btnFolderChooser;
	private JFileChooser chooser;
	private String choosertitle;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
		
		// if Directory.txt exists, auto fill the directory to the textFieldDatabaseLocation
		boolean dirExist = new File(System.getProperty("user.dir"), "Directory.txt").exists();
		if(dirExist)
		{
			try{
				FileReader fr = new FileReader("Directory.txt");
				BufferedReader br = new BufferedReader(fr);
				
				String str;
				while((str = br.readLine()) != null)
				{
					textFieldDatabaseLocation.setDir(str);
				}					
				
				br.close();
			}catch(IOException e1){
				JOptionPane.showMessageDialog(null, "File not found");
			}
		}
			
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){}
		frame = new JFrame();
			
		frame.setResizable(false);
		frame.setTitle("Login");
		logo = new ImageIcon(this.getClass().getResource("/Explorient Logo.png")).getImage();
		icon = new ImageIcon(this.getClass().getResource("/Explorient Icon.jpg")).getImage();
		frame.setIconImage(icon);
		frame.setBounds(100, 100, 320, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);// put this after setSize and pack
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblUsername.setBounds(10, 130, 80, 20);
		frame.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPassword.setBounds(10, 180, 80, 20);
		frame.getContentPane().add(lblPassword);
		
		textFieldUsername = new JTextField();
		textFieldUsername.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldUsername.setBounds(10, 150, 290, 23);
		frame.getContentPane().add(textFieldUsername);
		textFieldUsername.setColumns(10);
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(!textFieldDatabaseLocation.getText().equals(""))
				{

					connection = sqliteConnection.dbConnector(textFieldDatabaseLocation.getText());
										
					boolean dirExist = new File(System.getProperty("user.dir"), "Directory.txt").exists();
					// if Directory.txt doesn't exist, create the file and save the user entered directory
					if(!dirExist) {
						try{
							FileWriter fw = new FileWriter("Directory.txt");
							PrintWriter pw = new PrintWriter(fw);
							
							String x = textFieldDatabaseLocation.getText();					
							pw.println(x);
							
							pw.close();
						}catch(IOException e){
							JOptionPane.showMessageDialog(null, e);
						}
					}
					else // it exists
					{
						try{
							FileReader fr = new FileReader("Directory.txt");
							BufferedReader br = new BufferedReader(fr);
							// However, user moved the Database to another folder, then save the new directory provided by user
							// this will override the old directory
							if(!br.readLine().equals(textFieldDatabaseLocation.getText()))
							{							
								try{
									FileWriter fw = new FileWriter("Directory.txt");
									PrintWriter pw = new PrintWriter(fw);
									
									String x = textFieldDatabaseLocation.getText();					
									pw.println(x);
									
									pw.close();
								}catch(IOException e){
									JOptionPane.showMessageDialog(null, e);
								}
							}
							br.close();
						}catch(IOException e1){
							JOptionPane.showMessageDialog(null, "File not found");
						}
					}		
				}
				else
				{
					lblLoginMessage.setText("Please Provide Database Directory.");
					lblLoginMessage.setForeground(Color.red);
					return;
				}
				
				lblUsernameWarning.setText("");
				lblPasswordWarning.setText("");
				if(textFieldUsername.getText().trim().equals("")){
					lblUsernameWarning.setText("Username cannot be blank!");	
					lblUsernameWarning.setForeground (Color.red);
				}
				if(passwordField.getText().trim().equals("")){
					lblPasswordWarning.setText("Password cannot be blank!");
					lblPasswordWarning.setForeground (Color.red);
				}
				
				// authenticate login request
				try{
					String query = "select Username,Password from Administrators where Username=? and password=? ";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.setString(1, textFieldUsername.getText()); // index starts from 1
					pst.setString(2, passwordField.getText());
					
					ResultSet rs = pst.executeQuery();
					int count = 0;
					while(rs.next())
					{		
						count++;
					}
					if(count == 1)
					{
						lblLoginMessage.setText("Welcome!...");
						lblLoginMessage.setForeground (Color.blue);
						
						
						//TimeUnit.SECONDS.sleep(3);					
						
						// Start another JFrame
						frame.dispose();
						MainMenu v = new MainMenu();
						v.setVisible(true);
					}
					else if(count > 1)
					{
						// this shouldn't happen, check sqlite
						JOptionPane.showMessageDialog(null, "Duplicate Username and password");
					}
					else
					{
						lblLoginMessage.setText("Username or Password Incorrect!");
						lblLoginMessage.setForeground (Color.red);
					}
					
					// Closing connection
					rs.close();
					pst.close();
				}catch(Exception e)
				{
					JOptionPane.showMessageDialog(null, e);
				}		
				
			}
		});
		btnLogin.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnLogin.setBounds(105, 250, 100, 45);
		frame.getContentPane().add(btnLogin);
		
		JLabel labelLogo = new JLabel("");
		labelLogo.setIcon(new ImageIcon(logo));
		labelLogo.setBounds(-45, 0, 365, 105);
		frame.getContentPane().add(labelLogo);
		
		passwordField = new JPasswordField();
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER ) {
					btnLogin.doClick();
				}
			}
		});
		passwordField.setBounds(10, 200, 290, 23);
		frame.getContentPane().add(passwordField);
		
		lblUsernameWarning = new JLabel("");
		lblUsernameWarning.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblUsernameWarning.setBounds(74, 175, 226, 14);
		frame.getContentPane().add(lblUsernameWarning);
		
		lblPasswordWarning = new JLabel("");
		lblPasswordWarning.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPasswordWarning.setBounds(74, 225, 226, 14);
		frame.getContentPane().add(lblPasswordWarning);
		
		lblLoginMessage = new JLabel("");
		lblLoginMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginMessage.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblLoginMessage.setBounds(10, 299, 290, 20);
		frame.getContentPane().add(lblLoginMessage);
		
		lblClock = new JLabel("");
		lblClock.setHorizontalAlignment(SwingConstants.RIGHT);
		lblClock.setBounds(263, 11, 243, 14);
		frame.getContentPane().add(lblClock);
		
		JLabel lblLocalDatabaseLocation = new JLabel("Database Directory:");
		lblLocalDatabaseLocation.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblLocalDatabaseLocation.setBounds(10, 330, 150, 20);
		frame.getContentPane().add(lblLocalDatabaseLocation);
		
		textFieldDatabaseLocation = new HintTextField("e.g = Y:\\Users\\Richard\\Dropbox\\Database");
		textFieldDatabaseLocation.setBounds(10, 350, 263, 25);
		frame.getContentPane().add(textFieldDatabaseLocation);
		textFieldDatabaseLocation.setColumns(10);
		
		btnFolderChooser = new JButton("New button");
		btnFolderChooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser = new JFileChooser(); 
				FileFilter filter = new FileNameExtensionFilter("SQLite File", "sqlite");
				chooser.setFileFilter(filter);
				int response = chooser.showOpenDialog(frame);
				if(response == JFileChooser.APPROVE_OPTION)
					textFieldDatabaseLocation.setDir(chooser.getSelectedFile().toString());
				
			}
		});
		btnFolderChooser.setBounds(275, 350, 30, 25);
		frame.getContentPane().add(btnFolderChooser);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnSelect = new JMenu("Select");
		menuBar.add(mnSelect);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnSelect.add(mntmExit);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		//clock();
	}
	
	public void clock()
	{
		Thread clock = new Thread()
		{
			public void run()
			{
				try {
					for(;;)//infinite loop same as while(true){}
					{
						Calendar cal = new GregorianCalendar();
						int day = cal.get(Calendar.DAY_OF_MONTH);
						int month = cal.get(Calendar.MONTH);
						int year = cal.get(Calendar.YEAR);
						
						int second = cal.get(Calendar.SECOND);
						int minute = cal.get(Calendar.MINUTE);
						int hour = cal.get(Calendar.HOUR);
						
						//lblClock.setText("<html>Time  "+hour+":"+minute+":"+second+"<br> Date  "+(month+1)+"/"+day+"/"+year+"<html>");
						lblClock.setText("Time:  "+hour+":"+minute+":"+second+"   Date:  "+(month+1)+"/"+day+"/"+year);
					sleep(1000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		clock.start();
	}	
}











