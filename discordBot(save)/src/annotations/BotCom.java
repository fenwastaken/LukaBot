package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.lang.model.element.Element;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotCom {

	/**
	 * raw text that triggers the command, uses a key
	 * @return
	 */
	String command();
	
	/**
	 * accreditation required to use the command
	 * @return
	 */
	ComLvl lvl();
	
	/**
	 * Where this command is usable [MSG, PMSG, BOTH]
	 * @return
	 */
	ComType type();

	/**Category of the command
	 * 
	 * @return
	 */
	ComCategory category();
	
}
