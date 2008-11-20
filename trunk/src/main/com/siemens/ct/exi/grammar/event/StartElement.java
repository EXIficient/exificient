/*
 * Copyright (C) 2007, 2008 Siemens AG
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

import com.siemens.ct.exi.util.ExpandedNameComparable;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081103
 */

public class StartElement extends AbstractEvent implements
		ExpandedNameComparable {
	private String namespaceURI;

	private String localPart;

	public StartElement(String uri, String localName) {
		super("SE");
		eventType = EventType.START_ELEMENT;

		this.namespaceURI = uri == null ? XMLConstants.NULL_NS_URI : uri;
		this.localPart = localName;
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	public String getLocalPart() {
		return localPart;
	}

	public void setLocalPart(String localPart) {
		this.localPart = localPart;
	}

	public String toString() {
		return "SE(" + namespaceURI + ":" + localPart + ")";
	}

	@Override
	public int hashCode() {
		return (eventType.ordinal() ^ namespaceURI.hashCode() ^ localPart
				.hashCode());
	}

	public boolean equals(Object obj) {
		if (obj instanceof StartElement) {
			StartElement otherSE = (StartElement) obj;
			return (localPart.equals(otherSE.localPart) && namespaceURI
					.equals(otherSE.namespaceURI));
		} else {
			return false;
		}
	}

	public int compareTo(String namespace, String localName) {
		// first local-part and then uri
		final int c1 = localPart.compareTo(localName);
		return (c1 == 0 ? namespaceURI.compareTo(namespace) : c1);
	}

}
