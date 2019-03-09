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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jdesktop.swingx.JXDatePicker;

import com.rzheng.autocompletecomboBox.AutocompleteJComboBox;
import com.rzheng.autocompletecomboBox.StringSearchable;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JTextField;
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

import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


public class Voucher extends JFrame 
{
	private Connection connection = null;
	private JTextField textFieldBookingNumber;
	private static JTable tableVoucher;
	private Image icon;
	private JTable tablePassengers;
	private JComboBox comboBoxType, comboBoxRoomType;
	private JSpinner spinnerPassenger, spinnerNight, spinnerNumOfRoom;
	private JXDatePicker datePickerCheckIn, datePickerIssueDate;
	private JTextArea textAreaService;
	private int voucher;
	private String voucherID;
	private JButton btnCreate, btnUpdate, btnDelete;
	private List<Object> printContent;
	private JCheckBox chckbxDinner, chckbxBreakfast, chckbxLunch;
	private JFileChooser chooser;
	private JLabel lblManifest, lblNight, lblTour, lblPassengers;
	private JComboBox comboBoxRoomSize;
	private AutocompleteJComboBox comboBoxVendors;
	private JLabel lblIssueDate;
	private JCheckBox chckbxIssueDate;
	private ArrayList<String> vendorList;
	private static Voucher frame;
	private JMenuItem mntmContactList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new Voucher("");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static JTable getTable()
	{
		if (tableVoucher == null) createTable();
		return tableVoucher;
	}
	
	private static void createTable() {
		tableVoucher = new JTable();
	}

	public void enableEditing(boolean b)
	{
		btnCreate.setEnabled(b);
	}
	public Voucher(String bookingNumber) {
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){}
		// establish connection to database
		try{
			FileReader fr = new FileReader("Directory.txt");
			BufferedReader br = new BufferedReader(fr);
			
			String str;
			while((str = br.readLine()) != null)		
				connection = sqliteConnection.dbConnector(str);									
			br.close();
		}catch(IOException e1){
			JOptionPane.showMessageDialog(null, "File not found");
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1246, 747);
		//setResizable(false);
		setTitle("Voucher System");
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
				DefaultTableModel model = (DefaultTableModel)tableVoucher.getModel();
				if(setPassengerNumber()<=0){
					model.setRowCount(0);
					clear();
					enableEditing(false);
					return;
				}
				
				try{
					String query = "select * from Vouchers where BookingNumber="+textFieldBookingNumber.getText();				
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					tableVoucher.setModel(DbUtils.resultSetToTableModel(rs));
					
					enableEditing(true);
					pst.close();
					rs.close();	
				}catch(Exception e1){
					model.setRowCount(0);
					enableEditing(false);
					//e1.printStackTrace();
				}
				
				setPassengerNumber();	
				
				
				
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
		tabbedPane.setBounds(10, 166, 1210, 521);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Voucher", null, scrollPane, null);
		
