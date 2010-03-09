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

package com.siemens.ct.exi.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.xml.sax.helpers.NamespaceSupport;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.datatype.QNameDatatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammar.Grammar;
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
 * @version 0.4.20090331
 */

public abstract class AbstractEXICoder {

	// factory
	protected final EXIFactory exiFactory;
	protected final Grammar grammar;
	protected final boolean isSchemaInformed;
	protected final FidelityOptions fidelityOptions;
	protected final boolean preservePrefix;

	// error handler
	protected ErrorHandler errorHandler;

	// namespaces/prefixes
	protected NamespaceSupport namespaces;
	
	//	QName datatype (coder)
	protected QNameDatatype qnameDatatype;

	// element-context and rule (stack) while traversing the EXI document
	protected ElementContext elementContext;
	protected Rule currentRule;
	private ElementContext[] elementContextStack;
	private int elementContextStackIndex;
	public static final int INITIAL_STACK_SIZE = 16;
	
	// SE pool
	protected Map<QName, StartElement> runtimeElements;

	public AbstractEXICoder(EXIFactory exiFactory) {
		this.exiFactory = exiFactory;
		this.grammar = exiFactory.getGrammar();
		this.isSchemaInformed = grammar.isSchemaInformed();
		this.fidelityOptions = exiFactory.getFidelityOptions();
		
		// preserve prefixes
		preservePrefix = fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX);

		// namespaces/prefixes
		namespaces = new NamespaceSupport();
		
		// use default error handler per default
		this.errorHandler = new DefaultErrorHandler();

		// init once (runtime lists et cetera)
		runtimeElements = new HashMap<QName, StartElement>();
		elementContextStack = new ElementContext[INITIAL_STACK_SIZE];
	
		// QName datatype (coder)
		qnameDatatype = new QNameDatatype(null, namespaces, preservePrefix);
		qnameDatatype.setGrammarURIEnties(grammar.getGrammarEntries());
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	public NamespaceSupport getNamespaces() {
		return this.namespaces;
	}

	// re-init (rule stack etc)
	protected void initForEachRun() throws EXIException, IOException {
		// namespaces/prefixes
		namespaces.reset();
		
		// clear runtime rules
		runtimeElements.clear();
		
		// possible document/fragment grammar
		currentRule = exiFactory.isFragment() ? grammar.getBuiltInFragmentGrammar() : grammar.getBuiltInDocumentGrammar();
		
		// (core) context
		elementContextStackIndex = 0;
		elementContextStack[elementContextStackIndex] = elementContext = new ElementContext(null, currentRule);
		
		qnameDatatype.initForEachRun();
	}

	protected final void pushElement(StartElement se, Rule contextRule) {
		// update "rule" item of current peak (for popElement() later on) 
		// elementContext.rule = currentRule;
		elementContext.rule = contextRule;
		//	set "new" current-rule
		currentRule = se.getRule();
		//	create new stack item & push it
		pushElementContext(new ElementContext(se.getQName(), currentRule));
		
		// NS context
		namespaces.pushContext();
	}
	
	protected final void pushElementContext(ElementContext elementContext) {
		this.elementContext = elementContext;
		++elementContextStackIndex;
		// array needs to be extended?
		if (elementContextStack.length == elementContextStackIndex) {
			ElementContext[] elementContextStackNew = new ElementContext[elementContextStack.length << 2];
			System.arraycopy(elementContextStack, 0, elementContextStackNew, 0, elementContextStack.length);
			elementContextStack = elementContextStackNew;
		}
		elementContextStack[elementContextStackIndex] = elementContext;
	}

	protected final void popElement() {
		assert (this.elementContextStackIndex > 0);
		//	pop element from stack
		elementContextStack[elementContextStackIndex--] = null;	// let gc do the rest
		elementContext = elementContextStack[elementContextStackIndex];
		// update current rule to new (old) element stack
		currentRule = elementContext.rule;
		// NS context
		namespaces.popContext();
	}

	
	protected StartElement getGenericStartElement(QName qname) {
		// is there a global element that should be used
		StartElement nextSE = grammar.getGlobalElement(qname);
		if (nextSE == null) {
			// no global element --> runtime start element
			nextSE = runtimeElements.get(qname);
			if (nextSE == null) {
				// create new start element and new runtime rule
				nextSE = new StartElement(qname);
				nextSE.setRule(new SchemaLessStartTag());
				// add element to runtime map
				runtimeElements.put(qname, nextSE);
			}
		}

		return nextSE;
	}
	
	protected QName getElementContextQName() {
		return elementContextStack[elementContextStackIndex].qname;
	}

	/*
	 * 
	 */
	protected void throwWarning(String message) {
		errorHandler.warning(new EXIException(message + ", options="
				+ fidelityOptions));
		// System.err.println(message);
	}
	
	static final class ElementContext {
		final QName qname;
		Rule rule;	// may be modified while coding
		//	TODO prefix declarations
		List<Object> prefixDeclarations; 

		public ElementContext(QName qname, Rule rule) {
			this.qname = qname;
			this.rule = rule;
		}
	}
}