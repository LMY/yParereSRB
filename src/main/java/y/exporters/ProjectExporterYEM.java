package y.exporters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import y.em.Cell;
import y.em.Cell.Technology;
import y.em.Project;
import y.em.Site;
import y.em.SiteDBInfo;
import y.geom.Point3d;

class ProjectExporterYEM implements ProjectExporter {

	final static String TAG_PROJECT = "project";
	final static String TAG_RECIPIENTS = "recipients";
	final static String TAG_ADDRESS = "address";
	final static String TAG_SITES = "sites";
	final static String TAG_SITE = "site";
	final static String TAG_ID = "id";
	final static String TAG_NOTE = "note";
	final static String TAG_COMUNE = "comune";
	final static String TAG_CATASTO = "catasto";
	final static String TAG_GESTORE = "gestore";
	final static String TAG_CODE = "code";
	final static String TAG_PROTOCOL = "protocollo";
	final static String TAG_PROTOIN = "proto_in";
	final static String TAG_PROTOOUT = "proto_out";
	final static String TAG_PROTODATEIN = "proto_in_date";
	final static String TAG_PROTODATEOUT = "proto_out_date";
	final static String TAG_ACTIVATION = "attivazione";
	final static String TAG_REALIZATION = "realizzazione";
	final static String TAG_POSITION = "position";
	final static String TAG_X = "x";
	final static String TAG_Y = "y";
	final static String TAG_Z = "z";
	final static String TAG_CELLS = "cells";
	final static String TAG_CELL = "cell";
	final static String TAG_PATHMSI = "PathMsi";
	final static String TAG_HEIGHT = "height";
	final static String TAG_QUOTE = "quote";
	final static String TAG_DIRECTION = "direction";
	final static String TAG_POWER = "power";
	final static String TAG_GAIN = "gain";
	final static String TAG_TILTM = "tilt_m";
	final static String TAG_TILTE = "tilt_e";
	final static String TAG_ALPHA = "alpha";
	final static String TAG_TECHNOLOGIES = "technologies";
	final static String TAG_FREQUENCY = "frequency";
	final static String TAG_STUDIO = "studio_tecnico";
	final static String TAG_COMMENT = "comment";

	
	@Override
	public Project importProject(String filename) throws Exception {
		return importYEMProject(filename);
	}


	public static Project importYEMProject(String filename) throws Exception {
		File file = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();

		final Element root = doc.getDocumentElement(); // doc.getElementsByTagName("project");
		final NodeList nodes = root.getChildNodes();

		//output vars
		String studio_tecnico = "";
		String comment = "";
		List<String> destinatari = new ArrayList<String>();
		List<Site> sites = new ArrayList<Site>();

		for (int i = 0; i < nodes.getLength(); i++) {
			final Node node = nodes.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				final String nodename = node.getNodeName();

				if (nodename.equals(TAG_STUDIO))
					studio_tecnico = getXMLNodeValue(node);
				else if (nodename.equals(TAG_COMMENT))
					comment = getXMLNodeValue(node);
				else if (nodename.equals(TAG_RECIPIENTS)) {
					final NodeList dnodes = node.getChildNodes();
					for (int d=0; d<dnodes.getLength(); d++)
						destinatari.add(getXMLNodeValue(dnodes.item(d)));
				}
				else if (nodename.equals(TAG_SITES)) {
					final NodeList snodes = node.getChildNodes();
					for (int d=0; d<snodes.getLength(); d++)
						sites.add( parseYEMSite(snodes.item(d)) );
				}
			}
		}

		Project p = new Project();
		p.add(sites.toArray(new Site[sites.size()]));
		p.setDestinatari(destinatari);
		p.setStudio_tecnico(studio_tecnico);
		p.setComment(comment);

