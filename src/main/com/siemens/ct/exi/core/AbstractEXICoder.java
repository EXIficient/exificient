/*
 * Copyright (C) 2007, 2008 Siemens AG
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

import java.util.HashMap;
import java.util.Map;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammar.ElementKey;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.SchemaInformedGrammar;
import com.siemens.ct.exi.grammar.TypeGrammar;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.AttributeGeneric;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.CharactersGeneric;
import com.siemens.ct.exi.grammar.event.EndDocument;
import com.siemens.ct.exi.grammar.event.EndElement;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.event.StartElementGeneric;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaLessRuleStartTag;
import com.siemens.ct.exi.helpers.DefaultErrorHandler;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.UnsynchronizedStack;

/**
 * Shared functionality between EXI Encoder and Decoder.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081003
 */

public abstract class AbstractEXICoder
{
	// cached events
	protected final EndDocument						eventED;
	protected final StartElement					eventSE;
	protected final StartElementGeneric				eventSEg;
	protected final EndElement						eventEE;
	protected final Attribute						eventAT;
	protected final AttributeGeneric				eventATg;
	protected final Characters						eventCH;
	protected final CharactersGeneric				eventCHg;

	// factory
	protected EXIFactory							exiFactory;
	protected Grammar								grammar;

	// error handler
	protected ErrorHandler							errorHandler;

	// rules learned while coding ( uri -> localName -> rule)
	protected Map<String, HashMap<String, Rule>>	runtimeDispatcher;

	// saves scope for character StringTable & Channels as well as for
	// content-dispatcher
	protected UnsynchronizedStack<String>			scopeURI;
	protected UnsynchronizedStack<String>			scopeLocalName;

	protected UnsynchronizedStack<String>			scopeTypeURI;
	protected UnsynchronizedStack<String>			scopeTypeLocalName;

	// stack when traversing the EXI document
	protected UnsynchronizedStack<Rule>				openRules;

	// keys for fetching new rule
	private ElementKey								ruleKey;
	private ExpandedName							ruleName;
	private ExpandedName							ruleScope;
	private ExpandedName							ruleScopeType;

	public AbstractEXICoder ( EXIFactory exiFactory )
	{
		this.exiFactory = exiFactory;
		this.grammar = exiFactory.getGrammar ( );

		// setup final events
		eventED = new EndDocument ( );
		eventSE = new StartElement ( null, null );
		eventSEg = new StartElementGeneric ( );
		eventEE = new EndElement ( );
		eventAT = new Attribute ( null, null );
		eventATg = new AttributeGeneric ( );
		eventCH = new Characters ( null, null );
		eventCHg = new CharactersGeneric ( );

		// allocate expanded names for keys
		ruleKey = new ElementKey ( null );
		ruleName = new ExpandedName ( null, "" );
		ruleScope = new ExpandedName ( null, "" );
		ruleScopeType = new ExpandedName ( null, "" );

		// use default error handler per default
		this.errorHandler = new DefaultErrorHandler ( );

		// init once
		initOnce ( );
	}

	public void setErrorHandler ( ErrorHandler errorHandler )
	{
		this.errorHandler = errorHandler;
	}

	protected void initOnce ()
	{
		// runtime lists
		runtimeDispatcher = new HashMap<String, HashMap<String, Rule>> ( );
		openRules = new UnsynchronizedStack<Rule> ( );

		// scope
		scopeURI = new UnsynchronizedStack<String> ( );
		scopeLocalName = new UnsynchronizedStack<String> ( );
		// scopeType
		scopeTypeURI = new UnsynchronizedStack<String> ( );
		scopeTypeLocalName = new UnsynchronizedStack<String> ( );
	}

	// re-init (rule stack etc)
	protected void initForEachRun () throws EXIException
	{
		// rules learned while coding
		runtimeDispatcher.clear ( );
		// stack when traversing the EXI document
		openRules.clear ( );
		// reset scope root element (unknown)
		scopeURI.clear ( );
		scopeLocalName.clear ( );
		pushScope ( null, null );
		// reset scopeType
		scopeTypeURI.clear ( );
		scopeTypeLocalName.clear ( );
		pushScopeType ( null, null );
	}

	protected final void pushScope ( String uri, String localName )
	{
		scopeURI.addLast ( uri );
		scopeLocalName.addLast ( localName );
	}

	protected final void pushScopeType ( String uri, String localName )
	{
		scopeTypeURI.addLast ( uri );
		scopeTypeLocalName.addLast ( localName );
	}

