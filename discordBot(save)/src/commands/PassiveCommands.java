package commands;

import java.sql.SQLException;

import handy.Handler;
import managers.CharacterManager;
import managers.PlayerManager;

public class PassiveCommands {

	public PassiveCommands(String discriminator, String name, String nick){

		addNewPlayer(discriminator, name);
		addNewCharacter(discriminator, name, nick);

	}

	public void addNewPlayer(String discriminator, String name){

		if(!Handler.vPlayer.contains(discriminator)){
			try {
				if(!(PlayerManager.playerExistsFromDiscriminator(discriminator))){
					PlayerManager.setPlayer(discriminator, name);
					System.out.println("added " + discriminator);
				}
				Handler.vPlayer.add(discriminator);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addNewCharacter(String discriminator, String name, String nick){

			if(!Handler.vCharacter.contains("nick")){
				try {
					if(!(CharacterManager.doesCharacterExistFromDiscNick(discriminator, nick))){
						CharacterManager.setCharacter(discriminator, nick);
						System.out.println("character added for " + discriminator + ": " + nick + ".");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
}
