/*
 * Copyright (c) 2007-2016 Siemens AG
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
 * @version 0.9.7-SNAPSHOT
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
