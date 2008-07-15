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

package org.agnitas.beans.impl;

import java.util.*;
import org.agnitas.beans.*;
import org.agnitas.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author Martin Helff
 */
public class DynamicTagImpl implements DynamicTag {
    
    protected String dynName;
    protected int companyID;
    protected int mailingID;
    protected int id;
    protected java.util.Map dynContent;
    
    /** Holds value of property startTagStart. */
    protected int startTagStart;
    
    /** Holds value of property startTagEnd. */
    protected int startTagEnd;
    
    /** Holds value of property valueTagStart. */
    protected int valueTagStart;
    
    /** Holds value of property valueTagEnd. */
    protected int valueTagEnd;
    
    /** Holds value of property complex. */
    protected boolean complex;
    
    /** Holds value of property endTagStart. */
    protected int endTagStart;
    
    /** Holds value of property endTagEnd. */
    protected int endTagEnd;
    
    /** Creates new DynamicTag */
    
    public DynamicTagImpl() {
    }
    
    public void setDynName(String name) {
        dynName=name;
    }
    
    public void setCompanyID(int id) {
        companyID=id;
    }
    
    public void setMailingID(int id) {
        mailingID=id;
    }
    
    public void setId(int id) {
        this.id=id;
    }
    
    public boolean addContent(DynamicTagContent aContent) {
        
        dynContent.put(Integer.toString(aContent.getDynOrder()), aContent);
        return true;
    }
    
    public String getDynName() {
        return dynName;
    }
    
    public int getDynContentCount() {
        if(dynContent==null)
            return 0;
        
        return dynContent.size();
    }
    
    public int getId() {
        return this.id;
    }
    
    public java.util.Map getDynContent() {
        return dynContent;
    }
        
    public boolean changeContentOrder(int aID, int direction) {
        Iterator aIt=null;
        DynamicTagContent firstContent=null;
        DynamicTagContent swapContent=null;
        int otherID=0;
        int tmp=0;
        
        if(dynContent==null)
            return false;
        
        firstContent=(DynamicTagContent)this.getDynContentID(aID);
        
        if(firstContent!=null) {
            aIt=this.dynContent.values().iterator();
            if(direction==1) {
                //rauf
                otherID=-1;
                while(aIt.hasNext()) {
                    swapContent=(DynamicTagContent)aIt.next();
                    if(swapContent.getDynOrder()<firstContent.getDynOrder() && swapContent.getDynOrder()>otherID) {
                        otherID=swapContent.getDynOrder();
                    }
                }                
            } else {
                // runter
                otherID=Integer.MAX_VALUE;
                while(aIt.hasNext()) {
                    swapContent=(DynamicTagContent)aIt.next();
                    if(swapContent.getDynOrder()>firstContent.getDynOrder() && swapContent.getDynOrder()<otherID) {
                        otherID=swapContent.getDynOrder();
                    }
                }

            }
        }
        
        if(otherID==-1 || otherID==Integer.MAX_VALUE) {
           return false; 
        }
        
        swapContent=(DynamicTagContent)this.dynContent.get(Integer.toString(otherID));
        
        tmp=firstContent.getDynOrder();
        firstContent.setDynOrder(swapContent.getDynOrder());
        swapContent.setDynOrder(tmp);
        
        this.dynContent.put(Integer.toString(swapContent.getDynOrder()), swapContent);
        this.dynContent.put(Integer.toString(firstContent.getDynOrder()), firstContent);
        
        return true;
    }
    
    public int getMaxOrder() {
        int maxOrder=0;
        DynamicTagContent aContent=null;
        
        if(dynContent==null)
            return maxOrder;
        
        Iterator aIt=this.dynContent.values().iterator();
        while(aIt.hasNext()) {
            aContent=(DynamicTagContent)aIt.next();
            if(aContent.getDynOrder()>maxOrder) {
                maxOrder=aContent.getDynOrder();
            }
        }

        return maxOrder;
    }
    
