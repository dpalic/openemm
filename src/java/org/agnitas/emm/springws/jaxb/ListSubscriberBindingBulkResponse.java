//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.11.15 at 07:29:37 PM EET 
//


package org.agnitas.emm.springws.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="items">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="item" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;choice>
 *                             &lt;element ref="{http://agnitas.org/ws/schemas}ListSubscriberBindingResponse"/>
 *                             &lt;element ref="{http://agnitas.org/ws/schemas}Error"/>
 *                           &lt;/choice>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "ListSubscriberBindingBulkResponse")
public class ListSubscriberBindingBulkResponse {

    @XmlElement(required = true)
    protected ListSubscriberBindingBulkResponse.Items items;

    /**
     * Gets the value of the items property.
     * 
     * @return
     *     possible object is
     *     {@link ListSubscriberBindingBulkResponse.Items }
     *     
     */
    public ListSubscriberBindingBulkResponse.Items getItems() {
        return items;
    }

    /**
     * Sets the value of the items property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListSubscriberBindingBulkResponse.Items }
     *     
     */
    public void setItems(ListSubscriberBindingBulkResponse.Items value) {
        this.items = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="item" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;choice>
     *                   &lt;element ref="{http://agnitas.org/ws/schemas}ListSubscriberBindingResponse"/>
     *                   &lt;element ref="{http://agnitas.org/ws/schemas}Error"/>
     *                 &lt;/choice>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "item"
    })
    public static class Items {

        @XmlElement(required = true)
        protected List<ListSubscriberBindingBulkResponse.Items.Item> item;

        /**
         * Gets the value of the item property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the item property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ListSubscriberBindingBulkResponse.Items.Item }
         * 
         * 
         */
        public List<ListSubscriberBindingBulkResponse.Items.Item> getItem() {
            if (item == null) {
                item = new ArrayList<ListSubscriberBindingBulkResponse.Items.Item>();
            }
            return this.item;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;choice>
         *         &lt;element ref="{http://agnitas.org/ws/schemas}ListSubscriberBindingResponse"/>
         *         &lt;element ref="{http://agnitas.org/ws/schemas}Error"/>
         *       &lt;/choice>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "listSubscriberBindingResponse",
            "error"
        })
        public static class Item {

            @XmlElement(name = "ListSubscriberBindingResponse")
            protected ListSubscriberBindingResponse listSubscriberBindingResponse;
            @XmlElement(name = "Error")
            protected String error;

            /**
             * Gets the value of the listSubscriberBindingResponse property.
             * 
             * @return
             *     possible object is
             *     {@link ListSubscriberBindingResponse }
             *     
             */
            public ListSubscriberBindingResponse getListSubscriberBindingResponse() {
                return listSubscriberBindingResponse;
            }

            /**
             * Sets the value of the listSubscriberBindingResponse property.
             * 
             * @param value
             *     allowed object is
             *     {@link ListSubscriberBindingResponse }
             *     
             */
            public void setListSubscriberBindingResponse(ListSubscriberBindingResponse value) {
                this.listSubscriberBindingResponse = value;
            }

            /**
             * Gets the value of the error property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getError() {
                return error;
            }

            /**
             * Sets the value of the error property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setError(String value) {
                this.error = value;
            }

        }

    }

}