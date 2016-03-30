package y.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFFieldRun;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class UtilsOffice {

	public final static String REGEXP_SPACES_AND_STUFF = "[\\s)(\\,\\/]+";
	
	public static void putTextInRow(XWPFTableRow row, String fontname, int fontsize, String[] strings) {
		putTextInRow(row, fontname, fontsize, strings, (ParagraphAlignment)null);
	}
	
	public static void putTextInRow(XWPFTableRow row, String fontname, int fontsize, String[] strings, ParagraphAlignment aalign) {
		final ParagraphAlignment[] align = new ParagraphAlignment[strings.length];
		for (int i=0; i<align.length; i++)
			align[i] = aalign;
		
		putTextInRow(row, fontname, fontsize, strings, align);
	}
	
	public static void putTextInRow(XWPFTableRow row, String fontname, int fontsize, String[] strings, ParagraphAlignment[] align) {
		for (int i=0; i<strings.length; i++) {
			XWPFTableCell cell = row.getCell(i);
			if (cell == null)
				cell = row.createCell();
			
			// remove all paragraphs
			while (cell.getParagraphs().size() > 0)
				cell.removeParagraph(0);
			
			// create a paragraph, containing 1 run
			final XWPFParagraph paragraph = cell.addParagraph();

			if (align[i] != null && i < align.length)
				paragraph.setAlignment(align[i]);
			
			final XWPFRun run = paragraph.createRun();
			
			run.setFontFamily(fontname);
			run.setFontSize(fontsize);
			run.setText(strings[i], 0);
		}
	}
	
	
	
	public static void putTextInRowIntest(XWPFTableRow row, String fontname, int fontsize, String[] strings) {
		putTextInRowIntest(row, fontname, fontsize, strings, (ParagraphAlignment)null);
	}
	
	public static void putTextInRowIntest(XWPFTableRow row, String fontname, int fontsize, String[] strings, ParagraphAlignment aalign) {
		final ParagraphAlignment[] align = new ParagraphAlignment[strings.length];
		for (int i=0; i<align.length; i++)
			align[i] = aalign;
		
		putTextInRowIntest(row, fontname, fontsize, strings, align);
	}
	
	public static void putTextInRowIntest(XWPFTableRow row, String fontname, int fontsize, String[] strings, ParagraphAlignment[] align) {
		for (int i=0; i<strings.length; i++) {
			XWPFTableCell cell = row.getCell(i);
			if (cell == null)
				cell = row.createCell();
			
			// remove all paragraphs
			while (cell.getParagraphs().size() > 0)
				cell.removeParagraph(0);
			
			// create a paragraph, containing 1 run
			final XWPFParagraph paragraph = cell.addParagraph();

			if (align[i] != null && i < align.length)
				paragraph.setAlignment(align[i]);
			
			cell.setVerticalAlignment(XWPFVertAlign.CENTER);
			
			final XWPFRun run = paragraph.createRun();
			
			if (i == 0) {
				run.setBold(true);
				cell.setColor("E6E6E6");
			}

			run.setFontFamily(fontname);
			run.setFontSize(fontsize);
			run.setText(strings[i], 0);
		}
	}
	
	public static String[] formatIntestRow(String[] strings, int rown) {
		final String[] ret = new String[strings.length];
		
		for (int i=0; i<strings.length; i++)
			try {
				if (rown == 1 || rown == 2)	// 1 dec. coordinates
					ret[i] = ""+ Utils.formatDouble(Double.parseDouble(strings[i]), 1);
				else if (rown == 5)	// 2 dec. height
					ret[i] = ""+ Utils.formatDouble(Double.parseDouble(strings[i]), 2);
				else if (rown == 0 || rown == 4 || rown == 6 || rown == 7 || rown == 8 || rown == 9 || rown == 10)
					ret[i] = ""+ Utils.formatDoubleAsNeeded(Double.parseDouble(strings[i]));
				else 
					ret[i] = strings[i];
			}
			catch (NumberFormatException e) {
				ret[i] = strings[i];
			}
	
		return ret;
	}
	
	public static List<XWPFTable> getTable(CustomXWPFDocument hdoc, String which_one) {
		final List<XWPFTable> ret = new ArrayList<XWPFTable>();

		//        /|
		//       / |
		//      /  |
		//     /   | oh well
		//    /    |
		//   /     |
		//  /      |
		// /       |
		for (XWPFTable tbl : hdoc.getTables())
			if (tbl != null)
				for (XWPFTableRow row : tbl.getRows())
					if (row != null)
						for (XWPFTableCell cell : row.getTableCells())
							if (cell != null)
								for (XWPFParagraph p : cell.getParagraphs())
									if (p != null)
										for (XWPFRun r : p.getRuns())
											if (r != null) {
												final String text = r.getText(0);
												if (Utils.IsNullOrEmpty(text))
													continue;
												
												final String[] parts = text.split(REGEXP_SPACES_AND_STUFF);
												for (String word : parts)
													if (word.equals(which_one))
														if (!ret.contains(tbl))
															ret.add(tbl);
											}
		
		return ret;
	}
	
	

	public static List<String[]> getMeasureValue(String filename) throws Exception {
		
		final List<String[]> ret = new ArrayList<String[]>();
		
		final FileInputStream fis = new FileInputStream(new File(filename));

		// Finds the workbook instance for XLSX file
		XSSFWorkbook myWorkBook = null;
		try {
			myWorkBook = new XSSFWorkbook (fis);

			// Return first sheet from the XLSX workbook
			final XSSFSheet mySheet = myWorkBook.getSheetAt(0);

			// Get iterator to all the rows in current sheet
			final Iterator<Row> rowIterator = mySheet.iterator();

			// Traversing over each row of XLSX file
			while (rowIterator.hasNext()) {
				final Row row = rowIterator.next();

				final List<String> line = new ArrayList<String>();
				int col = 0;
				final Iterator<Cell> cellIterator = row.cellIterator();
				
				while (cellIterator.hasNext()) {
					final Cell cell = cellIterator.next();
					
					String value = "";

					switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
							break;
						case Cell.CELL_TYPE_NUMERIC:
							value = ""+cell.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_BOOLEAN:
							value = ""+cell.getBooleanCellValue();
							break;
						default :
					}
					
					try {
						if (col == 0)
							value = ""+(int)Double.parseDouble(value);
						else if (col == 1 || col == 3 || col == 4)
							; // string columns
						else // 2, 5, 6, 7
							value = ""+ Utils.formatDouble(Double.parseDouble(value), 1);
					}
					catch (NumberFormatException e) {} // first line will fail every conversion. anyway, whatever the case is, do not convert
					
					if (!value.isEmpty())
						line.add(value);
					
					++col;
				}

				ret.add(line.toArray(new String[line.size()]));
			}
		}
		catch (Exception e) {}
		finally {
			if (myWorkBook != null)
				try { myWorkBook.close(); }
				catch (Exception e2) {}
		}
		
		return ret;		
	}
	
	
	public static List<String[]> getRadioelettrica(String filename) throws Exception {
		
		final List<String[]> ret = new ArrayList<String[]>();
		
		final FileInputStream fis = new FileInputStream(new File(filename));

		// Finds the workbook instance for XLSX file
		XSSFWorkbook myWorkBook = null;
		try {
			myWorkBook = new XSSFWorkbook (fis);

			// Return first sheet from the XLSX workbook
			final XSSFSheet mySheet = myWorkBook.getSheetAt(0);

			// Get iterator to all the rows in current sheet
			final Iterator<Row> rowIterator = mySheet.iterator();

			// Traversing over each row of XLSX file
			while (rowIterator.hasNext()) {
				final Row row = rowIterator.next();

				final List<String> line = new ArrayList<String>();
				final Iterator<Cell> cellIterator = row.cellIterator();
				
				while (cellIterator.hasNext()) {
					final Cell cell = cellIterator.next();
					
					String value = "";

					switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							value = cell.getStringCellValue();
							break;
						case Cell.CELL_TYPE_NUMERIC:
							value = ""+cell.getNumericCellValue();
							break;
						case Cell.CELL_TYPE_BOOLEAN:
							value = ""+cell.getBooleanCellValue();
							break;
						default :
					}
					
				
					if (!value.isEmpty())
						line.add(value);
				}

				ret.add(line.toArray(new String[line.size()]));
			}
		}
		catch (Exception e) {}
		finally {
			if (myWorkBook != null)
				try { myWorkBook.close(); }
				catch (Exception e2) {}
		}
		
		return ret;		
	}
	
	
	public static boolean haveSameStyle(XWPFRun a, XWPFRun b) {
		
		if (a.getFontSize() != b.getFontSize())
			return false;
		
		if (a.isBold() != b.isBold())
			return false;
		
		if (a.isItalic() != b.isItalic())
			return false;
		
		if (a.isStrikeThrough() != b.isStrikeThrough())
			return false;
		
		if (a.isDoubleStrikeThrough() != b.isDoubleStrikeThrough())
			return false;
		
		if (a.isCapitalized() != b.isCapitalized())
			return false;
		
		if (a.isEmbossed() != b.isEmbossed())
			return false;
		if (a.isHighlighted() != b.isHighlighted())
			return false;
		if (a.isImprinted() != b.isImprinted())
			return false;
		if (a.isShadowed() != b.isShadowed())
			return false;
		if (a.isSmallCaps() != b.isSmallCaps())
			return false;
		
		if (a.getSubscript() != b.getSubscript())
			return false;
		
		try { if (!a.getFontFamily().equals(b.getFontFamily())) return false; } catch (Exception e) {}
		try { if (!a.getFontName().equals(b.getFontName())) return false; } catch (Exception e) {}
		try { if (!a.getColor().equals(b.getColor())) return false; } catch (Exception e) {}
		try { if (a.getUnderline() != b.getUnderline()) return false; } catch (Exception e) {}

		if ((a instanceof XWPFHyperlinkRun) != (b instanceof XWPFHyperlinkRun))
			return false;
		
		if ((a instanceof XWPFFieldRun) != (b instanceof XWPFFieldRun))
			return false;
		
		return true;
	}

}
