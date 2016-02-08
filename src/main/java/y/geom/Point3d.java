package y.geom;

public class Point3d extends Point2d
{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point3d other = (Point3d) obj;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	public final static Point3d ZERO = new Point3d();
	
	protected double z;
	
	public Point3d()			{ super(); z = 0; }
	public Point3d(Point3d p)	{ this(p.x, p.y, p.z); }
	public Point3d(double x, double y, double z)
	{
		super(x, y);
		this.z = z;
	}
	
	public double getZ()		{ return z; }
	public void setZ(double z)	{ this.z = z; }
	
	public static Point3d add(Point3d p1, Point3d p2)		{ return new Point3d(p1.x + p2.x, p1.y + p2.y, p1.z + p2.z); }
	public static Point3d subtract(Point3d p1, Point3d p2)	{ return new Point3d(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z); }
	public static Point3d multiply(Point3d p, double k)		{ return new Point3d(p.x * k, p.y * k, p.z * k); }
	public static double dot(Point3d p, Point3d q)			{ return p.x * q.x + p.y * q.y + p.z * q.z; }
	public static Point3d inverse(Point3d p)				{ return multiply(p, -1); }
	
	public String toString() { return "Point3d("+x+", "+y+", "+z+")"; } 
	
	public double length() { return Math.sqrt(x*x + y*y + z*z); }
	
	public static double distance(Point3d p1, Point3d p2) {
		final double dx = p1.getX() - p2.getX();
		final double dy = p1.getY() - p2.getY();
		final double dz = p1.getZ() - p2.getZ();
		
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
}
