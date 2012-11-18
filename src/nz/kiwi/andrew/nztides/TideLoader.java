package nz.kiwi.andrew.nztides;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

/**
 * Loads data from embedded csv file
 * @author andrew
 *
 */
public class TideLoader 
{
	
	/** The delimiter to read a line from the csv files */
	private static final String CSV_DELIMITER = ",";
	
	
	/**
	 * Gets a list of all the high and low tides for a port on a particular date
	 * @param p the port which to get the data for
	 * @param date the date (year, month, day) to get tide levels for
	 * @return a list of all the high and low tides for port (p) on the date (date) specified
	 * @throws PortDataNotFoundException if the port does not have any data for the year specified in date
	 */
	public static List<TideLevel> getTidesForDate(Port p, Calendar date) throws PortDataNotFoundException
	{
		List<TideLevel> levels = new ArrayList<TideLevel>();
		
		//get the port data (will throw exception if not found)
		Scanner scan = new Scanner(getPortData(p, date.get(Calendar.YEAR))); 
		
		// this will get the index of the row with the data we want
		int rowValue = date.get(Calendar.DAY_OF_YEAR) - 1;
		
		//loop through all the rows until we get to the date we want
		int currentIndex = 0;
		while(currentIndex < rowValue)
		{
			scan.nextLine();
			currentIndex++;
			
		}
		
		//the data for the specified date
		String tideData = scan.nextLine();
		scan.close(); //we are finished with it now
		
		Util.logger.info("Tide data for " + Util.date_format.format(date.getTime()) + ": "  + tideData);
		
		
		/*
		 * The format of this tide data from: http://www.linz.govt.nz/hydro/tidal-info/tide-tables/formats
		 * delimiter = ","
		 * 
		 *  # info data #
		 * [0] = day of month (1,2,3...)
		 * [1] = weekday name (Mo,Tu,We,Th,Fr,Sa,Su)
		 * [2] = month number (1 = Jan, 2 = Feb...)
		 * [3] = year (2012)
		 *  
		 *  There can be (at most) 4 different readings per day
		 *  There can be (at least) 3 different readings per day)
		 *  
		 *  # tide data #
		 *  [4,6,8,10] = 24 hour time (05:48, 16:02) of tide occurrence
		 *  [5,7,9,11] = height of tide (metres)
		 *  
		 *  Note: [10] and [11] can be null if there is not 4 tide levels for that day
		 *  
		 */
		
		//scan has been closed - so it is safe to overwrite it here
		scan = new Scanner(tideData);
		scan.useDelimiter(CSV_DELIMITER);
		
		//first double check that we have the correct dates (and step the scanner)
		if(assertDateCorrect(date, scan))
		{
			//the first tide levels we keep so we can determine if it is high or low tide later on
			String firstTime = scan.next();
			double firstTideHeight = scan.nextDouble();
			
			//the second tide we can then see if the first one was high or low
			String secondTime = scan.next();
			double secondTideHight = scan.nextDouble();
			
			//determine if the first tide was high or low
			boolean firstTideIsHigh = true;
			if(secondTideHight > firstTideHeight)
			{
				firstTideIsHigh = false;
			}
			
			//add first level
			levels.add(new TideLevel(p, firstTideHeight, createCalendar(date, firstTime), firstTideIsHigh));
			//second
			levels.add(new TideLevel(p, secondTideHight, createCalendar(date, secondTime), !firstTideIsHigh));
			
			//change this for every loop of the while
			boolean highTideSwitch = firstTideIsHigh;
			
			//for the rest, loop
			while(scan.hasNext())
			{
				String tideTime = scan.next();
				if(tideTime.length() > 0) //ensure we have a valid string 
				{
					double tideHeight = scan.nextDouble();
					
					levels.add(new TideLevel(p, tideHeight, createCalendar(date, tideTime), highTideSwitch));
					
					highTideSwitch = !highTideSwitch;
				}
			}
		}
		else
		{
			Util.logger.warning("Date [" + Util.date_format.format(date.getTime()) + "] and Tide Data[" + 
					tideData + "] do not match!");
			
			//TODO throw exception
		}
		
		scan.close();
		
		return levels;
	}
	
	/**
	 * Creates a calendar object from the params
	 * @param date the starting date
	 * @param time the time from the csv file (eg: "05:20", "16:41"...)
	 * @return a calendar object 
	 */
	private static Calendar createCalendar(Calendar date, String time)
	{
		Calendar c = new GregorianCalendar();
		c.set(Calendar.YEAR, date.get(Calendar.YEAR));
		c.set(Calendar.MONTH, date.get(Calendar.MONTH));
		c.set(Calendar.DATE, date.get(Calendar.DATE));
		
		String[] timeSplit = time.split(":");
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSplit[0]));
		c.set(Calendar.MINUTE, Integer.parseInt(timeSplit[1]));
		
		return c;
	}
	
	/**
	 * Checks the tideData to make sure it matches the date
	 * @param date the date this tideData is meant to be from
	 * @param scan the scanner with the data from the csv file
	 * @return true if the tideData date matches the calendar date
	 */
	private static boolean assertDateCorrect(Calendar date, Scanner scan)
	{
		boolean dataCorrect = false;
		
		/*
		 * # info data #
		 * [0] = day of month (1,2,3...)
		 * [1] = weekday name (Mo,Tu,We,Th,Fr,Sa,Su)
		 * [2] = month number (1 = Jan, 2 = Feb...)
		 * [3] = year (2012)
		 */
		
		int day = scan.nextInt();
		String weekdayName = scan.next(); //not needed
		int month = scan.nextInt();
		int year = scan.nextInt();
		
		/*
		int oDay = date.get(Calendar.DATE);
		int oMonth = date.get(Calendar.MONTH)+1;
		int oYear = date.get(Calendar.YEAR);
		*/
		
		dataCorrect = (day == date.get(Calendar.DATE) &&
						month == date.get(Calendar.MONTH)+1 &&
						year == date.get(Calendar.YEAR));
		
		return dataCorrect;
	}
	
	
	/** Path to embedded csv file (replace %n% with enum Port, %y% with year) */
	private static final String CSV_PATH = "data/%n%/%y%.csv";
	
	/**
	 * Loads the port resource from file
	 * @param port the port to load
	 * @return an InputStream from the port tide csv file
	 */
	private static InputStream getPortData(Port port, int year) throws PortDataNotFoundException
	{
		String path = CSV_PATH.replace("%n%", port.name().toLowerCase()).replace("%y%", ""+year);
		Util.logger.info("csv path: " + path);
		InputStream is = TideLoader.class.getResourceAsStream(path);
		
		if(is == null) //then it doesn't exist
		{
			Util.logger.warning("Port data (" + port.name() + ") was not found for year " + year);
			throw new PortDataNotFoundException(port, year);
		}
		
		return is;
	}
	
	
	
	
	
	
	
}
