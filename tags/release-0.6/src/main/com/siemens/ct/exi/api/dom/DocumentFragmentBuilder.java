/*
 * Copyright (C) 2007-2010 Siemens AG
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

package com.siemens.ct.exi.api.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Builds a <code>DocumentFragment</code> for a given EXI stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class DocumentFragmentBuilder {

	protected DocumentBuilder docBuilder;

	public DocumentFragmentBuilder(DocumentBuilder docBuilder) {
		this.docBuilder = docBuilder;
	}

	public DocumentBuilder getDocumentBuilder() {
		return this.docBuilder;
	}

	public DocumentFragment parse(InputStream is) throws SAXException,
			IOException {
		// Wrap the fragment in an arbitrary element
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write("<fragment>".getBytes());

		int b;
		while ((b = is.read()) != -1) {
			baos.write(b);
		}

		baos.write("</fragment>".getBytes());

		// parse
		Document doc = this.docBuilder.parse(new ByteArrayInputStream(baos
				.toByteArray()));

		// // Import the nodes of the new document into doc so that they
		// // will be compatible with doc
		Node node = doc.importNode(doc.getDocumentElement(), true);

		// Create the document fragment node to hold the new nodes
		DocumentFragment docfrag = doc.createDocumentFragment();

		// Move the nodes into the fragment
		while (node.hasChildNodes()) {
			docfrag.appendChild(node.removeChild(node.getFirstChild()));
		}

		// Return the fragment
		return docfrag;
	}

}
