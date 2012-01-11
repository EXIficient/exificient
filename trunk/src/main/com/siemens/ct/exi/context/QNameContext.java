/*
 * Copyright (C) 2007-2011 Siemens AG
 *
 * This program and its interfaces are free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.siemens.ct.exi.context;

import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.SchemaInformedFirstStartTagRule;

public class QNameContext {

	final int namespaceUriID;
	final int localNameID;
	final QName qName;
	final int qNameID;
	final String defaultQNameAsString;

	// global element
	StartElement grammarGlobalElement;

	// global grammar attribute (if any)
	Attribute grammarGlobalAttribute;

	// type grammar
	SchemaInformedFirstStartTagRule typeGrammar;

	// (direct) simple subtypes
	List<QNameContext> simpleTypeSubtypes;

	public QNameContext(int namespaceUriID, int localNameID, QName qName,
			int qNameID) {
		this.namespaceUriID = namespaceUriID;
		this.localNameID = localNameID;
		this.qName = qName;
		switch (namespaceUriID) {
		case 0:
			// "" [empty string]
			this.defaultQNameAsString = this.qName.getLocalPart();
			break;
		case 1:
			this.defaultQNameAsString = "xml:" + this.qName.getLocalPart();
			break;
		case 2:
			this.defaultQNameAsString = "xsi:" + this.qName.getLocalPart();
			break;
		default:
			this.defaultQNameAsString = "ns" + namespaceUriID + ":"
					+ this.qName.getLocalPart();
		}
		this.qNameID = qNameID;
	}

	public QName getQName() {
		return this.qName;
	}

	/**
	 * Returns the default qname as string with either the pre-populated
	 * prefixes or ns<UriID>. e.g.
	 * <p>
	 * 0, "" --> ""
	 * </p>
	 * <p>
	 * 1, "http://www.w3.org/XML/1998/namespace"" --> "xml"
	 * </p>
	 * <p>
	 * 2, "http://www.w3.org/2001/XMLSchema-instance" --> "xsi"
	 * </p>
	 * <p>
	 * 3, "..." --> ns3
	 * </p>
	 * <p>
	 * 4, "..." --> ns4
	 * </p>
	 * 
	 * @return
	 */
	public String getDefaultQNameAsString() {
		return defaultQNameAsString;
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

	public void setTypeGrammar(SchemaInformedFirstStartTagRule typeGrammar) {
		this.typeGrammar = typeGrammar;
	}

	// null if none
	public SchemaInformedFirstStartTagRule getTypeGrammar() {
		return this.typeGrammar;
	}

	/**
	 * Returns (direct) simple types in type hierarchy
	 * 
	 * @param type
	 * @return list of named sub-types or null
	 */
	public List<QNameContext> getSimpleTypeSubtypes() {
		return simpleTypeSubtypes;
	}

	public void setSimpleTypeSubtypes(List<QNameContext> simpleTypeSubtypes) {
		this.simpleTypeSubtypes = simpleTypeSubtypes;
	}

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
			return (other.localNameID == this.localNameID && other
					.getNamespaceUriID() == this.getNamespaceUriID());
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return getNamespaceUriID() ^ localNameID;
	}

}
