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

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

public class StartElementNS extends AbstractEvent {

	private String namespaceURI;

	public StartElementNS(String uri) {
		super(EventType.START_ELEMENT_NS);

		this.namespaceURI = uri == null ? XMLConstants.NULL_NS_URI : uri;
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	public String toString() {
		return super.toString() + "(" + namespaceURI + ":*)";
	}

	@Override
	public int hashCode() {
		// return (eventType.ordinal() ^ namespaceURI.hashCode());
		return (namespaceURI.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof StartElementNS) {
			StartElementNS otherSE = (StartElementNS) obj;
			return (namespaceURI.equals(otherSE.namespaceURI));
		} else {
			return false;
		}
	}
}
