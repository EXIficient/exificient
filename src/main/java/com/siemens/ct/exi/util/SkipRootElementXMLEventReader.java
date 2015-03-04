/*
 * Copyright (C) 2007-2015 Siemens AG
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

package com.siemens.ct.exi.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class SkipRootElementXMLEventReader implements XMLEventReader {

	List<XMLEvent> events;

	int index = -1;

	public SkipRootElementXMLEventReader(XMLEventReader parent)
			throws XMLStreamException {
		events = new ArrayList<XMLEvent>();
		int openElement = 0;

		while (parent.hasNext()) {
			XMLEvent next = parent.nextEvent();

			if (next.isStartDocument()) {
				events.add(next);
			} else if (next.isEndDocument()) {
				events.add(next);
			} else if (next.isStartElement()) {
				if (openElement > 0) {
					events.add(next);
				}
				openElement++;
			} else if (next.isEndElement()) {
				openElement--;
				if (openElement > 0) {
					events.add(next);
				}
			} else {
				if (openElement > 0) {
					events.add(next);
				}
			}

		}

	}

	public Object next() {
		return events.get(index);
	}

	public void remove() {
	}

	public void close() throws XMLStreamException {
	}

	public String getElementText() throws XMLStreamException {
		// return parent.getElementText();
		return null;
	}

	public Object getProperty(String arg0) throws IllegalArgumentException {
		return null;
	}

	public boolean hasNext() {
		return ++index < events.size();
	}

	public XMLEvent nextEvent() throws XMLStreamException {
		return events.get(index);
	}

	public XMLEvent nextTag() throws XMLStreamException {
		return events.get(index);
	}

	public XMLEvent peek() throws XMLStreamException {
		return events.get(index);
	}

}
