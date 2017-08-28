package commands;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.tools.Tool;

import annotations.BotCom;
import annotations.ComCategory;
import annotations.ComLvl;
import annotations.ComType;
import annotations.Comparison;
import cards.Deck;
import dice.DiceResult;
import dice.DiceType;
import gnu.trove.impl.HashFunctions;
import handy.Handler;
import handy.Tools;
import managers.CharacterManager;
import managers.PlayerManager;
import managers.ThreadManager;
import managers.UrlsManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
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
				if((fb.getArguments().size() > 0)){
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
						Tools.sendMessage("Here's what I got: " + ret);
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
			Handler.ev.getChannel().deleteMessageById(Handler.ev.getMessageId());
		}
	}
	
	@BotCom(command = Handler.TEST, lvl = ComLvl.ADMIN, type = ComType.MSG, category = ComCategory.ADMINS)
	public void test(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.TEST, Comparison.STARTS_WITH, ComLvl.ADMIN)){
			String line = Tools.mentionRemover(fb);
			Tools.sendMessage("HERE: " + line);
			
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
			String help = Tools.helpMaker();
			Tools.sendPrivateMessage(help, fb.getAuthor());
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
			String message = fb.getMessage();
			String param = message.substring(message.indexOf(" ") + 1);
			String endMessage = "";
			int end = -1; //dynamic end of the dice part before the hypothetic string part
			if(param.matches("[0-9]*d[0-9]+([+-][0-9]+)?([ ](.*))?")){
				try{
					int dice = 0;
					//checks the part before d, if there is none dice number is 1
					if(param.matches("d[0-9]+([+-][0-9]+)?([ ](.*))?")){
						System.out.println("no number before d");
						dice = 1;
					}else{
						System.out.println("number before d");
						dice = Integer.parseInt(param.substring(0 , param.indexOf("d")));
					}

					System.out.println("NUMBER OF DICE: " + dice);

					int sides = -1;
					//get the number of sides
					end = param.indexOf("+");
					if(end == -1){
						end = param.indexOf("-");
					}
					if(end == -1){
						end = param.indexOf(" ");
					}
					if(end == -1){
						end = param.length();
					}
					String si = param.substring((param.indexOf("d")+1), end);
					sides = Integer.parseInt(si);

					System.out.println("NUMBER OF SIDES: " + sides);

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

					//matches with a dice that has a modifier
					if(param.matches("[0-9]*d[0-9]+[+-][0-9]+([ ](.*))?")){
						int start = param.indexOf("+");
						if(start == -1){
							start = param.indexOf("-");
						}

						end = param.indexOf(" ", start);
						if(end == -1){
							end = param.length();
						}

						int modifier = Integer.parseInt(param.substring(start, end));
						if(modifier > 100 || modifier < -100){
							if(overkill.length()>0){
								overkill+= ", ";
							}
							overkill += "(-/+)100 as modifier max";
							if(modifier > 100){
								modifier = 100;
							}
							else{
								modifier = -100;
							}
						}
						dt.setModifier(modifier);
					}
					else{
						//no modifier
						end = param.indexOf(" ", param.indexOf("d"));
						if(end == -1){
							end = param.length();
						}
					}

					if(overkill.length()>0){
						overkill += ".";
						Tools.sendMessage(overkill);
					}
					DiceResult dr = dt.roll();
					String ret = fb.getAuthorNick() + " rolled " + dr.toString();

					//gets message if there is one
					if(end != -1 && end < param.length()){
						System.out.println("END: " + end + ", LENGTH" + + param.length());
						endMessage = param.substring(end + 1, param.length());
						System.out.println("END MESSAGE: " + endMessage);

						ret += " " + endMessage;

						//gets the check expression if there is one
						if(endMessage.startsWith("/")){
							int endCheck = endMessage.indexOf(" ");
							if(endCheck == -1){
								endCheck = endMessage.length();
							}
							String check = endMessage.substring(1, endCheck);
							System.out.println("CHECK: " + check + ", DR: " + dr.toInt());
							if(Tools.isNumeric(check)){
								endCheck = Integer.parseInt(check);
								if((dr.toInt()) >= endCheck){
									ret+= " SUCCESS!!";
								}
								else{
									ret+= " FAILED!!";
								}
							}
						}
					}

					//check for fumble or critical
					if(dr.toInt() - dt.getModifier() == 1){
						ret += " FUMBLE!";
					}

					if(dr.toInt() == sides){
						ret += " CRITICAL!";
					}

					Tools.sendMessage(ret);
				}
				catch(NumberFormatException e){
					System.out.println("NUMBERFORMATEXCEPTION\n " + e.getMessage());
				}
			}
			else if(fb.getArguments().elementAt(0).equals("player")){
				System.out.println("IIIIIN");

				Vector<String> vecRoles = new Vector<>();
				for(Role r : Handler.ev.getGuild().getRoles()){
					vecRoles.add(r.getName().toLowerCase());	
				}

				if(fb.getArguments().size() > 1){
					String argument1 = fb.getArguments().elementAt(1);
					if(Tools.isNumeric(argument1)){
						if(fb.getArguments().size() > 2){
							//1 number 1 role
							System.out.println(" in here");
							String raw = Tools.reBuilder(-1, fb.getArguments(), " ");
							String argument2 = raw.substring(raw.indexOf(" ", 7) + 1);
							System.out.println("ARGUMENT 2 " + argument2);
							System.out.println(vecRoles.toString() + " !! " + argument2);
							if(vecRoles.contains(argument2)){
								System.out.println(" 1 number 1 role");
								rollPlayer(Integer.parseInt(argument1), argument2);
							}
						}
						else{
							//1 number no role
							rollPlayer(Integer.parseInt(argument1), "");
							System.out.println(" 1 number no role");
						}
					}
					else{
						String argument2 = Tools.reBuilder(0, fb.getArguments(), " ").toLowerCase();
						System.out.println("ARGUMENT 2: " + argument2);
						if(vecRoles.contains(argument2)){
							rollPlayer(1, argument2);
						}
						System.out.println(" no number 1 role");
					}
				}
				else{
					rollPlayer(1, "");
					System.out.println(" no number no role");
				}
			}
			else{
				Tools.sendMessage("Your dice must be built like: (x)dy(+z)(\" \"message) "
						+ "where x, y and z are integers, the message can be anything "
						+ "as long as it starts with a whitespace, or you can also use " + 
						Handler.key + Handler.ROLL + " player (integer (role)/role), " + fb.getAuthorNick() + ".");
			}

		}		
	}

	public void rollPlayer(int number, String role){

		Vector<String> vecRoles = new Vector<>();
		for(Role r : Handler.ev.getGuild().getRoles()){
			vecRoles.add(r.getName().toLowerCase());	
		}

		if(number > 100){
			number = 100;
			Tools.sendMessage(number + " players max!");
		}

		List<Member> lUser = Handler.ev.getGuild().getMembers();
		Vector<String> vecNick = new Vector<>();
		for(Member u : lUser){

			if(role.equals("")){
				if(!u.getUser().isBot()){
					String name = Handler.ev.getGuild().getMemberById(u.getUser().getId()).getNickname();
					if(name == null){
						name = Handler.ev.getGuild().getMemberById(u.getUser().getId()).getEffectiveName();
					}
					vecNick.add(name);
				}
			}
			else{
				if(!u.getUser().isBot()){
					Vector<String> userRoles = new Vector<>();
					for(Role r : u.getRoles()){
						userRoles.add(r.getName().toLowerCase());
					}
					if(userRoles.contains(role)){
						String name = Handler.ev.getGuild().getMemberById(u.getUser().getId()).getNickname();
						if(name == null){
							name = Handler.ev.getGuild().getMemberById(u.getUser().getId()).getEffectiveName();
						}
						vecNick.add(name);
					}

				}
			}
		}

		Vector<String> vecRet = new Vector<>();
		for(int i = 0; i < number; i++){
			System.out.println("VECNICK " + vecNick.toString());
			DiceType dt = new DiceType(vecNick.size(), 1);
			DiceResult dr = dt.roll();

			if(vecNick.size() == 0){
				Tools.sendMessage("No one was found!");
				return;
			}
			else{
				int num = dr.toInt() - 1;
				System.out.println("NUM " + num);
				vecRet.add(vecNick.elementAt(num));
			}
			
		}

		Collections.sort(vecRet);

		String ret = "";
		for(String st : vecRet){
			ret += st + ", ";
		}
		ret = ret.substring(0, ret.length() - 2);

		String formula =  "picked among " + vecNick.size() + " players!";

		if(vecRet.size() == 1){
			formula = " was " + formula;
		}
		else{
			ret = "```" + ret + "```";
			formula = " were " + formula;
		}

		Tools.sendMessage(ret + formula);
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
