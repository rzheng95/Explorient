/*
 * This file is part of MineSweeper
 * Copyright (C) 2015-2019 Richard R. Zheng
 *
 * https://github.com/rzheng95/MineSweeper
 * 
 * All Right Reserved.
 */

package com.rzheng.explorient;
/* ====================================================================
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==================================================================== */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import javax.swing.JOptionPane;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;


public class PrintVoucher {
	
	private static final Integer Parse = null;


	public static void main(String[] args) throws IOException, InvalidFormatException {

		PrintVoucher pv = new PrintVoucher(15000, "");
		
	}
	private static int count = 0;
	private Connection connection = null;
	private int booking;
	private ArrayList<Object> vouchers;
	private XWPFDocument doc;
	private int paxNum;


	public PrintVoucher(int booking, String exportDirectory) throws IOException, InvalidFormatException{
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
		 
		
		 this.booking = booking;
		 
		 

		 
		boolean fileExist = new File("Voucher Template.docx").exists();
		if(fileExist)
		{
			InputStream is = new FileInputStream("Voucher Template.docx");
			doc = new XWPFDocument(is);
		}
		else
			doc = new XWPFDocument();
			
			
		 
		 vouchers = new ArrayList<>();
		 getVouchers();
		 
// land service
		 ArrayList<Object> ser = services();
		 
		 for(int i=0; i<ser.size(); i++)
		 {
			 ArrayList<Object> temp = ((ArrayList<Object>)ser.get(i));
			 Object vendor = ((ArrayList<Object>)temp.get(0)).get(getType("Manifest"));
			 String issueDate = processIssueDate(((ArrayList<Object>)temp.get(0)).get(getType("issueDate")).toString());

			 

			 String[] arr = vendor.toString().split(" ");
			 vendor = arr[0];

			 landServiceVoucherTemplate(booking, passengerNames(), formatService(((ArrayList<Object>)ser.get(i))), vendor(vendor, "Land Service"), issueDate);
		 }
		 
// hotel		 
		 ArrayList<Object> htl = hotels();	 

		 for(int i=0; i<htl.size(); i++)
		 {

			 ArrayList<Object> hotelService = formatHotel(((ArrayList<Object>)htl.get(i)));
			 
			 
			 ArrayList<Object> temp = ((ArrayList<Object>)htl.get(i));
			 Object vendor = ((ArrayList<Object>)temp.get(0)).get(getType("Manifest"));
			 String issueDate = processIssueDate(((ArrayList<Object>)temp.get(0)).get(getType("issueDate")).toString());
			 
			 
			 
			 
			 String[] arr = vendor.toString().split(" ");
			 vendor = arr[0];
				 
			 hotelVoucherTemplate(booking, passengerNames(), hotelService, vendor(vendor, "Hotel"), issueDate);
		 }
		 
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
	public String processIssueDate(String issueDate)
	{	
		if(!issueDate.equals("") && issueDate != null)
		 {
			 String[] dateArr = issueDate.split("/");
			 if(Integer.parseInt(dateArr[0]) < 10)
				 dateArr[0] = "0"+dateArr[0];
			 if(Integer.parseInt(dateArr[1]) < 10)
				 dateArr[1] = "0"+dateArr[1];
			 issueDate = dateArr[0]+"/"+dateArr[1]+"/"+dateArr[2];
		 }
		return issueDate;
	}


	public void hotelVoucherTemplate(int booking, String passengers, ArrayList<Object> hotelService, String vendor, String issueDate)
	{

		
		//Booking
			 XWPFParagraph p1 = doc.createParagraph();
			 p1.setAlignment(ParagraphAlignment.LEFT);
			 XWPFRun r1 = p1.createRun();
			 r1.setBold(true);
//			 r1.setText("Issue Date: "+issueDate+"                                                                                                "
//				 		+ "                                        "+"Booking #: "+booking);		
			 r1.setFontSize(14);
			 r1.setText("Hotel Voucher for Booking #: "+booking); 
	     
	   //Passenger
		     XWPFParagraph p2 = doc.createParagraph();
		     p2.setAlignment(ParagraphAlignment.LEFT);

		     XWPFRun r2 = p2.createRun();
		     r2.setBold(true);
		     r2.setText(passengers);
		     r2.setFontSize(12);

		     
		//line
		     p2.setBorderBottom(Borders.SINGLE);
		     
		     XWPFParagraph pp = doc.createParagraph();
		     XWPFRun temp = pp.createRun();
		     temp.setBold(true);
		     temp.setFontSize(13);
		     temp.setText( ((ArrayList<String>) hotelService.get(0)).get(0) );
		//Table
		     XWPFTable table = doc.createTable();
		//Create the heading row
		     XWPFTableRow tableRowHeading= table.getRow(0);
		     tableRowHeading.getCell(0).setText("Room"); // col 1
		     tableRowHeading.addNewTableCell().setText("No. of Nights"); // col 2
		     tableRowHeading.addNewTableCell().setText("Check In/Out"); // col 2
		     tableRowHeading.addNewTableCell().setText("Breakfast"); // col 3
		     tableRowHeading.addNewTableCell().setText("Room Category"); // col 3
		     
		     int count = 0;
		     
		     for(int i=0; i<hotelService.size(); i++)
		     {    	 
			     XWPFTableRow tableRow = table.createRow();
			     int numOfRoom = Integer.parseInt(((ArrayList<String>) hotelService.get(i)).get(4).split(" ")[0]);
			     count++;
			     String r = ""+count;
			     for(int j=0; j<numOfRoom-1; j++)
			     {
			    	 count++;
			    	 r += ","+count;
			     }
			     
			     tableRow.getCell(0).setText( r );
			     tableRow.getCell(1).setText( ((ArrayList<String>) hotelService.get(i)).get(1) );
			     tableRow.getCell(2).setText( ((ArrayList<String>) hotelService.get(i)).get(2) );
			     String bf = "No";
			     if(((ArrayList<String>) hotelService.get(i)).get(3).toLowerCase().contains("breakfast"))
			    	 bf = "Yes";
			     tableRow.getCell(3).setText( bf );
			     tableRow.getCell(4).setText( ((ArrayList<String>) hotelService.get(i)).get(4) );
		     }

		     
		//Hotel Vendor Confirmation
		     String hotelVendor = "", LSName = "", LSPhone = "";
		     try{							
					String query = "select * FROM Hotels where HotelName ='"+((ArrayList<String>) hotelService.get(0)).get(0)+"'";
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					if(rs.getString("Vendor") != null)
						hotelVendor = rs.getString("Vendor");
					
					pst.close();
					rs.close();
			 }catch(Exception e1){e1.printStackTrace();}
		     
		     if(!hotelVendor.equals("") && hotelVendor != null)
		     {
			     try{					
						String query = "select * FROM Vendors where LSCode ='"+hotelVendor+"'";
						PreparedStatement pst = connection.prepareStatement(query);
						ResultSet rs = pst.executeQuery();
						
						LSName = rs.getString("LSName");
						LSPhone = rs.getString("LocalPhone");
						
						pst.close();
						rs.close();
				  }catch(Exception e1){e1.printStackTrace();}
		     }
		     
		     XWPFParagraph p3 = doc.createParagraph();
 		     p3.setAlignment(ParagraphAlignment.LEFT);
 		     XWPFRun r3 = p3.createRun();
 		     r3.setFontSize(11);
 		     r3.setBold(true);
 		     
 		     r3.addBreak();    	 
 		     
		     r3.setText("As Confirmed & Paid for by "+LSName+" (Tel: "+LSPhone+")");		     
		     		     
		     
		     for(int i=0; i<(2-hotelService.size()); i++)
		    	 r3.addBreak();
		     

		     
		  //Vendor info
	 		    XWPFParagraph p4 = doc.createParagraph();
			    p4.setAlignment(ParagraphAlignment.LEFT);
			    XWPFRun temp2 = p4.createRun();
			    temp2.setBold(true);
			    temp2.setFontSize(11);
			    temp2.setText("Service Provided By:");
			    temp2.addBreak();
			    
			    XWPFRun r4 = p4.createRun();
			    r4.setFontSize(11);
			    
			    
			    if(vendor.contains(";"))
			     {
			    	 String[] lines = vendor.split(";");
			    	 
			    	 r4.setText(lines[0], 0);
			    	 for(int i=1;i<lines.length;i++){
			    		 r4.addBreak();
			    		 r4.setText(lines[i]);
	              }
			     }else {
	               r4.setText(vendor, 0);
	           }
	}
	
 
 	public void landServiceVoucherTemplate(int booking, String passengers, String services, String vendor, String issueDate) 
 	{
		//Booking
			 XWPFParagraph p1 = doc.createParagraph();
			 p1.setAlignment(ParagraphAlignment.LEFT);
			 XWPFRun r1 = p1.createRun();
			 r1.setBold(true);	 
//			 r1.setText("Issue Date: "+issueDate+"                                                                                                "
//			 		+ "                                        "+"Booking #: "+booking);	
			 
			 r1.setFontSize(14);
			 r1.setText("Land Service Voucher for Booking #: "+booking);

 		     
 		     
 		//Passenger
 		     XWPFParagraph p2 = doc.createParagraph();
 		     p2.setAlignment(ParagraphAlignment.LEFT);

 		     XWPFRun r2 = p2.createRun();
 		     r2.setBold(true);
 		     r2.setText(passengers);
 		     r2.setFontSize(12);

 		     
 		//line
 		     p2.setBorderBottom(Borders.SINGLE);

 		//Services 
 		     XWPFParagraph p3 = doc.createParagraph();
 		     p3.setAlignment(ParagraphAlignment.LEFT);
 		     XWPFRun temp = p3.createRun();
 		     temp.setBold(true);
 		    
 		     
 		    if(paxNum>1)
		    	 temp.setText("Please provide the following services for the passengers above ("+paxNum+"): ", 0);
		     else
		    	temp.setText("Please provide the following services for the passenger above ("+paxNum+"): ", 0);
 		   temp.addBreak();
 		   
 		     XWPFRun r3 = p3.createRun();
 		     r3.setFontSize(11);	     
 		     
 		     //r3.setBold(true);
 		     r3.setText("");
 		     
 		     
 		     
 		    String[] lines = new String[0];
 		     if(services.contains(";"))
 		     {
 		    	 lines = services.split(";");
 		    	 
 		    	 r3.setText(lines[0], 1);
 		    	for(int i=1;i<lines.length;i++){
                    r3.addBreak();
                    r3.setText(lines[i]);
                }
 		     }else {
                 r3.setText(services, 0);
             }
 		     
 		     for(int i=0; i<(7-lines.length); i++)
 		    	 r3.addBreak();
 		     
 		     
 	 	//Vendor info
 		    XWPFParagraph p4 = doc.createParagraph();
		    p4.setAlignment(ParagraphAlignment.LEFT);
		    XWPFRun temp2 = p4.createRun();
		    temp2.setBold(true);
		    temp2.setFontSize(11);
		    temp2.setText("Service Provided By:");
		    temp2.addBreak();
		    
		    
		    XWPFRun r4 = p4.createRun();
		    r4.setFontSize(11);
		    
		    //r4.setText(vendor);
		    
		    if(vendor.contains(";"))
		     {
		    	 lines = vendor.split(";");
		    	 r4.setText(lines[0], 0);
		    	 for(int i=1;i<lines.length;i++){
		    		 r4.addBreak();
		    		 r4.setText(lines[i]);
              }
		     }else {
               r4.setText(vendor, 0);
           }

 		     
 	}
 	
 	
    public String passengerNames()
    {
    	String names = "";
		paxNum = 0;
		try{							
			String query = "select Firstname,Lastname from Passengers where BookingNumber='"+booking+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				paxNum++;
				names += rs.getString("Firstname")+" "+rs.getString("Lastname")+", ";
			}

			pst.close();
			rs.close();
		}catch(Exception e1){e1.printStackTrace();}
				
		if(paxNum>1)			
			return "Passengers: "+names.substring(0, names.length()-2);	
		else{
			return "Passenger: "+names.substring(0, names.length()-2);	
		}   	
    }
    
    
    public void getVouchers()
    {

    	ArrayList<Object> temp = new ArrayList<>();
    	try{

    		String query = "select * from Vouchers where BookingNumber="+booking;
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			ArrayList<Object> ArrayList = new ArrayList<>();
			temp = new ArrayList<>();
			
			ArrayList<Date> dates = new ArrayList<>();
			
			while(rs.next())
			{
				String[] date = rs.getString("Date").split("/"); // 1
				if(Integer.parseInt(date[0])<10) date[0] = "0"+date[0];
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date d1 = sdf.parse(date[0]+"/"+date[1]+"/"+date[2]);
				
				dates.add(d1);
				temp.add(d1);
				temp.add(rs.getString("VoucherType")); // 2
				temp.add(rs.getString("Service")); // 3
				temp.add(rs.getString("Vendor")); // 4
				temp.add(rs.getString("Meal"));  // 5
				temp.add(rs.getInt("NumOfNight")); // 6
				temp.add(rs.getString("RoomType")); // 7
				temp.add(rs.getInt("NumOfRoom")); // 8
				temp.add(rs.getString("RoomSize")); // 9				
				temp.add(rs.getString("IssueDate")); // 10			
				
				ArrayList.add(temp);
				
			
				temp = new ArrayList<>();							
			}
				
			
			
			temp = new ArrayList<>();
			//sort by dates
			for(int i=0; i<dates.size();i++)
			{
				Date d = Collections.min(dates);
				int index = dates.indexOf(d);				
				temp.add(ArrayList.remove(index));				
				dates.remove(d);			
				i--;
			}
			
			pst.close();
			rs.close();
    	}
    	catch(Exception e1){e1.printStackTrace();}
    	
    	vouchers = temp;
    	
    }
 
