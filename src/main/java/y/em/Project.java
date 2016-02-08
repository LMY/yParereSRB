package y.em;

import java.util.ArrayList;
import java.util.List;

import y.geom.Point3d;


public class Project
{
	private Site[] sites;
	
	private String studio_tecnico;
	private String comment;
	
	private List<String> destinatari;
	
	public Project()
	{
		this(new Site[0]);
	}
	
	public Project(Site[] sites)
	{
		this.sites = sites;
		studio_tecnico = "";
		comment = "";
		destinatari = new ArrayList<String>();
	}
	
	
	public void add(Site c)
	{
		final Site[] newsites = new Site[sites.length+1];
		for (int i=0; i<sites.length; i++)
			newsites[i] = sites[i];
		
		newsites[sites.length] = c;
		sites = newsites;	
	}
	
	public boolean add(Site[] c)
	{
		if (sites == null || sites.length == 0) {	// non c'erano siti, prendi tutti i nuovi
			sites = c;
			return sites != null && sites.length > 0;
		}
		else if (c != null && c.length > 0) {		// c'erano siti, aggiungi siti
			final Site[] newsites = new Site[(sites == null ? 0 : sites.length) + (c == null ? 0 : c.length)];
			int idx = 0;
			
			for (int i=0; i<sites.length; i++)
				newsites[idx++] = sites[i];
			for (int i=0; i<c.length; i++)
				newsites[idx++] = c[i];

			sites = newsites;
			return true;
		}
		else	// c'erano siti, non c'è niente da aggiungere
			return false;
			
	}
	
	public boolean add(Project p)
	{
		return p == null ? false : add(p.sites);
	}
	

	public int getNCells(boolean active_only)
	{
		int ret = 0;
		
		for (Site s : sites) {
			final List<Cell> cells = s.getCells();
			
			for (Cell c : cells)
				if (c.isActive() || !active_only)
					ret++;
		}
		
		return ret;
	}
	
	
	public Site getSiteIndex(int idx) { return sites[idx]; }
	
	public Site getSiteByID(String ID)
	{
		for (int i=0; i<sites.length; i++)
			if (sites[i].getID().equals(ID))
				return sites[i];
		
		return null;		
	}
	
	public Site getFirstSite()
	{
		return sites==null||sites.length==0 ? null : sites[0];
	}
	
	
	public boolean delSiteIndex(int i)
	{
		if (i < 0 || i >= sites.length)
			return false;
		
		final Site[] newsites = new Site[sites.length-1];
		int k=0;
		for (int j=0; j<sites.length; j++)
			if (j != i)
				newsites[k++] = sites[j];
		
		sites = newsites;
		return true;
	}
	
	public boolean updateSite(int i, Site newsite) {
		if (i < 0 || i >= sites.length)
			return false;
		
		sites[i] = newsite;
		return true;
	}
	
	public Point3d getAveragePosition()
	{
		Point3d acc = Point3d.ZERO;
		if (sites.length > 0) {
			for (Site s : sites)
				acc = Point3d.add(acc, s.getPosition());
			return Point3d.multiply(acc, 1.0d/sites.length);
		}
		return acc;
	}
	
	public void MoveSite(int from, int to)
	{
		if (from < 0 || from >= sites.length || to < 0 || to >= sites.length)
			return;
		
		final Site s1 = sites[from];
		sites[from] = sites[to];
		sites[to] = s1;
	}

	public Site[] getSites() {
		return sites;
	}

	public void setSites(Site[] sites) {
		this.sites = sites;
	}

	public String getStudio_tecnico() {
		return studio_tecnico;
	}

	public void setStudio_tecnico(String studio_tecnico) {
		this.studio_tecnico = studio_tecnico;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<String> getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(List<String> destinatari) {
		this.destinatari = destinatari;
	}
	
	public void setAllCellsActive(boolean b) {
		for (Site s : sites)
			s.setAllCellsActive(b);
	}
}
