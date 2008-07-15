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
package	org.agnitas.backend;

import	java.io.Reader;
import	java.util.Hashtable;
import	java.util.Enumeration;
import	java.sql.ResultSet;
import	java.sql.Clob;
import	java.sql.SQLException;
import	org.agnitas.util.Log;

/**
 * Collection of all dynamic content
 */
class DynCollection {
    /**
     * Entry for storing target condiitions
     */
    private class Target {
        /** the unique ID for that target condition */
        protected long		id;
        /** the condition itself */
        protected String	condition;
        
        /** Constructor
         * @param nId the target ID
         */
        protected Target (long nId) {
            id = nId;
            condition = null;
        }
    }
    
    /** Reference to configuration */
    private Data		data;
    /** all dynamic names (including content) */
    protected Hashtable	names;
    /** number of all available names */
    protected int		ncount;

    /** Constructor
     * @param nData the configuration
     */
    protected DynCollection (Data nData) {
        data = nData;
        names = new Hashtable ();
        ncount = 0;
    }

    /** Collect all available dynamic parts from the database
     */
    protected void
    collectParts () throws Exception
    {
        ResultSet	rset;
        Hashtable	targets;
        int		tcount;
        
        rset = data.dbase.execQuery ("SELECT dyn_name_id, dyn_name " +
                         "FROM dyn_name_tbl " +
                         "WHERE mailing_id = " + data.mailing_id + " AND company_id = " + data.company_id);
        while (rset.next ()) {
            long	nameID;
            String	name;
            
            nameID = rset.getLong (1);
            name = rset.getString (2);
            if (! names.containsKey (new Long (nameID))) {
                names.put (new Long (nameID), new DynName (name, nameID));
                ncount++;
                data.logging (Log.DEBUG, "dyn", "Added dynamic name " + name);
            } else
                data.logging (Log.DEBUG, "dyn", "Skip already recorded name " + name);
        }
        rset.close ();
        
        targets = new Hashtable ();
        tcount = 0;
        rset = data.dbase.execQuery ("SELECT dyn_content_id, dyn_name_id, target_id, dyn_order, dyn_content " +
                         "FROM dyn_content_tbl " +
                         "WHERE dyn_name_id IN (SELECT dyn_name_id FROM dyn_name_tbl WHERE mailing_id = " + data.mailing_id + " AND company_id = " + data.company_id + ")");
        while (rset.next ()) {
            long	dyncontID;
            long	nameID;
            long	targetID;
            long	order;
            String	content;
            DynName	name;
            
            dyncontID = rset.getLong (1);
            nameID = rset.getLong (2);
            if ((name = (DynName) names.get (new Long (nameID))) != null) {
                targetID = rset.getLong (3);
                order = rset.getLong (4);
                content = StringOps.convertOld2New (StringOps.clob2string (rset.getClob (5)));
                name.add (new DynCont (dyncontID, targetID, order, content));
                if ((targetID != DynCont.MATCH_ALWAYS) &&
                    (targetID != DynCont.MATCH_NEVER) &&
                    (! targets.containsKey (new Long (targetID)))) {
                    targets.put (new Long (targetID), new Target (targetID));
                    tcount++;
                    data.logging (Log.DEBUG, "dyn", "Found target information for " + name.name + ", ID " + targetID);
                } else
                    data.logging (Log.DEBUG, "dyn", "Found target information for " + name.name + ", unhandled ID " + targetID);
            } else
                data.logging (Log.WARNING, "dyn", "Found content for " + name + " without an entry in dyn_name_tbl");
        }
        rset.close ();
        
        if (tcount > 0) {
            Enumeration	e;
            String		query;
            int		count;
            Target		tmp;
            
            e = targets.elements ();
            query = null;
            count = 0;
            while (e.hasMoreElements ()) {
                tmp = (Target) e.nextElement ();
                if (count == 0)
                    query = "SELECT target_id, target_sql " +
                        "FROM dyn_target_tbl " +
                        "WHERE company_id = " + data.company_id + " AND target_id IN (";
                else
                    query += ", ";
                query += tmp.id;
                ++count;
                if ((count == 20) || (! e.hasMoreElements ())) {
                    query += ")";
            
                    rset = data.dbase.execQuery (query);
                    while (rset.next ()) {
                        long	targetID;
                        String	targetSQL;

                        targetID = rset.getLong (1);
                        targetSQL = rset.getString (2);
                        if ((tmp = (Target) targets.get (new Long (targetID))) != null)
                            tmp.condition = targetSQL;
                        else
                            data.logging (Log.WARNING, "dyn", "Not requested target ID " + targetID + " found");
                    }
                    rset.close ();
                    count = 0;
                }
            }
        }

        for (Enumeration e = names.elements (); e.hasMoreElements (); ) {
            DynName	tmp = (DynName) e.nextElement ();

            for (int n = 0; n < tmp.clen; ++n) {
                DynCont	cont = (DynCont) tmp.content.elementAt (n);

                if ((cont.targetID != DynCont.MATCH_ALWAYS) &&
                    (cont.targetID != DynCont.MATCH_NEVER)) {
                    Target	tgt;

                    if ((tgt = (Target) targets.get (new Long (cont.targetID))) != null) {
                        if ((tgt.condition != null) && (tgt.condition.length () > 2))
                            cont.condition = tgt.condition;
                        else {
                            data.logging (Log.DEBUG, "dyn", "Setting ID " + cont.targetID + " to always matching due to missing condition");
                            cont.targetID = DynCont.MATCH_ALWAYS;
                        }
                    } else {
                        data.logging (Log.WARNING, "dyn", "Setting ID " + cont.targetID + " to never matching due to missing target");
                        cont.targetID = DynCont.MATCH_NEVER;
                    }
                }
            }
        }
    }
}