/*
 * Copyright (C) 2007-2009 Siemens AG
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

package com.siemens.ct.exi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.xml.sax.helpers.NamespaceSupport;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.URIEntry;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaLessStartTag;
import com.siemens.ct.exi.helpers.DefaultErrorHandler;

/**
 * Shared functionality between EXI Encoder and Decoder.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090331
 */

public abstract class AbstractEXICoder {
	
	// factory
	protected EXIFactory exiFactory;
	protected Grammar grammar;
	protected boolean isSchemaInformed;
	protected FidelityOptions fidelityOptions;

	// error handler
	protected ErrorHandler errorHandler;

	// namespaces/prefixes
	protected NamespaceSupport namespaces;

	// currentRule and rule stack when traversing the EXI document
	protected Rule currentRule;
	protected List<Rule> openRules;
	
	//	SE pool
	protected Map<QName, StartElement> runtimeElements;	

	// URI context
	protected URIContext uriContext;
	protected List<URIContext> uris;
	
	// Context (incl. stack)
	protected QName elementContext;
	protected List<QName> elementContextStack;

	public AbstractEXICoder(EXIFactory exiFactory) {
		this.exiFactory = exiFactory;
		this.grammar = exiFactory.getGrammar();
		this.isSchemaInformed = grammar.isSchemaInformed();
		this.fidelityOptions = exiFactory.getFidelityOptions();

		// namespaces/prefixes
		namespaces = new NamespaceSupport();

		// use default error handler per default
		this.errorHandler = new DefaultErrorHandler();

		// init once (runtime lists et cetera)
		runtimeElements = new HashMap<QName, StartElement>();
		openRules = new ArrayList<Rule>();
		elementContextStack = new ArrayList<QName>();
		uris = new ArrayList<URIContext>();
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	// re-init (rule stack etc)
	protected void initForEachRun() throws EXIException, IOException {
		// stack when traversing the EXI document
		openRules.clear();
		currentRule = null;
		// namespaces/prefixes
		namespaces.reset();
		// (core) context
		elementContextStack.clear();
		elementContextStack.add(elementContext = null);
		initForEachRunContext();
		// clear runtime rules
		runtimeElements.clear();
		
		// possible root elements
		if (exiFactory.isFragment()) {
			// push stack with fragment grammar
			openRules.add(currentRule = grammar.getBuiltInFragmentGrammar());
		} else {
			// push stack with document grammar
			openRules.add(currentRule = grammar.getBuiltInDocumentGrammar());
		}
	}

	protected void initForEachRunContext() {
		uris.clear();
		uriContext = null;

		/*
		 * "", empty string
		 */
		addURI(Constants.EMPTY_STRING);
		uriContext.addPrefix(Constants.EMPTY_STRING);

		/*
		 * "http://www.w3.org/XML/1998/namespace"
		 */
		addURI(XMLConstants.XML_NS_URI);
		uriContext.addPrefix(Constants.XML_PFX);
		uriContext.addLocalName("space");
		uriContext.addLocalName("lang");
		uriContext.addLocalName("id");
		uriContext.addLocalName("base");

		/*
		 * "http://www.w3.org/2001/XMLSchema-instance", xsi
		 */
		addURI(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		uriContext.addPrefix(Constants.XSI_PFX);
		uriContext.addLocalName("type");
		uriContext.addLocalName("nil");

		if (isSchemaInformed) {
			/*
			 * "http://www.w3.org/2001/XMLSchema", xsd
			 */
			addURI(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			uriContext.addLocalName("anyType");
			uriContext.addLocalName("anySimpleType");
			uriContext.addLocalName("string");
			uriContext.addLocalName("normalizedString");
			uriContext.addLocalName("token");
			uriContext.addLocalName("language");
			uriContext.addLocalName("Name");
			uriContext.addLocalName("NCName");
			uriContext.addLocalName("ID");
			uriContext.addLocalName("IDREF");
			uriContext.addLocalName("IDREFS");
			uriContext.addLocalName("ENTITY");
			uriContext.addLocalName("ENTITIES");
			uriContext.addLocalName("NMTOKEN");
			uriContext.addLocalName("NMTOKENS");
			uriContext.addLocalName("duration");
			uriContext.addLocalName("dateTime");
			uriContext.addLocalName("time");
			uriContext.addLocalName("date");
			uriContext.addLocalName("gYearMonth");
			uriContext.addLocalName("gYear");
			uriContext.addLocalName("gMonthDay");
			uriContext.addLocalName("gDay");
			uriContext.addLocalName("gMonth");
			uriContext.addLocalName("boolean");
			uriContext.addLocalName("base64Binary");
			uriContext.addLocalName("hexBinary");
			uriContext.addLocalName("float");
			uriContext.addLocalName("double");
			uriContext.addLocalName("anyURI");
			uriContext.addLocalName("QName");
			uriContext.addLocalName("NOTATION");
			uriContext.addLocalName("decimal");
			uriContext.addLocalName("integer");
			uriContext.addLocalName("nonPositiveInteger");
			uriContext.addLocalName("negativeInteger");
			uriContext.addLocalName("long");
			uriContext.addLocalName("int");
			uriContext.addLocalName("short");
			uriContext.addLocalName("byte");
			uriContext.addLocalName("nonNegativeInteger");
			uriContext.addLocalName("positiveInteger");
			uriContext.addLocalName("unsignedLong");
			uriContext.addLocalName("unsignedInt");
			uriContext.addLocalName("unsignedShort");
			uriContext.addLocalName("unsignedByte");

			/*
			 * Schema URIs & LocalNames
			 */
			URIEntry[] schemaEntries = grammar.getSchemaEntries();
			for (URIEntry schemaEntry : schemaEntries) {
				String uri = schemaEntry.uri;
				if (uri.equals(XMLConstants.NULL_NS_URI)) {
					updateURIContext(uri);
				} else {
					addURI(schemaEntry.uri);
				}
				for (String localName : schemaEntry.localNames) {
					uriContext.addLocalName(localName);
				}
			}
		}
	}

	public NamespaceSupport getNamespaces() {
		return namespaces;
	}

	protected final void replaceRuleAtTheTop(Rule top) {
		assert (!openRules.isEmpty());
		assert (top != null);

		if (top != currentRule) {
			openRules.set(openRules.size() - 1, currentRule = top);
		}
	}

	
	protected void pushElementContext(QName context) {
		elementContext = context;
		// push context stack
		elementContextStack.add(context);
		// push NS context
		namespaces.pushContext();
	}

	protected final void popElementContext() {
		// context stack
		assert (!elementContextStack.isEmpty());
		int sizeContext = elementContextStack.size();
		elementContextStack.remove(sizeContext - 1);
		elementContext = elementContextStack.get(sizeContext - 2);

		// NS context
		namespaces.popContext();
	}

	protected void pushElementRule(Rule r) {
		currentRule = r;
		// actually push the rule on the top of the stack
		assert (currentRule != null);
		openRules.add(currentRule);
	}

	protected final void popElementRule() {
		assert (!openRules.isEmpty());
		int size = openRules.size();
		openRules.remove(size - 1);
		currentRule = openRules.get(size - 2);
	}
	
	protected StartElement getGenericStartElement(String uri, String localName) {
		//	is there a global element that should be used
		StartElement nextSE = grammar.getGlobalElement(uri, localName);
		if( nextSE == null) {
			//	no global element --> runtime start element
			//	TODO avoid creating QName
			QName qnameSE = new QName(uri, localName);
			nextSE = runtimeElements.get(qnameSE);
			if ( nextSE == null) {
				//	create new start element and new runtime rule
				nextSE = new StartElement(qnameSE);
				nextSE.setRule(new SchemaLessStartTag());
				//	add element to runtime map
				runtimeElements.put(qnameSE, nextSE);
			}
		}
		
		return nextSE;
	}

	
	protected QName getAttributeContext(final String namespaceURI,
			final String localName) {
		updateURIContext(namespaceURI);
		Integer localNameID = uriContext.getLocalNameID(localName);		
		if (localNameID == null) {
			uriContext.addLocalName(localName);
			localNameID = uriContext.getLocalNameID(localName);	
		}

		QName atContext = uriContext.getNameContext(localNameID);
		assert (atContext != null);
		
		return atContext;
	}
	
	/*
	 * URIs
	 */
	protected void updateURIContext(final String namespaceURI) {
		if (uriContext.namespaceURI != namespaceURI) {
			// uriContext = uris.get(namespaceURI);
			for(URIContext uc: this.uris) {
				if (uc.namespaceURI.equals(namespaceURI)) {
					uriContext = uc;
					return;
				}
			}
			uriContext = null;
		}
	}
	
	protected void addURI(final String namespaceURI) {
		// assert (!uris.containsKey(namespaceURI));
		uriContext = new URIContext(namespaceURI, uris.size());
		uris.add(uriContext);
	}



	/*
	 * 
	 */
	protected void throwWarning(String message) {
		errorHandler.warning(new EXIException(message + ", options="
				+ fidelityOptions));
		// System.err.println(message);
	}
}