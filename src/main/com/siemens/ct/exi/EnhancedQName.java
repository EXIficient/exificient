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

import javax.xml.namespace.QName;

/**
 * Enhanced qualified name storing namespaceUri and localName using ID's
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */
public class EnhancedQName {

	protected final QName qname;
	protected int namespaceUriID;
	protected int localNameID;
	
    public EnhancedQName(final QName qname) {
    	this(qname, -1, -1);
    }
    
    public EnhancedQName(final QName qname, final int namespaceUriID, final int localNameID) {
    	this.qname = qname;
    	this.namespaceUriID = namespaceUriID;
    	this.localNameID = localNameID;
    }
    
    public void setNamespaceUriID(final int namespaceUriID) {
    	this.namespaceUriID = namespaceUriID;
    }
    
    public int getNamespaceUriID() {
    	return namespaceUriID;
    }

    public void setLocalNameID(final int localNameID) {
    	this.localNameID = localNameID;
    }
    
    public int getLocalNameID() {
    	return localNameID;
    }
    
    public QName getQName() {
    	return qname;
    }
    
	@Override
    public final boolean equals(Object objectToTest) {
        if (objectToTest == null || !(objectToTest instanceof EnhancedQName)) {
            return false;
        }

        EnhancedQName eqName = (EnhancedQName) objectToTest;
        
        return this.qname.equals(eqName.qname);

//        return namespaceUriID == eqName.namespaceUriID
//            && localNameID == eqName.localNameID;
    }
	
	@Override
    public final int hashCode() {
        // return namespaceUriID ^ localNameID;
		return qname.hashCode();
    }
	
	@Override
	public String toString() {
		return "{" + namespaceUriID + "," + qname.getNamespaceURI() + "}" + localNameID + "," + qname.getLocalPart();
	}
}
