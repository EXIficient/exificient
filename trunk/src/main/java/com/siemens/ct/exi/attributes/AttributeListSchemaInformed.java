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

package com.siemens.ct.exi.attributes;

import com.siemens.ct.exi.EXIFactory;

/**
 * Schema-informed attribute list.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.3-SNAPSHOT
 */

public class AttributeListSchemaInformed extends AbstractAttributeList {

	public AttributeListSchemaInformed(EXIFactory exiFactory) {
		super(exiFactory);
	}

	/*
	 * Inserting an item into a sorted list
	 * http://www.brpreiss.com/books/opus5/html/page192.html
	 */
	@Override
	protected void insertAttribute(String uri, String localName, String pfx,
			String value) {
		int i = this.getNumberOfAttributes();

		// greater ?
		while (i > 0 && isGreaterAttribute(i - 1, uri, localName)) {
			// move right
			i--;
		}

		// update position i
		attributeURI.add(i, uri);
		attributeLocalName.add(i, localName);
		attributePrefix.add(i, pfx);
		attributeValue.add(i, value);
	}

	protected final boolean isGreaterAttribute(int attributeIndex, String uri,
			String localName) {

		int compLocalName = getAttributeLocalName(attributeIndex).compareTo(localName);
		
		if (compLocalName > 0 ) {
			// localName is greater
			return true;
		} else if (compLocalName < 0 ) {
			// localName is smaller
			return false;
		} else {
			// localName's are equal 
			return (getAttributeURI(attributeIndex).compareTo(uri) > 0);
		}
	}

}
