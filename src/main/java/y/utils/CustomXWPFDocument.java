package y.utils;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlToken;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// http://stackoverflow.com/questions/17745466/insert-picture-in-word-document
public class CustomXWPFDocument extends XWPFDocument
{
    public CustomXWPFDocument(InputStream in) throws IOException
    {
        super(in);
    }

    public void createCustomPicture(XWPFRun run, String picFilename, int width, int height) throws Exception
    {
		final int format = getPictureFormat(picFilename);

		FileInputStream is = null;
		try {
			is = new FileInputStream(picFilename);
			final String id = addPictureData(is, format);
			// http://stackoverflow.com/questions/17745466/insert-picture-in-word-document
			createPicture(run, id, getNextPicNameNumber(format), width, height);
			
//			r.addPicture(is, format, picFilename,
//							Units.toEMU((double) config.get(Integer.class, "Image.width")),
//							Units.toEMU((double) config.get(Integer.class, "Image.height")));
		}
		finally {
			if (is != null)
				try { is.close(); }
				catch (Exception e2) {}
		}
    }
    
    
    private void createPicture(XWPFRun run, String blipId, int id, int width, int height)
    {
        final int EMU = 9525;
        width *= EMU;
        height *= EMU;
        //String blipId = getAllPictures().get(id).getPackageRelationship().getId();

        final CTInline inline = /* createParagraph().createRun()*/run.getCTR().addNewDrawing().addNewInline();

        String picXml = "" +
                "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">" +
                "   <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">" +
                "      <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">" +
                "         <pic:nvPicPr>" +
                "            <pic:cNvPr id=\"" + id + "\" name=\"Generated\"/>" +
                "            <pic:cNvPicPr/>" +
                "         </pic:nvPicPr>" +
                "         <pic:blipFill>" +
                "            <a:blip r:embed=\"" + blipId + "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>" +
                "            <a:stretch>" +
                "               <a:fillRect/>" +
                "            </a:stretch>" +
                "         </pic:blipFill>" +
                "         <pic:spPr>" +
                "            <a:xfrm>" +
                "               <a:off x=\"0\" y=\"0\"/>" +
                "               <a:ext cx=\"" + width + "\" cy=\"" + height + "\"/>" +
                "            </a:xfrm>" +
                "            <a:prstGeom prst=\"rect\">" +
                "               <a:avLst/>" +
                "            </a:prstGeom>" +
                "         </pic:spPr>" +
                "      </pic:pic>" +
                "   </a:graphicData>" +
                "</a:graphic>";

        //CTGraphicalObjectData graphicData = inline.addNewGraphic().addNewGraphicData();
        XmlToken xmlToken = null;
        try
        {
            xmlToken = XmlToken.Factory.parse(picXml);
        }
        catch(XmlException xe)
        {
            xe.printStackTrace();
        }
        inline.set(xmlToken);
        //graphicData.set(xmlToken);

        inline.setDistT(0);
        inline.setDistB(0);
        inline.setDistL(0);
        inline.setDistR(0);

        final CTPositiveSize2D extent = inline.addNewExtent();
        extent.setCx(width);
        extent.setCy(height);

        final CTNonVisualDrawingProps docPr = inline.addNewDocPr();
        docPr.setId(id);
        docPr.setName("Picture " + id);
        docPr.setDescr("Generated");
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
