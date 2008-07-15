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

package org.agnitas.stat.impl;

import java.io.*;
import java.util.*;
import org.agnitas.util.AgnUtils;
import org.agnitas.stat.URLStatEntry;

public class URLStatEntryImpl implements URLStatEntry {
    
    protected int urlID;
    protected String url;
    protected String shortname;
    protected int clicks;
    protected int clicksNetto;
    
    public URLStatEntryImpl() {
        this.urlID = 0;
    }
    
    public int getUrlID() {
        return this.urlID;
    }
    public String getShortname() {
        return this.shortname;
    }
    public String getUrl() {
        return this.url;
    }
    public int getClicks() {
        return this.clicks;
    }
    
    public void setUrlID(int urlID) {
        this.urlID = urlID;
    }
    public void setShortname(String shortname) {
        this.shortname = shortname;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setClicks(int clicks) {
        this.clicks = clicks;
    }
    
    public int getClicksNetto() {
        return this.clicksNetto;
    }
    
    public void setClicksNetto(int clicksNetto) {
        this.clicksNetto = clicksNetto;
    }
    
    public int compareTo(Object obj) {
        try {
            if(this.clicksNetto<((URLStatEntry)obj).getClicksNetto()) {
                return -1;
            }
            if(this.clicksNetto==((URLStatEntry)obj).getClicksNetto()) {
                return 0;
            }
            if(this.clicksNetto>((URLStatEntry)obj).getClicksNetto()) {
                return 1;
            }
        } catch (Exception e) {
            AgnUtils.logger().error(e.getMessage());
        }
        return -1;
    }
    
}