	protected final void popScope ()
	{
		scopeURI.removeLast ( );
		scopeLocalName.removeLast ( );

		// TODO pop scope xsi:type environment as well
		// mhhh, needs xsi:type and element matching
	}

	public final String getScopeURI ()
	{
		return scopeURI.peekLast ( );
	}

	public final String getScopeLocalName ()
	{
		return scopeLocalName.peekLast ( );
	}

	protected final String getScopeTypeURI ()
	{
		return scopeTypeURI.peekLast ( );
	}

	protected final String getScopeTypeLocalName ()
	{
		return scopeTypeLocalName.peekLast ( );
	}

	protected final Rule getCurrentRule ()
	{
		assert ( !openRules.isEmpty ( ) );

		return openRules.peekLast ( );
	}

	protected final void replaceRuleAtTheTop ( Rule top )
	{
		assert ( !openRules.isEmpty ( ) );
		assert ( top != null );

		openRules.replaceLast ( top );
	}

	protected final void pushRule ( Rule r )
	{
		assert ( r != null );

		openRules.addLast ( r );
	}

	protected final void popRule ()
	{
		assert ( !openRules.isEmpty ( ) );

		openRules.removeLast ( );
	}

	protected final FidelityOptions getFidelityOptions ()
	{
		return exiFactory.getFidelityOptions ( );
	}

	protected void pushRule ( final String namespaceURI, final String localName )
	{
		Rule ruleToPush = null;

		if ( grammar.isSchemaInformed ( ) )
		{
			// element rule known from schema ?
			if ( ( ruleToPush = getSchemaRuleForElement ( namespaceURI, localName ) ) == null )
			{
				// if rule not present use ur-type
				TypeGrammar urType = ( (SchemaInformedGrammar) grammar ).getUrType ( );
				ruleToPush = urType.getType ( );
			}
		}
		else
		{
			// runtime-grammar
			ruleToPush = getRuntimeRuleForElement ( namespaceURI, localName );
		}

		// pushing the rule on the top of the stack
		pushRule ( ruleToPush );
	}

	private Rule getSchemaRuleForElement ( final String namespaceURI, final String localName )
	{
		Rule ruleSchema = null;

		// 1st step (name only)
		ruleName.setLocalName ( localName );
		ruleName.setNamespaceURI ( namespaceURI );
		ruleKey.setName ( ruleName );
		ruleKey.setScope ( null );
		ruleKey.setScopeType ( null );

		ruleSchema = grammar.getRule ( ruleKey );

		if ( ruleSchema == null )
		{
			// 2nd step, including scope
			if ( getScopeLocalName ( ) != null )
			{
				ruleScope.setLocalName ( getScopeLocalName ( ) );
				ruleScope.setNamespaceURI ( getScopeURI ( ) );
				ruleKey.setScope ( ruleScope );
			}
			else
			{
				ruleKey.setScope ( null );
			}

			ruleSchema = exiFactory.getGrammar ( ).getRule ( ruleKey );

			if ( ruleSchema == null && getScopeTypeLocalName ( ) != null )
			{
				// include type
				ruleKey.setScope ( null );
				ruleScopeType.setLocalName ( getScopeTypeLocalName ( ) );
				ruleScopeType.setNamespaceURI ( getScopeTypeURI ( ) );
				ruleKey.setScopeType ( ruleScopeType );

				ruleSchema = exiFactory.getGrammar ( ).getRule ( ruleKey );
			}
		}

		return ruleSchema;
	}

	private Rule getRuntimeRuleForElement ( String namespaceURI, String localName )
	{
		// TODO replace containsKey replace with get & check against null

		// runtime-grammar
		if ( runtimeDispatcher.containsKey ( namespaceURI ) )
		{
			if ( !runtimeDispatcher.get ( namespaceURI ).containsKey ( localName ) )
			{
				// URI known, localName unknown
				runtimeDispatcher.get ( namespaceURI ).put ( localName, new SchemaLessRuleStartTag ( ) );
			}
		}
		else
		{
			// URI & localName unknown
			runtimeDispatcher.put ( namespaceURI, new HashMap<String, Rule> ( ) );
			runtimeDispatcher.get ( namespaceURI ).put ( localName, new SchemaLessRuleStartTag ( ) );
		}

		return runtimeDispatcher.get ( namespaceURI ).get ( localName );
	}

}
