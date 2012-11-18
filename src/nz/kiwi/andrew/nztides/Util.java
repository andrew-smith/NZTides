package nz.kiwi.andrew.nztides;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

public class Util
{
	/** The logger for this application */
	public static final Logger logger = Logger.getLogger("tidepredictions");
	
	/** Date format to use */
	public static final SimpleDateFormat date_format = new SimpleDateFormat("yyyy/MM/dd kk:mm");
	
	
	static
	{
		logger.setLevel(Level.WARNING);
	}
}
