package Explorient;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JButton;
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
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Login {

	private JFrame frame;
	private JTextField textFieldUsername;
	private JPasswordField passwordField;
	private Image logo, icon;
	private JLabel lblUsernameWarning, lblPasswordWarning, lblLoginMessage, lblClock;
	private Connection connection = null;
	private JButton btnLogin;

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
		connection = sqliteConnection.dbConnector("Explorient");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){}
		frame = new JFrame();
		
		frame.setResizable(false);
		frame.setTitle("Login");
		logo = new ImageIcon(this.getClass().getResource("/Explorient Logo.jpg")).getImage();
		icon = new ImageIcon(this.getClass().getResource("/Explorient Icon.jpg")).getImage();
		frame.setIconImage(icon);
		frame.setBounds(100, 100, 536, 315);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);// put this after setSize and pack
		
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblUsername.setBounds(10, 38, 94, 29);
		frame.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPassword.setBounds(10, 78, 94, 36);
		frame.getContentPane().add(lblPassword);
		
		textFieldUsername = new JTextField();
		textFieldUsername.setBounds(98, 43, 122, 20);
		frame.getContentPane().add(textFieldUsername);
		textFieldUsername.setColumns(10);
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblUsernameWarning.setText("");
				lblPasswordWarning.setText("");
				if(textFieldUsername.getText().trim().equals("")){
					lblUsernameWarning.setText("Username Can NOT be blank!");	
					lblUsernameWarning.setForeground (Color.red);
				}
				if(passwordField.getText().trim().equals("")){
					lblPasswordWarning.setText("Password Can NOT be blank!");
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
						Voucher v = new Voucher();
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
		btnLogin.setBounds(98, 135, 89, 23);
		frame.getContentPane().add(btnLogin);
		
		JLabel labelLogo = new JLabel("");
		labelLogo.setIcon(new ImageIcon(logo));
		labelLogo.setBounds(263, 38, 243, 179);
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
		passwordField.setBounds(98, 87, 122, 20);
		frame.getContentPane().add(passwordField);
		
		lblUsernameWarning = new JLabel("");
		lblUsernameWarning.setBounds(98, 62, 155, 14);
		frame.getContentPane().add(lblUsernameWarning);
		
		lblPasswordWarning = new JLabel("");
		lblPasswordWarning.setBounds(98, 110, 155, 14);
		frame.getContentPane().add(lblPasswordWarning);
		
		lblLoginMessage = new JLabel("");
		lblLoginMessage.setHorizontalAlignment(SwingConstants.CENTER);
		lblLoginMessage.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblLoginMessage.setBounds(44, 169, 199, 34);
		frame.getContentPane().add(lblLoginMessage);
		
		lblClock = new JLabel("");
		lblClock.setHorizontalAlignment(SwingConstants.RIGHT);
		lblClock.setBounds(263, 11, 243, 14);
		frame.getContentPane().add(lblClock);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnSelect = new JMenu("Select");
		menuBar.add(mnSelect);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnSelect.add(mntmExit);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		
		clock();
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
