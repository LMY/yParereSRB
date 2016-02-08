package y.em;

public class Antenna
{
	private AntennaPattern planeV;
	private AntennaPattern planeH;

	private String name;
	private double frequency;
	private double gain;
	private String tilt;
	private String comment;

	public Antenna()
	{
		name = "test";
		frequency = 900.0;
		gain = 15.0;
		tilt = "";
		comment = "test antenna";

		planeV = planeH = AntennaPattern.INVALID;
	}

	public String getName()
	{
		return name;
	}

	public double getFrequency()
	{
		return frequency;
	}

	public double getGain()
	{
		return gain;
	}

	public String getTilt()
	{
		return tilt;
	}

	public String getComment()
	{
		return comment;
	}

	public void setPlaneV(AntennaPattern planeV) {
		this.planeV = planeV;
	}

	public void setPlaneH(AntennaPattern planeH) {
		this.planeH = planeH;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}

	public void setTilt(String tilt) {
		this.tilt = tilt;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public AntennaPattern getPlaneV() {
		return planeV;
	}

	public AntennaPattern getPlaneH() {
		return planeH;
	}
	
	public double getVerticalMinAttenuation() { return planeV.getMinAttenuation(); }
	public double getVerticalMaxPowerAngle() { return planeV.getMaxPowerAngle(); }
//	public double getVerticalFrontToBackAttenuation() { return planeV.getFrontToBackAttenuation(); }
	public double getVerticalMinHalfPowerAngle() { return planeV.getMinHalfPowerAngle(); }
	public double getVerticalMaxHalfPowerAngle() { return planeV.getMaxHalfPowerAngle(); }
	public double getVerticalHalfPowerAngle() { return planeV.getHalfPowerAngle(); }
	
	public double getHorizontalMinAttenuation() { return planeH.getMinAttenuation(); }
	public double getHorizontalMaxPowerAngle() { return planeH.getMaxPowerAngle(); }
	public double getHorizontalFrontToBackAttenuation() { return planeH.getFrontToBackAttenuation(); }
	public double getHorizontalMinHalfPowerAngle() { return planeH.getMinHalfPowerAngle(); }
	public double getHorizontalMaxHalfPowerAngle() { return planeH.getMaxHalfPowerAngle(); }
	public double getHorizontalHalfPowerAngle() { return planeH.getHalfPowerAngle(); }	
}
