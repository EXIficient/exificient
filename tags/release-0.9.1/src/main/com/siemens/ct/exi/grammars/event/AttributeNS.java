/*
 * Copyright (C) 2007-2012 Siemens AG
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

package com.siemens.ct.exi.grammars.event;


/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
 */

public class AttributeNS extends AbstractEvent {

	private static final long serialVersionUID = 6004967457126269590L;
	
	protected final String namespaceUri;
	protected final int namespaceUriID;
	
	public AttributeNS(int namespaceUriID, String namespaceUri) {
		super(EventType.ATTRIBUTE_NS);
		this.namespaceUriID = namespaceUriID;
		this.namespaceUri = namespaceUri;
	}
	
	public String getNamespaceURI() {
		return namespaceUri;
	}
	
	public int getNamespaceUriID() {
		return namespaceUriID;
	}

	public String toString() {
		return super.toString() + "(" + getNamespaceURI() + ":*)";
	}

	@Override
	public int hashCode() {
		return (getNamespaceURI().hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AttributeNS) {
			AttributeNS otherSE = (AttributeNS) obj;
			return (getNamespaceURI().equals(otherSE.getNamespaceURI()));
		} else {
			return false;
		}
	}
}
