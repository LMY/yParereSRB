package y.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import org.apache.poi.openxml4j.opc.OPCPackage;
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
import y.utils.LastUsedFolder;
import y.utils.Utils;

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
	private XWPFDocument hdoc = null;
	
	public MainWindow() {
		super("yPareriSRB - "+yParereSRB.VersionString);
		
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
		yemFile = new JTextField("");
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
		go2Button.setEnabled(current_project != null);
	}
	
	
	
	// phase 1
	
	private boolean readTemplates() {
		try {
			// read template
			hdoc = new XWPFDocument(OPCPackage.open(new FileInputStream(templateFile.getText())));
			final Set<String> fields = getTemplateFields(hdoc);
			
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
				if (s.equals("$ANNO")) 
					map.put(s, this_year);
				else if (s.equals("$TODAY") || s.equals("$DATARELAZIONE") || s.equals("$DATE"))
					map.put(s, today);
				else if (s.equals("$COMUNE.NOME")) 
					map.put(s, site.getDbinfo().getComune());
				else if (s.equals("$GESTORE.NOME")) 
					map.put(s, site.getDbinfo().getOperatore());
				else if (s.equals("$INDIRIZZO.INDIRIZZO")) 
					map.put(s, Utils.capitalize(site.getDbinfo().getIndirizzo()));
				else if (s.equals("$PROTOCOLLO.NUMERO")) 
					map.put(s, site.getDbinfo().getProto_in().replace("[Aa\\/]", ""));
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
						final String[] parts = site.getDbinfo().getNote().split(REGEXP_SPACES_AND_STUFF);
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
					final String[] parts = site.getDbinfo().getNote().split(REGEXP_SPACES_AND_STUFF);
					
					boolean found = false;
					
					for (int i=0; i<parts.length-1; i++)
						if (parts[i].equalsIgnoreCase("riconfigura")) {
							found = true;
							map.put(s, i<parts.length-2 && parts[i+1].equalsIgnoreCase("id") ? parts[i+2] : parts[i+1]);
						}
					
					if (!found)
						map.put(s, "");
				}
			
				else
					map.put(s, "");
			}
			catch (Exception e) {
				map.put(s, "");
			}
		
		return map;
	}

	private String adjustDate(String data_proto_out) {
		try {
			final String r = data_proto_out.replaceFirst("00:00:00.0", "").trim();
			return r.substring(8, 10) + "." + r.substring(5, 7) + "." + r.substring(0, 4);
		}
		catch (Exception e) {
			return data_proto_out;
		}
	}

	public final static String REGEXP_SPACES_AND_STUFF = "[\\s)(\\,]+";
	
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
				
				final String[] parts = text.split(REGEXP_SPACES_AND_STUFF);
				for (String word : parts)
					if (word.startsWith("$"))
						ret.add(word);
			}
		}
		
		return ret;
	}
	
	
	// phase 2
	
	private boolean writeResult() {
		final String outfilename = Utils.saveFileDialog("Select file", this, "MS-Office docx", "docx");
		if (outfilename != null && !outfilename.isEmpty() && doWriteResult(hdoc, substTable.getMap(), outfilename)) {
			
			Utils.MessageBox("Done!", "OK");
			
			this.current_project = null;
			this.hdoc = null;
			
			return true;
		}
		else
			return false;
	}
		
	private static boolean doWriteResult(XWPFDocument hdoc, Map<String, String> subst, String filename) {		
		replace(hdoc.getParagraphs(), subst);

		for (XWPFTable tbl : hdoc.getTables())
			if (tbl != null)
				for (XWPFTableRow row : tbl.getRows())
					if (row != null)
						for (XWPFTableCell cell : row.getTableCells())
							if (cell != null)
								replace(cell.getParagraphs(), subst);
		
		for (XWPFFooter footer : hdoc.getFooterList())
			if (footer != null)
				replace(footer.getParagraphs(), subst);
		
		for (XWPFHeader header : hdoc.getHeaderList())
			if (header != null)
				replace(header.getParagraphs(), subst);
		
		try {
			hdoc.write(new FileOutputStream(filename));
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static void replace(List<XWPFParagraph> paragraphs, Map<String, String> subst) {
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
				
				for (String key : subst.keySet())
					if (text.contains(key))
						text = text.replace(key, subst.get(key));
				
				r.setText(text, 0);
			}
		}
	}
}
