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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.helpers.NamespaceSupport;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammar.ElementKey;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.GrammarSchemaInformed;
import com.siemens.ct.exi.grammar.TypeGrammar;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.AttributeGeneric;
import com.siemens.ct.exi.grammar.event.AttributeNS;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.CharactersGeneric;
import com.siemens.ct.exi.grammar.event.EndDocument;
import com.siemens.ct.exi.grammar.event.EndElement;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.event.StartElementGeneric;
import com.siemens.ct.exi.grammar.event.StartElementNS;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.RuleStartTagSchemaLess;
import com.siemens.ct.exi.helpers.DefaultErrorHandler;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * Shared functionality between EXI Encoder and Decoder.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090331
 */

public abstract class AbstractEXICoder {
	// cached events
	protected final EndDocument eventED;
	protected final StartElement eventSE;
	protected final StartElementNS eventSE_NS;
	protected final StartElementGeneric eventSEg;
	protected final EndElement eventEE;
	protected final Attribute eventAT;
	protected final AttributeNS eventAT_NS;
	protected final AttributeGeneric eventATg;
	protected final Characters eventCH;
	protected final CharactersGeneric eventCHg;

	// factory
	protected EXIFactory exiFactory;
	protected Grammar grammar;
	protected FidelityOptions fidelityOptions;

	// error handler
	protected ErrorHandler errorHandler;

	// rules learned while coding ( uri -> localName -> rule)
	protected Map<String, Map<String, Rule>> runtimeDispatcher;

	// saves scope for character StringTable & Channels as well as for
	// content-dispatcher
	protected List<String> scopeURI;
	protected List<String> scopeLocalName;

	protected List<String> scopeTypeURI;
	protected List<String> scopeTypeLocalName;

	// namespaces/prefixes
	protected NamespaceSupport namespaces;

	// currentRule and rule stack when traversing the EXI document
	protected List<Rule> openRules;
	protected Rule currentRule;

	// keys for fetching new rule
	private ElementKey ruleKey;
	private ExpandedName ruleName;
	private ExpandedName ruleScope;
	private ExpandedName ruleScopeType;

