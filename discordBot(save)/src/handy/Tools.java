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
import commands.CharacterCommands;
import commands.Games;
import commands.PermaCommands;
import managers.CharacterManager;
import managers.PlayerManager;
import managers.ThreadManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.UserImpl;
import objects.Folk;
import objects.FolkBox;
import ohdata.OHRoles;

public class Tools {

	/**
	 * returns true if the string parameter is numeric.
	 * @param str
	 * @return boolean
	 */
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

	/**
	 * sends a message on the channel contained in the last event,
	 * if null it will try to send to the last pmed user. WIP
	 * @param text
	 */
	public static void sendMessage(String text){
		if(!(Handler.channel == null)){
			Handler.channel.sendMessage(text).queue();
		}
	}
	
	public static void sendPrivateMessage(String text, Folk f){
		List<User> u = Handler.jda.getUsers();
		User user = Handler.jda.getUserById(f.getId());
		
		if(user != null){
			if(!user.hasPrivateChannel()){
				user.openPrivateChannel().complete();
			}
			((UserImpl)user).getPrivateChannel().sendMessage(text).queue();
		}
		else{
			Tools.sendMessage("User not found");
		}
	}

	/**
	 * feeds the Handler's method vectors if they have annotations
	 */
	public static void setMethodVector(){
		Handler.vBasicMethods = new Vector<Method>();
		Handler.vPermaMethods = new Vector<Method>();
		Handler.vCharacterMethods = new Vector<>();
		Handler.vGamesMethods = new Vector<Method>();

		Method[] basicMethods = BasicCommands.class.getMethods();
		Method[] permaMethods = PermaCommands.class.getMethods();
		Method[] characterMethods = CharacterCommands.class.getMethods();
		Method[] gamesMethods = Games.class.getMethods();

		for (Method m : basicMethods){
			if (m.isAnnotationPresent(BotCom.class)){
				Handler.vBasicMethods.add(m);
			}
		}

		for (Method m : permaMethods){
			if (m.isAnnotationPresent(BotCom.class)){
				Handler.vPermaMethods.add(m);
			}
		}

		for (Method m : characterMethods){
			if (m.isAnnotationPresent(BotCom.class)){
				Handler.vCharacterMethods.add(m);
			}
		}
		
		for (Method m : gamesMethods){
			if (m.isAnnotationPresent(BotCom.class)){
				Handler.vGamesMethods.add(m);
			}
		}
	}

	/**
	 * Builds the help String from methods
	 * @return String
	 */
	public static String helpMaker(){
		Vector<String> vUsers = new Vector<String>();
		Vector<String> vPlayers = new Vector<String>();
		Vector<String> vGameMasters = new Vector<String>();
		Vector<String> vTrusted = new Vector<String>();
		Vector<String> vAdministration = new Vector<String>();

		Method[] methodsBasic = BasicCommands.class.getMethods();
		Method[] methodsPerma = PermaCommands.class.getMethods();
		Method[] methodsCharacter = CharacterCommands.class.getMethods();
		Method[] methodsGames = Games.class.getMethods();

		Vector<Method[]> vecMethods = new Vector<>();
		Vector<Method> vecMethodsAll = new Vector<>();

		vecMethods.add(methodsBasic);
		vecMethods.add(methodsPerma);
		vecMethods.add(methodsCharacter);
		vecMethods.add(methodsGames);

		for(Method[] meth : vecMethods){
			for(Method m : meth){
				vecMethodsAll.add(m);
			}
		}


		for (Method m : vecMethodsAll){
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


		String ret = "```";

		if(vUsers.size() > 0){
			ret += "-Users:\n ";

			for(String str : vUsers){
				ret += "  " +  str + "\n ";
			}
		}

		if(vPlayers.size() > 0){
			ret = ret.substring(0, ret.length() - 1);
			ret += "\n-Players:\n ";

			for(String str : vPlayers){
				ret += "  " +   str + "\n ";
			}
		}

		if(vGameMasters.size() > 0){
			ret = ret.substring(0, ret.length() - 1);
			ret += "\n-Game Masters:\n ";

			for(String str : vGameMasters){
				ret += "  " +   str + "\n ";
			}
		}

		if(vTrusted.size() > 0){
			ret = ret.substring(0, ret.length() - 1);
			ret += "\n-Trusted:\n ";

			for(String str : vTrusted){
				ret += "  " +   str + "\n ";
			}
		}

		if(vAdministration.size() > 0){
			ret = ret.substring(0, ret.length() - 1);
			ret += "\n-Admin:\n ";

			for(String str : vAdministration){
				ret += "  " + str + "\n ";
			}
		}

		ret = ret.substring(0, ret.length() - 2);
		ret = ret + "```";
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
		case STARTS_EQUALS:
			if(message.toLowerCase().startsWith(skey(command).toLowerCase() + " ") || message.toLowerCase().equals(skey(command).toLowerCase())){
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
				sendMessage("Your rank is insufficient for that command.");
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
	 * Rebuilds a nick from the vector argument from the element at "wholeParameter" excluded.
	 * @param wholeParameter
	 * @param vec
	 * @param separator
	 * @return String
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
		System.out.println("REBUILT: " + ret);
		return ret;

	}

	/**
	 * Removes the nick of the mentionned Folk in the FolkBox's argument vector. Useful to get rid of names with spaces mostly.
	 * @param fb
	 * @param target
	 */
	public static void argumentNickRemover(FolkBox fb, Folk target){
		if(target.getNick().indexOf(" ") != -1){
			String nickPiece = "@" + target.getNick().substring(0, target.getNick().indexOf(" "));
			int position = fb.getArguments().indexOf(nickPiece);
			while(fb.getArguments().size() > position){
				System.out.println("REMOVING " + fb.getArguments().lastElement());
				fb.getArguments().remove(fb.getArguments().lastElement());
			}
		}else{
			System.out.println("REMOVING: " + "@" + target.getNick());
			fb.getArguments().removeElement("@" + target.getNick());
		}
	}

	/**
	 * Return the number of threads in the database
	 * @return int
	 */
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

	/**
	 * Used for building Folkboxes
	 * @param wanted int
	 * @return Folk
	 */
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

	/**
	 * Used for building Folkboxes
	 * @return Vector<Folk>
	 */
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

	/**
	 * Used for building Folkboxes
	 * @return
	 */
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
	
	public static String codeBlock(String str){
		return "```" + str + "```";
	}

}
