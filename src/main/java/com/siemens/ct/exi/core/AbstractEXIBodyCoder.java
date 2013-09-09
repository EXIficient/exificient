/*
 * Copyright (C) 2007-2012 Siemens AG
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
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.GrammarUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.datatype.BooleanDatatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.BuiltInStartTag;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.helpers.DefaultErrorHandler;
import com.siemens.ct.exi.util.xml.QNameUtilities;

/**
 * Shared functionality between EXI Body Encoder and EXI Body Decoder.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2
 */

public abstract class AbstractEXIBodyCoder {

	// factory
	protected final EXIFactory exiFactory;

	protected final Grammars grammar;
	protected final GrammarContext grammarContext;
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

	// runtime global elements
	protected Map<QNameContext, StartElement> runtimeGlobalElements;
	
	// runtime uris & names et cetera
	List<RuntimeUriContext> runtimeUris;
	
	// Xsi qname contexts
	protected QNameContext xsiTypeContext;
	protected QNameContext xsiNilContext;
	
	protected final int gUris; // number of grammar uris
	protected int nextQNameID;
	protected int nextUriID;
	
	/** EXI Profile parameters */
	protected final boolean limitGrammarLearning;
	protected final int maxBuiltInElementGrammars;
	protected final int maxBuiltInProductions;
	protected int learnedProductions;

	
	public AbstractEXIBodyCoder(EXIFactory exiFactory) throws EXIException {
		this.exiFactory = exiFactory;

		this.grammar = exiFactory.getGrammars();
		this.grammarContext = this.grammar.getGrammarContext();
		this.nextUriID = this.gUris = grammarContext.getNumberOfGrammarUriContexts();
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
		runtimeGlobalElements = new HashMap<QNameContext, StartElement>();
		runtimeUris = new ArrayList<RuntimeUriContext>();
		for(int i=0; i< this.gUris; i++) {
			this.runtimeUris.add(new RuntimeUriContext(this.grammarContext.getGrammarUriContext(i)));
		}
		elementContextStack = new ElementContext[INITIAL_STACK_SIZE];

		// Boolean datatype
		booleanDatatype = new BooleanDatatype(null);
		
		// EXI Profile: fine-grained grammar learning
		if(this.grammar.isSchemaInformed()) {
			maxBuiltInElementGrammars = this.exiFactory.getMaximumNumberOfBuiltInElementGrammars();
			maxBuiltInProductions = this.exiFactory.getMaximumNumberOfBuiltInProductions();	
			limitGrammarLearning = (maxBuiltInElementGrammars >= 0 || maxBuiltInProductions >= 0);
		} else {
			maxBuiltInElementGrammars = -1;
			maxBuiltInProductions = -1;
			limitGrammarLearning = false;
		}
	}

	protected QNameContext getXsiTypeContext() {
		if(xsiTypeContext == null) {
			xsiTypeContext = grammarContext.getGrammarUriContext(2).getQNameContext(1);	
		}
		return xsiTypeContext;
		
	}

	protected QNameContext getXsiNilContext() {
		if(xsiNilContext == null) {
			xsiNilContext = grammarContext.getGrammarUriContext(2).getQNameContext(0);	
		}
		return xsiNilContext;
	}
	
