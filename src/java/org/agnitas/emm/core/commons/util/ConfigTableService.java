package org.agnitas.emm.core.commons.util;

import org.agnitas.util.AgnUtils;
import org.springframework.dao.EmptyResultDataAccessException;

public class ConfigTableService {

	private static String THUMBNAIL_CLASS = "thumbnail";
	private static String THUMBNAIL_CLASSID = "0";
	private static String THUMBNAIL_KEY = "generate";
	
	private static String PREDELIVERY_THUMBNAIL_CLASS = "predelivery";
	private static String PREDELIVERY_THUMBNAIL_CLASSID = "0";
	private static String PREDELIVERY_THUMBNAIL_SCALEX = "scalex";
	private static String PREDELIVERY_THUMBNAIL_SCALEY = "scaley";
	private static String PREDELIVERY_THUMBNAIL_SIZEX = "sizex";
	private static String PREDELIVERY_THUMBNAIL_SIZEY = "sizey";
	private static String PREDELIVERY_THUMBNAIL_TRESHOLD = "treshold";
	
	private static final String DEPRECATED_UID_CLASSNAME = "uid.deprecated";
	private static final String DEPRECATED_UID_CLASSID = "0";
	private static final String DEPRECATED_UID_KEY = "redirection.url";

	private ConfigTableDao configTableDao;
	
	// returns true, if thumbnails should be generated
	public boolean isThumbnailingAllowed() {
		boolean returnValue = false;
		try {
			String returnString = configTableDao.getEntry(THUMBNAIL_CLASS, THUMBNAIL_CLASSID, THUMBNAIL_KEY);
			if (returnString.equals(THUMBNAIL_KEY)) {
				returnValue = true;
			}
		} catch (Exception e) {
			AgnUtils.logger().error("Error in ConfigTableService while getting a value for: " + THUMBNAIL_CLASS + " " + THUMBNAIL_CLASSID + " " + THUMBNAIL_KEY);
			e.printStackTrace();
			returnValue = false;	// if an exception occurs, we dont make thumbnails.
		}
		return returnValue;
	}
	
	public String getDeprecatedUIDRedirectionUrl( long companyId) {
		
		String keyname = DEPRECATED_UID_KEY + "." + companyId;
		
		try {
			return configTableDao.getEntry(DEPRECATED_UID_CLASSNAME, DEPRECATED_UID_CLASSID, keyname);
		} catch( EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			AgnUtils.logger().error("Error in ConfigTableService while getting a value for: " + DEPRECATED_UID_CLASSNAME + " " + DEPRECATED_UID_CLASSID + " " + keyname);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method returns the x and y scales for generating a thumbnail.
	 * @return
	 */
	public float[] getXYScaleForThumbnails() {
		float[] returnValues = new float[2];
		try {
			String tmpX = configTableDao.getEntry(PREDELIVERY_THUMBNAIL_CLASS, PREDELIVERY_THUMBNAIL_CLASSID, PREDELIVERY_THUMBNAIL_SCALEX);
			if (tmpX != null) {
				returnValues[0] = Float.parseFloat(tmpX);
			}
			String tmpY = configTableDao.getEntry(PREDELIVERY_THUMBNAIL_CLASS, PREDELIVERY_THUMBNAIL_CLASSID, PREDELIVERY_THUMBNAIL_SCALEY);
			if (tmpY != null) {
				returnValues[1] = Float.parseFloat(tmpY);
			}
		} catch (Exception e) {
			AgnUtils.logger().error("An error occured getting XY Scales for generating preview thumbnails");
		}		
		return returnValues;
	}
	
	/**
	 * This method returns the x and y size for the thumbnails.
	 * @return
	 */
	public int[] getXYSizeForThumbnails() {
		int[] returnValues = new int[2];
		try {
			String tmpX = configTableDao.getEntry(PREDELIVERY_THUMBNAIL_CLASS, PREDELIVERY_THUMBNAIL_CLASSID, PREDELIVERY_THUMBNAIL_SIZEX);
			if (tmpX != null) {
				returnValues[0] = Integer.parseInt(tmpX);
			}
			String tmpY = configTableDao.getEntry(PREDELIVERY_THUMBNAIL_CLASS, PREDELIVERY_THUMBNAIL_CLASSID, PREDELIVERY_THUMBNAIL_SIZEY);
			if (tmpY != null) {
				returnValues[1] = Integer.parseInt(tmpY);
			}
		} catch (Exception e) {
			AgnUtils.logger().error("An error occured getting XY Scales for generating preview thumbnails");
		}		
		return returnValues;
	}	
	
	/**
	 * this method returns a treshold when a thumbnail should be generated. If no value set in DB
	 * the thumbnails should always be generated.
	 * @return
	 */
	public float getMaxScaleSize() {
		float returnValue = 0.0f;
		try {
			String trashold = configTableDao.getEntry(PREDELIVERY_THUMBNAIL_CLASS, PREDELIVERY_THUMBNAIL_CLASSID, PREDELIVERY_THUMBNAIL_TRESHOLD);
			returnValue = Float.parseFloat(trashold);
		} catch (EmptyResultDataAccessException e) {
			// this is thrown, if no value is given in DB.
			// leave default to 0.0f (means: "always create thumbnail") 			
		} catch (Exception e) {
			AgnUtils.logger().error("An error occured getting an entry for the thumbnail treshold");
		}
		return returnValue;
	}
	
	public void setConfigTableDao(ConfigTableDao configTableDao) {
		this.configTableDao = configTableDao;
	}	
}