    public ArrayList<Object> services()
    {
    	/*
    	
    	String str = "";
    	ArrayList<Object> service = new ArrayList<>();
    	ArrayList<Object> temp = vouchers;//sortByDateAnd("Manifest");
    	for(int i=0; i<temp.size(); i++)
    	{
    		String type = ((ArrayList<String>)temp.get(i)).get(getType("Type"));  
    		
    		if(type.equals("Land Service") || type.equals("Air Ticket"))
    		{
    			service.add(temp.get(i));
    		}
    	}
    	
    	if(service.size() <= 0) return service;
    	
    	

    	//if(service.size() <= 0) return service;
    	// group services by vendor
    	int count = 0;
    	String vendor = ((ArrayList<String>)service.get(0)).get(getType("Manifest")).split(" ")[0];

    	
    	ArrayList<Object> returnArrayList = new ArrayList<>();
    	ArrayList<Object> tempArrayList = new ArrayList<>();
    	for(int i=0; i<service.size(); i++)
    	{
    		String tempVendor = ((ArrayList<String>)service.get(i)).get(getType("Manifest")).split(" ")[0];
    		
    		if(tempVendor.equals(vendor))
    		{   			
    			count++;
    			if(count > 5)
    			{
    				returnArrayList.add(tempArrayList);
    				tempArrayList = new ArrayList<>();
    				count = 1;
    			}   	
    			tempArrayList.add(service.get(i));
    		}
    		else
    		{
    			returnArrayList.add(tempArrayList);
    			tempArrayList = new ArrayList<>();
    			tempArrayList.add(service.get(i));
    			vendor = tempVendor;
    			count = 0;
    		}
    	}
    	returnArrayList.add(tempArrayList);   
    	return returnArrayList; */
    	return getBy(getType2("land service"),5);
    }
    
