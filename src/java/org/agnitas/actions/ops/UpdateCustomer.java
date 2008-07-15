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

package org.agnitas.actions.ops;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agnitas.actions.ActionOperation;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.SafeString;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Martin Helff
 */
public class UpdateCustomer extends ActionOperation implements Serializable {
    
    static final long serialVersionUID = -5598100419105432642L;
    
    protected static final int TYPE_INCREMENT_BY=1;
    protected static final int TYPE_DECREMENT_BY=2;
    protected static final int TYPE_SET_VALUE=3;
    
    // protected String updateStatement;
    
    /** 
     * Holds value of property columnName. 
     */
    protected String columnName;
    
    /**
     * Holds value of property updateType. 
     */
    protected int updateType;
    
    /**
     * Holds value of property updateValue. 
     */
    protected String updateValue;
    
    /**
     * Holds value of property columnType.
     */
    protected String columnType;
    
    /**
     * Creates new ActionOperationUpdateCustomer 
     */
    public UpdateCustomer() {
        columnName=new String("gender");
        updateType=TYPE_INCREMENT_BY;
        updateValue=new String("0");
    }
    
    /**
     * Declaration of decrement operator, increment operator
     * and equal operator depending on the column type
     * ...
     */
    public String buildSQL(String uValue) {
        // boolean exitValue=true;
        StringBuffer tmpStatement=new StringBuffer("");
        double tmpNum=0.0;
        String decOp=null;
        String incOp=null;
        String eqOp=null;
        
        if(this.columnType.equalsIgnoreCase("INTEGER") || this.columnType.equalsIgnoreCase("DOUBLE")) {
            decOp=" - ";
            incOp=" + ";
            eqOp=" = ";
        }
        
        if(this.columnType.equalsIgnoreCase("CHAR") || this.columnType.equalsIgnoreCase("VARCHAR")) {
            decOp=" - ";
            incOp=" || ";
            eqOp=" = ";
        }
        
        if(this.columnType.equalsIgnoreCase("DATE")) {
            decOp=" - ";
            incOp=" + ";
            eqOp=" = ";
        }
        
        tmpStatement.append(this.columnName);
        switch(this.updateType) {
            case TYPE_INCREMENT_BY:
                tmpStatement.append("="+this.columnName+incOp);
                break;
                
            case TYPE_DECREMENT_BY:
                tmpStatement.append("="+this.columnName+decOp);
                break;
                
            case TYPE_SET_VALUE:
                tmpStatement.append(eqOp);
                break;
        }
        
        if(this.columnType.equalsIgnoreCase("INTEGER") || this.columnType.equalsIgnoreCase("DOUBLE")) {
            try {
                tmpNum=Double.parseDouble(uValue);
            } catch (Exception e) {
                tmpNum=0.0;
            }
            tmpStatement.append(Double.toString(tmpNum));
        }
        
        if(this.columnType.equalsIgnoreCase("CHAR") || this.columnType.equalsIgnoreCase("VARCHAR")) {
            tmpStatement.append("'");
            tmpStatement.append(SafeString.getSQLSafeString(uValue));
            tmpStatement.append("'");
        }
        
        if(this.columnType.equalsIgnoreCase("DATE")) {
            if(this.updateType==TYPE_INCREMENT_BY || this.updateType==TYPE_DECREMENT_BY) {
                try {
                    tmpNum=Double.parseDouble(uValue);
                } catch (Exception e) {
                    tmpNum=0.0;
                }
                tmpStatement.append(Double.toString(tmpNum));
            } else {
                if(uValue.startsWith("sysdate")) {
                    tmpStatement.append(AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName());
                } else {
                    tmpStatement.append("'"+SafeString.getSQLSafeString(uValue)+"'");
                }
            }
        }
        return tmpStatement.toString();
    }
    
    /** Getter for property columnName.
     *
     * @return Value of property columnName.
     */
    public String getColumnName() {
        return columnName;
    }
    
    /** Setter for property columnName.
     *
     * @param columnName New value of property columnName.
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    
    /** Getter for property updateType.
     *
     * @return Value of property updateType.
     */
    public int getUpdateType() {
        return updateType;
    }
    
