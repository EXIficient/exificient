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

package com.siemens.ct.exi.util.sort;

import java.io.Serializable;
import java.util.Comparator;

import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.AttributeNS;
import com.siemens.ct.exi.grammars.event.Event;

/**
 * Helper Class for sorting EXI events
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public class EventCodeAssignment implements Comparator<Event>, Serializable {

	private static final long serialVersionUID = 7616132143378329230L;

	protected static final LexicographicSort lexSort = new LexicographicSort();

	// see http://www.w3.org/TR/exi/#eventCodeAssignment
	public int compare(Event o1, Event o2) {
		int diff = o1.getEventType().ordinal() - o2.getEventType().ordinal();
		if (diff == 0) {
			switch (o1.getEventType()) {
			case ATTRIBUTE:
				// sorted lexicographically by qname local-name, then by qname
				// uri
				return lexSort.compare((Attribute) o1, (Attribute) o2);
			case ATTRIBUTE_NS:
				// sorted lexicographically by uri
				AttributeNS atNS1 = (AttributeNS) o1;
				AttributeNS atNS2 = (AttributeNS) o2;
				return atNS1.getNamespaceURI().compareTo(
						atNS2.getNamespaceURI());
			case START_ELEMENT:
				// sorted in schema order
				return -1;
			case START_ELEMENT_NS:
				// sorted in schema order
				return -1;
			}
		}

		return diff;
	}

}
