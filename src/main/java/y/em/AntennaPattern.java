package y.em;

public class AntennaPattern
{
	public static final AntennaPattern INVALID = new AntennaPattern(new double[]{ Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY });
	
	public class AntennaPatternValue
	{
		public double angle0;
		public double angle1;
		
		public double value0;
		public double value1;
		
		public double delta0;
		public double delta1;
		
		public double getRoundValue() { return delta0<=delta1 ? value0 : value1; }
		public double getRoundAngle() { return delta0<=delta1 ? angle0 : angle1; }
	}
	
	private double detail;
	private double invdetail; // cached, per evitare divisioni in getResult()
	private double[] values;
	
	public AntennaPattern(double[] values)
	{
		this.values = values;
		detail = 360.0/this.values.length;
		invdetail = 1/detail;
	}
	
	public double getDetail() { return detail; }
	public double[] getValues() {
		return values;
	}
	
	/**
	 * get the attenuation values for angle a
	 * @param a NORMALIZED angle, [0; 2pi[
	 * @return the results
	 */
	public AntennaPatternValue getResult(double a)
	{
		AntennaPatternValue res = new AntennaPatternValue();
		
		int idx = (int)(a * invdetail);
		
		if (idx >= values.length) {		// while? ma non dovrebbe essere necessario, a è normalizzato 
			idx -= values.length;		// bugfix 1.17 141003
			a -= 360;					// bugfix 1.19 141027
		}

		res.value0 = values[idx];
		res.angle0 = detail*idx;
		res.delta0 = (a-res.angle0) * invdetail;// BUG 1.20, se detail!=1, delta0+delta1 != 1
		res.delta1 = (1-res.delta0);

		if (++idx < values.length) {
			res.value1 = values[idx];
			res.angle1 = res.angle0+detail;
		}
		else {
			res.value1 = values[0];
			res.angle1 = 0;
		}
		
		return res;
	}

	private int getMaxPowerAngleIndex()
	{
		if (values.length <= 0)
			return 0;
		
		int maxi = 0;
		for (int i=1; i<values.length; i++)
			if (values[i] > values[maxi])	// min attenuation (negative values)
				maxi = i;
		
		return maxi;
	}
	
	
	public double getMinAttenuation()
	{
		return values.length > 0 ? values[getMaxPowerAngleIndex()] : 0;
	}
	
	/**
	 * @return angle of max emission (min attenuation)
	 */
	public double getMaxPowerAngle()
	{
		return getMaxPowerAngleIndex()*detail;
	}

	/**
	 * @return positive difference, in dB, between angle of max emission (min attenuation) and opposite
	 */
	public double getFrontToBackAttenuation()
	{
		final int maxi = getMaxPowerAngleIndex();
		final double min_attenuation = values[maxi];
		final double opposite_min_attenuation = values[(maxi + values.length/2) % values.length];
		return opposite_min_attenuation - min_attenuation;
	}
	
	final static double deltadB = Math.log10(1024); // 2^10 ~= 3dB

	public double getMinHalfPowerAngle()
	{
		final int maxi = getMaxPowerAngleIndex();
		
		for (int i=1; i<values.length-1; i++)
			if ((values[maxi]-values[i] - deltadB)*(values[maxi]-values[i-1] - deltadB) <= 0)
				return (values[i]<values[i-1] ? i : i-1) * detail;
		
		return 0;
	}
	
	public double getMaxHalfPowerAngle()
	{
		final int maxi = getMaxPowerAngleIndex();
		
		for (int i=values.length-1; i >= 1; i--)
			if ((values[maxi]-values[i] - deltadB)*(values[maxi]-values[i-1] - deltadB) <= 0)
				return (values[i]<values[i-1] ? i : i-1) * detail;
		
		return 0;
	}
	
	public double getHalfPowerAngle()
	{
		final double a1 = getMinHalfPowerAngle();
		final double a2 = getMaxHalfPowerAngle();
		
//		return (a1 > 180 ? 360-a1 : a1) > (a2 > 180 ? 360-a2 : a2) ? a1 : a2;
		return Math.max((a1 > 180 ? 360-a1 : a1), (a2 > 180 ? 360-a2 : a2));
	}
}
