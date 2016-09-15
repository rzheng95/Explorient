package Explorient;


import java.awt.Color;
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.SwingConstants;
import javax.swing.JTabbedPane;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;


public class Voucher extends JFrame 
{
	private Connection connection = null;
	private JTextField textFieldBookingNumber;
	private JTable tableVoucher;
	private Image icon;
	private JTable tablePassengers;
	private JComboBox comboBoxType, comboBoxManifest, comboBoxRoomType;
	private JSpinner spinnerPassenger, spinnerNight;
	private JXDatePicker datePickerCheckIn;
	private JTextArea textAreaDescriptions;
	private int voucher;
	private String voucherID;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Voucher frame = new Voucher("");
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
	public Voucher(String bookingNumber) {
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){}
		
		try{
			FileReader fr = new FileReader("Directory.txt");
			BufferedReader br = new BufferedReader(fr);
			
			String str;
			while((str = br.readLine()) != null)		
				connection = sqliteConnection.dbConnector(str+"\\Explorient.sqlite");									
			br.close();
		}catch(IOException e1){
			JOptionPane.showMessageDialog(null, "File not found");
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1246, 625);
		//setResizable(false);
		setTitle("Voucher");
		setLocationRelativeTo(null);
		icon = new ImageIcon(this.getClass().getResource("/Explorient Icon.jpg")).getImage();
		setIconImage(icon);
		
		JLabel lblBookingNumber = new JLabel("Booking #:");
		lblBookingNumber.setBounds(10, 10, 80, 20);
		lblBookingNumber.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		textFieldBookingNumber = new JTextField(bookingNumber);
		textFieldBookingNumber.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldBookingNumber.setBounds(100, 10, 110, 23);
		textFieldBookingNumber.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				try{
					String query = "select * from B"+textFieldBookingNumber.getText();
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					tableVoucher.setModel(DbUtils.resultSetToTableModel(rs));
					
					refreshTable("B"+textFieldBookingNumber.getText(), "Voucher");
					pst.close();
					rs.close();	
				}catch(Exception e1){
					//e1.printStackTrace();
				}
				
