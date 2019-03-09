/*
 * This file is part of MineSweeper
 * Copyright (C) 2015-2019 Richard R. Zheng
 *
 * https://github.com/rzheng95/MineSweeper
 * 
 * All Right Reserved.
 */

package com.rzheng.explorient;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;

import net.proteanit.sql.DbUtils;

import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JMenuItem;

public class OperatingContactList {

	private JFrame frame;
	private JTextField textFieldBookingNumber;
	private JTextField textFieldName;
	private Connection connection = null;
	private JLabel lblNumOfPax;
	private XWPFDocument doc;
	private JRadioButton rdbtnHotel, rdbtnLand;
	private JTextField textFieldCity;
	private DefaultListModel listModelCity, listModelTemp, listModelVendor;
	private JList listCity, listTemp, listVendor;
	private boolean isHotel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OperatingContactList window = new OperatingContactList();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public OperatingContactList()  {
		try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception e){}
		initialize();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		// connecting database
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
		 
		 
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		
		frame.setTitle("Contact List Generator");
		Image icon = new ImageIcon(this.getClass().getResource("/Explorient Icon.jpg")).getImage();
		frame.setIconImage(icon);
		frame.setBounds(100, 100, 541, 453);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		listModelCity = new DefaultListModel();
		listModelTemp = new DefaultListModel();
		listModelVendor = new DefaultListModel();
		isHotel = true;
		
		JLabel lblBooking = new JLabel("Booking #:");
		lblBooking.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblBooking.setBounds(10, 10, 80, 20);
		frame.getContentPane().add(lblBooking);
		
