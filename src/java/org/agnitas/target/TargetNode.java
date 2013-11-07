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

package org.agnitas.target;

/**
 *
 * @author  mhe
 */
public abstract class TargetNode {
    
    public static final int CHAIN_OPERATOR_NONE = 0;
    public static final int CHAIN_OPERATOR_AND = 1;
    public static final int CHAIN_OPERATOR_OR = 2;
    
    public static final int OPERATOR_EQ = 1;
    public static final int OPERATOR_NEQ = 2;
    public static final int OPERATOR_GT = 3;
    public static final int OPERATOR_LT = 4;
    public static final int OPERATOR_LIKE = 5;
    public static final int OPERATOR_NLIKE = 6;
    public static final int OPERATOR_MOD = 7;
    public static final int OPERATOR_IS = 8;
    public static final int OPERATOR_LT_EQ = 9;
    public static final int OPERATOR_GT_EQ = 10;
    
    public static final String[] ALL_OPERATORS={"=", "<>", ">", "<", "LIKE", "NOT LIKE", "MOD", "IS", "<=", ">="};

//    public String[] OPERATORS;// ={"=", "<>", ">", "<", "LIKE", "NOT LIKE", "MOD"};
    public String[] OPERATORS=new String[]{"=", "<>", ">", "<","LIKE", "NOT LIKE", null, "IS", "<=", ">="};
    public String[] BSH_OPERATORS;
    
    /**
     * Initializes the arrays OPERATORS and BSH_OPERATORS 
     */
    protected abstract void initializeOperatorLists();
    
      /** 
       * Getter for property openBracketBefore.
       *
     * @return Value of property openBracketBefore.
     */
    public abstract boolean isOpenBracketBefore();
    
    /** 
     * Setter for property openBracketBefore.
     *
     * @param openBracketBefore New value of property openBracketBefore.
     */
    public abstract void setOpenBracketBefore(boolean openBracketBefore);
    
    /**
     * Getter for property closeBracketAfter.
     *
     * @return Value of property closeBracketAfter.
     */
    public abstract boolean isCloseBracketAfter();
    
    /**
     * Setter for property closeBracketAfter.
     *
     * @param closeBracketAfter New value of property closeBracketAfter.
     */
    public abstract void setCloseBracketAfter(boolean closeBracketAfter);
    
    /**
     * Getter for property chainOperator.
     *
     * @return Value of property chainOperator.
     */
    public abstract int getChainOperator();
    
    /**
     * Setter for property chainOperator.
     *
     * @param chainOperator New value of property chainOperator.
     */
    public abstract void setChainOperator(int chainOperator);
    
    /**
     * Generates sql.
     */
    public abstract String generateSQL();
    
    /**
     * Generates bsh
     */
    public abstract String generateBsh();
    
    /** 
     * Getter for property primaryOperator.
     *
     * @return Value of property primaryOperator.
     */
    public abstract int getPrimaryOperator();
    
    /**
     * Setter for property primaryOperator.
     *
     * @param primaryOperator New value of property primaryOperator.
     */
    public abstract void setPrimaryOperator(int primaryOperator);
    
    /**
     * Getter for property primaryField.
     *
     * @return Value of property primaryField.
     */
    public abstract String getPrimaryField();
    
    /**
     * Setter for property primaryField.
     *
     * @param primaryField New value of property primaryField.
     */
    public abstract void setPrimaryField(String primaryField);
    
    /**
     * Getter for property primaryFieldType.
     *
     * @return Value of property primaryFieldType.
     */
    public abstract String getPrimaryFieldType();
    
    /**
     * Setter for property primaryFieldType.
     *
     * @param primaryFieldType New value of property primaryFieldType.
     */
    public abstract void setPrimaryFieldType(String primaryFieldType);
    
    /**
     * Getter for property primaryValue.
     *
     * @return Value of property primaryValue.
     */
    public abstract String getPrimaryValue();
    
    /**
     * Setter for property primaryValue.
     *
     * @param primaryValue New value of property primaryValue.
     */
    public abstract void setPrimaryValue(String primaryValue);   
}
