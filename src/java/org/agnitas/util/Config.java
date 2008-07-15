/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/
package	org.agnitas.util;

import	java.io.File;
import	java.io.FileInputStream;
import	java.io.IOException;
import	java.util.Properties;
import	java.util.Enumeration;

/**
 * general class to read and check configuration files
 * 
 * Usage:
 * - import this module
 * - write a new class which extends Config
 * - in the constructor of your class call:
 *   super (property, defaultFilename)
 *   initialize your own stuff
 *   call validation (String[] variables) with a list of all valid
 *        variables in the configuration file
 * - implement the two abstract methods:
 *   validate (int vindex, String value);
 *   missing (int vindex);
 *   where vindex is the index into the variables array. In these functions
 *         you can check if a missing variable is an error or a given value
 *         is allow. If an error occurs, throw ConfigException
 * 
 * There are some utility routines available:
 * - mkInt (String value);
 *   converts the string into an integer
 * ... more to follow as required ...
 */
public abstract class Config extends Properties {
    /** 
     * the name of the configuration file 
     */
    protected String	filename;

    /**
     * creates a default property out of the class name
     *
     * @return the property as string
     */
    private String mkproperty () {
        String	prop;
        int	pos;
        
        prop = this.getClass ().getName ();
        if ((pos = prop.lastIndexOf ('.')) > 0) {
            prop = prop.substring (0, pos);
        }
        return prop + ".config";
    }
    
    /**
     * Check for existance of file
     *
     * @param fname filename to check
     * @return true if file exists, false otherwise
     */
    private boolean fileExists (String fname) {
        boolean	exists = false;
        
        try {
            File	f = new File (fname);
            
            exists = f.exists ();
        } catch (Exception e) {
            ;
        }
        return exists;
    }
    
    /** 
     * Check if a path is a directory
     *
     * @param path the path name
     * @return true if it is a directory, false otherwise
     */
    private boolean isDirectory (String path) {
        boolean	isdir = false;
        
        try {
            File	f = new File (path);
            
            isdir = (f.exists () && f.isDirectory ());
        } catch (Exception e) {
            ;
        }
        return isdir;
    }

    public abstract String getConfigProperty ();
    public abstract String getConfigFilename ();
    public abstract String[] getConfigVariables ();