	public AbstractEXICoder(EXIFactory exiFactory) {
		this.exiFactory = exiFactory;
		this.grammar = exiFactory.getGrammar();
		this.fidelityOptions = exiFactory.getFidelityOptions();

		// setup final events
		eventED = new EndDocument();
		eventSE = new StartElement(null, null);
		eventSE_NS = new StartElementNS(null);
		eventSEg = new StartElementGeneric();
		eventEE = new EndElement();
		eventAT = new Attribute(null, null);
		eventAT_NS = new AttributeNS(null);
		eventATg = new AttributeGeneric();
		eventCH = new Characters(null, null);
		eventCHg = new CharactersGeneric();

		// allocate expanded names for keys
		ruleKey = new ElementKey(null);
		ruleName = new ExpandedName(null, "");
		ruleScope = new ExpandedName(null, "");
		ruleScopeType = new ExpandedName(null, "");

		// namespaces/prefixes
		namespaces = new NamespaceSupport();

		// use default error handler per default
		this.errorHandler = new DefaultErrorHandler();

		// init once
		initOnce();
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	protected void initOnce() {
		// runtime lists
		runtimeDispatcher = new HashMap<String, Map<String, Rule>>();
		openRules = new ArrayList<Rule>();
		// scope
		scopeURI = new ArrayList<String>();
		scopeLocalName = new ArrayList<String>();
		// scopeType
		scopeTypeURI = new ArrayList<String>();
		scopeTypeLocalName = new ArrayList<String>();
	}

	// re-init (rule stack etc)
	protected void initForEachRun() throws EXIException {
		// rules learned while coding
		runtimeDispatcher.clear();
		// stack when traversing the EXI document
		openRules.clear();
		currentRule = null;
		pushRule(null);
		// reset scope root element (unknown)
		scopeURI.clear();
		scopeLocalName.clear();
		pushScope(null, null);
		// reset scopeType
		scopeTypeURI.clear();
		scopeTypeLocalName.clear();
		pushScopeType(null, null);
		// namespaces/prefixes
		namespaces.reset();
	}

	protected final void pushScopeType(String uri, String localName) {
		scopeTypeURI.add(uri);
		scopeTypeLocalName.add(localName);
	}
	
	protected void pushScope(final String namespaceURI, final String localName) {
		//	push scope
		scopeURI.add(namespaceURI);
		scopeLocalName.add(localName);

		// push NS context
		namespaces.pushContext();
	}

	protected void popScope() {
		scopeURI.remove(scopeURI.size() - 1);
		scopeLocalName.remove(scopeLocalName.size() - 1);

		// pop NS context
		namespaces.popContext();

		// TODO pop scope xsi:type environment as well
		// mhhh, needs xsi:type and element matching
	}

	public final String getScopeURI() {
		return scopeURI.get(scopeURI.size() - 1);
	}

	public final String getScopeLocalName() {
		return scopeLocalName.get(scopeLocalName.size() - 1);
	}

	public NamespaceSupport getNamespaces() {
		return this.namespaces;
	}

	protected final String getScopeTypeURI() {
		return scopeTypeURI.get(scopeTypeURI.size() - 1);
	}

	protected final String getScopeTypeLocalName() {
		return scopeTypeLocalName.get(scopeTypeLocalName.size() - 1);
	}

	protected final void replaceRuleAtTheTop(Rule top) {
		assert (!openRules.isEmpty());
		assert (top != null);

		if (top != currentRule) {
			// openRules.replaceLast(currentRule = top);
			openRules.set(openRules.size() - 1, currentRule = top);
		}
	}

	protected final void pushRule(Rule r) {
		// assert ( r != null );

		// openRules.addLast(currentRule = r);
		openRules.add(currentRule = r);
	}

	protected final void popRule() {
		assert (!openRules.isEmpty());

		// openRules.removeLast();
		// currentRule = openRules.peekLast();
		int size = openRules.size();
		openRules.remove(size - 1);
		currentRule = openRules.get(size - 2);
	}

	protected void pushRule(final String namespaceURI, final String localName) {
		Rule ruleToPush = null;

		if (grammar.isSchemaInformed()) {
			// element rule known from schema ?
			if ((ruleToPush = getSchemaRuleForElement(namespaceURI, localName)) == null) {
				// if rule not present use ur-type
				TypeGrammar urType = ((GrammarSchemaInformed) grammar)
						.getUrType();
				ruleToPush = urType.getType();
			}
		} else {
			// runtime-grammar
			ruleToPush = getRuntimeRuleForElement(namespaceURI, localName);
		}

		// pushing the rule on the top of the stack
		pushRule(ruleToPush);
	}

	private Rule getSchemaRuleForElement(final String namespaceURI,
			final String localName) {
		Rule ruleSchema = null;

		// 1st step (name only)
		ruleName.setLocalName(localName);
		ruleName.setNamespaceURI(namespaceURI);
		ruleKey.setName(ruleName);
		ruleKey.setScope(null);
		ruleKey.setScopeType(null);

		ruleSchema = grammar.getRule(ruleKey);

		if (ruleSchema == null) {
			// 2nd step, including scope
			if (getScopeLocalName() != null) {
				ruleScope.setLocalName(getScopeLocalName());
				ruleScope.setNamespaceURI(getScopeURI());
				ruleKey.setScope(ruleScope);
			} else {
				ruleKey.setScope(null);
			}

			ruleSchema = grammar.getRule(ruleKey);

			if (ruleSchema == null && getScopeTypeLocalName() != null) {
				// include type
				ruleKey.setScope(null);
				ruleScopeType.setLocalName(getScopeTypeLocalName());
				ruleScopeType.setNamespaceURI(getScopeTypeURI());
				ruleKey.setScopeType(ruleScopeType);

				ruleSchema = grammar.getRule(ruleKey);
			}
		}

		return ruleSchema;
	}

	private Rule getRuntimeRuleForElement(String namespaceURI, String localName) {
		Map<String, Rule> mapNS;
		Rule r;

		// runtime-grammar
		if ((mapNS = runtimeDispatcher.get(namespaceURI)) == null) {
			// URI & localName unknown
			mapNS = new HashMap<String, Rule>();
			runtimeDispatcher.put(namespaceURI, mapNS);
			r = new RuleStartTagSchemaLess();
			mapNS.put(localName, r);
		} else {
			if ((r = mapNS.get(localName)) == null) {
				// URI known, localName unknown
				r = new RuleStartTagSchemaLess();
				mapNS.put(localName, r);
			}
		}

		return r;
	}
}
