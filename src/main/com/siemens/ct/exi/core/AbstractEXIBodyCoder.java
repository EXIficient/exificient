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

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.datatype.BooleanDatatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultErrorHandler;
import com.siemens.ct.exi.util.xml.QNameUtilities;

/**
 * Shared functionality between EXI Body Encoder and EXI Body Decoder.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public abstract class AbstractEXIBodyCoder {

	// factory
	protected final EXIFactory exiFactory;

	protected final Grammars grammar;
	protected final FidelityOptions fidelityOptions;
	protected final boolean preservePrefix;
	protected final boolean preserveLexicalValues;

	// error handler
	protected ErrorHandler errorHandler;

	// Boolean datatype (coder)
	protected final BooleanDatatype booleanDatatype;

	// element-context and rule (stack) while traversing the EXI document
	private ElementContext elementContext; // cached context to avoid heavy
											// array lookup
	protected ElementContext[] elementContextStack;
	protected int elementContextStackIndex;
	public static final int INITIAL_STACK_SIZE = 16;

	// SE pool
	protected Map<QName, StartElement> runtimeElements;

	public AbstractEXIBodyCoder(EXIFactory exiFactory) throws EXIException {
		this.exiFactory = exiFactory;

		this.grammar = exiFactory.getGrammars();
		this.fidelityOptions = exiFactory.getFidelityOptions();
		
		// preserve prefixes
		preservePrefix = fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX);
		// preserve lecicalValues
		preserveLexicalValues = fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE);
		
		// use default error handler per default
		this.errorHandler = new DefaultErrorHandler();

		// init once (runtime lists et cetera)
		runtimeElements = new HashMap<QName, StartElement>();
		elementContextStack = new ElementContext[INITIAL_STACK_SIZE];

		// Boolean datatype
		booleanDatatype = new BooleanDatatype(null);
	}

	protected final Grammar getCurrentGrammar() {
		return this.elementContext.gr;
	}

	protected final void updateCurrentRule(Grammar newCurrentGrammar) {
		this.elementContext.gr = newCurrentGrammar;
	}

	protected final ElementContext getElementContext() {
		return elementContext;
	}

	protected final void updateElementContext(ElementContext elementContext) {
		this.elementContext = elementContext;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	// re-init (rule stack etc)
	protected void initForEachRun() throws EXIException, IOException {
		// clear runtime rules
		runtimeElements.clear();

		// possible document/fragment grammar
		Grammar startRule = exiFactory.isFragment() ? grammar.getFragmentGrammar()
				: grammar.getDocumentGrammar();

		// (core) context
		elementContextStackIndex = 0;
		elementContextStack[elementContextStackIndex] = elementContext = new ElementContext(
				null, startRule);
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
		return prefix.length() == 0 ? XMLConstants.NULL_NS_URI : null;
	}

	protected void pushElement(Grammar updContextGrammar, StartElement se) {
		// update "rule" item of current peak (for popElement() later on)
		elementContext.gr = updContextGrammar;

		// check element context array size
		if (elementContextStack.length == ++elementContextStackIndex) {
			ElementContext[] elementContextStackNew = new ElementContext[elementContextStack.length << 2];
			System.arraycopy(elementContextStack, 0, elementContextStackNew, 0,
					elementContextStack.length);
			elementContextStack = elementContextStackNew;
		}

		// create new stack item & push it
		elementContextStack[elementContextStackIndex] = elementContext = new ElementContext(
				se.getQNameContext(), se.getGrammar());
	}

	protected final ElementContext popElement() {
		assert (this.elementContextStackIndex > 0);
		// pop element from stack
		ElementContext poppedEC = elementContextStack[elementContextStackIndex];
		elementContextStack[elementContextStackIndex--] = null;
		elementContext = elementContextStack[elementContextStackIndex];

		return poppedEC;
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
		private String prefix;
		private String sqname;
		Grammar gr; // may be modified while coding
		List<NamespaceDeclaration> nsDeclarations; // prefix declarations

		final QNameContext qnameContext;

		public ElementContext(QNameContext qnameContext, Grammar gr) {
			this.qnameContext = qnameContext;
			this.gr = gr;
		}

		String getQNameAsString() {
			if (sqname == null) {
				if (preservePrefix) {
					sqname = QNameUtilities.getQualifiedName(
							qnameContext.getLocalName(), getPrefix());
				} else {
					sqname = qnameContext.getDefaultQNameAsString();
				}
			}
			return sqname;
		}
		
		void setPrefix(String pfx) {
			this.prefix = pfx;
		}
		
		String getPrefix() {
//			if(this.prefix == null) {
//				this.prefix = checkPrefixMapping(qnameContext);
//			}
			return this.prefix;
		}
	}
}