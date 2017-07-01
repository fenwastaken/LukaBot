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
import managers.inventoryManager;
import objects.Folk;
import objects.FolkBox;

public class CharacterCommands {

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
				String ret = inventoryManager.getInventory(target.getDiscriminator());
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

	@BotCom(command = Handler.ADD_ITEM , lvl = ComLvl.GAMEMASTER, type = ComType.MSG, category = ComCategory.GAMEMASTERS)
	public void giveItem(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.ADD_ITEM, Comparison.STARTS_WITH, ComLvl.GAMEMASTER)){
			Folk target = null;
			if(fb.hasFolks()){
				try{
					target = fb.getFolkNbX(0);
					int qt = Integer.parseInt(fb.getArguments().firstElement());
					String item = fb.getArguments().elementAt(1);
					inventoryManager.addItem(target.getDiscriminator(), item, qt);
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
					String ret = inventoryManager.remItem(target.getDiscriminator(), item, qt);
					
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
					boolean worked = inventoryManager.clearItems(target.getDiscriminator());
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
	
}
