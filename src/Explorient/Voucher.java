package Explorient;


import java.awt.Color;
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import net.proteanit.sql.DbUtils;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingConstants;
import javax.swing.JTabbedPane;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;


public class Voucher extends JFrame 
{
	private Connection connection = null;
	private JTextField textFieldBookingNumber;
	private JTable tableVoucher;
	private Image icon;
	private JTable tablePassengers;
	private JComboBox comboBoxHotelCode, comboBoxType;
	private JSpinner spinnerPassenger, spinnerNight;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Voucher frame = new Voucher();
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
	public Voucher() {
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){}
		connection = sqliteConnection.dbConnector("Y:\\Users\\Richard\\Dropbox\\Database\\Explorient.sqlite");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1236, 745);
		//setResizable(false);
		setTitle("Voucher");
		setLocationRelativeTo(null);
		icon = new ImageIcon(this.getClass().getResource("/Explorient Icon.jpg")).getImage();
		setIconImage(icon);
		
		JLabel lblBookingNumber = new JLabel("Booking #:");
		lblBookingNumber.setBounds(10, 10, 80, 20);
		lblBookingNumber.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		textFieldBookingNumber = new JTextField();
		textFieldBookingNumber.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldBookingNumber.setBounds(90, 10, 110, 23);
		textFieldBookingNumber.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				try{
					String query = "select * from B"+textFieldBookingNumber.getText();
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					tableVoucher.setModel(DbUtils.resultSetToTableModel(rs));
					
					refreshTable("B"+textFieldBookingNumber.getText());
					pst.close();
					rs.close();
					
					try{
						
						String query1 = "select * from PaxInfos where BookingNumber=? ";
						PreparedStatement pst1 = connection.prepareStatement(query1);
						pst1.setString(1, textFieldBookingNumber.getText());
						ResultSet rs1 = pst1.executeQuery();
						
						/*if(rs1.getString("Firstname") != null)
							lblPassengerNames.setText(rs1.getString("Firstname"));
						if(rs1.getString("Middlename") != null)
							lblPassengerNames.setText(lblPassengerNames.getText()+" "+rs1.getString("Middlename"));
						if(rs1.getString("Lastname") != null)
							lblPassengerNames.setText(lblPassengerNames.getText()+" "+rs1.getString("Lastname"));*/
						
						tablePassengers.setModel(DbUtils.resultSetToTableModel(rs1));
						
						pst1.close();
						rs1.close();
						
					}catch(Exception e1)
					{
						e1.printStackTrace();
					}
					
				}catch(Exception e1)
				{
					//e1.printStackTrace();
				}
				
			}
			@Override
			public void keyTyped(KeyEvent event) {
				char c = event.getKeyChar();
				if(!(Character.isDigit(c) || (c==KeyEvent.VK_BACK_SPACE) || c==KeyEvent.VK_DELETE)){
					getToolkit().beep();
					event.consume();// unable to press that key
				}
			}
		});
		textFieldBookingNumber.setColumns(10);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(203, 0, 1007, 670);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Voucher", null, scrollPane, null);
		
		tableVoucher = new JTable();
		tableVoucher.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try{
					int row = tableVoucher.getSelectedRow();
					String voucherID = (tableVoucher.getModel().getValueAt(row, 0)).toString();
					
					
					String query = "select * from B"+textFieldBookingNumber.getText()+" where VoucherID='"+voucherID+"'";
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					

					comboBoxType.setSelectedItem(rs.getString("Type"));
					comboBoxHotelCode.setSelectedItem(rs.getString("HotelCode"));
					spinnerPassenger.setValue(rs.getInt("NumOfPax"));
					


					
					
					pst.close();
					
				}catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}
		});
		tableVoucher.setFont(new Font("Tahoma", Font.BOLD, 12));
		scrollPane.setViewportView(tableVoucher);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("Passengers", null, scrollPane_1, null);
		
		tablePassengers = new JTable();
		tablePassengers.setFont(new Font("Tahoma", Font.BOLD, 12));
		scrollPane_1.setViewportView(tablePassengers);
		getContentPane().setLayout(null);
		getContentPane().add(lblBookingNumber);
		getContentPane().add(textFieldBookingNumber);
		getContentPane().add(tabbedPane);
		
		JLabel lblType = new JLabel("Type:");
		lblType.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblType.setBounds(10, 50, 80, 20);
		getContentPane().add(lblType);
		
		comboBoxType = new JComboBox();
		comboBoxType.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxType.setModel(new DefaultComboBoxModel(new String[] {"Hotel", "Land Service"}));
		comboBoxType.setEditable(true);
		comboBoxType.setBounds(90, 50, 110, 23);
		getContentPane().add(comboBoxType);
		
		JXDatePicker datePickerCheckIn = new JXDatePicker();
		datePickerCheckIn.getEditor().setFont(new Font("Tahoma", Font.PLAIN, 11));
		datePickerCheckIn.setBounds(90, 210, 110, 20);
		getContentPane().add(datePickerCheckIn);
		
		
		JLabel lblDateCheckIn = new JLabel("Check In:");
		lblDateCheckIn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDateCheckIn.setBounds(10, 210, 80, 20);
		getContentPane().add(lblDateCheckIn);
		
		JLabel lblNight = new JLabel("Night:");
		lblNight.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNight.setBounds(10, 170, 80, 20);
		getContentPane().add(lblNight);
		
		spinnerPassenger = new JSpinner();
		spinnerPassenger.setFont(new Font("Tahoma", Font.PLAIN, 12));
		spinnerPassenger.setBounds(90, 130, 110, 23);
		getContentPane().add(spinnerPassenger);
		
		JLabel lblDescriptions = new JLabel("Descriptions:");
		lblDescriptions.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDescriptions.setBounds(10, 290, 80, 20);
		getContentPane().add(lblDescriptions);
		
		JTextArea textAreaDescriptions = new JTextArea();
		textAreaDescriptions.setFont(new Font("Tahoma", Font.PLAIN, 12));
		Border border = BorderFactory.createLineBorder(Color.black);
		textAreaDescriptions.setBorder(border);
		textAreaDescriptions.setLineWrap(true);
		textAreaDescriptions.setWrapStyleWord(true);
		textAreaDescriptions.setBounds(10, 320, 190, 160);
		getContentPane().add(textAreaDescriptions);
		
		JLabel lblHotelCode = new JLabel("Hotel Code:");
		lblHotelCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblHotelCode.setBounds(10, 90, 80, 20);
		getContentPane().add(lblHotelCode);
		
		comboBoxHotelCode = new JComboBox();
		comboBoxHotelCode.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxHotelCode.setEditable(true);
		comboBoxHotelCode.setBounds(90, 90, 110, 23);
		getContentPane().add(comboBoxHotelCode);
		
		JLabel lblPassengers = new JLabel("Passenger:");
		lblPassengers.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPassengers.setBounds(10, 130, 80, 20);
		getContentPane().add(lblPassengers);
		
		spinnerNight = new JSpinner();
		spinnerNight.setFont(new Font("Tahoma", Font.PLAIN, 12));
		spinnerNight.setBounds(90, 170, 110, 23);
		getContentPane().add(spinnerNight);
		
		JLabel lblManifest = new JLabel("Manifest:");
		lblManifest.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblManifest.setBounds(10, 250, 80, 20);
		getContentPane().add(lblManifest);
		
		JComboBox comboBoxManifest = new JComboBox();
		comboBoxManifest.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxManifest.setEditable(true);
		comboBoxManifest.setBounds(90, 252, 110, 23);
		getContentPane().add(comboBoxManifest);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmCreateNewBooking = new JMenuItem("Create New Booking");
		mntmCreateNewBooking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				CreateNewBooking cnb = new CreateNewBooking();
				cnb.setVisible(true);
			}
		});
		mnFile.add(mntmCreateNewBooking);
		
		JMenuItem mntmSearch = new JMenuItem("Search");
		mnFile.add(mntmSearch);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);

	}
	
	public void refreshTable(String tableName)
	{
		try{
			String query = "select * from "+tableName;
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			tableVoucher.setModel(DbUtils.resultSetToTableModel(rs));
			
			
			pst.close();
			rs.close();
		}catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}
}