	public String formatService(ArrayList<Object> service)
	{
		String returnString = "";
		for(int i=0; i<service.size(); i++)
		{
			Format formatter =  new SimpleDateFormat("MMM");
			Date d = (Date) ((ArrayList<Object>)service.get(i)).get(0);
			String month = formatter.format(d);			
			
			String meal = ((ArrayList<Object>)service.get(i)).get(4).toString().trim().toString();
			
			String s = "";
			if(meal.contains("L"))
				s = " (Lunch)";
			if(meal.contains("D"))
			{
				if(meal.contains("L"))
				{
					s = s.substring(0, s.length()-1);
					s += ", Dinner)";
				}
				else
					s = " (Dinner)";
			}
			

			returnString += month+" "+d.getDate() + ": " + ((ArrayList<Object>)service.get(i)).get(2)+s+";";
		
		}

		return returnString;
	}
	
	public ArrayList<Object> formatHotel(ArrayList<Object> hotelList)
	{
		ArrayList<Object> returnArrayList = new ArrayList<>();
		
		for(int i=0; i<hotelList.size(); i++)
		{
			ArrayList<String> temp = new ArrayList<>();
		// Hotel Name
			String hotelCode = ((ArrayList<Object>)hotelList.get(i)).get(getType("manifest")).toString();
			String[] arr = hotelCode.split(" ");
			hotelCode = arr[0];
			String hotel = "";
			for(int j=1; j<arr.length; j++)
				hotel += arr[j]+" ";
			hotel = hotel.substring(1, hotel.length()-2);
			
		// Num of Night
			String numOfNight = ((ArrayList<Object>)hotelList.get(i)).get(getType("numOfNight")).toString();
			
		// Check In and Check Out Dates
			Format formatter =  new SimpleDateFormat("MMM");
			Date checkIn = (Date) ((ArrayList<Object>)hotelList.get(i)).get(0);
			String checkInMonth = formatter.format(checkIn);		

			Calendar c = Calendar.getInstance();
			c.setTime(checkIn);
			c.add(Calendar.DATE, Integer.parseInt(numOfNight));
			Date checkOut = (Date) c.getTime();
			String checkOutMonth = formatter.format(checkOut);	
			
			String checkInOut = checkInMonth+" "+checkIn.getDate()+" - "+checkOutMonth+" "+checkOut.getDate();
			
		// meal
			String meal = ((ArrayList<Object>)hotelList.get(i)).get(4).toString().trim();			
			String s = "";
			if(meal.contains("B"))
				s += "Breakfast,";
			if(meal.contains("L"))
				s += " Lunch,";
			if(meal.contains("D"))
				s += " Dinner,";
			if(!meal.equals(""))
				meal = s.substring(0, s.length()-1);
			else
				meal = "N/A";
			
		// Room Description			
			String numOfRoom = ((ArrayList<Object>)hotelList.get(i)).get(getType("numOfRoom")).toString();
			String roomSize = ((ArrayList<Object>)hotelList.get(i)).get(getType("roomSize")).toString();
			String roomType;
			if(Integer.parseInt(numOfRoom) > 1)
				roomType = ((ArrayList<Object>)hotelList.get(i)).get(getType("roomType")).toString()+"s";
			else
				roomType = ((ArrayList<Object>)hotelList.get(i)).get(getType("roomType")).toString();
			String roomDescription = numOfRoom+" "+roomSize+" "+roomType;
			
			
			temp.add(hotel);
			temp.add(numOfNight);
			temp.add(checkInOut);
			temp.add(meal);
			temp.add(roomDescription);
			returnArrayList.add(temp);
		}
		
		
		return returnArrayList;
		
	}
	
