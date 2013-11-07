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

package org.agnitas.target.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.agnitas.target.TargetNode;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;

/**
 *
 * @author  mhe
 */
public class TargetNodeDate extends TargetNode implements Serializable {

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

    /** Holds value of property dateFormat. */
    protected String dateFormat;

    private static final long serialVersionUID = -6885016603800628942L;

    /** Creates a new instance of TargetNodeString */
    public TargetNodeDate() {
    	initializeOperatorLists();
        if(AgnUtils.isOracleDB()) {
        	dateFormat=new String("yyyymmdd"); // default format
        } else {
        	dateFormat=new String("%Y%m%d"); // default format
        }
    }

    @Override
    protected void initializeOperatorLists() {
        OPERATORS=new String[]{"=", "<>", ">", "<", null, null, null, "IS", "<=", ">="};
        BSH_OPERATORS=new String[]{"==", "!=", ">", "<", null, null, null, "IS", "<=", ">="};
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

        if(this.primaryOperator==TargetNode.OPERATOR_IS) {
            if(!this.primaryField.equals(AgnUtils.getSQLCurrentTimestampName())) {
                tmpSQL.append("cust.");
            }
            tmpSQL.append(this.primaryField);
            tmpSQL.append(" ");
            tmpSQL.append(this.OPERATORS[this.primaryOperator-1]);
            tmpSQL.append(" ");
            tmpSQL.append(SafeString.getSQLSafeString(this.primaryValue));
        } else {
            String fieldName="";

            if(this.primaryField.equals(AgnUtils.getSQLCurrentTimestampName())) {
                fieldName=this.primaryField;
            } else {
                fieldName="cust."+this.primaryField;
            }
            tmpSQL.append(AgnUtils.sqlDateString(fieldName, this.dateFormat)+" ");
            tmpSQL.append(this.OPERATORS[this.primaryOperator-1]);
           
            if( this.primaryValue.contains("now()") && AgnUtils.isMySQLDB()) {
            	tmpSQL.append(" " + AgnUtils.sqlDateString(this.primaryValue, this.dateFormat));
            }
            
            else {
            	if(this.primaryValue.startsWith(AgnUtils.getSQLCurrentTimestampName()) && AgnUtils.isOracleDB() ) {
                    tmpSQL.append(" " + AgnUtils.sqlDateString(this.primaryValue, this.dateFormat));
                }
                
                
                else {
                    //tmpSQL.append(" " + AgnUtils.sqlDateString(this.primaryValue, this.dateFormat));
                    tmpSQL.append("'"+this.primaryValue+"' ");
                }
            }
            
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
            case TargetNode.OPERATOR_IS:
            	if( AgnUtils.isOracleDB() ) {
                	tmpBsh.append(this.primaryField.toUpperCase());
                } else {                
                	tmpBsh.append(this.primaryField);
                }
                if(this.primaryValue.startsWith("null")) {
                    tmpBsh.append("==");
                } else {
                    tmpBsh.append("!=");
                }
                tmpBsh.append("null ");
                break;

            default:
                tmpBsh.append("AgnUtils.compareString(");
                tmpBsh.append("AgnUtils.formatDate(");
                if( AgnUtils.isOracleDB() ) {
                	tmpBsh.append(this.primaryField.toUpperCase());
                } else {                
                	tmpBsh.append(this.primaryField);
                }
                tmpBsh.append(", \"");
                tmpBsh.append(this.dateFormat.replace('m', 'M')); // from sql-style to java-style
                tmpBsh.append("\") ");
                tmpBsh.append(", ");
                if(this.primaryValue.startsWith("sysdate") || this.primaryValue.contains("now()") ) {
                    tmpBsh.append("AgnUtils.formatDate(");
                    tmpBsh.append("AgnUtils.getSysdate(\"");
                    tmpBsh.append(this.primaryValue);
                    tmpBsh.append("\"), \"");
                    tmpBsh.append(this.dateFormat.replace('m', 'M'));
                    tmpBsh.append("\") ");
                } 
                
                else {
                    tmpBsh.append(" \"");
                    tmpBsh.append(SafeString.getSQLSafeString(this.primaryValue));
                    tmpBsh.append("\"");
                }
                tmpBsh.append(", ");
                tmpBsh.append(Integer.toString(this.primaryOperator-1));
                tmpBsh.append(") ");
        }

        if(this.closeBracketAfter) {
            tmpBsh.append(")");
        }

        return tmpBsh.toString();
    }

    /** Getter for property dateFormat.
     * @return Value of property dateFormat.
     */
    public String getDateFormat() {
        return this.dateFormat;
    }

    /** Setter for property dateFormat.
     * @param dateFormat New value of property dateFormat.
     */
    public void setDateFormat(String dateFormat) {
        if(dateFormat!=null) {
            this.dateFormat = dateFormat;
        }
    }

    public void setPrimaryOperator(int primOp) {
        if(primOp==TargetNode.OPERATOR_LIKE)
            primOp=TargetNode.OPERATOR_EQ;

        if(primOp==TargetNode.OPERATOR_NLIKE)
            primOp=TargetNode.OPERATOR_NEQ;

        if(primOp==TargetNode.OPERATOR_MOD)
            primOp=TargetNode.OPERATOR_EQ;

        this.primaryOperator=primOp;
    }

    public void setPrimaryValue(String primVal) {
        if(this.primaryOperator==TargetNode.OPERATOR_IS) {
            if(!primVal.equals("null") && !primVal.equals("not null")) {
                this.primaryValue=new String("null");
            } else {
                this.primaryValue=primVal;
            }
        } else {
            primVal=primVal.toLowerCase();
            if(primVal.startsWith("sysdate")) { // if special-value "sysdate", parse if it is correct
                if(!primVal.equals("sysdate")) {
                    if(primVal.length()>=9) {
                        char operator=primVal.charAt(7);
                        if(operator=='+' || operator=='-') { // check if illegal operator
                            int value=0;
                            try {
                                value=Integer.parseInt(primVal.substring(8));
                            } catch (Exception e) {
                                value=0;
                            }
                            if(value==0) { // not a valid operand, set to default
                                primVal=new String("sysdate");
                            }
                        } else {
                            primVal=new String("sysdate");
                        }
                    } else { // is too short
                        primVal=new String("sysdate");
                    }
                }
            }
            this.primaryValue=new String(primVal);
        }
    }

    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;

        allFields=in.readFields();
        this.chainOperator=allFields.get("chainOperator", TargetNode.CHAIN_OPERATOR_NONE);
        this.primaryField=(String)allFields.get("primaryField", new String("default"));
        this.primaryFieldType=(String)allFields.get("primaryFieldType", new String("DATE"));
        this.primaryOperator=allFields.get("primaryOperator", TargetNode.OPERATOR_EQ);
        this.primaryValue=(String)allFields.get("primaryValue", new String("0"));
        this.dateFormat=(String)allFields.get("dateFormat", new String("yyyymmdd"));
        this.closeBracketAfter=allFields.get("closeBracketAfter", false);
        this.openBracketBefore=allFields.get("openBracketBefore", false);

        initializeOperatorLists();
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
}
