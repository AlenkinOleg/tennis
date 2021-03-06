package tennisclient.control;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import tennisclient.model.*;
import tennisclient.view.*;

public class TennisClient extends JApplet implements Runnable {
	private JTextField id;
	private JTextArea display;
	private JPanel boardPanel, panel2;
	private Client client;
	private Thread outputThread;
	private char mySide;
	private String address;

	JFrame myFrame;
    	MyPanel myPanel;
 
    	JLabel lb1, lb2;

	int score1;
	int score2;
     
	int val;

	boolean pad1=false, pad2=false;

	public TennisClient(String adr) {
		address = adr;
		System.out.println(adr);
	}
	 
	public void init()
	{
		myFrame = new JFrame();
	     	myPanel = new MyPanel();

		lb1 = new JLabel("Player1 = 0");
		lb2 = new JLabel("Player2 = 0");

		Container c = myFrame.getContentPane();
		c.setLayout(new BorderLayout());

		myPanel.add(lb1);
		myPanel.add(lb2);
		
		c.add(myPanel);
		
		myFrame.setSize(500,500);
		myFrame.setVisible(true);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		myFrame.addKeyListener(new KeyboradHandler());
	}
	 
	public void start() {
		client = new Client(address);
		client.start();
		outputThread = new Thread( this );
		outputThread.start();
	}

	public void run() {
		mySide = client.readChar();
		myPanel.setBias(mySide);

		while ( true ) {
			myPanel.setOpponentY(client.readInt());
			client.writeInt(myPanel.getMyY());
			myPanel.setBallX(client.readInt());
			myPanel.setBallY(client.readInt());
			score1 = client.readInt();
			score2 = client.readInt();
			myPanel.repaint();
			lb1.setText("Player 1 ="+ score1 +"");
			lb2.setText("Player 2 ="+ score2 +"");
			if (score1 == 5 || score2 == 5)
				break;
		}
		if (score1 > score2)
			lb1.setText("Player1 wins!!!");
		else 
			lb1.setText("Player2 wins!!!");
		myPanel.remove(lb2);
	}

	public class KeyboradHandler extends KeyAdapter {	
		public void keyPressed(KeyEvent ke) {		
			if(ke.getKeyCode()==ke.VK_DOWN) {
				if(myPanel.getMyY() != 365) {
					myPanel.addMyY(5);			
				}                 
			}
			
			if(ke.getKeyCode()==ke.VK_UP) {
				if(myPanel.getMyY() != 0) {
					myPanel.addMyY(-5);			
				}
			}
		}
	}

	public static void main(String[] args) {
		TennisClient cl = new TennisClient(args[0]);
		cl.init();
		cl.start();
	}
}
