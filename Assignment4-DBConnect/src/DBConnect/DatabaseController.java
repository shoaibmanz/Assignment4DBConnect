package DBConnect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.spi.SyncProviderException;

public class DatabaseController {
	
	private static Connection connection;
	
	private static HashMap<SupportedDB, String> classNames = new HashMap<SupportedDB, String>();
	private static HashMap<SupportedDB, String> urlPrefix = new HashMap<SupportedDB, String>();
	
	static {
		
		classNames.put(SupportedDB.MySQL, "com.mysql.cj.jdbc.Driver");
		urlPrefix.put(SupportedDB.MySQL, "jdbc:mysql:");
		
/*		String url = "jdbc:mysql://localhost:3306/Test";
		
		try {
			Class.forName ("com.mysql.cj.jdbc.Driver");
			
			connection = DriverManager.getConnection (url, "shoaib", "shoaib");
			connection.setAutoCommit(false);
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}  catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
*/
	}
	
	public static void MakeConnection(SupportedDB database, String dbname, String hostname, String port, String username, String password) throws Exception {
		
		if (connection != null) {
			connection.close();
		}

		 Class.forName(classNames.get(database)).newInstance();
		  
		 String url = urlPrefix.get(database) + "//" + hostname + ":" + port + "/" + dbname;
		  
		 connection = DriverManager.getConnection (url, username, password);
		 connection.setAutoCommit(false);
	}
	
	public static DatabaseMetaData getMetadata() throws SQLException {
			
		return connection.getMetaData();	
	}
	
	public static String getCatalog() throws SQLException {
		return connection.getCatalog();
	}

	
	public static ResultSet fetchQuery(String SQLQuery) throws SQLException {
		
		Statement stmt = connection.createStatement();		
		return stmt.executeQuery(SQLQuery);
	}
	
	public static void acceptChanges(CachedRowSet rowset) throws SQLException {
		
		rowset.acceptChanges(connection);
	}
}
/*		Statement statement = conn.createStatement();

String query = "Select * from Student;";

ResultSet set = statement.executeQuery(query);

while (set.next()) {
	
	System.out.println(set.getInt(1) + set.getString(2));
	
}

conn.close();*/