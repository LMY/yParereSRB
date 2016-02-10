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
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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
					ret[i] = ""+ Utils.formatDouble(Double.parseDouble(strings[i]), 1);
				else if (rown == 0 || rown == 4 || rown == 6 || rown == 7 || rown == 8 || rown == 9 || rown == 10)
					ret[i] = ""+ Utils.formatDoubleAsNeeded(Double.parseDouble(strings[i]), 1);
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
	
	public static int getPictureFormat(String imgFile) {
		imgFile = imgFile.toLowerCase();
		
        if (imgFile.endsWith(".emf")) return XWPFDocument.PICTURE_TYPE_EMF;
        else if(imgFile.endsWith(".wmf")) return XWPFDocument.PICTURE_TYPE_WMF;
        else if(imgFile.endsWith(".pict")) return XWPFDocument.PICTURE_TYPE_PICT;
        else if(imgFile.endsWith(".jpeg") || imgFile.endsWith(".jpg")) return XWPFDocument.PICTURE_TYPE_JPEG;
        else if(imgFile.endsWith(".png")) return XWPFDocument.PICTURE_TYPE_PNG;
        else if(imgFile.endsWith(".dib")) return XWPFDocument.PICTURE_TYPE_DIB;
        else if(imgFile.endsWith(".gif")) return XWPFDocument.PICTURE_TYPE_GIF;
        else if(imgFile.endsWith(".tiff")) return XWPFDocument.PICTURE_TYPE_TIFF;
        else if(imgFile.endsWith(".eps")) return XWPFDocument.PICTURE_TYPE_EPS;
        else if(imgFile.endsWith(".bmp")) return XWPFDocument.PICTURE_TYPE_BMP;
        else if(imgFile.endsWith(".wpg")) return XWPFDocument.PICTURE_TYPE_WPG;
        
        return -1;
	}
}
