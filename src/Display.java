
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import java.awt.Canvas;

public class Display extends JFrame {
	static int count = 0;
	public static int xVal;
	public static int numLevels = 10;
	public static int yVal;
	public static int xVal2;
	public static int yVal2;
	public static int tempXD, tempYD, tempXE, tempYE;
	public static int xA, xB, xC, xD;
	public static int yA, yB, yC, yD;
	public static int xAB, yAB;
	public static int yE, xE;

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {

		// creates and shows the gui
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();

			}
		});

	}

	private static void createAndShowGUI() {

		// The inner workings of creating the specific window of the GUI

		System.out.println("Created GUI on EDT? "
				+ SwingUtilities.isEventDispatchThread());

		// create a JFrame object, the window will be called square example.
		// tutorial for making a menu via oracle's website:
		// https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("file");
		menuBar.add(menu);
		JMenuItem menuItem2 = new JMenuItem("Quit");
		menu.add(menuItem2);

		// https://stackoverflow.com/questions/9778621/how-to-make-a-jmenu-item-do-something-when-its-clicked
		// for making a menu do something when clicked

		menuItem2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);

			}

		});

		JFrame frame = new JFrame("Sandwich");
		frame.setJMenuBar(menuBar);

		// When we close out of the application, we will exit it using this
		// line.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// display the window

		final sqFrame sm = new sqFrame();

	
							
						
		sm.repaint();

					
		frame.add(sm);

		// use pack to size the window appropriately (or can be manually set).
		frame.pack();

		// display the window
		frame.setVisible(true);
	}
}

class sqFrame extends Canvas {
	private static final long serialVersionUID = 1L;
	int xWindow = 1270;
	int yWindow = 720;
	int[] xSPoints = new int[4];
	int[] ySPoints = new int[4];
	int[] tXPoints = new int[3];
	int[] tYPoints = new int[3];

	// generate the size of the window
	public Dimension getPreferredSize() {
		return new Dimension(xWindow, yWindow);
	}

	public void paint(Graphics gfx) 
	{
	
	}
}
