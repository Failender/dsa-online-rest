//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.11.26 at 12:11:01 PM CET 
//


package de.failender.heldensoftware.xml.datenxml;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element ref="{}nahkampfwaffe" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute getName="inbenutzung" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "nahkampfwaffe"
})
@XmlRootElement(name = "nahkampfwaffen")
public class Nahkampfwaffen {

    protected List<Nahkampfwaffe> nahkampfwaffe;
    @XmlAttribute(name = "inbenutzung", required = true)
    protected boolean inbenutzung;

    /**
     * Gets the value of the nahkampfwaffe property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nahkampfwaffe property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNahkampfwaffe().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Nahkampfwaffe }
     * 
     * 
     */
    public List<Nahkampfwaffe> getNahkampfwaffe() {
        if (nahkampfwaffe == null) {
            nahkampfwaffe = new ArrayList<Nahkampfwaffe>();
        }
        return this.nahkampfwaffe;
    }

    /**
     * Gets the value of the inbenutzung property.
     * 
     */
    public boolean isInbenutzung() {
        return inbenutzung;
    }

    /**
     * Sets the value of the inbenutzung property.
     * 
     */
    public void setInbenutzung(boolean value) {
        this.inbenutzung = value;
    }

}
