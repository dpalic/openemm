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

package org.agnitas.target.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.agnitas.target.TargetNode;
import org.agnitas.util.SafeString;

/**
 *
 * @author  mhe
 */
public class TargetNodeString extends TargetNode implements Serializable {
    
    //    public static char columnType='C';
    
    private static final long serialVersionUID = -5363353927700548241L;
    
    /** Holds value of property openBracketBefore. */
    protected boolean openBracketBefore;
    
    /** Holds value of property closeBracketAfter. */
    protected boolean closeBracketAfter;
    
    /** Holds value of property chainOperator. */
    protected int chainOperator;
    
    /** Holds value of property primaryOperator. */
    protected int primaryOperator;
    
    /** Holds value of property primaryField. */
    protected String primaryField;
    
    /** Holds value of property primaryFieldType. */
    protected String primaryFieldType;
    
    /** Holds value of property primaryValue. */
    protected String primaryValue;
    
    /** Creates a new instance of TargetNodeString */
    public TargetNodeString() {
        OPERATORS=new String[]{"=", "<>", ">", "<", "LIKE", "NOT LIKE", null, "IS"};
        BSH_OPERATORS=new String[]{"==", "!=", ">", "<", null, null, null, "IS"};
    }
    
    public String generateSQL() {
        StringBuffer tmpSQL=new StringBuffer("");
        
        switch(this.chainOperator) {
            case TargetNode.CHAIN_OPERATOR_AND:
                tmpSQL.append(" AND ");
                break;
            case TargetNode.CHAIN_OPERATOR_OR:
                tmpSQL.append(" OR ");
                break;
            default:
                tmpSQL.append(" ");
        }
        
        if(this.openBracketBefore) {
            tmpSQL.append("(");
        }
        
        if(this.primaryOperator!=TargetNode.OPERATOR_IS) {
            tmpSQL.append("lower(cust.");
        } else {
            tmpSQL.append("cust.");
        }
        tmpSQL.append(this.primaryField);
        if(this.primaryOperator!=TargetNode.OPERATOR_IS) {
            tmpSQL.append(") ");
        } else {
            tmpSQL.append(" ");
        }
        tmpSQL.append(this.OPERATORS[this.primaryOperator-1]);
        if(this.primaryOperator!=TargetNode.OPERATOR_IS) {
            tmpSQL.append(" lower('");
        } else {
            tmpSQL.append(" ");
        }
        tmpSQL.append(SafeString.getSQLSafeString(this.primaryValue));
        if(this.primaryOperator!=TargetNode.OPERATOR_IS) {
            tmpSQL.append("')");
        } else {
            tmpSQL.append(" ");
        }
        
        if(this.closeBracketAfter) {
            tmpSQL.append(")");
        }
        
        return tmpSQL.toString();
    }
    
    public String generateBsh() {
        StringBuffer tmpBsh=new StringBuffer("");
        
        switch(this.chainOperator) {
            case TargetNode.CHAIN_OPERATOR_AND:
                tmpBsh.append(" && ");
                break;
            case TargetNode.CHAIN_OPERATOR_OR:
                tmpBsh.append(" || ");
                break;
            default:
                tmpBsh.append(" ");
        }
        
        if(this.openBracketBefore) {
            tmpBsh.append("(");
        }
        
        switch(this.primaryOperator) {
            case TargetNode.OPERATOR_LIKE:
            case TargetNode.OPERATOR_NLIKE:
                if(this.primaryOperator==TargetNode.OPERATOR_NLIKE) {
                    tmpBsh.append("!");
                }
                tmpBsh.append("AgnUtils.match(AgnUtils.toLowerCase(\"");
                tmpBsh.append(this.primaryValue);
                tmpBsh.append("\"), AgnUtils.toLowerCase(");
                tmpBsh.append(this.primaryField.toUpperCase());
                tmpBsh.append("))");
                break;
                
            case TargetNode.OPERATOR_IS:
                tmpBsh.append(this.primaryField.toUpperCase());
                if(this.primaryValue.startsWith("null")) {
                    tmpBsh.append("==");
                } else {
                    tmpBsh.append("!=");
                }
                tmpBsh.append("null ");
                break;
                
            default:
                tmpBsh.append("AgnUtils.compareString(AgnUtils.toLowerCase(");
                tmpBsh.append(this.primaryField.toUpperCase());
                tmpBsh.append("), ");
                tmpBsh.append("AgnUtils.toLowerCase(\"");
                tmpBsh.append(SafeString.getSQLSafeString(this.primaryValue));
                tmpBsh.append("\"), ");
                tmpBsh.append(Integer.toString(this.primaryOperator-1));
                tmpBsh.append(") ");
        }
        
        if(this.closeBracketAfter) {
            tmpBsh.append(")");
        }
        
        return tmpBsh.toString();
    }
    
