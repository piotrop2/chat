package serverchat;

import java.io.*;
import java.net.*;


/**
 * Klasa ktora jest wątkiem obsługującym odbieranie i wysylanie danych na sockecie dla danego klienta
 */

class ClientHandle extends Thread
{
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String clientName;


    public ClientHandle(Socket s) {
        try {
          socket=s;
          oos = new ObjectOutputStream(socket.getOutputStream());
          ois = new ObjectInputStream(socket.getInputStream());
          start();
        }catch (IOException e) {};
    }

    public void run() {
    	boolean flag=true;
        try {
         while(flag)
         {
    	 	Message message = (Message) ois.readObject();							//pobranie wiadomosci od klienta
    	 	switch(message.getType()){
    	 		case EXIT: {
	    	 					flag = false;
	    	 					closeConection();
	    	 					ChatServer.removeClient(clientName);				//usuniecie klienta, (klient sie rozlaczyl)
	    	 					break;
    	 					}
    	 		case REGISTER:{
    	 						clientName = message.getSender();
    	 						ChatServer.registerClient(clientName, this);		//rejestracja klienta
    	 						Message tmpMessage = ChatServer.getList(clientName);
    	 						sendMessage(tmpMessage);							//przeslanie listy osob dostepnych do rozmowy
    	 						break;
    	 					}
    	 		case LIST: 	{
    	 						Message tmpMessage = ChatServer.getList(clientName);//lista osob dostepnych (odswierzenie listy osob przez klienta)
    	 						sendMessage(tmpMessage);
    	 						break;
    	 					}
    	 		case INVITE_TO_CHAT: 	{
    	 						ChatServer.createChatRoom(message); 				//zaproszenie do czatu
								break;
							}
    	 		case CONFIRM_CHAT: 	{
    	 						ChatServer.chatConfirmation(message);				//potwierdzenie zaproszenia
								break;
							}
    	 		case MESSAGE: {
    	 						ChatServer.receiveMessage(message);					//wiadomosc
								break;
							}
    	 		case FILE:  {														//plik
    	 						MyFile myFile = (MyFile)ois.readObject();
    	 						ChatServer.receiveFile(message, myFile);
								break;
							 }

    	 	}


          }
         }catch (Exception e) {
        	 System.out.println(e.getMessage());
        };
    }


    public void sendMessage(Message message) {		//wysylanie wiadomosci do socketa klienta
    	try{
    		oos.writeObject(message);
    		oos.flush();
    	}catch(IOException ee) {}
    }

    public String getClientName(){
    	return clientName;
    }

    public void sendFile(MyFile myFile){		    //wysylanie pliku do socketa klienta
    	try{
    		oos.writeObject(myFile);
    		oos.flush();
    	}catch(IOException ee) {}
    }

    public void closeConection() {					//zamkniecie polaczenia
        try {
        	ois.close();
        	oos.close();
            socket.close();
            System.out.println(clientName +" close connection");
        }catch(IOException e) {};
    }

}