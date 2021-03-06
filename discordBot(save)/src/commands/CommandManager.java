package commands;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import com.mashape.unirest.request.GetRequest;

import annotations.BotCom;
import annotations.ComType;
import discordBot.Details;
import handy.Handler;
import handy.Tools;
import managers.CharacterManager;
import managers.PlayerManager;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import objects.FolkBox;

public class CommandManager extends ListenerAdapter{

	Handler h = null;
	Games games = new Games();
	
	public CommandManager(Handler h){
		this.h = h;
	}

	public void onReady(ReadyEvent event){

	}

	public void onMessageUpdate(MessageUpdateEvent event) {
		MessageReceivedEvent eve = new MessageReceivedEvent(Handler.jda, event.getResponseNumber(), event.getMessage());
		commands(eve);
	}
	
//	public void onPrivateMessageReceived(PrivateMessageReceivedEvent event){
//		Handler.ev = new MessageReceivedEvent(Handler.jda, event.getResponseNumber(), event.getMessage());
//		Handler.channel = null;
//		Handler.upm = event.getAuthor();
//		Tools.helpMaker();
//	}
	
	public void onMessageReceived(MessageReceivedEvent event){
		if(!event.getAuthor().isBot()){
			commands(event);
		}
	}
	
	public void commands(MessageReceivedEvent event){
		Handler.ev = event;
		Handler.channel = event.getTextChannel();
		
		//that fb goes through all methods
		FolkBox fb = new FolkBox();

		System.out.println(fb.getMessage() + " | " + fb.getAuthor().getDiscriminator() + " | " + fb.getAuthor().getName()
				+ " | " + fb.getAuthorNick() + " | " + Handler.channel.getName());


		if(!fb.getAuthor().getDiscriminator().equals(Details.DISCRIMINATOR)){
			PassiveCommands pc = new PassiveCommands(fb);

			//basic
			if(!Handler.locked){
				BasicCommands basic = new BasicCommands();
				for(Method m : Handler.vBasicMethods){
					try {
						BotCom bc = m.getAnnotation(BotCom.class);
						if(bc.type() == ComType.MSG || bc.type() == ComType.BOTH){ //checks that the command is intended for public message
							m.invoke(basic, fb);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//other commands go there
				CharacterCommands character = new CharacterCommands();
				for(Method m : Handler.vCharacterMethods){
					try {
						BotCom bc = m.getAnnotation(BotCom.class);
						if(bc.type() == ComType.MSG || bc.type() == ComType.BOTH){ //checks that the command is intended for public message
							m.invoke(character, fb);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//games
				for(Method m : Handler.vGamesMethods){
					try {
						BotCom bc = m.getAnnotation(BotCom.class);
						if(bc.type() == ComType.MSG || bc.type() == ComType.BOTH){ //checks that the command is intended for public message
							m.invoke(games, fb);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			//perma
			PermaCommands perma = new PermaCommands();
			for(Method m : Handler.vPermaMethods){
				try {
					BotCom bc = m.getAnnotation(BotCom.class);
					if(bc.type() == ComType.MSG || bc.type() == ComType.BOTH){ //checks that the command is intended for public message
						m.invoke(perma, fb);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}
}
