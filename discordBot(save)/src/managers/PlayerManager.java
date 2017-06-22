package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import database.PostgreSQLJDBC;

public class PlayerManager {

	public static void setPlayer(String discriminator, String name) throws SQLException{
		String sql = "INSERT INTO player (discriminator, name, date, active) VALUES (?, ?, CURRENT_DATE, ?)";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, discriminator);
		st.setString(2, name.toLowerCase());
		st.setBoolean(3, true);
		st.executeUpdate();
	}
	
	public static boolean playerExistsFromID(int id) throws SQLException{
		String sql = "SELECT name FROM player WHERE id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = true;
		}
		return ret;
	}
	
	public static boolean playerExistsFromDiscriminator(String discriminator) throws SQLException{
		String sql = "SELECT id FROM player WHERE discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, discriminator);
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = true;
		}
		return ret;
	}
	
	public static int getPlayerIdFromDiscriminator(String discriminator) throws SQLException{
		String sql = "SELECT id FROM player WHERE discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, discriminator);
		ResultSet rs = st.executeQuery();
		int ret = -2;
		if(rs.next()){
			ret = rs.getInt("id");
		}
		return ret;
	}
	
	public static String getDiscriminatorFromName(String name) throws SQLException{
		String sql = "SELECT discriminator FROM player WHERE name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, name.toLowerCase());
		ResultSet rs = st.executeQuery();
		String ret = "";
		if(rs.next()){
			ret = rs.getString("discriminator");
		}
		return ret;
	}
	
	public static void setPlayer4c(String discriminator, String fc) throws SQLException{
		String sql = "UPDATE player SET fc = ? WHERE discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, fc);
		st.setString(2, discriminator);
		st.executeUpdate();
	}
	
	public static String getPlayer4c(String discriminator) throws SQLException{
		String sql = "SELECT fc FROM player WHERE Discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1,  discriminator);
		ResultSet rs = st.executeQuery();
		String ret = "";
		if(rs.next()){
			ret = rs.getString("fc");
		}
		return ret;
	}
	
}
