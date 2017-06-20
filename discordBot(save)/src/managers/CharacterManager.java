package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.PostgreSQLJDBC;

public class CharacterManager {

	public static void setCharacter(String name, String nick) throws SQLException{
		int playerId = PlayerManager.getPlayerIdFromDiscriminator(name);
		String sql = "INSERT INTO character (player_id, character_name) VALUES (?, ?)";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setInt(1, playerId);
		st.setString(2, nick.toLowerCase());
		st.executeUpdate();
	}

	public static boolean doesCharacterExistFromNick(String nick) throws SQLException{
		String sql = "SELECT id FROM character WHERE character_name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick.toLowerCase());
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = true;
		}
		return ret;
	}
	
	public static boolean doesCharacterExistFromNick(String discriminator, String nick) throws SQLException{
		int player_id = PlayerManager.getPlayerIdFromDiscriminator(discriminator);
		String sql = "SELECT id FROM character WHERE character_name = ? AND player_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick.toLowerCase());
		st.setInt(2, player_id);
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = true;
		}
		return ret;
	}

	public static void setAvatar(String discriminator, String character_name, String avatar) throws SQLException{
		int player_id = PlayerManager.getPlayerIdFromDiscriminator(discriminator);
		String sql = "UPDATE character SET avatar = ? WHERE player_id = ? AND character_name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, avatar);
		st.setInt(2, player_id);
		st.setString(3, character_name.toLowerCase());
		st.executeUpdate();
	}
	
	public static String getAvatar(String nick) throws SQLException{
		String sql = "SELECT avatar FROM character WHERE character_name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnexion().prepareStatement(sql);
		st.setString(1, nick.toLowerCase());
		ResultSet rs = st.executeQuery();
		String ret = "";
		if(rs.next()){
			ret = rs.getString("avatar");
		}
		return ret;
	}

}