	public ArrayList<Object> getBy(int By, int size)
	{
		String str = "";
    	ArrayList<Object> service = new ArrayList<>();
    	ArrayList<Object> temp = vouchers;//sortByDateAnd("Manifest");
    	for(int i=0; i<temp.size(); i++)
    	{
    		String type = ((ArrayList<String>)temp.get(i)).get(getType("Type"));  
    		
    		if(By == getType2(type))
    		{
    			service.add(temp.get(i));
    		}
    	}
    	
    	if(service.size() <= 0) return service;
    	 	
    	// group services by vendor
    	int count = 0;
    	String vendor = ((ArrayList<String>)service.get(0)).get(getType("Manifest")).split(" ")[0];

    	
    	ArrayList<Object> returnArrayList = new ArrayList<>();
    	ArrayList<Object> tempArrayList = new ArrayList<>();
    	for(int i=0; i<service.size(); i++)
    	{
    		String tempVendor = ((ArrayList<String>)service.get(i)).get(getType("Manifest")).split(" ")[0];
    		if (tempVendor.contains("BPE"))
    			System.out.println("");
    		if(tempVendor.equals(vendor))
    		{   			
    			count++;
    			if(count > size)
    			{
    				returnArrayList.add(tempArrayList);
    				tempArrayList = new ArrayList<>();
    				count = 1;
    			}
    			tempArrayList.add(service.get(i));
    		}
    		else
    		{
    			returnArrayList.add(tempArrayList);
    			tempArrayList = new ArrayList<>();
    			tempArrayList.add(service.get(i));
    			vendor = tempVendor;
    			count = 0;
    		}
    	}
    	returnArrayList.add(tempArrayList);   
    	return returnArrayList;
	}
	
	
	public ArrayList<Object> hotels()
	{	
		/*
    	ArrayList<Object> hotel = new ArrayList<>();
    	ArrayList<Object> temp = vouchers; //sortByDateAnd("Manifest");
    	
    	for(int i=0; i<temp.size(); i++)
    	{
    		String type = ((ArrayList<String>)temp.get(i)).get(getType("Type"));   		
    		if(type.equals("Hotel"))
    		{
    			hotel.add(temp.get(i));
    		}
    	}    
    	
    	if(hotel.size() <= 0)return hotel;

    	int count = 0;
    	String vendor = ((ArrayList<String>)hotel.get(0)).get(getType("Manifest"));
    	ArrayList<Object> returnArrayList = new ArrayList<>();
    	ArrayList<Object> tempArrayList = new ArrayList<>();
    	for(int i=0; i<hotel.size(); i++)
    	{
    		String tempVendor = ((ArrayList<String>)hotel.get(i)).get(getType("Manifest"));
    		if(tempVendor.equals(vendor))
    		{   			
    			count++;
    			if(count > 3)
    			{
    				returnArrayList.add(tempArrayList);
    				tempArrayList = new ArrayList<>();
    				count = 0;
    			}   	
    			tempArrayList.add(hotel.get(i));
    		}
    		else
    		{
    			returnArrayList.add(tempArrayList);
    			tempArrayList = new ArrayList<>();
    			tempArrayList.add(hotel.get(i));
    			vendor = tempVendor;
    			count = 0;
    		}
    	}
    	returnArrayList.add(tempArrayList);    
    	
    	return returnArrayList;	
    	 */
		return getBy(getType2("hotel"),2);
    	
	}
    
