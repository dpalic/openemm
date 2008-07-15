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
package org.agnitas.backend;

import	nettrack.net.encoding.Punycode;
import	nettrack.net.encoding.PunycodeException;

/** This class handles emails and there different
 * representations
 */
public class EMail {
    /** The full email with optional comment */
    public String	full;
    /** The email alone */
    public String	pure;
    /** The full email, coded using punycode */
    public String	full_puny;
    /** The email alone coded using punycode */
    public String	pure_puny;
    
    /** Extracts the email address by stripping any comment
     * @param str the string to extract the email address from
     * @return the pure email
     */
    private String extractPureAddress (String str) {
        int	n, m;

        if (((n = str.indexOf ('(')) != -1) &&
            ((m = str.indexOf (')', n + 1)) != -1))
            str = (n > 0 ? str.substring (0, n) : "") + (m + 1 < str.length () ? str.substring (m + 1) : "");
        if (((n = str.indexOf ('<')) != -1) &&
            ((m = str.indexOf ('>', n + 1)) != -1))
            str = str.substring (n + 1, m);
        n = str.length ();
        for (m = 0; (m < n) && (str.charAt (m) == ' '); ++m)
            ;
        if (m > 0) {
            str = str.substring (m);
            n -= m;
        }
        for (m = n - 1; (m >= 0) && (str.charAt (m) == ' '); --m)
            ;
        if (m < n - 1)
            str = str.substring (0, m + 1);
        return str;
    }

    /** Encode a string using punycode
     * @param str the input email address
     * @return the punycode version
     */
    private String punycoded (String str) {
        int	n;
        
        if ((n = str.indexOf ('@')) != -1) {
            String		user = str.substring (0, n);
            String		domain = str.substring (n + 1).toLowerCase ();
            int		dlen = domain.length ();
            StringBuffer	ndomain = new StringBuffer (dlen + 32);
            
            n = 0;
            while (n < dlen) {
                int	dpos = domain.indexOf ('.', n);
                String	sub;
                int	slen, m;
                
                if (dpos == -1)
                    dpos = dlen;
                sub = domain.substring (n, dpos);
                slen = dpos - n;
                for (m = 0; m < slen; ++m) {
                    char	ch = sub.charAt (m);

                    if ("0123456789abcdefghijklmnopqrstuvwxyz_-".indexOf (ch) == -1)
                        break;
                }
                if (m < slen)
                    try {
                        sub = "xn--" + Punycode.encode (sub);
                    } catch (PunycodeException e) {
                        ;
                    }
                ndomain.append (sub);
                if (dpos < dlen)
                    ndomain.append ('.');
                n = dpos + 1;
            }
            str = user + '@' + ndomain.toString ();
        }
        return str;
    }

    /** Create the punycode version of the available email address
     */
    private void makePunyCoded () {
        pure = null;
        full_puny = null;
        pure_puny = null;
        if (full != null) {
            pure = extractPureAddress (full);
            pure_puny = punycoded (pure);

            int		cur = 0;
            int		len = full.length ();
            int		plen = pure.length ();
            StringBuffer	temp = new StringBuffer (len + 32);
                
            if (plen > 0) {
                while (cur < len) {
                    int	n;
                    
                    if ((n = full.indexOf (pure, cur)) != -1) {
                        if (n > cur)
                            temp.append (full.substring (cur, n));
                        temp.append (pure_puny);
                        cur = n + plen;
                    } else {
                        temp.append (full.substring (cur, len));
                        cur = len;
                    }
                }
                full_puny = temp.toString ();
            } else
                full_puny = full;
        }
    }
    
    /** Constructor
     * @param str the email address with optional comment
     */
    public EMail (String str) {
        setEMail (str);
    }
    
    /** Set and parse the email address
     */
    public void setEMail (String str) {
        full = str;
        makePunyCoded ();
    }

    /** Checks if the email address could be fully parse
     * @return true, if address is valid
     */
    public boolean valid () {
        return (full != null) && (full_puny != null) && (pure != null) && (pure_puny != null);
    }
    
    /** String representation of ourself
     * @return our representation
     */
    public String toString () {
        return	 "(" + (full == null ? "" : full) +
            ", " + (full_puny == null ? "" : full_puny) +
            ", " + (pure == null ? "" : pure) +
            ", " + (pure_puny == null ? "" : pure_puny) +
            ")";
    }
}