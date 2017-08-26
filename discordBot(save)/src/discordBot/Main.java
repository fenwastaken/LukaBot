package discordBot;

import java.io.InputStream;
import java.util.Scanner;

import commands.CommandManager;
import commands.Games;
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

		
		Scanner scan = new Scanner(System.in);
		int again = 0;

		while(again == 0){
			try{

				if(!Details.SIMPLE_NAME.equals("Luka")){
					Handler.key = "§";
					Handler.greetingsLocked = true;
				}

				PostgreSQLJDBC.getConnection();

				Handler h = new Handler();

				Handler.jda = new JDABuilder(AccountType.BOT).setToken(Details.TOKEN).buildBlocking();
				Handler.jda.setAutoReconnect(true);
				Handler.jda.addEventListener(new CommandManager(h));
				Handler.gui = new GUI(Handler.jda);
				Handler.gui.setVisible(true);
				Tools.setMethodVector();
				System.out.println("should be good");
				again = 1;
			}
			catch(Exception e){
				System.out.println("couldn't log in");
				try{
					System.out.println("1 to keep trying, 0 to quit");
					again = scan.nextInt();
				}
				catch(NumberFormatException d){
					again = 0;
				}
			}
			
			Games.setVector();
			
		}
	}

}
