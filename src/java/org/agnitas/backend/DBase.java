/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
package org.agnitas.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;

import org.agnitas.util.Log;

/** Database abstraction layer
 */
public class DBase {
    /** name for current date in database */
    public String       sysdate = "now()";
    public String       timestamp = "change_date";
    public String       queryTableList = "SHOW TABLES";
    public String       measureType = "MEASURE_TYPE";
    /** Reference to configuration */
    private Data        data = null;
    /** database specific driver object */
    public DBDriver    driver = null;
    /** connection to database used internally */
    public Connection  connect = null;
    /** save original commitstate */
    private boolean     commitstate = false;

    /** beware: only one result set per statement can be open at the
     * same time -- use DBase.createStatement() for new resultsets
     * internally used
     */
    private Statement   stmt = null;

    /** to have a collection of all open statments to close them
     * finally, store a reference into this table
     */
    public HashSet      scoll = null;

    /**
     * Create the interal used default statement
     */
    private void createDefaultStatement () throws SQLException {
        int mze;

        if (stmt != null) {
            stmt.close ();
        }
        stmt = connect.createStatement ();

        if ((mze = data.blockSize ()) > stmt.getFetchSize ()) {
            stmt.setFetchSize (mze > 1024 ? 1024 : mze);
        }
    }
    
    /** Returns the currently used database driver
     * @return instance of the driver
     */
    public DBDriver getDBDriver () {
        return new DBMySQL ();
    }

    /** Constructor for this class
     */
    public DBase (Data nData, Connection nconn) throws Exception {
        data = nData;
        if (nconn == null) {
            driver = getDBDriver ();
            try {
                driver.initDriver ();
                connect = DriverManager.getConnection (data.sqlConnect (), data.dbLogin (), data.dbPassword ());
                createDefaultStatement ();
            } catch (Exception e) {
                data.logging (Log.ERROR, "dbase", "Unable to setup JDBC driver: " + e);
                throw new Exception("Error initializing database connection: " + e);
            }
        } else {
            driver = null;
            connect = nconn;
            stmt = null;
        }
        if (connect != null) {
            commitstate = connect.getAutoCommit ();
            connect.setAutoCommit (true);
        }
        scoll = new HashSet ();
    }

    /**
     * Cleanup, close open statements and database connection
     */
    public void
    done () throws Exception
    {
        int err;

        err = 0;
        if (scoll.size () > 0) {
            Iterator    i = scoll.iterator ();
            Statement   tmp;

            while (i.hasNext ()) {
                tmp = (Statement) i.next ();
                if (tmp != null)
                    try {
                        tmp.close ();
                    } catch (Exception e) {
                        data.logging (Log.WARNING, "dbase", "Failed to close open statement: " + e);
                    }
            }
            scoll.clear ();
        }
        if (stmt != null) {
            try {
                stmt.close ();
                stmt = null;
            } catch (Exception e) {
                data.logging (Log.WARNING, "dbase", "Failed to cleanup statement: " + e);
                ++err;
            }
        }
        if (driver != null) {
            if (connect != null) {
                try {
                    connect.close ();
                    connect = null;
                } catch (Exception e) {
                    data.logging (Log.WARNING, "dbase", "Failed to cleanup connection: " + e);
                    ++err;
                }
            }
            try {
                driver.deinitDriver ();
            } catch (Exception e) {
                data.logging (Log.WARNING, "dbase", "Failed to cleanup driver: " + e);
                ++err;
            }
        } else if (connect != null) {
            connect.setAutoCommit (commitstate);
            connect = null;
        }
        if (err != 0)
            throw new Exception ("Error deinitializing database connection");
    }

    /**
     * Set database connection, so we do not need to open out own
     * connection
     */
    public void setConnection (Connection conn) throws Exception {
        connect = conn;
        if (connect != null) {
            try {
                commitstate = connect.getAutoCommit ();
                connect.setAutoCommit (true);
            } catch (SQLException e) {
                throw new Exception ("Unable to set auto commit on connection: " + e.toString ());
            }
        }
    }

    /** return current connection
     * @return current used connection
     */
    public Connection getConnection () {
        return connect;
    }

    /**
     * Create a new statment
     * @return the newly created statement
     */
    public Statement createStatement () throws Exception {
        Statement   temp;

        try {
            temp = connect.createStatement ();
            if (temp != null) {
                scoll.add (temp);
            }
        } catch (SQLException e) {
            throw new Exception ("New statement failed: " + e);
        }
        return temp;
    }

