package y.em;

import java.util.ArrayList;
import java.util.List;

import y.geom.Point3d;


public class Site
{
	public static final String SEPARATOR1 = ";";
	public static final String SEPARATOR_CELLS = "#";
	
	private ArrayList<Cell> cells;
	private String ID;
	private String comment;

	private SiteDBInfo dbinfo;
	
	public Site(String id)
	{
		cells = new ArrayList<Cell>();
		dbinfo = new SiteDBInfo();

		this.ID = id;
		this.comment = "";
	}
	
	public String getID()				{ return ID; }
	public String getComment()			{ return comment; }
	public void setID(String v)			{ ID = v; }
	public void setComment(String s)	{ comment = s; }
	
	public List<Cell> getCells()		{ return cells; }
	

	public void AddCell(Cell newcell)
	{
		cells.add(newcell);
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(ID);
		sb.append(SEPARATOR1);
		sb.append(comment);
		sb.append(SEPARATOR1);
		sb.append(dbinfo.toString());
		sb.append(SEPARATOR1);
		for (int i=0, imax=cells.size(); i<imax; i++) {
			if (i > 0)
				sb.append(SEPARATOR_CELLS);
			sb.append(cells.get(i).toString());
		}
		
		return sb.toString();
	}
	
	public static Site parseFrom(String s)
	{
		final String[] parts = s.split(SEPARATOR1);
		
		Site site = new Site(parts[0]);
		site.comment = parts[1];
		site.dbinfo = SiteDBInfo.parseFrom(parts[2]);
		
		final String[] cells_parts = parts[3].split(SEPARATOR_CELLS);
		for (String sc : cells_parts)
			site.cells.add(Cell.parseCellFrom(sc));
		
		return site;
	}

	public void changeID(String newid)
	{
		this.ID = newid;
		for (Cell c : cells)
			c.setSiteID(newid);		
	}
	
	
	// position
	public Point3d getPosition() {
		if (dbinfo != null) {
			final Point3d pos = dbinfo.getPosition();
			if (!pos.equals(Point3d.ZERO))
				return pos;
		}
		
		return getAveragePosition();
	}

	public void setPosition(Point3d position) {
		dbinfo.setPosition(position);
	}
	
	
	private Point3d getAveragePosition()
	{
		final int n = cells.size();
		
		Point3d p = Point3d.ZERO;
		if (n == 0)
			return p;
		
		for (Cell c : cells)
			p = Point3d.add(p, c.getPosition());
		
		return Point3d.multiply(p, 1.0/n);
	}

	public SiteDBInfo getDbinfo() {
		return dbinfo;
	}

	public void setDbinfo(SiteDBInfo dbinfo) {
		this.dbinfo = dbinfo;
	}
	
	public void setAllCellsActive(boolean b) {
		for (Cell c : cells)
			c.setActive(b);
	}
}