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

package com.siemens.ct.exi;

import java.io.Serializable;

import javax.xml.XMLConstants;

/**
 * Enhanced namespaceURI storing namespace uri ID as well as the string
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */
public class EnhancedNamespaceURI implements Serializable {

	private static final long serialVersionUID = 4926376840076118448L;
	
	protected final String namespaceUri;
	protected int namespaceUriID;
	
    public EnhancedNamespaceURI(final String namespaceUri) {
    	this(namespaceUri, -1);
    }
    
    public EnhancedNamespaceURI(final String namespaceUri, final int namespaceUriID) {
    	this.namespaceUri = namespaceUri == null ? XMLConstants.NULL_NS_URI : namespaceUri;
    	this.namespaceUriID = namespaceUriID;
    }
    
    public void setNamespaceUriID(final int namespaceUriID) {
    	this.namespaceUriID = namespaceUriID;
    }
    
    public int getNamespaceUriID() {
    	return namespaceUriID;
    }
    
    public String getNamespaceURI() {
    	return namespaceUri;
    }
    
	@Override
    public final boolean equals(Object objectToTest) {
        if (objectToTest == null || !(objectToTest instanceof EnhancedNamespaceURI)) {
            return false;
        }

        EnhancedNamespaceURI eNamespace = (EnhancedNamespaceURI) objectToTest;

        return namespaceUri.equals(eNamespace.namespaceUri);
    }
	
	@Override
    public final int hashCode() {
		return namespaceUri.hashCode();
    }
	
	@Override
	public String toString() {
		return namespaceUriID + "," + namespaceUri;
	}
}
