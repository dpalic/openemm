package org.agnitas.emm.core.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.agnitas.emm.core.commons.util.Constants.*;

/**
 * Use this class to handle standard formats 
 * 
 *
 */
public class DateUtil {

	
	/**
	 * @param date
	 * @return the date formatted with the Constants.DATE_PATTERN_FULL  
	 */
	public static String formatDateFull(Date date ) {
		SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN_FULL);
		return format.format(date);
	}
	
	/**
	 * @param dateAsString - date which matches the Constants.DATE_PATTERN_FULL
	 * @return 
	 * @throws ParseException
	 */
	
	public static Date parseFullDate(String dateAsString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN_FULL);
		Date date;
		date = format.parse(dateAsString);
		return date;
	}
}
