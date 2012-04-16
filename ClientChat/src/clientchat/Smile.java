package clientchat;

import javax.swing.ImageIcon;

/**
 * Klasa reprezentująca emoticonki
 */
public class Smile {
	private String sign;			//znak w tekscie
	private String description;		//opis
	private ImageIcon icon;			//ikonka
	
	public Smile(String sign, String description, ImageIcon icon){
		this.sign = sign;
		this.description = description;
		this.icon = icon;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
}
