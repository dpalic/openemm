package org.agnitas.emm.core.commons.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.agnitas.emm.core.commons.annotations.dev.RelatedIssues;
import org.agnitas.emm.core.velocity.VelocityCheck;
import org.agnitas.util.AgnUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * ConfigurationService for EMM
 * This class uses buffering of the values of the config_tbl for better performance.
 * The value for refreshing period is also stored in config_tbl and can be changed
 * manually with no need for restarting the server.
 * For refreshing the values the very next time the old period value will be used.
 * Afterwards the new one will take effect.
 * The value 0 means, there will be no buffering.
 * 
 * @author aso
 *
 */
public class ConfigService {
	
	/** The logger. */
	private static final transient Logger logger = Logger.getLogger(ConfigService.class);

	/** DAO for access config table. */
	protected ConfigTableDao configTableDao;
		
	public static enum Value {
		DB_Vendor("DB_Vendor"),
		ConfigurationExpirationMinutes("configuration.expiration.minutes"),
		Test("Test.Test"),
		Password_Plaintext_Allowed("password.plaintext.allowed"),
		Pickup_Rdir("pickup.rdir"),
		
		System_Licence("system.licence"),
		
		@Deprecated
		@RelatedIssues({"AGNEMM-2509", "AGNEMM-1535"})
		System_Master("system.master"),
		
		@Deprecated
		@RelatedIssues({"AGNEMM-2509", "AGNEMM-1535"})
		System_Master_Hash("system.master-hash"),
		
		Linkchecker_Linktimeout("linkchecker.linktimeout"),
		Linkchecker_Threadcount("linkchecker.threadcount"),
		
		Predelivery_Litmusapikey("predelivery.litmusapikey"),
		Predelivery_Litmusapiurl("predelivery.litmusapiurl"),
		Predelivery_Litmuspassword("predelivery.litmuspassword"),
		
		Uid_Generation_Default_Version("uid.generation.default.version"),
		Uid_Deprecated_Redirection_Url("uid.deprecated.redirection.url"),

		Thumbnail_Generate("thumbnail.generate"),
		Thumbnail_Scalex("thumbnail.scalex"),
		Thumbnail_Scaley("thumbnail.scaley"),
		Thumbnail_Sizex("thumbnail.sizex"),
		Thumbnail_Sizey("thumbnail.sizey"),
		Thumbnail_Treshold("thumbnail.treshold"),
		
		VelocityRuntimeCheck("velocity.runtimecheck"),
		VelocityScriptAbort("velocity.abortscripts"),
		
		RdirLandingpage("system.RdirLandingpage"),
		
		ActionOperationsEnableStoreOld("actionoperations.enable_store_old"),
		
		SupportEmergencyUrl("system.support_emergency_url"),
		
		/** Time (in minutes) for mailing generation. */
		MailGenerationTimeMinutes("mailing.generation.minutes"),
		
		/** Config value for configuration of host authentication. */
		HostAuthentication("host_authentication.authentication");
		
