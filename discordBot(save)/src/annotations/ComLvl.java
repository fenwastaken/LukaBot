package annotations;

public enum ComLvl {

	 ADMIN(0), GM(1), PLAYER(2), NON_PLAYER(3), BANNED(4);

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
			ret = "GM";
			break;
		case 2:
			ret = "PLAYER";
			break;
		case 4:
			ret = "BANNED";
			break;
		default:
			ret = "NON_PLAYER";
		}
		
		return ret;
	  }
}
