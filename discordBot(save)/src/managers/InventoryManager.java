package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.iwebpp.crypto.TweetNaclFast.poly1305;

import database.PostgreSQLJDBC;
import objects.Item;

public class InventoryManager {
	
	public static void initiateInventory(String discriminator, String nick) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		if(!inventoryExists(characterId)){
			boundCategoryToCharacter(discriminator, nick, "equipped", 1);
			boundCategoryToCharacter(discriminator, nick, "saddlebag", 2);
			CharacterManager.updateInventoryState(discriminator, nick, "u");
		}
	}
	
	public static boolean inventoryExists(int characterId) throws SQLException{
		String sql = "SELECT id FROM inventory_custom WHERE character_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = true;
		}
		return ret;
	}
	
	public static Vector<String> getAllCustomCategoryNamesFromCharId(String discriminator, String nick) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "SELECT category_id FROM inventory_custom WHERE character_id = ? ORDER BY position";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		ResultSet rs = st.executeQuery();
		Vector<String> ret = new Vector<String>();
		while(rs.next()){
			int categoryId = rs.getInt("category_id");
			String category = getCategoryNameFromId(categoryId);
			ret.add(category);
		}
		return ret;
	}
	

	
	public static int getItemId(String discriminator, String nick, String category, String name) throws SQLException{
		int categoryId = InventoryManager.getCategoryIdFromName(category);
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "SELECT id FROM public.inventory WHERE character_id = ? AND name = ? AND category_id = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.setString(2, name);
		st.setInt(3, categoryId);
		int ret = -1;
		ResultSet rs = st.executeQuery();
		if(rs.next()){
			ret = rs.getInt("id");
		}
		return ret;
	}
	
	public static int getItemQty(String discriminator, String nick, String category, String name) throws SQLException{
		int categoryId = InventoryManager.getCategoryIdFromName(category);
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "SELECT quantity FROM public.inventory WHERE character_id = ? AND name = ? AND category_id = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.setString(2, name);
		st.setInt(3, categoryId);
		int ret = -1;
		ResultSet rs = st.executeQuery();
		if(rs.next()){
			ret = rs.getInt("quantity");
		}
		return ret;
	}
	
	public static int updateItem(String discriminator, String nick, String name, int quantity, String category) throws SQLException{
		int itemId = InventoryManager.getItemId(discriminator, nick, category, name);
		int oldQty = InventoryManager.getItemQty(discriminator, nick, category, name);
		int newQty = oldQty + quantity;
		
		if(newQty > 0){
			String sql = "UPDATE public.inventory SET quantity = ? WHERE id = ?;";
			PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
			st.setInt(1, newQty);
			st.setInt(2, itemId);
			st.executeUpdate();
			return 1;
		}
		else{
			remItem(discriminator, nick, name, category);
			return 0;
		}
	}
	
	public static int addItem(String discriminator, String nick, String name, int quantity, String category) throws SQLException{
		int categoryId = InventoryManager.getCategoryIdFromName(category);
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "INSERT INTO public.inventory(character_id, name, quantity, category_id)VALUES (?, ?, ?, ?);";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.setString(2, name);
		st.setInt(3, quantity);
		st.setInt(4, categoryId);
		st.execute();
		return 1;
	}
	
	public static void remItem(String discriminator, String nick, String name, String category) throws SQLException{
		int itemId = InventoryManager.getItemId(discriminator, nick, category, name);
		String sql = "DELETE FROM public.inventory WHERE id = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, itemId);
		st.execute();
	}
	
	public static int addUpdateItem(String discriminator, String nick, String name, int quantity, String category) throws SQLException{
		int ret = -1;
		if(doesItemExist(discriminator, nick, name, category)){
			ret = updateItem(discriminator, nick, name, quantity, category);
		}
		else{
			ret = addItem(discriminator, nick, name, quantity, category);
		}
		return ret;
	}
	
	public static boolean doesItemExist(String discriminator, String nick, String name, String category) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		int categoryId = InventoryManager.getCategoryIdFromName(category);
		String sql = "SELECT id FROM public.inventory WHERE character_id = ? AND name = ? AND category_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.setString(2, name);
		st.setInt(3, categoryId);
		boolean exists = false;
		ResultSet rs = st.executeQuery();
		if(rs.next()){
			exists = true;
		}
		return exists;
	}
	
	public static Vector<Item> getAllItems(String discriminator, String nick) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "SELECT  name, quantity, category_id FROM public.inventory WHERE character_id = ? ORDER BY name;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		ResultSet rs = st.executeQuery();
		Vector<Item> ret = new Vector<Item>();
		while(rs.next()){
			String name = rs.getString("name");
			int qty = rs.getInt("quantity");
			int categoryId = rs.getInt("category_id");
			Item it = new Item(name, qty, categoryId);
			ret.add(it);
		}
		return ret;
	}
	
	public static Vector<Item> getAllItemsOfCategory(String discriminator, String nick, String category) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		int categoryId = InventoryManager.getCategoryIdFromName(category);
		String sql = "SELECT  name, quantity FROM public.inventory WHERE character_id = ? AND category_id = ? ORDER BY name;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.setInt(2, categoryId);
		ResultSet rs = st.executeQuery();
		Vector<Item> ret = new Vector<Item>();
		while(rs.next()){
			String name = rs.getString("name");
			int qty = rs.getInt("quantity");
			Item it = new Item(name, qty, categoryId);
			ret.add(it);
		}
		return ret;
	}

	public static void clearInventory(String discriminator, String nick) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "DELETE FROM public.inventory WHERE character_id = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.execute();
	}
	
	public static void clearInventoryOfCategory(String discriminator, String nick, String category) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		int categoryId = InventoryManager.getCategoryIdFromName(category);
		String sql = "DELETE FROM public.inventory WHERE character_id = ? AND category_id = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.setInt(2, categoryId);
		st.execute();
	}
	
	public static boolean hasCategoryFromdisNick(String discriminator, String nick, String category) throws SQLException{
		Vector<String> vec = getAllCustomCategoryNamesFromCharId(discriminator, nick);
		System.out.println("CATEGORY FROM DISC NIC" + vec.toString() + " {"+category+"}");
		return vec.contains(category);
	}
	
	public static boolean doesCategoryExist(String name) throws SQLException{
		String sql = "SELECT id FROM inventory_category WHERE name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, name.toLowerCase());
		boolean exists = false;
		ResultSet rs = st.executeQuery();
		if(rs.next()){
			exists = true;
		}
		return exists;
	}
	
	public static void addCategory(String name, boolean default_category) throws SQLException{
		String sql = "INSERT INTO inventory_category (name, \"default\") VALUES (?, ?);";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, name);
		st.setBoolean(2, default_category);
		st.execute();
	}
	
	public static int getCategoryIdFromName(String category) throws SQLException{
		String sql = "SELECT id FROM inventory_category WHERE name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, category);
		ResultSet rs = st.executeQuery();
		int id = -1;
		if(rs.next()){
			id = rs.getInt("id");
		}
		return id;		
	}
	
	public static String getCategoryNameFromId(int categoryId) throws SQLException{
		String sql = "SELECT name FROM inventory_category WHERE id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, categoryId);
		ResultSet rs = st.executeQuery();
		String name = "none";
		if(rs.next()){
			name = rs.getString("name");
		}
		return name;		
	}
	
	public static boolean getCategoryTypeFromName(String category) throws SQLException{
		String sql = "SELECT \"default\" FROM inventory_category WHERE name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, category);
		ResultSet rs = st.executeQuery();
		boolean ret = false;
		if(rs.next()){
			ret = rs.getBoolean("default");
		}
		return ret;		
	}
	
	public static void boundCategoryToCharacter(String discriminator, String nick, String category, int position) throws SQLException{
		int categoryId = getCategoryIdFromName(category);
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "INSERT INTO public.inventory_custom(category_id, character_id, position) VALUES (?, ?, ?)";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, categoryId);
		st.setInt(2, characterId);
		st.setInt(3, position);
		st.execute();
	}
	
	public static int getNextCategoryPosition(String discriminator, String nick) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "SELECT position FROM inventory_custom WHERE character_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		int ret = 0;
		ResultSet rs = st.executeQuery();
		while(rs.next()){
			int position = rs.getInt("position");
			if(position > ret){
				ret = position;
			}
		}
		ret ++;
		return ret;
	}
	
	public static int getLastCategoryPosition(String discriminator, String nick) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "SELECT position FROM inventory_custom WHERE character_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		int ret = 0;
		ResultSet rs = st.executeQuery();
		while(rs.next()){
			int position = rs.getInt("position");
			if(position > ret){
				ret = position;
			}
		}
		return ret;
	}
	
	public static int getCustomCategoryPosition(String discriminator, String nick, String category) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		int categoryId = InventoryManager.getCategoryIdFromName(category);
		String sql = "SELECT position FROM inventory_custom WHERE character_id = ? AND category_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.setInt(2, categoryId);
		int ret = 0;
		ResultSet rs = st.executeQuery();
		if(rs.next()){
			ret = rs.getInt("position");
		}
		return ret;
	}
	
	public static int getCustomCategoryId(String discriminator, String nick, String category) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		int categoryId = InventoryManager.getCategoryIdFromName(category);
		String sql = "SELECT id FROM inventory_custom WHERE character_id = ? AND category_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.setInt(2, categoryId);
		int ret = 0;
		ResultSet rs = st.executeQuery();
		if(rs.next()){
			ret = rs.getInt("id");
		}
		return ret;
	}
	
	public static int getCategoryIdFromPosition(String discriminator, String nick, int position) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "SELECT category_id FROM inventory_custom WHERE character_id = ? AND position = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.setInt(2, position);
		int ret = 0;
		ResultSet rs = st.executeQuery();
		if(rs.next()){
			ret = rs.getInt("category_id");
		}
		return ret;
	}
	
	public static void updateCategoryposition(String discriminator, String nick, int categoryId, int position) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "UPDATE public.inventory_custom SET \"position\" = ? WHERE id = ? AND character_id = ?;";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, position);
		st.setInt(2, categoryId);
		st.setInt(3, characterId);
		st.executeUpdate();
	}
	
	public static boolean moveCategory(String discriminator, String nick, String category, int move) throws SQLException{
		int categoryTomove = InventoryManager.getCustomCategoryId(discriminator, nick, category);
		int categoryPosition = InventoryManager.getCustomCategoryPosition(discriminator, nick, category);
		int newPosition = categoryPosition + move;
		if(newPosition > 0 && newPosition <= getLastCategoryPosition(discriminator, nick)){
			int categoryAtThatPosition = InventoryManager.getCategoryIdFromPosition(discriminator, nick, newPosition);
			InventoryManager.updateCategoryposition(discriminator, nick, categoryTomove, newPosition);
			InventoryManager.updateCategoryposition(discriminator, nick, categoryAtThatPosition, categoryPosition);
			return true;
		}
		return false;	
	}
	
	public static void deleteAllItemsOfACategory(String discriminator, String nick, String category) throws SQLException{
		int categoryId = InventoryManager.getCategoryIdFromName(category);
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "DELETE FROM inventory WHERE category_id = ? AND character_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, categoryId);
		st.setInt(2, characterId);
		st.execute();
	}
	
	public static void deleteCustomCategory(String discriminator, String nick, String category) throws SQLException{
		int customCategoryId = InventoryManager.getCategoryIdFromName(category);
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "DELETE FROM inventory_custom WHERE category_id = ? AND character_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, customCategoryId);
		st.setInt(2, characterId);
		st.execute();
	}
	
	public static boolean isCategoryEmpty(String discriminator, String nick, String category) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		int categoryId = InventoryManager.getCategoryIdFromName(category);
		String sql = "SELECT id FROM inventory WHERE character_id = ? AND category_id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, characterId);
		st.setInt(2, categoryId);
		boolean exists = true;
		ResultSet rs = st.executeQuery();
		while(rs.next()){
			exists = false;
		}
		return exists;
	}
	
	
}
