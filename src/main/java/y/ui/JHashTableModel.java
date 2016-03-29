package y.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

public class JHashTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1561888643775208744L;
	
	private List<String> keys;
	private List<String> values;
	
	public JHashTableModel() {
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
	
	public int getRow(String prop) {
		for (int i=0; i<keys.size(); i++)
			if (keys.get(i).equals(prop))
				return i;
		
		return -1;
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
