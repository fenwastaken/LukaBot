package cards;

public class Card {

	String color;
	String strValue;
	String fullName;
	String shortName;
	int value;

	public Card(String color, int value){
		this.color = color;
		this.value = value;

		if(value > 10){
			switch (value){
			case 11:
				this.strValue = "Jack";
				break;
			case 12:
				this.strValue = "Queen";
				break;
			case 13:
				this.strValue = "King";
				break;
			case 14:
				this.strValue = "Ace";
			break;
			}
		}
		else{
			strValue = value + "";
		}
		
		this.fullName = strValue + " of " + color;
		
		int len = 0;
		
		if(strValue.length() < 3){
			len = strValue.length();
		}
		else{
			len = 1;
		}
		this.shortName = color.substring(0, 1) + strValue.substring(0, len);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}


}
