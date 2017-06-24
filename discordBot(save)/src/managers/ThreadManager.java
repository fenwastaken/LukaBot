package managers;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.PostgreSQLJDBC;

public class ThreadManager {

	public static int countRow() throws SQLException{
		String sql = "SELECT COUNT(*) AS total FROM thread;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		int ret = -2;
		if(rs.next()){
			ret = rs.getInt("total");
		}
		return ret;

	}
	
	public static int setThread(String thread) throws SQLException{
		String sql = "INSERT INTO thread(link)VALUES (?);";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, thread);
		int ret = st.executeUpdate();
		return ret;
	}
	
	public static String getThread(int id) throws SQLException{
		String sql = "SELECT link FROM thread WHERE id = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		String ret = "none";
		if(rs.next()){
			ret = rs.getString("link");
		}
		return ret;
	}
	
}