		tableVoucher = new JTable();
		tableVoucher.addMouseListener(new MouseAdapter() {
			public void click(MouseEvent arg0, boolean b)
			{
				try{
					int row = tableVoucher.getSelectedRow();
					voucherID = (tableVoucher.getModel().getValueAt(row, 0)).toString();
									
					String query = "select * from Vouchers where VoucherID='"+voucherID+"'";
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					
					
					chckbxIssueDate.setSelected(false);
					comboBoxType.setSelectedItem(rs.getString("VoucherType"));
					comboBoxRoomType.setSelectedItem(rs.getString("RoomType"));
					spinnerPassenger.setValue(rs.getInt("NumOfPax"));
					spinnerNight.setValue(rs.getInt("NumOfNight"));
					spinnerNumOfRoom.setValue(rs.getInt("NumOfRoom"));
					comboBoxRoomSize.setSelectedItem(rs.getString("RoomSize"));
					chckbxIssueDate.setVisible(false);
					datePickerIssueDate.setVisible(true);
					lblIssueDate.setLocation(900, 115);
					String[] dateArr = rs.getString("IssueDate").split("/");
					Date d = new Date((Integer.parseInt(dateArr[2])-1900),(Integer.parseInt(dateArr[0])-1),Integer.parseInt(dateArr[1]));
					datePickerIssueDate.setDate(d);
										
					
					// Date picker
					// JOptionPane.showMessageDialog(null, "The day is "+d.getDate()+" Year is "+(d.getYear()+1900)+" the month is "+(d.getMonth()+1));
					//String date = rs.getString("Date");
					dateArr = rs.getString("Date").split("/");
					d = new Date((Integer.parseInt(dateArr[2])-1900),(Integer.parseInt(dateArr[0])-1),Integer.parseInt(dateArr[1]));
					datePickerCheckIn.setDate(d);
					comboBoxVendors.setSelectedItem(rs.getString("Vendor"));
					comboBoxVendors.hidePopup();
					textAreaService.setText(rs.getString("Service"));		
					String meal = rs.getString("Meal");
					
					if(meal != null)
					{
						chckbxBreakfast.setSelected(false);
						chckbxLunch.setSelected(false);
						chckbxDinner.setSelected(false);
						for(int i=0; i<meal.length(); i++)
						{
							if(meal.charAt(i) == 'B')
								chckbxBreakfast.setSelected(true); 
							if(meal.charAt(i) == 'L')
								chckbxLunch.setSelected(true);
							if(meal.charAt(i) == 'D')
								chckbxDinner.setSelected(true);
						}
					}	
					pst.close();
					rs.close();
				}catch(Exception e1){
					e1.printStackTrace();
				}
				
				if(b)
					click(arg0, false);
			}
			
			
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				click(arg0, true);
				comboBoxVendors.hidePopup();
				frame.requestFocus();
				
			}
			@Override
			public void mousePressed(MouseEvent e) {
				btnDelete.setEnabled(true);
				btnUpdate.setEnabled(true);
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
		
		JLabel lblType = new JLabel("Voucher:");
		lblType.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblType.setBounds(10, 80, 80, 20);
		getContentPane().add(lblType);
		
		comboBoxType = new JComboBox();
		comboBoxType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 1)
				{
					loadVendors();
					StringSearchable searchable = new StringSearchable(vendorList);
					comboBoxVendors.setSeachable(searchable);
					comboBoxVendors.setModel(new DefaultComboBoxModel(vendorList.toArray()));	
					if(e.getItem().equals("Hotel"))
					{
						lblManifest.setText("Hotel:");
						comboBoxRoomType.setEditable(true);
						comboBoxRoomType.setEnabled(true);
						comboBoxRoomSize.setEditable(true);
						comboBoxRoomSize.setEnabled(true);
						spinnerNight.setEnabled(true);
						spinnerNumOfRoom.setEnabled(true);
						textAreaService.setEditable(false);
						textAreaService.setEnabled(false);
						textAreaService.setText("");
						textAreaService.setVisible(false);
						lblTour.setVisible(false);
						
					}
					else
					{
						comboBoxRoomType.setSelectedItem("");
						comboBoxRoomType.setEditable(false);
						comboBoxRoomType.setEnabled(false);	
						comboBoxRoomSize.setSelectedItem("");
						comboBoxRoomSize.setEditable(false);
						comboBoxRoomSize.setEnabled(false);	
						chckbxBreakfast.setSelected(false);
						lblManifest.setText("Vendor:");
						spinnerNight.setValue(0);
						spinnerNight.setEnabled(false);
						spinnerNumOfRoom.setValue(0);
						spinnerNumOfRoom.setEnabled(false);
						textAreaService.setEditable(true);
						textAreaService.setEnabled(true);
						textAreaService.setVisible(true);
						lblTour.setVisible(true);
					}
				}
			}
		});
		comboBoxType.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxType.setModel(new DefaultComboBoxModel(new String[] {"Hotel", "Land Service", "Air Ticket"}));
		comboBoxType.setBounds(100, 80, 110, 23);
		getContentPane().add(comboBoxType);
		
		datePickerCheckIn = new JXDatePicker();
		datePickerCheckIn.getEditor().setFont(new Font("Tahoma", Font.PLAIN, 12));
		datePickerCheckIn.setBounds(305, 80, 225, 20);
		getContentPane().add(datePickerCheckIn);
		

		
		
		JLabel lblDateCheckIn = new JLabel("Check In:");
		lblDateCheckIn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDateCheckIn.setBounds(230, 80, 80, 20);
		getContentPane().add(lblDateCheckIn);
		
		lblNight = new JLabel("Night");
		lblNight.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNight.setBounds(290, 45, 50, 20);
		getContentPane().add(lblNight);
		
		spinnerPassenger = new JSpinner();
		spinnerPassenger.setValue(1);
		spinnerPassenger.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(Integer.parseInt(spinnerPassenger.getValue().toString()) < 1)
				{
					spinnerPassenger.setValue(1);
				}
			}
		});
		spinnerPassenger.setFont(new Font("Tahoma", Font.PLAIN, 12));
		spinnerPassenger.setBounds(100, 45, 110, 23);
		getContentPane().add(spinnerPassenger);
		
		lblTour = new JLabel("Service:");
		lblTour.setVisible(false);
		lblTour.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTour.setBounds(555, 80, 63, 20);
		getContentPane().add(lblTour);
		
		textAreaService = new JTextArea();
		textAreaService.setVisible(false);
		textAreaService.setEditable(false);
		textAreaService.setEnabled(false);
		textAreaService.setFont(new Font("Tahoma", Font.PLAIN, 12));
		Border border = BorderFactory.createLineBorder(Color.black);
		textAreaService.setBorder(border);
		textAreaService.setLineWrap(true);
		textAreaService.setWrapStyleWord(true);
		textAreaService.setBounds(622, 80, 598, 23);
		getContentPane().add(textAreaService);
		
		lblPassengers = new JLabel("Passenger:");
		lblPassengers.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPassengers.setBounds(10, 45, 80, 20);
		getContentPane().add(lblPassengers);
		
		spinnerNight = new JSpinner();
		spinnerNight.setValue(1);
		spinnerNight.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(Integer.parseInt(spinnerNight.getValue().toString()) > 1)
					lblNight.setText("Nights");				
				else
				{
					lblNight.setText("Night");
					spinnerNight.setValue(1);
				}
			}
		});
		spinnerNight.setFont(new Font("Tahoma", Font.PLAIN, 12));
		spinnerNight.setBounds(230, 45, 50, 23);
		getContentPane().add(spinnerNight);
		
		lblManifest = new JLabel("Hotel:");
		lblManifest.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblManifest.setBounds(230, 10, 70, 20);
		getContentPane().add(lblManifest);
		
		
		loadVendors();
		StringSearchable searchable = new StringSearchable(vendorList);

		comboBoxVendors = new AutocompleteJComboBox(searchable);
		
		comboBoxVendors.setModel(new DefaultComboBoxModel(vendorList.toArray()));	
		
		
		comboBoxVendors.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxVendors.setBounds(305, 10, 870, 23);
		getContentPane().add(comboBoxVendors);
		
		
		btnCreate = new JButton("Create");
		btnCreate.setEnabled(false);
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {						
				if(!checkValid()) return;
				
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
					
					String issueDate = "";
					if(chckbxIssueDate.isSelected())
					{
						Date today = Calendar.getInstance().getTime();
						issueDate =(today.getMonth()+1)+"/"+today.getDate()+"/"+(today.getYear()+1900);
					}
					else
					{
						Date selectedDate = datePickerIssueDate.getDate();
						issueDate =(selectedDate.getMonth()+1)+"/"+selectedDate.getDate()+"/"+(selectedDate.getYear()+1900);
					}
					
					Date d = datePickerCheckIn.getDate();		
					String meal = "";
					if(chckbxBreakfast.isSelected())
						meal += "B ";
					if(chckbxLunch.isSelected())
						meal += "L ";
					if(chckbxDinner.isSelected())
						meal += "D ";
					if(comboBoxType.getSelectedItem().equals("Hotel"))
					{
						query = "insert into Vouchers (VoucherID,BookingNumber,VoucherType,NumOfPax,NumOfRoom,NumOfNight,RoomSize,RoomType,Date,Vendor,Meal,Service,IssueDate) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						pst = connection.prepareStatement(query);
						pst.setString(1, "EXPV"+voucher);
						pst.setInt(2, Integer.parseInt(textFieldBookingNumber.getText()));
						pst.setString(3, comboBoxType.getSelectedItem().toString());
						pst.setInt(4, Integer.parseInt(spinnerPassenger.getValue().toString()));
						pst.setInt(5, Integer.parseInt(spinnerNumOfRoom.getValue().toString()));					
						pst.setInt(6, Integer.parseInt(spinnerNight.getValue().toString()));
						pst.setString(7, comboBoxRoomSize.getSelectedItem().toString());
						pst.setString(8, comboBoxRoomType.getSelectedItem().toString());
						pst.setString(9, ""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900));
						pst.setString(10, comboBoxVendors.getSelectedItem().toString());
						pst.setString(11, meal);
						pst.setString(12, textAreaService.getText());
						pst.setString(13, issueDate);
					}
					else
					{
						query = "insert into Vouchers (VoucherID,BookingNumber,VoucherType,NumOfPax,Date,Vendor,Meal,Service,IssueDate) values (?,?,?,?,?,?,?,?,?)";
						pst = connection.prepareStatement(query);
						pst.setString(1, "EXPV"+voucher);
						pst.setInt(2, Integer.parseInt(textFieldBookingNumber.getText()));
						pst.setString(3, comboBoxType.getSelectedItem().toString());
						pst.setInt(4, Integer.parseInt(spinnerPassenger.getValue().toString()));
						pst.setString(5, ""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900));
						pst.setString(6, comboBoxVendors.getSelectedItem().toString());
						pst.setString(7, meal);
						pst.setString(8, textAreaService.getText());
						pst.setString(9, issueDate);
					}
					comboBoxVendors.transferFocusBackward();
					JOptionPane.showMessageDialog(null, "Voucher Created.");
					pst.execute();										
					pst.close();				
				}catch(Exception e1){e1.printStackTrace();}
				
				
				// update voucher id
				try{				
					String query = "Update IDs set EXPID='"+1+"' , Voucher='"+voucher+"' where EXPID='"+1+"'  ";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.execute();									
					pst.close();
					}catch(Exception e1){
					e1.printStackTrace();
				}
				
				//clear();		
				comboBoxRoomType.setSelectedItem("");
				spinnerNight.setValue(1);
				spinnerNumOfRoom.setValue(1);
				datePickerCheckIn.setDate(null);
				textAreaService.setText("");
				chckbxDinner.setSelected(false);
				chckbxBreakfast.setSelected(false);
				chckbxLunch.setSelected(false);
				comboBoxRoomSize.setSelectedItem("");

				refreshTable();
			}
		});
		btnCreate.setBounds(10, 124, 90, 25);
		getContentPane().add(btnCreate);
		
		comboBoxRoomType = new JComboBox();
		comboBoxRoomType.setModel(new DefaultComboBoxModel(new String[] {"Deluxe Room", "Deluxe City View Room", "Deluxe River View Room", "Premium Room", "Superior Room"}));
		comboBoxRoomType.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxRoomType.setEditable(true);
		comboBoxRoomType.setBounds(622, 45, 298, 23);
		getContentPane().add(comboBoxRoomType);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clear();			
			}
		});
		btnClear.setBounds(110, 124, 90, 25);
		getContentPane().add(btnClear);
		
		btnUpdate = new JButton("Update");
		btnUpdate.setEnabled(false);
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!checkValid())return;
				
				int action = JOptionPane.showConfirmDialog(null, "Do you want to update "+voucherID+"?", "Update", JOptionPane.YES_NO_OPTION);
				btnDelete.setEnabled(false);
				btnUpdate.setEnabled(false);
				if(action==1)return;
				
					String issueDate = "";
					if(chckbxIssueDate.isSelected())
					{
						Date today = Calendar.getInstance().getTime();
						issueDate =(today.getMonth()+1)+"/"+today.getDate()+"/"+(today.getYear()+1900);
					}
					else
					{
						Date selectedDate = datePickerIssueDate.getDate();
						issueDate =(selectedDate.getMonth()+1)+"/"+selectedDate.getDate()+"/"+(selectedDate.getYear()+1900);
					}

					Date d = datePickerCheckIn.getDate();
					String meal = "";
					if(chckbxBreakfast.isSelected())
						meal += "B ";
					if(chckbxLunch.isSelected())
						meal += "L ";
					if(chckbxDinner.isSelected())
						meal += "D ";
					if(comboBoxType.getSelectedItem().equals("Hotel"))
					{
						try{
							String query = "Update Vouchers set VoucherID='"+voucherID+"' ,VoucherType='"+comboBoxType.getSelectedItem()+"',"
									+ " RoomType='"+comboBoxRoomType.getSelectedItem()+  "' ,NumOfPax='"+spinnerPassenger.getValue()+"' ,NumOfNight='"+spinnerNight.getValue()
									+"' ,RoomSize='"+comboBoxRoomSize.getSelectedItem()+"', NumOfRoom='"+spinnerNumOfRoom.getValue()
									+"' ,Date='"+""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900)+"' ,Vendor='"+comboBoxVendors.getSelectedItem()
									+"' ,Meal='"+meal+"' ,Service='"+textAreaService.getText()+"' ,IssueDate='"+issueDate+"'where VoucherID='"+voucherID+"'";
							PreparedStatement pst = connection.prepareStatement(query);
	
							pst.execute();
							
							JOptionPane.showMessageDialog(null, "Voucher Updated");
							
							pst.close();
							
						}catch(Exception e1){e1.printStackTrace();}
					}
					if(comboBoxType.getSelectedItem().equals("Land Service") || comboBoxType.getSelectedItem().equals("Air Ticket"))
					{
						try{
							String query = "Update Vouchers set VoucherID='"+voucherID+"' ,VoucherType='"+comboBoxType.getSelectedItem()+"',"
									+ " RoomType='"+""+"' ,NumOfPax='"+spinnerPassenger.getValue()+"' ,NumOfNight='"+""
									+"' ,Date='"+""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900)+"' ,Vendor='"+comboBoxVendors.getSelectedItem()
									+"' ,Meal='"+meal+"' ,Service='"+textAreaService.getText()+"' ,IssueDate='"+issueDate+"'where VoucherID='"+voucherID+"'";
							PreparedStatement pst = connection.prepareStatement(query);
	
							pst.execute();
							
							JOptionPane.showMessageDialog(null, "Voucher Updated");
							
							pst.close();
							
						}catch(Exception e1){e1.printStackTrace();}
					}
					comboBoxVendors.transferFocusBackward();
				refreshTable();
			}
		});
		btnUpdate.setBounds(210, 124, 90, 25);
		getContentPane().add(btnUpdate);
		
		btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int action = JOptionPane.showConfirmDialog(null, "Did you try updating voucher? \nDo you really want to delete this voucher?", "Delete", JOptionPane.YES_NO_OPTION);
				btnDelete.setEnabled(false);
				btnUpdate.setEnabled(false);
				if(action==0){
					try{
						
						String query = "delete from Vouchers where VoucherID='"+voucherID+"' ";
						PreparedStatement pst = connection.prepareStatement(query);	
						clear();
						pst.execute();
						pst.close();						
					}catch(Exception e1){
						e1.printStackTrace();
					}
					refreshTable();
				}
			}
		});
		btnDelete.setBounds(310, 124, 90, 25);
		getContentPane().add(btnDelete);
		
		chckbxBreakfast = new JCheckBox("Breakfast");
		chckbxBreakfast.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxBreakfast.setBounds(926, 44, 100, 23);
		getContentPane().add(chckbxBreakfast);
		
		chckbxLunch = new JCheckBox("Lunch");
		chckbxLunch.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxLunch.setBounds(1028, 44, 90, 23);
		getContentPane().add(chckbxLunch);
		
		chckbxDinner = new JCheckBox("Dinner");
		chckbxDinner.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxDinner.setBounds(1120, 44, 100, 23);
		getContentPane().add(chckbxDinner);
		
		spinnerNumOfRoom = new JSpinner();
		spinnerNumOfRoom.setValue(1);
		spinnerNumOfRoom.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(Integer.parseInt(spinnerNumOfRoom.getValue().toString()) < 1)
					spinnerNumOfRoom.setValue(1);
			}
		});
		spinnerNumOfRoom.setFont(new Font("Tahoma", Font.PLAIN, 12));
		spinnerNumOfRoom.setBounds(340, 45, 50, 23);
		getContentPane().add(spinnerNumOfRoom);
		
		comboBoxRoomSize = new JComboBox();
		comboBoxRoomSize.setEditable(true);
		comboBoxRoomSize.setModel(new DefaultComboBoxModel(new String[] {"Twin"}));
		comboBoxRoomSize.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxRoomSize.setBounds(411, 45, 200, 23);
		getContentPane().add(comboBoxRoomSize);
		
		datePickerIssueDate = new JXDatePicker();
		datePickerIssueDate.setVisible(false);	
		datePickerIssueDate.getEditor().setFont(new Font("Tahoma", Font.PLAIN, 11));
		datePickerIssueDate.setBounds(995, 115, 225, 20);
		getContentPane().add(datePickerIssueDate);
				
		lblIssueDate = new JLabel("Issue Date:");
		lblIssueDate.setBounds(1085, 115, 69, 17);
		getContentPane().add(lblIssueDate);
		lblIssueDate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		chckbxIssueDate = new JCheckBox("today");
		chckbxIssueDate.setBounds(1160, 112, 61, 25);
		getContentPane().add(chckbxIssueDate);
		chckbxIssueDate.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(chckbxIssueDate.isSelected())
					chckbxIssueDate.setSelected(false);
				else
					chckbxIssueDate.setSelected(true);
				chckbxIssueDate.setVisible(false);
				
				datePickerIssueDate.setVisible(true);
				lblIssueDate.setLocation(900, 115);
			}
		});
		
		chckbxIssueDate.setSelected(true);
		chckbxIssueDate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnClear2 = new JButton("X");
		btnClear2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				comboBoxVendors.setSelectedItem("");
			}
		});
		btnClear2.setBounds(1175, 9, 40, 25);
		getContentPane().add(btnClear2);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmCreateNewBooking = new JMenuItem("New Booking");
		mntmCreateNewBooking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				CreateNewBooking cnb = new CreateNewBooking();
				cnb.setVisible(true);
			}
		});
		
		JMenuItem mntmMainMenu = new JMenuItem("Main Menu");
		mntmMainMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				MainMenu s = new MainMenu();
				s.setVisible(true);
			}
		});
		mnFile.add(mntmMainMenu);
		mnFile.add(mntmCreateNewBooking);
		
		JMenuItem mntmSearch = new JMenuItem("Database");
		mntmSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				InformationCenter s = new InformationCenter();
				s.setVisible(true);
			}
		});
		mnFile.add(mntmSearch);
		
		JMenuItem mntmPrint = new JMenuItem("Print");
		mntmPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(textFieldBookingNumber.getText().equals("")) return;

				if(tableVoucher.getRowCount() < 1) return;
				
				int booking = Integer.parseInt(textFieldBookingNumber.getText());
				
				chooser = new JFileChooser(); 
				int response = chooser.showSaveDialog(Voucher.this);
				String directory = "";
				if(response == JFileChooser.APPROVE_OPTION)
					directory = chooser.getSelectedFile().toString();
				
				
				try {
					PrintVoucher pv = new PrintVoucher(booking, directory);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		
		mntmContactList = new JMenuItem("Contact List");
		mntmContactList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();				
				OperatingContactList ocl = new OperatingContactList();		
			}
		});
		mnFile.add(mntmContactList);
		mnFile.add(mntmPrint);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);

		
		loadVendors();
	}
	
	public void refreshTable()
	{
		try{
			String query = "select * from Vouchers where BookingNUmber="+textFieldBookingNumber.getText();
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			tableVoucher.setModel(DbUtils.resultSetToTableModel(rs));			
			
			pst.close();
			rs.close();
		}catch(Exception e1){e1.printStackTrace();}
		try{
			String query = "select * from Passengers where BookingNUmber="+textFieldBookingNumber.getText();
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			tablePassengers.setModel(DbUtils.resultSetToTableModel(rs));			
			
			pst.close();
			rs.close();
		}catch(Exception e1){e1.printStackTrace();}
	}
	
	public void clear()
	{
		comboBoxRoomType.setSelectedItem("");
		spinnerNight.setValue(1);
		spinnerNumOfRoom.setValue(1);
		datePickerCheckIn.setDate(null);
		comboBoxVendors.setSelectedItem("");
		textAreaService.setText("");
		chckbxDinner.setSelected(false);
		chckbxBreakfast.setSelected(false);
		chckbxLunch.setSelected(false);
		comboBoxRoomSize.setSelectedItem("");
		datePickerIssueDate.setVisible(false);
		datePickerIssueDate.setDate(null);
		chckbxIssueDate.setVisible(true);
		chckbxIssueDate.setSelected(true);
		lblIssueDate.setLocation(1085, 115);
	}
	
	public void loadVendors()
	{
		// load manifest comboBoxType
		String code = "", name = "", table = "";
		if(comboBoxType.getSelectedItem().equals("Hotel")){
			code = "HotelCode";
			name = "HotelName";
			table = "Hotels";
		}
		else{
			code = "LSCode";
			name = "LSName";
			table = "Vendors";
		}
		
		vendorList = new ArrayList<>();
		vendorList.add("");
		try{				
			
			String query = "select "+code+","+name+" from "+table;
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();

			while(rs.next())
			{
				String c = rs.getString(code);
				String n = rs.getString(name);
				vendorList.add(c+ " (" + n+")");
			}
			
			pst.close();
			rs.close();
		}catch(Exception e1){
			//e1.printStackTrace();
		}
		

	}
	
	public boolean checkValid()
	{		
		if(comboBoxType.getSelectedItem().toString().equals("Hotel"))
		{
			if(Integer.parseInt(spinnerNight.getValue().toString()) <= 0)
			{
				JOptionPane.showMessageDialog(null, "No one sleeps at hotel for "+spinnerNight.getValue()+" night :)", "Exception", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if(Integer.parseInt(spinnerNumOfRoom.getValue().toString()) <= 0)
			{
				JOptionPane.showMessageDialog(null, spinnerNumOfRoom.getValue()+" Room?", "Exception", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if(comboBoxRoomType.getSelectedItem() == null)
			{
				JOptionPane.showMessageDialog(null, "Voucher Info Incomplete!", "Exception", JOptionPane.WARNING_MESSAGE);
				return false;
			}	
		}

		if(Integer.parseInt(spinnerPassenger.getValue().toString()) == 0 ||
					   datePickerCheckIn.getDate() == null ||
					   comboBoxVendors.getSelectedItem() == null ||
					   comboBoxVendors.getSelectedItem().equals(""))
		{
			JOptionPane.showMessageDialog(null, "Voucher Info Incomplete!", "Exception", JOptionPane.WARNING_MESSAGE);
			comboBoxVendors.transferFocusBackward();
			return false;
		}
		
		
		if(chckbxIssueDate.isSelected() == false && datePickerIssueDate.getDate() == null)
		{
			JOptionPane.showMessageDialog(null, "You Must Select an Issue Date", "Exception", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	public List<Object> formatPrintContent(List<Object> list)
	{
		if(textFieldBookingNumber.getText().equals("")) return list;
		// 1. booking # 
		// 2. issue date
		// 3. passenger names
		// 4. services
		// 5. service provider info 
		
		
		// 1
		list = new ArrayList<>();
		list.add(textFieldBookingNumber.getText()); 
		
		// 2
		Calendar cal = new GregorianCalendar();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String month = new SimpleDateFormat("MMM").format(cal.getTime());
		int year = cal.get(Calendar.YEAR);
		list.add(month+" "+day+", "+year); 
		
		// 3
		ArrayList<String> names = new ArrayList<>();;
		try{							
			String query = "select Firstname,Lastname from PaxInfos where BookingNumber='"+textFieldBookingNumber.getText()+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next())			
				names.add(rs.getString("Lastname")+" "+rs.getString("Firstname"));
			
			pst.close();
			rs.close();
		}catch(Exception e1){e1.printStackTrace();}
		list.add(names); 
		
		// 4	
		List<Object> services = new ArrayList<>();
		try{							
			String query = "select * from B"+textFieldBookingNumber.getText()+" where Type='Hotel'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			ArrayList<ArrayList<String>> hotelServices = new ArrayList<>();
			ArrayList<String> row = new ArrayList<>();
			while(rs.next())
			{
				row.add(rs.getString("NumOfPax"));
				row.add(rs.getString("NumOfNight"));
				row.add(rs.getString("RoomType"));
				row.add(rs.getString("Date"));
				row.add(rs.getString("Manifest"));
				row.add(rs.getString("Meal"));
				row.add(rs.getString("Description"));	
				hotelServices.add(row);
				row = new ArrayList<>();
			}
			services.add(hotelServices);
			
			
			query = "select * from B"+textFieldBookingNumber.getText()+" where Type='Land Service'";
			pst = connection.prepareStatement(query);
			rs = pst.executeQuery();
			
			ArrayList<ArrayList<String>> landServices = new ArrayList<>();
			row = new ArrayList<>();
			while(rs.next())
			{
				row.add(rs.getString("NumOfPax"));
				row.add(rs.getString("Date"));
				row.add(rs.getString("Manifest"));
				row.add(rs.getString("Meal"));
				row.add(rs.getString("Description"));
				landServices.add(row);
				row = new ArrayList<>();			
			}
			services.add(landServices);
			
			pst.close();
			rs.close();
		}catch(Exception e1){e1.printStackTrace();}
		list.add(services);
		
		
		// service provider info	
		//[hotel, hotel, hotel, landService, landService]
				
		return list;	
	}
	
	public int setPassengerNumber()
	{
		int paxNum = 0;
		try{							
			String query = "select * from Passengers where BookingNumber="+textFieldBookingNumber.getText();
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
					
			tablePassengers.setModel(DbUtils.resultSetToTableModel(rs));	
			// grab # of pax
			rs = pst.executeQuery();
			int count = 0;
			while(rs.next())
				count++;
			spinnerPassenger.setValue(count);
			paxNum = count;
			pst.close();
			rs.close();
		}catch(Exception e1){
			DefaultTableModel model = (DefaultTableModel)tablePassengers.getModel();
			model.setRowCount(0);
			//e1.printStackTrace();
		}		
		return paxNum;
	}
}















