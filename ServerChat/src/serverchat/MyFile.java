package serverchat;

import java.io.Serializable;

/**
 * Klasa która niejako obudowuje klasę File po to aby przesłać plik poprzez ObjectOutputStream
 */
public class MyFile implements Serializable{

	private static final long serialVersionUID = 1019722937492174843L;
	private String fileName;		//nazwa pliku
	private byte[] fileByteArray;	//plik w postaci tablicy bajtów
	
	public MyFile() {};
	public MyFile(String fileName, byte[] fileByteArray){
		this.fileByteArray=fileByteArray;
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFileByteArray() {
		return fileByteArray;
	}

	public void setFileByteArray(byte[] fileByteArray) {
		this.fileByteArray = fileByteArray;
	}
}
