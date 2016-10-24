package Explorient;


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
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

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
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.SwingConstants;
import javax.swing.JTabbedPane;
import javax.swing.JSeparator;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JCheckBox;


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
	private JButton btnCreate, btnUpdate, btnDelete;
	private List<Object> printContent;
	private JCheckBox chckbxSnack, chckbxDinner, chckbxBreakfast, chckbxLunch;
	private JFileChooser chooser;

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
		// establish connection to database
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
		setBounds(100, 100, 1246, 662);
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
					
					btnCreate.setEnabled(true);
					btnUpdate.setEnabled(true);
					pst.close();
					rs.close();	
				}catch(Exception e1){
					DefaultTableModel model = (DefaultTableModel)tableVoucher.getModel();
					model.setRowCount(0);
					btnCreate.setEnabled(false);
					btnUpdate.setEnabled(false);
					//e1.printStackTrace();
				}
				
				try{							
					String query = "select * from PaxInfos where BookingNumber="+textFieldBookingNumber.getText();
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
							
					tablePassengers.setModel(DbUtils.resultSetToTableModel(rs));	
					// grab # of pax
					rs = pst.executeQuery();
					int count = 0;
					while(rs.next())
						count++;
					spinnerPassenger.setValue(count);

					pst.close();
					rs.close();
				}catch(Exception e1){
					DefaultTableModel model = (DefaultTableModel)tablePassengers.getModel();
					model.setRowCount(0);
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
					
					clear();
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
					String meal = rs.getString("Meal");
					
					for(int i=0; i<meal.length(); i++)
					{
						if(meal.charAt(i) == 'B')
							chckbxBreakfast.setSelected(true);
						if(meal.charAt(i) == 'L')
							chckbxLunch.setSelected(true);
						if(meal.charAt(i) == 'D')
							chckbxDinner.setSelected(true);
						if(meal.charAt(i) == 'C')
							chckbxSnack.setSelected(true);
					}
					
					pst.close();
					rs.close();
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				btnDelete.setEnabled(true);
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
					loadManifest();
					if(e.getItem().equals("Land Service"))
					{
						comboBoxRoomType.setSelectedItem("");
						comboBoxRoomType.setEditable(false);
						comboBoxRoomType.setEnabled(false);	
						spinnerNight.setValue(0);
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
		comboBoxType.setModel(new DefaultComboBoxModel(new String[] {"Hotel", "Land Service", "Air Ticket"}));
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
		lblDescriptions.setBounds(10, 370, 80, 20);
		getContentPane().add(lblDescriptions);
		
		textAreaDescriptions = new JTextArea();
		textAreaDescriptions.setFont(new Font("Tahoma", Font.PLAIN, 12));
		Border border = BorderFactory.createLineBorder(Color.black);
		textAreaDescriptions.setBorder(border);
		textAreaDescriptions.setLineWrap(true);
		textAreaDescriptions.setWrapStyleWord(true);
		textAreaDescriptions.setBounds(10, 390, 190, 83);
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
					Date d = datePickerCheckIn.getDate();		
					String meal = "";
					if(chckbxBreakfast.isSelected())
						meal += "B ";
					if(chckbxLunch.isSelected())
						meal += "L ";
					if(chckbxDinner.isSelected())
						meal += "D ";
					if(chckbxSnack.isSelected())
						meal += "S ";
					if(comboBoxType.getSelectedItem().equals("Hotel"))
					{
						query = "insert into "+"B"+textFieldBookingNumber.getText()+" (VoucherID,Type,NumOfPax,NumOfNight,RoomType,Date,Manifest,Meal,Description) values (?,?,?,?,?,?,?,?,?)";
						pst = connection.prepareStatement(query);
						pst.setString(1, "EXPV"+voucher);
						pst.setString(2, comboBoxType.getSelectedItem().toString());
						pst.setString(3, spinnerPassenger.getValue().toString());
						pst.setString(4, spinnerNight.getValue().toString());
						pst.setString(5, comboBoxRoomType.getSelectedItem().toString());
						pst.setString(6, ""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900));
						pst.setString(7, comboBoxManifest.getSelectedItem().toString());
						pst.setString(8, meal);
						pst.setString(9, textAreaDescriptions.getText());
					}
					else
					{
						query = "insert into "+"B"+textFieldBookingNumber.getText()+" (VoucherID,Type,NumOfPax,Date,Manifest,Meal,Description) values (?,?,?,?,?,?,?)";
						pst = connection.prepareStatement(query);
						pst.setString(1, "EXPV"+voucher);
						pst.setString(2, comboBoxType.getSelectedItem().toString());
						pst.setString(3, spinnerPassenger.getValue().toString());
						pst.setString(4, ""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900));
						pst.setString(5, comboBoxManifest.getSelectedItem().toString());
						pst.setString(6, meal);
						pst.setString(7, textAreaDescriptions.getText());
					}
					
					JOptionPane.showMessageDialog(null, "Voucher Created.");
					pst.execute();										
					pst.close();				
				}catch(Exception e1){e1.printStackTrace();}
				
				
				// update voucher id
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
		
		btnUpdate = new JButton("Update");
		btnUpdate.setEnabled(false);
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!checkValid())return;
				
				int action = JOptionPane.showConfirmDialog(null, "Do you want to update "+voucherID+"?", "Update", JOptionPane.YES_NO_OPTION);
				if(action==1)return;
					Date d = datePickerCheckIn.getDate();
					String meal = "";
					if(chckbxBreakfast.isSelected())
						meal += "B ";
					if(chckbxLunch.isSelected())
						meal += "L ";
					if(chckbxDinner.isSelected())
						meal += "D ";
					if(chckbxSnack.isSelected())
						meal += "S ";
					if(comboBoxType.getSelectedItem().equals("Hotel"))
					{
						try{
							String query = "Update "+"B"+textFieldBookingNumber.getText()+" set VoucherID='"+voucherID+"' ,Type='"+comboBoxType.getSelectedItem()+"',"
									+ " RoomType='"+comboBoxRoomType.getSelectedItem()+"' ,NumOfPax='"+spinnerPassenger.getValue()+"' ,NumOfNight='"+spinnerNight.getValue()
									+"' ,Date='"+""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900)+"' ,Manifest='"+comboBoxManifest.getSelectedItem()
									+"' ,Meal='"+meal+"' ,Description='"+textAreaDescriptions.getText()+"' where VoucherID='"+voucherID+"'";
							PreparedStatement pst = connection.prepareStatement(query);
	
							pst.execute();
							
							JOptionPane.showMessageDialog(null, "Data Updated");
							
							pst.close();
							
						}catch(Exception e1){e1.printStackTrace();}
					}
					if(comboBoxType.getSelectedItem().equals("Land Service") || comboBoxType.getSelectedItem().equals("Air Ticket"))
					{
						try{
							String query = "Update "+"B"+textFieldBookingNumber.getText()+" set VoucherID='"+voucherID+"' ,Type='"+comboBoxType.getSelectedItem()+"',"
									+ " RoomType='"+""+"' ,NumOfPax='"+spinnerPassenger.getValue()+"' ,NumOfNight='"+""
									+"' ,Date='"+""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900)+"' ,Manifest='"+comboBoxManifest.getSelectedItem()
									+"' ,Meal='"+meal+"' ,Description='"+textAreaDescriptions.getText()+"' where VoucherID='"+voucherID+"'";
							PreparedStatement pst = connection.prepareStatement(query);
	
							pst.execute();
							
							JOptionPane.showMessageDialog(null, "Voucher Updated");
							
							pst.close();
							
						}catch(Exception e1){e1.printStackTrace();}
					}
				refreshTable("B"+textFieldBookingNumber.getText(), "Voucher");
			}
		});
		btnUpdate.setBounds(10, 525, 90, 25);
		getContentPane().add(btnUpdate);
		
		btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
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
				btnDelete.setEnabled(false);
			}
		});
		btnDelete.setBounds(111, 525, 90, 25);
		getContentPane().add(btnDelete);
		
		chckbxBreakfast = new JCheckBox("Breakfast");
		chckbxBreakfast.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxBreakfast.setBounds(10, 290, 97, 23);
		getContentPane().add(chckbxBreakfast);
		
		chckbxLunch = new JCheckBox("Lunch");
		chckbxLunch.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxLunch.setBounds(10, 330, 97, 23);
		getContentPane().add(chckbxLunch);
		
		chckbxDinner = new JCheckBox("Dinner");
		chckbxDinner.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxDinner.setBounds(110, 290, 97, 23);
		getContentPane().add(chckbxDinner);
		
		chckbxSnack = new JCheckBox("Snack");
		chckbxSnack.setFont(new Font("Tahoma", Font.PLAIN, 14));
		chckbxSnack.setBounds(110, 330, 97, 23);
		getContentPane().add(chckbxSnack);
		
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
		
		JMenuItem mntmSearch = new JMenuItem("Info Center");
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

				int booking = Integer.parseInt(textFieldBookingNumber.getText());
				
				chooser = new JFileChooser(); 
				
				
			    chooser.setCurrentDirectory(new java.io.File("."));
			    //chooser.setDialogTitle(choosertitle);
			    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    
			    String directory = "";
			    // disable the "All files" option.
			    chooser.setAcceptAllFileFilterUsed(false);			     
			    if (chooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) 
			    	directory = chooser.getSelectedFile().toString()+"\\";
	
				try {
					PrintVoucher pv = new PrintVoucher(booking, directory);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		mnFile.add(mntmPrint);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);

		
		loadManifest();
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
		chckbxSnack.setSelected(false);
		chckbxDinner.setSelected(false);
		chckbxBreakfast.setSelected(false);
		chckbxLunch.setSelected(false);
	}
	
	public void loadManifest()
	{
		// load manifest comboBoxType
		String column = "", table = "";
		if(comboBoxType.getSelectedItem().equals("Hotel")){
			column = "HotelCode";
			table = "Hotels";
		}
		if(comboBoxType.getSelectedItem().equals("Land Service")){
			column = "LSCode";
			table = "LandServices";
		}
		try{				
			
			String query = "select "+column+" from "+table;
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();

			int count = 0;
			
			while(rs.next())
				count++;
			String[] arr = new String[count+1];
			rs = pst.executeQuery();
			count = 1;
			arr[0] = "";
			while(rs.next()){
				arr[count] = rs.getString(column);
				count++;
			}
			comboBoxManifest.setModel(new JComboBox(arr).getModel());
			
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
			if(comboBoxRoomType.getSelectedItem() == null)
			{
				JOptionPane.showMessageDialog(null, "Voucher Info Incomplete!", "Exception", JOptionPane.WARNING_MESSAGE);
				return false;
			}
				
		}

		if(Integer.parseInt(spinnerPassenger.getValue().toString()) == 0 ||
					   datePickerCheckIn.getDate() == null ||
					   comboBoxManifest.getSelectedItem() == null ||
					   comboBoxManifest.getSelectedItem().equals(""))
		{
			JOptionPane.showMessageDialog(null, "Voucher Info Incomplete!", "Exception", JOptionPane.WARNING_MESSAGE);
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
		
		
		System.out.println(list);
		
		
		return list;	
	}
}















