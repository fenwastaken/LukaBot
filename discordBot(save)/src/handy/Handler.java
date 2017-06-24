package handy;

import java.lang.reflect.Method;
import java.util.Vector;

import discordBot.Details;
import gui.GUI;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Handler {

	public static JDA jda = null;
	
	public static GUI  gui = null;
	
	public static MessageReceivedEvent ev = null;
	public static TextChannel channel = null;

	
	//infos
	public static String versionNumber = "0.7b";
	public static String botName = Details.NAME.substring(0, Details.NAME.indexOf("#"));

	//global bot variables
	public static String admin = "Spot-On"; //must be set manually, used only for reference
	public static String key = "!";
	public static boolean locked = false;
	public static Vector<Method> vBasicMethods;
	public static Vector<Method> vPermaMethods;
	public static Vector<String> vPlayer = new Vector<>();
	public static Vector<String> vCharacter = new Vector<>();

	//command names
	final public static String GET_VERSION = "version";
	final public static String SET_KEY = "key";
	final public static String GET_HELP = "help";
	final public static String HELLO = "hello";
	final public static String GET_ACC = "acc";
	final public static String YOUTUBE = "yt";
	final public static String ROLL = "r";
	final public static String SETAVATAR = "avaset";
	final public static String GETAVATAR = "avatar";
	final public static String GETFC = "4c";
	final public static String SETFC = "4cset";
	final public static String SETTHREAD = "threadset";
	final public static String GETTHREAD = "thread";
	final public static String GET_CHARACTERS = "character";
	final public static String LAST_SEEN = "lastseen";
	final public static String LOCK = "luka"; //must be set manually
	
}
