package y.ui;

import y.utils.AddressBook;

public class JHashTableModelAddressBook extends JHashTableModel {

	private static final long serialVersionUID = 3790750099069842186L;
	
	private AddressBook book;
	
	public JHashTableModelAddressBook(AddressBook book) {
		super();
		
		this.book = book;		
	}
	
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int column) {
		
		if (column == 0) {
			super.setValueAt(aValue, rowIndex, column);
			return;
		}
		
		try {
			final Object content = super.getValueAt(rowIndex, 0);
			final String text = (String) content;
			
			final String best_text = book.getStartingName((String) aValue);
			final String newtext = text.replace(".NOME", ".EMAIL").replace(".nome", ".email");
			
			if (text.toLowerCase().endsWith(".nome")) {
				final int rown = super.getRow(newtext);
				if (rown >= 0) {
					super.setValueAt(best_text, rowIndex, column);
					super.setValueAt(book.getEmail(best_text), rown, column);
					this.fireTableDataChanged();
				}
			}
			else
				super.setValueAt(aValue, rowIndex, column);
		}
		catch (Exception e) {}
	}
}
