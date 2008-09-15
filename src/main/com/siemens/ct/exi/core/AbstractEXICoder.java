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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.ErrorHandler;
import com.siemens.ct.exi.grammar.ElementKey;
import com.siemens.ct.exi.grammar.Grammar;
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

/**
 * Shared functionality between EXI Encoder and Decoder.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080915
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

	// error handler
	protected ErrorHandler							errorHandler;

	// rules learned while coding ( uri -> localName -> rule)
	protected Map<String, HashMap<String, Rule>>	runtimeDispatcher;

	// saves scope for character StringTable & Channels as well as for
	// content-dispatcher
	protected List<String>							scopeURI;
	protected List<String>							scopeLocalName;

	protected List<String>							scopeTypeURI;
	protected List<String>							scopeTypeLocalName;

	// stack when traversing the EXI document
	protected List<Rule>							openRules;

	public AbstractEXICoder ( EXIFactory exiFactory )
	{
		this.exiFactory = exiFactory;

		// setup final events
		eventED = new EndDocument ( );
		eventSE = new StartElement ( null, null );
		eventSEg = new StartElementGeneric ( );
		eventEE = new EndElement ( );
		eventAT = new Attribute ( null, null );
		eventATg = new AttributeGeneric ( );
		eventCH = new Characters ( null, null );
		eventCHg = new CharactersGeneric ( );

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
		openRules = new ArrayList<Rule> ( );

		// scope
		scopeURI = new ArrayList<String> ( );
		scopeLocalName = new ArrayList<String> ( );
		// scopeType
		scopeTypeURI = new ArrayList<String> ( );
		scopeTypeLocalName = new ArrayList<String> ( );
	}

	// re-init (rule stack etc)
	protected void initForEachRun () throws EXIException
	{
		// rules learned while coding
		runtimeDispatcher.clear ( );
		// stack when traversing the EXI document
		openRules.clear ( );
		// scope
		scopeURI.clear ( );
		scopeLocalName.clear ( );
		// scope for root element (unknown)
		pushScope ( null, null );
		pushScopeType ( null, null );
	}

	protected final void pushScope ( String uri, String localName )
	{
		scopeURI.add ( uri );
		scopeLocalName.add ( localName );
	}

	protected final void pushScopeType ( String uri, String localName )
	{
		scopeTypeURI.add ( uri );
		scopeTypeLocalName.add ( localName );
	}

	protected final void popScope ()
	{
		scopeURI.remove ( scopeURI.size ( ) - 1 );
		scopeLocalName.remove ( scopeLocalName.size ( ) - 1 );

		// TODO pop scope xsi:type environment as well
		// mhhh, needs xsi:type and element matching
	}

	public final String getScopeURI ()
	{
		return scopeURI.get ( scopeURI.size ( ) - 1 );
	}

	public final String getScopeLocalName ()
	{
		return scopeLocalName.get ( scopeLocalName.size ( ) - 1 );
	}

	protected final String getScopeTypeURI ()
	{
		return scopeTypeURI.get ( scopeTypeURI.size ( ) - 1 );
	}

	protected final String getScopeTypeLocalName ()
	{
		return scopeTypeLocalName.get ( scopeTypeLocalName.size ( ) - 1 );
	}

	protected final Rule getCurrentRule ()
	{
		assert ( !openRules.isEmpty ( ) );

		return openRules.get ( openRules.size ( ) - 1 );
	}

	protected final Rule replaceRuleAtTheTop ( Rule top )
	{
		assert ( !openRules.isEmpty ( ) );

		return openRules.set ( openRules.size ( ) - 1, top );
	}

	protected final void pushRule ( Rule r )
	{
		openRules.add ( r );
	}

	protected final void popRule ()
	{
		assert ( !openRules.isEmpty ( ) );

		openRules.remove ( openRules.size ( ) - 1 );
	}

	protected final FidelityOptions getFidelityOptions ()
	{
		return exiFactory.getFidelityOptions ( );
	}

	protected Rule getRuleForElement ( String namespaceURI, String localName )
	{
		Grammar g = exiFactory.getGrammar ( );

		Rule ruleSchema = null;

		if ( g.isSchemaInformed ( ) )
		{
			// 1st step
			ExpandedName name = new ExpandedName ( namespaceURI, localName );
			ElementKey key = new ElementKey ( name );
			ruleSchema = g.getRule ( key );

			if ( ruleSchema == null )
			{
				// 2nd step, including scope
				ExpandedName scope = null;
				if ( getScopeLocalName ( ) != null )
				{
					scope = new ExpandedName ( getScopeURI ( ), getScopeLocalName ( ) );
				}
				key.setScope ( scope );
				ruleSchema = exiFactory.getGrammar ( ).getRule ( key );

				if ( ruleSchema == null && getScopeTypeLocalName ( ) != null )
				{
					// include type
					key.setScope ( null );
					ExpandedName scopeType = new ExpandedName ( getScopeTypeURI ( ), getScopeTypeLocalName ( ) );
					key.setScopeType ( scopeType );

					ruleSchema = exiFactory.getGrammar ( ).getRule ( key );
				}
			}
		}

		if ( ruleSchema == null )
		{
			// runtime-grammar
			return getRuntimeRuleForElement ( namespaceURI, localName );
		}
		else
		{
			// schema-informed grammar
			return ruleSchema;
		}
	}

	protected Rule getRuntimeRuleForElement ( String namespaceURI, String localName )
	{
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
