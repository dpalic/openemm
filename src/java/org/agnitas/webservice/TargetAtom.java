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

/**
 * TargetAtom.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package org.agnitas.webservice;

public class TargetAtom  implements java.io.Serializable {
    /**
     * Operator which should be used to connect this Atom in the list of Atoms. 
     * 
     * Possible values:
     * 0 == no operator
     * 1 == logical AND
     * 2 == logical OR
     * 
     * On the first TargetAtom in the list, this value must be 0, on the others 1 or 2.
     */
    private int chainOperator;
    /**
     * Fieldname in Database to be evaluated
     */
    private java.lang.String primaryField;
    /**
     * Operator for comparison.
     * Possible values:
     * 
     * 1 == equal
     * 2 == not equal
     * 3 == greater than
     * 4 == less than
     * 5 == SQL-Like (only for Alphanumerical DB-Fields)
     * 6 == SQL-Not-Like (only for Alphanumerical DB-Fields)
     */
    private int primaryOperator;
    /**
     * Value to be compared with primaryField
     */
    private java.lang.String primaryValue;
    /**
     * not used
     */
    private int secondaryOperator;
    /**
     * not used
     */
    private java.lang.String secondaryValue;
    /**
     * Set a opening bracket before this TargetAtom
     */
    private boolean closeBraketBefore;
    /**
     * Set a closing bracket after this TargetAtom
     */
    private boolean closeBraketAfter;
    /**
     * Date-Format used for Date-Comparision, must be a valid SQL-Date-Formatstring
     */
    private java.lang.String dateFormat;

    public TargetAtom() {
    }

    /**
     * 
     * @return 
     */
    public int getChainOperator() {
        return chainOperator;
    }

    /**
     * 
     * @param chainOperator 
     */
    public void setChainOperator(int chainOperator) {
        this.chainOperator = chainOperator;
    }

    /**
     * 
     * @return 
     */
    public java.lang.String getPrimaryField() {
        return primaryField;
    }

    /**
     * 
     * @param primaryField Field name in Database
     */
    public void setPrimaryField(java.lang.String primaryField) {
        this.primaryField = primaryField;
    }

    /**
     * 
     * @return 
     */
    public int getPrimaryOperator() {
        return primaryOperator;
    }

    /**
     * 
     * @param primaryOperator 
     */
    public void setPrimaryOperator(int primaryOperator) {
        this.primaryOperator = primaryOperator;
    }

    /**
     * 
     * @return 
     */
    public java.lang.String getPrimaryValue() {
        return primaryValue;
    }

    /**
     * 
     * @param primaryValue 
     */
    public void setPrimaryValue(java.lang.String primaryValue) {
        this.primaryValue = primaryValue;
    }

    /**
     * 
     * @return 
     */
    public int getSecondaryOperator() {
        return secondaryOperator;
    }

    /**
     * 
     * @param secondaryOperator 
     */
    public void setSecondaryOperator(int secondaryOperator) {
        this.secondaryOperator = secondaryOperator;
    }

    /**
     * 
     * @return 
     */
    public java.lang.String getSecondaryValue() {
        return secondaryValue;
    }

    /**
     * 
     * @param secondaryValue 
     */
    public void setSecondaryValue(java.lang.String secondaryValue) {
        this.secondaryValue = secondaryValue;
    }

    /**
     * 
     * @return 
     */
    public boolean isCloseBraketBefore() {
        return closeBraketBefore;
    }

    /**
     * 
     * @param closeBraketBefore 
     */
    public void setCloseBraketBefore(boolean closeBraketBefore) {
        this.closeBraketBefore = closeBraketBefore;
    }

    /**
     * 
     * @return 
     */
    public boolean isCloseBraketAfter() {
        return closeBraketAfter;
    }

    /**
     * 
     * @param closeBraketAfter 
     */
    public void setCloseBraketAfter(boolean closeBraketAfter) {
        this.closeBraketAfter = closeBraketAfter;
    }

    /**
     * 
     * @return 
     */
    public java.lang.String getDateFormat() {
        return dateFormat;
    }

    /**
     * 
     * @param dateFormat 
     */
    public void setDateFormat(java.lang.String dateFormat) {
        this.dateFormat = dateFormat;
    }

    private java.lang.Object __equalsCalc = null;
    /**
     * 
     * @param obj 
     * @return 
     */
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TargetAtom)) return false;
        TargetAtom other = (TargetAtom) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.chainOperator == other.getChainOperator() &&
            ((this.primaryField==null && other.getPrimaryField()==null) || 
             (this.primaryField!=null &&
              this.primaryField.equals(other.getPrimaryField()))) &&
            this.primaryOperator == other.getPrimaryOperator() &&
            ((this.primaryValue==null && other.getPrimaryValue()==null) || 
             (this.primaryValue!=null &&
              this.primaryValue.equals(other.getPrimaryValue()))) &&
            this.secondaryOperator == other.getSecondaryOperator() &&
            ((this.secondaryValue==null && other.getSecondaryValue()==null) || 
             (this.secondaryValue!=null &&
              this.secondaryValue.equals(other.getSecondaryValue()))) &&
            this.closeBraketBefore == other.isCloseBraketBefore() &&
            this.closeBraketAfter == other.isCloseBraketAfter() &&
            ((this.dateFormat==null && other.getDateFormat()==null) || 
             (this.dateFormat!=null &&
              this.dateFormat.equals(other.getDateFormat())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    /**
     * 
     * @return 
     */
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getChainOperator();
        if (getPrimaryField() != null) {
            _hashCode += getPrimaryField().hashCode();
        }
        _hashCode += getPrimaryOperator();
        if (getPrimaryValue() != null) {
            _hashCode += getPrimaryValue().hashCode();
        }
        _hashCode += getSecondaryOperator();
        if (getSecondaryValue() != null) {
            _hashCode += getSecondaryValue().hashCode();
        }
        _hashCode += new Boolean(isCloseBraketBefore()).hashCode();
        _hashCode += new Boolean(isCloseBraketAfter()).hashCode();
        if (getDateFormat() != null) {
            _hashCode += getDateFormat().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TargetAtom.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:agnitas-webservice", "TargetAtom"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chainOperator");
        elemField.setXmlName(new javax.xml.namespace.QName("", "chainOperator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryField");
        elemField.setXmlName(new javax.xml.namespace.QName("", "primaryField"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryOperator");
        elemField.setXmlName(new javax.xml.namespace.QName("", "primaryOperator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "primaryValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secondaryOperator");
        elemField.setXmlName(new javax.xml.namespace.QName("", "secondaryOperator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secondaryValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "secondaryValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("closeBraketBefore");
        elemField.setXmlName(new javax.xml.namespace.QName("", "closeBraketBefore"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("closeBraketAfter");
        elemField.setXmlName(new javax.xml.namespace.QName("", "closeBraketAfter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateFormat");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dateFormat"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
