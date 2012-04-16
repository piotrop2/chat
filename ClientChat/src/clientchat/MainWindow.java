package clientchat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Startowe okno aplikacji
 */
public class MainWindow {

	private JFrame frame;
	private static String host;
	private static Integer port;
	private JTextField textField;
	private Client client;
	private java.awt.List usersList;
	private boolean isConnected;
	private JButton connectButton;
	private static MainWindow window;
	private static Map<String, ChatWindow> chatWindows = new HashMap<String, ChatWindow>();   //lista okien (chat roomow)

	/*
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/*
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Multiczat");
		frame.setBounds(100, 100, 196, 502);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if(isConnected)
					client.closeConnection();		//podczas zamykania okna powiadamia serwer ze sie rozlaczyl
			}
		});
		isConnected = false;
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("Menu");
		menuBar.add(mnNewMenu);
		host = "localhost";
		port = 2011;

		JMenuItem mntmKonfiguracja = new JMenuItem("Konfiguracja");
		mntmKonfiguracja.addActionListener(new ActionListener() {	//wybranie z menu opcji konfiguracja
			public void actionPerformed(ActionEvent arg0) {
				new Configuration(window);
			}
		});
		mnNewMenu.add(mntmKonfiguracja);

		JMenuItem mntmZakocz = new JMenuItem("Zako\u0144cz");
		mnNewMenu.add(mntmZakocz);
		frame.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Nick :");
		lblNewLabel.setBounds(10, 11, 32, 14);
		frame.getContentPane().add(lblNewLabel);

		textField = new JTextField();
		textField.setBounds(49, 8, 117, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		connectButton = new JButton("Po\u0142\u0105cz");
		connectButton.addActionListener(new ActionListener() {			//klikniecie przycisku poalacz
			public void actionPerformed(ActionEvent arg0) {
				String name = textField.getText();
				if(!host.isEmpty() && port>1024 && !name.isEmpty() ) {	//jesli pola sa wypelnione
					if(!isConnected){
						client = new Client(host, port, name, window);	//tworzy polaczenie z serwerem (Nowa klasę obslugujaca wymiane danych z serwerem)
						isConnected = true;
						connectButton.setText("Roz\u0142acz");
					}else {
						client.closeConnection();
						isConnected = false;
						connectButton.setText("Po\u0142\u0105cz");
					}

				}else {
                    JOptionPane.showMessageDialog(null,
            				"Nieprawidłowe dane do połączenia",
            				"Nieprawidłowe dane do połączenia",
            				 JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		connectButton.setBounds(77, 34, 89, 23);
		frame.getContentPane().add(connectButton);

		usersList = new java.awt.List();
		usersList.setBounds(10, 68, 156, 291);
		usersList.setMultipleSelections(true);
		frame.getContentPane().add(usersList);

		JButton btnNewButton = new JButton("Chat");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {			//klikniecie przycisku czat
				String[] users = usersList.getSelectedItems();
				if(users.length > 0){
					client.invitationToChat(users);					//zaproszenie do czatu
				}else{
					  JOptionPane.showMessageDialog(null,
	            				"Zaznacz użytkowników z listy z którymi chcesz rozmawiać",
	            				"Brak zaznaczenia",
	            				 JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton.setBounds(77, 399, 89, 23);
		frame.getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Odśwież listę");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(isConnected)
					client.getList();							//odsierzenie listy osob
			}
		});
		btnNewButton_1.setBounds(10, 369, 156, 23);
		frame.getContentPane().add(btnNewButton_1);
	}

	public  void setHostAndPort(String h, Integer p){
		host = h;
		port = p;
	}

	public  String[] getHostAndPort(){
		return new String[] { host, Integer.toString(port) };
	}

	public void setUsersList(String usersString){				//ustawienie listy uzytkownikow do konwersacji
		if(usersString!=null){
			String users[] = usersString.split(",");
			usersList.removeAll();
			for (String user : users) {
				usersList.add(user);
			}
		}
	}

	public void receiveMessage(String chatRoomId, String sender, String message){   //odebranie wiadomosci i przekazanie jej do odpowiedniego okna
		if(chatWindows.containsKey(chatRoomId))
			chatWindows.get(chatRoomId).receiveMessage(sender, message);
	}

	public void receiveFile(String chatRoomId,String sender, MyFile myFile){	    //odebranie pliku i przekazanie go do odpowiedniego okna
		if(chatWindows.containsKey(chatRoomId))
			chatWindows.get(chatRoomId).receiveFile(sender, myFile);
	}

	public int confirmInvitationToChat(String message){								//okno dialogowe do potwierdzeia checi wziecia udzialu w czacie
		int ans = JOptionPane.showConfirmDialog(null
							 ,"Czy zgadzasz sie na rozmowe z: "+message
							 ,"Propozycja rozmowy"
							 ,JOptionPane.YES_NO_OPTION	);

		return ans;
	}

	public void chatReject(String client){											//powiadomienie klienta ze klienci odrzucili chęc chatu
		JOptionPane.showMessageDialog(null
				 ,"Rozmowa została odrzucona przez : "+client
				 ,"Odrzucona rozmowy"
				 ,JOptionPane.INFORMATION_MESSAGE);
	}

	public void startChat(String chatWindowId, String chatList){					//rozpoczecie czatu, utorzenie i ustawienie okna czatu
		ChatWindow chatWindow = new ChatWindow(this);
		chatWindow.setChatRoomId(chatWindowId);
		chatWindow.setReceivers(chatList);
		chatWindow.setName(client.getClientName());
		chatWindows.put(chatWindowId, chatWindow);
	}

	public void sendMessage(String chatRoomId, String message){						//wysylanie wiadomosci
		client.sendMessage(chatRoomId, message);
	}

	public void exitChatRoom(String chatRoomId){									//zamkniecie okna czatu = zakonczenie chatu
		client.exitChatRoom(chatRoomId);
		chatWindows.remove(chatRoomId);
	}

	public void sendFile(String chatRoomId, File file){								//wysylanei pliku
		client.sendFile(chatRoomId, file);
	}
}