    /**
     * Create a new prepared statement
     * @param pstr the statement to be prepared
     * @return the newly creted prepared statement
     */
    public PreparedStatement prepareStatement (String pstr) throws Exception {
        PreparedStatement   temp;

        data.logging (Log.DEBUG, "dbase", "DB-Prep: " + pstr);
        try {
            temp = connect.prepareStatement (pstr);
            if (temp != null) {
                scoll.add (temp);
            }
        } catch (SQLException e) {
            data.logging (Log.DEBUG, "dbase", "DB-Prep failed: " + e);
            throw new Exception ("Prepare statement failed: " + e);
        }
        return temp;
    }


    /**
     * Close a statement
     * @param temp the statement to close
     */
    public void closeStatement (Statement temp) throws SQLException {
        temp.close ();
        scoll.remove (temp);
    }

    /**
     * reset the default statement (close/open) it, if an error
     * had occured
     */
    public void resetDefaultStatement () throws SQLException {
        try {
            createDefaultStatement ();
        } catch (SQLException e) {
            stmt = null;
            createDefaultStatement ();
        }
    }

    /**
     * Execute a query
     * @param st the statement to use
     * @param query the SQL query
     * @return result set for that query
     */
    public ResultSet execQuery (Statement st, String query) throws Exception {
        ResultSet   rset;

        data.logging (Log.DEBUG, "dbase", "DB-Exec: " + query);
        try {
            rset = st.executeQuery (query);
        } catch (SQLException e) {
            data.logging (Log.DEBUG, "dbase", "DB-Exec failed: " + e);
            throw new Exception ("Query " + query + " failed: " + e);
        }
        return rset;
    }

    /**
     * Execute a query using default statement
     * @param query the SQL query
     * @return result set for that query
     */
    public ResultSet execQuery (String query) throws Exception {
        if (stmt == null) {
            createDefaultStatement ();
        }
        return execQuery (stmt, query);
    }

    /**
     * Executes a simple query, typically used to fetch
     * exactly one record from the database
     * @param query the SQL query
     * @return result set with already fetched first record
     */
    public ResultSet simpleQuery (String query) throws Exception {
        ResultSet   rset;

        rset = execQuery (query);
        if (! rset.next ()) {
            throw new Exception ("No entry for query: " + query);
        }
        return rset;
    }

    /**
     * Execute an update/insert/delete or other SQL command
     * that do not return any result set
     * @param st the statement to use
     * @param query the SQL query
     * @return the number of rows affected
     */
    public int execUpdate (Statement st, String query) throws Exception {
        int rc;

        data.logging (Log.DEBUG, "dbase", "DB-Updt: " + query);
        try {
            rc = st.executeUpdate (query);
        } catch (SQLException e) {
            data.logging (Log.DEBUG, "dbase", "DB-Updt failed: " + e);
            throw new Exception ("Update " + query + " failed: " + e);
        }
        return rc;
    }

    /**
     * Execute an update/insert/delete (...) using the default
     * statement
     * @param query the SQL query
     * @return the number of rows affected
     */
    public int execUpdate (String query) throws Exception {
        if (stmt == null) {
            createDefaultStatement ();
        }
        return execUpdate (stmt, query);
    }

    /**
     * Check the string for a minimum length or not all spaces,
     * otherwise set it to null
     * @param s the string to validate
     * @param minLength the minimal length required for the string
     * @return the modified string
     */
    private String
    validate (String s, int minLength)
    {
        if (s != null) {
            int len = s.length ();

            if (len < minLength) {
                s = null;
            } else {
                int n;

                for (n = 0; n < len; ++n) {
                    if (s.charAt (n) != ' ') {
                        break;
                    }
                }
                if (n == len) {
                    s = null;
                }
            }
        }
        return s;
    }

    /** get valid string using position
     * @param rset the result set
     * @param pos the position
     * @param minLength minimum length
     * @return the validated string
     */
    public String
    getValidString (ResultSet rset, int pos, int minLength) throws SQLException
    {
        return validate (rset.getString (pos), minLength);
    }

    /** get valid string using position
     * @param rset the result set
     * @param pos the position
     * @return the validated string
     */
    public String
    getValidString (ResultSet rset, int pos) throws SQLException
    {
        return validate (rset.getString (pos), 1);
    }

    /** get valid string using column name
     * @param rset the result set
     * @param column the name of the column
     * @param minLength minimum length
     * @return the validated string
     */
    public String
    getValidString (ResultSet rset, String column, int minLength) throws SQLException
    {
        return validate (rset.getString (column), minLength);
    }

    /** get valid string using column name
     * @param rset the result set
     * @param column the name of the column
     * @return the validated string
     */
    public String
    getValidString (ResultSet rset, String column) throws SQLException
    {
        return validate (rset.getString (column), 1);
    }
}
