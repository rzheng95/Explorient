package Explorient;
import java.sql.*;
import javax.swing.*;

public class sqliteConnection 
{
	
	Connection conn = null;
	
	
	public static Connection dbConnector(String db)
	{
		try{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:Y:\\Users\\Richard\\Dropbox\\Database\\"+db+".sqlite");
			//JOptionPane.showMessageDialog(null, "Connect Successful!");
			return conn;
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}
}
