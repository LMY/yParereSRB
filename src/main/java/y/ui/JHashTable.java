package y.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class JHashTable extends JPanel {

	private static final long serialVersionUID = -6612588161525911197L;

	
	private HashTableModel model;
	private JTable table;
	
	public JHashTable() {
		super();
		setLayout(new BorderLayout());
		
		model = new HashTableModel();
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
	
	private class HashTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1561888643775208744L;
		
		private List<String> keys;
		private List<String> values;
		
		public HashTableModel() {
			keys = new ArrayList<String>();
			values = new ArrayList<String>();
		}
		
		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return values.size();
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			return arg1 == 0 ? keys.get(arg0) : values.get(arg0);
		}
		
		public void clearData() {
			keys.clear();
			values.clear();
			
			this.fireTableDataChanged();
		}
		
		public void updateData(Set<String> map) {
			keys.clear();
			values.clear();

			for (String key : map) {
				keys.add(key);
				values.add("");
			}
			
			this.fireTableDataChanged();
		}
		
		public void updateData(Map<String, String> map) {
			keys.clear();
			values.clear();
			
			for (String key : map.keySet()) {
				keys.add(key);
				values.add(map.get(key));
			}
			
			this.fireTableDataChanged();
		}
		
		@Override
	    public String getColumnName(int column) {
			return column == 0 ? "Field" : "Value"; 
	    }
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int column) {
			if (column == 0)
				keys.set(rowIndex, (String) aValue);
			else
				values.set(rowIndex, (String) aValue);
		}
		
	    @Override
	    public Class<? extends Object> getColumnClass(int column) {
	    	return String.class;
	    }
	    
	    @Override
	    public boolean isCellEditable(int row, int column) {
	    	return column == 1;
	    }
	    
	    public Map<String, String> getMap() {
	    	final Map<String, String> ret = new HashMap<String, String>();
	    	
	    	for (int i=0, imax=keys.size(); i<imax; i++)
	    		ret.put(keys.get(i), values.get(i));
	    	
	    	return ret;
	    }
	}
}
