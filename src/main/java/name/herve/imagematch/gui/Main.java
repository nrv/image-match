package name.herve.imagematch.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//JFrame.setDefaultLookAndFeelDecorated(true);
		
		JFrame frame = new JFrame();
		frame.setSize(1024, 1024);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container mainPanel = frame.getContentPane();
		mainPanel.setLayout(new BorderLayout());
		
		mainPanel.add(new FileChooserWithTextField("Go"), BorderLayout.PAGE_START);
		
		frame.setVisible(true);

	}

}
