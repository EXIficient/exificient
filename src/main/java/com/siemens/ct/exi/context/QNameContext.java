/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.context;

import java.io.Serializable;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTagGrammar;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class QNameContext implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3603278826401543189L;
	
	final int namespaceUriID;
	final int localNameID;
	final QName qName;
	final int qNameID;
	final String defaultQNameAsString;
	final String defaultPrefix;

	// global element
	StartElement grammarGlobalElement;

	// global grammar attribute (if any)
	Attribute grammarGlobalAttribute;
	
	// type grammar
	SchemaInformedFirstStartTagGrammar typeGrammar;

//	// simple basetype
//	QNameContext simpleBaseType;
//	
//	// simple datatype
//	Datatype simpleDatatype;
//	
//	// simple base datatype
//	Datatype simpleBaseDatatype;

	public QNameContext(int namespaceUriID, int localNameID, QName qName,
			int qNameID) {
		this.namespaceUriID = namespaceUriID;
		this.localNameID = localNameID;
		this.qName = qName;
		switch (namespaceUriID) {
		case 0:
			// "" [empty string]
			this.defaultPrefix = "";
			this.defaultQNameAsString = this.qName.getLocalPart();
			break;
		case 1:
			this.defaultPrefix = "xml";
			this.defaultQNameAsString = defaultPrefix + ":" + this.qName.getLocalPart();
			break;
		case 2:
			this.defaultPrefix = "xsi";
			this.defaultQNameAsString = defaultPrefix + ":" + this.qName.getLocalPart();
			break;
		default:
			this.defaultPrefix = "ns" + namespaceUriID;
			this.defaultQNameAsString = defaultPrefix + ":" + this.qName.getLocalPart();
		}
		this.qNameID = qNameID;
	}

	public QName getQName() {
		return this.qName;
	}

	/**
	 * Returns the default qname as string with either the pre-populated
	 * prefixes or ns&lt;UriID&gt;. e.g.
	 * <p>
	 * 0, "" &rarr; ""
	 * </p>
	 * <p>
	 * 1, "http://www.w3.org/XML/1998/namespace"" &rarr; "xml"
	 * </p>
	 * <p>
	 * 2, "http://www.w3.org/2001/XMLSchema-instance" &rarr; "xsi"
	 * </p>
	 * <p>
	 * 3, "..." &rarr; ns3
	 * </p>
	 * <p>
	 * 4, "..." &rarr; ns4
	 * </p>
	 * 
	 * @return qname as String
	 */
	public String getDefaultQNameAsString() {
		return defaultQNameAsString;
	}
	
	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public int getQNameID() {
		return this.qNameID;
	}

	public int getLocalNameID() {
		return localNameID;
	}

	public String getLocalName() {
		return qName.getLocalPart();
	}
	
	public void setGlobalStartElement(StartElement grammarGlobalElement) {
		this.grammarGlobalElement = grammarGlobalElement;
	}
	
	public StartElement getGlobalStartElement() {
		return grammarGlobalElement;
	}

	public void setGlobalAttribute(Attribute grammarGlobalAttribute) {
		this.grammarGlobalAttribute = grammarGlobalAttribute;
	}

	public Attribute getGlobalAttribute() {
		return grammarGlobalAttribute;
	}
	
	public void setTypeGrammar(SchemaInformedFirstStartTagGrammar typeGrammar) {
		this.typeGrammar = typeGrammar;
	}

	// null if none
	public SchemaInformedFirstStartTagGrammar getTypeGrammar() {
		return this.typeGrammar;
	}



//	/**
//	 * Set simple base-type
//	 * 
//	 */
//	public void setSimpleBaseType(QNameContext simpleBaseType) {
//		this.simpleBaseType = simpleBaseType;
//	}
//	
//	/**
//	 * Returns simple base-type
//	 * 
//	 * @return base-type
//	 */
//	public QNameContext getSimpleBaseType() {
//		return this.simpleBaseType;
//	}
//	
//	public void setSimpleDatatype(Datatype simpleDatatype) {
//		this.simpleDatatype = simpleDatatype;
//	}
//	
//	public Datatype getSimpleDatatype() {
//	return this.simpleDatatype;
//}
//	
//	public void setSimpleBaseDatatype(Datatype simpleBaseDatatype) {
//		this.simpleBaseDatatype = simpleBaseDatatype;
//	}
//	
//	public Datatype getSimpleBaseDatatype() {
//		return this.simpleBaseDatatype;
//	}

	
	

	public int getNamespaceUriID() {
		return this.namespaceUriID;
	}

	public String getNamespaceUri() {
		return this.qName.getNamespaceURI();
	}

	protected int compareTo(String localName) {
		return this.getQName().getLocalPart().compareTo(localName);
	}

	public String toString() {
		return "{" + namespaceUriID + "}" + localNameID + ","
				+ this.getLocalName();
	}

	@Override
	public final boolean equals(Object o) {
		if (o instanceof QNameContext) {
			QNameContext other = (QNameContext) o;
			// return (other.localNameID == this.localNameID && other
			//		.getNamespaceUriID() == this.getNamespaceUriID());
			return (other.qNameID == this.qNameID);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		// return getNamespaceUriID() ^ localNameID;
		return qNameID;
	}

}
