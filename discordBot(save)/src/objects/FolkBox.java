package objects;

import java.util.Vector;

import handy.Handler;
import handy.Tools;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class FolkBox {

	private String AuthorNick;
	private MessageReceivedEvent ev;
	private Folk author;
	private Vector<Folk> mentionned;
	private String message;
	private Vector<String> arguments;
	
	public FolkBox(){
		this.ev = Handler.ev;
		this.author = Tools.getAuthor();
		this.mentionned = Tools.getMentionned();
		this.message = ev.getMessage().getContent();
		this.arguments = Tools.cuter(message, " ");
		this.AuthorNick = this.author.getNick();
	}

	public MessageReceivedEvent getEv() {
		return ev;
	}

	public void setEv(MessageReceivedEvent ev) {
		this.ev = ev;
	}

	public Folk getAuthor() {
		return author;
	}

	public void setAuthor(Folk author) {
		this.author = author;
	}

	public Vector<Folk> getMentionned() {
		return mentionned;
	}

	public void setMentionned(Vector<Folk> mentionned) {
		this.mentionned = mentionned;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Vector<String> getArguments() {
		return arguments;
	}

	public void setArguments(Vector<String> arguments) {
		this.arguments = arguments;
	}
	
	public String getAuthorNick() {
		return AuthorNick;
	}

	public void setAuthorNick(String authorNick) {
		AuthorNick = authorNick;
	}

	public boolean hasXFolksMin(int x){
		boolean ret = false;
		if(this.mentionned.size() >= x){
			ret = true;
		}
		return ret;
	}
	
	public boolean hasFolks(){
		boolean ret = false;
		if(this.mentionned.size() > 0){
			ret = true;
		}
		return ret;
	}
	
	public Folk getFolkNbX(int x){
		return this.mentionned.elementAt(x);
	}
}
