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

package com.siemens.ct.exi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EnhancedQName;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.datatype.BooleanDatatype;
import com.siemens.ct.exi.datatype.QNameDatatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaLessStartTag;
import com.siemens.ct.exi.helpers.DefaultErrorHandler;

/**
 * Shared functionality between EXI Body Encoder and EXI Body Decoder.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public abstract class AbstractEXIBodyCoder {

	// xsi:type & nil
	static final QName XSI_NIL = new QName(
			XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_NIL);
	static final EnhancedQName XSI_NIL_ENHANCED = new EnhancedQName(XSI_NIL, 2, 0);
	static final QName XSI_TYPE = new QName(
			XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, Constants.XSI_TYPE);
	static final EnhancedQName XSI_TYPE_ENHANCED = new EnhancedQName(XSI_TYPE, 2, 1);

	// factory
	protected final EXIFactory exiFactory;

	protected Grammar grammar;
	protected FidelityOptions fidelityOptions;
	protected boolean preservePrefix;
	protected boolean preserveLexicalValues;

	// error handler
	protected ErrorHandler errorHandler;

	// QName and Boolean datatype (coder)
	protected QNameDatatype qnameDatatype;
	protected BooleanDatatype booleanDatatype;

	// element-context and rule (stack) while traversing the EXI document
	protected ElementContext elementContext;
	protected Rule currentRule;
	protected ElementContext[] elementContextStack;
	protected int elementContextStackIndex;
	public static final int INITIAL_STACK_SIZE = 16;

	// SE pool
	protected Map<QName, StartElement> runtimeElements;

	public AbstractEXIBodyCoder(EXIFactory exiFactory) throws EXIException {
		this.exiFactory = exiFactory;
		// QName datatype (coder)
		qnameDatatype = new QNameDatatype(this, null);

		initFactoryInformation();

		// use default error handler per default
		this.errorHandler = new DefaultErrorHandler();

		// init once (runtime lists et cetera)
		runtimeElements = new HashMap<QName, StartElement>();
		elementContextStack = new ElementContext[INITIAL_STACK_SIZE];

		// Boolean datatype
		booleanDatatype = new BooleanDatatype(null);
	}

	protected void initFactoryInformation() throws EXIException {
		this.grammar = exiFactory.getGrammar();
		this.fidelityOptions = exiFactory.getFidelityOptions();

		// preserve prefixes
		preservePrefix = fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX);

		// preserve lecicalValues
		preserveLexicalValues = fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE);

		qnameDatatype.setFactoryInformation(preservePrefix, grammar.getGrammarEntries());

	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	// re-init (rule stack etc)
	protected void initForEachRun() throws EXIException, IOException {
		// clear runtime rules
		runtimeElements.clear();

		// possible document/fragment grammar
		currentRule = exiFactory.isFragment() ? grammar.getFragmentGrammar()
				: grammar.getDocumentGrammar();

		// (core) context
		elementContextStackIndex = 0;
		StartElement outerDummy = new StartElement(null);
		outerDummy.setRule(currentRule);
		elementContextStack[elementContextStackIndex] = elementContext = new ElementContext(outerDummy);

		qnameDatatype.initForEachRun();
	}

	public final void declarePrefix(String pfx, String uri) {
		declarePrefix(new NamespaceDeclaration(uri, pfx));
	}

	protected final void declarePrefix(NamespaceDeclaration nsDecl) {
		if (elementContext.nsDeclarations == null) {
			elementContext.nsDeclarations = new ArrayList<NamespaceDeclaration>();
		}
		assert (!elementContext.nsDeclarations.contains(nsDecl));
		elementContext.nsDeclarations.add(nsDecl);
	}

	public final String getURI(String prefix) {
		// check all stack items except last one (in reverse order)
		for (int i = elementContextStackIndex; i > 0; i--) {
			ElementContext ec = elementContextStack[i];
			if (ec.nsDeclarations != null) {
				for (NamespaceDeclaration ns : ec.nsDeclarations) {
					if (ns.prefix.equals(prefix)) {
						return ns.namespaceURI;
					}
				}
			}
		}
		return null;
	}

	public final String getPrefix(String uri) {
		if (XMLConstants.NULL_NS_URI.equals(uri)) {
			return XMLConstants.DEFAULT_NS_PREFIX;
		} else if (XMLConstants.XML_NS_URI.equals(uri)) {
			return XMLConstants.XML_NS_PREFIX;
		}
//		// check all stack items except last one (in reverse order)
//		for (int i = elementContextStackIndex; i > 0; i--) {
		// check all stack items except first one
		for (int i = 1; i <= elementContextStackIndex; i++) {
			ElementContext ec = elementContextStack[i];
			if (ec.nsDeclarations != null) {
				for (NamespaceDeclaration ns : ec.nsDeclarations) {
					if (ns.namespaceURI.equals(uri)) {
						return ns.prefix;
					}
				}
			}
		}
		return null;
	}

	protected void pushElement(StartElement se, Rule contextRule) {
		// update "rule" item of current peak (for popElement() later on)
		elementContext.rule = contextRule;
		// set "new" current-rule
		currentRule = se.getRule();
		// create new stack item & push it
		elementContext = new ElementContext(se);
		// needs array to be extended?
		if (elementContextStack.length == ++elementContextStackIndex) {
			ElementContext[] elementContextStackNew = new ElementContext[elementContextStack.length << 2];
			System.arraycopy(elementContextStack, 0, elementContextStackNew, 0,
					elementContextStack.length);
			elementContextStack = elementContextStackNew;
		}
		elementContextStack[elementContextStackIndex] = elementContext;
	}

	protected final ElementContext popElement() {
		assert (this.elementContextStackIndex > 0);
		// pop element from stack
		ElementContext poppedEC = elementContextStack[elementContextStackIndex--];
		elementContext = elementContextStack[elementContextStackIndex];
		// update current rule to new (old) element stack
		currentRule = elementContext.rule;

		return poppedEC;
	}

	// protected StartElement getGenericStartElement(QName qname) {
	protected StartElement getGenericStartElement(EnhancedQName eqname) {
		// is there a global element that should be used
		QName qname = eqname.getQName();
		StartElement nextSE = grammar.getGlobalElement(qname);
		if (nextSE == null) {
				// no global element --> runtime start element
				nextSE = runtimeElements.get(qname);
				if (nextSE == null) {
					// create new start element and add runtime rule
					nextSE = new StartElement(eqname);
					nextSE.setRule(new SchemaLessStartTag());
					// add element to runtime map
					runtimeElements.put(eqname.getQName(), nextSE);
				}
		}

		return nextSE;
	}

	protected QName getElementContextQName() {
		return elementContextStack[elementContextStackIndex].eqname.getQName();
	}

	/*
	 * 
	 */
	protected void throwWarning(String message) {
		errorHandler.warning(new EXIException(message + ", options="
				+ exiFactory.getFidelityOptions()));
		// System.err.println(message);
	}

	// static
	final class ElementContext {
		// final QName qname;
		final EnhancedQName eqname;
		String prefix;
		String sqname;
		final StartElement se;
		Rule rule; // may be modified while coding
		// prefix declarations
		List<NamespaceDeclaration> nsDeclarations;

		public ElementContext(StartElement se) {
			this.se = se;
			// this.qname = se.getQName();
			this.eqname = se.getEnhancedQName();
			this.rule = se.getRule();
		}

		String getQNameAsString() {
			if (sqname == null) {
				sqname = qnameDatatype.getQNameAsString(eqname, prefix);
			}
			return sqname;
		}
	}
}