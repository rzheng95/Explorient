package Explorient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.proteanit.sql.DbUtils;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.awt.event.ItemEvent;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Search extends JFrame {

	private JPanel contentPane;
	private JTable tableSearchResult;
	private JTextField textFieldSearch;
	private JTextField textFieldCode;
	private JLabel lblHotelName;
	private JLabel lblContact;
	private JLabel lblLocalPhone;
	private JLabel lblFax;
	private JLabel lblStreet;
	private JTextField textFieldName;
	private JTextField textFieldContact;
	private JTextField textFieldLocalPhone;
	private JTextField textFieldFax;
	private JTextField textFieldStreet;
	private JLabel lblCity;
	private JLabel lblStateProvince;
	private JLabel lblCountry;
	private JLabel lblZipcode;
	private JTextField textFieldCity;
	private JTextField textFieldStateProvince;
	private JTextField textFieldCountry;
	private JTextField textFieldZipcode;
	private Connection connection = null;
	private JComboBox comboBoxColumn, comboBoxTable;
	private String id;
	private JLabel lblHotelCode;
	private JLabel lblWarningMessage;
	private int hlaID; // Hotel LandService Agent ID

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Search frame = new Search();
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
	public Search() {
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
		setBounds(100, 100, 935, 763);
		setTitle("Information Center");
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 40, 900, 300);
		contentPane.add(scrollPane);
		
		tableSearchResult = new JTable();
		tableSearchResult.setFont(new Font("Tahoma", Font.BOLD, 12));
		tableSearchResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try{
					int row = tableSearchResult.getSelectedRow();
					String table = removeSpaces(comboBoxTable.getSelectedItem().toString());
					id = (tableSearchResult.getModel().getValueAt(row, 0)).toString();
					String column = null;
					if(table.equals("Hotels"))
						column = "HotelID";
					else if(table.equals("LandServices"))
						column = "LSID";
					else
						column = "AgentID";
									
					String query = "select * from "+table+" where "+column+"='"+id+"'";
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();				
					
					if(table.equals("Hotels")){
						textFieldCode.setText(rs.getString("HotelCode"));
						textFieldName.setText(rs.getString("HotelName"));
						textFieldContact.setText(rs.getString("Contact"));
						textFieldLocalPhone.setText(rs.getString("LocalPhone"));
					}
					else if(table.equals("LandServices")){
						textFieldCode.setText(rs.getString("LSCode"));
						textFieldName.setText(rs.getString("LSName"));
						textFieldContact.setText(rs.getString("Contact"));
						textFieldLocalPhone.setText(rs.getString("LocalPhone"));
					}
					else{
						textFieldCode.setText(rs.getString("AgentCode"));
						textFieldName.setText(rs.getString("AgentName"));
						textFieldContact.setText(rs.getString("Attention"));
						textFieldLocalPhone.setText(rs.getString("Telephone"));
					}


					textFieldFax.setText(rs.getString("Fax"));
					textFieldStreet.setText(rs.getString("Street"));
					textFieldCity.setText(rs.getString("City"));
					textFieldStateProvince.setText(rs.getString("State"));
					textFieldCountry.setText(rs.getString("Country"));
					textFieldZipcode.setText(rs.getString("Zipcode"));
		
					pst.close();		
					rs.close();
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
		});
		scrollPane.setViewportView(tableSearchResult);
		
		comboBoxTable = new JComboBox();
		comboBoxTable.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == 1)
				{
					if(e.getItem().equals("Hotels"))
					{
						refreshTable("Hotels");
						comboBoxColumn.setModel(new DefaultComboBoxModel(new String[] {"Hotel Code", "Hotel Name", "Contact", "Local Phone", "Fax", "Street", "City", "State/Province", "Country", "Zip Code"}));
						lblHotelCode.setText("Hotel Code:");
						lblHotelName.setText("Hotel Name:");
						lblContact.setText("Contact:");
						lblLocalPhone.setText("Local Phone:");
						clear();
					}
					else if(e.getItem().equals("Land Services"))
					{
						refreshTable("LandServices");
						comboBoxColumn.setModel(new DefaultComboBoxModel(new String[] {"LS Code", "LS Name", "Contact", "Local Phone", "Fax", "Street", "City", "State/Province", "Country", "Zip Code"}));
						lblHotelCode.setText("LS Code:");
						lblHotelName.setText("LS Name:");
						lblContact.setText("Contact:");
						lblLocalPhone.setText("Local Phone:");
						clear();
					}
					else if(e.getItem().equals("Agents"))
					{
						refreshTable("Agents");
						comboBoxColumn.setModel(new DefaultComboBoxModel(new String[] {"Agent Code", "Agent Name", "Attention", "Street", "City", "State", "Country", "Zip Code", "Telephone", "Fax"}));
						lblHotelCode.setText("Agent Code:");
						lblHotelName.setText("Agent Name:");
						lblContact.setText("Attention:");
						lblLocalPhone.setText("Telephone:");
						clear();
					}
				}
			}
		});
		comboBoxTable.setModel(new DefaultComboBoxModel(new String[] {"Hotels", "Land Services", "Agents"}));
		comboBoxTable.setBounds(10, 10, 100, 20);
		contentPane.add(comboBoxTable);
		
		textFieldSearch = new JTextField();
		textFieldSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				String table = removeSpaces(comboBoxTable.getSelectedItem().toString());
				String column = removeSpaces(comboBoxColumn.getSelectedItem().toString());
				try{
				
					String query = "select * from "+table+" where "+column+" LIKE '"+textFieldSearch.getText()+"%'";
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					
					tableSearchResult.setModel(DbUtils.resultSetToTableModel(rs));
						
					pst.close();
					rs.close();
			    }catch(Exception e1){
			    	e1.printStackTrace();
			    }				
			}
		});
		textFieldSearch.setBounds(280, 10, 200, 22);
		contentPane.add(textFieldSearch);
		textFieldSearch.setColumns(10);
		
		comboBoxColumn = new JComboBox();
		comboBoxColumn.setModel(new DefaultComboBoxModel(new String[] {"Hotel Code", "Hotel Name", "Contact", "Local Phone", "Fax", "Street", "City", "State/Province", "Country", "Zip Code"}));
		comboBoxColumn.setBounds(120, 10, 150, 20);
		contentPane.add(comboBoxColumn);
		
		lblHotelCode = new JLabel("Hotel Code:");
		lblHotelCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblHotelCode.setBounds(10, 360, 110, 20);
		contentPane.add(lblHotelCode);
		
		textFieldCode = new JTextField();
		textFieldCode.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldCode.setBounds(120, 360, 790, 23);
		contentPane.add(textFieldCode);
		textFieldCode.setColumns(10);
		
		lblHotelName = new JLabel("Hotel Name:");
		lblHotelName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblHotelName.setBounds(10, 390, 110, 20);
		contentPane.add(lblHotelName);
		
		lblContact = new JLabel("Contact:");
		lblContact.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblContact.setBounds(10, 420, 110, 20);
		contentPane.add(lblContact);
		
		lblLocalPhone = new JLabel("Local Phone:");
		lblLocalPhone.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblLocalPhone.setBounds(10, 450, 110, 20);
		contentPane.add(lblLocalPhone);
		
		lblFax = new JLabel("Fax:");
		lblFax.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblFax.setBounds(10, 480, 110, 20);
		contentPane.add(lblFax);
		
		lblStreet = new JLabel("Street:");
		lblStreet.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblStreet.setBounds(10, 510, 110, 20);
		contentPane.add(lblStreet);
		
		textFieldName = new JTextField();
		textFieldName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldName.setColumns(10);
		textFieldName.setBounds(120, 390, 790, 23);
		contentPane.add(textFieldName);
		
		textFieldContact = new JTextField();
		textFieldContact.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldContact.setColumns(10);
		textFieldContact.setBounds(120, 420, 790, 23);
		contentPane.add(textFieldContact);
		
		textFieldLocalPhone = new JTextField();
		textFieldLocalPhone.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldLocalPhone.setColumns(10);
		textFieldLocalPhone.setBounds(120, 450, 790, 23);
		contentPane.add(textFieldLocalPhone);
		
		textFieldFax = new JTextField();
		textFieldFax.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldFax.setColumns(10);
		textFieldFax.setBounds(120, 480, 790, 23);
		contentPane.add(textFieldFax);
		
		textFieldStreet = new JTextField();
		textFieldStreet.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldStreet.setColumns(10);
		textFieldStreet.setBounds(120, 510, 790, 23);
		contentPane.add(textFieldStreet);
		
		lblCity = new JLabel("City:");
		lblCity.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCity.setBounds(10, 540, 110, 20);
		contentPane.add(lblCity);
		
		lblStateProvince = new JLabel("State/Province:");
		lblStateProvince.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblStateProvince.setBounds(10, 570, 110, 20);
		contentPane.add(lblStateProvince);
		
		lblCountry = new JLabel("Country:");
		lblCountry.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblCountry.setBounds(10, 600, 110, 20);
		contentPane.add(lblCountry);
		
		lblZipcode = new JLabel("Zipcode:");
		lblZipcode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblZipcode.setBounds(10, 631, 110, 20);
		contentPane.add(lblZipcode);
		
		textFieldCity = new JTextField();
		textFieldCity.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldCity.setColumns(10);
		textFieldCity.setBounds(120, 540, 790, 23);
		contentPane.add(textFieldCity);
		
		textFieldStateProvince = new JTextField();
		textFieldStateProvince.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldStateProvince.setColumns(10);
		textFieldStateProvince.setBounds(120, 570, 790, 23);
		contentPane.add(textFieldStateProvince);
		
		textFieldCountry = new JTextField();
		textFieldCountry.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldCountry.setColumns(10);
		textFieldCountry.setBounds(120, 600, 790, 23);
		contentPane.add(textFieldCountry);
		
		textFieldZipcode = new JTextField();
		textFieldZipcode.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldZipcode.setColumns(10);
		textFieldZipcode.setBounds(120, 630, 790, 23);
		contentPane.add(textFieldZipcode);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setBounds(820, 664, 89, 23);
		contentPane.add(btnDelete);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String table = removeSpaces(comboBoxTable.getSelectedItem().toString());
				/*if(table.equals("Hotel"))
				{
					try{
						String query = "Update "+table+" set VoucherID='"+voucherID+"' ,Type='"+comboBoxType.getSelectedItem()+"',"
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
			refreshTable("B"+textFieldBookingNumber.getText(), "Voucher");*/
			}
		});
		btnUpdate.setBounds(721, 664, 89, 23);
		contentPane.add(btnUpdate);
		
		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String table = removeSpaces(comboBoxTable.getSelectedItem().toString());
				if(textFieldCode.getText().equals("")||
					textFieldName.getText().equals("")||
					textFieldContact.getText().equals("")||
					textFieldLocalPhone.getText().equals("")||				
					textFieldStreet.getText().equals("")||
					textFieldCity.getText().equals("")||
					textFieldCountry.getText().equals(""))
				{
					if(table.equals("Hotels"))
						lblWarningMessage.setText("Hotels Info Incomplete!");
					else if(table.equals("LandServices"))
						lblWarningMessage.setText("Land Service Info Incomplete!");
					else if(table.equals("Agents"))
						lblWarningMessage.setText("Agents Info Incomplete!");
					
					lblWarningMessage.setForeground(Color.red);
					return;
				}
				
				
				// grab ID depending on what table is selected
				try{				
					String query = "select * from IDs where EXPID=1 ";
					Statement stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(query);	
					hlaID = rs.getInt(table.substring(0,table.length()-1));	//remove the s at the end
					hlaID++;
					stmt.close();
					rs.close();
				}catch(Exception e1){e1.printStackTrace();}	
				
				try{					
					String query;
					PreparedStatement pst;
					
					if(table.equals("Hotels")){
						query = "insert into "+table+" (HotelID,HotelCode,HotelName,Contact,LocalPhone,Fax,Street,City,State,Country,Zipcode) values (?,?,?,?,?,?,?,?,?,?,?)";
						pst = connection.prepareStatement(query);
						pst.setString(1, "EXPH"+hlaID);
					}
					else if(table.equals("LandServices"))
					{
						query = "insert into "+table+" (LSID,LSCode,LSName,Contact,LocalPhone,Fax,Street,City,State,Country,Zipcode) values (?,?,?,?,?,?,?,?,?,?,?)";
						pst = connection.prepareStatement(query);
						pst.setString(1, "EXPL"+hlaID);
					}
					else // Agents
					{
						query = "insert into "+table+" (AgentID,AgentCode,AgentName,Attention,Telephone,Fax,Street,City,State,Country,Zipcode) values (?,?,?,?,?,?,?,?,?,?,?)";
						pst = connection.prepareStatement(query);
						pst.setString(1, "EXPA"+hlaID);
					}
					pst.setString(2, textFieldCode.getText());
					pst.setString(3, textFieldName.getText());
					pst.setString(4, textFieldContact.getText());
					pst.setString(5, textFieldLocalPhone.getText());
					pst.setString(6, textFieldFax.getText());
					pst.setString(7, textFieldStreet.getText());
					pst.setString(8, textFieldCity.getText());
					pst.setString(9, textFieldStateProvince.getText());
					pst.setString(10, textFieldCountry.getText());
					pst.setString(11, textFieldZipcode.getText());

					String s = comboBoxTable.getSelectedItem().toString().substring(0,comboBoxTable.getSelectedItem().toString().length()-1);
					JOptionPane.showMessageDialog(null, textFieldName.getText()+"("+textFieldCode.getText()+") has been added to the "+s+" Database");
					pst.execute();								
					pst.close();
					
				}catch(Exception e1){e1.printStackTrace();}
				
				// update id
				try{				
					//String query = "Update IDs set EXPID='"+1+"' ,Booking='"+(lastBookingNumber+1)+"' ,Voucher='"+voucher+"' ,Passenger='"+passenger+"' where EXPID='"+1+"'  ";
					String query = "Update IDs set EXPID='"+1+"' , "+table.substring(0,table.length()-1)+"='"+hlaID+"' where EXPID='"+1+"'  ";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.execute();					
					//JOptionPane.showMessageDialog(null, "Voucher ID Updated");					
					pst.close();
					}catch(Exception e1){
					e1.printStackTrace();
				}
				
				refreshTable(table);
				
				
			}
		});
		btnCreate.setBounds(622, 664, 89, 23);
		contentPane.add(btnCreate);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		btnClear.setBounds(523, 664, 89, 23);
		contentPane.add(btnClear);
		
		lblWarningMessage = new JLabel("");
		lblWarningMessage.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblWarningMessage.setBounds(10, 660, 500, 20);
		contentPane.add(lblWarningMessage);
		
		
		initialize();
	}
	
	public void initialize()
	{
		refreshTable("Hotels");
	}
	
	public void refreshTable(String tableName)
	{
		try{
			String query = "select * from "+tableName;
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();			
			tableSearchResult.setModel(DbUtils.resultSetToTableModel(rs));
			pst.close();
			rs.close();
		}catch(Exception e1){
			e1.printStackTrace();
		}
	}
	
	public String removeSpaces(String s)
	{
		return s.replaceAll(" ", "").trim();
	}
	
	public void clear()
	{
		textFieldCode.setText("");
		textFieldName.setText("");
		textFieldContact.setText("");
		textFieldLocalPhone.setText("");
		textFieldFax.setText("");
		textFieldStreet.setText("");
		textFieldCity.setText("");
		textFieldStateProvince.setText("");
		textFieldCountry.setText("");
		textFieldZipcode.setText("");
	}
}

















