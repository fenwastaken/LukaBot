package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import database.PostgreSQLJDBC;

public class CharacterManager {

	public static void deactivateCharacterFromDiscNick(String discriminator, String nick, boolean active) throws SQLException{
		String sql = "UPDATE  character SET active = ? WHERE player_id = (SELECT id FROM player WHERE discriminator = ?) AND character_name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setBoolean(1, active);
		st.setString(2, discriminator);
		st.setString(3, nick.toLowerCase());
		st.executeUpdate();
	}

	public static void deactivateCharacterFromNick(String nick, boolean active) throws SQLException{
		String sql = "UPDATE character SET active = ? WHERE character_name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setBoolean(1, active);
		st.setString(2, nick.toLowerCase());
		st.executeUpdate();
	}

	public static void setCharacter(String name, String nick) throws SQLException{
		int playerId = PlayerManager.getPlayerIdFromDiscriminator(name);
		String sql = "INSERT INTO character (player_id, character_name, active) VALUES (?, ?, true)";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, playerId);
		st.setString(2, nick.toLowerCase());
		st.executeUpdate();
	}

	public static boolean doesCharacterExistFromNick(String nick) throws SQLException{
		String sql = "SELECT id FROM character WHERE character_name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, nick.toLowerCase());
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = true;
		}
		return ret;
	}

	public static boolean doesCharacterExistFromDiscNick(String discriminator, String nick) throws SQLException{
		int player_id = PlayerManager.getPlayerIdFromDiscriminator(discriminator);
		String sql = "SELECT id FROM character WHERE character_name = ? AND player_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
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
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, avatar);
		st.setInt(2, player_id);
		st.setString(3, character_name.toLowerCase());
		st.executeUpdate();
	}

	public static Vector<String> getAvatarless() throws SQLException{
		String sql = "SELECT character_name FROM character WHERE avatar IS NULL AND active = TRUE ORDER BY character_name";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		Vector<String> ret = new Vector<>();
		while(rs.next()){
			ret.add(rs.getString("character_name"));
		}
		return ret;

	}
	
	public static Vector<String> getAvatarfull() throws SQLException{
		String sql = "SELECT character_name FROM character WHERE avatar IS NOT NULL AND active = TRUE ORDER BY character_name";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		Vector<String> ret = new Vector<>();
		while(rs.next()){
			ret.add(rs.getString("character_name"));
		}
		return ret;

	}
	
	public static String getAvatar(String discriminator, String nick) throws SQLException{
		int id = PlayerManager.getPlayerIdFromDiscriminator(discriminator);
		String sql = "SELECT avatar FROM character WHERE character_name = ? AND player_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, nick.toLowerCase());
		st.setInt(2, id);
		ResultSet rs = st.executeQuery();
		String ret = "";
		if(rs.next()){
			ret = rs.getString("avatar");
		}
		return ret;
	}
	
	public static String getThumbnail(String discriminator, String nick) throws SQLException{
		int id = PlayerManager.getPlayerIdFromDiscriminator(discriminator);
		String sql = "SELECT thumbnail FROM character WHERE character_name = ? AND player_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, nick.toLowerCase());
		st.setInt(2, id);
		ResultSet rs = st.executeQuery();
		String ret = "";
		if(rs.next()){
			ret = rs.getString("thumbnail");
		}
		return ret;
	}

	public static int getCharacterNbFromDiscriminator(String discriminator) throws SQLException{
		String sql = "SELECT id FROM character WHERE player_id = (SELECT id FROM player WHERE discriminator = ?) AND active = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, discriminator);
		st.setBoolean(2, true);
		ResultSet rs = st.executeQuery();
		int nb = 0;
		while(rs.next()){
			nb++;
		}
		System.out.println(nb + " characters!");
		return nb;
	}

	public static int getCharacterNb() throws SQLException{
		String sql = "SELECT id FROM character WHERE active = true";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		int nb = 0;
		while(rs.next()){
			nb++;
		}
		System.out.println(nb + " characters!");
		return nb;
	}

	public static int getCharacterWAvatarNb() throws SQLException{
		String sql = "SELECT id FROM character WHERE avatar IS NOT NULL AND active = true";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		ResultSet rs = st.executeQuery();
		int nb = 0;
		while(rs.next()){
			nb++;
		}
		System.out.println(nb + " characters!");
		return nb;
	}


	public static Vector<String> getCharacterNamesFromDiscriminator(String discriminator) throws SQLException{
		String sql = "SELECT character_name FROM character WHERE player_id = (SELECT id FROM player WHERE discriminator = ?) AND character.active = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, discriminator);
		st.setBoolean(2, true);
		ResultSet rs = st.executeQuery();
		Vector<String>ret = new Vector<>();
		while(rs.next()){
			ret.add(rs.getString("character_name"));
		}
		System.out.println("RET SIZE " + ret.size());
		return ret;
	}
	
	public static int getCharacterIdFromDiscNick(String discriminator, String nick) throws SQLException{
		String sql = "SELECT id FROM character WHERE player_id = (SELECT id FROM player WHERE discriminator = ?) AND character.active = ? AND character_name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, discriminator);
		st.setBoolean(2, true);
		st.setString(3, nick.toLowerCase());
		ResultSet rs = st.executeQuery();
		int ret = -2;
		while(rs.next()){
			ret = rs.getInt("id");
		}
		return ret;
	}
	
	public static boolean hasPlayerAvatar(String discriminator) throws SQLException{
		String sql = "SELECT avatar FROM character WHERE player_id = (SELECT id FROM player WHERE discriminator = ?)";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, discriminator);
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		while(rs.next()){
			String avatar = rs.getString("avatar");
			if(avatar != null && avatar.length() > 0){
				ret = true;
			}
		}
		return ret;
	}
	
	public static boolean hasThumbnail(String discriminator, String nick) throws SQLException{
		String sql = "SELECT thumbnail FROM character WHERE player_id = (SELECT id FROM player WHERE discriminator = ?) AND character_name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, discriminator);
		st.setString(2, nick.toLowerCase());
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		while(rs.next()){
			String avatar = rs.getString("thumbnail");
			if(avatar != null && avatar.length() > 0){
				ret = true;
			}
		}
		return ret;
	}
	
	public static String getInventoryStateFromDisNick(String discriminator, String nick) throws SQLException{
		String sql = "SELECT inventory_state FROM character WHERE player_id = (SELECT id FROM player WHERE discriminator = ?) AND character_name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, discriminator);
		st.setString(2, nick.toLowerCase());
		String state = "";
		ResultSet rs = st.executeQuery();
		if(rs.next()){
			state = rs.getString("inventory_state");
		}
		return state;
	}
	
	public static void updateInventoryState(String discriminator, String nick, String state) throws SQLException{
		int characterId = getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "UPDATE public.character SET inventory_state = ? WHERE id = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, state);
		st.setInt(2, characterId);
		st.executeUpdate();
	}
	
	public static void setThumbnail(String discriminator, String nick, String thumbnail) throws SQLException{
		int characterId = getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "UPDATE public.character SET thumbnail = ? WHERE id = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, thumbnail);
		st.setInt(2, characterId);
		st.executeUpdate();
	}

}
