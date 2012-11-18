package nz.kiwi.andrew.nztides;

import java.util.Calendar;
import java.util.List;



/**
 * Defines a single tide level.
 * Included is the port this tide is from, the height in metres,
 * the date/time.
 * 
 * This can be either a high tide or a low tide.
 * 
 * 
 * @author andrew
 *
 */
public class TideLevel implements Comparable<TideLevel>
{
	
	/** The port this tide is from */
	private final Port port;
	
	/** True if high tide, false if low tide */
	private final boolean isHighTide;
	
	/** The height (in metres) of the tide */
	private final double height;
	
	/** The date and time which this tide occurs */
	private final Calendar date;
	
	/**
	 * Creates a TideLevel for one high or low tide
	 * @param port The port this tide is from
	 * @param height the height (in metres) 
	 * @param date the date and time this measurement occurs
	 * @param isHighTide true if this is a high tide, false if it is low tide
	 */
	public TideLevel(Port port, double height, Calendar date, boolean isHighTide)
	{
		this.port = port;
		this.height = height;
		this.date = date;
		this.isHighTide = isHighTide;
	}

	
	
	/**
	 * Gets if this is a high tide reading
	 * @return true if it is high tide, false if it is low tide
	 */
	public boolean isHighTide()
	{
		return this.isHighTide;
	}
	
	/**
	 * Gets if this is a low tide reading
	 * @return true if this is low tide, false if it is high tide
	 */
	public boolean isLowTide()
	{
		return !isHighTide();
	}
	
	
	/**
	 * Gets the port this tide level belongs to
	 * @return the port this tide level belongs to
	 */
	public Port getPort()
	{
		return this.port;
	}
	
	
	/**
	 * Gets the next high or low tide that comes after this
	 * @return the next high or low tide that comes after this
	 * @throws PortDataNotFoundException if the port data was not found
	 */
	public TideLevel getNext() throws PortDataNotFoundException
	{
		TideLevel nextTide = null;
		
		//first get all the tides from this date
		List<TideLevel> levels = TideLoader.getTidesForDate(this.port, this.date);
		
		//try and see if next tide is on the same day
		for(int i=0; i<levels.size()-1 && nextTide == null; i++)
		{
			if(this.compareTo(levels.get(i)) == 0) // 0 = this tide
			{
				//then the next one must be the next tide from this one
				nextTide = levels.get(i+1);
			}
		}
		
		//if it is still null then we need to go to tomorrow
		if(nextTide == null)
		{
			//create a new calendar object
			Calendar tomorrow = (Calendar)this.date.clone();
			tomorrow.add(Calendar.DATE, 1);
			levels = TideLoader.getTidesForDate(this.port, tomorrow);
			
			//the first one is the earliest one - so must be next
			nextTide = levels.get(0);
		}
		
		return nextTide;
	}
	
	/**
	 * Gets the previous high or low tide that comes after this
	 * @return the previous high or low tide that comes after this
	 * @throws PortDataNotFoundException if the port data was not found
	 */
	public TideLevel getPrevious() throws PortDataNotFoundException
	{
		TideLevel prevTide = null;
		
		//first get all the tides from this date
		List<TideLevel> levels = TideLoader.getTidesForDate(this.port, this.date);
		
		//try and see if next tide is on the same day
		for(int i=levels.size()-1; i > 0 && prevTide == null; i--)
		{
			if(this.compareTo(levels.get(i)) == 0) // 0 = this tide
			{
				//then the previous one must be the next tide from this one
				prevTide = levels.get(i-1);
			}
		}
		
		//if it is still null then we need to go to yesterday
		if(prevTide == null)
		{
			//create a new calendar object
			Calendar yesterday = (Calendar)this.date.clone();
			yesterday.add(Calendar.DATE, -1);
			levels = TideLoader.getTidesForDate(this.port, yesterday);
			
			//the first one is the earliest one - so must be next
			prevTide = levels.get(levels.size()-1);
		}
		
		
		return prevTide;
	}
	
	
	@Override
	public String toString()
	{
		String s = "TideLevel for " + port + " @ " + Util.date_format.format(this.date.getTime());
		
		s += " H:" + this.height + " isHighTide:" + isHighTide();
		
		return s;
	}



	@Override
	public int compareTo(TideLevel tideLevel) 
	{
		Calendar d1 = this.date;
		Calendar d2 = tideLevel.date;
		
		boolean sameYear = d1.get(Calendar.YEAR) == d2.get(Calendar.YEAR);
		boolean sameDate = d1.get(Calendar.DAY_OF_YEAR) == d2.get(Calendar.DAY_OF_YEAR);
		boolean sameHour = d1.get(Calendar.HOUR_OF_DAY) == d2.get(Calendar.HOUR_OF_DAY);
		boolean sameMinute = d1.get(Calendar.MINUTE) == d2.get(Calendar.MINUTE);
		
		if(sameYear && sameDate && sameHour && sameMinute)
		{
			return 0;
		}
		else //we are on different days, so just return standard date compare
			return this.date.compareTo(tideLevel.date);
	}
}
