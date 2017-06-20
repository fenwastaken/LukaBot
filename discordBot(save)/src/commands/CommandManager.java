package commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Vector;

import annotations.BotCom;
import annotations.ComCategory;
import annotations.ComLvl;
import annotations.ComType;
import annotations.Comparison;
import dice.DiceType;
import discordBot.Details;
import handy.Handler;
import managers.CharacterManager;
import managers.PlayerManager;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandManager extends ListenerAdapter{

	Handler h = null;
	
	public CommandManager(Handler h){
		this.h = h;
	}





	public void onMessageReceived(MessageReceivedEvent event){
		Handler.ev = event;
		Handler.channel = event.getTextChannel();
		System.out.println("channel == " + Handler.channel.getName());
		String message = Handler.ev.getMessage().getContent().toString();
		String author = Handler.ev.getMessage().getAuthor().getName();
		String name = Handler.ev.getAuthor().getName();
		String discriminator = Handler.ev.getAuthor().getDiscriminator();
		String nick = Handler.ev.getMessage().getMember().getNickname();
		String channel = Handler.ev.getMessage().getChannel().getName();
		System.out.println(message + " | " + discriminator + " | " + author + " | " + nick + " | " + channel);

		if(!discriminator.equals(Details.botDicriminator)){
			PassiveCommands pc = new PassiveCommands(discriminator, name, nick);

			if(nick == null){
				nick = name;
			}


			for(Method m : Handler.vMethod){
				BasicCommands basic = new BasicCommands();
				try {
					BotCom bc = m.getAnnotation(BotCom.class);
					if(bc.type() == ComType.MSG || bc.type() == ComType.BOTH){ //checks that the command is intended for public message
						m.invoke(basic, message, discriminator, nick, channel);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
