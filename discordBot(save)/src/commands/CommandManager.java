package commands;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import annotations.BotCom;
import annotations.ComType;
import discordBot.Details;
import handy.Handler;
import handy.Tools;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import objects.FolkBox;

public class CommandManager extends ListenerAdapter{

	Handler h = null;

	public CommandManager(Handler h){
		this.h = h;
	}

	public void onMessageReceived(MessageReceivedEvent event){
		Handler.admin.add("Spot-On");
		Handler.admin.add("Daybreak");

		Handler.ev = event;
		Handler.channel = event.getTextChannel();
		//System.out.println("channel == " + Handler.channel.getName());
		//String message = Handler.ev.getMessage().getContent().toString();
		//String author = Handler.ev.getMessage().getAuthor().getName();
		//String name = Handler.ev.getAuthor().getName();
		//String discriminator = Handler.ev.getAuthor().getDiscriminator();
		//String nick = Handler.ev.getMessage().getMember().getNickname();
		//String channel = Handler.ev.getMessage().getChannel().getName();

		FolkBox fb = new FolkBox();
		
		System.out.println(fb.getMessage() + " | " + fb.getAuthor().getDiscriminator() + " | " + fb.getAuthor().getName()
		+ " | " + fb.getAuthorNick() + " | " + Handler.channel.getName());

		if(!fb.getAuthor().getDiscriminator().equals(Details.botDicriminator)){
			PassiveCommands pc = new PassiveCommands(fb.getAuthor().getDiscriminator(), fb.getAuthor().getName(), fb.getAuthorNick());

			BasicCommands basic = new BasicCommands();
			for(Method m : Handler.vMethod){
				try {
					BotCom bc = m.getAnnotation(BotCom.class);
					if(bc.type() == ComType.MSG || bc.type() == ComType.BOTH){ //checks that the command is intended for public message
						m.invoke(basic, fb);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
