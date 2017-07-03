package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.PostgreSQLJDBC;

public class PouchManager {

	public static void createPouch(String discriminator, String nick) throws SQLException{
		if(CharacterManager.doesCharacterExistFromDiscNick(discriminator, nick)){
			if(!pouchExists(discriminator, nick)){
				int id = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
				String sql = "INSERT INTO pouch (character_id, amount) VALUES (?, ?)";
				PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
				st.setInt(1, id);
				st.setInt(2, 0);
				st.executeUpdate();
			}
		}
	}
	
	public static boolean pouchExists(String discriminator, String nick) throws SQLException{
		int id = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		boolean exists = false;
		String sql = "SELECT id FROM pouch WHERE character_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, id);
		ResultSet rs = st.executeQuery();
		if(rs.next()){
			exists = true;
		}
		return exists;
	}
	
	public static boolean CreatePouchIfCharacterExists(String discriminator, String nick) throws SQLException{
		boolean created = false;
		if(CharacterManager.doesCharacterExistFromDiscNick(discriminator, nick)){
			if(!pouchExists(discriminator, nick)){
				createPouch(discriminator, nick);
				created = true;
			}
		}
		return created;
	}
	
	public static int getPouchContent(String discriminator, String nick) throws SQLException{
		int amount = -1;
		int id = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		if(pouchExists(discriminator, nick)){
			String sql = "SELECT amount FROM pouch WHERE character_id = ?";
			PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();
			
			if(rs.next()){
				amount = rs.getInt("amount");
			}
		}
		return amount;
	}
	
	public static void changeAmount(String discriminator, String nick, int amount, String addOrSub) throws SQLException{
		int id = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		if(pouchExists(discriminator, nick)){
			int current = getPouchContent(discriminator, nick);
			switch(addOrSub){
			case "add":
				amount = amount + current; 
				break;
			case "sub":
				amount = current - amount;
				break;
			}
			String sql = "UPDATE pouch SET amount = ? WHERE character_id = ?";
			PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
			st.setInt(1, amount);
			st.setInt(2, id);
			st.executeUpdate();
		}
	}
	
}
