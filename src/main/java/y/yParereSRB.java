package y;

import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.UIManager;

import y.ui.MainWindow;
import y.utils.Utils;

public class yParereSRB {
	public static final String ReleaseDate = "2016-02-08";
	public static final String VersionString = "0.01"; 
	public static final String VersionDateString = VersionString + " (" + ReleaseDate + ")"; 
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception exception) {
			Utils.MessageBox(null, "Invalid look and feel.", "ERROR");
		}
		
		Locale.setDefault(Locale.US);
		NumberFormat.getInstance(Locale.US).setGroupingUsed(false);
		
//		final Config config = Config.read();
		
		final MainWindow main = new MainWindow();
		main.setVisible(true);
	}
}
