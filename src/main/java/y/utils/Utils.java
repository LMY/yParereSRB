package y.utils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


public class Utils
{
	/**
	 * Salva un file di testo
	 * @param filename nome del file da salvare (con percorso assoluto)
	 * @param content contenuto del file di testo
	 * @throws FileNotFoundException se è stato impossibile salvare il file
	 */
	public static void saveText(String filename, String content) throws IOException
	{
		final PrintWriter out = new PrintWriter(filename);
		out.write(content);
		out.close();
	}
	
	/**
	 * Leggi l'intero contenuto di un file di testo
	 * @param filename nome del file da leggere
	 * @return la stringa contentente il testo del file
	 * @throws IOException
	 */
	public static String ReadWholeFile(String filename) throws IOException
	{
		final File file = new File(filename);
		final FileInputStream fis = new FileInputStream(file);
		final byte[] data = new byte[(int)file.length()];
		fis.read(data);
		fis.close();
		return new String(data, "UTF-8");
	}
	
	/**
	 * Inserisce una riga all'inizio di un file di testo
	 * @param filename nome del file
	 * @param text testo da inserire
	 * @param invalid_chars caratteri da rimuovere (dal file di ingresso)
	 * @return true se l'operazione è avvenuta con successo
	 */
	public static boolean WriteToBeginningOfFileAndClean(String filename, String text, char[] invalid_chars)
	{
		try {
			String content = ReadWholeFile(filename);
			new File(filename).delete();
			
			if (invalid_chars != null)
				for (char c : invalid_chars)
					content = content.replace(""+c, "");
			
			if (!text.endsWith("\n"))
				text += "\n";
		
			saveText(filename, text + content);
			return true;
		}
		catch (Exception e) { return false; }
	}
	
	public static boolean FileClean(String filename, char[] invalid_chars)
	{
		try {
			String content = ReadWholeFile(filename);
			new File(filename).delete();
			
			if (invalid_chars != null)
				for (char c : invalid_chars)
					content = content.replace(""+c, "");
			
			saveText(filename, content);
			return true;
		}
		catch (Exception e) { return false; }
	}
	