    public String vendor(Object vendor, String type)
    {
    	String returnString = "";
    	if(type.equals("Land Service"))
    	{	    	
			try{							
				String query = "select LSName,LocalPhone,Street,City,State,Country,Zipcode from Vendors where LSCode='"+vendor+"'";
				PreparedStatement pst = connection.prepareStatement(query);
				ResultSet rs = pst.executeQuery();
				
				if(rs.getString("State") == null || rs.getString("State").equals(""))
					returnString += rs.getString("LSName")+";"
										+ "Telephone: "+rs.getString("LocalPhone")+";"
											+ "Address: "+rs.getString("Street")+", "+rs.getString("City")+", "+rs.getString("Country")+", "+rs.getString("Zipcode");
				else
					returnString += rs.getString("LSName")+";"
							+ "Telephone: "+rs.getString("LocalPhone")+";"
								+ "Address: "+rs.getString("Street")+", "+rs.getString("City")+", "+rs.getString("State")+", "+rs.getString("Country")+", "+rs.getString("Zipcode");
				
				pst.close();
				rs.close();
			}catch(Exception e1){e1.printStackTrace();}
			
    	}
    	else if (type.equals("Hotel"))
    	{
			try{							
				String query = "select HotelName,LocalPhone,Street,City,State,Country,Zipcode from Hotels where HotelCode='"+vendor+"'";
				PreparedStatement pst = connection.prepareStatement(query);
				ResultSet rs = pst.executeQuery();
				
				if(rs.getString("State") == null || rs.getString("State").equals(""))
					returnString += rs.getString("HotelName")+";"
										+ "Telephone: "+rs.getString("LocalPhone")+";"
											+ "Address: "+rs.getString("Street")+", "+rs.getString("City")+", "+rs.getString("Country")+", "+rs.getString("Zipcode");
				else
					returnString += rs.getString("HotelName")+";"
							+ "Telephone: "+rs.getString("LocalPhone")+";"
								+ "Address: "+rs.getString("Street")+", "+rs.getString("City")+", "+rs.getString("State")+", "+rs.getString("Country")+", "+rs.getString("Zipcode");
				
				pst.close();
				rs.close();
			}catch(Exception e1){e1.printStackTrace();}
			
    	}
    	returnString = returnString.trim();
    	count++;
    	String s = "; ";
    	if(count == 3)
    	{
    		count = 0;
    		s = "";
    	}
    	if(returnString.substring(returnString.length()-1).equals(","))
    		return returnString.substring(0, returnString.length()-1)+s;
    	else
    		return returnString+s;
    	
    	
    }
    
