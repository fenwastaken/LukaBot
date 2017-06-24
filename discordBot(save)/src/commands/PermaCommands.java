package commands;

import java.sql.SQLException;

import annotations.BotCom;
import annotations.ComCategory;
import annotations.ComLvl;
import annotations.ComType;
import annotations.Comparison;
import handy.Handler;
import handy.Tools;
import managers.PlayerManager;
import objects.FolkBox;

public class PermaCommands {

	@BotCom(command = Handler.LOCK , lvl = ComLvl.ADMIN, type = ComType.MSG, category = ComCategory.ADMINS)
	public void lock(FolkBox fb){
		if(Tools.check(fb.getAuthorDiscriminator(), fb.getMessage(), Handler.LOCK, Comparison.STARTS_WITH, ComLvl.ADMIN)){
			String str = fb.getMessage().substring(Handler.key.length() + Handler.LOCK.length());
			if(str.equalsIgnoreCase(" take a break")){
				Tools.sendMessage("Alright, I'll only watch for a while.");
				Handler.locked = true;
			}
			else if(str.equalsIgnoreCase(" break's over")){
				Tools.sendMessage("Good! It was getting dull!");
				Handler.locked = false;
			}
		}
	}
	
	
	
}
