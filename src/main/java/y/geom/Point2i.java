package y.geom;

public class Point2i
{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Point2i other = (Point2i) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public final static Point2i ZERO = new Point2i();
	
	protected int x;
	protected int y;
	
	public Point2i()			{ this(0, 0); }
	public Point2i(Point2i p)	{ this(p.x, p.y); }
	
	public Point2i(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }

	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	
	public static Point2i add(Point2i p1, Point2i p2)		{ return new Point2i(p1.x + p2.x, p1.y + p2.y); }
	public static Point2i subtract(Point2i p1, Point2i p2)	{ return new Point2i(p1.x - p2.x, p1.y - p2.y); }
	public static Point2i multiply(Point2i p, double k)		{ return new Point2i((int)(p.x * k), (int) (p.y * k)); }
	public static Point2i dot(Point2i p1, Point2i p2)		{ return new Point2i(p1.x * p2.x, p1.y * p2.y); }
	public static Point2i inverse(Point2i p)				{ return multiply(p, -1); }
	
	public String toString() { return "Point2d("+x+", "+y+")"; }
	
	public double length() { return Math.sqrt(x*x + y*y); }
	
	public static double distance(Point2i p1, Point2i p2) {
		final double dx = p1.getX() - p2.getX();
		final double dy = p1.getY() - p2.getY();
		
		return Math.sqrt(dx*dx + dy*dy);
	}
}
