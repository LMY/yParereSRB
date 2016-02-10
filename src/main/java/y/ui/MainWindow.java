package y.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import y.yParereSRB;
import y.em.Project;
import y.em.Site;
import y.exporters.ProjectExporterProvider;
import y.utils.AddressBook;
import y.utils.CustomXWPFDocument;
import y.utils.GeneralProperties;
import y.utils.LastUsedFolder;
import y.utils.Utils;
import y.utils.UtilsOffice;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	public final static double ASPECT_RATIO = 16.0/9.0;
	public final static int WINDOW_WIDTH = 1100;
	public final static int WINDOW_HEIGHT = (int) (WINDOW_WIDTH/ASPECT_RATIO);
	public final static Dimension PREFERRED_DIMENSION = new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);

	public final static String TEMPLATE_FILENAME = "template.docx";
	
	
	private JTextField templateFile;
	private JTextField yemFile;
	
	private JPanel phase0panel;
	
	private JHashTable substTable;
	private JButton go2Button;
	
	private Project current_project = null;
	private CustomXWPFDocument wpf_document = null;
	
	private GeneralProperties<String> config;
	private AddressBook book;
	
	public MainWindow(GeneralProperties<String> config, AddressBook book) {
		super("yPareriSRB - "+yParereSRB.VersionString);
		
		this.config = config;
		this.book = book;
		
		LastUsedFolder.init(".");

		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	onClose();
		    	System.exit(0);
		    }
		});
		
		setPreferredSize(PREFERRED_DIMENSION);
	
		
		setLayout(new BorderLayout());
		
		phase0panel = new JPanel();
		phase0panel.setLayout(new BorderLayout());
		
		final JPanel phase0panelC = new JPanel();
		phase0panelC.setLayout(new GridLayout(0,2));
		
		phase0panelC.add(new JLabel(" Template:"));
		templateFile = new JTextField(TEMPLATE_FILENAME);
		phase0panelC.add(Utils.createOpenFileTextField(this, templateFile, "template file", "docx"));
		
		phase0panelC.add(new JLabel(" yEM Filename:"));
		yemFile = new JTextField("D:\\ARPA\\160127 Nimis-ViaMerano-VF_UD5297\\00prjVFserteco.yem");
		phase0panelC.add(Utils.createOpenFileTextField(this, yemFile, "yEM file", "yem"));
		
		
		final JButton goButton = new JButton("Read yEm");
		goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				readTemplates();
				enablePanels();
			}
		});
		
		phase0panel.add(phase0panelC, BorderLayout.CENTER);
		phase0panel.add(goButton, BorderLayout.SOUTH);
		
		
		substTable = new JHashTable();
		
		go2Button = new JButton("Write");
		go2Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				writeResult();
				enablePanels();
			}
		});
		
		this.add(phase0panel, BorderLayout.NORTH);
		this.add(new JScrollPane(substTable), BorderLayout.CENTER);
		this.add(go2Button, BorderLayout.SOUTH);
		
		enablePanels();
		
		pack();
		Utils.centerWindow(this);
	}

	private void onClose() {
		
	}
	
	private void enablePanels() {
		Utils.enableComponents(phase0panel, current_project == null);
		
		substTable.setEnabled(current_project != null);
		if (current_project == null)
			substTable.clear();
		
		go2Button.setEnabled(current_project != null);
	}
	
	
	
	// phase 1
	
	private boolean readTemplates() {
		try {
			// read template
			wpf_document = new CustomXWPFDocument(new FileInputStream(templateFile.getText()));
			final Set<String> fields = getTemplateFields(wpf_document);
			
			// read project
			final String yemFilename = yemFile.getText();
			if (yemFilename == null || yemFilename.isEmpty())
				throw new Exception("yEM filename is empty");
			
			current_project = ProjectExporterProvider.importProject(yemFilename);
			if (current_project == null || current_project.getSites() == null || current_project.getSites().length == 0)
				throw new Exception("Empty project");
		
			substTable.update(fillInDefaults(fields));
			
			return true;
		}
		catch (Exception e) {
			current_project = null;
			Utils.MessageBox("Error reading project:\n"+e.toString(), "ERROR");
			return false;
		}
	}

	private Map<String, String> fillInDefaults(Set<String> fields) {
		final Map<String, String> map = new LinkedHashMap<String, String>();	// preserve az order of keys
		
		final String today = Utils.getTodayDate();
		final String this_year = Utils.getTodayYear();
		
		final String[] aFields =  fields.toArray(new String[fields.size()]);
		Arrays.sort(aFields);
		
		final Site site = current_project.getFirstSite();
		
		for (final String s : aFields)
			try {
				if (s.equals("$A.ANNO")) 
					map.put(s, this_year);
				else if (s.equals("$A.BREVEANNO")) 
					map.put(s, this_year.substring(2));
				else if (s.equals("$A.DATA") || s.equals("$TODAY") || s.equals("$DATE"))
					map.put(s, today);
				else if (s.equals("$COMUNE.NOME")) {
					final String mem_comune = site.getDbinfo().getComune();
					map.put(s, mem_comune);
					map.put("$COMUNE.EMAIL", book.getEmail(book.getCloserName(mem_comune)));
				}
				else if (s.equals("$GESTORE.NOME")) {
					final String mem_gestore = site.getDbinfo().getOperatore();
					map.put(s, mem_gestore);
					map.put("$GESTORE.EMAIL", book.getEmail(book.getCloserName(mem_gestore)));
				}
				else if (s.equals("$STUDIOTECNICO.NOME")) {
					final String mem_gestore = "";
					map.put(s, mem_gestore);
					map.put("$STUDIOTECNICO.EMAIL", book.getEmail(book.getCloserName(mem_gestore)));
				}				
				else if (s.equals("$INDIRIZZO.INDIRIZZO")) 
					map.put(s, Utils.capitalize(site.getDbinfo().getIndirizzo()));
				else if (s.equals("$PROTOCOLLO.NUMERO")) 
					map.put(s, site.getDbinfo().getProto_in().replaceAll("[Aa\\/]", ""));
				else if (s.equals("$PROTOCOLLO.DATA") || s.equals("$PROTOCOLLO.DATAIN")) 
					map.put(s, adjustDate(site.getDbinfo().getData_proto_in()));
				else if (s.equals("$PROTOCOLLO.DATAOUT")) 
					map.put(s, adjustDate(site.getDbinfo().getData_proto_out()));
				else if (s.equals("$SITO.ID")) 
					map.put(s, site.getID());
				else if (s.equals("$SITO.NOME") || s.equals("$SITO.NAME")) {
					if (site.getDbinfo().getCodiceSito() != null && !site.getDbinfo().getCodiceSito().isEmpty())
						map.put(s, site.getDbinfo().getCodiceSito());
					else {
						final String[] parts = site.getDbinfo().getNote().split(UtilsOffice.REGEXP_SPACES_AND_STUFF);
						map.put(s, parts.length > 0 ? parts[0] : "");
					}
				}
				else if (s.equals("$SITO.X")) 
					map.put(s, Utils.formatDouble(site.getPosition().getX(), 1));
				else if (s.equals("$SITO.Y")) 
					map.put(s, Utils.formatDouble(site.getPosition().getY(), 1));
				else if (s.equals("$SITO.Z")) 
					map.put(s, Utils.formatDouble(site.getPosition().getZ(), 2));
				else if (s.equals("$RICONFIGURA.ID")) {
					final String[] parts = site.getDbinfo().getNote().split(UtilsOffice.REGEXP_SPACES_AND_STUFF);
					
					boolean found = false;
					
					for (int i=0; i<parts.length-1; i++)
						if (parts[i].equalsIgnoreCase("riconfigura")) {
							found = true;
							map.put(s, i<parts.length-2 && parts[i+1].equalsIgnoreCase("id") ? parts[i+2] : parts[i+1]);
						}
					
					if (!found)
						map.put(s, "");
				}
				else if (s.startsWith("$PIC.")) {
					final String[] parts = s.split("\\.");
					map.put(s, getPic(parts[1]));
				}
				else if (s.startsWith("$TABELLA.")) {
					final String[] parts = s.split("\\.");
					map.put(s, getTable(parts[1]));
				}
			
				else {
					// get from conf, if present
					final String config_value = config.get(String.class, s);
					map.put(s, Utils.IsNullOrEmpty(config_value) ? "" : config_value);
				}
			}
			catch (Exception e) {
				map.put(s, "");
			}
		
		return map;
	}
	
	
	private static List<String> getFilesOfType(String dirname, String[] valid_exts) {
		final File dir = new File(dirname);
		final String[] names = dir.list();
		
		final List<String> ret = new ArrayList<String>();
		
		for (String s : names) {
			final String ext = Utils.getFileExtension(s);
			
			for (String e : valid_exts)
				if (ext.equalsIgnoreCase(e))
					ret.add(dirname + s);
		}
	
		return ret;
	}
	
	private static String[] EXTENSIONS_PIC = { "jpg", "jpeg", "png" };
	private static String[] EXTENSIONS_TABLE = { "xlsx", "xls" };
	
	private String getPic(String name) {
		final List<String> pics = getFilesOfType(Utils.getFolderOfFile(yemFile.getText()) + File.separator, EXTENSIONS_PIC);
		
		for (String s : pics) {
			final String lowS = Utils.getFilenameOfFile(s).toLowerCase();
			
			if (name.equals("VA")) {
				if (lowS.contains("va") || lowS.contains("volume"))
					return s;
			}
			else if (name.equals("MISURE")) {
				if (lowS.contains("mis"))
					return s;
			}
			else if (name.equals("CAMPO15")) {
				if (lowS.contains("campo1.5") || lowS.contains("campo15") || lowS.contains("c15") || lowS.contains("c1.5"))
					return s;
			}
			else if (name.equals("CAMPO6")) {
				if (lowS.contains("campo6") || lowS.contains("c6"))
					return s;
			}
		}
		
		return "";
	}
	
	private String getTable(String name) {
		final List<String> tables = getFilesOfType(Utils.getFolderOfFile(yemFile.getText()) + File.separator, EXTENSIONS_TABLE);
		
		for (String s : tables) {
			final String lowS = Utils.getFilenameOfFile(s).toLowerCase();
			
			if (name.equals("PREESISTENTI")) {
				if (lowS.contains("prees"))
					return s;
			}
			else if (name.equals("MISURE")) {
				if (lowS.contains("measure"))
					return s;
			}
			else if (name.equals("RADIOELETTRICA")) {
				if (lowS.contains("radioel"))
					return s;
			}
		}
		
		return "";
	}
	
	
	private static String adjustDate(String data_proto_out) {
		try {
			final String r = data_proto_out.replaceFirst("00:00:00.0", "").trim();
			return r.substring(8, 10) + "." + r.substring(5, 7) + "." + r.substring(0, 4);
		}
		catch (Exception e) {
			return data_proto_out;
		}
	}

	
	private static Set<String> getTemplateFields(XWPFDocument hdoc) {
		final Set<String> ret = new HashSet<String>();

		ret.addAll(scan(hdoc.getParagraphs()));

		for (XWPFTable tbl : hdoc.getTables())
			if (tbl != null)
				for (XWPFTableRow row : tbl.getRows())
					if (row != null)
						for (XWPFTableCell cell : row.getTableCells())
							if (cell != null)
								ret.addAll(scan(cell.getParagraphs()));
		
		for (XWPFFooter footer : hdoc.getFooterList())
			if (footer != null)
				ret.addAll(scan(footer.getParagraphs()));
		
		for (XWPFHeader header : hdoc.getHeaderList())
			if (header != null)
				ret.addAll(scan(header.getParagraphs()));
	
		return ret;
	}
	
	private static Set<String> scan(List<XWPFParagraph> paragraphs) {
		final Set<String> ret = new HashSet<String>();
		
		for (XWPFParagraph p : paragraphs) {
			if (p == null)
				continue;
			
		    final List<XWPFRun> runs = p.getRuns();
		    if (runs == null)
		    	continue;
		    
			for (XWPFRun r : runs) {
				if (r == null)
					continue;
				
				String text = r.getText(0);
				if (text == null || text.isEmpty())
					continue;
				
				final String[] parts = text.split(UtilsOffice.REGEXP_SPACES_AND_STUFF);
				for (String word : parts)
					if (word.startsWith("$"))
						ret.add(word);
			}
		}
		
		return ret;
	}
	
	
	// phase 2
	
	private boolean writeResult() {
		String outfilename = Utils.saveFileDialog("Select file", this, "MS-Office docx", "docx");
		
		if (outfilename != null && !outfilename.isEmpty()) {
			if (Utils.getFilenameOfFile(outfilename).equals(TEMPLATE_FILENAME)) {
				Utils.MessageBox(TEMPLATE_FILENAME+"\nIs an invalid filename.\nChoose any other one!", "ERROR");
				return false;
			}
			
			// check if file exists
			if (new File(outfilename).exists() && !Utils.MessageBoxYesNo(this, "File\n"+outfilename+"\nexists. Overwrite?", "Confirm overwrite"))
				return false;
			
			// append ".docx" if user does not specify extension
			if (!outfilename.toLowerCase().endsWith("doc") && !outfilename.toLowerCase().endsWith("docx"))
				outfilename += ".docx";
			
			if (doWriteResult(wpf_document, substTable.getMap(), outfilename)) {
				Utils.MessageBox("Done!", "OK");
				
				this.current_project = null;
				this.wpf_document = null;
				
				return true;
			}
		}

		return false;
	}
		
	private boolean doWriteResult(CustomXWPFDocument hdoc, Map<String, String> subst, String filename) {		
		replace(hdoc, hdoc.getParagraphs(), subst);

		for (XWPFTable tbl : hdoc.getTables()) {
			if (tbl != null)
				for (XWPFTableRow row : tbl.getRows())
					if (row != null)
						for (XWPFTableCell cell : row.getTableCells())
							if (cell != null)
								replace(hdoc, cell.getParagraphs(), subst);
		}
		
		for (XWPFFooter footer : hdoc.getFooterList())
			if (footer != null)
				replace(hdoc, footer.getParagraphs(), subst);
		
		for (XWPFHeader header : hdoc.getHeaderList())
			if (header != null)
				replace(hdoc, header.getParagraphs(), subst);
		
		// TABLE REPLACE
		{
			final List<XWPFTable> tables = UtilsOffice.getTable(hdoc, "$TABELLA.PREESISTENTI");
			if (tables.size() != 1)
				Utils.MessageBox("There are "+tables.size()+"\n$TABELLA.PREESISTENTI", "WARNING");
			
			for (XWPFTable table : tables) {
				final List<XWPFTableRow> rows = table.getRows(); 

				while (rows.size() > 1)
					table.removeRow(1);
				
				final Site[] preesistenti = current_project.getSites();
				for (int i=1; i<preesistenti.length; i++)
					UtilsOffice.putTextInRow(table.createRow(),
							config.getOrDefault(String.class, "Font.preesistenti.name", "Garamond"),
							config.getOrDefault(Integer.class, "Font.preesistenti.size", 12),
							new String[] { preesistenti[i].getID(), preesistenti[i].getDbinfo().getOperatore(), preesistenti[i].getDbinfo().getIndirizzo() },
							new ParagraphAlignment[] { ParagraphAlignment.CENTER, ParagraphAlignment.CENTER, ParagraphAlignment.LEFT });
			}
		}
		
		{
			final List<XWPFTable> tables = UtilsOffice.getTable(hdoc, "$TABELLA.MISURE");
			if (tables.size() != 1)
				Utils.MessageBox("There are "+tables.size()+"\n$TABELLA.MISURE", "WARNING");
			
			final String measure_filename = subst.get("$TABELLA.MISURE");
			List<String[]> measures = null;
			
			try {
				measures = UtilsOffice.getMeasureValue(measure_filename);
			}
			catch (Exception e) {
				Utils.MessageBox("Cannot read measure filename:\n"+measure_filename, "ERROR");
			}
			
			if (measures != null)
				for (XWPFTable table : tables) {
					final List<XWPFTableRow> rows = table.getRows(); 
	
					while (rows.size() > 2)	
						table.removeRow(2);
					
					for (int i=1; i<measures.size(); i++)
						UtilsOffice.putTextInRow(table.createRow(),
								config.getOrDefault(String.class, "Font.misure.name", "Garamond"),
								config.getOrDefault(Integer.class, "Font.misure.size", 10),
								measures.get(i),
								new ParagraphAlignment[] { ParagraphAlignment.CENTER, ParagraphAlignment.LEFT, ParagraphAlignment.CENTER, 
															ParagraphAlignment.CENTER, ParagraphAlignment.CENTER, ParagraphAlignment.CENTER
															, ParagraphAlignment.CENTER, ParagraphAlignment.CENTER });
				}
		}

		{
			final List<XWPFTable> tables = UtilsOffice.getTable(hdoc, "$TABELLA.RADIOELETTRICA");
			if (tables.size() == 0)
				Utils.MessageBox("There are "+tables.size()+"\n$TABELLA.RADIOELETTRICA", "WARNING");
			
			final String radioelettrica_filename = subst.get("$TABELLA.RADIOELETTRICA");
			List<String[]> radioelt = null;
			
			try {
				radioelt = UtilsOffice.getRadioelettrica(radioelettrica_filename);
			}
			catch (Exception e) {
				Utils.MessageBox("Cannot read measure filename:\n"+radioelettrica_filename, "ERROR");
			}
			
			if (radioelt != null) {
				int k=5;	// moving in radioelt
				
				for (int i=0, imax=tables.size(); i<imax; i++) {
					final XWPFTable table = tables.get(i);
					final List<XWPFTableRow> rows = table.getRows(); 
	
					while (rows.size() > 1)	
						table.removeRow(1);
					
					++k; // skip first table line
					
					if (!((k < radioelt.size() && radioelt.get(k).length > 0))) { // no such line
						int position = hdoc.getPosOfTable(table);
						hdoc.removeBodyElement(position);
						hdoc.removeBodyElement(position); // table caption
						// remove table
					}
					else {
						// put data in table
						int lineoftable=0;
						
						while (k < radioelt.size() && radioelt.get(k).length > 0) {
							UtilsOffice.putTextInRowIntest(table.createRow(),
									config.getOrDefault(String.class, "Font.radioelettriche.name", "Garamond"),
									config.getOrDefault(Integer.class, "Font.radioelettriche.size", 10),
									UtilsOffice.formatIntestRow(radioelt.get(k), lineoftable++), ParagraphAlignment.CENTER);
									
							++k;
						}
					}
					
					++k; // skip bottom empty line
				}
			}
		}
		
		FileOutputStream fo = null;
		
		try {
			fo = new FileOutputStream(filename);
			hdoc.write(fo);
			fo.flush();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if (fo != null)
				try { fo.close(); }
				catch (Exception e2) {}
		}
	}
	
	private void replace(CustomXWPFDocument hdoc, List<XWPFParagraph> paragraphs, Map<String, String> subst) {
		for (XWPFParagraph p : paragraphs) {
			if (p == null)
				continue;
			
		    final List<XWPFRun> runs = p.getRuns();
		    if (runs == null)
		    	continue;
		    
		    // NORMAL TEXT REPLACE
			for (XWPFRun r : runs) {
				if (r == null)
					continue;
				
				String text = r.getText(0);
				if (text == null || text.isEmpty())
					continue;
				
				for (String key : subst.keySet())
					if (!key.startsWith("$PIC") && !key.startsWith("$TABELLA") && text.contains(key)) {
						final String value = subst.get(key);
						text = text.replace(key, value != null ? value : "");
					}
				
				r.setText(text, 0);
			}
			
			// PIC REPLACE
			for (XWPFRun r : runs) {
				if (r == null)
					continue;
				
				String text = r.getText(0);
				if (text == null || text.isEmpty())
					continue;
				
				for (String key : subst.keySet())
					if (key.startsWith("$PIC") && text.contains(key)) {
						r.setText("", 0);
						
						FileInputStream is = null;
						final String picFilename = subst.get(key);
						try {
							is = new FileInputStream(picFilename);
							
							// http://stackoverflow.com/questions/17745466/insert-picture-in-word-document
							final int format = UtilsOffice.getPictureFormat(picFilename);
							final String id = hdoc.addPictureData(is, format);
							hdoc.createPicture(r, id, hdoc.getNextPicNameNumber(format),
									config.get(Integer.class, "Image.width"), config.get(Integer.class, "Image.height"));
							
//							r.addPicture(is, format, picFilename,
//									Units.toEMU((double) config.get(Integer.class, "Image.width")),
//									Units.toEMU((double) config.get(Integer.class, "Image.height")));
						}
						catch (Exception e) {
							Utils.MessageBox("Couldn't add pic:\n"+picFilename, "ERROR");
						}
						finally {
							if (is != null)
								try { is.close(); }
								catch (Exception e2) {}
						}
						
						break;
					}
			}
		}
	}
}