	/**
	 * Visualizza una finestra di messaggio
	 * @param text testo da visualizzare 
	 * @param title titolo della finestra
	 */
	public static void MessageBox(String text, String title)
	{
		MessageBox(null, text, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Visualizza una finestra di messaggio
	 * @param text testo da visualizzare 
	 * @param title titolo della finestra
	 */
	public static void MessageBox(Component window, String text, String title)
	{
		MessageBox(window, text, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Visualizza una finestra di messaggio
	 * @param text testo da visualizzare 
	 * @param title titolo della finestra
	 * @param type tipo della finestra di dialogo, si trovano in JOptionPane, es: JOptionPane.WARNING_MESSAGE
	 */
	public static void MessageBox(Component window, String text, String title, int type)
	{
		JOptionPane.showMessageDialog(window, text, title, type);
	}
	
	public static boolean MessageBoxShowAgain(String message, String title)
	{
		final JCheckBox checkbox = new JCheckBox("Non visualizzare più questo messaggio");
		final Object[] params = { message, checkbox };
		JOptionPane.showMessageDialog(null, params, title, JOptionPane.INFORMATION_MESSAGE);
		return !checkbox.isSelected();
	}
	
	/**
	 * Visualizza una finestra di messaggio per chiedere all'utente { Si, No }
	 * @param window la finestra da cui si visualizza
	 * @param text testo da visualizzare 
	 * @param title titolo della finestra
	 * @return true se l'utente ha selezionato si
	 */
	public static boolean MessageBoxYesNo(Component window, String text, String title)
	{
		return JOptionPane.showConfirmDialog(window, text, title, JOptionPane.YES_NO_OPTION) == 0;
	}
	
	/**
	 * Visualizza una finestra di messaggio per chiedere all'utente { Si ,No, Annulla }
	 * @param window la finestra da cui si visualizza
	 * @param text testo da visualizzare 
	 * @param title titolo della finestra
	 * @return USER_YES, USER_NO, USER_CANCEL
	 */
	public static int MessageBoxYesNoCancel(Component window, String text, String title)
	{
		return JOptionPane.showConfirmDialog(window, text, title, JOptionPane.YES_NO_CANCEL_OPTION);
	}
	
	public static int USER_YES = 0;
	public static int USER_NO = 1;
	public static int USER_CANCEL = 2;
	
	/**
	 * Chiede all'utente una string
	 * @param title titolo della finestra
	 * @return la stringa immessa, o null se l'utente ha cliccato Cancel
	 */
	public static String MessageBoxString(String title)
	{
		return JOptionPane.showInputDialog(title);
	}
	
	
	public static String[] MessageBoxStrings(String title)
	{
		return MessageBoxStrings(title, ",");
	}
	
	public static String[] MessageBoxStrings(String title, String separator)
	{
		final String input = Utils.MessageBoxString(title);
		if (input == null)
			return null;
		
		final String[] fields = input.split(separator);
		for (int i=0; i<fields.length; i++)
			fields[i] = fields[i].trim();
		
		return fields;
	}
	
	
	/**
	 * Centra la finestra nello schermo
	 * @param form
	 */
	public static void centerWindow(JFrame form)
	{
		final Dimension screen_dim = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension window_dim = form.getSize();
		
		final int posX = (screen_dim.width - window_dim.width) / 2;
		final int posY = (screen_dim.height- window_dim.height) / 2;
		
		form.setLocation(posX, posY);
	}
	
	/**
	 * Leggi le prime nlines da un file di testo e tornale
	 * @param filename nome del file da cui leggere
	 * @param nlines numero di righe da leggere
	 * @return il testo risultante, "" se il file è vuoto o non esistente. Se si verifica un errore viene tornato tutto il testo che è stato possibile leggere
	 */
	public static String peekFile(String filename, int nlines)
	{
		String res = "";
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(filename));
			
			for (int l=0; l<nlines; l++) {
				final String tl = br.readLine();
				if (tl == null)
					break;
				if (!res.isEmpty())
					res += "\n";
				res += tl;
			}
		}
		catch (Exception e) {}
		finally {
			try { br.close(); }
			catch (Exception e) {}
		}

		return res;
	}
	
	public static void jtable_adjustRowSizes(JTable jTable) {
        for (int row = 0; row < jTable.getRowCount(); row++) {
            int maxHeight = 0;
            for (int column = 0; column < jTable.getColumnCount(); column++) {
            	final TableCellRenderer cellRenderer = jTable.getCellRenderer(row, column);
            	final Object valueAt = jTable.getValueAt(row, column);
            	final Component tableCellRendererComponent = cellRenderer.getTableCellRendererComponent(jTable, valueAt, false, false, row, column);
            	final int heightPreferable = tableCellRendererComponent.getPreferredSize().height;
                maxHeight = Math.max(heightPreferable, maxHeight);
            }
            jTable.setRowHeight(row, maxHeight);
        }
    }

    public static void jtable_adjustColumnSizes(JTable table, int column, int margin) {
    	final DefaultTableColumnModel colModel = (DefaultTableColumnModel) table.getColumnModel();
    	final TableColumn col = colModel.getColumn(column);

        TableCellRenderer renderer = col.getHeaderRenderer();
        if (renderer == null)
            renderer = table.getTableHeader().getDefaultRenderer();

        Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
        int width = comp.getPreferredSize().width;

        for (int r = 0; r < table.getRowCount(); r++) {
            renderer = table.getCellRenderer(r, column);
            comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, column), false, false, r, column);
            final int currentWidth = comp.getPreferredSize().width;
            width = Math.max(width, currentWidth);
        }

        width += 2 * margin;

        col.setPreferredWidth(width);
        col.setWidth(width);
    }
		
	public static String getFolderOfFile(String filename)
	{
		try { return new File(filename).getParent(); }
		catch (Exception e) { return ""; }
	}
	
	public static String formatDouble(double value, int numdec)
	{
		if (value < 0) return "-" + formatDouble(-value, numdec);
		
		if (numdec >= 0)
			return String.format("%1$."+numdec+"f", value).replace(',', '.');
		else
			return ""+value;
	}

