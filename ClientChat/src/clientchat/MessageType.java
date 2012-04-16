package clientchat;

import java.io.Serializable;

/**
 * Enum służacy do rozrużniania typów wiadomości przesyłanych pomiedy serwerem a klientem
 */

public enum MessageType implements Serializable{
	EXIT,
	LIST,
	MESSAGE,
	INVITE_TO_CHAT,
	INVITATION_TO_CHAT,
	START_CHAT,
	CONFIRM_CHAT,
	EXIT_CHAT,
	FILE,
	REGISTER;
}
