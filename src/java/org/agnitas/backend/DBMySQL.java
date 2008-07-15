package org.agnitas.backend;

import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Driver;

/**
 * Database depended part of database access routines
 */

public class DBMySQL implements DBDriver {

    /** Connection to real database driver */
    private Driver	driver = null;

    /** empty constructor
     */
    public DBMySQL () {
    }

    /** Cleanup
     */
    public void finalize () {
        try {
            deinitDriver ();
        } catch (SQLException e) {
            ;
        }
    }

    /** setup driver specific
     */
    public void initDriver () throws SQLException {
        driver = new Driver ();
        DriverManager.registerDriver (driver);
    }

    /**
     * cleanup driver specific
     */
    public void deinitDriver () throws java.sql.SQLException {
        if (driver != null) {
            DriverManager.deregisterDriver (driver);
            driver = null;
        }
    }
}

