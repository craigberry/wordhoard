import javax.swing.*;
import java.awt.event.*;

public class Test {

	private static class MainWindow extends JFrame {
		public MainWindow () {
			setTitle("Test");
			JMenuItem dlog1Cmd = new JMenuItem("Good Dialog");
			dlog1Cmd.addActionListener(
				new ActionListener () {
					public void actionPerformed (ActionEvent event) {
						handleDlog1Cmd();
					}
				}
			);
			JMenuItem dlog2Cmd = new JMenuItem("Bad Dialog");
			dlog2Cmd.addActionListener(
				new ActionListener () {
					public void actionPerformed (ActionEvent event) {
						handleDlog2Cmd();
					}
				}
			);
			JMenu dlogMenu = new JMenu("Dialogs");
			dlogMenu.add(dlog1Cmd);
			dlogMenu.add(dlog2Cmd);
			JMenuBar menuBar = new JMenuBar();
			menuBar.add(dlogMenu);
			setJMenuBar(menuBar);
			JLabel label = new JLabel("Hello world");
			label.setBorder(BorderFactory.createEmptyBorder(100,100,100,100));
			setContentPane(label);
			pack();
			setLocation(50, 50);
			setVisible(true);
		}
	}
	
	private static class MyDialog extends JDialog {
		public MyDialog (JFrame parentWindow) {
			super(parentWindow);
			setTitle(null);
			setModal(true);
			setResizable(false);
			JPanel panel = new JPanel();
			JButton okButton = new JButton("OK");
			okButton.addActionListener(
				new ActionListener () {
					public void actionPerformed (ActionEvent event) {
						dispose();
					}
				}
			);
			panel.add(okButton);
			getRootPane().setDefaultButton(okButton);
			setContentPane(panel);
			pack();
			setLocation(60, 60);
			setVisible(true);
		}
	}
	
	private static JFrame mainWindow;
	
	private static void handleDlog1Cmd () {
		new MyDialog(mainWindow);
	}
	
	private static void handleDlog2Cmd () {
		new MyDialog(null);

	}

	private static void doit () {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		mainWindow = new MainWindow();
	}

	public static void main (String[] args) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run () {
					try {
						doit();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		);
	}
	
}
