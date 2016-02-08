package y.em;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import y.geom.Point3d;
import y.utils.Utils;

public class Cell implements Comparable<Cell> 
{
	public static final String SEPARATOR = "^";
	
	private String PathMsi;
	private double height;
	private double quote;
	private double direction;
	private double power;
	private double gain;
	private int tilt_m;
	private int tilt_e;
	private String site_id;
	
	private boolean active;
	private double frequency;
	
	private double alpha;
	private String note;
	
	public enum Technology { GSM, UMTS, DCS, LTE, DVBH;
		public static String[] Names =  { "GSM", "UMTS", "DCS", "LTE", "DVBH" }; 
		public static Technology create(int ordinal)
		{
			return Technology.values()[ordinal];
		}

		public static Technology createSingle(String s)
		{
			for (int i=0; i<Names.length; i++)
				if (s.equals(Names[i]))
					return create(i);
			return null; // exception?
		}
		public static String getName(Technology type)
		{
			return Names[type.ordinal()];
		}
		
		public static String getName(List<Technology> type)
		{
			Collections.sort(type, new Comparator<Technology>() {
				@Override
				public int compare(Technology o1, Technology o2) {
					final int d = o1.ordinal() - o2.ordinal();
					return d>0 ? +1 : d<0 ? -1 : 0;
				}
			});
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<type.size(); i++) {
				sb.append(Technology.getName(type.get(i)));
				if (i != type.size()-1)
					sb.append('/');
			}
			
