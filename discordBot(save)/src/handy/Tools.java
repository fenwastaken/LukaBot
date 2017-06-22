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
import commands.CommandManager;
import managers.ThreadManager;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import objects.Folk;

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
		Vector<String> vBasic = new Vector<String>();
		Vector<String> vPlayerC = new Vector<String>();
		Vector<String> vIngame = new Vector<String>();
		Vector<String> vGM = new Vector<String>();
		Vector<String> vAdministration = new Vector<String>();

		Method[] methods = BasicCommands.class.getMethods();

		for (Method m : methods){
			if (m.isAnnotationPresent(BotCom.class)){
				BotCom bc = m.getAnnotation(BotCom.class);



				String command = Handler.key + bc.command();

				if(bc.type() == ComType.MSG || bc.type() == ComType.BOTH){

					switch(bc.category()){

					case BASIC:
						vBasic.add(command);
						break;

					case PLAYER_CREATION:
						vPlayerC.add(command);
						break;

					case INGAME:
						vIngame.add(command);
						break;

					case GM:
						vGM.add(command);
						break;

					case ADMINISTRATION:
						vAdministration.add(command);
						break;

					}
				}
			}
		}

		Collections.sort(vBasic);
		Collections.sort(vPlayerC);
		Collections.sort(vIngame);
		Collections.sort(vGM);
		Collections.sort(vAdministration);


		String ret = "";

		if(vBasic.size() > 0){
			ret += "Basic: ";

			for(String str : vBasic){
				ret += str + ", ";
			}
		}

		if(vPlayerC.size() > 0){
			ret += "Player Creation: ";

			for(String str : vPlayerC){
				ret += str + ", ";
			}
		}

		if(vIngame.size() > 0){
			ret += "Ingame: ";

			for(String str : vIngame){
				ret += str + ", ";
			}
		}

		if(vGM.size() > 0){
			ret += "GM: ";

			for(String str : vGM){
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

		return (vBasic.size() + vPlayerC.size() + vIngame.size() + vGM.size() + vAdministration.size()) + " commands: " + ret;

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
	public static boolean check(String nick, String message, String command, Comparison comparison, ComLvl minLvl){
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

		ComLvl senderLvl = levelChecker(nick);

		if(senderLvl.getValue() > minLvl.getValue()){
			ret = false;
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
	public static ComLvl levelChecker(String nick){

		ComLvl lvl = ComLvl.NON_PLAYER;

		for(String str : Handler.admin){
			if(nick.equals(str)){
				lvl = ComLvl.ADMIN;	
			}
		}
		return lvl;
	}

	/**
	 * returns the last word of a string [using space as a separator]
	 * if paramNumber = 0, 1 would return the word before the last, etc
	 * @param message
	 * @param paramNumber
	 * @return
	 */
	public static String lastParameter(String message, int paramNumber){
		Vector<String> vec = cuter(message, " ");
		return vec.elementAt(vec.size() - (1 - paramNumber));
	}

	public static Vector<String> cuter(String message, String separator){
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
			
			
			Folk folk = new Folk(disc, name, nick, id);
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
			Folk f = new Folk(disc, name, nick, id);
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
		Folk folk = new Folk(disc, name, nick, id);
		
		return folk;
	}
	
}
