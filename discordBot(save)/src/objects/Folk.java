package objects;

import java.util.List;

import net.dv8tion.jda.core.entities.Role;
import ohdata.OHRoles;

public class Folk {

	private String name;
	private String nick;
	private String discriminator;
	private String id;
	private List<Role> lr;
	
	public Folk(String discriminator, String name, String nick, String id, List<Role> lr){
		this.discriminator = discriminator;
		this.name = name;
		
		if(nick == null || nick.equals("") || nick.equals("null")){
			nick = name;
		}
		this.nick = nick;
		this.id = id;
		this.lr = lr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean hasRole(OHRoles role){
		boolean ret = false;
		for(Role r : this.lr){
			if(r.getName().equals(role.getValue())){
				ret = true;
			}
		}
		return ret;
	}
	
	
}
