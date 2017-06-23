package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.Vector;

import annotations.BotCom;
import annotations.ComCategory;
import annotations.ComLvl;
import annotations.ComType;
import annotations.Comparison;
import dice.DiceType;
import handy.Handler;
import handy.Tools;
import managers.CharacterManager;
import managers.PlayerManager;
import managers.ThreadManager;
import objects.Folk;
import objects.FolkBox;

public class BasicCommands {

	/*
	@BotCom(command = Handler.SETTHREAD , lvl = ComLvl.NON_PLAYER, type = ComType.MSG, category = ComCategory.ADMINISTRATION)
	public void setThread(FolkBox fb){
		if(Tools.check(fb.getAuthorNick(), fb.getMessage(), Handler.SETTHREAD, Comparison.STARTS_WITH, ComLvl.ADMIN)){

		}
	}
	}
	 */



	@BotCom(command = Handler.GETAVATAR , lvl = ComLvl.PLAYER, type = ComType.MSG, category = ComCategory.PLAYERS)
	public void getAvatar(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GETAVATAR, Comparison.STARTS_WITH, ComLvl.PLAYER)){
			String target = "";
			String disc = "";
			
			if(fb.hasFolks()){//
				Folk folk = fb.getFolkNbX(0);
				target = folk.getNick();
				disc = folk.getDiscriminator();
			}
			else if(fb.getArguments().elementAt(0).equals("me")){
				Folk auth = fb.getAuthor();
				target = auth.getNick();
				disc = auth.getDiscriminator();
			}

			try {
				System.out.println("SEARCHING FOR " + disc + ", " + target);
				if(CharacterManager.doesCharacterExistFromDiscNick(disc, target)){
					System.out.println("character exists");
					String ret = CharacterManager.getAvatar(disc, target);
					if(ret == null){
						Tools.sendMessage(target + " has no avatar yet.");
					}
					else{
						Tools.sendMessage(target + "'s avatar: " + ret);
					}
				}
				else{
					
					if(fb.hasFolks()){
						Tools.sendMessage("There's no " + target + " registered, " + fb.getAuthorNick() + ".");	
					}
					else{
						Tools.sendMessage("You mentionned no one, " + fb.getAuthorNick());
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@BotCom(command = Handler.SETAVATAR , lvl = ComLvl.USER, type = ComType.MSG, category = ComCategory.USERS)
	public void setAvatar(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.SETAVATAR, Comparison.STARTS_WITH, ComLvl.USER)){
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
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GET_ACC, Comparison.EQUALS, ComLvl.USER)){
			Tools.sendMessage("Your accreditation level is " + ComLvl.ADMIN.getString(Tools.levelChecker(fb.getAuthorDiscriminator()).getValue()) + ", " + fb.getAuthorNick() + ".");
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
			if(param.matches("[0-9]+d[0-9]+[+-][0-9]+") || param.matches("[0-9]+d[0-9]+")){
				try{
					int dice = Integer.parseInt(param.substring(0 , param.indexOf("d")));

					int sides = -1;

					if(param.matches("[0-9]*d[0-9]*[+-][0-9]*")){
						int end = param.indexOf("+");
						if(end == -1){
							end = param.indexOf("-");
						}
						String si = param.substring((param.indexOf("d")+1), end);
						System.out.println("SI " + si);
						sides = Integer.parseInt(si);
					}
					else{
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
				Tools.sendMessage("Your dice must be built like: xdy or xdy+z where x, y and z are integers, " + fb.getAuthorNick() + ".");
			}

		}		
	}

	@BotCom(command = Handler.GETTHREAD , lvl = ComLvl.USER, type = ComType.MSG, category = ComCategory.USERS)
	public void getThread(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.GETTHREAD, Comparison.EQUALS, ComLvl.USER)){
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

	@BotCom(command = Handler.SETTHREAD , lvl = ComLvl.TRUSTED, type = ComType.MSG, category = ComCategory.TRUSTED)
	public void setThread(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.SETTHREAD, Comparison.STARTS_WITH, ComLvl.TRUSTED)){
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
