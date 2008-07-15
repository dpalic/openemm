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

package org.agnitas.beans;

import java.sql.Blob;

/**
 * 
 * @author Martin Helff    
 */
public interface MailingComponent extends java.io.Serializable {
    
    int TYPE_ATTACHMENT = 3;
    public static final int TYPE_PERSONALIZED_ATTACHMENT = 4;
    int TYPE_HOSTED_IMAGE = 5;
    int TYPE_IMAGE = 1;
    int TYPE_TEMPLATE = 0;
    int TYPE_PREC_ATTACHMENT = 7;

    /**
     * Getter for property binaryBlock.
     * 
     * @return Value of property binaryBlock.
     */
    byte[] getBinaryBlock();

    /**
     * Getter for property id.
     *
     * @return Value of property id.
     */
    int getId();

    /**
     * Getter for property componentName.
     *
     * @return Value of property componentName.
     */
    String getComponentName();

    /**
     * Getter for property emmBlock.
     *
     * @return Value of property emmBlock.
     */
    String getEmmBlock();

    /**
     * Getter for property mimeType.
     *
     * @return Value of property mimeType.
     */
    String getMimeType();

    /**
     * Getter for property targetID.
     * 
     * @return Value of property targetID.
     */
    int getTargetID();

    /**
     * Getter for property type.
     * 
     * @return Value of property type.
     */
    int getType();

    /**
     * Load contents from URL.
     */
    boolean loadContentFromURL();

    /**
     * nse
     */
    String makeEMMBlock();

    /**
     * Setter for property binaryBlock.
     *
     * @param cid New value of propety binaryBlock.
     */
    void setBinaryBlock(byte[] cid);

void setBinaryBlob(Blob imageBlob);
Blob getBinaryBlob();
    /**
     * Setter for property companyID.
     *
     * @param cid New value of propety companyID.
     */
    void setCompanyID(int cid);

    /**
     * Setter for property id.
     *
     * @param cid New value of propety id.
     */
    void setId(int cid);

    /**
     * Setter for property componentName.
     *
     * @param cid New value of propety componentName.
     */
    void setComponentName(String cid);

    /**
     * Setter for property emmBlock.
     *
     * @param cid New value of propety emmBlock.
     */
    void setEmmBlock(String cid);

    /**
     * Setter for property mailingID.
     *
     * @param cid New value of propety mailingID.
     */
    void setMailingID(int cid);

    /**
     * Setter for property mimieType.
     *
     * @param cid New value of propety mimeType.
     */
    void setMimeType(String cid);

    /**
     * Setter for property targetID.
     * 
     * @param targetID New value of property targetID.
     */
    void setTargetID(int targetID);

    /**
     * Setter for property type.
     *
     * @param cid New value of propety type.
     */
    void setType(int cid);

    /**
     * Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    public int getMailingID();

    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    public int getCompanyID();
    
}
