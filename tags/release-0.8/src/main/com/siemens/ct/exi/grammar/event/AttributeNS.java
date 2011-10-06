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

package com.siemens.ct.exi.grammar.event;

import com.siemens.ct.exi.EnhancedNamespaceURI;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class AttributeNS extends AbstractEvent {

	private static final long serialVersionUID = 6004967457126269590L;

	protected final EnhancedNamespaceURI eNamespaceURI;
	protected final String namespaceURI;
	protected int namespaceUriID;

	public AttributeNS(EnhancedNamespaceURI uri) {
		super(EventType.ATTRIBUTE_NS);

		this.eNamespaceURI = uri;
		this.namespaceURI = uri.getNamespaceURI();
	}

	public EnhancedNamespaceURI getEnhancedNamespaceURI() {
		return eNamespaceURI;
	}
	
	public String getNamespaceURI() {
		return namespaceURI;
	}
	
	public int getNamespaceUriID() {
		return namespaceUriID;
	}

	public void setNamespaceUriID(int namespaceUriID) {
		this.namespaceUriID = namespaceUriID;
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
		if (obj instanceof AttributeNS) {
			AttributeNS otherSE = (AttributeNS) obj;
			return (namespaceURI.equals(otherSE.namespaceURI));
		} else {
			return false;
		}
	}
}
