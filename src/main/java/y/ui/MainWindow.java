package y.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
				go();
				enablePanels();
			}
		});
		
		phase0panel.add(phase0panelC, BorderLayout.CENTER);
		phase0panel.add(goButton, BorderLayout.SOUTH);
		
		
		this.add(phase0panel, BorderLayout.NORTH);
		
		enablePanels();
	//	CreateMenuBar();
		
		pack();
		Utils.centerWindow(this);
	}
	
//	public void CreateMenuBar()
//	{
//		final JMenuBar menubar = new JMenuBar();
//		
//		final JMenu filemenu = new JMenu("File");
//		filemenu.setMnemonic(KeyEvent.VK_F);
//		final JMenuItem menunew = new JMenuItem("Nuovo");
//		menunew.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) { New(); }
//		});
//		menunew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
//
//		final JMenuItem openmenu = new JMenuItem("Apri...");
//		openmenu.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) { Open(); }
//		});
//		openmenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
//
//		final JMenuItem closemenu = new JMenuItem("Chiudi");
//		closemenu.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) { Close(); }
//		});
//		closemenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
//
//		final JMenuItem savemenu = new JMenuItem("Salva");
//		savemenu.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) { Save(); }
//		});
//		savemenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
//
//		final JMenuItem saveasmenu = new JMenuItem("Salva con nome...");
//		saveasmenu.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) { SaveAs(); }
//		});
//		saveasmenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
//
//		final JMenuItem exitmenu = new JMenuItem("Esci");
//		exitmenu.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) { System.exit(0); }
//		});
//
//		filemenu.add(menunew);
//		filemenu.addSeparator();
//		filemenu.add(openmenu);
//		filemenu.addSeparator();
//		filemenu.add(closemenu);
//		filemenu.addSeparator();
//		filemenu.add(savemenu);
//		filemenu.add(saveasmenu);
//		filemenu.addSeparator();
//		filemenu.add(configmenu);
//		filemenu.addSeparator();
//		filemenu.add(exitmenu);
//
//		// HELP
//		final JMenu helpmenu = new JMenu("Help");
//		helpmenu.setMnemonic(KeyEvent.VK_H);
//		final JMenuItem manualmenu = new JMenuItem("Manuale");
//		manualmenu.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) { Help(); }
//		});
//		manualmenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
//		helpmenu.add(manualmenu);
//
//		final JMenuItem aboutmenu2 = new JMenuItem("About");
//		aboutmenu2.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) { About(); }
//		});	
//		helpmenu.addSeparator();
//		helpmenu.add(aboutmenu2);
//		
//		
//		menubar.add(filemenu);
//		menubar.add(Box.createHorizontalGlue());
//		menubar.add(helpmenu);
//
//		setJMenuBar(menubar);
//	}
	
	private Project current_project = null;
	
	private boolean go() {
		try {
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
	
	private void onClose() {
		
	}
	
	private void enablePanels() {
		Utils.enableComponents(phase0panel, current_project == null);
	}
	

	
//	private void Help() {
//		
//	}
//	
//	private void About() {
//		
//	}
//	
//	private void Configure() {
//		
//	}
//	
//	private void New() {
//		current_project = null;
//		enablePanels();
//	}
//	
//	private void Open() {
//		
//	}
//	
//	private void Close() {
//		
//	}
//	
//	private void Save() {
//		
//	}
//	
//	private void SaveAs() {
//		
//	}
}
