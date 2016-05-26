package tennisserver;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class TennisServer extends JFrame {
	private JTextArea output;
	private Player players[];
	private Calculator calculator;
	private ServerSocket server;

	public TennisServer() {
		super( "tennis" );

		players = new Player[ 2 ];
		try {
			server = new ServerSocket( 5000, 0 );
		}
		catch( IOException e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
		calculator = new Calculator(this);
		output = new JTextArea();
		getContentPane().add( output, BorderLayout.CENTER );
		output.setText( "Server awaiting connections\n" );
		setSize( 300, 300 );
		show();
	}

	public void execute() {
		for ( int i = 0; i < players.length; i++ ) {
			try {
				players[ i ] = new Player( server.accept(), this, i, calculator );
				players[ i ].start();
			}
			catch( IOException e ) {
				e.printStackTrace();
				System.exit( 1 );
			}
		}
		synchronized ( players[ 0 ] ) {
			players[ 0 ].threadSuspended = false;
			players[ 0 ].notify();
		}
		synchronized ( players[1] ) {
			players[ 1 ].threadSuspended = false;
			players[ 1 ].notify();
		}
		calculator.start();
	}

	public void display( String s ) {
		output.append( s + "\n" );
	}

	public static void main( String args[] ) {
		TennisServer game = new TennisServer();
		game.addWindowListener( new WindowAdapter() { public void windowClosing( WindowEvent e ) { System.exit( 0 ); } });
		game.execute();
	}
}

class Calculator extends Thread {
	private int bx;
	private int by;
	private int p1y;
	private int p2y;
	private int height;
	private int width;
	private int val;
	private boolean pad1;
	private boolean pad2;
	private int score1;
	private int score2;
	private TennisServer control;
	private File progress;
	private PrintWriter out;
	
	public Calculator(TennisServer t) {
		p1y = 180;
		p2y = 180;
		score1 = 0;
		score2 = 0;
		bx = 10;
		by = 200;
		pad1 = false;
		pad2 = false;
		height = 474;
		width = 498;
		val = 1;
		control = t;
		progress = new File(System.getProperty("user.dir") + "/tennisserver/history/progress.txt");
 
		try {
			progress.createNewFile();
		 	out = new PrintWriter(progress.getAbsoluteFile());
			out.println("Start Game");
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		while ( !gameOver() ) {
			determineBallDirection();
			changeBallDirection();
			try {
				Thread.sleep(50);
			}
			catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
		control.display("Game Over");
		out.println("Game Over");
		out.close();
	}

	public void determineBallDirection() {

		if(pad1==true && by >= height-15) {
			val=3;	    
		}

		else if(pad2==true && by >= height-15) {
			val=2;  		
		}

		else if(pad1==true && by <= 0) {
			val=1;   	
		}

		else if(pad2==true && by <= 0) {
			val=4; 			
		}

		if(by > p2y+40 && by <= p2y+80 && bx>=(width-40)) {
			val=4;
			pad2=true;
			pad1=false;
		}

		else if(by >= p2y && by <= p2y+40 && bx>=(width-40)) {
			val=2;
			pad2=true;
			pad1=false;
		}

		else if(by >= p1y+40 && by <= p1y+80 && bx<=10) {
			val=1;
			pad1=true;
			pad2=false;
		}			

		else if(by >= p1y && by <= p1y+40 && bx<=10) {
			val=3;     
			pad1=true; 
			pad2=false;
		}

		if(bx >= width) {
			score1++;		
			val=7;
			out.println("Score1: " + score1 + " Score2: " + score2 + "");
		}

		else if(bx < 0) {
			score2++;
			val=8;
			out.println("Score1: " + score1 + " Score2: " + score2 + "");
		}
	}

	public void changeBallDirection( ) 
	{	
		switch(val) {
		case 1:
			bx += 5;
			by += 5;
			break;
		case 2:
			bx -= 5;
			by -= 5;
			break;
		case 3:
			bx += 5;
			by -= 5;
			break;
		case 4:
			bx -= 5;
			by += 5;
			break;
		case 7:
			bx  = 10;
			by  = 200;
			p2y = 180;
			p1y = 180;
			break;
		case 8:
			bx = width-40;
			by  = 200;
			p2y = 180;
			p1y = 180;
			break;	
		}
	}

	public boolean gameOver() {
		if (score1 == 5 || score2 == 5) {
			return true;
		}
		return false;
	}

	public void setPlayer1Y(int y) {
		p1y = y;
	}

	public void setPlayer2Y(int y) {
		p2y = y;
	}
	
	public int getPlayer1Y() {
		return p1y;
	}

	public int getPlayer2Y() {
		return p2y;
	}

	public int getBallX() {
		return bx;
	}

	public int getBallY() {
		return by;
	}

	public int getScore1() {
		return score1;
	}

	public int getScore2() {
		return score2;
	}
}

class Player extends Thread {
	private Socket connection;
	private DataInputStream input;
	private DataOutputStream output;
	private TennisServer control;
	private Calculator calculator;
	protected boolean threadSuspended = true;
	protected char side;
	
	public Player( Socket s, TennisServer t, int num, Calculator calc ) {
		side = ( num == 0 ? 'L' : 'R' );		
		connection = s;
		try {
			input = new DataInputStream(connection.getInputStream() );
			output = new DataOutputStream(connection.getOutputStream() );
		}
		catch( IOException e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
		control = t;
		calculator = calc;
	}

	public void run() {
		boolean done = false;
		control.display( "Player " + side + " connected" );
		try {
			output.writeChar( side );
		}
		catch( IOException e ) {
			e.printStackTrace();
			System.exit( 1 );
		}

		try {			
			try {
				synchronized( this ) {
					while ( threadSuspended )
						wait();
				}
			}
			catch ( InterruptedException e ) {
				e.printStackTrace();
			}
			while ( !done ) {
				
				if (side == 'L') {
					output.writeInt(calculator.getPlayer2Y());
					calculator.setPlayer1Y(input.readInt());
				}
				else {
					output.writeInt(calculator.getPlayer1Y());
					calculator.setPlayer2Y(input.readInt());
				}
				output.writeInt(calculator.getBallX());
				output.writeInt(calculator.getBallY());
				output.writeInt(calculator.getScore1());
				output.writeInt(calculator.getScore2());
				if ( calculator.gameOver() )
					done = true;
			}
			connection.close();
		}
		catch( IOException e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}
}
