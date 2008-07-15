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

package org.agnitas.beans.impl;

import org.agnitas.beans.EmmLayout;

/**
 *
 * @author  mhe
 */
public class EmmLayoutImpl implements EmmLayout {
    
    /** Holds value of property headerUrl. */
    private String headerUrl;
    
    /** Holds value of property footerUrl. */
    private String footerUrl;
    
    /** Holds value of property normalColor. */
    private String normalColor;
    
    /** Holds value of property highlightColor. */
    private String highlightColor;
    
    /** Holds value of property baseUrl. */
    private String baseUrl;
    
    private static final long serialVersionUID = 210849440921811193L;
    
    /** Holds value of property layoutID. */
    private int layoutID;
        
    /** Creates new EmmLayout */
    public EmmLayoutImpl() {
    }
    
    /** Getter for property headerUrl.
     * @return Value of property headerUrl.
     */
    public String getHeaderUrl() {
        return this.headerUrl;
    }
    
    /** Setter for property headerUrl.
     * @param headerUrl New value of property headerUrl.
     */
    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }
    
    /** Getter for property footerUrl.
     * @return Value of property footerUrl.
     */
    public String getFooterUrl() {
        return this.footerUrl;
    }
    
    /** Setter for property footerUrl.
     * @param footerUrl New value of property footerUrl.
     */
    public void setFooterUrl(String footerUrl) {
        this.footerUrl = footerUrl;
    }
    
    /** Getter for property normalColor.
     * @return Value of property normalColor.
     */
    public String getNormalColor() {
        return this.normalColor;
    }
    
    /** Setter for property normalColor.
     * @param normalColor New value of property normalColor.
     */
    public void setNormalColor(String normalColor) {
        this.normalColor = normalColor;
    }
    
    /** Getter for property highlightColor.
     * @return Value of property highlightColor.
     */
    public String getHighlightColor() {
        return this.highlightColor;
    }
    
    /** Setter for property highlightColor.
     * @param highlightColor New value of property highlightColor.
     */
    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }
        
    /** Getter for property baseUrl.
     * @return Value of property baseUrl.
     */
    public String getBaseUrl() {
        return this.baseUrl;
    }
    
    /** Setter for property baseUrl.
     * @param baseUrl New value of property baseUrl.
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
        
    /** Getter for property layoutID.
     * @return Value of property layoutID.
     *
     */
    public int getLayoutID() {
        return this.layoutID;
    }
    
    /** Setter for property layoutID.
     * @param layoutID New value of property layoutID.
     *
     */
    public void setLayoutID(int layoutID) {
        this.layoutID = layoutID;
    }

    /**
     * Holds value of property companyID.
     */
    private int companyID;

    /**
     * Getter for property companyID.
     * @return Value of property companyID.
     */
    public int getCompanyID() {

        return this.companyID;
    }

    /**
     * Setter for property companyID.
     * @param companyID New value of property companyID.
     */
    public void setCompanyID(int companyID) {

        this.companyID = companyID;
    }
}
