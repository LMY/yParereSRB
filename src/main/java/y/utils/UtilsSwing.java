package y.utils;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class UtilsSwing {
	
	public static JButton createButton(String name, ActionListener listener) {
		final JButton button = new JButton(name);
		button.addActionListener(listener);
		return button;
	}
	
	public static JMenuItem createMenuitem(String name, ActionListener listener) {
		final JMenuItem button = new JMenuItem(name);
		button.addActionListener(listener);
		return button;
	}

	public static JMenuItem createMenuitem(String name, ActionListener listener, String keyBind) {
		final JMenuItem button = new JMenuItem(name);
		button.addActionListener(listener);
		button.setAccelerator(KeyStroke.getKeyStroke(keyBind));
		return button;
	}
}
