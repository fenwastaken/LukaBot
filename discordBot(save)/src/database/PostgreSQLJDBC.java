package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLJDBC {

	public static Connection cnt = ;
	public static String dbName = "";
	public static String host = "";
	public static String port = "";
	public static String userName = "";
	public static String password = "";

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
