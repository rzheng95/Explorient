package Explorient;


import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JButton;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import autoCompleteComboBox.AutocompleteJComboBox;
import autoCompleteComboBox.StringSearchable;

import javax.swing.JSeparator;
import javax.swing.DropMode;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreateNewBooking extends JFrame {

	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private int booking, passenger;
	private JPanel contentPane;
	private Image icon;
	private JTextField textFieldAgentName;
	private JTextField textFieldStreet;
	private JTextField textFieldTelephone;
	private JTextField textFieldAttention;
	private JTextField textFieldPaxNames;
	private JComboBox comboBoxGateway;
	private JTable table;
	private DefaultTableModel model;
	private JTextField textFieldCity;
	private JTextField textFieldCountry;
	private JTextField textFieldZipcode;
	private JLabel lblPassengerWarning, lblAgentWarning;
	private JButton btnFinalize;
	private ArrayList<String> agentList;;
	private AutocompleteJComboBox comboBoxAgentCode;
	private JTextField textFieldState;
	private JXDatePicker datePickerDeparture;
	private JButton btnDelete;
	private JTextField textFieldFax;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CreateNewBooking frame = new CreateNewBooking();
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
	public CreateNewBooking(){	
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
		setBounds(100, 100, 520, 617);
		//setResizable(false);
		setTitle("New Booking");
		setLocationRelativeTo(null);
		icon = new ImageIcon(this.getClass().getResource("/Explorient Icon.jpg")).getImage();
		setIconImage(icon);
		
		// get Agent list
		try{				
			String query = "select AgentCode, AgentName from Agents";
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);	
			agentList = new ArrayList<>();
			while(rs.next()){
				String aCode = rs.getString("AgentCode");	
				String aName = rs.getString("AgentName");	
				agentList.add(aCode+" ("+aName+")");
			}
			stmt.close();
			rs.close();
		}catch(Exception e1){
			e1.printStackTrace();
		}	
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmVoucher = new JMenuItem("Voucher");
		mntmVoucher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				Voucher v = new Voucher("");
				v.setVisible(true);
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
		
		JMenuItem mntmInfoCenter = new JMenuItem("Info Center");
		mntmInfoCenter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				InformationCenter s = new InformationCenter();
				s.setVisible(true);
			}
		});
		mnFile.add(mntmInfoCenter);
		mnFile.add(mntmVoucher);
		
		JMenu mnAbout = new JMenu("About");
		menuBar.add(mnAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblAgentCode = new JLabel("Agent Code:");
		lblAgentCode.setBounds(10, 10, 90, 20);
		lblAgentCode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(15, 366, 0, 0);
		
		JLabel lblAgentName = new JLabel("Agent Name:");
		lblAgentName.setBounds(10, 40, 90, 20);
		lblAgentName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		textFieldAgentName = new JTextField();
		textFieldAgentName.setBounds(100, 40, 200, 23);
		textFieldAgentName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldAgentName.setColumns(10);
		
		JLabel lblStreet = new JLabel("Street:");
		lblStreet.setBounds(10, 100, 90, 20);
		lblStreet.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		textFieldStreet = new JTextField();
		textFieldStreet.setBounds(100, 100, 200, 23);
		textFieldStreet.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldStreet.setColumns(10);
		
		JLabel lblTelephone = new JLabel("Telephone:");
		lblTelephone.setBounds(10, 250, 90, 20);
		lblTelephone.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		textFieldTelephone = new JTextField();
		textFieldTelephone.setBounds(100, 250, 200, 23);
		textFieldTelephone.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldTelephone.setColumns(10);
		
		JLabel lblAttention = new JLabel("Attention:");
		lblAttention.setBounds(10, 70, 90, 20);
		lblAttention.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		textFieldAttention = new JTextField();
		textFieldAttention.setBounds(100, 70, 200, 23);
		textFieldAttention.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldAttention.setColumns(10);
		
		JLabel lblDeparture = new JLabel("Departure:");
		lblDeparture.setBounds(10, 360, 90, 20);
		lblDeparture.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		datePickerDeparture = new JXDatePicker();
		datePickerDeparture.setBounds(100, 360, 200, 23);
		
		
		JLabel lblPassengerNames = new JLabel("Pax names:");
		lblPassengerNames.setBounds(10, 420, 90, 20);
		lblPassengerNames.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		textFieldPaxNames = new JTextField();
		textFieldPaxNames.setBounds(100, 420, 200, 23);
		textFieldPaxNames.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldPaxNames.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				char c = event.getKeyChar();
				if(c==KeyEvent.VK_ENTER){
					String s = textFieldPaxNames.getText().trim();
					while(s.contains("  "))
						s = s.replace("  ", " ");
					if(s.contains(" ")){
						String[] list = s.split(" ");
						for(int i=0; i<list.length; i++)
						{
							if(list[i].contains("-"))
							{
								String[] temp = list[i].split("-");
								list[i] = "";
								for(int j=0; j<temp.length; j++)
								{
									temp[j] = temp[j].substring(0, 1).toUpperCase() + temp[j].substring(1).toLowerCase();		
									if(j==temp.length-1)
										list[i] += temp[j];
									else
										list[i] += temp[j]+ "-";
								}
							}
							else
							{
								list[i] = list[i].substring(0, 1).toUpperCase() + list[i].substring(1).toLowerCase();
							}
						}
						model.addRow(list);
					}
					textFieldPaxNames.setText("");
				}
			}
		});
		textFieldPaxNames.setColumns(10);
		
		btnFinalize = new JButton("Finalize & Create Booking");
		btnFinalize.setBounds(10, 482, 290, 40);
		btnFinalize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		
				if(comboBoxAgentCode.getSelectedItem() == null || 
				   textFieldAttention.getText().equals("") ||
				   textFieldAgentName.getText().equals("") ||
				   textFieldStreet.getText().equals("")||
				   textFieldCity.getText().equals("")||
				   textFieldCountry.getText().equals("") ||
				   textFieldTelephone.getText().equals(""))
				{
					lblAgentWarning.setText("Agent Info Incomplete!");
					return;
				}
				else 
				{
					lblAgentWarning.setText("");
				}
				
				// add agent if not already exist to db				
				
				String str = comboBoxAgentCode.getSelectedItem().toString();
				String[] agentCodeAndName = str.split(" ");
				String agentCode = agentCodeAndName[0];
				boolean agentExist = false;
				ArrayList<String> agentCodes = new ArrayList<>();
				try{									
					String query = "select AgentCode from Agents";
					Statement stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(query);	

					while(rs.next())
						agentCodes.add(rs.getString("AgentCode"));
					
					stmt.close();
					rs.close();
				}catch(Exception e1){e1.printStackTrace();}	
				
				for(String c : agentCodes)
					if(c.equals(agentCode))
						agentExist = true;
				
				
				if(!agentExist)
				{
					// grab ID from IDs
					int agentID = -1;
					try{				
						String query = "select * from IDs where EXPID=1 ";
						Statement stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(query);	
						agentID = rs.getInt("Agent");	
						agentID++;
						stmt.close();
						rs.close();
					}catch(Exception e1){e1.printStackTrace();}	
					
					if(agentID != -1)
					{
						// add agent
						try{					
							String query;
							PreparedStatement pst;
							query = "insert into Agents (AgentID,AgentCode,AgentName,Attention,Street,City,State,Country,Zipcode,Telephone,Fax) values (?,?,?,?,?,?,?,?,?,?,?)";
							pst = connection.prepareStatement(query);
							
							pst.setString(1, "EXPA"+agentID);
							pst.setString(2, comboBoxAgentCode.getSelectedItem().toString());
							pst.setString(3, textFieldAgentName.getText());
							pst.setString(4, textFieldAttention.getText());
							pst.setString(5, textFieldStreet.getText());
							pst.setString(6, textFieldCity.getText());
							pst.setString(7, textFieldState.getText());
							pst.setString(8, textFieldCountry.getText());
							pst.setString(9, textFieldZipcode.getText());
							pst.setString(10, textFieldTelephone.getText());
							pst.setString(11, textFieldFax.getText());
				
							pst.execute();								
							pst.close();
							
						}catch(Exception e1){e1.printStackTrace();}
						
						// update ID
						try{				
							String query = "Update IDs set Agent='"+agentID+"' where EXPID='"+1+"'  ";
							PreparedStatement pst = connection.prepareStatement(query);
							pst.execute();								
							pst.close();
							}catch(Exception e1){
							e1.printStackTrace();
						}
					}
				}
				
								
				
				
				if(datePickerDeparture.getDate() == null || comboBoxGateway.getSelectedItem().toString().trim().equals(""))
				{
					lblPassengerWarning.setText("Passenger Info Imcomplete!");
					lblPassengerWarning.setForeground(Color.red);
					return;
				}
				
				if(tablePassengerCount() > 0) 
				{
					lblPassengerWarning.setText("");
					btnFinalize.setBackground(Color.blue);
					// fetch data from IDs, grab booking and passenger # from IDs
					try{				
						String query = "select * from IDs where EXPID=1 ";
						Statement stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(query);	
	
						booking = rs.getInt("Booking");	
						booking++; // increment booking #
						passenger = rs.getInt("Passenger");	
						stmt.close();
						rs.close();
					}catch(Exception e1){
						e1.printStackTrace();
					}	

					// update PaxInfos database					
					addPassengers();
												
					// update booking and passenger in IDs
					try{				
						String query = "Update IDs set EXPID='"+1+"' ,Booking='"+booking+"' ,Passenger='"+passenger+"' where EXPID='"+1+"'  ";
						PreparedStatement pst = connection.prepareStatement(query);
						pst.execute();					
						//JOptionPane.showMessageDialog(null, "Booking and passenger IDs Updated");					
						pst.close();
						}catch(Exception e1){
						e1.printStackTrace();
					}
					
					clearAll();
			
					JOptionPane.showMessageDialog(null, "Booking "+booking+" has been created.", "New Booking", JOptionPane.PLAIN_MESSAGE);
					dispose();
					Voucher v = new Voucher(""+booking);
					v.setVisible(true);
					v.refreshTable();
				}
				else
				{
					lblPassengerWarning.setText("Can't have 0 passenger");
					lblPassengerWarning.setForeground(Color.red);
					btnFinalize.setBackground(Color.red);
				}
			}
		});
		btnFinalize.setFont(new Font("Tahoma", Font.BOLD, 13));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(330, 10, 160, 436);
		
		model = new DefaultTableModel();
		
		table = new JTable(model);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				btnDelete.setEnabled(true);
			}
		});

		model.addColumn("Lastname");
		model.addColumn("Firstname");	
		
		//model.addRow(new Object[]{"Richard", "Zheng"});
		
		scrollPane.setViewportView(table);
		
		JLabel lblGateway = new JLabel("Gateway:");
		lblGateway.setBounds(10, 390, 90, 20);
		lblGateway.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		btnDelete = new JButton("Delete");
		btnDelete.setEnabled(false);
		btnDelete.setBounds(427, 460, 63, 23);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(table.getSelectedRow() == -1) return;
				model.removeRow(table.getSelectedRow());
				btnDelete.setEnabled(false);
			}
		});
		
		JLabel lblCity = new JLabel("City:");
		lblCity.setBounds(10, 130, 90, 20);
		lblCity.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblCountry = new JLabel("Country:");
		lblCountry.setBounds(10, 190, 90, 20);
		lblCountry.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblZipcode = new JLabel("Zipcode:");
		lblZipcode.setBounds(10, 220, 90, 20);
		lblZipcode.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPane.setLayout(null);
		contentPane.add(lblNewLabel);
		contentPane.add(lblAttention);
		contentPane.add(textFieldAttention);
		contentPane.add(lblTelephone);
		contentPane.add(textFieldTelephone);
		contentPane.add(lblStreet);
		contentPane.add(textFieldStreet);
		contentPane.add(lblAgentCode);
		contentPane.add(lblAgentName);
		contentPane.add(textFieldAgentName);
		contentPane.add(lblPassengerNames);
		contentPane.add(lblDeparture);
		contentPane.add(textFieldPaxNames);
		contentPane.add(btnFinalize);
		contentPane.add(scrollPane);
		contentPane.add(lblGateway);
		contentPane.add(btnDelete);
		contentPane.add(lblCity);
		contentPane.add(lblCountry);
		contentPane.add(lblZipcode);
		contentPane.add(datePickerDeparture);
		
		textFieldCity = new JTextField();
		textFieldCity.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldCity.setColumns(10);
		textFieldCity.setBounds(100, 130, 200, 23);
		contentPane.add(textFieldCity);
		
		textFieldCountry = new JTextField();
		textFieldCountry.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldCountry.setColumns(10);
		textFieldCountry.setBounds(100, 190, 200, 23);
		contentPane.add(textFieldCountry);
		
		textFieldZipcode = new JTextField();
		textFieldZipcode.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldZipcode.setColumns(10);
		textFieldZipcode.setBounds(100, 220, 200, 23);
		contentPane.add(textFieldZipcode);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(15, 330, 300, 8);
		contentPane.add(separator);
		
		lblPassengerWarning = new JLabel("");
		lblPassengerWarning.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPassengerWarning.setBounds(10, 450, 290, 20);
		contentPane.add(lblPassengerWarning);	

		
		StringSearchable searchable = new StringSearchable(agentList);

		comboBoxAgentCode = new AutocompleteJComboBox(searchable);

		
		comboBoxAgentCode.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxAgentCode.setBounds(100, 10, 150, 23);
		contentPane.add(comboBoxAgentCode);
		
		JButton btnFill = new JButton("Fill");
		btnFill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		
				if(comboBoxAgentCode.getSelectedItem() == null) return;
				boolean existInAgentList = false;
				for(String s : agentList)
					if(comboBoxAgentCode.getSelectedItem().toString().equals(s))
						existInAgentList = true;
				if(existInAgentList)
				{
					try{				
						String[] temp = comboBoxAgentCode.getSelectedItem().toString().split(" ");
						
						String query = "select * from Agents where AgentCode='"+temp[0].trim()+"'";
						Statement stmt = connection.createStatement();
						ResultSet rs = stmt.executeQuery(query);	
	
						textFieldAgentName.setText(rs.getString("AgentName"));
						textFieldAttention.setText(rs.getString("Attention"));
						textFieldStreet.setText(rs.getString("Street"));
						textFieldCity.setText(rs.getString("City"));
						textFieldState.setText(rs.getString("State"));
						textFieldCountry.setText(rs.getString("Country"));
						textFieldZipcode.setText(rs.getString("Zipcode"));
						textFieldTelephone.setText(rs.getString("Telephone"));
	
						stmt.close();
						rs.close();
					}catch(Exception e1){
						e1.printStackTrace();
					}	
				}
			}
		});
		btnFill.setBounds(250, 10, 50, 23);
		contentPane.add(btnFill);
		
		JLabel lblState = new JLabel("State:");
		lblState.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblState.setBounds(10, 160, 90, 20);
		contentPane.add(lblState);
		
		textFieldState = new JTextField();
		textFieldState.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldState.setColumns(10);
		textFieldState.setBounds(100, 160, 200, 23);
		contentPane.add(textFieldState);
		
		lblAgentWarning = new JLabel("");
		lblAgentWarning.setForeground(Color.RED);
		lblAgentWarning.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblAgentWarning.setBounds(10, 310, 290, 20);
		contentPane.add(lblAgentWarning);
		
		comboBoxGateway = new JComboBox();
		comboBoxGateway.setModel(new DefaultComboBoxModel(new String[] {"Land Only"}));
		comboBoxGateway.setFont(new Font("Tahoma", Font.PLAIN, 12));
		comboBoxGateway.setEditable(true);
		comboBoxGateway.setBounds(100, 390, 200, 23);
		contentPane.add(comboBoxGateway);
		
		JLabel lblFax = new JLabel("Fax:");
		lblFax.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblFax.setBounds(10, 280, 90, 20);
		contentPane.add(lblFax);
		
		textFieldFax = new JTextField();
		textFieldFax.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textFieldFax.setColumns(10);
		textFieldFax.setBounds(100, 280, 200, 23);
		contentPane.add(textFieldFax);

			
	}

	
	public Object[][] getPassengersFromTable(JTable table)
	{
		int row = model.getRowCount();
		int col = model.getColumnCount();
		
		Object[][] tableData = new Object[row][col];
		
		for(int i=0; i<row; i++)
			for(int j=0; j<col; j++)
				tableData[i][j] = model.getValueAt(i, j);		
		
		return tableData;
	}
	
	public int tablePassengerCount()
	{
		Object[][] temp = getPassengersFromTable(table);
		int nRow = temp.length;
		return nRow;
	}
	
	public void addPassengers()
	{
		
		if(tablePassengerCount() > 0)
		{
			Object[][] passengerTable = getPassengersFromTable(table);
			for(int i=0; i<passengerTable.length; i++)
			{
				String firstName = passengerTable[i][1].toString();
				String lastName = passengerTable[i][0].toString();
				Date d = datePickerDeparture.getDate();
				try{					
					String query = "insert into Passengers (PassengerID, BookingNumber, Agent, Firstname, Middlename, Lastname, Departure, Gateway) values (?,?,?,?,?,?,?,?)";
					PreparedStatement pst = connection.prepareStatement(query);
					passenger++;
					pst.setString(1, "EXPP"+passenger);
					pst.setInt(2, booking);
					pst.setString(3, comboBoxAgentCode.getSelectedItem().toString());
					if(firstName.contains("-"))
					{
						String[] temp = firstName.trim().split("-");
						
						pst.setString(4, temp[0]);//firstname
						pst.setString(5, temp[1]);//middlename
					}
					else
					{
						firstName.toLowerCase();
						firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
						pst.setString(4, firstName.toString());
						pst.setString(5, null);
					}											
					pst.setString(6, lastName.toString());//lastname
					pst.setString(7, ""+(d.getMonth()+1)+"/"+d.getDate()+"/"+(d.getYear()+1900));
					pst.setString(8, comboBoxGateway.getSelectedItem().toString());
				
					pst.execute();				
					pst.close();					
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void clearAll()
	{
		comboBoxAgentCode.setSelectedItem("");
		textFieldAgentName.setText("");
		textFieldAttention.setText("");
		textFieldStreet.setText("");
		textFieldCity.setText("");
		textFieldState.setText("");
		textFieldCountry.setText("");
		textFieldZipcode.setText("");
		textFieldTelephone.setText("");
		datePickerDeparture.setDate(null);
		comboBoxGateway.setSelectedItem("");		
		model.setRowCount(0);
	}
}
