		private Value(String name) {
			this.name = name;
		}
		private final String name;
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	public static enum CompanyConfigValue {
		COMPANY_PROPERTY_DEFAULT_LINK_EXTENSION("DefaultLinkExtension");
		
		private CompanyConfigValue(String name) {
			this.name = name;
		}
		private final String name;
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private static Map<String, String> configurationValues = null;
	private static Calendar expirationTime = null;
	
	// ----------------------------------------------------------------------------------------------------------------
	// Dependency Injection

	public void setConfigTableDao(ConfigTableDao configTableDao) {
		this.configTableDao = configTableDao;
	}
	
	// ----------------------------------------------------------------------------------------------------------------
	// Business Logic

	private void refreshValues() {
		try {
			if (expirationTime == null || GregorianCalendar.getInstance().after(expirationTime)) {
				Map<String, String> newValues = configTableDao.getAllEntries();
				
				int minutes = 0;
				if (newValues.containsKey(Value.ConfigurationExpirationMinutes.toString()))
					minutes = Integer.parseInt(newValues.get(Value.ConfigurationExpirationMinutes.toString()));

				if (minutes > 0) {
					GregorianCalendar nextExpirationTime = new GregorianCalendar();
					nextExpirationTime.add(GregorianCalendar.MINUTE, minutes);
					expirationTime = nextExpirationTime;
				} else {
					expirationTime = null;
				}
				
				configurationValues = newValues;
			}
		} catch (Exception e) {
			logger.error("Cannot refresh config data from database", e);
		}
	}
	
	public String getValue(Value configurationValueID) {
		refreshValues();
		return configurationValues.get(configurationValueID.toString());
	}
	
	public String getValue(Value configurationValueID, String defaultValue) {
		String returnValue = getValue(configurationValueID); 
		if (returnValue == null || returnValue.length() == 0) {
			return defaultValue;
		} else {
			return returnValue;
		}
	}
	
	public String getValue(Value configurationValueID, @VelocityCheck int companyID) {
		refreshValues();
		return configurationValues.get(configurationValueID.toString() + "." + companyID);
	}
	
	/**
	 * Does a chained search for specified configuration. Search stops at first match:
	 * 
	 * <ol>
	 *   <li>Search configuration value for given company ID.</li>
	 *   <li>Search configuration value for company ID 0.</li>
	 *   <li>Search configuration value without company ID</li>
	 * </ol>
	 * 
	 * If there is no matching configuration, {@code null} is returned.
	 * 
	 * @param configurationValueID configuration value ID
	 * @param companyID company ID

	 * @return configuration value as String or {@code null}
	 */
	public String getValueWithFallback(Value configurationValueID, @VelocityCheck int companyID) {
		String value = getValue( configurationValueID, companyID);
		
		if( value == null) {
			value = getValue( configurationValueID, 0);
		}

		if( value == null) {
			value = getValue( configurationValueID);
		}
		
		return value;
	}
	
	/**
	 * Same as {@link #getValueWithFallback(Value, int)} with additional default value.
	 * 
	 * If no matching configuration is found, the specified default value is returned.
	 * 
	 * @param configurationValueID configuration value ID
	 * @param companyID company ID
	 * @param defaultValue default value
	 * 
	 * @return configuration value or specified default value
	 */
	public String getValueWithFallback( Value configurationValueID, @VelocityCheck int companyID, String defaultValue) {
		String value = getValueWithFallback(configurationValueID, companyID);
		
		if( value == null)
			return defaultValue;
		else
			return value;
	}
	
	public boolean getBooleanValue(Value configurationValueID) {
		String value = getValue(configurationValueID);
		
		return AgnUtils.interpretAsBoolean( value);
	}
	
	public boolean getBooleanValue(Value configurationValueID, @VelocityCheck int companyID) {
		String value = getValue(configurationValueID, companyID);
		
		return AgnUtils.interpretAsBoolean( value);
	}
	
	/**
	 * Returns a boolean value using logic with fallback and default value.
	 * See {@link #getValueWithFallback(Value, int, String)} for details on fallback.
	 * 
	 * @param configurationValueID ID of configuration value
	 * @param companyID company ID
	 * @param defaultValue default value
	 * 
	 * @return boolean configuration value of default value
	 */
	public boolean getBooleanValueWithFallback( Value configurationValueID, @VelocityCheck int companyID, boolean defaultValue) {
		String value = getValueWithFallback(configurationValueID, companyID, Boolean.toString( defaultValue));
		
		return AgnUtils.interpretAsBoolean( value);
	}
	
	/**
	 * Returns an integer value using logic with fallback and default value.
	 * See {@link #getValueWithFallback(Value, int, String)} for details on fallback.
	 * 
	 * @param configurationValueID ID of configuration value
	 * @param companyID company ID
	 * @param defaultValue default value
	 * 
	 * @return integer configuration value of default value
	 */
	public int getIntegerValueWithFallback(Value configurationValueID, @VelocityCheck int companyID, int defaultValue) {
		String value = getValueWithFallback(configurationValueID, companyID, Integer.toString(defaultValue));
		
		return Integer.parseInt(value);
	}
	
	public int getIntegerValue(Value configurationValueID) {
		String value = getValue(configurationValueID);
		if (StringUtils.isNotEmpty(value))
			return Integer.parseInt(value);
		else
			return 0;
	}

	public float getFloatValue(Value configurationValueID) {
		String value = getValue(configurationValueID);
		if (StringUtils.isNotEmpty(value))
			return Float.parseFloat(value);
		else
			return 0;
	}
	
	public List<String> getListValue(Value configurationValueID) {
		String value = getValue(configurationValueID);
		if (StringUtils.isNotEmpty(value))
			return Arrays.asList(value.split(";"));
		else
			return Collections.emptyList();
	}
	
	/**
	 * Checks, if runtime checks for Velocity are enabled. If no settings for
	 * given company ID are found, checks for company ID 0. If no settings for
	 * company ID 0 are found, runtime checks are enabled globally.
	 *  
	 * @param companyId company ID to check
	 * 
	 * @return true, if runtime checks are enabled
	 */
	public boolean isVelocityRuntimeCheckEnabled( int companyId) {
		String value = getValue( Value.VelocityRuntimeCheck, companyId);
		
		if( value != null) {
			return AgnUtils.interpretAsBoolean( value);
		} else {
			if( companyId != 0) {
				value = getValue( Value.VelocityRuntimeCheck);
				
				if( value != null) {
					return AgnUtils.interpretAsBoolean( value);
				} else {
					return true;
				}
			} else {
				return true;
			}
		}
	}

	/**
	 * Checks, if invalid Velocity scripts are to be aborted. If no settings for
	 * given company ID are found, checks for company ID 0. If no settings for
	 * company ID 0 are found, abortion of scripts is globally enabled.
	 * 
	 * @param companyId company ID to check
	 * 
	 * @return true if scripts are to be aborted
	 */
	public boolean isVelocityScriptAbortEnabled(int companyId) {
		String value = getValue( Value.VelocityScriptAbort, companyId);
		
		if( value != null) {
			return AgnUtils.interpretAsBoolean( value);
		} else {
			if( companyId != 0) {
				value = getValue( Value.VelocityScriptAbort);
				
				if( value != null) {
					return AgnUtils.interpretAsBoolean( value);
				} else {
					return true;
				}
			} else {
				return true;
			}
		}
	}
	
	/**
	 * Returns the time (in minutes) of mail generation before delivery.
	 *  
	 * @param companyID company ID
	 * @param defaultValue default value (in minutes)
	 * 
	 * @return time (in minutes) of mail generation before delivery
	 */
	public int getMailGenerationMinutes(int companyID, int defaultValue) {
		return getIntegerValueWithFallback(Value.MailGenerationTimeMinutes, companyID, defaultValue);
	}
}
