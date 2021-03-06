/*
 * This file is part of MineSweeper
 * Copyright (C) 2015-2019 Richard R. Zheng
 *
 * https://github.com/rzheng95/MineSweeper
 * 
 * All Right Reserved.
 */

package com.rzheng.explorient;

import java.sql.*;
import javax.swing.*;

public class sqliteConnection 
{
	
	Connection conn = null;
	
	public static Connection dbConnector(String db)
	{
		try{
			Class.forName("org.sqlite.JDBC");
			//Connection conn = DriverManager.getConnection("jdbc:sqlite:Y:\\Users\\Richard\\Dropbox\\Database\\"+db+".sqlite");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:"+db);
			//JOptionPane.showMessageDialog(null, "Connection Successful!");
			return conn;
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}
	
	public static String processURL(String url)
	{
		for(int i=0; i<url.length(); i++)
		{
			String alpha  = url.substring(i, i+1);
			if(url.substring(i, i+1).equals("\\"))
			{				
				url = url.substring(0, i) + "\\" + url.substring(i);
				i+=2;
			}
		}
		return "jdbc:sqlite:"+url;
	}
}
