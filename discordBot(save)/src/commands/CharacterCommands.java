package commands;

import java.sql.SQLException;
import java.util.Vector;

import annotations.BotCom;
import annotations.ComCategory;
import annotations.ComLvl;
import annotations.ComType;
import annotations.Comparison;
import handy.Handler;
import handy.Tools;
import managers.CharacterManager;
import managers.PlayerManager;
import managers.PouchManager;
import managers.InventoryManager;
import objects.Folk;
import objects.FolkBox;
import objects.Item;

public class CharacterCommands {

	@BotCom(command = Handler.ALTER_MONEY , lvl = ComLvl.GAMEMASTER, type = ComType.MSG, category = ComCategory.GAMEMASTERS)
	public void changeMoney(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.ALTER_MONEY, Comparison.STARTS_WITH, ComLvl.GAMEMASTER)){
			if(fb.hasFolks()){
				try{
					Folk target = fb.getFolkNbX(0);
					int amount = Integer.parseInt(fb.getArguments().elementAt(0));
					int multiplier = 0;
					String currency = fb.getArguments().elementAt(1);
					if(currency.equals("gold") || currency.equals("silver") || currency.equals("copper")){
						switch(currency){
						case "gold":
							multiplier = 100;
							break;

						case "silver":
							multiplier = 10;
							break;

						case "copper":
							multiplier = 1;
							break;
						}
						amount = amount * multiplier;
							PouchManager.CreatePouchIfCharacterExists(target.getDiscriminator(), target.getNick());
							if(PouchManager.pouchExists(target.getDiscriminator(), target.getNick())){
								PouchManager.changeAmount(target.getDiscriminator(), target.getNick(), amount, "add");
								Tools.sendMessage(pouchToString(target));
							}
							else{
								Tools.sendMessage(target.getNick() + " has no pouch and can't be given one.");
							}
					}
					else{
						Tools.sendMessage("What are those coins...?");
					}
				}
				catch(NumberFormatException e){
					Tools.sendMessage("That amount doesn't feel right...");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@BotCom(command = Handler.GIVE_MONEY , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void pay(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GIVE_MONEY, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(fb.hasFolks()){
				try{
					Folk target = fb.getFolkNbX(0);
					int amount = Integer.parseInt(fb.getArguments().elementAt(0));
					if (amount < 0){
						amount = 0;
					}
					int multiplier = 0;
					String currency = fb.getArguments().elementAt(1);
					if(currency.equals("gold") || currency.equals("silver") || currency.equals("copper")){
						switch(currency){
						case "gold":
							multiplier = 100;
							break;

						case "silver":
							multiplier = 10;
							break;

						case "copper":
							multiplier = 1;
							break;
						}
						amount = amount * multiplier;
						if(PouchManager.getPouchContent(fb.getAuthorDiscriminator(), fb.getAuthorNick()) >= amount){
							PouchManager.CreatePouchIfCharacterExists(target.getDiscriminator(), target.getNick());
							if(PouchManager.pouchExists(target.getDiscriminator(), target.getNick())){
								PouchManager.changeAmount(fb.getAuthorDiscriminator(), fb.getAuthorNick(), amount, "sub");
								PouchManager.changeAmount(target.getDiscriminator(), target.getNick(), amount, "add");
								Tools.sendMessage(pouchToString(fb.getAuthor()));
								Tools.sendMessage(pouchToString(target));
							}
							else{
								Tools.sendMessage(target.getNick() + " has no pouch and can't be given one.");
							}
						}
						else{
							Tools.sendMessage("You don't have enough, " + fb.getAuthorNick() + ".");
						}
						
					}
					else{
						Tools.sendMessage("What are those coins...?");
					}
				}
				catch(NumberFormatException e){
					Tools.sendMessage("That amount doesn't feel right...");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	@BotCom(command = Handler.GET_POUCH , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void getPouch(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_POUCH, Comparison.STARTS_EQUALS, ComLvl.PLAYER)){
			Folk target = null;
			try {
				if(fb.hasFolks()){
					target = fb.getFolkNbX(0);
				}
				else{
					target = fb.getAuthor();
				}

				boolean created = PouchManager.CreatePouchIfCharacterExists(target.getDiscriminator(), target.getNick());
				if(created){
					Tools.sendMessage(target.getNick() + " was granted a brand new magic pouch!");
				}

				String ret = pouchToString(target);
				Tools.sendMessage(ret);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@BotCom(command = Handler.GET_CHARACTERS , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void getCharacters(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_CHARACTERS, Comparison.STARTS_EQUALS, ComLvl.PLAYER)){
			String discriminator;
			String name = "";
			if(fb.hasFolks()){
				discriminator = fb.getFolkNbX(0).getDiscriminator();
				name = fb.getFolkNbX(0).getNick();
			}
			else{
				discriminator = fb.getAuthorDiscriminator();
				name = fb.getAuthorNick();
			}
			try {
				Vector<String>vec = CharacterManager.getCharacterNamesFromDiscriminator(discriminator);
				String ret = "";
				for(String str : vec){
					ret += str + ", ";
				}
				ret = ret.substring(0, ret.length()-2) + ".";
				Tools.sendMessage(name + " has " + CharacterManager.getCharacterNbFromDiscriminator(discriminator) + " character(s): " + ret);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@BotCom(command = Handler.GET_AVATAR , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void getAvatar(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_AVATAR, Comparison.STARTS_EQUALS, ComLvl.PLAYER)){
			String target = "";
			String disc = "";

			if(fb.hasFolks()){//
				Folk folk = fb.getFolkNbX(0);
				target = folk.getNick();
				disc = folk.getDiscriminator();
			}
			else{
				Folk auth = fb.getAuthor();
				target = auth.getNick();
				disc = auth.getDiscriminator();
			}

			try {
				if(CharacterManager.doesCharacterExistFromDiscNick(disc, target)){
					String ret = CharacterManager.getAvatar(disc, target);
					if(ret == null){
						Tools.sendMessage(target + " has no avatar yet.");
					}
					else{
						Tools.sendMessage(target + "'s avatar: " + ret);
					}
				}
				else{
					Tools.sendMessage("There's no " + target + " registered, " + fb.getAuthorNick() + ".");	
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@BotCom(command = Handler.SET_AVATAR , lvl = ComLvl.USER, type = ComType.MSG, category = ComCategory.USERS)
	public void setAvatar(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.SET_AVATAR, Comparison.STARTS_WITH, ComLvl.USER)){
			String avatar = Tools.lastParameter(fb.getMessage(), 0);
			Folk author = fb.getAuthor();

			if(avatar.contains("imgur")){
				if(avatar.contains("?")){
					avatar = avatar.substring(0, avatar.indexOf("?"));
				}
				try {
					CharacterManager.setAvatar(author.getDiscriminator(), author.getNick(), avatar);
					Tools.sendMessage("Your new avatar is set, " + author.getNick() + ".");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}
			else{
				Tools.sendMessage("It is not an imgur link, " + author.getNick() + ".");
			}


		}
	}

	@BotCom(command = Handler.REMOVE_AVATAR , lvl = ComLvl.ADMIN, type = ComType.MSG, category = ComCategory.ADMINS)
	public void remAvatar(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.REMOVE_AVATAR, Comparison.STARTS_WITH, ComLvl.ADMIN)){
			if(fb.hasFolks()){
				try {
					String discriminator = fb.getFolkNbX(0).getDiscriminator();
					String nick = fb.getFolkNbX(0).getNick();
					CharacterManager.setAvatar(discriminator, nick, null);
					int rank = PlayerManager.getPlayerRank(discriminator);
					String ret = nick + "'s avatar was removed.";
					if (rank != 5){
						PlayerManager.updatePlayerRank(4, discriminator);
						ret = ret.substring(0, ret.length() - 1) + " and their rank is downgraded to user. sorry :)";
					}
					Tools.sendMessage(ret);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@BotCom(command = Handler.GET_AVATARFULL , lvl = ComLvl.ADMIN, type = ComType.MSG, category = ComCategory.ADMINS)
	public void getAvatarfull(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_AVATARFULL, Comparison.EQUALS, ComLvl.ADMIN)){
			try {
				Vector<String> ret = CharacterManager.getAvatarfull();
				String str = "```";
				int i = 0;
				for(String st : ret){
					i++;
					str += st + ", ";
					if(i >= 5){
						str += "\n";
						i = 0;
					}
				}
				str += "```";
				Tools.sendMessage("There's "+ ret.size() + " character(s) with avatar(s): " + str);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@BotCom(command = Handler.GET_AVATARLESS , lvl = ComLvl.ADMIN, type = ComType.MSG, category = ComCategory.ADMINS)
	public void getAvatarless(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_AVATARLESS, Comparison.EQUALS, ComLvl.ADMIN)){
			try {
				Vector<String> ret = CharacterManager.getAvatarless();
				String str = "```";
				int i = 0;
				for(String st : ret){
					i++;
					str += st + ", ";
					if(i >= 5){
						str += "\n";
						i = 0;
					}
				}
				str += "```";
				Tools.sendMessage("There's "+ ret.size() + " character(s) without avatar(s): " + str);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@BotCom(command = Handler.GET_INVENTORY , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void getInventory(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_INVENTORY, Comparison.STARTS_EQUALS, ComLvl.PLAYER)){
			Folk target = null;
			if(fb.hasFolks()){
				target = fb.getFolkNbX(0);
			}
			else{
				target = fb.getAuthor();
			}
			try {
				String ret = InventoryManager.getInventory(target.getDiscriminator());
				if(ret.length() == 2){
					Tools.sendMessage(target.getNick() + "'s inventory is empty.");
				}
				else{
					Tools.sendMessage(target.getNick() + "'s inventory: " + ret + ".");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@BotCom(command = Handler.GIVE_ITEM , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void giveItem(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GIVE_ITEM, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			Folk target = null;
			if(fb.hasFolks()){
				try{
					target = fb.getFolkNbX(0);
					int qt = Integer.parseInt(fb.getArguments().firstElement());
					String item = fb.getArguments().elementAt(1);

					//
					Item ret = InventoryManager.getObjectFromInventory(fb.getAuthorDiscriminator(), item);

					if(ret == null){
						Tools.sendMessage("You don't have enough of that object, " + fb.getAuthorNick() + ".");
					}
					else{
						InventoryManager.remItem(fb.getAuthorDiscriminator(), item, qt);
						InventoryManager.addItem(target.getDiscriminator(), item, qt);
						Tools.sendMessage(fb.getAuthorNick() + " gave " + qt + " " + item + " to " + target.getNick() + ".");
					}
				}
				catch(NumberFormatException | SQLException e){
					Tools.sendMessage("That huh... was a weird number..");
				}
			}
			else{
				Tools.sendMessage("You mentionned no one, " + fb.getAuthorNick() + ".");
			}

		}
	}

	@BotCom(command = Handler.ADD_ITEM , lvl = ComLvl.GAMEMASTER, type = ComType.MSG, category = ComCategory.GAMEMASTERS)
	public void grantItem(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.ADD_ITEM, Comparison.STARTS_WITH, ComLvl.GAMEMASTER)){
			Folk target = null;
			if(fb.hasFolks()){
				try{
					target = fb.getFolkNbX(0);
					int qt = Integer.parseInt(fb.getArguments().firstElement());
					String item = fb.getArguments().elementAt(1);
					InventoryManager.addItem(target.getDiscriminator(), item, qt);
					Tools.sendMessage(target.getNick() + " recieved " + qt + " " + item + ".");
				}
				catch(NumberFormatException | SQLException e){
					Tools.sendMessage("That huh... was a weird number..");
					e.printStackTrace();
				}
			}
			else{
				Tools.sendMessage("You mentionned no one, " + fb.getAuthorNick() + ".");
			}

		}
	}

	@BotCom(command = Handler.REM_ITEM , lvl = ComLvl.GAMEMASTER, type = ComType.MSG, category = ComCategory.GAMEMASTERS)
	public void takeItem(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.REM_ITEM, Comparison.STARTS_WITH, ComLvl.GAMEMASTER)){
			Folk target = null;
			if(fb.hasFolks()){
				try{
					target = fb.getFolkNbX(0);
					int qt = Integer.parseInt(fb.getArguments().firstElement());
					String item = fb.getArguments().elementAt(1);
					String ret = InventoryManager.remItem(target.getDiscriminator(), item, qt);

					switch(ret){
					case "substracted":
						Tools.sendMessage(target.getNick() + " lost " + qt + " " + item + ".");
						break;
					case "deleted":
						Tools.sendMessage(target.getNick() + " lost all of their " + item + "(s).");
						break;
					case "not found":
						Tools.sendMessage(target.getNick() + " had no " + item + " to begin with.");
						break;
					}
				}
				catch(NumberFormatException | SQLException e){
					Tools.sendMessage("That huh... was a weird number..");
					e.printStackTrace();
				}
			}
			else{
				Tools.sendMessage("You mentionned no one, " + fb.getAuthorNick() + ".");
			}

		}
	}

	@BotCom(command = Handler.CLEAR_INVENTORY , lvl = ComLvl.GAMEMASTER, type = ComType.MSG, category = ComCategory.GAMEMASTERS)
	public void clearItems(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.CLEAR_INVENTORY, Comparison.STARTS_WITH, ComLvl.GAMEMASTER)){
			Folk target = null;
			if(fb.hasFolks()){
				try {
					target = fb.getFolkNbX(0);
					boolean worked = InventoryManager.clearItems(target.getDiscriminator());
					if(worked){
						Tools.sendMessage(target.getNick() + "'s inventory was cleared.");
					}
					else{
						Tools.sendMessage(target.getNick() + "'s inventory was empty.");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				Tools.sendMessage("You mentionned no one, " + fb.getAuthorNick() + ".");
			}

		}
	}

	@BotCom(command = Handler.DEACTIVATE , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void deactivate(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.DEACTIVATE, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(!fb.getArguments().isEmpty()){
				String nick = Tools.reBuilder(-1, fb.getArguments(), " ");
				try {

					ComLvl senderLvl = Tools.levelChecker(fb.getAuthorDiscriminator());
					boolean isAdmin = true;
					if(senderLvl.getValue() > 0){
						isAdmin = false;
					}

					if(!isAdmin){ //player deleting own character
						if(CharacterManager.doesCharacterExistFromDiscNick(fb.getAuthorDiscriminator(), nick)){
							CharacterManager.deactivateCharacterFromDiscNick(fb.getAuthorDiscriminator(), nick, false);
							Tools.sendMessage(nick + " was deactivated.");
						}
						else{
							Tools.sendMessage("You have no character named " + nick + ".");
						}
					}
					else{//admin deleting any character
						if(CharacterManager.doesCharacterExistFromNick(nick)){
							CharacterManager.deactivateCharacterFromNick(nick, false);
							Tools.sendMessage(nick + " was deactivated.");
						}
						else{
							Tools.sendMessage("There is no character named " + nick + ".");
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@BotCom(command = Handler.STATS , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void getStats(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.STATS, Comparison.EQUALS, ComLvl.PLAYER)){
			try {
				int playerNb = PlayerManager.getPlayerNb();
				int legitPlayer = PlayerManager.getLegitPlayerNb();
				int charaNb = CharacterManager.getCharacterNb();
				int avaNb = CharacterManager.getCharacterWAvatarNb();
				Tools.sendMessage("So, we have a total of " + playerNb + " players and " + charaNb + " characters." 
						+ avaNb + " of these characters are legit, they are owned by " + legitPlayer + " different players.");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String pouchToString(Folk target){
		String ret = "oopsies";
		try {
			int amount;
			amount = PouchManager.getPouchContent(target.getDiscriminator(), target.getNick());
			int gold = amount/100;
			amount = amount%100;
			int silver = amount/10;
			int copper = amount%10;

			ret = target.getNick() + " has ";
			boolean munnies = false;
			if(gold > 0){
				ret += gold + " Gold coin(s), ";
				munnies = true;
			}
			if(silver > 0){
				ret += silver + " Silver coin(s), ";
				munnies = true;
			}
			if(copper > 0){
				ret += copper + " Copper coin(s).";
				munnies = true;
			}

			if(!munnies){
				ret = target.getNick() + " 's pouch is empty. :c";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
		
	}

}
