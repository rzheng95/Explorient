package Explorient;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.parser.Element;

public class Printing extends JFrame {

	private Connection connection = null;
	private JPanel contentPane;
	private JTextPane textPane;
	private int bookingNumber, numOfPax;
	private boolean singlePassenger = false;
	private List<Object> servicesList, hotelList;
	private String services, servicePrivider;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					int placeHolder = 15001;
					Printing frame = new Printing(placeHolder);
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
	public Printing(int bookingNumber) {
		numOfPax = 0;
		servicesList = new ArrayList<>();
		hotelList = new ArrayList<>();
		services = "";
		servicePrivider = "";
		this.bookingNumber = bookingNumber;
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
		setBounds(100, 100, 494, 751);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		
		
		textPane = new JTextPane();
		textPane.setBounds(10, 11, 458, 654);
		textPane.setContentType("text/html");
		textPane.setEditable(false);

		
		// calling methods
		landServices();
		

		contentPane.add(textPane);
		
		JButton btnNewButton = new JButton("Print");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					textPane.print();
				} catch (PrinterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(195, 678, 89, 23);
		contentPane.add(btnNewButton);
	}

	public void formatPrintContent()
	{
		// 1. booking # 
		// 2. issue date
		// 3. passenger names
		// 4. services
		// 5. service provider info
		
		HTMLDocument doc = (HTMLDocument)textPane.getDocument();
		//HTMLEditorKit editorKit = (HTMLEditorKit)textPane.getEditorKit();	
        HTMLEditorKit kit = new HTMLEditorKit() {
            public ViewFactory getViewFactory() {
                return new HTMLFactory() {
                    public InlineView create(javax.swing.text.Element elem) {
                        AttributeSet attrs = elem.getAttributes();
                        Object elementName =
                            attrs.getAttribute(AbstractDocument.ElementNameAttribute);
                        Object o = (elementName != null) ?
                            null : attrs.getAttribute(StyleConstants.NameAttribute);
                        if (o instanceof HTML.Tag) {
                            HTML.Tag kind = (HTML.Tag) o;
                            if (kind == HTML.Tag.CONTENT) {
                                return new InlineView(elem) {
                                    private short left;
                                    private short right;
                                    private short top;
                                    private short bottom;
                                    protected void setPropertiesFromAttributes() {
                                        AttributeSet attr = getAttributes();
                                        if (attr != null) {
                                            top = (short) StyleConstants.getSpaceAbove(attr);
                                            left = (short) StyleConstants.getLeftIndent(attr);
                                            bottom = (short) StyleConstants.getSpaceBelow(attr);
                                            right = (short) StyleConstants.getRightIndent(attr);
                                        }
                                        super.setPropertiesFromAttributes();
                                    }
                                    //TODO : use the top, left, bottom and right properties to draw the margin/padding
                                };
                            }
                        }
                        return (InlineView) super.create(elem);
                    }
                };
            }
        };
		
		StyleSheet styleSheet = kit.getStyleSheet();
		Calendar cal = new GregorianCalendar();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String month = new SimpleDateFormat("MMM").format(cal.getTime());
		int year = cal.get(Calendar.YEAR);

		String content = "";
		// Booking # and Issue Date
		content += "<p class=\"topRightCorner\">Booking #: "+bookingNumber+space(10)+"<br>Issue Date: "+(month+" "+day+", "+year)+"</p>";			
		styleSheet.addRule(".topRightCorner{"
				+ "font-size: 6px;"
				+ "font-family: Tahoma;"				
				+ "text-align: right;"
				+ "}");
		
		// passenger names
		content += "<p id=\"passenger\">"+getPassengerNames()+"<hr>";
		styleSheet.addRule("#passenger{"
				+ "font-size: 9px;"
				+ "font-family: Calibri;"
				+ "font-weight: bold;"
				+ "margin-bottom: 5px"
				+ "}");
		
		// provide service
		if(singlePassenger)
			content += "<p id=\"title\">Please provide the follwing services for the passenger above("+numOfPax+"):</p>";
		else
			content += "<p id=\"title\">Please provide the follwing services for the passengers above("+numOfPax+"):</p>";	
		styleSheet.addRule("#title{"
				+ "font-size: 9px;"
				+ "font-family: Calibri;"
				+ "font-weight: bold;"
				+ "margin-top: -20px"
				+ "}");
		
		

		// services
		content += "<p class=\"services\">"+services+"</p>";	
		styleSheet.addRule(".services{"
				+ "font-size: 8px;"
				+ "font-family: Calibri;"
				+ "font-weight: bold;"
				+ "margin-top: -10px;"
				+ "}");
		
		// service provider info
		content += "<p class=\"servicesProvider\">"+getSerivePrivider(servicePrivider)+"</p>";	
		
		styleSheet.addRule(".servicesProvider{"
				+ "font-size: 6px;"
				+ "font-family: Calibri;"
				+ "}");
		
		
		try {
			kit.insertHTML(doc, doc.getLength(), content, 0, 0, null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void landServices()
	{
		getServices(); // date, type, description, LSCode, Meal
		//System.out.println(servicesList);
		String manifest = ((ArrayList<String>)servicesList.get(0)).get(3);; // DESBKK
		int lines = 3;	
		for(int i=0; i<servicesList.size(); i++)
		{
			
			String tempManifest = ((ArrayList<String>)servicesList.get(i)).get(3);
			
			if(tempManifest.equals(manifest) && lines > 0)
			{
				manifest = tempManifest;
				Format formatter =  new SimpleDateFormat("MMM");
				Date d = (Date) ((ArrayList<Object>)servicesList.get(i)).get(0);
				String month_1 = formatter.format(d);
				
				services += month_1+" "+d.getDate()+": "+((ArrayList<Object>)servicesList.get(i)).get(2)+"<br>";

				lines--;
				
				continue;
			}
			servicePrivider = manifest;
			formatPrintContent();
			
			i--;
			manifest = tempManifest;
			services = "";
			lines = 3;
			
		}
		formatPrintContent();
	}
	
	
	public String getSerivePrivider(String serviceProvider)
	{
		String returnString = "";
		try{							
			String query = "select LSName,LocalPhone,Street,City,State,Country,Zipcode from LandServices where LSCode='"+serviceProvider+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			if(rs.getString("State") == null || rs.getString("State").equals(""))
				returnString += "Service Provider: "+rs.getString("LSName")+"<br>"
									+ "Local Phone: "+rs.getString("LocalPhone")+"<br>"
										+ "Adress: "+rs.getString("Street")+", "+rs.getString("City")+", "+rs.getString("Country")+", "+rs.getString("Zipcode");
			else
				returnString += "Service Provider: "+rs.getString("LSName")+"<br>"
						+ "Local Phone: "+rs.getString("LocalPhone")+"<br>"
							+ "Adress: "+rs.getString("Street")+", "+rs.getString("City")+", "+rs.getString("State")+", "+rs.getString("Country")+", "+rs.getString("Zipcode");
			
			pst.close();
			rs.close();
		}catch(Exception e1){e1.printStackTrace();}
		
		return returnString;
	}
	
	
	public String getPassengerNames()
	{
		String names = "";
		int count = 0;
		try{							
			String query = "select Firstname,Lastname from PaxInfos where BookingNumber='"+bookingNumber+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				count++;
				names += rs.getString("Lastname")+" "+rs.getString("Firstname")+", ";
			}

			pst.close();
			rs.close();
		}catch(Exception e1){e1.printStackTrace();}
		numOfPax = count;
		if(count>1)			
			return "Passengers: "+names.substring(0, names.length()-2);	
		else{
			singlePassenger = true;
			return "Passenger: "+names.substring(0, names.length()-2);	
		}
	}
	
	public void getServices()
	{
		List<Object> temp = new ArrayList<>();
		try{							
			String query = "select * from B"+bookingNumber+" where Type='Land Service'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			List<Object> list = new ArrayList<>();
			temp = new ArrayList<>();
			
			List<Date> dates = new ArrayList<>();
			
			
			while(rs.next())
			{
				String[] date = rs.getString("Date").split("/");
				if(Integer.parseInt(date[0])<10) date[0] = "0"+date[0];
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date d1 = sdf.parse(date[0]+"/"+date[1]+"/"+date[2]);
				
				dates.add(d1);
				temp.add(d1);
				temp.add(rs.getString("Type"));
				temp.add(rs.getString("Description"));
				temp.add(rs.getString("Manifest"));
				temp.add(rs.getString("Meal"));
				list.add(temp);
				temp = new ArrayList<>();
							
			}
				
			temp = new ArrayList<>();
			// sort by dates
			for(int i=0; i<dates.size();i++)
			{
				Date d = Collections.min(dates);
				int index = dates.indexOf(d);				
				temp.add(list.remove(index));				
				dates.remove(d);			
				i--;
			}
								
			pst.close();
			rs.close();
		}catch(Exception e1){e1.printStackTrace();}

		servicesList = temp;
	}
	
	public String space(int n)
	{
		String temp = "";
		for(int i=0; i<n; i++)
			temp+="&nbsp;";
		return temp;
	}
	
}
