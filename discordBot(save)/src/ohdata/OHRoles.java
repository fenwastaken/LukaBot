package ohdata;

public enum OHRoles {

	 COMISSAR("Commissar"), 
	 BBBOT("BB's Bots"),
	 GM("Gallant Masochists (GMs)"),
	 SUPP_MAIN("Support Mains"),
	 BOT_WORK("Botworks Engineer"),
	 BASED_ARTIST("Based Artists"),
	 PLAYER("Players"),
	 LURKER("LURKER"),
	 FROG_MOD("Frog Mod");

	private final String name;       

	private OHRoles(String s) {
		name = s;
	}

	public String getValue(){
		return name;
	}



}
