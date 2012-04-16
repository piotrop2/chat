package serverchat;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Główna klasa serwerwa która przyjmuje nowych klientów na sockecie. Zarzadza watkami klientów i chat roomami
 */
public class ChatServer {

	private static Map<String, ClientHandle> registeredClients = new HashMap<String, ClientHandle>();
	private static List<String> clientsNames = new ArrayList<String>();
    private static Integer chatRoomsNumber = new Integer(0);
    private static Map<String, ChatRoom> chatRooms = new HashMap<String, ChatRoom>();

public class PropertiesTest {
        //Plik z konfiguracj¹
        private File f = new File("config.properties");
        //przyszly obiekt Properties
        private Properties properties = new Properties();
       
        public void loadProperties(){
                //Strumien wejsciowy
                InputStream is;
                try {
                        is = new FileInputStream(f);
                        //ladujemy nasze ustawienia
                        properties.load(is);
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}
    
    void runMyServer() throws IOException
    {

                        
                // pobieranie z pliku properties
                System.setProperty("config.properties", "UTF-8");
                PropertiesTest pt = new PropertiesTest();
                pt.loadProperties();
                String port_s = pt.properties.getProperty("PORT");
                int PORT = Integer.parseInt(port_s);
                
		//tworzenie gniazda serwera
		ServerSocket server = new ServerSocket(PORT);       //ustawienie nasluchiwania na porcie 2011

		System.out.println("Server run ... ");
		while(true){
            //Akceptacja polaczenia;
			Socket socket =  server.accept();				//po zaakceptowniu polaczenia z klientem tworzy watek który obsługuje klienta
			System.out.println("New client..");
			new ClientHandle(socket);
			//Tworzenie watku obsugujacego klienta
		}
    }

    public static boolean registerClient(String clientName, ClientHandle client){     //funkcja do rejestrowania watkow klientów
    	if(registeredClients.containsKey(clientName))
    		return false;
    	else {
    		registeredClients.put(clientName, client);								  //dozucenie do hasmapy klienta w postaci pary 'nazwa_klienta'->watek_obsługujacy_tego_klienta
    		clientsNames.add(clientName);											  //potrzebne do generowania listy uzytkownikow gotowych do czatu
    		return true;
    	}
    }

    public static Message getList(String clientName){				    //funkcja tworzaca wiadomosc typu LIST zawierającą liste uzytkownikow gotowych do czatu
    	Message message = new Message();
    	message.setType(MessageType.LIST);

    	String receivers = null;
    	int clientNumber = clientsNames.size();
    	if(clientNumber>1){
    		receivers="";
    		for(String tmp: clientsNames)
    			if(!tmp.equals(clientName))
    				receivers += tmp +",";
    		receivers = receivers.substring(0, receivers.length()-1);
    	}
    	message.setMessage(receivers);

    	return message;
    }

    public static void removeClient(String clientName){					//funkcja usuwajaca watek klienta z hashmapy i listy imion
    	if(registeredClients.containsKey(clientName)){
    		registeredClients.remove(clientName);
    		clientsNames.remove(clientName);
    	}
    }

    public static void createChatRoom(Message message){					//funkcja tworzaca chatRoom
    	List<ClientHandle> clients = new ArrayList<ClientHandle>();
    	String clientList[] = message.getMessage().split(",");			//wydzielenie osob ktore biora udzial w tym chat roomie
    	for(String tmp: clientList){
    		if(registeredClients.containsKey(tmp))
    			clients.add(registeredClients.get(tmp));				//dodanie ich do tablicy
    	}
    	if(registeredClients.containsKey(message.getSender()))			//dodanie do listy watku osoby ktora tworzy chat room
			clients.add(registeredClients.get(message.getSender()));
    	ChatRoom chr = new ChatRoom(chatRoomsNumber.toString(), clients, message.getSender());	//stworzenie chat roomu
    	chatRooms.put(chatRoomsNumber.toString(), chr);					//dodanie do listy chat romoom
    	chatRoomsNumber++;
    }

    public static void chatConfirmation(Message message){				//funkca ktora przekazuje potwierdzenie lub odrzuceni checi udzialu w chat roomie
    	int answ = 1;
    	try{
    		answ = Integer.parseInt(message.getMessage());
    	}catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    	boolean operation = chatRooms.get(message.getChatRoom()).confirm(message.getSender(), answ == 0 ? true : false); //przekazanie odpowiedzi do odpowiedniego chat roomu
    	if(operation)
    		removeChatRoom(message.getChatRoom());
    }

    public static void removeChatRoom(String id){						//usuniecie chat room'u
    	if(chatRooms.containsKey(id))
    		chatRooms.remove(id);
    }

    public static void receiveMessage(Message message){					//funkcja odbierajaca wiadomosc od klienta i przekazujaca ja do chat room'u
    	if(chatRooms.containsKey(message.getChatRoom()))
    		chatRooms.get(message.getChatRoom()).receiveMessage(message);
    }

    public static void receiveFile(Message message, MyFile myFile){		//funkcja odbierajaca wiadomosc oraz plik
    	if(chatRooms.containsKey(message.getChatRoom()))
    		chatRooms.get(message.getChatRoom()).receiveFile(message, myFile);
    }

    public static void main(String[] args) {
        try {
            new ChatServer().runMyServer();								//odpalenie serwera
        }catch(IOException e) {};
    }



}