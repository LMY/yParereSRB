package y.ui;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTable;

public class JHashTable extends JPanel {

	private static final long serialVersionUID = -6612588161525911197L;
	
	private JHashTableModel model;
	private JTable table;
	
	public JHashTable(JHashTableModel model) {
		super();
		setLayout(new BorderLayout());
		
		this.model = model;
		table = new JTable(model);
		
		this.add(table, BorderLayout.CENTER);
	}
	
	public void update(Set<String> theset) {
		model.updateData(theset);
	}

	public void update(Map<String, String> map) {
		model.updateData(map);
	}
	
	public void clear() {
		model.clearData();
	}
	
	public Map<String, String> getMap() {
		return model.getMap();
	}
}
