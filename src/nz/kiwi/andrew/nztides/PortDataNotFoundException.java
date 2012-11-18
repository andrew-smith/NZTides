package nz.kiwi.andrew.nztides;


public class PortDataNotFoundException extends Exception 
{
	/**	SID */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception if a port was not found
	 * @param p the port that was not found
	 * @param year the year that could not be found
	 */
	public PortDataNotFoundException(Port p, int year)
	{
		super("The port: " + p.toString() + " (Port." + p.name() + ") data was not found for the year " + year);
	}
	
}
