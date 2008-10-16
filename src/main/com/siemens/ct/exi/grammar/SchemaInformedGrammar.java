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

package com.siemens.ct.exi.grammar;

import java.util.HashMap;
import java.util.Map;

import com.siemens.ct.exi.grammar.event.AttributeGeneric;
import com.siemens.ct.exi.grammar.event.CharactersGeneric;
import com.siemens.ct.exi.grammar.event.EndDocument;
import com.siemens.ct.exi.grammar.event.EndElement;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.event.StartElementGeneric;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.RuleDocEnd;
import com.siemens.ct.exi.grammar.rule.RuleDocument;
import com.siemens.ct.exi.grammar.rule.RuleFragment;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRuleDocContent;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRuleElement;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRuleFragmentContent;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRuleStartTag;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081016
 */

public class SchemaInformedGrammar extends AbstractGrammar
{
	protected Map<ElementKey, Rule>				elementDispatcher;

	protected Map<ExpandedName, TypeGrammar>	grammarTypes;

	protected final TypeGrammar					urType;

	protected SchemaInformedGrammar ( ExpandedName[] globalElements )
	{
		super ( true );

		// init document
		initDocumentGrammar ( globalElements );

		// allocate memory
		elementDispatcher = new HashMap<ElementKey, Rule> ( );
		grammarTypes = new HashMap<ExpandedName, TypeGrammar> ( );

		// ur-type
		urType = SchemaInformedGrammar.getUrTypeRule ( );
	}

	public static TypeGrammar getUrTypeRule ()
	{
		// ur-Type
		SchemaInformedRule urType1 = new SchemaInformedRuleElement ( );
		urType1.addRule ( new StartElementGeneric ( ), urType1 );
		urType1.addTerminalRule ( new EndElement ( ) );
		urType1.addRule ( new CharactersGeneric ( ), urType1 );

		SchemaInformedRule urType0 = new SchemaInformedRuleStartTag ( urType1 );
		urType0.addRule ( new AttributeGeneric ( ), urType0 );
		urType0.addRule ( new StartElementGeneric ( ), urType1 );
		urType0.addTerminalRule ( new EndElement ( ) );
		urType0.addRule ( new CharactersGeneric ( ), urType1 );
		urType0.setHasNamedSubtypes ( true );
		urType0.setFirstElementRule ( );

		// empty ur-Type
		SchemaInformedRule emptyUrType0 = new SchemaInformedRuleElement ( );
		emptyUrType0.addRule ( new AttributeGeneric ( ), emptyUrType0 );
		emptyUrType0.addTerminalRule ( new EndElement ( ) );
		emptyUrType0.setFirstElementRule ( );

		// nillable ?
		urType0.setNillable ( false, emptyUrType0 );

		return new TypeGrammar ( urType0, emptyUrType0 );
	}

	protected void setUriEntries ( String[] uris )
	{
		this.uris = uris;
	}

	protected void setLocalNamesEntries ( ExpandedName[] localNames )
	{
		this.localNames = localNames;
	}

	protected void addElementRule ( ElementKey elementKey, Rule r )
	{
		elementDispatcher.put ( elementKey, r );
	}

	public Rule getRule ( ElementKey elementKey )
	{
		return elementDispatcher.get ( elementKey );
	}

	protected void addTypeGrammar ( ExpandedName typeName, TypeGrammar typeGrammar )
	{
		grammarTypes.put ( typeName, typeGrammar );
	}

	public TypeGrammar getTypeGrammar ( String namespaceURI, String name )
	{
		if ( namespaceURI == null || name == null)
		{
			return null;
		}
		else
		{
			ExpandedName en = new ExpandedName ( namespaceURI, name );
			return grammarTypes.get ( en );			
		}
	}

	public TypeGrammar getUrType ()
	{
		return urType;
	}

	private void initDocumentGrammar ( ExpandedName[] globalElements )
	{
		/*
		 * rule (DocEnd)
		 */
		builtInDocEndGrammar = new RuleDocEnd ( "DocEnd" );
		builtInDocEndGrammar.addTerminalRule ( new EndDocument ( ) );

		/*
		 * rule (DocContent)
		 */
		builtInDocContentGrammar = new SchemaInformedRuleDocContent ( builtInDocEndGrammar, "DocContent" );

		/*
		 * rule (DocContent) add global elements
		 */
		for ( ExpandedName globalElement : globalElements )
		{
			StartElement se = new StartElement ( globalElement.getNamespaceURI ( ), globalElement.getLocalName ( ) );
			builtInDocContentGrammar.addRule ( se, builtInDocEndGrammar );
		}

		/*
		 * rule (Document)
		 */
		builtInDocumentGrammar = new RuleDocument ( builtInDocContentGrammar, "Document" );
		builtInDocumentGrammar.addRule ( new StartDocument ( ), builtInDocContentGrammar );
	}

	public Rule getBuiltInFragmentGrammar ()
	{
		// Note: create new instance since fragment content grammar may change
		// over time

		/*
		 * Fragment Content
		 */
		Rule builtInFragmentContentGrammar = new SchemaInformedRuleFragmentContent ( "FragmentContent" );

		// all the unique qnames of elements sorted lexicographically, first by
		// localName, then by uri
		// TODO sorting & *overlapping qname*
		for ( ElementKey elKey : elementDispatcher.keySet ( ) )
		{
			ExpandedName name = elKey.getName ( );
			StartElement se = new StartElement ( name.getNamespaceURI ( ), name.getLocalName ( ) );
			builtInFragmentContentGrammar.addRule ( se, builtInFragmentContentGrammar );
		}

		/*
		 * Fragment
		 */
		Rule builtInFragmentGrammar = new RuleFragment ( builtInFragmentContentGrammar, "Fragment" );
		builtInFragmentGrammar.addRule ( new StartDocument ( ), builtInFragmentContentGrammar );

		return builtInFragmentGrammar;
	}

}
