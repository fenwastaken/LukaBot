package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLJDBC {

	public static Connection cnt = null;
	public static String dbName = "lukabdd";
	public static String host = "localhost";
	public static String port = "5432";
	public static String userName = "postgres";
	public static String password = "admin";

	public static Connection getConnection() {

		if(cnt == null){
			try {
				Class.forName("org.postgresql.Driver");
				cnt = DriverManager
						.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + dbName,
								userName , password);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			System.out.println("Opened database successfully");
		}

		return cnt;
	}


	public static void closeConnection(){
		if(cnt != null){
			try {
				cnt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
