package objects;

import java.sql.SQLException;

import managers.InventoryManager;

public class Item {

	String name;
	int quantity;
	int categoryId;
	String categoryName;
	
	public Item(String name, int quantity, int category) {
		super();
		this.name = name.toLowerCase();
		if(quantity < 0){
			quantity = 0;
		}
		this.quantity = quantity;
		this.categoryId = category;
		try {
			this.categoryName = InventoryManager.getCategoryNameFromId(this.getCategoryId());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int category) {
		this.categoryId = category;
	}
	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String toString(){
		String ret = this.name;
		if(this.quantity > 1){
			ret += " * " + this.quantity;
		}
		return ret;
	}
	
}
