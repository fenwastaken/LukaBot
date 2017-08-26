package handy;

import java.lang.reflect.Method;
import java.util.Vector;

import discordBot.Details;
import gui.GUI;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Handler {

	public static JDA jda = null;
	
	public static GUI  gui = null;
	
	public static MessageReceivedEvent ev = null;
	public static TextChannel channel = null;
	public static User upm = null;

	
	//infos
	public static String versionNumber = "1.15";
	public static String botName = Details.NAME.substring(0, Details.NAME.indexOf("#"));

	//global bot variables
	public static String admin = "Spot-On"; //must be set manually, used only for reference
	public static String key = "!";
	public static boolean locked = false;
	public static boolean greetingsLocked = false;
	
	public static Vector<Method> vBasicMethods;
	public static Vector<Method> vPermaMethods;
	public static Vector<Method> vCharacterMethods;
	public static Vector<Method> vGamesMethods;
	
	public static Vector<String> vPlayer = new Vector<>();
	public static Vector<String> vCharacter = new Vector<>();

	//command names
	final public static String GET_VERSION = "ver";
	final public static String SET_KEY = "key";
	final public static String GET_HELP = "help";
	final public static String HELLO = "hello";
	final public static String GET_ACC = "acc";
	final public static String YOUTUBE = "yt";
	final public static String ROLL = "r";

	final public static String SET_THREAD = "threadset";
	final public static String GET_THREAD = "thread";

	final public static String GET_CHARACTERS = "character";
	final public static String LAST_SEEN = "lastseen";
	/**/final public static String LOCK = "luka";/**/ //must be set manually
	/*/final public static String LOCK = "lynn";/**/ //must be set manually

	final public static String RANK = "rankset";
	final public static String GET_LINK = "link";
	final public static String SET_LINK = "linkset";
	final public static String STATS = "stats";
	final public static String DEACTIVATE = "deactivate";
	//inventory
	final public static String MAKE_ITEM = "make";
	final public static String EXPORT_ITEM = "export";
	final public static String CLEAR_INVENTORY = "clear";
	final public static String GET_INVENTORY = "inv";
	//avatar
	final public static String SET_AVATAR = "avaset";
	final public static String SET_THUMBNAIL = "thumbnailset";
	final public static String GET_AVATAR = "avatar";
	final public static String GET_AVATARLESS = "avatarless";
	final public static String GET_AVATARFULL = "avatarfull";
	final public static String REMOVE_AVATAR = "avaboom";
	//pouch
	final public static String GET_POUCH = "pouch";
	final public static String GIVE_MONEY = "pay";
	final public static String ALTER_MONEY = "money";
	//don't work atm
	final public static String GIVE_ITEM = "give";
	final public static String REM_ITEM = "take";
	
	//games
		//hangman
	final public static String GUESS = "guess";
	final public static String GENERATE_WORD = "hangman";
		//cards
	final public static String MAKE_DECK = "makedeck";
	final public static String DRAW = "draw";
	final public static String REVEAL = "reveal";
	final public static String SHUFFLE = "shuffle";
	final public static String DISCARD = "discard";
	
	//channel names
	final public static String CHAN_MAIN = "general";
	final public static String CHAN_FUN = "autismcentral";
	
}