	protected final boolean isBuiltInStartTagGrammarWithAtXsiTypeOnly(Grammar g) {
		boolean ret = false;
		if ( g.getNumberOfEvents() == 1) {
			Production p0 = g.getProduction(0);
			Event ev0 = p0.getEvent();
			if(ev0.isEventType(EventType.ATTRIBUTE)) {
				Attribute at = (Attribute) ev0;
				QNameContext qn0 = at.getQNameContext();
				if(qn0.getNamespaceUriID() == 2 && qn0.getLocalNameID() == 1) {
					// AT type cast only
					ret = true;
				}
			}
		}
		
		return ret;
	}
	
	
	protected final StartElement getGlobalStartElement(QNameContext qnc) {
		StartElement se = qnc.getGlobalStartElement();
		if(se == null) {
			// no global StartElement stemming from schema-informed grammars
			// --> check for previous runtime SE
			se = runtimeGlobalElements.get(qnc);
			if(se == null) {
				// no global runtime grammar yet
				se = new StartElement(qnc);
				se.setGrammar(new BuiltInStartTag());
				runtimeGlobalElements.put(qnc, se);
			}
		}
		
		return se;
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

		// clear runtime data
		this.runtimeGlobalElements.clear();
		for(int i=0; i<nextUriID; i++) {
			this.runtimeUris.get(i).clear();
		}
		
		// re-set schema-informed grammar IDs
		nextQNameID = this.grammarContext.getNumberOfGrammarQNameContexts();
		nextUriID = this.gUris;

		// possible document/fragment grammar
		Grammar startRule = exiFactory.isFragment() ? grammar
				.getFragmentGrammar() : grammar.getDocumentGrammar();

		// (core) context
		elementContextStackIndex = 0;
		elementContextStack[elementContextStackIndex] = elementContext = new ElementContext(
				null, startRule);
	}

	protected final void declarePrefix(String pfx, String uri) {
		declarePrefix(new NamespaceDeclaration(uri, pfx));
	}

	protected final void declarePrefix(NamespaceDeclaration nsDecl) {
		if (elementContext.nsDeclarations == null) {
			elementContext.nsDeclarations = new ArrayList<NamespaceDeclaration>();
		}
		assert (!elementContext.nsDeclarations.contains(nsDecl));
		elementContext.nsDeclarations.add(nsDecl);
	}

	protected final String getURI(String prefix) {
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

	protected final String getPrefix(String uri) {
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
	
	
	protected RuntimeUriContext addUri(String uri) {
		RuntimeUriContext ruc;
		int uriID = nextUriID++;
		if (uriID < runtimeUris.size()) {
			// re-use existing entry
			ruc = runtimeUris.get(uriID);
			// Update namespace uri (ID is already ok)
			ruc.setNamespaceUri(uri);
		} else {
			// create new uri entry
			ruc = new RuntimeUriContext(uriID, uri); 
			this.runtimeUris.add(ruc);
		}
		
		return ruc;
	}
	
	protected int getNumberOfUris() {
		return nextUriID;
	}
	
	protected RuntimeUriContext getUri(String namespaceUri) {
		for(int i=0; i<nextUriID && i<runtimeUris.size(); i++) {
			RuntimeUriContext ruc = runtimeUris.get(i);
			if(ruc.namespaceUri.equals(namespaceUri)) {
				return ruc;
			}
		}
		
		return null;
	}
	
	protected RuntimeUriContext getUri(int namespaceUriID) {
		assert(namespaceUriID >= 0 && namespaceUriID < nextUriID); // this.getNumberOfUris()
		return runtimeUris.get(namespaceUriID);	

		
	}

	/*
	 * 
	 */
	protected void throwWarning(String message) {
		errorHandler.warning(new EXIException(message + ", options="
				+ exiFactory.getFidelityOptions()));
		// System.err.println(message);
	}

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
			return this.prefix;
		}
	}
	
