package cards;

import java.util.Collections;
import java.util.Vector;

import handy.Tools;

public class Deck {

	String name;
	Vector<Card> vecCard = new Vector<Card>();

	public Deck(String name){
		this.name = name;

		for(int i = 1; i <= 4; i++){
			String color = "";
			switch(i){
			case 1:
				color = "Heart";
				break;
			case 2:
				color = "Diamonds";
				break;
			case 3:
				color = "Spades";
				break;
			case 4:
				color = "Clover";
				break;
			}
			for(int j = 2; j <= 14; j++){
				Card card = new Card(color, j);
				System.out.println("full name: " + card.getFullName());
				System.out.println("Short name: " + card.getShortName());
				System.out.println("Value: " + card.getValue());
				vecCard.add(card);
			}
		}
	}

	public void present(){
		Tools.sendMessage(vecCard.size() + " cards:");
		String ret = "";
		for(Card c : vecCard){
			ret += c.getShortName() + ", ";
		}
		Tools.sendMessage(Tools.codeBlock(ret));
		ret = "";
		for(Card c : vecCard){
			ret += c.getFullName() + ", ";
		}
		Tools.sendMessage(Tools.codeBlock(ret));
	}

	public void addCard(Card c){
		this.getVecCard().add(c);
	}

	public void removeCard(Card c){
		this.getVecCard().removeElement(c);
	}

	public void shuffle(){
		Collections.shuffle(getVecCard());
	}

	public int count(){
		return getVecCard().size();
	}

	public String reveal(){

		Vector<String> vec = new Vector<>();
		for(Card c : getVecCard()){
			vec.add(c.getShortName());
		}

		Collections.sort(vec);

		String ret = "```";
		for(String str : vec){
			ret += str + ", ";
		}

		ret = ret.substring(0, ret.length()-2) + ".";
		ret += "```";
		return ret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector<Card> getVecCard() {
		return vecCard;
	}

	public void setVecCard(Vector<Card> vecCard) {
		this.vecCard = vecCard;
	}



}
