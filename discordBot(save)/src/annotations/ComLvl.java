package annotations;

public enum ComLvl {

	 ADMIN(0), TRUSTED(1), GAMEMASTER(2),  PLAYER(3), USER(4), BANNED(5);

	  private int value;    

	  private ComLvl(int value) {
	    this.value = value;
	  }

	  public int getValue() {
	    return value;
	  }
	
	  public String getString(int value){
		String ret = "";
		
		switch(value){
		
		case 0:
			ret = "ADMIN";
			break;
		case 1:
			ret = "TRUSTED";
			break;
		case 2:
			ret = "GAMEMASTER";
			break;
		case 3:
			ret = "PLAYER";
			break;
		case 5:
			ret = "BANNED";
			break;
		default:
			ret = "USER";
		}
		
		return ret;
	  }
}
