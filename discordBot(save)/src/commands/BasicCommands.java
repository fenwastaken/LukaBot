package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.Vector;

import javax.tools.Tool;

import annotations.BotCom;
import annotations.ComCategory;
import annotations.ComLvl;
import annotations.ComType;
import annotations.Comparison;
import dice.DiceType;
import gnu.trove.impl.HashFunctions;
import handy.Handler;
import handy.Tools;
import managers.CharacterManager;
import managers.PlayerManager;
import managers.ThreadManager;
import managers.UrlsManager;
import managers.InventoryManager;
import objects.Folk;
import objects.FolkBox;

public class BasicCommands {
	
	@BotCom(command = Handler.SET_LINK , lvl = ComLvl.TRUSTED, type = ComType.MSG, category = ComCategory.TRUSTED)
	public void setLink(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.SET_LINK, Comparison.STARTS_WITH, ComLvl.TRUSTED)){
			System.out.println("here yeah");
			if(fb.getArguments().size() == 2){
				String name = fb.getArguments().elementAt(0);
				String link = fb.getArguments().elementAt(1);
				System.out.println("name " + name + " link " + link);
				try{
					if(!UrlsManager.exists(name)){
						System.out.println("looks good");
						UrlsManager.setLink(name, link);
						Tools.sendMessage("Alrighty, I added the link for " + name);
					}
					else{
						//update
						UrlsManager.updateLink(link, name);
						Tools.sendMessage("I updated " + name + "'s link, " + fb.getAuthorNick() + ".");
					}
				}
				catch(SQLException e){
					Tools.sendMessage("Something went wrong...");
					e.printStackTrace();
				}
			}
			else{
				Tools.sendMessage("Wait wait wait... I need a name and a link, nothing more, nothing less, " + fb.getAuthorNick());
			}
		}
	}

	@BotCom(command = Handler.GET_LINK , lvl = ComLvl.USER, type = ComType.MSG, category = ComCategory.USERS)
	public void getLink(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_LINK, Comparison.STARTS_EQUALS, ComLvl.USER)){
			try {
				if(!fb.getArguments().elementAt(0).equals(Handler.key + Handler.GET_LINK)){
					String name = fb.getArguments().elementAt(0);

					String link = UrlsManager.getLinkFromName(name);
					if(link != null && link.length() > 0){
						Tools.sendMessage("Here it is, " + fb.getAuthorNick() + ": " + link);
					}
					else{
						Tools.sendMessage("Hmmmm... Nope, I don't have that one.");
					}

				}
				else{
					String ret = UrlsManager.getAllNames();
					if(ret.length() > 0){
						Tools.sendMessage("Here's what i got: " + ret);
					}
					else{
						Tools.sendMessage("I got no link saved yet.");
					}

				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@BotCom(command = Handler.RANK , lvl = ComLvl.ADMIN, type = ComType.MSG, category = ComCategory.ADMINS)
	public void setRank(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.RANK, Comparison.STARTS_WITH, ComLvl.ADMIN)){
			if(fb.hasFolks()){
				Folk f = fb.getFolkNbX(0);
				try{
					int rank = Integer.parseInt(fb.getArguments().elementAt(0));
					PlayerManager.updatePlayerRank(rank, f.getDiscriminator());
					Tools.sendMessage(f.getNick() + "'s rank was set to " + ComLvl.USER.getString(rank) + ".");
				}
				catch(NumberFormatException |SQLException e){
					Tools.sendMessage(fb.getArguments().elementAt(0) + " is no valid rank.");
				}
			}
		}
	}

	@BotCom(command = Handler.LAST_SEEN , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void lastSeen(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.LAST_SEEN, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			if(fb.hasFolks()){
				try {
					String discriminator = fb.getFolkNbX(0).getDiscriminator();
					String nick = fb.getFolkNbX(0).getNick();
					String ret = PlayerManager.getDateToString(discriminator);
					Tools.sendMessage(nick + " was last seen " + ret + " ago.");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	@BotCom(command = Handler.HELLO, lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void hello(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.HELLO, Comparison.EQUALS, ComLvl.PLAYER)){
			Tools.sendMessage("Hello, " + new FolkBox().getAuthorNick() + ".");
		}
	}

	@BotCom(command = Handler.GET_VERSION, lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void getVersion(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_VERSION, Comparison.EQUALS, ComLvl.PLAYER)){
			Tools.sendMessage("I am " + Handler.botName + ", version " + Handler.versionNumber + ". My current admin is " + Handler.admin + ".");
		}
	}

	@BotCom(command = Handler.SET_KEY, lvl = ComLvl.ADMIN, type = ComType.MSG, category = ComCategory.ADMINS)
	public void setKey(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.SET_KEY, Comparison.STARTS_WITH, ComLvl.ADMIN)){
			String temp = fb.getMessage().substring(Handler.key.length() + Handler.SET_KEY.length() + 1);
			if(temp.startsWith("/")){
				temp = "!";
			}
			Handler.key = temp;
			Tools.sendMessage("My new key is now " + Handler.key + ".");
		}
	}

	@BotCom(command = Handler.GET_HELP, lvl = ComLvl.USER, type = ComType.BOTH, category = ComCategory.USERS)
	public void getHelp(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_HELP, Comparison.EQUALS, ComLvl.USER)){
			Tools.sendMessage(Tools.helpMaker());
		}
	}


	@BotCom(command = Handler.GET_ACC, lvl = ComLvl.USER, type = ComType.MSG, category = ComCategory.USERS)
	public void getAcc(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_ACC, Comparison.STARTS_EQUALS, ComLvl.USER)){
			String disc;
			String name;
			if(fb.hasFolks()){
				disc = fb.getFolkNbX(0).getDiscriminator();
				name = fb.getFolkNbX(0).getNick() + "'s ";
			}
			else{
				disc = fb.getAuthorDiscriminator();
				name = "Your ";
			}
			Tools.sendMessage(name + "accreditation level is " 
					+ ComLvl.ADMIN.getString(Tools.levelChecker(disc).getValue()) + ", " + fb.getAuthorNick() + ".");
		}
	}
	@BotCom(command = Handler.YOUTUBE, lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void youtube(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.YOUTUBE, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			Vector<String> param = fb.getArguments();

			String search = "";

			for(String str : param){
				search += str + "+";
			}
			search = search.substring(0, search.length() - 1);
			URL url;
			try {
				url = new URL("https://www.youtube.com/results?search_query=" + search);

				String ret = "";
				String merde = "/watch?v=Lxbdvo2vFwc&amp;list=PLbpi6ZahtOH4CPwU6AGz6O1HuLmt-uSgt";

				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String inputLine;
				String all = "";
				while ((inputLine = in.readLine()) != null){
					all += inputLine;
				}
				in.close();

				while(ret.equals("") || ret.equals(merde)){
					int start = all.indexOf("/watch?v=");
					int end = all.indexOf("\"", start);
					ret = all.substring(start, end);
					System.out.println(ret);
					all = all.substring(start + 8);
				}

				Tools.sendMessage("Here's the result, " + fb.getAuthorNick() + " : " + "https://www.youtube.com" + ret);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@BotCom(command = Handler.ROLL, lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void roll(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.ROLL, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			String param = Tools.lastParameter(fb.getMessage(), 0).toLowerCase();
			if(param.matches("[0-9]*d[0-9]+[+-][0-9]+") || param.matches("[0-9]*d[0-9]+")){
				System.out.println("DICE");
				try{
					int dice = 0;

					if(param.indexOf("d") == 0){
						System.out.println("d20 style");
						dice = 1;
					}else{
						System.out.println("DICE more than one :: d: " + param.indexOf("d"));
						dice = Integer.parseInt(param.substring(0 , param.indexOf("d")));
					}
					
					int sides = -1;

					if(param.matches("[0-9]*d[0-9]+[+-][0-9]+")){
						System.out.println("DICE with a + something");
						int end = param.indexOf("+");
						if(end == -1){
							end = param.indexOf("-");
						}
						String si = param.substring((param.indexOf("d")+1), end);
						System.out.println("SI " + si);
						sides = Integer.parseInt(si);
					}
					else{
						System.out.println("DICE no + something");
						String si2 = param.substring(param.indexOf("d")+1);
						System.out.println("SI2 " + si2);
						sides = Integer.parseInt(si2);
					}


					String overkill = "";

					if(dice > 100){
						dice = 100;
						overkill = "100 dices max";			
					}
					if(sides > 100){
						sides = 100;
						if(overkill.length()>0){
							overkill+= ", ";
						}
						overkill += "100 sides max";
					}


					DiceType dt = new DiceType(sides, dice);

					if(param.matches("[0-9]*d[0-9]*[+-][0-9]*")){
						int start = param.indexOf("+");
						if(start == -1){
							start = param.indexOf("-");
						}
						int modifier = Integer.parseInt(param.substring(start, param.length()));
						if(modifier > 100){
							if(overkill.length()>0){
								overkill+= ", ";
							}
							overkill += "100 as modifier max";
							modifier = 100;
						}
						System.out.println("MODIFIER: " + modifier);
						dt.setModifier(modifier);
					}

					if(overkill.length()>0){
						overkill += ".";
						Tools.sendMessage(overkill);
					}
					Tools.sendMessage(fb.getAuthorNick() + " rolled " + dt.roll().toString());
				}
				catch(NumberFormatException e){
					System.out.println("NUMBERFORMATEXCEPTION\n " + e.getMessage());
				}
			}
			else{
				Tools.sendMessage("Your dice must be built like: (x)dy(+z) where x, y and z are integers, " + fb.getAuthorNick() + ".");
			}

		}		
	}

	@BotCom(command = Handler.GET_THREAD , lvl = ComLvl.USER, type = ComType.MSG, category = ComCategory.USERS)
	public void getThread(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_THREAD, Comparison.EQUALS, ComLvl.USER)){
			int total = Tools.countThread();
			try {
				String thread = ThreadManager.getThread(total);
				Tools.sendMessage("Thread " + total + " link: " + thread);
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
		}
	}

	@BotCom(command = Handler.SET_THREAD , lvl = ComLvl.TRUSTED, type = ComType.MSG, category = ComCategory.TRUSTED)
	public void setThread(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.SET_THREAD, Comparison.STARTS_WITH, ComLvl.TRUSTED)){
			String link = Tools.cutter(fb.getMessage(), " ").elementAt(0);
			try {
				ThreadManager.setThread(link);
				int total = Tools.countThread();
				Tools.sendMessage("New thread added! Number of threads = " + total);
			} catch (SQLException e) {
				// 
				e.printStackTrace();
			}
		}
	}

}
