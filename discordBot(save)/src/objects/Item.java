package objects;

public class Item {

	String name;
	int quantity;
	
	public Item(String name, int quantity) {
		super();
		this.name = name.toLowerCase();
		if(quantity < 0){
			quantity = 0;
		}
		this.quantity = quantity;
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
	
	public String toString(){
		String ret = this.name;
		if(this.quantity > 1){
			ret += " * " + this.quantity;
		}
		return ret;
	}
	
}