    /**
     * Constructor for the class
     *
     * @param property the property to look up the filename
     * @param defaultFilename the default, if no filename is found
     * @throws org.agnitas.util.ConfigException 
     */
    public Config () throws ConfigException {
        super ();
	String property = getConfigProperty ();
	String defaultFilename = getConfigFilename ();
        if (property == null) {
            property = mkproperty ();
        }
        filename = System.getProperty (property, defaultFilename);
        if (! fileExists (filename)) {
            if (fileExists (defaultFilename)) {
                filename = defaultFilename;
            } else {
                String		classPath = System.getProperty ("java.class.path");
                String[]	parts = property.split ("\\.");
                String		extra = null;
                
                for (int n = 0; n < parts.length - 1; ++n) {
                    if (extra != null) {
                        extra += "/" + parts[n];
                    } else {
                        extra = parts[n];
                    }
                }
                if (classPath != null) {
                    parts = classPath.split (":");
                    for (int n = 0; n < parts.length; ++n) {
                        if (isDirectory (parts[n])) {
                            String	fname = parts[n] + "/" + defaultFilename;
                            
                            if (fileExists (fname)) {
                                filename = fname;
                                break;
                            }
                            if (extra != null) {
                                fname = parts[n] + "/" + extra + "/" + defaultFilename;
                                if (fileExists (fname)) {
                                    filename = fname;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        try {
            load (new FileInputStream (filename));
        } catch (IOException e) {
            throw new ConfigException ("Failed reading input file " + filename + ": " + e);
        }
    }

    /** 
     * Converts a string to int
     *
     * @param str source as string
     * @return result as integer
     */
    protected int mkInt (String str) {
        return Integer.parseInt (str);
    }
    
    /**
     * Converts a string to a bool
     *
     * @param str source as string
     * @return result as boolean
     */
    protected boolean mkBool (String str) {
        boolean	val = false;
        
        if (str != null) {
            try {
                char	ch = str.charAt (0);
            
                switch (ch) {
                case 't':
                case 'T':
                case 'y':
                case 'Y':
                case '1':
                case '+':
                    val = true;
                    break;
                }
            } catch (IndexOutOfBoundsException e) {
                ;
            }
        }
        return val;
    }

    /**
     * Must be overwritten by subclass
     *
     * @return true, if subselection is accepted
     * @param variable 
     * @param vindex index into configuration variable array
     * @param parm subselection
     * @throws org.agnitas.util.ConfigException 
     */
    protected abstract boolean selected (int vindex, String variable, String parm) throws ConfigException;

    /**
     * Validates a found value for a variable, throws ConfigException
     * if validation failed
     *
     * @param variable 
     * @param vindex index into configuration variable array
     * @param value the value to validate
     * @throws org.agnitas.util.ConfigException 
     */
    protected abstract void validate (int vindex, String variable, String value) throws ConfigException;

    /**
     * This method is called for every missing variable in the configuration
     * file, the application should either set a default value or throw a
     * ConfigException
     *
     * @param variable 
     * @param vindex index into configuration variable array
     * @throws org.agnitas.util.ConfigException 
     */
    protected abstract void missing (int vindex, String variable) throws ConfigException;

    /**
     * This method is called for every unknown entry in the configuration
     * file. The application can ignore this or throw a ConfigException
     *
     * @param variable 
     * @throws org.agnitas.util.ConfigException 
     */
    protected abstract void unknown (String variable) throws ConfigException;

    /** Throws an exception with detailed information
     *
     * @param e the original ConfigException
     * @param detail more detailed information
     * @param variable the variable causing the exception
     */
    private final void failed (ConfigException e, String detail, String variable)
        throws ConfigException {
        throw new ConfigException (filename + ": " +
                       (e == null ? "" : "[" + e + "] ") +
                       "Validation of " +
                       (detail == null ? "" : detail + " ") +
                       "variable " + variable + " failed");
    }

    /**
     * Validate all variables in the configuration file against
     * the variable array
     *
     * @param variables the array of variables
     * @throws org.agnitas.util.ConfigException 
     */
    public void validation () throws ConfigException {
        String[] variables = getConfigVariables ();
        for (int n = 0; n < variables.length; ++n) {
            String	value = getProperty (variables[n]);
            int	len = variables[n].length ();
            int	pos;
            
            for (Enumeration name = propertyNames (); name.hasMoreElements (); ) {
                String	variable = (String) name.nextElement ();
                String	parm;

                if (((pos = variable.indexOf ('.')) != -1) && (pos == len)) {
                    parm = variable.substring (pos + 1);
                    try {
                        if (selected (n, variables[n], parm)) {
                            value = getProperty (variable);
                            break;
                        }
                    } catch (ConfigException e) {
                        failed (e, null, variable);
                    }
                }
            }
            try {
                if (value == null) {
                    missing (n, variables[n]);
                } else {
                    validate (n, variables[n], value);
                }
            } catch (ConfigException e) {
                failed (e, (value == null ? "missing" : null), variables[n]);
            }
        }

        for (Enumeration name = propertyNames (); name.hasMoreElements (); ) {
            String	variable = (String) name.nextElement ();
            int	n;

            if ((n = variable.indexOf ('.')) != -1) {
                variable = variable.substring (0, n);
            }
            for (n = 0; n < variables.length; ++n) {
                if (variable.equals (variables[n])) {
                    break;
                }
            }
            if (n == variables.length) {
                try {
                    unknown (variable);
                } catch (ConfigException e) {
                    failed (e, "unknown", variable);
                }
            }
        }
    }
}
