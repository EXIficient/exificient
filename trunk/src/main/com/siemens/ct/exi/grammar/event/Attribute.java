/*
 * Copyright (C) 2007-2009 Siemens AG
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

package com.siemens.ct.exi.grammar.event;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.datatype.BuiltIn;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.ExpandedNameComparator;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081103
 */

public class Attribute extends AbstractDatatypeEvent {
	private String namespaceURI;
	private String localName;

	public Attribute(String uri, String localName, ExpandedName valueType,
			Datatype datatype) {
		super("AT", valueType, datatype);
		eventType = EventType.ATTRIBUTE;

		this.namespaceURI = uri == null ? XMLConstants.NULL_NS_URI : uri;
		this.localName = localName;
	}

	public Attribute(String uri, String localName) {
		this(uri, localName, BuiltIn.DEFAULT_VALUE_NAME,
				BuiltIn.DEFAULT_DATATYPE);
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String toString() {
		return "AT({" + namespaceURI + "}" + localName + ")";
	}

	@Override
	public int hashCode() {
		return (eventType.ordinal() ^ namespaceURI.hashCode() ^ localName
				.hashCode());
	}

	public boolean equals(Object obj) {
		if (obj instanceof Attribute) {
			Attribute otherAT = (Attribute) obj;
			return (localName.equals(otherAT.localName) && namespaceURI
					.equals(otherAT.namespaceURI));
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(Event o) {
		if (o instanceof Attribute) {
			// both AT events
			Attribute otherAT = (Attribute) o;
			return ExpandedNameComparator.compare(namespaceURI, localName,
					otherAT.namespaceURI, otherAT.localName);
		} else {
			return super.compareTo(o);
		}
	}

}
