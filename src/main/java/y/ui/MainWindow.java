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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
		
		
		this.add(phase0panel, BorderLayout.NORTH);
		
		enablePanels();
		
		pack();
		Utils.centerWindow(this);
	}

	private void onClose() {
		
	}
	
	private void enablePanels() {
		Utils.enableComponents(phase0panel, current_project == null);
	}
	
	
	
	// phase 1
	
	private boolean readTemplates() {
		try {
			// read template
			hdoc = new XWPFDocument(OPCPackage.open(new FileInputStream(templateFile.getText())));
			final Set<String> fields = getTemplateFields(hdoc);
			
			// read project
			current_project = ProjectExporterProvider.importProject(yemFile.getText());
			if (current_project == null || current_project.getSites() == null || current_project.getSites().length == 0)
				throw new Exception("Empty project");
		
			return true;
		}
		catch (Exception e) {
			current_project = null;
			Utils.MessageBox("Error reading project:\n"+e.toString(), "ERROR");
			return false;
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
				
				final String[] parts = text.split("\\s");
				for (String word : parts)
					if (word.startsWith("$"))
						ret.add(word);
			}
		}
		
		return ret;
	}
	
	
	// phase 2
	
	private static boolean writeResult(XWPFDocument hdoc, HashMap<String, String> subst, String outfilename) {
		
		replace(hdoc.getParagraphs(), subst);

		for (XWPFTable tbl : hdoc.getTables()) {
			if (tbl == null)
				continue;
			
			for (XWPFTableRow row : tbl.getRows()) {
				if (row == null)
					continue;
				
				for (XWPFTableCell cell : row.getTableCells()) {
					if (cell == null)
						continue;
					
					replace(cell.getParagraphs(), subst);
				}
			}
		}
		
		for (XWPFFooter footer : hdoc.getFooterList()) {
			if (footer == null)
				continue;
			
			replace(footer.getParagraphs(), subst);
		}
		
		for (XWPFHeader header : hdoc.getHeaderList()) {
			if (header == null)
				continue;
			
			replace(header.getParagraphs(), subst);
		}
		
		try {
			hdoc.write(new FileOutputStream(outfilename));
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static void replace(List<XWPFParagraph> paragraphs, HashMap<String, String> subst) {
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