	public static String formatDouble(double value, int intcip, int numdec)
	{
		if (value < 0) return "-" + formatDouble(-value, intcip, numdec);
		
		if (numdec > 0)
			return String.format("%0"+(intcip+numdec+1)+"."+numdec+"f", value).replace(',', '.');
		else if (numdec == 0)
			return String.format("%0"+intcip+".0f", value).replace(',', '.');
		else
			return ""+value;
	}

	public static String formatDoubleAsNeeded(double value)
	{
		if (value < 0) return "-" + formatDoubleAsNeeded(-value);
		
		return new DecimalFormat("#.##").format(value).replace(',', '.');
	}
	
	public static String formatDoubleAsNeeded(double value, int maxdec)
	{
		if (value < 0) return "-" + formatDoubleAsNeeded(-value, maxdec);
		
		return new DecimalFormat(maxdec == 0 ? "#" : "#." +stringFromRepChar('#', maxdec) ).format(value).replace(',', '.');
	}
	
	public static String stringFromRepChar(char c, int times)
	{
		 final char[] chars = new char[times];
		 Arrays.fill(chars, c);
		 return new String(chars);
	}
	
	public static int roundAngle(double a)
	{
		final int ia = (int)Math.round(a);
		return ia < 360 ? ia : 0;
	}
	
	public static double normalizeAngle(double a)
	{
		while (a < 0)
			a += 360;
		while (a >= 360)
			a -= 360;
		return a;
	}
	
	public static String ConnectionString(String host, String dbname, String username, String password)
	{
		return "\"host="+host+" user="+username+" dbname="+dbname+" password="+password+"\"";
	}
	
	public static String formatTwoDigitInt(int x)
	{
		return (x<10 ? "0" : "") + x;
	}
	
	
	public static int numberOfDecimals(double d)
	{
		if ((d == Math.floor(d)) && !Double.isInfinite(d)) 	// if it is an integer
			return 0;
		else {
			final String text = Double.toString(Math.abs(d));
			final int integerPlaces = text.indexOf('.');
			return integerPlaces < 0 ? 0 : text.length() - integerPlaces - 1;
		}
	}
	
	public static void clipboardCopy(String data)
	{
		final StringSelection selection = new StringSelection(data);
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}
	
	public static String clipboardPaste()
	{
		String result = "";
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final Transferable contents = clipboard.getContents(null);
		final boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);