			return sb.toString();
		}
		public static List<Technology> create(String s) {
			final String[] t = s.split("/");
			List<Technology> ret = new ArrayList<Technology>();
			for (String st : t) {
				final Technology tech = Technology.createSingle(st);
				if (tech != null)
					ret.add(tech);
			}
			return ret;
		}
	};
	
	private List<Technology> technologies;
	
	private double x;
	private double y;
	
	
	public Cell()
	{
		alpha = 1;
		technologies = new ArrayList<Technology>();
		note = "";
		active = true;
		frequency = 0;
	}
	
	public String getPathMsi()			{ return PathMsi==null?"":PathMsi.toLowerCase(); }
	public double getHeight()			{ return height; }
	public double getQuote()			{ return quote; }
	public double getDirection()		{ return direction; }
	public double getPower()			{ return power; }
	public double getPowerKW()			{ return power/1000; }
	public double getGain()				{ return gain; }
	public int getTiltM()				{ return tilt_m; }
	public int getTiltE()				{ return tilt_e; }
	public int getTilt()				{ return tilt_e+tilt_m; }
	public String getSiteID()			{ return site_id; }
	public double getX()				{ return x; }
	public double getY()				{ return y; }
	public boolean isActive()			{ return active; }
	public double getFrequency()		{ return frequency != 0 ? frequency : getFrequencyFromAntennaPattern(); }
	
	public void setPathMsi(String v)	{ PathMsi = v.toLowerCase(); }
	public void setHeight(double v)		{ height = v; }
	public void setQuote(double v)		{ quote = v; }
	public void setDirection(double v)	{ direction = v; }
	public void setPower(double v)		{ power = v; }
	public void setPowerKW(double v)	{ power = v*1000; }
	public void setGain(double v)		{ gain = v; }
	public void setTiltM(int v)			{ tilt_m = v; }
	public void setTiltE(int v)			{ tilt_e = v; }
	public void setSiteID(String v)		{ site_id = v; }
	public void setX(double v)			{ x = v; }
	public void setY(double v)			{ y = v; }
	public void setActive(boolean b)	{ active = b; }
	public void setFrequency(double v)	{ frequency = v; }
	
	// usate da SiteCSVExporter
	public String getAntennaFilename()
	{
		final String path = getPathMsi();
		int lastp = path.lastIndexOf(Utils.PathSeparator);
		if (lastp < 0) lastp = path.lastIndexOf(Utils.PathSeparator);
		
		return lastp >= 0 ? path.substring(lastp+1) : path;
	}
	
	public String getAntennaBrand()
	{
		final String path = getPathMsi();
		int lastp = path.lastIndexOf(Utils.PathSeparator);
		if (lastp <= 1) lastp = path.lastIndexOf(Utils.PathSeparator);
		if (lastp <= 1) return path;
		
		String ot = path.substring(0, lastp);
		lastp = ot.lastIndexOf(Utils.PathSeparator);
		if (lastp < 1) lastp = ot.lastIndexOf(Utils.PathSeparator);
		return lastp >= 0 ? ot.substring(lastp+1) : ot;
	}
	
	// usate da SiteCSVExporter
	private int getFrequencyFromAntennaPattern()
	{
		final String antenna_filename = getAntennaFilename();
		int nearest = 0;
		int near_diff = 9999;
		
		final int[] freqs = { 800, 900, 1800, 2100, 2600 };
		
		final int[] nums = Utils.getAllIntsInString(antenna_filename);
		
		for (int cur : nums)
			for (int f : freqs) {
				int cdiff = Math.abs(f-cur);
				if (cdiff < near_diff) {
					near_diff = cdiff;
					nearest = f;
				}
			}
		
		return nearest;
	}
	
	// usate da SiteCSVExporter
	public String getTecnologia()
	{
		// freqs 450 and 460 are exceptions, check them first
		if (frequency == 450)
			return "Link Monocanale";
		else if (frequency == 460)
			return "Rete di Diffusione";
		
		// otherwise, get it from technologies(db)
		if (!technologies.isEmpty())
			return Technology.getName(technologies);
		
		// if not present (prj file?) guess from antenna pattern
		final int freq = getFrequencyFromAntennaPattern();
		
		switch (freq) {
			case 800 : return "LTE";
			case 900 : return "GSM/UMTS";
			case 1800 : return "DCS/LTE";
			case 2100 : return "UMTS";
			case 2600 : return "LTE";
			default : return "";
		}
	}
	
	public boolean checkTiltFromAntennaFilename()
	{
		return checkTiltAgainstAntennaFilename(getAntennaFilename(), tilt_e);
	}
	
	public static boolean checkTiltAgainstAntennaFilename(String antenna_filename, int tilt_e)
	{
		final int[] nums = Utils.getAllIntsInString(antenna_filename);
		
		if (nums.length == 0)	// se non ci sono numeri, lasciamo perdere
			return true;
		
		boolean wasthereanything = false;
		
		for (int i=nums.length-1; i>=0; i--) {
			if (nums[i] == tilt_e)
				return true;
		
			if (nums[i] >= 0 && nums[i] <= 16)
				wasthereanything = true;
		}
		
		return !wasthereanything;
		
//		for (int i=nums.length-1; i>=0; i--)
//			if (nums[i] == 45)	// "p45", "m45" : ignora e prendi il numero prima
//				continue;
//			else {
//				if (nums[i] == tilt_e)
//					return true;						// sicuramente è giusto
//				else {
//					if (nums[i] >=0 && nums[i] <= 16)	// sembra un tilt, ed è diverso. sembra sbagliato
//						return false;
//					else								// chissà che numero è, lasciamo stare.
//						return true;
//				}
//			}
//		
//		return true;
	}
	
	
	public String toString()
	{
		return PathMsi + SEPARATOR + height + SEPARATOR + quote + SEPARATOR + direction + SEPARATOR + power + SEPARATOR + gain + SEPARATOR + 
				tilt_m + SEPARATOR + tilt_e + SEPARATOR + site_id + SEPARATOR + alpha + SEPARATOR + x + SEPARATOR + y;
	}
	
	public static Cell parseCellFrom(String s)
	{
		final String[] parts = s.split("\\"+SEPARATOR, -1); // regexp: "\^"
		int argi = 0;
		final Cell c = new Cell();
		c.PathMsi = parts[argi++];
		c.height = Double.parseDouble(parts[argi++]);
		c.quote = Double.parseDouble(parts[argi++]);
		c.direction = Double.parseDouble(parts[argi++]);
		c.power = Double.parseDouble(parts[argi++]);
		c.gain = Double.parseDouble(parts[argi++]);
		c.tilt_m = Integer.parseInt(parts[argi++]);
		c.tilt_e = Integer.parseInt(parts[argi++]);
		c.site_id = parts[argi++];
		c.alpha = Double.parseDouble(parts[argi++]);
		c.x = Double.parseDouble(parts[argi++]);
		c.y = Double.parseDouble(parts[argi++]);
		
		return c;
	}
	
	public Point3d getPosition()
	{
		return new Point3d(x, y, quote);
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public List<Technology> getTechnologies() {
		return technologies;
	}

	public void setTechnologies(List<Technology> technologies) {
		this.technologies = technologies;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	@Override
	public int compareTo(Cell arg0) {
		final double f1 = this.getFrequency();
		final double f2 = arg0.getFrequency();
		
		if (f1 < f2)
			return -1;
		else if (f1 > f2)
			return +1;
		
		// technology sort, 900 mhz: GSM < GSM/UMTS < UMTS  ...< everything else
		// technology sort, 1800 mhz: DCS < DCS/LTE < LTE ...< everything else
		final List<Technology> t1 = this.getTechnologies();
		final List<Technology> t2 = arg0.getTechnologies();
		if (!t1.isEmpty() && !t2.isEmpty() && (f1 == 900 || f1 == 1800)) {
			final boolean[] p1 = getTechnologyArray(t1, f1);
			final boolean[] p2 = getTechnologyArray(t2, f2);
			
			if (p1[0] == p2[0] && p1[1] == p2[1])		// 4 cases
				; //bugfix 1.26 //return 0;
			else if (p1[0] == false && p1[1] == false)	// 3 cases
				return +1;
			else if (p2[0] == false && p2[1] == false)	// 3 cases
				return -1;
			else if ((p2[0] == false && p2[1] == true) || (p2[0] == true && p2[1] == true && p1[0] == true)) // 3 cases
				return -1;
			else //
				return +1;
		}
		
		final double d1 = this.getDirection();
		final double d2 = arg0.getDirection();
		
		if (d1 < d2)
			return -1;
		else if (d1 > d2)
			return +1;
		else
			return 0;
	}
	
	private static boolean[] getTechnologyArray(List<Technology> t1, double freq) {
		boolean[] p1 = { false, false };
		for (Technology a : t1)
			if ((a == Technology.GSM && freq == 900) || (a == Technology.DCS && freq == 1800))
				p1[0] = true;
			else if ((a == Technology.UMTS && freq == 900) || (a == Technology.LTE && freq == 1800))
				p1[1] = true;
		return p1;
	}
	
	public String getName() {
		return site_id + "-" + getTecnologia() + "-" + Utils.formatDoubleAsNeeded(direction);
	}
}
