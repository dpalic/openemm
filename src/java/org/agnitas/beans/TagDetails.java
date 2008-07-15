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

import java.util.Hashtable;

/**
 *
 * @author mhe
 */
public interface TagDetails {
    void analyzeParameters() throws Exception;

    /**
     * Search for tagName in fullText.
     *
     * @return Value of found tagName
     */
    String findTagName();

    /**
     * Search for tagParameters in fullText.
     */
    boolean findTagParameters();

    /**
     * Getter for property endPos.
     * 
     * @return Value of property endPos.
     */
    int getEndPos();

    /**
     * Getter for property fullText.
     * 
     * @return Value of property fullText.
     */
    String getFullText();

    /**
     * Getter for property name.
     * 
     * @return Value of property name.
     */
    String getName();

    /**
     * Getter for property startPos.
     * 
     * @return Value of property startPos.
     */
    int getStartPos();

    /**
     * Getter for property tagName.
     * 
     * @return Value of property tagName.
     */
    String getTagName();

    /**
     * Getter for property tagParameters.
     * 
     * @return Value of property tagParameters.
     */
    Hashtable getTagParameters();

    /**
     * @return true==complex
     * false==notcomplex
     */
    boolean isComplex();

    /**
     * Setter for property tagName.
     * 
     * @param tagName New value of property tagName.
     */
    void setTagName(String tagName);

    /**
     * Setter for property tagParameters.
     * 
     * @param tagParameters New value of property tagParameters.
     */
    void setTagParameters(Hashtable tagParameters);

    /**
     * Setter for property startPos.
     *
     * @param startPos New value of property startPos.
     */
    public void setStartPos(int startPos);

    /**
     * Setter for property name.
     *
     * @param name New value of property name.
     */
    public void setName(java.lang.String name);

    /**
     * Setter for property fullText.
     *
     * @param fullText New value of property fullText.
     */
    public void setFullText(java.lang.String fullText);

    /**
     * Setter for property endPos.
     *
     * @param endPos New value of property endPos.
     */
    public void setEndPos(int endPos);
    
}
