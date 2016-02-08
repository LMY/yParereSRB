package y.exporters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import y.em.Cell;
import y.em.Project;
import y.em.Site;
import y.em.SiteDBInfo;
import y.utils.Utils;

class ProjectExporterPRJ implements ProjectExporter {
	public Project importProject(String filename) throws Exception {
		Project p = new Project();
		
		BufferedReader s = null;
		
		try {
			s = new BufferedReader(new FileReader(filename));
		}
		catch (IOException e) { return null; }
		
		String line;
		int linen=1;
		
		try {
			while ((line=s.readLine()) != null) {
				line = line.trim();
				
				if (line.equalsIgnoreCase("[generali]") || line.length() == 0);
				else if (line.startsWith("NCelle")) {
//					StringTokenizer z = new StringTokenizer(line, "=");
//					z.nextToken();
//					ncelle = Integer.parseInt(z.nextToken());
				}
				else if (line.startsWith("NSiti")) {
//					StringTokenizer z = new StringTokenizer(line, "=");
//					z.nextToken();
//					p.sites = new Site[Integer.parseInt(z.nextToken())];
				}
				else if (line.startsWith("[Sito"))
					p.add( ReadSiteFromFile(s) );
				else if (line.startsWith("[Cella")) {
					Cell cell = ReadCellFromFile(s);
					
					final Site thesite = p.getSiteByID(cell.getSiteID());
					if (thesite == null)
						Utils.MessageBox("ERROR, site id "+cell.getSiteID()+ " not found.\n", "Errore Interno");
					else {
						thesite.AddCell(cell);
					}
//					i_cell++;
				}
			}
		}
		catch (IOException e) { throw new Exception("IOException, line "+linen); }
		catch (NumberFormatException e) { throw new Exception("NumberFormatException, line "+linen); }
		catch (NullPointerException e) { throw new Exception("NullPointerException, line "+linen); }
		finally {
			if (s != null)
				try { s.close(); }
				catch (Exception e) {}
		}
		
		return p;
	}
	
	
	private Site ReadSiteFromFile(BufferedReader s) throws Exception
	{
		String line;
		
		String ID = "";
		String comment = "";
		SiteDBInfo dbinfo = new SiteDBInfo();

		try {
			while ((line=s.readLine()) != null) {
				line = line.trim();
				
				if (line.equals(""))		// end of Cell definition
					break;
				else {
					if (!line.contains("="))
						return null;
					
					final String value = line.substring(line.indexOf("=")+1).trim();
					
					if (line.startsWith("Codice"))
						ID = value;
					else if (line.startsWith("Intesta"))
						comment = value;
					else if (line.startsWith("Operatore"))
						dbinfo.setOperatore(value);
					else if (line.startsWith("Indirizzo"))
						dbinfo.setIndirizzo(value);
					else if (line.startsWith("Comune"))
						dbinfo.setComune(value);
					else if (line.startsWith("Provincia"))
						dbinfo.setProvincia(value);
					else if (line.startsWith("Note"))
						dbinfo.setNote(value);
					else if (line.startsWith("Stato"))
						dbinfo.setStato(value);
					else if (line.startsWith("CodiceSito"))
						dbinfo.setCodiceSito(value);
				}
			}
		}
		catch (IOException e) { throw new Exception("Site::IOException"); }
		catch (NumberFormatException e) { throw new Exception("Site::NumberFormatException"); }
		catch (NullPointerException e) { throw new Exception("Site::NullPointerException"); }
		
		Site site = new Site(ID);
		site.setComment(comment);
		site.setDbinfo(dbinfo);
		
		return site;
	}
	
	
	
	private Cell ReadCellFromFile(BufferedReader s)
	{
		String line;
		Cell c = new Cell();

		try {
			while ((line=s.readLine()) != null) {
				line = line.trim().toLowerCase();
				
				if (line.equals(""))		// end of Cell definition
					break;
				else {
					if (!line.contains("="))
						continue;
					
					final String value = line.substring(line.indexOf("=")+1).trim();
//					final String lowValue = value.toLowerCase();
					
					try {
					if (line.startsWith("pathmsi"))
						c.setPathMsi( value.toLowerCase() );
					else if (line.startsWith("altezza"))
						c.setHeight( Double.parseDouble(value) );
					else if (line.startsWith("quota"))
						c.setQuote( Double.parseDouble(value) );
					else if (line.startsWith("direzione"))
						c.setDirection( Double.parseDouble(value) );
					else if (line.startsWith("potenza(kw)"))
						c.setPower( 1000*Double.parseDouble(value) );	// in KW
					else if (line.startsWith("guadagno"))
						c.setGain( Double.parseDouble(value) );
					else if (line.startsWith("tilt_m"))
						c.setTiltM( (int)Double.parseDouble(value) );
					else if (line.startsWith("tilt_e"))
						c.setTiltE( (int)Double.parseDouble(value) );
					else if (line.startsWith("sito"))
						c.setSiteID(value);
					else if (line.startsWith("x"))
						c.setX( Double.parseDouble(value) );
					else if (line.startsWith("y"))
						c.setY( Double.parseDouble(value) );
//					else if (line.startsWith("alpha"))
//						c.setAlpha(Double.parseDouble(value));
					}
					catch (Exception e) { Utils.MessageBox("Warning, leggendo una cella: "+e.toString()+"\nriga: "+line, "WARNING"); }
				}
			}
		}
		catch (Exception e) { Utils.MessageBox("Cell::Exception: "+e.toString(), "ERRORE"); return null; }
		
		return c;
	}
}
