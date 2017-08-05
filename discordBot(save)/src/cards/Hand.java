package cards;

import java.util.Collections;
import java.util.Vector;

public class Hand {

	String nick;
	String discriminator;
	Deck boundDeck;
	Vector<Card> vHand = new Vector<Card>();
	
	public Hand(String nick, String discriminator, Deck boundDeck) {
		super();
		this.nick = nick;
		this.discriminator = discriminator;
		this.boundDeck = boundDeck;
	}

	public void add(Card c){
		getvHand().add(c);
	}
	
	public void remove(Card c){
		getvHand().removeElement(c);
	}
	
	public String reveal(){

			Vector<String> vec = new Vector<>();
			for(Card c : getvHand()){
				vec.add(c.getShortName());
			}
			
			Collections.sort(vec);
			
			String ret = "```";
			for(String str : vec){
				ret += str + ", ";
			}
			
			ret = ret.substring(0, ret.length()-2);
			ret += "```";
			
			if(vHand.isEmpty()){
				ret = "empty";
			}
			
			return ret;
		
	}
	
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

	public Vector<Card> getvHand() {
		return vHand;
	}

	public void setvHand(Vector<Card> vHand) {
		this.vHand = vHand;
	}
	
	
	
}
