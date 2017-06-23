package handy;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import annotations.BotCom;
import annotations.ComLvl;
import annotations.ComType;
import annotations.Comparison;
import commands.BasicCommands;
import managers.CharacterManager;
import managers.PlayerManager;
import managers.ThreadManager;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import objects.Folk;
import objects.FolkBox;
import ohdata.OHRoles;

public class Tools {

	public static boolean isNumeric(String str)  
	{  
		try  
		{  
			int i = Integer.parseInt(str);  
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;  
	}

	public static void sendMessage(String text){
		Handler.channel.sendMessage(text).queue();
	}

	public static void setMethodVector(){
		Handler.vMethod = new Vector<Method>();
		Method[] methods = BasicCommands.class.getMethods();

		for (Method m : methods){
			if (m.isAnnotationPresent(BotCom.class)){
				Handler.vMethod.add(m);
			}
		}
	}

	public static String helpMaker(){
		Vector<String> vUsers = new Vector<String>();
		Vector<String> vPlayers = new Vector<String>();
		Vector<String> vGameMasters = new Vector<String>();
		Vector<String> vTrusted = new Vector<String>();
		Vector<String> vAdministration = new Vector<String>();

		Method[] methods = BasicCommands.class.getMethods();

		for (Method m : methods){
			if (m.isAnnotationPresent(BotCom.class)){
				BotCom bc = m.getAnnotation(BotCom.class);



				String command = Handler.key + bc.command();

				if(bc.type() == ComType.MSG || bc.type() == ComType.BOTH){

					switch(bc.category()){

					case USERS:
						vUsers.add(command);
						break;

					case PLAYERS:
						vPlayers.add(command);
						break;

					case GAMEMASTERS:
						vGameMasters.add(command);
						break;

					case TRUSTED:
						vTrusted.add(command);
						break;

					case ADMINS:
						vAdministration.add(command);
						break;

					}
				}
			}
		}

		Collections.sort(vUsers);
		Collections.sort(vPlayers);
		Collections.sort(vGameMasters);
		Collections.sort(vTrusted);
		Collections.sort(vAdministration);


		String ret = "";

		if(vUsers.size() > 0){
			ret += "Users: ";

			for(String str : vUsers){
				ret += str + ", ";
			}
		}

		if(vPlayers.size() > 0){
			ret += "Players: ";

			for(String str : vPlayers){
				ret += str + ", ";
			}
		}

		if(vGameMasters.size() > 0){
			ret += "Game Masters: ";

			for(String str : vGameMasters){
				ret += str + ", ";
			}
		}

		if(vTrusted.size() > 0){
			ret += "Trusted: ";

			for(String str : vTrusted){
				ret += str + ", ";
			}
		}

		if(vAdministration.size() > 0){
			ret += "Admin: ";

			for(String str : vAdministration){
				ret += str + ", ";
			}
		}

		ret = ret.substring(0, ret.length() - 2);
		ret += ".";

		return (vUsers.size() + vPlayers.size() + vGameMasters.size() + vTrusted.size() + vAdministration.size()) + " commands: " + ret;

	}


	/**
	 * compare the message and the command using the right key.
	 * Types of comparisons:
	 * equals [eq]
	 * starts [st]
	 * ends [en]
	 * contains [co]
	 * @param message
	 * @param command
	 * @param comparison
	 * @return
	 */
	public static boolean check(String discriminator, String message, String command, Comparison comparison, ComLvl minLvl){
		boolean ret = false;

		switch(comparison){
		case EQUALS:
			if(message.toLowerCase().equals(skey(command).toLowerCase())){
				ret = true;
			}
			break;
		case STARTS_WITH:
			if(message.toLowerCase().startsWith(skey(command).toLowerCase() + " ")){
				ret = true;
			}
			break;
		case ENDS_WITH:
			if(message.toLowerCase().endsWith(skey(command).toLowerCase())){
				ret = true;
			}
			break;
		case CONTAINS:
			if(message.toLowerCase().contains(skey(command).toLowerCase())){
				ret = true;
			}
			break;
		default:
			ret = false;
		}

		if(ret == true){//no need to check permission if no command is used beforehand
			ComLvl senderLvl = levelChecker(discriminator);

			if(senderLvl.getValue() > minLvl.getValue()){
				ret = false;
			}
		}

		return ret;
	}

	public static String skey(String command){
		return Handler.key + command;
	}


	/**
	 * Checks the accreditation lvl of a nick
	 * @param nick
	 * @return
	 */
	public static ComLvl levelChecker(String discriminator){
		ComLvl ret = null;
		try {
			int dbLvl = PlayerManager.getPlayerRank(discriminator);
			FolkBox fb = new FolkBox();
			if(CharacterManager.hasAvatar(discriminator) 
					&& fb.getAuthor().hasRole(OHRoles.PLAYER) 
					&& dbLvl > 3 
					&& dbLvl != 5){
				PlayerManager.updatePlayerRank(3, discriminator);
				System.out.println(discriminator + " is now rank 3");
				dbLvl = 3;
			}
			switch(dbLvl){
			case 5:
				ret = ComLvl.BANNED;
				break;
			case 4:
				ret = ComLvl.USER;
				break;
			case 3:
				ret = ComLvl.PLAYER;
				break;
			case 2:
				ret = ComLvl.GAMEMASTER;
				break;
			case 1:
				ret = ComLvl.TRUSTED;
				break;
			case 0:
				ret = ComLvl.ADMIN;
				break;
			default:
				ret = ComLvl.USER;
				break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(discriminator + " " + ret.getString(ret.getValue()));
		return ret;
	}

	/**
	 * returns the last word of a string [using space as a separator]
	 * if paramNumber = 0, 1 would return the word before the last, etc
	 * @param message
	 * @param paramNumber
	 * @return
	 */
	public static String lastParameter(String message, int paramNumber){
		Vector<String> vec = cutter(message, " ");
		return vec.elementAt(vec.size() - (1 - paramNumber));
	}

	public static Vector<String> cutter(String message, String separator){
		Vector<String> vec = new Vector<String>();
		int start = message.indexOf(separator) + 1;
		int stop = message.indexOf(separator, start);
		String ret = "";

		while(stop <= message.length() && stop != -1){
			ret = message.substring(start, stop);
			vec.add(ret);
			start = stop + 1;
			stop = message.indexOf(separator, start);
		}

		ret = message.substring(start);
		vec.add(ret);

		return vec;
	}

	/**
	 * fooking discord allowing names with spaces is fucking mu cuter
	 * @param wholeParameter
	 * @param vec
	 * @param separator
	 * @return
	 */
	public static String reBuilder(int wholeParameter, Vector<String> vec, String separator){

		String ret = "";
		if(wholeParameter != -1){
			vec.remove(wholeParameter);
		}
		for(String str : vec){
			ret += str + separator;
		}

		ret = ret.substring(0, ret.length() - separator.length());
		return ret;

	}

	public static int countThread(){
		int nbr = -3;
		try {
			nbr = ThreadManager.countRow();
		} catch (SQLException e) {
			// 
			e.printStackTrace();
		}
		return nbr;
	}

	public static Folk getMentionNb(int wanted){
		Vector<Folk> vecu = Tools.getMentionned();
		String name = "";
		String nick = "";
		String disc = "";
		String id = "";
		if(vecu.size()> wanted - 1){
			id = vecu.elementAt(wanted).getId();
			disc = vecu.elementAt(wanted).getDiscriminator();
			nick = Handler.ev.getGuild().getMemberById(vecu.elementAt(0).getId()).getNickname();
			name = vecu.elementAt(wanted).getName();
			List<Role> lr = Handler.ev.getGuild().getMemberById(id).getRoles();

			Folk folk = new Folk(disc, name, nick, id, lr);
			return folk;

		}
		return null;
	}

	public static Vector<Folk> getMentionned(){
		List<User> list = Handler.ev.getMessage().getMentionedUsers();
		Vector<Folk> ret = new Vector<>();
		for(User u : list){
			String name = u.getName();
			String nick = Handler.ev.getGuild().getMemberById(u.getId()).getNickname();
			String disc = u.getDiscriminator();
			String id = u.getId();
			List<Role> lr = Handler.ev.getGuild().getMemberById(id).getRoles();					
			Folk f = new Folk(disc, name, nick, id, lr);
			ret.add(f);
		}
		return ret;
	}

	public static Folk getAuthor(){
		User auth = Handler.ev.getAuthor();
		String disc = auth.getDiscriminator();
		String name = auth.getName();
		String nick = Handler.ev.getGuild().getMemberById(auth.getId()).getNickname();
		String id = auth.getId();
		List<Role> lr = Handler.ev.getGuild().getMemberById(id).getRoles();
		Folk folk = new Folk(disc, name, nick, id, lr);

		return folk;
	}

}
