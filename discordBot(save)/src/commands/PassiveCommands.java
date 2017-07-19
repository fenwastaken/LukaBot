package commands;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dice.DiceResult;
import dice.DiceType;
import handy.Handler;
import handy.Tools;
import managers.CharacterManager;
import managers.PlayerManager;
import objects.FolkBox;

public class PassiveCommands {

	FolkBox fb;

	public PassiveCommands(FolkBox fb){

		this.fb = fb;

		addNewPlayer(fb);
		addNewCharacter(fb);

	}

	public void addNewPlayer(FolkBox fb){

		String discriminator = fb.getAuthorDiscriminator();
		String name = fb.getAuthor().getName();

		if(!Handler.vPlayer.contains(discriminator)){
			try {
				if(!(PlayerManager.playerExistsFromDiscriminator(discriminator))){
					PlayerManager.setPlayer(discriminator, name);
					System.out.println("added " + discriminator);
				}
				Handler.vPlayer.add(discriminator);
				PlayerManager.updatePlayerName(name, discriminator);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		updateDate(discriminator);
	}

	public void updateDate(String discriminator){
		try {
			sayHello();
			PlayerManager.updatePlayerDate(discriminator);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sayHello() throws SQLException{

		if(PlayerManager.getDate(fb.getAuthorDiscriminator()) != null && moreThanADay(PlayerManager.getDate(fb.getAuthorDiscriminator()))){

			DiceType dt = new DiceType(10, 1);
			String ret = "";
			int result = dt.roll().toInt();
			switch(result){
			case 1:
				ret = "Welcome back, " + fb.getAuthorNick() + "!";
				break;
			case 2:
				ret = "Oh hey " + fb.getAuthorNick() + ".";
				break;
			case 3:
				ret = "Hello " + fb.getAuthorNick() + "!";
				break;
			case 4:
				ret = "Look at who's back! Hey " + fb.getAuthorNick() + "!";
				break;
			case 5:
				ret = "Good to see you " + fb.getAuthorNick() + "!";
				break;
			case 6:
				ret = "Hello helo " + fb.getAuthorNick() + " !";
				break;
			case 7:
				ret = "This " + fb.getAuthorNick() + " has returned! Hi!";
				break;
			case 8:
				ret = "I see a wild " + fb.getAuthorNick() + " appeared! Hey!";
				break;
			case 9:
				ret = "How are you doing " + fb.getAuthorNick() + "?";
				break;
			case 10:
				ret = "Oh! " + fb.getAuthorNick() + "! I missed you!";
				break;
			}
			Tools.sendMessage(ret);
		}
	}

	public boolean moreThanADay(Date date){
		final long MILLIS_PER_DAY = (24 * 60 * 60 * 1000L)/2;
		Date current = new Date();

		boolean moreThanDay = Math.abs(current.getTime() - date.getTime()) >= MILLIS_PER_DAY;

		return moreThanDay;
	}

	public void addNewCharacter(FolkBox fb){

		String discriminator = fb.getAuthorDiscriminator();
		String nick = fb.getAuthorNick();

		if(!Handler.vCharacter.contains("nick")){
			try {
				if(!(CharacterManager.doesCharacterExistFromNick(nick))){
					if(CharacterManager.getCharacterNbFromDiscriminator(discriminator) < 3){
						CharacterManager.setCharacter(discriminator, nick);
						System.out.println("character added for " + discriminator + ": " + nick + ".");
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
