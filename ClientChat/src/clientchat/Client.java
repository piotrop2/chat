package clientchat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Klasa wątek obsługująca wymianę danych pomiedzy serwerem a klientem
 */
public class Client extends Thread {
	private String host;
	private Integer port;
	private Socket socket;
	private String clientName;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private MainWindow mainWindowHandle;    //uchwyt do glowego okna

	public Client(String host, Integer port, String name, MainWindow mainWindowHandle) {
		this.host = host;
		this.port = port;
		this.clientName = name;
		this.mainWindowHandle = mainWindowHandle;
		start();
	}

	public Client() { start(); }		//odpalenie watku

	private void connect() {			//polacznei z serwerem
		try{
			socket = new Socket(host, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
	        ois = new ObjectInputStream(socket.getInputStream());
		}catch(IOException ee) {
			System.out.println(ee.getMessage());
		}
	}

    public void run()
    {
		connect();
		register();
    	boolean flag=true;
        try {
         while(flag)
         {
    	 	Message message = (Message) ois.readObject();		//odebranie wiadomosci
    	 	switch(message.getType()){
    	 		case LIST: 	{									//lista rozmowcow
    	 						mainWindowHandle.setUsersList(message.getMessage());
    	 						break;
    	 					}
    	 		case INVITATION_TO_CHAT: 	{					//zaproszenie na czat
								int answer = mainWindowHandle.confirmInvitationToChat(message.getMessage());
								responseOnInvitation(answer, message.getChatRoom());
								break;
							}
    	 		case EXIT_CHAT: {								//zakonczenie czatu
    	 						mainWindowHandle.chatReject(message.getSender());
	 							break;
    	 					}
    	 		case START_CHAT: {								//rozpoczecie czatu
								mainWindowHandle.startChat(message.getChatRoom(), message.getMessage());
								break;
							}

    	 		case MESSAGE: {									//wiadomosc
    	 						mainWindowHandle.receiveMessage(message.getChatRoom(), message.getSender(), message.getMessage());
    	 						break;
							}
    	 		case FILE: {									//plik
    	 						MyFile myFile = (MyFile)ois.readObject();
								mainWindowHandle.receiveFile(message.getChatRoom(),message.getSender(), myFile);
								break;
    	 					}

    	 	}


          }
         }catch (Exception e) {};
    }

    public void sendMessage(Message message) {			//wyslanie wiadomosci
    	try{
			oos.writeObject(message);
			oos.flush();
    	}catch(IOException ee) {}
    }

    public void register(){								//rejestracja klienta
    	Message message = new Message(MessageType.REGISTER, null, clientName, null);
    	sendMessage(message);
    }

    public void getList(){								//prozba o przeslanie listy osob
    	Message message = new Message(MessageType.LIST, null, clientName, null);
    	sendMessage(message);
    }

    public void responseOnInvitation(int answer, String chatRoomId){	//odpowiedz na zaproszenie do czatu
    	Message message = new Message(MessageType.CONFIRM_CHAT, chatRoomId, clientName, Integer.toString(answer));
    	sendMessage(message);
    }

    public void invitationToChat(String[] receivers){    //zaproszenie do czatu
    	String receiverList = "";
    	for (String string : receivers) {
    		receiverList += string + ",";
		}
    	receiverList = receiverList.substring(0, receiverList.length()-1);
    	Message message = new Message(MessageType.INVITE_TO_CHAT, null, clientName, receiverList);
    	sendMessage(message);
    }

    public void sendMessage(String chatRoomId, String message){//wyslanie wiadomosci do konkretnego pokoju rozmow (chat room'a)
    	Message messageObj = new Message(MessageType.MESSAGE, chatRoomId, clientName, message);
    	sendMessage(messageObj);
    }

    public void closeConnection(){							//zamkniecie polaczenia
    	Message message = new Message(MessageType.EXIT, null, clientName, null);  //powiadomienie serwera
    	sendMessage(message);
        try
        {
        	ois.close();
        	oos.close();
            socket.close();
        }catch(IOException e) {};
    }

	public String getClientName() {
		return clientName;
	}

	public void exitChatRoom(String chatRoomId){			//wyjscie z chat rooma
    	Message message = new Message(MessageType.EXIT_CHAT, chatRoomId, clientName, null);
    	sendMessage(message);
	}

	public void sendFile(String chatRoomId,File file){		//wyslanie pliku
    	Message message = new Message(MessageType.FILE, chatRoomId, clientName, file.getName());  //powiadomienie ze zaraz przyjdzie plik
    	sendMessage(message);

		try{
	        byte [] fileByteArray  = new byte [(int)file.length()];
	        FileInputStream fis = new FileInputStream(file);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        bis.read(fileByteArray,0,fileByteArray.length);

	        MyFile myFile = new MyFile(file.getName(), fileByteArray);   //obudowanie pliku w klase MyFile

			sendFile(myFile);
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	public void sendFile(MyFile myFile){		//wyslanie pliku
		try{
			oos.writeObject(myFile);
			oos.flush();
		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}
}
