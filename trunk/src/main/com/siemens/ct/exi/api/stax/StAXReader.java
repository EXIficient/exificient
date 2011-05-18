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

package com.siemens.ct.exi.api.stax;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * De-Serializes EXI to StAX
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public class StAXReader implements XMLStreamReader {

	public void close() throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	public int getAttributeCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getAttributeLocalName(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public QName getAttributeName(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAttributeNamespace(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAttributePrefix(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAttributeType(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAttributeValue(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAttributeValue(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCharacterEncodingScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getElementText() throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getEventType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public QName getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public NamespaceContext getNamespaceContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNamespaceCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getNamespacePrefix(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespaceURI() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespaceURI(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespaceURI(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPIData() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPITarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getProperty(String arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	public char[] getTextCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTextLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTextStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasName() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasNext() throws XMLStreamException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasText() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAttributeSpecified(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCharacters() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEndElement() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStandalone() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStartElement() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isWhiteSpace() {
		// TODO Auto-generated method stub
		return false;
	}

	public int next() throws XMLStreamException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int nextTag() throws XMLStreamException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void require(int arg0, String arg1, String arg2)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	public boolean standaloneSet() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		// System.setProperty("javax.xml.stream.XMLInputFactory", "javax.xml.stream.XMLInputFactory"); 
		// System.clearProperty("javax.xml.stream.XMLInputFactory");
		// XMLInputFactory ifact = new WstxInputFactory();
		XMLInputFactory f = XMLInputFactory.newInstance(); 
		XMLStreamReader r = f.createXMLStreamReader(new FileInputStream("./data/W3C/PrimerNotebook/notebook.xml")); 
		while (r.hasNext()) { 
		    r.next(); 
		}
	}

}