    /** Setter for property updateType.
     *
     * @param updateType New value of property updateType.
     */
    public void setUpdateType(int updateType) {
        if(updateType<1 || updateType>3) {
            updateType=1;
        }
         this.updateType = updateType;

    }
    
    /** Getter for property updateValue.
     *
     * @return Value of property updateValue.
     */
    public String getUpdateValue() {
        return updateValue;
    }
    
    /** Setter for property updateValue.
     *
     * @param updateValue New value of property updateValue.
     */
    public void setUpdateValue(String updateValue) {
        this.updateValue = updateValue;
    }
    
    /**
     * Reads an Object and puts the read fields into allFields
     * Gets columnName, updateType, updateValue and columnType from allFields
     * throws IOException or ClassNotFoundException
     *
     * @param in inputstream from Object
     */
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        
        allFields=in.readFields();
        this.columnName=(String)allFields.get("columnName", new String("default"));
        this.updateType=allFields.get("updateType", 1);
        this.updateValue=(String)allFields.get("updateValue", new String("0"));
        this.columnType=(String)allFields.get("columnType", new String(""));
    }
    
    /**
     * Checks if customer id is filled
     * Builds an UPDATE-statement for a customer
     * Tries to execute this SQL-statement
     *
     * @return true==sucess
     * false=error
     * @param con 
     * @param companyID 
     * @param params HashMap containing all available informations
     */
    public boolean executeOperation(ApplicationContext con, int companyID, HashMap params) {
        int customerID=0;
        Integer tmpNum=null;
        boolean exitValue=true;
        JdbcTemplate tmpl=AgnUtils.getJdbcTemplate(con);
        String sql="";
        String uStatement=null;
        
        if(params.get("customerID")==null) {
            return false;
        }
        
        tmpNum=(Integer)params.get("customerID");
        customerID=tmpNum.intValue();
        
        uStatement=this.buildSQL(generateUpdateValue(params));
        
        sql="UPDATE customer_" + companyID + "_tbl SET change_date="+AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName()+", " +
                uStatement + " WHERE customer_id=" + customerID;
        try {
            tmpl.execute(sql);
        } catch (Exception e) {
        	AgnUtils.sendExceptionMail("SQL: "+sql, e);
            AgnUtils.logger().error("executeOperation: "+e);
            AgnUtils.logger().error("SQL: "+sql);
            exitValue=false;
        }
        return exitValue;
    }
    
    /**
     * Generates the values for the update
     *
     * @param params HashMap containing all available informations
     */
    protected String generateUpdateValue(HashMap params) {
        Matcher aMatcher=null;
        Pattern aRegExp=Pattern.compile("##[^#]+##");
        StringBuffer aBuf=new StringBuffer(this.updateValue);
        String tmpString=null;
        String tmpString2=null;
        
        try {
            // aRegExp=new RE("##[^#]+##");
            aMatcher=aRegExp.matcher(aBuf);
            while(aMatcher.find()) {
                tmpString=aBuf.toString().substring(aMatcher.start()+2, aMatcher.end()-2);
                tmpString2=new String("");
                if(params.get(tmpString)!=null) {
                    tmpString2=params.get(tmpString).toString();
                }
                aBuf.replace(aMatcher.start(), aMatcher.end(), tmpString2);
                aMatcher=aRegExp.matcher(aBuf);
            }
        } catch (Exception e) {
            AgnUtils.logger().error("generateUpdateValue: "+e);
        }
        return aBuf.toString();
    }
    
    /**
     * Getter for property columnType.
     *
     * @return Value of property columnType.
     */
    public String getColumnType() {
        return this.columnType;
    }
    
    /**
     * Setter for property columnType.
     *
     * @param columnType New value of property columnType.
     */
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
    
    /**
     * Getter for property nameType.
     *
     * @return Value of property nameType.
     */
    public String getNameType() {
        return new String(this.columnName+"#"+this.columnType);
    }
    
    /**
     * Setter for property nameType.
     *
     * @param nameType New value of property nameType.
     */
    public void setNameType(String nameType) {
        this.columnType = nameType.substring(nameType.indexOf('#')+1);
        this.columnName = nameType.substring(0, nameType.indexOf('#'));
    }
}
