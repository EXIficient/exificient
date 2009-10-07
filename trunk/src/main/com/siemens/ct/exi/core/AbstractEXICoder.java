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

import javax.xml.namespace.QName;

import org.xml.sax.helpers.NamespaceSupport;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.GrammarURIEntry;
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

	// Context (incl. stack)
	protected QName elementContext;
	protected List<QName> elementContextStack;

	// currentRule and rule stack when traversing the EXI document
	protected Rule currentRule;
	protected List<Rule> openRules;

	// SE pool
	protected Map<QName, StartElement> runtimeElements;

	// URI context
	protected RuntimeURIEntry uriContext;
	protected List<RuntimeURIEntry> runtimeURIEntries;

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
		runtimeURIEntries = new ArrayList<RuntimeURIEntry>();
		
		// init URI entries for the first time
		GrammarURIEntry[] grammarURIEntries = grammar.getGrammarEntries();
		
		for (GrammarURIEntry grammarEntry : grammarURIEntries) {
			addURI(grammarEntry.uri);
			// prefixes
			for (String prefix : grammarEntry.prefixes) {
				uriContext.addPrefix(prefix);
			}
			// local-names
			for (String localName : grammarEntry.localNames) {
				uriContext.addLocalName(localName);
			}
		}
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
		GrammarURIEntry[] grammarURIEntries = grammar.getGrammarEntries();
		
		/*
		 * Remove entries from runtime lists that could have been added
		 * from a previous coding step
		 */
		//	remove URIs 
		assert(grammarURIEntries.length <= runtimeURIEntries.size() );
		int uriSize;
		while(grammarURIEntries.length < (uriSize = runtimeURIEntries.size())) {
			runtimeURIEntries.remove(uriSize-1);
		}
		//	remove localNames & prefixes
		for(int i=0;i<grammarURIEntries.length; i++) {
			RuntimeURIEntry rue = runtimeURIEntries.get(i);
			//	local-names
			String[] grammarLocalNames = grammarURIEntries[i].localNames;
			int localNameSize;
			while(grammarLocalNames.length < (localNameSize = rue.getLocalNameSize() )) {
				rue.removeLocalName(localNameSize-1);
				// System.out.println("remove!!!");
			}
			// prefixes
			String[] grammarPrefixes = grammarURIEntries[i].prefixes;
			int prefixSize;
			while(grammarPrefixes.length < (prefixSize = rue.getPrefixSize() )) {
				rue.removePrefix(prefixSize-1);
			}
		}
		
		uriContext = runtimeURIEntries.get(0);
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
		//	update current rule
		currentRule = openRules.get(size - 2);
	}

	protected StartElement getGenericStartElement(String uri, String localName) {
		// is there a global element that should be used
		StartElement nextSE = grammar.getGlobalElement(uri, localName);
		if (nextSE == null) {
			// no global element --> runtime start element
			// TODO avoid creating QName
			QName qnameSE = new QName(uri, localName);
			nextSE = runtimeElements.get(qnameSE);
			if (nextSE == null) {
				// create new start element and new runtime rule
				nextSE = new StartElement(qnameSE);
				nextSE.setRule(new SchemaLessStartTag());
				// add element to runtime map
				runtimeElements.put(qnameSE, nextSE);
			}
		}

		return nextSE;
	}

	protected QName getAttributeContext(final String namespaceURI,
			final String localName) {
		updateURIContext(namespaceURI);
		
		QName atContext;
		Integer localNameID = uriContext.getLocalNameID(localName);
		if (localNameID == null) {
			atContext = uriContext.addLocalName(localName);
		} else {
			atContext = uriContext.getNameContext(localNameID);
		}
		assert (atContext != null);

		return atContext;
	}

	/*
	 * URIs
	 */
	protected void updateURIContext(final String namespaceURI) {
		if (uriContext.namespaceURI != namespaceURI) {
			// try to find right URI Entry
			for (RuntimeURIEntry uc : this.runtimeURIEntries) {
				if (uc.namespaceURI.equals(namespaceURI)) {
					uriContext = uc;
					return;
				}
			}
			//	URI unknown so far
			uriContext = null;
		}
	}

	protected void addURI(final String namespaceURI) {
		// assert (!uris.containsKey(namespaceURI));
		uriContext = new RuntimeURIEntry(namespaceURI, runtimeURIEntries.size());
		runtimeURIEntries.add(uriContext);
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