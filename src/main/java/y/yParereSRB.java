package y;

import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.UIManager;

import y.ui.MainWindow;
import y.utils.GeneralProperties;
import y.utils.GeneralPropertiesExporter;
import y.utils.Utils;

public class yParereSRB {
	public static final String ReleaseDate = "2016-02-08";
	public static final String VersionString = "0.01"; 
	public static final String VersionDateString = VersionString + " (" + ReleaseDate + ")"; 
	
	public static final String CONFIG_FILENAME = "yParereSRB.xml";
	public static final String ADDRESSES_FILENAME = "addresses.xml";
	
	
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
		GeneralProperties<String> config = null;
		try {
			config = GeneralPropertiesExporter.read(CONFIG_FILENAME, p -> { return p; });
		} catch (Exception e) {
			Utils.MessageBox(CONFIG_FILENAME+" doesn't exist", "WARNING");
		}
		
		
		final MainWindow main = new MainWindow(config);
		main.setVisible(true);
	}
}
