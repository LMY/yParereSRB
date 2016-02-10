package y.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class UtilsOffice {

	public final static String REGEXP_SPACES_AND_STUFF = "[\\s)(\\,\\/]+";
	
	public static void putTextInRow(XWPFTableRow row, String fontname, int fontsize, String[] strings) {
		for (int i=0; i<strings.length; i++) {
			final XWPFTableCell cell = row.getCell(i);
			
			// remove all paragraphs
			while (cell.getParagraphs().size() > 0)
				cell.removeParagraph(0);
			
			// create a paragraph, containing 1 run
			final XWPFRun run = cell.addParagraph().createRun();
			
			run.setFontFamily(fontname);
			run.setFontSize(fontsize);
			run.setText(strings[i], 0);
		}
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
