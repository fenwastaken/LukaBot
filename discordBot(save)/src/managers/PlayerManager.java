package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import database.PostgreSQLJDBC;

public class PlayerManager {
	
	

	public static int getPlayerNb() throws SQLException{
		String sql = "SELECT id FROM player";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		int ret = 0;
		while(rs.next()){
			ret++;
		}
		return ret;
	}
	
	public static int getLegitPlayerNb() throws SQLException{
		String sql = "SELECT DISTINCT player.id FROM player join character on player.id = player_id WHERE character.avatar IS NOT NULL ORDER by id;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		int ret = 0;
		while(rs.next()){
			ret++;
		}
		return ret;
	}
	
	public static void setPlayer(String discriminator, String name) throws SQLException{
		String sql = "INSERT INTO player (discriminator, name, date, active, user_rank) VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, discriminator);
		st.setString(2, name.toLowerCase());
		st.setBoolean(3, true);
		st.setInt(4, 4);
		st.executeUpdate();
	}
	
	public static boolean playerExistsFromID(int id) throws SQLException{
		String sql = "SELECT name FROM player WHERE id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
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
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
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
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
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
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, name.toLowerCase());
		ResultSet rs = st.executeQuery();
		String ret = "";
		if(rs.next()){
			ret = rs.getString("discriminator");
		}
		return ret;
	}
	
	
	public static void updatePlayerDate(String discriminator) throws SQLException{
		String sql = "UPDATE player SET last_seen = CURRENT_TIMESTAMP WHERE discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, discriminator);
		st.executeUpdate();
	}

	public static void updatePlayerName(String name, String discriminator) throws SQLException{
		String sql = "UPDATE player SET name = ? WHERE discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, name);
		st.setString(2, discriminator);
		st.executeUpdate();
	}
	
	public static void updatePlayerRank(int rank, String discriminator) throws SQLException{
		String sql = "UPDATE player SET user_rank = ? WHERE discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, rank);
		st.setString(2, discriminator);
		st.executeUpdate();
	}
	
	public static int getPlayerRank(String discriminator) throws SQLException{
		String sql = "SELECT user_rank FROM player WHERE discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, discriminator);
		ResultSet rs = st.executeQuery();
		int rank = -2;
		if(rs.next()){
			rank = rs.getInt("user_rank");
		}
		return rank;
	}
	
	public static String getDate(String name) throws SQLException{
		String sql = "SELECT name, DATE_TRUNC('seconds', CURRENT_TIMESTAMP - last_seen) AS time FROM player WHERE discriminator = ? ORDER BY time;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, name);
		ResultSet rs = st.executeQuery();
		String ret = "";
		if(rs.next()){
			ret = rs.getString("time");
		}
		return ret;
	}
}