    public void setPrimaryOperator(int primOp) {
        if(primOp==TargetNode.OPERATOR_MOD)
            primOp=TargetNode.OPERATOR_EQ;
        
        this.primaryOperator=primOp;
    }
    
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        allFields=in.readFields();
        this.chainOperator=allFields.get("chainOperator", TargetNode.CHAIN_OPERATOR_NONE);
        this.primaryField=(String)allFields.get("primaryField", new String("default"));
        this.primaryFieldType=(String)allFields.get("primaryFieldType", new String("VARCHAR"));
        this.primaryOperator=allFields.get("primaryOperator", TargetNode.OPERATOR_EQ);
        this.primaryValue=(String)allFields.get("primaryValue", new String(" "));
        this.closeBracketAfter=allFields.get("closeBracketAfter", false);
        this.openBracketBefore=allFields.get("openBracketBefore", false);
        
        OPERATORS=new String[]{"=", "<>", ">", "<", "LIKE", "NOT LIKE", null, "IS"};
        BSH_OPERATORS=new String[]{"==", "!=", ">", "<", null, null, null, "IS"};
        return;
    }
    
    /** Getter for property openBracketBefore.
     * @return Value of property openBracketBefore.
     */
    public boolean isOpenBracketBefore() {
        return this.openBracketBefore;
    }
    
    /** Setter for property openBracketBefore.
     * @param openBracketBefore New value of property openBracketBefore.
     */
    public void setOpenBracketBefore(boolean openBracketBefore) {
        this.openBracketBefore=openBracketBefore;
    }
    
    /** Getter for property closeBracketAfter.
     * @return Value of property closeBracketAfter.
     */
    public boolean isCloseBracketAfter() {
        return this.closeBracketAfter;
    }
    
    /** Setter for property closeBracketAfter.
     * @param closeBracketAfter New value of property closeBracketAfter.
     */
    public void setCloseBracketAfter(boolean closeBracketAfter) {
        this.closeBracketAfter=closeBracketAfter;
    }
    
    /** Getter for property chainOperator.
     * @return Value of property chainOperator.
     */
    public int getChainOperator() {
        return this.chainOperator;
    }
    
    /** Setter for property chainOperator.
     * @param chainOperator New value of property chainOperator.
     */
    public void setChainOperator(int chainOperator) {
        this.chainOperator=chainOperator;
    }
    
    /** Getter for property primaryOperator.
     * @return Value of property primaryOperator.
     */
    public int getPrimaryOperator() {
        return this.primaryOperator;
    }
    
    /** Getter for property primaryField.
     * @return Value of property primaryField.
     */
    public String getPrimaryField() {
        return this.primaryField;
    }
    
    /** Setter for property primaryField.
     * @param primaryField New value of property primaryField.
     */
    public void setPrimaryField(String primaryField) {
        this.primaryField=primaryField;
    }
    
    /** Getter for property primaryFieldType.
     * @return Value of property primaryFieldType.
     */
    public String getPrimaryFieldType() {
        return this.primaryFieldType;
    }
    
    /** Setter for property primaryFieldType.
     * @param primaryFieldType New value of property primaryFieldType.
     */
    public void setPrimaryFieldType(String primaryFieldType) {
        this.primaryFieldType=primaryFieldType;
    }
    
    /** Getter for property primaryValue.
     * @return Value of property primaryValue.
     */
    public String getPrimaryValue() {
        return this.primaryValue;
    }
    
    /**
     * Setter for property primaryValue.
     * @param primValue 
     */
    public void setPrimaryValue(String primValue) {
        if(this.primaryOperator==TargetNode.OPERATOR_IS) {
            if(!primValue.equals("null") && !primValue.equals("not null")) {
                this.primaryValue=new String("null");
            } else {
                this.primaryValue=primValue;
            }
        } else {
            this.primaryValue=primValue;
        }
    }
}
