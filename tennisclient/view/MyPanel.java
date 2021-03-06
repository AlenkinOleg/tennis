package tennisclient.view;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MyPanel extends JPanel {

	int bx,by;
	int myX,myY;
	int myBias;
	int opponentBias;
	int opponentX,opponentY;

	public MyPanel() {

		bx=10;
		by=200;
		myX = 0;
		myY = 180; 
		opponentX = 0;
		opponentY = 180;
	}

	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		g.setColor(Color.GREEN);
		g.fillOval(bx, by, 15, 15);

		g.setColor(Color.BLACK);
		g.fillRoundRect(myX + myBias, myY, 10, 100, 10, 10);
		g.fillRoundRect(opponentX + opponentBias, opponentY, 10, 100, 10, 10);
	}

	public void setOpponentY(int y) {
		opponentY = y;
	}
	
	public void setBias(char indicator) {
		if (indicator == 'L') {
			myBias = 0;
			opponentBias = getWidth()-20;
			System.out.println("L\n");
			System.out.println(getHeight());
		}
		else {
			opponentBias = 0;
			myBias = getWidth()-20;
			System.out.println("R\n");
		}
	}

	public void setBallX(int x) {
		bx = x;
	}
	
	public void setBallY(int y) {
		by = y;
	}

	public int getMyY() {
		return myY;
	}

	public int getMyX() {
		return myX;
	}

	public void addMyY(int delta) {
		myY += delta;
	}
}
