package y.geom;

public class Point2d
{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point2d other = (Point2d) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	public final static Point2d ZERO = new Point2d();
	
	protected double x;
	protected double y;
	
	public Point2d()			{ this(0, 0); }
	public Point2d(Point2d p)	{ this(p.x, p.y); }
	
	public Point2d(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double getX() { return x; }
	public double getY() { return y; }

	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }
	
	public static Point2d add(Point2d p1, Point2d p2)		{ return new Point2d(p1.x + p2.x, p1.y + p2.y); }
	public static Point2d subtract(Point2d p1, Point2d p2)	{ return new Point2d(p1.x - p2.x, p1.y - p2.y); }
	public static Point2d multiply(Point2d p, double k)		{ return new Point2d(p.x * k, p.y * k); }
	public static Point2d dot(Point2d p1, Point2d p2)		{ return new Point2d(p1.x * p2.x, p1.y * p2.y); }
	public static Point2d inverse(Point2d p)				{ return multiply(p, -1); }
	
	public String toString() { return "Point2d("+x+", "+y+")"; }
	
	public double length() { return Math.sqrt(x*x + y*y); }
	
	public static double distance(Point2d p1, Point2d p2) {
		final double dx = p1.getX() - p2.getX();
		final double dy = p1.getY() - p2.getY();
		
		return Math.sqrt(dx*dx + dy*dy);
	}
}
