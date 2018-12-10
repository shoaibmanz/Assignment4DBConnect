import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

	public static void main(String[] args) {
		
		
		
		try {
			
			String url = "jdbc:mysql://localhost:3306/Test";
			
			Class.forName ("com.mysql.cj.jdbc.Driver").newInstance ();
			
			Connection conn = DriverManager.getConnection (url, "shoaib", "shoaib");
			
			Statement statement = conn.createStatement();
			
			String query = "Select * from Student;";
			
			ResultSet set = statement.executeQuery(query);
			
			while (set.next()) {
				
				System.out.println(set.getInt(1) + set.getString(2));
				
			}
			
			conn.close();
		}
		catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (SQLException e) {
	
			e.printStackTrace();
		}

	}

}
