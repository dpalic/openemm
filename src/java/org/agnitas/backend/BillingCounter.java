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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.agnitas.util.Log;

/**
 * Collects the number of generated mails and stores
 * them in the database
 */
public class BillingCounter {
    /**
     * Current number of mails per mailtype
     */
    private long mailtype_counter[] = {0, 0, 0};

    /**
     * Number of mails since last write to database
     */
    private long current_mails = 0;

    /**
     * Statement to store mails in database
     */
    private PreparedStatement prep_statement = null;

    /**
     * Reference to configuration
     */
    private Data data = null;

    /**
     * Total number of mails
     */
    private long total_mails = 0;

    /**
     * Constructor
     * Creates an INSERT-statement for the number of mails
     * and throws an exception if the insert was failed.
     * Creates an UPDATE-statement for the status-id and tries to execute it.
     * Loggs if failes.
     *
     * @param data global data reference
     */
    public BillingCounter(Data data) throws Exception {
        this.data = data;

        try {
            // init row in billing table2
            Statement stmt = data.dbase.createStatement ();

            data.dbase.execUpdate (stmt,
                           "INSERT INTO mailing_backend_log_tbl " +
                           "(status_id, mailing_id, current_mails, total_mails, " + data.dbase.timestamp + ", creation_date) " +
                           "VALUES (" + data.maildrop_status_id + ", " + data.mailing_id + ", 0, 0, " + data.dbase.sysdate + ", " + data.dbase.sysdate + ")");
            data.logging (Log.VERBOSE, "billing", "Init mailing_backend_log done.");
            data.dbase.closeStatement (stmt);
        } catch (SQLException e) {
            data.logging (Log.ERROR, "billing", "Unable to init mailing_backend_log_tbl");
            throw new Exception ("Error: Could not setup mailing_backend_log_tbl: " + e.toString ());
        }

        String prep =
            "UPDATE mailing_backend_log_tbl SET current_mails = ?, total_mails = " +
            data.totalSubscribers + ", " + data.dbase.timestamp + " = " + data.dbase.sysdate +
            " WHERE status_id = " + data.maildrop_status_id;
        try{
            // enter stored procedure
            prep_statement = data.dbase.prepareStatement(prep);
            data.logging (Log.VERBOSE, "billing", "Init mailing backend store proc. done");
        } catch (SQLException e){
            data.logging (Log.ERROR, "billing", "Unable to prepare statement for mailing_backend_log_tbl using " + prep + ": " + e);
            throw new Exception ("Error sending prp_statement for table MAILING_Backend_TBL: " + e);
            //System.exit(9);
        }
    }

    /**
     * Cleaunup database connection (close)
     */
    public void done () throws Exception {
        if (prep_statement != null) {
            try {
                data.dbase.closeStatement (prep_statement);
                prep_statement = null;
            } catch (SQLException e) {
                throw new Exception ("Unable to close database relation: " + e);
            }
        }
    }

    /**
     * Adds a single mail to logging.
     *
     * @param mailtype of this mail
     */
    public void sadd (int mailtype) {
        total_mails++;
        current_mails++;
        mailtype_counter[mailtype]++;
    }

    /**
     * update log table with current number of mails
     *
     * @param mail_count current number of mails
     */
    protected void update_log (long mail_count) throws Exception {
        try{
            prep_statement.clearParameters();
            prep_statement.setLong(1, mail_count);
            prep_statement.executeUpdate();
            data.logging (Log.DEBUG, "billing", "Update log done at message no:" + mail_count);
        } catch (SQLException e){
            data.logging (Log.ERROR, "billing", "Unable to update mailing_backend_log_tbl: " + e);
            throw new Exception ("ERROR! Could not update billing table with current_mails: " + e);
        }
    }


    /***
     * write it to the database -- after all mails were written
     *
     * @param block_size size of an output block
     * @param block_count number of blocks generated
     */
    public void write_db (long block_size, long block_count) throws Exception {
        Statement stmt=null;
        String end_backend_log =
            "UPDATE mailing_backend_log_tbl " +
            "SET current_mails = " + total_mails +  ", total_mails = " + total_mails + " " +
            "WHERE status_id = " + data.maildrop_status_id;
        String wend_backend_log =
            "INSERT INTO world_mailing_backend_log_tbl (mailing_id, current_mails, total_mails, " + data.dbase.timestamp + ", creation_date) " +
            "VALUES (" + data.mailing_id + ", " + total_mails + ", " + total_mails + ", " + data.dbase.sysdate + ", " + data.dbase.sysdate + ")";

        try{
            stmt = data.dbase.createStatement();
            // total mail number is corrected here, and equals total_mails.
            // before that, the total_mails number was the number of all
            // subscribers. the result is noe the number of all sent mails, minus
            // the mails which could not be sent because of empty or ellegal fields

            data.dbase.execUpdate (stmt, end_backend_log);
            data.logging (Log.VERBOSE, "billing", "Final update backend_log done.");
            if (data.isWorldMailing () && (wend_backend_log != null)) {
                try {
                    data.dbase.execUpdate (stmt, wend_backend_log);
                } catch (SQLException e) {
                    data.logging (Log.WARNING, "billing", "Unable to insert record into world_mailing_backend_log_tbl: " + e);
                }
            }
        } catch (SQLException e){
            data.logging (Log.ERROR, "billing", "Unable to update mailing_backend_log_tbl using " + end_backend_log + ": " + e);
            throw new Exception ("Error writing final mailing_backend_tbl values: " + e);
            //System.exit(9);
        }

        try{
            data.dbase.closeStatement (stmt);
        } catch (SQLException e){
            data.logging (Log.ERROR, "billing", "Unable to update mailing_account_tbl: " + e);
            throw new Exception("Error writing final mailing_account_tbl values: " + e);
            //System.exit(9);
        }
    }

    /**
     * Write some runtime information to the logfile
     */
    public void output() {
        if (data.islog (Log.NOTICE)) {
            data.logging (Log.NOTICE, "billing", "Total mail message" + Log.exts (total_mails) + " written: " + total_mails);
            for(int i=0; i < mailtype_counter.length; i++) {
                data.logging (Log.NOTICE, "billing",
                          "Mailtype " + i + ": " + mailtype_counter[i] +
                          " message" + Log.exts (mailtype_counter[i]));
            }
        }
    }

    /**
     * Write some final information to the logfile
     */
    public void debug_out () {
        if (data.islog (Log.DEBUG)) {
            int n;

            data.logging (Log.DEBUG, "billing", "Mailtype/Number/Bytes:");
            for (n = 0; n < 3; ++n) {
                data.logging (Log.DEBUG, "billing", "\t" + n + "/" + mailtype_counter[n]);
            }
        }
    }
}
