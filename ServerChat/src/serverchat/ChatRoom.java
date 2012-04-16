package serverchat;

import java.util.List;

/*
 * Klasa ktora reprezentuje pokój rozmow (chat room).
 * Posiada liste osob które czatuja ze soba
 * Informacje kto stworzy ten pokoj oraz unikalny
 * identyfikator pokoju chatRoomId
 */

public class ChatRoom
{
    private String chatRoomId;
    private List<ClientHandle> clients;
    private String creator;
    private boolean confirmed[];

    public ChatRoom(String chatRoomId, List<ClientHandle> clients, String creator) {
        this.chatRoomId = chatRoomId;
        this.clients = clients;
        this.creator = creator;
        this.confirmed = new boolean[clients.size()];

        confirm(creator, true);
        invite();
    }

    private void invite(){						//funkcja wysylajaca zaproszenia do chat roomu
    	for(ClientHandle client: clients){
    		String clientsList = getListOfChaters(client.getClientName());
    		if(!client.getClientName().equalsIgnoreCase(creator)){
	    		Message message = new Message(MessageType.INVITATION_TO_CHAT, chatRoomId, creator, clientsList);
	    		client.sendMessage(message);
    		}
    	}
    }

    private String getListOfChaters(String receiver){ //funkcja zwracajaca liste osob w danym pokoju jako ciag tekstowy
		String clientsList ="";
		for(ClientHandle tmp: clients){
			if(!tmp.getClientName().equalsIgnoreCase(receiver))
				clientsList += tmp.getClientName() +",";
		}
		return clientsList;
    }

    public boolean confirm(String clientName, boolean value){	//funkcja do potwierdzania chceci wziecia udzialu w rozmowie
    	if(!value){												//jesli ktos sie nie zgodzi czat room jest zamykany
    		exitChat(clientName);
    		return true;
    	}
    	for(int i=0; i<clients.size(); i++){
    		if(clients.get(i).getClientName().equalsIgnoreCase(clientName)){
    			confirmed[i] = value;
    		}
    	}
    	if(checkConfirmations())								//jesli wszyscy sie zgodzili powiadom klientow ze zaczynaja czat
    		startChat();
    	return false;
    }

    public boolean checkConfirmations(){						//sprawdzenie czy wszyscy potwierdzili chec wziecia udzialu w rozmowie
    	int size = 0;
    	for(int i=0; i<confirmed.length; i++)
    		if(confirmed[i]) size++;

    	return size == confirmed.length;
    }

    public boolean isChatRoomEmpty(){							//jak w nazwie
    	return clients.isEmpty();
    }

    public void exitChat(String clientName){					//zamkniecie calego pokoju
		Message message = new Message(MessageType.EXIT_CHAT, chatRoomId, clientName, null);
		for(int i=0; i<confirmed.length; i++) {
			if(confirmed[i] && !clients.get(i).getClientName().equalsIgnoreCase(clientName))
				clients.get(i).sendMessage(message);
		}
    }

    public void startChat(){									//funkcja ktora powiadamia osoby w pokoju ze zaczynamy czat
    	for(ClientHandle client: clients){
    		String clientsList = getListOfChaters(client.getClientName());
    		Message message = new Message(MessageType.START_CHAT, chatRoomId, null, clientsList);
    		client.sendMessage(message);
    	}
    }

    public void sendMessage(Message message){					//wyslanie wiadomosci do wszystkich
    	for(ClientHandle client: clients){
    		client.sendMessage(message);
    	}
    }

    public boolean exitChatRoom(String clientName){				//funkcja ktora sluzy do obslugi osob ktore sie rozlaczaja podzczas czatu
    	for(int i=0; i<clients.size(); i++) {
	    	if(clients.get(i).getClientName().equalsIgnoreCase(clientName)) {
	    		clients.remove(i);
	    		break;
	    	}
    	}

    	return isChatRoomEmpty();
    }

    public void receiveMessage(Message message){				//funkcja wysylajaca wiadomosc do wszystkich z pokoju oprocz nadawcy wiadomosci
    	for(ClientHandle client: clients){
    		if(!client.getClientName().equalsIgnoreCase(message.getSender()))
    			client.sendMessage(message);
    	}
    }

    public void receiveFile(Message message, MyFile myFile){	//odebranie pliku
    	for(ClientHandle client: clients){
    		if(!client.getClientName().equalsIgnoreCase(message.getSender())){
    			client.sendMessage(message);
    			client.sendFile(myFile);
    		}
    	}
    }
}
