package objects;

public class Folk {

	private String name;
	private String nick;
	private String discriminator;
	private String id;
	
	public Folk(String discriminator, String name, String nick, String id){
		this.discriminator = discriminator;
		this.name = name;
		
		if(nick == null || nick.equals("") || nick.equals("null")){
			nick = name;
		}
		this.nick = nick;
		this.id = id;
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
	
	
	
	
}