    public int getType(String type)
    {
    	switch(type.toLowerCase())
    	{
    	case "manifest":
    		return 3;   	
    	case "type":
    		return 1;
    	case "issuedate":
    		return 9;
    	case "numofnight":
    		return 5;
    	case "numofroom":
    		return 7;
    	case "roomsize":
    		return 8;
    	case "roomtype":
    		return 6;
    	}
    	return -1;
    }
    public int getType2(String type)
    {
    	switch(type.toLowerCase())
    	{
    	case "hotel": return 0;
    	case "land service": return 1;
    	case "air ticket": return 1;
    	}
    	return -1;
    }
    
    public ArrayList<Object> sortByDateAnd(String type)
    {
    	int index = getType(type);
    	
    	ArrayList<String> temp2 = new ArrayList<>();
    	
    	for(int i=0; i<vouchers.size(); i++)
    	{ 		
			String man = ((ArrayList<Object>)vouchers.get(i)).get(index).toString();
			
			if(!temp2.contains(man))   				
				temp2.add(man);   		
    	}    
    	
    	ArrayList<Object> temp = new ArrayList<>();
    	    	
    	for(String s : temp2)
    	{
    		for(Object o : vouchers)
    		{
    			String x = ((ArrayList<Object>)o).get(index).toString();
    			if(s.equals(x))
    			{
    				temp.add(o);
    			}
    		}
    	}   	
    	return temp;	
    }
}














