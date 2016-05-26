package tennisclient.model;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class Client {
	private Socket connection;
	private DataInputStream input;
	private DataOutputStream output;
	private Thread outputThread;

	public void start() {
		try {
			connection = new Socket(InetAddress.getByName( "127.0.0.1" ), 5000 );
			input = new DataInputStream(connection.getInputStream() );
			output = new DataOutputStream(connection.getOutputStream() );
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	public int readInt() {
		try {
			return input.readInt();
		}
		catch ( IOException e ) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public char readChar() {
		try {
			return input.readChar();
		}
		catch ( IOException e ) {
			e.printStackTrace();
			return '.';
		}
	}
	
	public void writeInt(int a) {
		try {
			output.writeInt(a);
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	public void writeChar(char a) {
		try {
			output.writeChar(a);
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
	}
}
