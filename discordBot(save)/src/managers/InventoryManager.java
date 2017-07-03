package managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import database.PostgreSQLJDBC;
import objects.Item;

public class InventoryManager {
	
	public static String remItem(String discriminator, String item, int quantity) throws SQLException{
		String ret = "not found";
		int id = getObjectIdFromInventory(discriminator, item);
		if(id > 0){
			Item it = getObjectFromInventory(discriminator, item);
			quantity = it.getQuantity() - quantity;
			if(quantity <= 0){
				delItem(item, discriminator);
				ret = "deleted";
			}
			else{
				updateObjectNb(quantity, id);
				ret = "substracted";
			}
			
		}
		return ret;
	}
	
	public static void delItem(String name, String discriminator) throws SQLException{
		String sql = "DELETE FROM inventory WHERE name = ? AND discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, name);
		st.setString(2, discriminator);
		st.executeUpdate();
	}
	
	public static void addItem(String discriminator, String item, int quantity) throws SQLException{
		int id = getObjectIdFromInventory(discriminator, item);
		if(id > 0){
			Item it = getObjectFromInventory(discriminator, item);
			quantity += it.getQuantity();
			updateObjectNb(quantity, id);
		}
		else{
			String sql = "INSERT INTO inventory (discriminator, name, quantity) VALUES (?, ?, ?)";
			PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
			st.setString(1, discriminator);
			st.setString(2, item.toLowerCase());
			st.setInt(3, quantity);
			st.executeUpdate();
		}

	}
	
	public static boolean clearItems(String discriminator) throws SQLException{
		boolean worked = false;
			String sql = "DELETE FROM inventory WHERE discriminator = ?";
			PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
			st.setString(1, discriminator);
			int op = st.executeUpdate();
			if(op != 0){
				worked = true;
			}
		return worked;
	}
	
	public static String getInventory(String discriminator) throws SQLException{
		String sql = "SELECT name, quantity FROM inventory WHERE discriminator = ? ORDER BY NAME";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, discriminator);
		ResultSet rs = st.executeQuery();
		Vector<String> vec = new Vector<String>();
		while(rs.next()){
			String item = rs.getString("name");
			int qty = Integer.parseInt(rs.getString("quantity"));
			Item it = new Item(item, qty);
			vec.add(it.toString());
		}
		return vec.toString();
	}
	
	public static Item getObjectFromInventory(String discriminator, String item) throws SQLException{
		String sql = "SELECT name, quantity FROM inventory WHERE name = ? and discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, item);
		st.setString(2, discriminator);
		ResultSet rs = st.executeQuery();
		Item it = null;
		if(rs.next()){
			String n = rs.getString("name");
			int i = rs.getInt("quantity");
			 it = new Item(n, i);
		}
		return it;
	}
	
	public static int getObjectIdFromInventory(String discriminator, String item) throws SQLException{
		String sql = "SELECT id, quantity FROM inventory WHERE name = ? and discriminator = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setString(1, item);
		st.setString(2, discriminator);
		ResultSet rs = st.executeQuery();
		int ret = -2;
		if(rs.next()){
			ret = rs.getInt("id");
		}
		return ret;
	}
	
	public static void updateObjectNb(int quantity, int id) throws SQLException{
		String sql = "UPDATE inventory SET quantity = ? WHERE id = ?";
		PreparedStatement st = PostgreSQLJDBC.getConnection().prepareStatement(sql);
		st.setInt(1, quantity);
		st.setInt(2, id);
		st.executeUpdate();
	}
	
}
