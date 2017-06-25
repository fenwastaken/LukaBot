package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.PostgreSQLJDBC;

public class UrlsManager {

	public static void updateLink(String link, String name) throws SQLException{
		String sql = "UPDATE urls SET link = ? WHERE name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, link);
		st.setString(2, name);
		st.executeUpdate();
	}
	
	public static void setLink(String name, String link) throws SQLException{
		String sql = "INSERT INTO urls (name, link) VALUES (?, ?);";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, name);
		st.setString(2, link);
		st.executeUpdate();
	}
	
	public static String getLinkFromName(String name) throws SQLException{
		String sql = "SELECT link FROM urls WHERE name = ? ;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, name);
		ResultSet rs = st.executeQuery();
		String ret = "";
		if(rs.next()){
			ret = rs.getString("link");
		}
		return ret;
	}
	
	public static String getAllNames() throws SQLException{
		String sql = "SELECT name FROM urls;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		String ret = "";
		while(rs.next()){
			ret += rs.getString("name") + ", ";
		}
		if(ret.length() > 0){
			ret = ret.substring(0, ret.length() - 2) + ".";
		}
		return ret;
	}
	
	public static boolean exists(String name) throws SQLException{
		String sql = "SELECT link FROM urls WHERE name = ? ;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, name);
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = true;
		}
		return ret;
	}
	
}
