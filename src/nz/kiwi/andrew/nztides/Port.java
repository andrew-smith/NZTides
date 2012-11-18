package nz.kiwi.andrew.nztides;

/**
 * Defines all the ports we have data for
 * @author andrew
 *
 */
public enum Port 
{
	Auckland("36.51'S", "174.46'E"),
	Bluff(null, null), 
	Dunedin(null, null), 
	Gisborne(null, null), 
	Lyttelton(null, null), 
	Marsden_Point(null, null), 
	Napier(null, null), 
	Nelson(null, null), 
	Onehunga(null, null), 
	Picton(null, null), 
	Port_Chalmers(null, null), 
	Taranaki(null, null), 
	Tauranga(null, null), 
	Timaru(null, null), 
	Wellington(null, null),
	Westport(null, null);
	
	/** Latitude and Longitude */
	private String sLat, sLong;
	
	private Port(String sLat, String sLong)
	{
		this.sLat = sLat;
		this.sLong = sLong;
	}
	
	
	@Override
	public String toString()
	{
		//replace all underscores with a space so it looks proper
		return super.toString().replaceAll("_", " ");
	}
}
