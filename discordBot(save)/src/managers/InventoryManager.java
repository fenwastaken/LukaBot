package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.iwebpp.crypto.TweetNaclFast.poly1305;

import database.PostgreSQLJDBC;
import objects.Item;

public class InventoryManager {
	
	public static int getCategoryIdFromName(String categoryName) throws SQLException{
		String sql = "SELECT id FROM inventory_category WHERE name = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, categoryName);
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
	
	public static void addCategory(String discriminator, String nick, String category, int position) throws SQLException{
		int categoryId = getCategoryIdFromName(category);
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		String sql = "INSERT INTO public.inventory_custom(category_id, character_id, position) VALUES (?, ?, ?)";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, categoryId);
		st.setInt(2, characterId);
		st.setInt(3, position);
		st.execute();
	}
	
	public static void initiateInventory(String discriminator, String nick) throws SQLException{
		int characterId = CharacterManager.getCharacterIdFromDiscNick(discriminator, nick);
		if(!inventoryExists(characterId)){
			addCategory(discriminator, nick, "equipped", 1);
			addCategory(discriminator, nick, "saddlebag", 2);
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
	
	public static Vector<String> getAllCustomCategoriesFromCharId(String discriminator, String nick) throws SQLException{
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
	
	public static boolean hasCategoryFromdisNick(String discriminator, String nick, String category) throws SQLException{
		Vector<String> vec = getAllCustomCategoriesFromCharId(discriminator, nick);
		return vec.contains(category);
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
			remItem(discriminator, nick, name, quantity, category);
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
	
	public static void remItem(String discriminator, String nick, String name, int quantity, String category) throws SQLException{
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
	
}