		if (hasTransferableText) {
			try {
				result = (String)contents.getTransferData(DataFlavor.stringFlavor);
			}
			catch (Exception e){
//				System.out.println(ex);
//				ex.printStackTrace();
			}
		}
		return result;
	}
	
	public static int[] getAllIntsInString(String s)
	{
		final Pattern pattern = Pattern.compile("[0-9]+"); 
		final Matcher matcher = pattern.matcher(s);
		
		final ArrayList<Integer> list = new ArrayList<Integer>();
		
		while (matcher.find())
			try {
				list.add(Integer.parseInt(matcher.group()));
			}
			catch (Exception e) {}
		
		final int[] ret = new int[list.size()];
		for (int i=0; i<ret.length; i++)
			ret[i] = list.get(i);
		return ret;
	}
	public static boolean hasText(JTextField component)
	{
		return component.getText() != null && !component.getText().isEmpty();
	}
	
	/**
	 * Calcola la permutazione che ordina i dati di ingresso
	 * @param input interi da ordinare
	 * @return array : output[i] è l'indice della posizione dove dovrebbe essere inserito input[i] per ordinare l'array
	 */
	public static <E> int[] ArrayGetSortPermutation(final Collection<E> input, final Comparator<Integer> comparator)
	{
		final int len = input.size();
		final List<Integer> indices = new ArrayList<Integer>(len);
		for (int i=0; i<len; i++)
			indices.add(i);

		Collections.sort(indices, comparator);

		return ArrayIntegerToInt(indices);
	}
	
	public static int[] ArrayIntegerToInt(List<Integer> array)
	{
		final int[] ret = new int[array.size()];

		int i=0;
		for (Iterator<Integer> iter=array.iterator(); iter.hasNext();)
			ret[i++] = iter.next();

		return ret;		
	}
	
	public static void touch(String filename) {
		final File file = new File(filename);
		if (file.exists()) {
			final long now = System.currentTimeMillis();
			final long then = file.lastModified();
			
			if (now > then)
				file.setLastModified(now);
		}
	}

	public static boolean abortOnExistingAndDontOverwrite(String filename)
	{
		return (new File(filename).exists() && Utils.MessageBoxYesNo(null, filename+" esiste già. Sovrascrivere?", "Conferma sovrascrittura") == false);
	}
	
	
	public static <T> int[] permutationSort(final List<T> list, final Comparator<T> comparator)
	{
		final int len = list.size();
		final Integer[] indices = new Integer[len];
		for (int i = 0; i < len; i++)
			indices[i] = i;
		
		Arrays.sort(indices, new Comparator<Integer>() {
			public int compare(Integer i, Integer j) {
				return comparator.compare(list.get(i), list.get(j));
			}
		});
		
		final int[] ret = new int[indices.length];
		for (int i=0; i<ret.length; i++)
			ret[i] = indices[i];
		
		return ret;
	}
	
	public static <T> int[] permutationSort(final T[] array, final Comparator<T> comparator)
	{
		final Integer[] indices = new Integer[array.length];
		for (int i = 0; i < array.length; i++)
			indices[i] = i;
		
		Arrays.sort(indices, new Comparator<Integer>() {
			public int compare(Integer i, Integer j) {
				return comparator.compare(array[i], array[j]);
			}
		});
		
		final int[] ret = new int[indices.length];
		for (int i=0; i<ret.length; i++)
			ret[i] = indices[i];
		
		return ret;
	}
	
	public static <T> void applyPermutation(final T[] input, final T[] output, int[] permutation)
	{
		for (int i=0; i<permutation.length; i++)
			output[i] = input[permutation[i]];
	}
	
	public static <T> void applyPermutation(final List<T> input, final List<T> output, int[] permutation)
	{
		for (int i=0; i<permutation.length; i++)
			output.set(i, input.get(permutation[i]));
	}
	
	/**
	 * Capture console output of a process
	 * @param p Process to capture
	 * @return captured lines
	 */
	public static String captureOutputOfProcess(Process p)
	{
		final BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		final StringBuilder text = new StringBuilder();
		
		String line;
		try {
			while ((line = input.readLine()) != null)
				text.append(line + "\n");
		}
		catch (IOException e) {
			text.append("** EXCEPTION ("+e.getMessage()+") **");
		}
		
		try { p.waitFor(); }
		catch (InterruptedException e) {}
		
		return text.toString();
	}
	
	public final static String PathSeparator = File.separator;	// for those times when you already depend upon Utils, but not File
	
	public static boolean mkdirs(String filename) {
		try {
			new File(new File(filename).getParent()).mkdirs();
			return true;
		}
		catch (Exception e) { return false; }
	}
	
	/**
	 * Using reflections, associate keystroke to a method of target
	 * @param target object on which the method will be invoked
	 * @param component component where action will be associated
	 * @param key keystroke to bind
	 * @param function function (of target) to be called upon keystroke
	 */
	@SuppressWarnings("serial")
	public static final void associateKeyBind(final Object target, JComponent component, KeyStroke key, final String function) {
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, function);
		component.getActionMap().put(function, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					target.getClass().getDeclaredMethod(function, new Class[]{}).invoke(target, new Object[]{});
				}
				catch (Exception ex) {}
			} });
	}
	
	
	public static String getFileExtension(String filename) {
		final int i = filename.lastIndexOf('.');
	    return i > 0 ? filename.substring(i+1) : "";
	}
	
	public static final Color TableForegroundNormal = UIManager.getColor("Table.foreground");
	
	
	public static String firstNotEmpty(String... strings) {
		for (final String s : strings)
			if (s != null && !s.isEmpty()) // if (!String.isNullOrEmpty(s))
				return s;
		
		return "";
	}
}
