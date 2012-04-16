package clientchat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 *  Klasa tworząca okno sluzace do odbierania i wysylania wiadomosci
 */

public class ChatWindow extends JFrame {


	private static final long serialVersionUID = 6190230968629495104L;
	private JPanel contentPane;
	private String chatRoomId;
	private JTextPane textPane;
	private StyledDocument doc;
	private JLabel lblRozmawiaszZ;
	private JTextArea textArea;
	private String name;
	private SimpleAttributeSet attributeSet;
	private MainWindow mainWindow;					//handler do głownego okna
	private List<Smile> smiles;						//lista zawierajaca emoticony

	/*
	 * Create the frame.
	 */



	public ChatWindow(MainWindow mainWindow) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {		//podczas zamykania tego okna wysylana jest wiadomosc do pokoju rozmow ze dany uzytkownik wyszedl z niego
				exitChatRoom();
			}
		});

		this.mainWindow = mainWindow;
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 409, 519);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);


		textPane = new JTextPane();
		textPane.setEditable(false);
		doc = (StyledDocument)textPane.getDocument();
		//textPane.setBounds(10, 31, 420, 272);
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setBounds(10, 31, 360, 272);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		JScrollPane scrollPane2 = new JScrollPane(textArea);
		scrollPane2.setBounds(10, 318, 360, 114);
		contentPane.add(scrollPane2);

		JButton sendMessageButton = new JButton("Wy\u015Blij wiadomo\u015B\u0107");
		sendMessageButton.addActionListener(new ActionListener() {		//nacisniecie przycisku wyslij wiadomosc
			public void actionPerformed(ActionEvent arg0) {
				if(!textArea.getText().equalsIgnoreCase("")){
					sendMessage(textArea.getText());
					textArea.setText("");
				}else {
				  JOptionPane.showMessageDialog(null,
            				"Wpisz treść wiadomości",
            				"Brak tresci wiadomości",
            				 JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		sendMessageButton.setBounds(208, 443, 162, 23);
		contentPane.add(sendMessageButton);

		lblRozmawiaszZ = new JLabel("Rozmawiasz z:");
		lblRozmawiaszZ.setBounds(9, 11, 421, 14);
		contentPane.add(lblRozmawiaszZ);

		JButton sendFileButton = new JButton("Wy\u015Blij plik");
		sendFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {		//nacisniecie przycisku wyslij plik
				JFileChooser fileChooser = new JFileChooser();

			    int ret = fileChooser.showDialog(null, "Open file");

			    if (ret == JFileChooser.APPROVE_OPTION) {
			      File file = fileChooser.getSelectedFile();
			      sendFile(file);
			    }
			}
		});
		sendFileButton.setBounds(10, 443, 115, 23);
		contentPane.add(sendFileButton);

		attributeSet = new SimpleAttributeSet();
		initSmiles();								//inicjalizacja listy z buzkami
		setVisible(true);
	}

	public void exitChatRoom(){						//powiadomie serwera ze konczymy czat
		mainWindow.exitChatRoom(chatRoomId);
	}

	public void sendFile(File file){				//wyslanie pliku
        mainWindow.sendFile(chatRoomId, file);
	}

	public void receiveFile(String sender, MyFile myFile){		//odebreanie pliku
		JOptionPane.showMessageDialog(null,
			 sender + " przesyła ci plik o nazwie " + myFile.getFileName(),
			 "Przesyłanie pliku",
			 JOptionPane.INFORMATION_MESSAGE);
		File file = saveFile(myFile);							//zapis na dysku
		if(file!=null){
			JOptionPane.showMessageDialog(null,
					 "Plik został zapisany w: " + file.getAbsolutePath(),
					 "Zapisano pliku",
					 JOptionPane.INFORMATION_MESSAGE);
		}else{
			JOptionPane.showMessageDialog(null,
					 "Problem z zapisaniem pliku: " + myFile.getFileName(),
					 "Problem z zapisem",
					 JOptionPane.ERROR_MESSAGE);
		}
	}

	public File saveFile(MyFile myFile){						//zapis na dysku piku
	    try{
		    FileOutputStream fos = new FileOutputStream(myFile.getFileName());
		    BufferedOutputStream bos = new BufferedOutputStream(fos);
		    bos.write(myFile.getFileByteArray());
		    bos.flush();
		    return new File(myFile.getFileName());
	    }catch( Exception e){
	    	System.out.println("FileReceiver: " +e.getMessage());
	    }
	    return null;
	}

	public void sendMessage(String message){					//wyslanei wiadomosci
		mainWindow.sendMessage(chatRoomId, message);
		receiveMessage(name, message);
	}

	public String getChatRoomId() {
		return chatRoomId;
	}

	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}

	public void setReceivers(String receivers){					//ustawienie rozmowcow
		lblRozmawiaszZ.setText("Rozmawiasz z: "+receivers);
	}

	public void receiveMessage(String sender, String message){  //odebrane wiadomosci i wyswietelnie jej w oknie
		String header = sender + " :  " + getCurrentDateTime();
		String line = "\n------------------------------------------------------------------------------------\n";

		try{
			doc.insertString(doc.getLength(), line, attributeSet);
			doc.insertString(doc.getLength(), header, attributeSet);
			doc.insertString(doc.getLength(), line, attributeSet);
			doc.insertString(doc.getLength(), message, attributeSet);
			doc.insertString(doc.getLength(), "\n", attributeSet);
		}catch(BadLocationException ee){
			System.out.println(ee.getMessage());
		}

		insertSmiles();										    //przerenderowanie wiadomosci i powstawianie buziek

		textPane.requestFocusInWindow();
		textPane.setCaretPosition(doc.getLength());
	}

	private String getCurrentDateTime() {						//pobranie obecnej daty i godziny
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
	}

	private void initSmiles(){									//inicjalizacja buziek
		smiles = new ArrayList<Smile>();
		smiles.add(new Smile(":)", "usmiech", new ImageIcon("./emots/usmiech.gif"))); //jaki jest skrót tej buzki, nazwa, obrazek
		smiles.add(new Smile(":;", "oko", new ImageIcon("./emots/oczko2.gif")));
		smiles.add(new Smile(":L", "jezyk", new ImageIcon("./emots/jezyk.gif")));
		smiles.add(new Smile(":/", "zly", new ImageIcon("./emots/zly.gif")));
		smiles.add(new Smile(":o", "ok", new ImageIcon("./emots/ok.gif")));
	}

	public void insertSmiles(){									//funkcja parsujaca text i wstawiajaca buzki w zamian za skroty
		try {
		    for (Smile smile : smiles) {
			    String text = doc.getText(0, doc.getLength());
			    Style style = doc.addStyle(smile.getDescription(), null);
			    StyleConstants.setIcon(style, smile.getIcon());

			    int index = text.indexOf(smile.getSign());
			    while(index!=-1){
				    doc.remove(index, 2);

				    doc.insertString(index, smile.getDescription(), style);
				    text = doc.getText(0, doc.getLength());
				    index = text.indexOf(smile.getSign());
			    }
		    }
		} catch (BadLocationException e) {
		}
	}

	public void setName(String name) {
		this.name = name;
	}
}