		return p;
	}
	
	private static Site parseYEMSite(Node node) {
		final String id = node.getAttributes().getNamedItem(TAG_ID).getNodeValue();
		String comment = "";
		String catasto = "";
		Point3d position = new Point3d();
		List<Cell> cells = new ArrayList<Cell>();
		SiteDBInfo ip = new SiteDBInfo();
		
		final NodeList childs = node.getChildNodes();
		for (int i=0; i<childs.getLength(); i++) {
			final Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				 final String nodename = child.getNodeName();
				 if (nodename.equals(TAG_COMMENT))
					 comment = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_NOTE))
					 ip.setNote(getXMLNodeValue(child));
				 else if (nodename.equals(TAG_ADDRESS))
					 ip.setIndirizzo(getXMLNodeValue(child));
				 else if (nodename.equals(TAG_COMUNE))
					 ip.setComune(getXMLNodeValue(child));
				 else if (nodename.equals(TAG_CATASTO))
					 catasto = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_GESTORE))
					 ip.setOperatore(getXMLNodeValue(child));
				 else if (nodename.equals(TAG_CODE))
					 ip.setCodiceSito(getXMLNodeValue(child));
				 else if (nodename.equals(TAG_PROTOCOL)) {
					 final NodeList grandchilds = child.getChildNodes();
					 for (int k=0; k<grandchilds.getLength(); k++) {
						 final Node grandchild = grandchilds.item(k);
						 if (grandchild.getNodeType() == Node.ELEMENT_NODE) {
							 final String nodename2 = grandchild.getNodeName();
							 if (nodename2.equals(TAG_PROTOIN))
								 ip.setProto_in(getXMLNodeValue(grandchild));
							 else if (nodename2.equals(TAG_PROTOOUT))
								 ip.setProto_out(getXMLNodeValue(grandchild));
							 else if (nodename2.equals(TAG_PROTODATEIN))
								 ip.setData_proto_in(getXMLNodeValue(grandchild));
							 else if (nodename2.equals(TAG_PROTODATEOUT))
								 ip.setData_proto_out(getXMLNodeValue(grandchild));
							 else if (nodename2.equals(TAG_ACTIVATION))
								 ip.setData_attivazione(getXMLNodeValue(grandchild));
							 else if (nodename2.equals(TAG_REALIZATION))
								 ip.setData_realizzazione(getXMLNodeValue(grandchild));
						 }
					 }
				 }
				 else if (nodename.equals(TAG_POSITION))
					 position = parseYEMPosition(child);
				 else if (nodename.equals(TAG_CELLS)) {
					 final NodeList cellNodes = child.getChildNodes();
					 for (int c=0; c<cellNodes.getLength(); c++) {
						 final Cell newcell = parseYEMCell(cellNodes.item(c));
						 if (newcell != null) {
							 cells.add(newcell);
							 newcell.setSiteID(id);
						 }
					 }
				 }
			}
		}
		
		Site s = new Site(id);
		s.setDbinfo(ip);
		s.setPosition(position);
		s.getDbinfo().setCatasto(catasto);
		s.setComment(comment);
		
		for (Cell newcell : cells)
			s.AddCell(newcell);
		
		return s;
	}

	private static Point3d parseYEMPosition(Node node) {
		final Point3d pos = new Point3d();
		
		final NodeList childs = node.getChildNodes();
		for (int i=0; i<childs.getLength(); i++) {
			final Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				 final String nodename = child.getNodeName();
				 if (nodename.equals(TAG_X))
					 pos.setX(Double.parseDouble(getXMLNodeValue(child)));
				 else if (nodename.equals(TAG_Y))
					 pos.setY(Double.parseDouble(getXMLNodeValue(child)));
				 else if (nodename.equals(TAG_Z))
					 pos.setZ(Double.parseDouble(getXMLNodeValue(child)));
			}
		}
					 
		return pos;	
	}

	private static Cell parseYEMCell(Node node)
	{
		String PathMsi = "";
		String height = "";
		String quote = "";
		String direction = "";
		String power = "";
		String gain = "";
		String tilt_m = "";
		String tilt_e = "";
		String alpha = "";
		String note = "";
		String x = "";
		String y = "";
		String frequency = "";
		List<Technology> techs = null;
		
		final NodeList childs = node.getChildNodes();
		for (int i=0; i<childs.getLength(); i++) {
			final Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				 final String nodename = child.getNodeName();
				 if (nodename.equals(TAG_PATHMSI))
					 PathMsi = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_HEIGHT))
					 height = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_QUOTE))
					 quote = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_DIRECTION))
					 direction = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_POWER))
					 power = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_GAIN))
					 gain = getXMLNodeValue(child);

				 else if (nodename.equals(TAG_TILTM))
					 tilt_m = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_TILTE))
					 tilt_e = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_ALPHA))
					 alpha = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_NOTE))
					 note = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_TECHNOLOGIES))
					 techs = Cell.Technology.create(getXMLNodeValue(child));
				 
				 else if (nodename.equals(TAG_FREQUENCY))
					 frequency = getXMLNodeValue(child);
				 
				 else if (nodename.equals(TAG_X))
					 x = getXMLNodeValue(child);
				 else if (nodename.equals(TAG_Y))
					 y= getXMLNodeValue(child);
			}
		}
		
		Cell c = new Cell();
		c.setPathMsi(PathMsi);
		c.setNote(note);
		
		if (techs != null) c.setTechnologies(techs);
		
		try { c.setX(Double.parseDouble(x)); } catch (Exception e) {}
		try { c.setY(Double.parseDouble(y)); } catch (Exception e) {}
		
		try { c.setAlpha(Double.parseDouble(alpha)/KILO); } catch (Exception e) {}
		try { c.setHeight(Double.parseDouble(height)/KILO); } catch (Exception e) {}
		try { c.setQuote(Double.parseDouble(quote)/KILO); } catch (Exception e) {}
		try { c.setDirection(Double.parseDouble(direction)); } catch (Exception e) {}
		try { c.setPower(Double.parseDouble(power)/KILO); } catch (Exception e) {}
		try { c.setGain(Double.parseDouble(gain)/KILO); } catch (Exception e) {}

		try { c.setTiltM(Integer.parseInt(tilt_m)); } catch (Exception e) {}
		try { c.setTiltE(Integer.parseInt(tilt_e)); } catch (Exception e) {}
		
		try { c.setFrequency(Double.parseDouble(frequency)); } catch (Exception e) {}
		
		return c;
	}

	private static double KILO = 1000.0;
	
	
	private static String getXMLNodeValue(Node node) {
		return node.getLastChild().getTextContent().trim();
	}
}