	final class RuntimeUriContext
//	implements UriContext
	{
		final int namespaceUriID;
		private String namespaceUri; // may be modified in subsequent runs
		final GrammarUriContext guc; // null if not present
		
		List<QNameContext> qnames;
		List<String> prefixes;
		
		public RuntimeUriContext(int namespaceUriID, String namespaceUri) {
			this(null, namespaceUriID, namespaceUri);
		}
		
		public RuntimeUriContext(GrammarUriContext guc) {
			this(guc, guc.getNamespaceUriID(), guc.getNamespaceUri());
		}
		
		private RuntimeUriContext(GrammarUriContext guc, int namespaceUriID, String namespaceUri) {
			this.guc = guc;
			this.namespaceUriID = namespaceUriID;
			this.namespaceUri = namespaceUri;
		}
		
		protected void clear() {
			if(guc == null) {
				namespaceUri = null;	
			}
			// Note: re-use existing lists for subsequent runs
			if(qnames != null && qnames.size() > 0) {
				qnames.clear();
			}
			if(preservePrefix && prefixes != null && prefixes.size() > 0) {
				prefixes.clear();
			}
		}
		
		protected QNameContext getQNameContext(String localName) {
			QNameContext qnc = null;
			if(guc != null) {
				qnc = guc.getQNameContext(localName);
			}
			if (qnc == null) {
				// check runtime qnames
				if(qnames != null && qnames.size() != 0) {
					// Idea: recent entries more likely?
					for(int i=qnames.size()-1; i>=0; i--) {
						qnc = qnames.get(i);
						if(qnc.getLocalName().equals(localName)) {
							return qnc;
						}
					}
					qnc = null; // none found
				}
				
			}
			
			return qnc;
		}
		
		protected QNameContext getQNameContext(int localNameID) {
			QNameContext qnc = null;
			int sub = 0;
			if(guc != null) {
				qnc = guc.getQNameContext(localNameID);
				sub = guc.getNumberOfQNames();
			}
			if (qnc == null) {
				// check runtime qnames
				localNameID -= sub;
				assert(localNameID >= 0 && localNameID <qnames.size());
				qnc = qnames.get(localNameID);
			}
			
			return qnc;
		}
		
		protected int getNumberOfQNames() {
			int n = 0;
			if(guc != null) {
				n = guc.getNumberOfQNames();
			}
			if(qnames != null) {
				n += qnames.size();
			}
			return n;
		}
		
		protected QNameContext addQNameContext(String localName) {
			if(qnames == null) {
				qnames = new ArrayList<QNameContext>();
			}
			int localNameID = getNumberOfQNames();
			QName qName = new QName(namespaceUri, localName);
			int qNameID = nextQNameID++;
			QNameContext qnc = new QNameContext(namespaceUriID, localNameID, qName, qNameID);
			qnames.add(qnc);
			
			return qnc;
		}
		
		
		protected int getNumberOfPrefixes() {
			int pfs = 0;
			if(guc != null) {
				pfs = guc.getNumberOfPrefixes();
			}
			
			if(prefixes != null) {
				assert(preservePrefix);
				pfs += prefixes.size();
			}
			
			return pfs;
		}
		
		protected void addPrefix(String prefix) {
			assert(preservePrefix);
			
			if(prefixes == null) {
				prefixes = new ArrayList<String>();
			}
			prefixes.add(prefix);
		}
		
		protected int getPrefixID(String prefix) {
			assert(preservePrefix);
			
			int id = Constants.NOT_FOUND;
			if(guc != null) {
				id = guc.getPrefixID(prefix);
			}
			if(id == Constants.NOT_FOUND) {
				if(prefixes != null && prefixes.size() != 0) {
					for(int i = 0; i<prefixes.size(); i++) {
						if ( prefixes.get(i).equals(prefix)) {
							return i;
						}
					}					
				}
			}
			
			return id;
		}
		
		protected String getPrefix(int prefixID) {
			String pfx = null;
			int sub = 0;
			if(guc != null) {
				pfx = guc.getPrefix(prefixID);
				sub = guc.getNumberOfPrefixes();
			}
			if(pfx == null) {
				assert(preservePrefix);
				assert(this.prefixes != null);
				prefixID -= sub;
				assert(prefixID >= 0 && prefixID<prefixes.size());
				pfx = prefixes.get(prefixID);
			}
			
			return pfx;
		}

		protected void setNamespaceUri(String namespaceUri) {
			this.namespaceUri = namespaceUri;
		}
		
		protected String getNamespaceUri() {
			return this.namespaceUri;
		}
		

		protected int getNamespaceUriID() {
			return this.namespaceUriID;
		}

		
	}
	
}