				try{							
					String query = "select * from PaxInfos where BookingNumber="+textFieldBookingNumber.getText();
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					
					tablePassengers.setModel(DbUtils.resultSetToTableModel(rs));		
					pst.close();
					rs.close();
				}catch(Exception e1){
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
		tabbedPane.setBounds(220, 9, 1000, 545);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Voucher", null, scrollPane, null);
		
		tableVoucher = new JTable();
		tableVoucher.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try{
					int row = tableVoucher.getSelectedRow();
					voucherID = (tableVoucher.getModel().getValueAt(row, 0)).toString();
									
					String query = "select * from B"+textFieldBookingNumber.getText()+" where VoucherID='"+voucherID+"'";
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					
					
					comboBoxType.setSelectedItem(rs.getString("Type"));
					comboBoxRoomType.setSelectedItem(rs.getString("RoomType"));
					spinnerPassenger.setValue(rs.getInt("NumOfPax"));
					spinnerNight.setValue(rs.getInt("NumOfNight"));
					// Date picker
					// JOptionPane.showMessageDialog(null, "The day is "+d.getDate()+" Year is "+(d.getYear()+1900)+" the month is "+(d.getMonth()+1));
					//String date = rs.getString("Date");
					String[] dateArr = rs.getString("Date").split("/");
					Date d = new Date((Integer.parseInt(dateArr[2])-1900),(Integer.parseInt(dateArr[0])-1),Integer.parseInt(dateArr[1]));
					datePickerCheckIn.setDate(d);
					comboBoxManifest.setSelectedItem(rs.getString("Manifest"));
					textAreaDescriptions.setText(rs.getString("Description"));
					
					
					


					
					
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
		comboBoxType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 1)
				{
					if(e.getItem().equals("Land Service"))
					{
						comboBoxRoomType.setSelectedItem("");
						comboBoxRoomType.setEditable(false);
						comboBoxRoomType.setEnabled(false);				
						spinnerNight.setEnabled(false);
					}
					if(e.getItem().equals("Hotel"))
					{
						comboBoxRoomType.setEditable(true);
						comboBoxRoomType.setEnabled(true);
						spinnerNight.setEnabled(true);
					}
				}
			}
		});
		comboBoxType.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxType.setModel(new DefaultComboBoxModel(new String[] {"Hotel", "Land Service"}));
		comboBoxType.setBounds(100, 50, 110, 23);
		getContentPane().add(comboBoxType);
		
		datePickerCheckIn = new JXDatePicker();
		datePickerCheckIn.getEditor().setFont(new Font("Tahoma", Font.PLAIN, 11));
		datePickerCheckIn.setBounds(100, 210, 110, 20);
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
		spinnerPassenger.setBounds(100, 130, 110, 23);
		getContentPane().add(spinnerPassenger);
		
		JLabel lblDescriptions = new JLabel("Descriptions:");
		lblDescriptions.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDescriptions.setBounds(10, 290, 80, 20);
		getContentPane().add(lblDescriptions);
		
		textAreaDescriptions = new JTextArea();
		textAreaDescriptions.setFont(new Font("Tahoma", Font.PLAIN, 12));
		Border border = BorderFactory.createLineBorder(Color.black);
		textAreaDescriptions.setBorder(border);
		textAreaDescriptions.setLineWrap(true);
		textAreaDescriptions.setWrapStyleWord(true);
		textAreaDescriptions.setBounds(10, 315, 190, 160);
		getContentPane().add(textAreaDescriptions);
		
		JLabel lblPassengers = new JLabel("Passenger:");
		lblPassengers.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPassengers.setBounds(10, 130, 80, 20);
		getContentPane().add(lblPassengers);
		
		spinnerNight = new JSpinner();
		spinnerNight.setFont(new Font("Tahoma", Font.PLAIN, 12));
		spinnerNight.setBounds(100, 170, 110, 23);
		getContentPane().add(spinnerNight);
		
		JLabel lblManifest = new JLabel("Manifest:");
		lblManifest.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblManifest.setBounds(10, 250, 80, 20);
		getContentPane().add(lblManifest);
		
		comboBoxManifest = new JComboBox();
		comboBoxManifest.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxManifest.setEditable(true);
		comboBoxManifest.setBounds(100, 250, 110, 23);
		getContentPane().add(comboBoxManifest);
		
		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// grab Voucher number from IDs
				try{				
					String query = "select * from IDs where EXPID=1 ";
					Statement stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(query);	
					voucher = rs.getInt("Voucher");	
					voucher++;
					stmt.close();
					rs.close();
				}catch(Exception e1){e1.printStackTrace();}	
				
				
				
				try{					
					String query;
					PreparedStatement pst;
					Date d = datePickerCheckIn.getDate();
					if(comboBoxType.getSelectedItem().equals("Hotel"))
					{
						query = "insert into "+"B"+textFieldBookingNumber.getText()+" (VoucherID,Type,NumOfPax,NumOfNight,RoomType,Date,Manifest,Description) values (?,?,?,?,?,?,?,?)";
						pst = connection.prepareStatement(query);
						pst.setString(1, "EXPV"+voucher);
						pst.setString(2, comboBoxType.getSelectedItem().toString());
						pst.setString(3, spinnerPassenger.getValue().toString());
						pst.setString(4, spinnerNight.getValue().toString());
						pst.setString(5, comboBoxRoomType.getSelectedItem().toString());
						pst.setString(6, ""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900));
						pst.setString(7, comboBoxManifest.getSelectedItem().toString());
						pst.setString(8, textAreaDescriptions.getText());
					}
					else
					{
						query = "insert into "+"B"+textFieldBookingNumber.getText()+" (VoucherID,Type,NumOfPax,Date,Manifest,Description) values (?,?,?,?,?,?)";
						pst = connection.prepareStatement(query);
						pst.setString(1, "EXPV"+voucher);
						pst.setString(2, comboBoxType.getSelectedItem().toString());
						pst.setString(3, spinnerPassenger.getValue().toString());
						pst.setString(4, ""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900));
						pst.setString(5, comboBoxManifest.getSelectedItem().toString());
						pst.setString(6, textAreaDescriptions.getText());
					}
					pst.execute();					
					//JOptionPane.showMessageDialog(null, "Data saved!");					
					pst.close();
					
				}catch(Exception e1){e1.printStackTrace();}
				
				
				// update booking and passenger in IDs
				try{				
					//String query = "Update IDs set EXPID='"+1+"' ,Booking='"+(lastBookingNumber+1)+"' ,Voucher='"+voucher+"' ,Passenger='"+passenger+"' where EXPID='"+1+"'  ";
					String query = "Update IDs set EXPID='"+1+"' , Voucher='"+voucher+"' where EXPID='"+1+"'  ";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.execute();					
					//JOptionPane.showMessageDialog(null, "Voucher ID Updated");					
					pst.close();
					}catch(Exception e1){
					e1.printStackTrace();
				}
				
				refreshTable("B"+textFieldBookingNumber.getText(), "Voucher");
			}
		});
		btnCreate.setBounds(10, 490, 90, 25);
		getContentPane().add(btnCreate);
		
		JLabel lblRoomType = new JLabel("Room Type:");
		lblRoomType.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblRoomType.setBounds(10, 90, 90, 20);
		getContentPane().add(lblRoomType);
		
		comboBoxRoomType = new JComboBox();
		comboBoxRoomType.setModel(new DefaultComboBoxModel(new String[] {"Deluxe Room", "Deluxe City View Room", "Deluxe River View Room", "Premium Room", "Superior Room"}));
		comboBoxRoomType.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxRoomType.setEditable(true);
		comboBoxRoomType.setBounds(100, 90, 110, 23);
		getContentPane().add(comboBoxRoomType);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clear();			
			}
		});
		btnClear.setBounds(111, 490, 90, 25);
		getContentPane().add(btnClear);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
					
					Date d = datePickerCheckIn.getDate();
					if(comboBoxType.getSelectedItem().equals("Hotel"))
					{
						try{
							String query = "Update "+"B"+textFieldBookingNumber.getText()+" set VoucherID='"+voucherID+"' ,Type='"+comboBoxType.getSelectedItem()+"',"
									+ " RoomType='"+comboBoxRoomType.getSelectedItem()+"' ,NumOfPax='"+spinnerPassenger.getValue()+"' ,NumOfNight='"+spinnerNight.getValue()
									+"' ,Date='"+""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900)+"' ,Manifest='"+comboBoxManifest.getSelectedItem()
									+"' ,Description='"+textAreaDescriptions.getText()+"' where VoucherID='"+voucherID+"'";
							PreparedStatement pst = connection.prepareStatement(query);
	
							pst.execute();
							
							JOptionPane.showMessageDialog(null, "Data Updated");
							
							pst.close();
							
						}catch(Exception e1){e1.printStackTrace();}
					}
					if(comboBoxType.getSelectedItem().equals("Land Service"))
					{
						try{
							String query = "Update "+"B"+textFieldBookingNumber.getText()+" set VoucherID='"+voucherID+"' ,Type='"+comboBoxType.getSelectedItem()+"',"
									+ " RoomType='"+""+"' ,NumOfPax='"+spinnerPassenger.getValue()+"' ,NumOfNight='"+""
									+"' ,Date='"+""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900)+"' ,Manifest='"+comboBoxManifest.getSelectedItem()
									+"' ,Description='"+textAreaDescriptions.getText()+"' where VoucherID='"+voucherID+"'";
							PreparedStatement pst = connection.prepareStatement(query);
	
							pst.execute();
							
							JOptionPane.showMessageDialog(null, "Data Updated");
							
							pst.close();
							
						}catch(Exception e1){e1.printStackTrace();}
					}
				refreshTable("B"+textFieldBookingNumber.getText(), "Voucher");
			}
		});
		btnUpdate.setBounds(10, 525, 90, 25);
		getContentPane().add(btnUpdate);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int action = JOptionPane.showConfirmDialog(null, "Did you try updating voucher? \nDo you really want to delete this voucher?", "Delete", JOptionPane.YES_NO_OPTION);
				if(action==0){
					try{
						
						String query = "delete from B"+textFieldBookingNumber.getText()+" where VoucherID='"+voucherID+"' ";
						PreparedStatement pst = connection.prepareStatement(query);	
						clear();
						pst.execute();
						pst.close();						
					}catch(Exception e1){
						e1.printStackTrace();
					}
					refreshTable("B"+textFieldBookingNumber.getText(), "Voucher");
				}
			}
		});
		btnDelete.setBounds(111, 525, 90, 25);
		getContentPane().add(btnDelete);
		
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
		
		JMenuItem mntmPrint = new JMenuItem("Print");
		mnFile.add(mntmPrint);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);

	}
	
	public void refreshTable(String tableName, String jtable)
	{
		try{
			String query = "select * from "+tableName;
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			if(jtable.equals("Voucher"))
				tableVoucher.setModel(DbUtils.resultSetToTableModel(rs));
			if(jtable.equals("Passenger"))
				tablePassengers.setModel(DbUtils.resultSetToTableModel(rs));
				
			
			
			pst.close();
			rs.close();
		}catch(Exception e1)
		{
			e1.printStackTrace();
		}
	}
	
	
	public void clear()
	{
		comboBoxRoomType.setSelectedItem("");
		spinnerPassenger.setValue(0);
		spinnerNight.setValue(0);
		datePickerCheckIn.setDate(null);
		comboBoxManifest.setSelectedItem("");
		textAreaDescriptions.setText("");
	}
}

