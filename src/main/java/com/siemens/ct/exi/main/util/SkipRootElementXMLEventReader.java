/*
 * Copyright (c) 2007-2018 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.main.util;

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
 * @version 1.0.0
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
