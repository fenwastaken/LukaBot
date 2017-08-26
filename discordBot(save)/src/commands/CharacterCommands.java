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
					if(currency.equals("platinum") || currency.equals("gold") || currency.equals("silver") || currency.equals("copper")){
						switch(currency){
						case "platinum":
							multiplier = 1000;
							break;
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
							Tools.sendMessage(Tools.pouchToString(target, false));
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
					if(currency.equals("platinum") || currency.equals("gold") || currency.equals("silver") || currency.equals("copper")){
						switch(currency){
						case "platinum":
							multiplier = 1000;
							break;
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
								Tools.sendMessage(Tools.pouchToString(fb.getAuthor(), false));
								Tools.sendMessage(Tools.pouchToString(target, false));
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

				String ret = Tools.pouchToString(target, false);
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

	@BotCom(command = Handler.SET_THUMBNAIL , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void setThumbnail(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.SET_THUMBNAIL, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(fb.getArguments().size() > 0){
				String tn = fb.getArguments().firstElement();
				if(tn.contains("imgur") && (tn.endsWith("png") || tn.endsWith("jpg"))){
					try {
						CharacterManager.setThumbnail(fb.getAuthorDiscriminator(), fb.getAuthorNick(), tn);
						Tools.sendMessage("Your thumbnail is set, " + fb.getAuthorNick() + ".");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					Tools.sendMessage("You must use an imgur link with either a jpg or png extension, " + fb.getAuthorNick() + ".");
				}
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
	
	@BotCom(command = Handler.REMOVE_THUMBNAIL , lvl = ComLvl.ADMIN, type = ComType.MSG, category = ComCategory.ADMINS)
	public void remThumbnail(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.REMOVE_THUMBNAIL, Comparison.STARTS_WITH, ComLvl.ADMIN)){
			if(fb.hasFolks()){
				try {
					String discriminator = fb.getFolkNbX(0).getDiscriminator();
					String nick = fb.getFolkNbX(0).getNick();
					CharacterManager.setThumbnail(discriminator, nick, null);
					Tools.sendMessage(nick + "'s thumbnail was removed, sorry! :)");
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

			try {
				InventoryManager.initiateInventory(fb.getAuthorDiscriminator(), fb.getAuthorNick());
				if(fb.getArguments().size() > 0){
					String category = fb.getArguments().elementAt(0).toLowerCase();

					boolean has = InventoryManager.hasCategoryFromdisNick(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category);
					if(has){
						Tools.sendMessage(Tools.inventoryMaker(fb.getAuthor(), category));
					}
					else{
						Tools.sendMessage("You don't have that category in your inventory, " + fb.getAuthorNick() + ".");
					}
				}
				else{
					Tools.sendMessage(Tools.inventoryMaker(fb.getAuthor(), ""));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@BotCom(command = Handler.ADD_CATEGORY , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void giveItem(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.ADD_CATEGORY, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(fb.getArguments().size() > 0){
				String category = fb.getArguments().firstElement();
				try {
					if(!InventoryManager.doesCategoryExist(category)){
						InventoryManager.addCategory(category, false);
					}
					if(InventoryManager.hasCategoryFromdisNick(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category)){
						Tools.sendMessage("You already have that category in your inventory, " + fb.getAuthorNick() + "!");
					}
					else{
						int position = InventoryManager.getNextCategoryPosition(fb.getAuthorDiscriminator(), fb.getAuthorNick());
						InventoryManager.boundCategoryToCharacter(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category, position);
						Tools.sendMessage(category.toLowerCase() + " was added to your inventory, " + fb.getAuthorNick() + "!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				Tools.sendMessage("Where's the category you want to add, " + fb.getAuthorNick() + "?");
			}
		}
	}
	
	@BotCom(command = Handler.REMOVE_CATEGORY , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void takeItem(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.REMOVE_CATEGORY, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(fb.getArguments().size() > 0){
				String category = fb.getArguments().firstElement();
				try {
					if(InventoryManager.hasCategoryFromdisNick(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category)){
						if(!InventoryManager.getCategoryTypeFromName(category)){
							InventoryManager.deleteAllItemsOfACategory(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category);
							InventoryManager.deleteCustomCategory(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category);
							
							Vector<String> cat = InventoryManager.getAllCustomCategoryNamesFromCharId(fb.getAuthorDiscriminator(), fb.getAuthorNick());
							int counter = 0;
							for(String str : cat){
								counter++;
								int categoryId = InventoryManager.getCustomCategoryId(fb.getAuthorDiscriminator(), fb.getAuthorNick(), str);
								InventoryManager.updateCategoryposition(fb.getAuthorDiscriminator(), fb.getAuthorNick(), categoryId, counter);
							}
							Tools.sendMessage(category + " was removed properly, " + fb.getAuthorNick());
							
						}
						else{
							Tools.sendMessage("that's not a category you're allowed to delete, " + fb.getAuthorNick() + "!");
						}
					}
					else{
						Tools.sendMessage("Come on! You don't even have that " + category + " category in your inventory, " + fb.getAuthorNick() + "!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@BotCom(command = Handler.MOVE_CATEGORY , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void moveCategory(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.MOVE_CATEGORY, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(fb.getArguments().size() > 1){
				String category = fb.getArguments().firstElement();
				try {
					if(InventoryManager.hasCategoryFromdisNick(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category)){
						String move = fb.getArguments().elementAt(1).toLowerCase();
						if(!move.equals("up") && !move.equals("down")){
							Tools.sendMessage("You can only move a category up or down,  not [" + move + "].");
						}else{
							int pos = 0;
							switch (move){
							case "up":
								pos = -1;
								break;
							case "down":
								pos = 1;
								break;
							}
							
							try {
								if(InventoryManager.moveCategory(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category, pos)){
									Tools.sendMessage("Category " + category + " was moved, " + fb.getAuthorNick() + "!");
								}
								else{
									Tools.sendMessage("you can't move that any higher/lower, " + fb.getAuthorNick() + "!");
								}
								
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}
					else{
						Tools.sendMessage("Come on! You don't even have that " + category + " category in your inventory, " + fb.getAuthorNick() + "!");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				Tools.sendMessage("you must format the command this way: " + Handler.key + "categorymove categoryname up/down.");
			}
		}
	}
	
	/*@BotCom(command = Handler.GIVE_ITEM , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void giveItem(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GIVE_ITEM, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			//foo
		}
	}*/
	
	
	@BotCom(command = Handler.INVENTORY_STATE , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void changeInventoryState(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.INVENTORY_STATE, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(fb.getArguments().size() > 0){
				String state = fb.getArguments().firstElement();
				String ret;
				switch(state){
				case "locked":
					ret = "l";
					break;
				case "unlocked":
					ret = "u";
					break;
					default:
						Tools.sendMessage(state + " is neither locked or unlocked, " + fb.getAuthorNick() + ".");
						return;
				}
				try {
					CharacterManager.updateInventoryState(fb.getAuthorDiscriminator(), fb.getAuthorNick(), ret);
					Tools.sendMessage("your inventory was " + state + " " + fb.getAuthorNick() + "!");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				Tools.sendMessage("You must format the cmmand this way: " + Handler.key + Handler.INVENTORY_STATE + " locked/unlocked");
			}
		}
	}

	@BotCom(command = Handler.MAKE_ITEM , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void grantItem(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.MAKE_ITEM, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			String line = fb.getEv().getMessage().getContent();
			line = line.replaceAll(", ", ",");



			if(line.contains("[") && line.contains("]")){
				/*getting the category*/
				int start = line.indexOf("[");
				int stop = line.indexOf("]", start + 1);
				String category = line.substring(start + 1, stop);
				line = line.substring(0, start) + line.substring(stop + 2);
				System.out.println("LINE: " + line);
				Vector<String> vec = Tools.cutter(line, ",");

				try {
					if(InventoryManager.hasCategoryFromdisNick(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category)){
						String mistakes = "";
						String good = "";
						int count = 0;
						String removed = "";

						for(String str : vec){
							if(str.indexOf(" ") > 0){
								try{
									int qty = Integer.parseInt(str.substring(0, str.indexOf(" ")));
									String item = str.substring(str.indexOf(" ") + 1);
									good = good + qty + " * " + item + ", ";
									int result = InventoryManager.addUpdateItem(fb.getAuthorDiscriminator(), fb.getAuthorNick(), item, qty, category);
									count += qty;
									
									if(result < 1){
										removed += item + " was deleted from " + category + ", "; 
									}
									
								}
								catch(NumberFormatException e){
									mistakes+= "(" + str + "),";
								}
							}
							else{
								mistakes+= "(" + str + "),";
							}
						}
						if(good.length() > 2){
							good = good.substring(0, good.length()-2);
						}

						if(good.length() > 0){
							
							String formattedWords = "";
							if(count > 1){
								formattedWords = " items were ";
							}
							else{
								formattedWords = " item was ";
							}
							
							Tools.sendMessage("The following " + formattedWords + " added to [" + category + "]: " + good + ".");
							
							if(removed.length() > 0){
								Tools.sendMessage(removed.substring(0, removed.length() - 2) + ".");
							}
							
						}
						if(mistakes.length()>0){
							mistakes = mistakes.substring(0, mistakes.length() - 1);
							Tools.sendMessage("The following items were not formatted properly: " + mistakes + ".");
						}
					}
					else{
						Tools.sendMessage("You never added that category [" + category + "] to your inventory, " + fb.getAuthorNick() + ".");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				Tools.sendMessage("You need to add a [category] " + fb.getAuthorNick() + "!"); 
			}

		}
	}

	@BotCom(command = Handler.EXPORT_ITEM , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void export(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.EXPORT_ITEM, Comparison.EQUALS, ComLvl.PLAYER)){
			try {
				Vector<String> vecCat = InventoryManager.getAllCustomCategoryNamesFromCharId(fb.getAuthorDiscriminator(), fb.getAuthorNick());
				Vector<Item> vecItem;
				String ret = "```";
				
				for(String cat : vecCat){
					vecItem = InventoryManager.getAllItemsOfCategory(fb.getAuthorDiscriminator(), fb.getAuthorNick(), cat);
					ret += Handler.key + "make [" + cat + "] ";
					for(Item it : vecItem){
						ret += it.getQuantity() + " " + it.getName() + ", ";
					}
					ret = ret.substring(0, ret.length() - 2);
					ret += "\n";
				}
				ret += "```";
				
				Tools.sendMessage(ret);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@BotCom(command = Handler.CLEAR_INVENTORY , lvl = ComLvl.GAMEMASTER, type = ComType.MSG, category = ComCategory.GAMEMASTERS)
	public void clearItems(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.CLEAR_INVENTORY, Comparison.STARTS_EQUALS, ComLvl.GAMEMASTER)){
			if(fb.getArguments().size() > 0){
				String category = fb.getArguments().firstElement();
				try {
					if(InventoryManager.hasCategoryFromdisNick(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category)){
						InventoryManager.clearInventoryOfCategory(fb.getAuthorDiscriminator(), fb.getAuthorNick(), category);
						Tools.sendMessage("Your " + category + " was cleared, " + fb.getAuthorNick() +".");
					}
					else{
						Tools.sendMessage("you don't have this category in your inventory, " + fb.getAuthorNick() + ".");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				try {
					InventoryManager.clearInventory(fb.getAuthorDiscriminator(), fb.getAuthorNick());
					Tools.sendMessage("Your inventory was cleared, " + fb.getAuthorNick() +".");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@BotCom(command = Handler.DEACTIVATE , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void deactivate(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.DEACTIVATE, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(!fb.getArguments().isEmpty()){
				String nick = Tools.reBuilder(-1, fb.getArguments(), " ");
				nick = nick.replace("@", "");
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