    public DynamicTagContent getDynContentID(int id) {
        DynamicTagContent aContent=null;
        Iterator aIterator=null;
        
        if(dynContent==null)
            return null;
        
        aIterator=dynContent.values().iterator();
        
        while(aIterator.hasNext()) {
            aContent=(DynamicTagContent)aIterator.next();
            if(aContent.getId()==id) {
                return aContent;
            }
            aContent=null;
        }
        
        return null;
    }
            
    public boolean removeContent(int aID) {
        DynamicTagContent aContent=null;
        Iterator aIt=null;
        
        if(dynContent==null)
            return false;
        
        aIt=dynContent.values().iterator();
        while(aIt.hasNext()) {
            aContent=(DynamicTagContent)aIt.next();
            if(aContent.getId()==aID) {
                aIt.remove();
                break;
            }
        }
        
        return true;
    }
            
    public int getCompanyID() {
        return companyID;
    }
    
    public int getMailingID() {
        return mailingID;
    }
    
    /** Getter for property startPos.
     * @return Value of property startPos.
     *
     */
    public int getStartTagStart() {
        return this.startTagStart;
    }
    
    /**
     * Setter for property startPos.
     * @param startTagStart 
     */
    public void setStartTagStart(int startTagStart) {
        this.startTagStart = startTagStart;
    }
    
    /** Getter for property endPos.
     * @return Value of property endPos.
     *
     */
    public int getStartTagEnd() {
        return this.startTagEnd;
    }
    
    /**
     * Setter for property endPos.
     * @param startTagEnd 
     */
    public void setStartTagEnd(int startTagEnd) {
        this.startTagEnd = startTagEnd;
    }
    
    /** Getter for property valueStart.
     * @return Value of property valueStart.
     *
     */
    public int getValueTagStart() {
        return this.valueTagStart;
    }
    
    /**
     * Setter for property valueStart.
     * @param valueTagStart 
     */
    public void setValueTagStart(int valueTagStart) {
        this.valueTagStart = valueTagStart;
    }
    
    /** Getter for property valueEnd.
     * @return Value of property valueEnd.
     *
     */
    public int getValueTagEnd() {
        return this.valueTagEnd;
    }
    
    /**
     * Setter for property valueEnd.
     * @param valueTagEnd 
     */
    public void setValueTagEnd(int valueTagEnd) {
        this.valueTagEnd = valueTagEnd;
    }
    
    /** Getter for property complex.
     * @return Value of property complex.
     *
     */
    public boolean isComplex() {
        return this.complex;
    }
    
    /** Setter for property complex.
     * @param complex New value of property complex.
     *
     */
    public void setComplex(boolean complex) {
        this.complex = complex;
    }
    
    /** Getter for property endTagStart.
     * @return Value of property endTagStart.
     *
     */
    public int getEndTagStart() {
        return this.endTagStart;
    }
    
    /** Setter for property endTagStart.
     * @param endTagStart New value of property endTagStart.
     *
     */
    public void setEndTagStart(int endTagStart) {
        this.endTagStart = endTagStart;
    }
    
    /** Getter for property endTagEnd.
     * @return Value of property endTagEnd.
     *
     */
    public int getEndTagEnd() {
        return this.endTagEnd;
    }
    
    /** Setter for property endTagEnd.
     * @param endTagEnd New value of property endTagEnd.
     *
     */
    public void setEndTagEnd(int endTagEnd) {
        this.endTagEnd = endTagEnd;
    }
    
    /**
     * Setter for property dynContent.
     * @param dynContent New value of property dynContent.
     */
    public void setDynContent(java.util.Map dynContent) {
        this.dynContent=dynContent;
    }

    /**
     * Holds value of property mailing.
     */
    protected org.agnitas.beans.Mailing mailing;

    /**
     * Getter for property mailing.
     * @return Value of property mailing.
     */
    public org.agnitas.beans.Mailing getMailing() {

        return this.mailing;
    }

    /**
     * Setter for property mailing.
     * @param mailing New value of property mailing.
     */
    public void setMailing(org.agnitas.beans.Mailing mailing) {

        this.mailing = mailing;
    }

    public boolean equals(Object obj) {
        return ((DynamicTag)obj).hashCode()==this.hashCode();
    }

    public int hashCode() {
        return dynName.hashCode();
    }
    
}


