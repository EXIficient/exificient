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

package com.siemens.ct.exi.api.xmlpull;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.7-SNAPSHOT
 */

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamEncoder;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.values.StringValue;

public class EXISerializer implements XmlSerializer {
	
	final protected EXIFactory factory;
	final protected EXIStreamEncoder exiStream;
	
	protected EXIBodyEncoder exiBody;
	
	protected OutputStream outputStream;
	
	protected List<NamespaceDeclaration> nsDecls;
	
	String currentNamespace;
	String currentName;
	

	public EXISerializer(EXIFactory factory) throws EXIException {
		this.factory = factory;
		
		this.exiStream = factory.createEXIStreamEncoder();
		
		nsDecls = new ArrayList<NamespaceDeclaration>();
	}

	public void setFeature(String name, boolean state)
			throws IllegalArgumentException, IllegalStateException {
		// TODO check if any feature could be of interest
		throw new IllegalStateException("EXI does not support setting feature " + name + " to " + state);

	}

	public boolean getFeature(String name) {
		return false; // unknown
	}

	public void setProperty(String name, Object value)
			throws IllegalArgumentException, IllegalStateException {
		// TODO check if any property could be of interest
		throw new IllegalStateException("EXI does not support setting property " + name + " to " + value);
	}

	public Object getProperty(String name) {
		return null; // unknown 
	}

	public void setOutput(OutputStream os, String encoding) throws IOException,
			IllegalArgumentException, IllegalStateException {
		try {
			this.outputStream = os;
			exiBody = exiStream.encodeHeader(os);
		} catch (EXIException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void setOutput(Writer writer) throws IOException,
			IllegalArgumentException, IllegalStateException {
		throw new IllegalArgumentException("EXI requires byte-based stream. Consider using OutputStream");
	}

	public void startDocument(String encoding, Boolean standalone)
			throws IOException, IllegalArgumentException, IllegalStateException {
		try {
			exiBody.encodeStartDocument();
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	public void endDocument() throws IOException, IllegalArgumentException,
			IllegalStateException {
		try {
			exiBody.encodeEndDocument();
		} catch (EXIException e) {
			throw new IOException(e);
		}
	}

	public void setPrefix(String prefix, String namespace) throws IOException,
			IllegalArgumentException, IllegalStateException {
		// Note: is called before start tag --> store it
		nsDecls.add(new NamespaceDeclaration(namespace, prefix));
	}

	public String getPrefix(String namespace, boolean generatePrefix)
			throws IllegalArgumentException {
		return null;
	}

	public int getDepth() {
		return 0;
	}

	public String getNamespace() {
		return currentNamespace;
	}

	public String getName() {
		return currentName;
	}
	
	public XmlSerializer startTag(String namespace, String name)
			throws IOException, IllegalArgumentException, IllegalStateException {
		try {
			this.currentNamespace = namespace;
			this.currentName = name;
			exiBody.encodeStartElement(namespace, name, null);
			return this;
		} catch (EXIException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public XmlSerializer attribute(String namespace, String name, String value)
			throws IOException, IllegalArgumentException, IllegalStateException {
		try {
			exiBody.encodeAttribute(namespace, name, null, new StringValue(value));
			return this;
		} catch (EXIException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public XmlSerializer endTag(String namespace, String name)
			throws IOException, IllegalArgumentException, IllegalStateException {
		try {
			exiBody.encodeEndElement();
			return this;
		} catch (EXIException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public XmlSerializer text(String text) throws IOException,
			IllegalArgumentException, IllegalStateException {
		try {
			exiBody.encodeCharacters(new StringValue(text));
			return this;
		} catch (EXIException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public XmlSerializer text(char[] buf, int start, int len)
			throws IOException, IllegalArgumentException, IllegalStateException {
		return text(new String(buf, start, len));
	}

	public void cdsect(String text) throws IOException,
			IllegalArgumentException, IllegalStateException {
		text("<![CDATA[" + text + "]]>");
	}

	public void entityRef(String text) throws IOException,
			IllegalArgumentException, IllegalStateException {
		try {
			exiBody.encodeEntityReference(text);
		} catch (EXIException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void processingInstruction(String text) throws IOException,
			IllegalArgumentException, IllegalStateException {
		text("<?" + text + "?>");
	}

	public void comment(String text) throws IOException,
			IllegalArgumentException, IllegalStateException {
		try {
			char[] ch = text.toCharArray();
			exiBody.encodeComment(ch, 0, ch.length);
		} catch (EXIException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void docdecl(String text) throws IOException,
			IllegalArgumentException, IllegalStateException {
		text("<!DOCTYPE " + text + ">");
	}

	public void ignorableWhitespace(String text) throws IOException,
			IllegalArgumentException, IllegalStateException {
		text(text);
	}

	public void flush() throws IOException {
		exiBody.flush();
	}

}
