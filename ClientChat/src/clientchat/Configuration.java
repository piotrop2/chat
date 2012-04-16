package clientchat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * Klasa która tworzy okno do konfiguracji polaczenia z serwerem
 */
public class Configuration extends JFrame {

	/*
	 *
	 */
	private static final long serialVersionUID = 13232L;

	private JPanel contentPane;
	private JTextField hostTextField;
	private JTextField portTextField;
	private MainWindow mainWindow;					//handler do głównego okna

	/*
	 * Create the frame.
	 */
	public Configuration(MainWindow window) {
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setWindow(window);
		setTitle("Konfiguracja");
		setBounds(100, 100, 217, 151);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Host :");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(10, 11, 46, 14);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Port :");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_1.setBounds(10, 35, 46, 14);
		contentPane.add(lblNewLabel_1);

		hostTextField = new JTextField();
		hostTextField.setBounds(51, 9, 136, 20);
		contentPane.add(hostTextField);
		hostTextField.setColumns(10);

		portTextField = new JTextField();
		portTextField.setBounds(101, 33, 86, 20);
		contentPane.add(portTextField);
		portTextField.setColumns(10);

		JButton saveButton = new JButton("Zapisz/Wyjdz");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mainWindow.setHostAndPort(hostTextField.getText(), Integer.parseInt(portTextField.getText()));
				dispose();
			}
		});
		saveButton.setBounds(76, 78, 114, 23);
		contentPane.add(saveButton);
		setFields(mainWindow.getHostAndPort());
		setVisible(true);
	}

	public void setFields(String[] data){
		hostTextField.setText(data[0]);
		portTextField.setText(data[1]);
	}

	public void setWindow(MainWindow mainWindow){
		this.mainWindow = mainWindow;
	}
}
