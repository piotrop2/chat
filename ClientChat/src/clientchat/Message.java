package clientchat;

import java.io.Serializable;

/**
 * Klasa służaca do przekazywania wiadomosci pomiedzy serwer-klient
 */

public class Message implements Serializable {

	private static final long serialVersionUID = 1085460939321457525L;
	private MessageType type;
	private String chatRoom;
	private String sender;
	private String message;
	
	public Message(){}
	public Message(MessageType type, String chatRoom, String sender, String message){
		this.type = type;
		this.chatRoom = chatRoom;
		this.sender = sender;
		this.message = message;
	}
	
	public MessageType getType() {
		return type;
	}
	public void setType(MessageType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getChatRoom() {
		return chatRoom;
	}
	public void setChatRoom(String chatRoom) {
		this.chatRoom = chatRoom;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
}
