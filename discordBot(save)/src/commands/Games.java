package commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.plaf.synth.SynthDesktopIconUI;

import org.apache.http.impl.nio.ssl.SSLClientIOEventDispatch;

import annotations.BotCom;
import annotations.ComCategory;
import annotations.ComLvl;
import annotations.ComType;
import annotations.Comparison;
import cards.Card;
import cards.Deck;
import cards.Hand;
import handy.Handler;
import handy.Tools;
import objects.Folk;
import objects.FolkBox;

public class Games {

	//hangman
	String word;
	static Vector<String> vec;
	Boolean[] bool;
	String tried;
	String lastDiscriminator;

	//deck
	Deck deck = null;
	Vector<Hand> vecHand = null;
	Deck discarded = null;

	public Games(){
		word = "";
		tried = "";
		vec = new Vector<String>();
		bool = new Boolean[0];
		lastDiscriminator = "";
	}


	//cards
	@BotCom(command = Handler.MAKE_DECK , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void makeDeck(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.MAKE_DECK, Comparison.EQUALS, ComLvl.PLAYER)){
			if(Handler.channel.getName().equals(Handler.CHAN_FUN) || Handler.channel.getName().equals(Handler.CHAN_MAIN)){
				deck = new Deck("default");
				vecHand = new Vector<Hand>();
				discarded = new Deck("discarded");
				deck.shuffle();
				Tools.sendMessage("Deck was reset! (" + deck.getVecCard().size() + " cards).");
			}
		}
	}

	@BotCom(command = Handler.REVEAL , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void revealDeck(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.REVEAL, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(Handler.channel.getName().equals(Handler.CHAN_FUN) || Handler.channel.getName().equals(Handler.CHAN_MAIN)){
				if(fb.getArguments().size()>0){
					String argument = fb.getArguments().elementAt(0);

					if(deck != null){
						switch(argument){
						case "deck":
							Tools.sendMessage(deck.reveal() + " (" + deck.count() + " card(s))");
							break;
						case "hand":
							checkHand(fb.getAuthor());
							Hand h = getHandFromFolk(fb.getAuthor());
							Tools.sendMessage(fb.getAuthorNick() + "'s hand: " + h.reveal() + " (" + h.getvHand().size() + " card(s))");
							break;
						case "card":
							if(fb.getArguments().size() > 1){
								String argument2 = fb.getArguments().elementAt(1);
								if(hasCard(fb.getAuthor(), argument2)){
									Hand hand = getHandFromFolk(fb.getAuthor());
									String cardName = "";
									for(Card c : hand.getvHand()){
										if(c.getShortName().toLowerCase().equals(argument2.toLowerCase())){
											cardName = c.getFullName();
										}
									}
									Tools.sendMessage(fb.getAuthorNick() + " reveals a " + cardName + "!");
								}
								else{
									Tools.sendMessage(fb.getAuthorNick() + " doesn't have that card!");
								}
							}
							break;
						}
					}
					else{
						Tools.sendMessage("There is no deck set. Use " + Handler.key + Handler.MAKE_DECK + ", " + fb.getAuthorNick() + ".");
					}
				}

			}
		}
	}

	@BotCom(command = Handler.DRAW , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void draw(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.DRAW, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(Handler.channel.getName().equals(Handler.CHAN_FUN) || Handler.channel.getName().equals(Handler.CHAN_MAIN)){
				if(deck != null){
					if(deck.getVecCard().size() > 0){
						Card c = deck.getVecCard().firstElement();
						String argument = fb.getArguments().elementAt(0);
						switch(argument){
						case "r":
							getHandFromFolk(fb.getAuthor()).add(c);
							deck.removeCard(c);
							Tools.sendMessage(fb.getAuthorNick() + " drew a " + c.getFullName() + "!");
							break;
						case "h":
							getHandFromFolk(fb.getAuthor()).add(c);
							deck.removeCard(c);
							Tools.sendPrivateMessage("You drew a " + c.getFullName() + "!" + getHandFromFolk(fb.getAuthor()).reveal(), fb.getAuthor());
							Tools.sendMessage(fb.getAuthorNick() + " drew a card!");
							break;
						}
					}
					else{
						Tools.sendMessage("The deck is empty!");
					}
				}
				else{
					Tools.sendMessage("There is no deck set. Use " + Handler.key + Handler.MAKE_DECK + ", " + fb.getAuthorNick() + ".");
				}

			}
		}
	}

	public boolean hasCard(Folk f, String card){
		checkHand(f);
		Hand h = getHandFromFolk(f);
		boolean ret = false;
		for(Card c: h.getvHand()){
			if(c.getShortName().toLowerCase().equals(card.toLowerCase())){
				ret = true;
			}
		}
		
		return ret;
	}
	
	public void checkHand(Folk f){
		boolean exists = false;
		for(Hand h : vecHand){
			if(h.getDiscriminator().equals(f.getDiscriminator())){
				exists = true;
			}
		}
		if(!exists){
			Hand hand = new Hand(f.getNick(), f.getDiscriminator(), deck);
			vecHand.add(hand);
		}
	}

	public Hand getHandFromFolk(Folk f){
		checkHand(f);
		Hand ret = null;
		for(Hand h : vecHand){
			if(h.getDiscriminator().equals(f.getDiscriminator())){
				ret = h;
			}
		}
		return ret;
	}
	
		@BotCom(command = Handler.DISCARD , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
		public void discard(FolkBox fb){
			if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.DISCARD, Comparison.STARTS_WITH, ComLvl.PLAYER)){
				if(Handler.channel.getName().equals(Handler.CHAN_FUN) || Handler.channel.getName().equals(Handler.CHAN_MAIN)){
					if(fb.getArguments().size() > 1){
						String argumentType = fb.getArguments().elementAt(0);
						String argumentCard = fb.getArguments().elementAt(1);
						
						if(argumentType.equals("r")){
							if(hasCard(fb.getAuthor(), argumentCard)){
								Hand h = getHandFromFolk(fb.getAuthor());
								Card c = null;
								for(Card card : h.getvHand()){
									if(card.getShortName().toLowerCase().equals(argumentCard.toLowerCase())){
										c = card;
										System.out.println("FOUND");
									}
								}
									System.out.println(h.reveal());
									if(c == null){
										System.out.println("C IS NULL");
									}
									else{
										System.out.println(c.getFullName());
										System.out.println(c.getShortName());
									}
									h.remove(c);
									discarded.addCard(c);
									Tools.sendMessage(fb.getAuthorNick() + " discarded a " + c.getFullName());
								
							}
							else{
								Tools.sendMessage("You don't have that card, " + fb.getAuthorNick() + ".");
							}
						}
						else if(argumentType.equals("h")){
							if(argumentCard.substring(0, 1).equals("#") && Tools.isNumeric(argumentCard.substring(1))){
								int nb = Integer.parseInt(argumentCard.substring(1));
								Hand h = getHandFromFolk(fb.getAuthor());
								if(h.getvHand().size() >= nb){
									Card c = h.getvHand().elementAt(nb - 1);
									h.getvHand().remove(c);
									discarded.addCard(c);
									Tools.sendMessage(fb.getAuthorNick() + " discarded a card!");
								}
								else{
									Tools.sendMessage("You don't have this many cards, " + fb.getAuthorNick());
								}
							}
							
						}
						
					}
					
				}
			}
		}
	
	//	@BotCom(command = Handler.MAKE_DECK , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	//	public void makeDeck(FolkBox fb){
	//		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.MAKE_DECK, Comparison.EQUALS, ComLvl.PLAYER)){
	//			if(Handler.channel.getName().equals(Handler.CHAN_FUN) || Handler.channel.getName().equals(Handler.CHAN_MAIN)){
	//
	//				
	//			}
	//		}
	//	}

	//hangman

	public static void setVector(){
		vec.add("abruptly");
		vec.add("absurd");
		vec.add("abyss");
		vec.add("affix");
		vec.add("askew");
		vec.add("avenue");
		vec.add("awkward");
		vec.add("axiom");
		vec.add("azure");
		vec.add("bagpipes");
		vec.add("bandwagon");
		vec.add("banjo");
		vec.add("bayou");
		vec.add("beekeeper");
		vec.add("bikini");
		vec.add("blitz");
		vec.add("blizzard");
		vec.add("boggle");
		vec.add("bookworm");
		vec.add("boxcar");
		vec.add("boxful");
		vec.add("buckaroo");
		vec.add("buffalo");
		vec.add("buffoon");
		vec.add("buxom");
		vec.add("buzzard");
		vec.add("buzzing");
		vec.add("buzzwords");
		vec.add("caliph");
		vec.add("cobweb");
		vec.add("cockiness");
		vec.add("croquet");
		vec.add("crypt");
		vec.add("curacao");
		vec.add("cycle");
		vec.add("daiquiri");
		vec.add("dirndl");
		vec.add("disavow");
		vec.add("dizzying");
		vec.add("duplex");
		vec.add("dwarves");
		vec.add("embezzle");
		vec.add("equip");
		vec.add("espionage");
		vec.add("euouae");
		vec.add("exodus");
		vec.add("faking");
		vec.add("fishhook");
		vec.add("fixable");
		vec.add("fjord");
		vec.add("flapjack");
		vec.add("flopping");
		vec.add("fluffiness");
		vec.add("flyby");
		vec.add("foxglove");
		vec.add("frazzled");
		vec.add("frizzled");
		vec.add("fuchsia");
		vec.add("funny");
		vec.add("gabby");
		vec.add("galaxy");
		vec.add("galvanize");
		vec.add("gazebo");
		vec.add("giaour");
		vec.add("gizmo");
		vec.add("glowworm");
		vec.add("glyph");
		vec.add("gnarly");
		vec.add("gnostic");
		vec.add("gossip");
		vec.add("grogginess");
		vec.add("haiku");
		vec.add("haphazard");
		vec.add("hyphen");
		vec.add("iatrogenic");
		vec.add("icebox");
		vec.add("injury");
		vec.add("ivory");
		vec.add("ivy");
		vec.add("jackpot");
		vec.add("jaundice");
		vec.add("jawbreaker");
		vec.add("jaywalk");
		vec.add("jazziest");
		vec.add("jazzy");
		vec.add("jelly");
		vec.add("jigsaw");
		vec.add("jinx");
		vec.add("jiujitsu");
		vec.add("jockey");
		vec.add("jogging");
		vec.add("joking");
		vec.add("jovial");
		vec.add("joyful");
		vec.add("juicy");
		vec.add("jukebox");
		vec.add("jumbo");
		vec.add("kayak");
		vec.add("kazoo");
		vec.add("keyhole");
		vec.add("khaki");
		vec.add("kilobyte");
		vec.add("kiosk");
		vec.add("kitsch");
		vec.add("kiwifruit");
		vec.add("klutz");
		vec.add("knapsack");
		vec.add("larynx");
		vec.add("lengths");
		vec.add("lucky");
		vec.add("luxury");
		vec.add("lymph");
		vec.add("marquis");
		vec.add("matrix");
		vec.add("megahertz");
		vec.add("microwave");
		vec.add("mnemonic");
		vec.add("mystify");
		vec.add("naphtha");
		vec.add("nightclub");
		vec.add("nowadays");
		vec.add("numbskull");
		vec.add("nymph");
		vec.add("onyx");
		vec.add("ovary");
		vec.add("oxidize");
		vec.add("oxygen");
		vec.add("pajama");
		vec.add("peekaboo");
		vec.add("phlegm");
		vec.add("pixel");
		vec.add("pizazz");
		vec.add("pneumonia");
		vec.add("polka");
		vec.add("pshaw");
		vec.add("psyche");
		vec.add("puppy");
		vec.add("puzzling");
		vec.add("quartz");
		vec.add("queue");
		vec.add("quips");
		vec.add("quixotic");
		vec.add("quiz");
		vec.add("quizzes");
		vec.add("quorum");
		vec.add("razzmatazz");
		vec.add("rhubarb");
		vec.add("rhythm");
		vec.add("rickshaw");
		vec.add("schnapps");
		vec.add("scratch");
		vec.add("shiv");
		vec.add("snazzy");
		vec.add("sphinx");
		vec.add("spritz");
		vec.add("squawk");
		vec.add("staff");
		vec.add("strength");
		vec.add("strengths");
		vec.add("stretch");
		vec.add("stronghold");
		vec.add("stymied");
		vec.add("subway");
		vec.add("swivel");
		vec.add("syndrome");
		vec.add("thriftless");
		vec.add("thumbscrew");
		vec.add("topaz");
		vec.add("transcript");
		vec.add("transgress");
		vec.add("transplant");
		vec.add("triphthong");
		vec.add("twelfth");
		vec.add("twelfths");
		vec.add("unknown");
		vec.add("unworthy");
		vec.add("unzip");
		vec.add("uptown");
		vec.add("vaporize");
		vec.add("vixen");
		vec.add("vodka");
		vec.add("voodoo");
		vec.add("vortex");
		vec.add("voyeurism");
		vec.add("walkway");
		vec.add("waltz");
		vec.add("wave");
		vec.add("wavy");
		vec.add("waxy");
		vec.add("wellspring");
		vec.add("wheezy");
		vec.add("whiskey");
		vec.add("whizzing");
		vec.add("whomever");
		vec.add("wimpy");
		vec.add("witchcraft");
		vec.add("wizard");
		vec.add("woozy");
		vec.add("wristwatch");
		vec.add("wyvern");
		vec.add("xylophone");
		vec.add("yachtsman");
		vec.add("yippee");
		vec.add("yoked");
		vec.add("youthful");
		vec.add("yummy");
		vec.add("zephyr");
		vec.add("zigzag");
		vec.add("zigzagging");
		vec.add("zilch");
		vec.add("zipper");
		vec.add("zodiac");
		vec.add("zombievec");
	}

	@BotCom(command = Handler.GENERATE_WORD , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void pickWord(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GENERATE_WORD, Comparison.EQUALS, ComLvl.PLAYER)){
			if(Handler.channel.getName().equals(Handler.CHAN_FUN)){
				if(word.equals("")){
					Collections.shuffle(vec);
					word = vec.firstElement();
					lastDiscriminator = "";
					tried = "";
					bool = new Boolean[word.length()];
					for(int i = 0 ; i < bool.length ; i++){
						bool[i] = false;
					}
					Tools.sendMessage("A new word has been picked!");
					Tools.sendMessage(display());
				}
				else{
					Tools.sendMessage("There already is a word to guess: " + display() + ".");
				}
			}
		}
	}

	@BotCom(command = Handler.GUESS , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void guess(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GUESS, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(Handler.channel.getName().equals(Handler.CHAN_FUN)){
				if(!word.equals("")){
					if(!lastDiscriminator.equals(fb.getAuthorDiscriminator())){
						lastDiscriminator = fb.getAuthorDiscriminator();
						String guess = fb.getArguments().firstElement();
						if(guess.length() == 1){
							if(tried.indexOf(guess) == -1){
								tried += guess;
								System.out.println("111111111111");
								System.out.println("CHECK BOOL " + Arrays.toString(bool));
								boolean foundOne = false;
								for(int i = 0; i < word.length() ; i++){
									if(word.substring(i, i+1).equals(guess)){
										bool[i] = true;
										foundOne = true;
										System.out.println("TRUE");
									}
									else{
										System.out.println("NOPE");
									}
								}

								boolean won = true;
								for(int j = 0 ; j < bool.length; j++){
									if(bool[j] == false){
										won = false;
									}
								}
								if(won){
									Tools.sendMessage(fb.getAuthorNick() + " found the word! it was " + word + "!");
									word = "";
								}
								else{
									if(foundOne){
										Tools.sendMessage("Well played, " + guess + " was a good guess.");
									}
									else{
										Tools.sendMessage("No " + guess + " in that word " + fb.getAuthorNick() + "!");
									}
									Tools.sendMessage("Here we are: " + display() + ".");
								}
							}
							else{
								Tools.sendMessage(guess + " was tried already!");
							}
						}else{
							if(word.equals(guess)){
								Tools.sendMessage(fb.getAuthorNick() + " found the word! it was " + word + "!");
								word = "";
							}
							else{
								Tools.sendMessage("Hmm no, " + fb.getAuthorNick() + ", that't not it.");
							}
						}
					}
					else{
						Tools.sendMessage("You can't try twice in a row, " + fb.getAuthorNick() + "!");
					}

				}
				else{
					Tools.sendMessage("There is no word chosen currently, try using " + Handler.key + Handler.GENERATE_WORD + ", " + fb.getAuthorNick() + ".");
				}
			}
		}
	}

	public String display(){
		String ret = "";
		for (int i = 0; i < word.length(); i++){
			if(bool[i] == true){
				ret += word.substring(i, i+1) + " ";
			}
			else {
				ret += "*" + " ";
			}
		}
		return ret;
	}

	public void makeDeck(){

	}
}
