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

import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;

import org.agnitas.util.Log;

/**
 * Collection of all dynamic content
 */
public class DynCollection {
    /**
     * Entry for storing target condiitions
     */
    private class Target {
        /** the unique ID for that target condition */
        protected long      id;
        /** the condition itself */
        protected String    condition;

        /** Constructor
         * @param nId the target ID
         */
        protected Target (long nId) {
            id = nId;
            condition = null;
        }
    }

    /** Reference to configuration */
    private Data        data;
    /** all dynamic names (including content) */
    protected Hashtable <Long, Object>
                names;
    /** number of all available names */
    protected int       ncount;

    /** Constructor
     * @param nData the configuration
     */
    protected DynCollection (Data nData) {
        data = nData;
        names = new Hashtable <Long, Object> ();
        ncount = 0;
    }

    /** Creates a new dynamic content element
     * @param dyncontID the unique ID
     * @param targetID optional ID for target expression
     * @param order priority of this content
     * @param content the content itself
     * @return a new dynamic content instance
     */
    public Object mkDynCont (long dyncontID, long targetID, long order, String content) {
        return new DynCont (dyncontID, targetID, order, content);
    }

    public Object mkDynName (String name, long nameID) {
        return new DynName (name, nameID);
    }

    protected String queryDynNameColumns () {
        return "dyn_name_id, dyn_name";
    }

    protected void setDynNameColumns (Object dno, ResultSet rset) {
    }

    /** Collect all available dynamic parts from the database
     */
    protected void collectParts () throws Exception
    {
        ResultSet   rset;
        Hashtable <Long, Target>
                targets;
        int     tcount;

        rset = data.dbase.execQuery ("SELECT " + queryDynNameColumns () + " " +
                         "FROM dyn_name_tbl " +
                          "WHERE mailing_id = " + data.mailing_id + " AND company_id = " + data.company_id);
        while (rset.next ()) {
            long    nameID;
            String  name;

            nameID = rset.getLong (1);
            name = rset.getString (2);
            if (! names.containsKey (new Long (nameID))) {
                Object  dno = mkDynName (name, nameID);

                setDynNameColumns (dno, rset);
                names.put (new Long (nameID), dno);
                ncount++;
                data.logging (Log.DEBUG, "dyn", "Added dynamic name " + name);
            } else
                data.logging (Log.DEBUG, "dyn", "Skip already recorded name " + name);
        }
        rset.close ();

        targets = new Hashtable <Long, Target> ();
        tcount = 0;
        rset = data.dbase.execQuery ("SELECT dyn_content_id, dyn_name_id, target_id, dyn_order, dyn_content " +
                         "FROM dyn_content_tbl " +
                         "WHERE dyn_name_id IN (SELECT dyn_name_id FROM dyn_name_tbl WHERE mailing_id = " + data.mailing_id + " AND company_id = " + data.company_id + ")");
        while (rset.next ()) {
            long    dyncontID;
            long    nameID;
            long    targetID;
            long    order;
            String  content;
            DynName name;

            dyncontID = rset.getLong (1);
            nameID = rset.getLong (2);
            if ((name = (DynName) names.get (new Long (nameID))) != null) {
                targetID = rset.getLong (3);
                order = rset.getLong (4);
                content = StringOps.convertOld2New (StringOps.clob2string (rset.getClob (5)));
                name.add ((DynCont) mkDynCont (dyncontID, targetID, order, content), data);
                if ((targetID != DynCont.MATCH_ALWAYS) &&
                    (targetID != DynCont.MATCH_NEVER) &&
                    (! targets.containsKey (new Long (targetID)))) {
                    targets.put (new Long (targetID), new Target (targetID));
                    tcount++;
                    data.logging (Log.DEBUG, "dyn", "Found target information for " + name.name + ", ID " + targetID);
                } else
                    data.logging (Log.DEBUG, "dyn", "Found target information for " + name.name + ", already handled ID " + targetID);
            } else
                data.logging (Log.WARNING, "dyn", "Found content for " + name + " without an entry in dyn_name_tbl");
        }
        rset.close ();

        if (tcount > 0) {
            Enumeration <Target>
                    e;
            String      query;
            int     count;
            Target      tmp;

            e = targets.elements ();
            query = null;
            count = 0;
            while (e.hasMoreElements ()) {
                tmp = e.nextElement ();
                if (count == 0)
                    query = "SELECT target_id, target_sql " +
                        "FROM dyn_target_tbl " +
                        "WHERE target_id IN (";
                else
                    query += ", ";
                query += tmp.id;
                ++count;
                if ((count == 20) || (! e.hasMoreElements ())) {
                    query += ")";

                    rset = data.dbase.execQuery (query);
                    while (rset.next ()) {
                        long    targetID;
                        String  targetSQL;

                        targetID = rset.getLong (1);
                        targetSQL = rset.getString (2);
                        if ((tmp = targets.get (new Long (targetID))) != null)
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
            DynName tmp = (DynName) e.nextElement ();

            for (int n = 0; n < tmp.clen; ++n) {
                DynCont cont = (DynCont) tmp.content.elementAt (n);

                if ((cont.targetID != DynCont.MATCH_ALWAYS) &&
                    (cont.targetID != DynCont.MATCH_NEVER)) {
                    Target  tgt;

                    if ((tgt = targets.get (new Long (cont.targetID))) != null) {
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