		textFieldBookingNumber = new JTextField();
		textFieldBookingNumber.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldBookingNumber.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {

				try{							
					String query = "select * from Passengers where BookingNumber="+textFieldBookingNumber.getText();
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();							
	
					// grab # of pax
					rs = pst.executeQuery();
					int count = 0;
					while(rs.next())
						count++;

					lblNumOfPax.setText("Number of passengers: "+count);
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
					frame.getToolkit().beep();
					event.consume();// unable to press that key
				}
			}
		});
		textFieldBookingNumber.setBounds(100, 10, 90, 23);
		frame.getContentPane().add(textFieldBookingNumber);
		textFieldBookingNumber.setColumns(10);
		
	    rdbtnHotel = new JRadioButton("Hotel");
		rdbtnHotel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(rdbtnHotel.isSelected())
					rdbtnLand.setSelected(false);
			}
		});
		rdbtnHotel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rdbtnHotel.setBounds(10, 40, 70, 23);
		frame.getContentPane().add(rdbtnHotel);
		
		rdbtnLand = new JRadioButton("Land");
		rdbtnLand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnLand.isSelected())
					rdbtnHotel.setSelected(false);
			}
		});
		rdbtnLand.setFont(new Font("Tahoma", Font.PLAIN, 12));
		rdbtnLand.setBounds(90, 40, 70, 23);
		frame.getContentPane().add(rdbtnLand);
		
		JLabel lblFor = new JLabel("For");
		lblFor.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblFor.setBounds(170, 41, 40, 20);
		frame.getContentPane().add(lblFor);
		
		textFieldName = new JTextField();
		textFieldName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldName.setBounds(210, 40, 300, 23);
		frame.getContentPane().add(textFieldName);
		textFieldName.setColumns(10);
		
		final JButton btnPrint = new JButton("Print");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(textFieldBookingNumber.getText().trim().equals("")) return;
				if(listModelCity.getSize() != listModelVendor.getSize())
				{
					JOptionPane.showMessageDialog(null, "Number of cities does not match the number of vendors!", "Exception", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				boolean fileExist = new File("Operating List Background.docx").exists();
				if(fileExist)
				{
					InputStream is;
					try {
						is = new FileInputStream("Operating List Background.docx");
						try {
							doc = new XWPFDocument(is);
						} catch (IOException e) {e.printStackTrace();}
						
					} catch (FileNotFoundException e) {e.printStackTrace();}
					
				}
				else
					doc = new XWPFDocument();
							
			// File chooser
				JFileChooser chooser = new JFileChooser(); 
				int response = chooser.showSaveDialog(frame);
				String directory = "";
				if(response == JFileChooser.APPROVE_OPTION)
					directory = chooser.getSelectedFile().toString();
				
				createList(directory);
			}
		});
		btnPrint.setBounds(300, 345, 210, 38);
		frame.getContentPane().add(btnPrint);
		
		lblNumOfPax = new JLabel("");
		lblNumOfPax.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblNumOfPax.setBounds(200, 10, 232, 20);
		frame.getContentPane().add(lblNumOfPax);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 130, 90, 180);
		frame.getContentPane().add(scrollPane_2);
		
		listCity = new JList(listModelCity);
		scrollPane_2.setViewportView(listCity);
		
		textFieldCity = new JTextField();
		textFieldCity.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				char c = e.getKeyChar();
				if(c==KeyEvent.VK_ENTER){					
					addCity();				
				}
				
			}
		});
		textFieldCity.setBounds(10, 322, 90, 23);
		frame.getContentPane().add(textFieldCity);
		textFieldCity.setColumns(10);
		
		JButton btnAdd = new JButton("+");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCity();
			}
		});
		btnAdd.setBounds(10, 353, 40, 23);
		frame.getContentPane().add(btnAdd);
		
		JButton btnMinus = new JButton("-");
		btnMinus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = listCity.getSelectedIndex();
				if(index >= 0)
				listModelCity.removeElementAt(index);
			}
		});
		btnMinus.setBounds(59, 353, 40, 23);
		frame.getContentPane().add(btnMinus);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(130, 130, 90, 180);
		frame.getContentPane().add(scrollPane_1);
		
		listVendor = new JList(listModelVendor);
		scrollPane_1.setViewportView(listVendor);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(300, 130, 210, 180);
		frame.getContentPane().add(scrollPane);
		
		listTemp = new JList(listModelTemp);
		scrollPane.setViewportView(listTemp);
		
		JButton btnAdd_1 = new JButton("<<<");
		btnAdd_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = listTemp.getSelectedIndex();
				if(index < 0) return;
				String[] temp = listModelTemp.get(index).toString().split(" ");
				
				listModelVendor.addElement(temp[0]);
			}
		});
		btnAdd_1.setBounds(230, 150, 60, 23);
		frame.getContentPane().add(btnAdd_1);
		
		JButton btnRemove = new JButton(">>>");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = listVendor.getSelectedIndex();	
				if(index < 0) return;
				listModelVendor.removeElementAt(index);
			}
		});
		btnRemove.setBounds(230, 200, 60, 23);
		frame.getContentPane().add(btnRemove);
		
		JButton btnGetVendors = new JButton("Get Vendors");
		btnGetVendors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!textFieldBookingNumber.equals("") && (rdbtnHotel.isSelected() || rdbtnLand.isSelected()) )
				{
					if(rdbtnHotel.isSelected()) 
						isHotel = true;
					else
						isHotel = false;
					
					String type = "Hotel";
					if(rdbtnLand.isSelected()) type = "Land Service";
					try{							
						String query = "select Vendor FROM Vouchers where BookingNumber='"+textFieldBookingNumber.getText()+"' AND VoucherType='"+type+"'";
						PreparedStatement pst = connection.prepareStatement(query);
						ResultSet rs = pst.executeQuery();
						
						ArrayList<String> temp = new ArrayList<>();
						listModelTemp.clear();
						listModelVendor.clear();
						while(rs.next())			
							temp.add(rs.getString("Vendor"));
						
						ArrayList<String> vendors = new ArrayList<>();
		
						// remove duplicates
						for(int i=0; i<temp.size(); i++)
						{
							boolean exist = false;
							for(int j=0; j<vendors.size(); j++)
							{
								if(temp.get(i).equals(vendors.get(j)))
									exist = true;								
							}
							if(!exist)
								vendors.add(temp.get(i));
						}
						
						for(int i=0; i<vendors.size(); i++)
							listModelTemp.addElement(vendors.get(i));
						
						
						pst.close();
						rs.close();
					}catch(Exception e1){e1.printStackTrace();}

				}
			}
		});
		btnGetVendors.setBounds(10, 70, 107, 23);
		frame.getContentPane().add(btnGetVendors);
		
		JLabel lblCity = new JLabel("City:");
		lblCity.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCity.setBounds(12, 110, 46, 14);
		frame.getContentPane().add(lblCity);
		
		JLabel lblVendor = new JLabel("Vendor:");
		lblVendor.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblVendor.setBounds(132, 110, 46, 14);
		frame.getContentPane().add(lblVendor);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmMainMenu = new JMenuItem("Main Menu");
		mntmMainMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				MainMenu s = new MainMenu();
				s.setVisible(true);
			}
		});
		mnFile.add(mntmMainMenu);
		
		JMenuItem mntmDatabase = new JMenuItem("Database");
		mntmDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				InformationCenter s = new InformationCenter();
				s.setVisible(true);
			}
		});
		
		JMenuItem mntmNewBooking = new JMenuItem("New Booking");
		mntmNewBooking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				CreateNewBooking cnb = new CreateNewBooking();
				cnb.setVisible(true);
			}
		});
		mnFile.add(mntmNewBooking);
		mnFile.add(mntmDatabase);
		
		JMenuItem mntmVoucher = new JMenuItem("Voucher");
		mntmVoucher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				Voucher v = new Voucher("");
				v.setVisible(true);
			}
		});
		mnFile.add(mntmVoucher);
		
		JMenuItem mntmPrint = new JMenuItem("Print");
		mntmPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnPrint.doClick();
			}
		});
		mnFile.add(mntmPrint);
	}
	
	public void addCity()
	{
		String text = textFieldCity.getText().toLowerCase().trim();
		if(text.length() <= 0) return;
		if(text.contains(" "))
		{
			String[] arr = text.split(" ");
			text = "";
			for(int i=0; i<arr.length; i++)
			{
				String space = " ";
				if(i==arr.length-1)
					space = "";
				text += capitalFirstLetter(arr[i])+space;
			}
		}
		else
			text = capitalFirstLetter(text);
		
		
		if(!text.equals(""))
			listModelCity.addElement(text);
		textFieldCity.setText("");
	}
	
	public String capitalFirstLetter(String text)
	{
		if(text.length()>1)
			return text.substring(0,1).toUpperCase() + text.substring(1);
		else
			return text.toUpperCase();
	}
	
	private void setRun(XWPFRun run , String fontFamily , int fontSize , String text , boolean bold , boolean addBreak) 
	{
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        if(text.contains(";"))
        {
        	String[] lines = text.split(";");
        	run.setText(lines[0], 0); // set first line into XWPFRun
            for(int i=1;i<lines.length;i++){
                // add break and insert new text
                run.addBreak();
                run.setText(lines[i]);
            }    		
        }
        else       	
        	run.setText(text);
        run.setBold(bold);
        if (addBreak) run.addBreak();
    }

	public String fetchDataByCode(String code)
	{
		String returnString = "";
		String table = "Hotels";
		String name = "HotelName";
		String LSorHotelCode = "HotelCode";
		if(!isHotel) 
		{
			table = "Vendors";
			name = "LSName";
			LSorHotelCode = "LSCode";
		}
		
		try{

    		String query = "select "+name+",Street,City,State,Country,LocalPhone from "+table+" where "+LSorHotelCode+"='"+code.toUpperCase()+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();	
			
			while(rs.next())
			{
				returnString += rs.getString(name)+";";
				returnString += rs.getString("Street")+";";
				
				String city = rs.getString("City");
				String state = rs.getString("State");
				if(!city.trim().equals(""))
					returnString += city;
				if(!state.trim().equals(""))
				{
					returnString += ", "+state+";";
				}
				else
					returnString += ";";
				
				returnString += rs.getString("Country")+";";
				returnString += "Tel: "+rs.getString("LocalPhone")+";";
			}
					
			pst.close();
			rs.close();
    	}
    	catch(Exception e1){e1.printStackTrace();}
		
		return returnString;
	}
	
	
	public void createList(String exportDirectory)
	{
		 XWPFParagraph p1 = doc.createParagraph();
		 p1.setAlignment(ParagraphAlignment.CENTER);
		 XWPFRun r1 = p1.createRun();
		 r1.setBold(true);
		 r1.setFontFamily("Arial Narrow");
		 r1.setFontSize(12);
		 String title = "Hotel Listing for ";
		 if(!isHotel) title = "Land Service Operator List for ";
		 
		 r1.setText(title+textFieldName.getText());
		 
		 
	// Cities
		 ArrayList<String> cities = new ArrayList<>();
		 for(int i=0; i<listModelCity.getSize(); i++)
			 cities.add((String) listModelCity.getElementAt(i));
		 
	// Vendors
		 ArrayList<String> vendors = new ArrayList<>();
		 for(int i=0; i<listModelVendor.getSize(); i++)
			 vendors.add((String) listModelVendor.getElementAt(i));
		 
	// Initialize Table
		 XWPFTable table = doc.createTable();
		 XWPFTableRow tableRowHeading= table.getRow(0);
		 
	// http://stackoverflow.com/questions/36545834/how-to-decrease-default-height-of-a-table-row-in-word-using-apache-poi-in-java
		 tableRowHeading.setHeight(288);
		 tableRowHeading.getCtRow().getTrPr().getTrHeightArray(0).setHRule(STHeightRule.EXACT);
		 
		 String headingText = "Hotel";
		 if(!isHotel) headingText = "Local Operator Contact List";
		 
		 XWPFParagraph paragraph = tableRowHeading.getCell(0).addParagraph();
		 tableRowHeading.getCell(0).setColor("C0C0C0");
		 tableRowHeading.getCell(0).removeParagraph(0);
		 setRun(paragraph.createRun(), "Arial Narrow", 11, "City", true, false);

		 
		 paragraph = tableRowHeading.addNewTableCell().addParagraph();
		 tableRowHeading.getCell(1).setColor("C0C0C0");
		 tableRowHeading.getCell(1).removeParagraph(0);
		 setRun(paragraph.createRun(), "Arial Narrow", 11, headingText, true, false);
		 
		 table.getRow(0).getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(4000));
		 table.getRow(0).getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(8000));
		 
		 
		 for(int i=0; i<cities.size(); i++)
		 {
			 XWPFTableRow tableRow = table.createRow();	
			 paragraph = tableRow.getCell(0).addParagraph();
			 tableRow.getCell(0).removeParagraph(0);
			 setRun(paragraph.createRun(), "Arial Narrow", 11, cities.get(i), false, false);

			 
			 
			 paragraph = tableRow.getCell(1).addParagraph();
			 tableRow.getCell(1).removeParagraph(0);
			 setRun(paragraph.createRun(), "Arial Narrow", 11, fetchDataByCode(vendors.get(i)), false, false);

		 }
		 	 
		 
	// printing
		 if(!exportDirectory.contains(".docx"))
			 exportDirectory += ".docx";
		 
		 try{
			 FileOutputStream out = new FileOutputStream(exportDirectory);
			 doc.write(out);
			 
			 doc.close();
		     out.close();
		 }catch(IOException e){
			 JOptionPane.showMessageDialog(null, "This action can't be completed because the file is open in Word.", "Exception", JOptionPane.ERROR_MESSAGE);
		 }
	}
}
