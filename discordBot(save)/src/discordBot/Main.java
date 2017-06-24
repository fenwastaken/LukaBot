package discordBot;

import commands.CommandManager;
import database.PostgreSQLJDBC;
import gui.GUI;
import handy.Handler;
import handy.Tools;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

public class Main {

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try{
			
			PostgreSQLJDBC.getConnection();
			
			Handler h = new Handler();
			
			Handler.jda = new JDABuilder(AccountType.BOT).setToken(Details.TOKEN).buildBlocking();
			Handler.jda.setAutoReconnect(true);
			Handler.jda.addEventListener(new CommandManager(h));
			Handler.gui = new GUI(Handler.jda);
			Handler.gui.setVisible(true);
			Tools.setMethodVector();
			System.out.println("should be good");
			
		}
		catch(Exception e){
			System.out.println("couldn't log in");
		}
		
	}

}
