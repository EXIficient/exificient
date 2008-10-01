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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import javax.xml.XMLConstants;

import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.LSInput;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.BuiltIn;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.CharactersGeneric;
import com.siemens.ct.exi.grammar.event.EndElement;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.Lambda;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRuleContentAll;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRuleElement;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRuleStartTag;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080924
 */

/*
 * 
 * Note: Schema-Mapping partly influenced by
 * http://www.ltg.ed.ac.uk/~ht/XML_Europe_2003.html
 * 
 */
public class XSDGrammarBuilder implements DOMErrorHandler
{
	protected Map<ElementKey, Rule>						elementDispatcher;

	protected Map<ExpandedName, TypeGrammar>			grammarTypes;

	// ////////////////

	// sorted LocalNames (pre-initializing LocalName Partition)
	protected Set<ExpandedName>							sortedLocalNames;

	// ///////////////

	private static final Event							END_ELEMENT		= new EndElement ( );

	private static final Event							LAMBDA			= new Lambda ( );

	private static boolean								RESOLVE_LAMBDAS	= true;				// Default

	private XSModel										xsModel;

	// avoiding recursive element handling
	private List<XSElementDeclaration>					handledElements;

	private Stack<ExpandedName>							enclosingElements;

	private Map<ExpandedName, SchemaInformedRule>		ruleForType;

	private List<XSElementDeclaration>					outstandingElements;
	private Map<XSElementDeclaration, XSTypeDefinition>	type4Element;

	// XML Schema loader
	private XSLoader									xsLoader;

	// errors while schema parsing
	private List<DOMError>								schemaParsingErrors;

	protected XSDGrammarBuilder ()
	{
		// allocate memory
		elementDispatcher = new HashMap<ElementKey, Rule> ( );
		handledElements = new Vector<XSElementDeclaration> ( );
		enclosingElements = new Stack<ExpandedName> ( );
		ruleForType = new HashMap<ExpandedName, SchemaInformedRule> ( );
		grammarTypes = new HashMap<ExpandedName, TypeGrammar> ( );
		sortedLocalNames = new TreeSet<ExpandedName> ( );
		outstandingElements = new ArrayList<XSElementDeclaration> ( );
		type4Element = new HashMap<XSElementDeclaration, XSTypeDefinition> ( );
		schemaParsingErrors = new ArrayList<DOMError> ( );

		// schema loader
		try
		{
			xsLoader = getXSLoader ( );
		}
		catch ( Exception e )
		{
			// inidicates problem
			xsLoader = null;
		}
	}

	protected void init ()
	{
		elementDispatcher.clear ( );
		handledElements.clear ( );
		enclosingElements.clear ( );
		enclosingElements.push ( null ); // default scope
		ruleForType.clear ( );
		grammarTypes.clear ( );
		sortedLocalNames.clear ( );
		outstandingElements.clear ( );
		type4Element.clear ( );
	}

	public static XSDGrammarBuilder newInstance ()
	{
		return new XSDGrammarBuilder ( );
	}

	public SchemaInformedGrammar build ( XSModel xsModel ) throws EXIException
	{
		if ( xsModel == null || schemaParsingErrors.size ( ) > 0 )
		{
			String exMsg = "Problem occured while building XML Schema Model (XSModel)!";

			for ( int i = 0; i < schemaParsingErrors.size ( ); i++ )
			{
				exMsg += "\n. " + schemaParsingErrors.get ( i ).getMessage ( );
			}

			throw new EXIException ( exMsg );
		}
		this.xsModel = xsModel;

		// init
		init ( );

		// initialize grammars
		ExpandedName[] globalElements = initGrammars ( );

		SchemaInformedGrammar sig = new SchemaInformedGrammar ( globalElements );

		// initialize URI table entries
		String[] uris = initURITableEntries ( );
		sig.setUriEntries ( uris );

		// sorted LocalNames (copy to array)
		ExpandedName[] localNames = new ExpandedName[sortedLocalNames.size ( )];
		sortedLocalNames.toArray ( localNames );
		sig.setLocalNamesEntries ( localNames );

		// element dispatcher
		for ( ElementKey elementKey : elementDispatcher.keySet ( ) )
		{
			sig.addElementRule ( elementKey, elementDispatcher.get ( elementKey ) );
		}

		// type grammar
		for ( ExpandedName typeName : grammarTypes.keySet ( ) )
		{
			sig.addTypeGrammar ( typeName, grammarTypes.get ( typeName ) );
		}

		return sig;
	}

	public SchemaInformedGrammar build ( String xsdLocation ) throws EXIException
	{
		return build ( getXSModel ( xsdLocation ) );
	}

	public SchemaInformedGrammar build ( InputStream inputStream ) throws EXIException
	{
		return build ( getXSModel ( inputStream ) );
	}

	private XSLoader getXSLoader () throws ClassCastException, ClassNotFoundException, InstantiationException,
			IllegalAccessException
	{
		// get DOM Implementation using DOM Registry
		System.setProperty ( DOMImplementationRegistry.PROPERTY, "org.apache.xerces.dom.DOMXSImplementationSourceImpl" );
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance ( );

		XSImplementation impl = (XSImplementation) registry.getDOMImplementation ( "XS-Loader" );

		XSLoader schemaLoader = impl.createXSLoader ( null );

		DOMConfiguration config = schemaLoader.getConfig ( );

		// set error handler
		config.setParameter ( "error-handler", this );

		// set validation feature
		config.setParameter ( "validate", Boolean.TRUE );

		return schemaLoader;
	}

	public boolean handleError ( DOMError error )
	{
		// collect error(s)
		schemaParsingErrors.add ( error );

		short severity = error.getSeverity ( );
		if ( severity == DOMError.SEVERITY_ERROR )
		{
			String msg = "[xs-error]: " + error.getMessage ( );
			throw new RuntimeException ( msg );
		}

		if ( severity == DOMError.SEVERITY_WARNING )
		{
			String msg = "[xs-warning]: " + error.getMessage ( );
			throw new RuntimeException ( msg );
		}

		return true;
	}

	private void checkXSLoader () throws EXIException
	{
		if ( xsLoader == null )
		{
			throw new EXIException ( "Problems while creating XML Schema loader" );
		}
	}

	public XSModel getXSModel ( String xsd ) throws EXIException
	{
		checkXSLoader ( );

		// reset errors
		schemaParsingErrors.clear ( );

		return xsLoader.loadURI ( xsd );
	}

	public XSModel getXSModel ( LSInput ls ) throws EXIException
	{
		checkXSLoader ( );

		// reset errors
		schemaParsingErrors.clear ( );

		return xsLoader.load ( ls );
	}

	public XSModel getXSModel ( InputStream inputStream ) throws EXIException
	{
		LSInput lsInput = new DOMInputImpl ( );
		lsInput.setByteStream ( inputStream );

		return getXSModel ( lsInput );
	}

	private static boolean isNamespacesOfInterest ( String namespaceURI )
	{
		if ( namespaceURI == null || namespaceURI.equals ( XMLConstants.XML_NS_URI )
				|| namespaceURI.equals ( XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI )
				|| namespaceURI.equals ( XMLConstants.W3C_XML_SCHEMA_NS_URI ) )
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private String[] initURITableEntries ()
	{
		StringList namespaces = xsModel.getNamespaces ( );
		TreeSet<String> sortedURIs = new TreeSet<String> ( );

		for ( int i = 0; i < namespaces.getLength ( ); i++ )
		{
			if ( isNamespacesOfInterest ( namespaces.item ( i ) ) )
			{
				sortedURIs.add ( namespaces.item ( i ) );
			}
		}

		// copy to array
		String[] uris = new String[sortedURIs.size ( )];
		sortedURIs.toArray ( uris );

		return uris;
	}

	private void addLocalNameStringEntry ( ExpandedName expName )
	{
		/*
		 * When a schema is provided, the string table (Local-name) is also
		 * pre-populated with the local name of each attribute, element and type
		 * declared in the schema, partitioned by namespace URI and sorted
		 * lexicographically.
		 */
		if ( isNamespacesOfInterest ( expName.namespaceURI ) )
		{
			if ( !sortedLocalNames.contains ( expName ) )
			{
				// new entry
				sortedLocalNames.add ( expName );
			}
		}
	}

	private ExpandedName[] initGrammars ()
	{
		// ExpandedName[] globalElements
		ArrayList<ExpandedName> docElements = new ArrayList<ExpandedName> ( );

		// handle all known types
		XSNamedMap types = xsModel.getComponents ( XSConstants.TYPE_DEFINITION );
		for ( int i = 0; i < types.getLength ( ); i++ )
		{
			XSTypeDefinition td = (XSTypeDefinition) types.item ( i );

			ExpandedName name = new ExpandedName ( td.getNamespace ( ), td.getName ( ) );
			TypeGrammar typeGrammar = translateTypeDefinitionToFSA ( td );
			grammarTypes.put ( name, typeGrammar );
		}

		// for scope-aware parsing (e.g. enclosing elements)
		handledElements.clear ( );

		// global elements
		XSNamedMap globalElements = xsModel.getComponents ( XSConstants.ELEMENT_DECLARATION );
		for ( int i = 0; i < globalElements.getLength ( ); i++ )
		{
			XSElementDeclaration globalElement = (XSElementDeclaration) globalElements.item ( i );

			// add global elements (DocContent)
			docElements.add ( new ExpandedName ( globalElement.getNamespace ( ), globalElement.getName ( ) ) );

			if ( !type4Element.containsKey ( globalElement ) )
			{
				type4Element.put ( globalElement, globalElement.getTypeDefinition ( ) );
			}

			// create rules for global elements (do not have scope)
			translatElementDeclarationToFSA ( globalElement, null );

			// Substitution Groups handled properly ?
			// [...] Note that the head element must be declared as a global
			// element [...]
			// http://www.w3.org/TR/2001/REC-xmlschema-0-20010502/#SubsGroups
		}

		// outstanding elements ?
		if ( outstandingElements.size ( ) > 0 )
		{
			// INFO: this happens for named complex types
			for ( int i = 0; i < outstandingElements.size ( ); i++ )
			{
				XSElementDeclaration outstandingElement = outstandingElements.get ( i );
				XSTypeDefinition enclosingType = outstandingElement.getEnclosingCTDefinition ( );

				// get (possible) enclosing elements
				ArrayList<ExpandedName> enclosingElements = null;
				if ( enclosingType != null )
				{
					enclosingElements = getPossibleEnclosingElements ( enclosingType );
				}

				if ( enclosingElements == null || enclosingElements.size ( ) == 0 )
				{
					// XSComplexTypeDefinition ct =
					// outstandingElement.getEnclosingCTDefinition ( );

					translatElementDeclarationToFSA ( outstandingElement, null );
				}
				else
				{
					for ( int k = 0; k < enclosingElements.size ( ); k++ )
					{
						translatElementDeclarationToFSA ( outstandingElement, enclosingElements.get ( k ) );
					}
				}
			}

		}

		ExpandedName[] xx = new ExpandedName[docElements.size ( )];
		return docElements.toArray ( xx );
	}

	private ArrayList<ExpandedName> getPossibleEnclosingElements ( XSTypeDefinition enclosingType )
	{
		// check types
		Iterator<XSElementDeclaration> iter = type4Element.keySet ( ).iterator ( );

		ArrayList<ExpandedName> enclosingElements = new ArrayList<ExpandedName> ( );

		while ( iter.hasNext ( ) )
		{
			XSElementDeclaration el = iter.next ( );
			XSTypeDefinition elementType = type4Element.get ( el );

			if ( enclosingType == elementType
					|| enclosingType.derivedFromType ( elementType, XSConstants.DERIVATION_EXTENSION ) )
			{
				// System.out.println( el + " of interest!");
				enclosingElements.add ( new ExpandedName ( el.getNamespace ( ), el.getName ( ) ) );
			}
		}

		return enclosingElements;
	}

	private ArrayList<ElementKey>	unusableKeys	= new ArrayList<ElementKey> ( );

	private void addToElementDispatcher ( ElementKey key, Rule r )
	{
		if ( unusableKeys.contains ( key ) )
		{
			// not usable, no distinction

		}
		else if ( elementDispatcher.containsKey ( key ) )
		{
			Rule existingRule = elementDispatcher.get ( key );

			if ( existingRule != r )
			{
				// remove rule again & mark this key as un-usable
				elementDispatcher.remove ( key );
				unusableKeys.add ( key );
			}
		}
		else
		{
			elementDispatcher.put ( key, r );
		}
	}

	private void addRuleFor ( ExpandedName name, ExpandedName scope, ExpandedName type, SchemaInformedRule r )
	{
		// rule is going to be added 4 times
		// A: name only
		ElementKey keyA = new ElementKey ( name );
		addToElementDispatcher ( keyA, r );
		// B: name & scope only
		if ( scope != null )
		{
			ElementKey keyB = new ElementKey ( name, scope, null );
			addToElementDispatcher ( keyB, r );
		}
		// C: name & type only
		if ( type != null )
		{
			ElementKey keyC = new ElementKey ( name, null, type );
			addToElementDispatcher ( keyC, r );
		}
		if ( scope != null && type != null )
		{
			// D: name & scope & type
			ElementKey keyD = new ElementKey ( name, scope, type );
			addToElementDispatcher ( keyD, r );
		}
	}

	private static Vector<XSAttributeUse> getSortedAttributes ( XSObjectList attributes )
	{

		Vector<String> vQnames = new Vector<String> ( );
		Hashtable<String, XSAttributeUse> ht = new Hashtable<String, XSAttributeUse> ( );

		for ( int i = 0; i < attributes.getLength ( ); i++ )
		{
			XSObject attr = attributes.item ( i );
			if ( attr.getType ( ) == XSConstants.ATTRIBUTE_USE )
			{
				XSAttributeUse attrUse = (XSAttributeUse) attr;
				XSAttributeDeclaration attrDecl = attrUse.getAttrDeclaration ( );

				String key = attrDecl.getName ( ) + attrDecl.getNamespace ( );

				vQnames.addElement ( key );
				ht.put ( key, attrUse );
			}
			else
			{
				throw new IllegalArgumentException ( "[ERROR] Unknown Attribute type: " + attr.getType ( ) );
			}
		}
		Collections.sort ( vQnames );

		// construct sorted vector
		Vector<XSAttributeUse> vSortedAttributes = new Vector<XSAttributeUse> ( );
		for ( int i = 0; i < vQnames.size ( ); i++ )
		{
			XSAttributeUse xsAttr = ht.get ( vQnames.elementAt ( i ) );
			vSortedAttributes.addElement ( xsAttr );
		}

		return vSortedAttributes;
	}

	protected Attribute getAttributeEvent ( XSAttributeUse attrUse )
	{
		XSAttributeDeclaration attrDecl = attrUse.getAttrDeclaration ( );
		XSSimpleTypeDefinition attrTypeDefinition = attrDecl.getTypeDefinition ( );

		// expanded name for string table pre-population
		ExpandedName atName = new ExpandedName ( attrDecl.getNamespace ( ), attrDecl.getName ( ) );
		addLocalNameStringEntry ( atName );

		ExpandedName qNameType;

		if ( attrTypeDefinition.getAnonymous ( ) )
		{
			XSTypeDefinition tdBase = attrTypeDefinition.getBaseType ( );

			if ( tdBase.getName ( ) == null )
			{
				//	
				// System.err.println ( "Abort processing " + tdBase + " (set "
				// + BuiltIn.DEFAULT_VALUE_NAME + ")" );
				qNameType = BuiltIn.DEFAULT_VALUE_NAME;
				// continue;
			}
			else
			{
				qNameType = new ExpandedName ( tdBase.getNamespace ( ), tdBase.getName ( ) );
			}

		}
		else
		{
			qNameType = new ExpandedName ( attrTypeDefinition.getNamespace ( ), attrTypeDefinition.getName ( ) );
		}

		return new Attribute ( atName.namespaceURI, atName.localName, qNameType, BuiltIn
				.getDatatype ( attrTypeDefinition ) );
	}

	private SchemaInformedRule handleAttributes ( SchemaInformedRule ruleContent, SchemaInformedRule ruleContent2,
			XSObjectList attributes )
	{
		SchemaInformedRule ruleCurrent = new SchemaInformedRuleStartTag ( ruleContent2 );
		ruleCurrent.joinRules ( ruleContent );

		if ( attributes != null && attributes.getLength ( ) > 0 )
		{
			// attributes will occur sorted lexically by qname (in EXI Stream)
			Vector<XSAttributeUse> vSortedAttributes = getSortedAttributes ( attributes );

			for ( int i = vSortedAttributes.size ( ) - 1; i >= 0; i-- )
			{
				XSAttributeUse attrUse = vSortedAttributes.elementAt ( i );

				Attribute at = getAttributeEvent ( attrUse );

				SchemaInformedRule newCurrent = new SchemaInformedRuleStartTag ( ruleContent2 );
				newCurrent.addRule ( at, ruleCurrent );

				if ( !attrUse.getRequired ( ) )
				{
					// optional
					newCurrent.joinRules ( ruleCurrent );
				}
				ruleCurrent = newCurrent;
			}
		}

		return ruleCurrent;

	}

	private void handleSubstitutionGroups ( XSElementDeclaration xsElementDeclaration, SchemaInformedRule elementRule,
			SchemaInformedRule s )
	{
		// SubstitutionGroups
		XSObjectList xsSubstitutionGroups = xsModel.getSubstitutionGroup ( xsElementDeclaration );

		if ( xsSubstitutionGroups != null && xsSubstitutionGroups.getLength ( ) > 0 )
		{
			for ( int i = 0; i < xsSubstitutionGroups.getLength ( ); i++ )
			{
				/*
				 * [...] Note that the head element must be declared as a global
				 * element [...]
				 * [http://www.w3.org/TR/2001/REC-xmlschema-0-20010502
				 * /#SubsGroups]
				 */
				XSElementDeclaration xsSGElementDeclaration = (XSElementDeclaration) xsSubstitutionGroups.item ( i );

				StartElement seSG = new StartElement ( xsSGElementDeclaration.getNamespace ( ), xsSGElementDeclaration
						.getName ( ) );

				elementRule.addRule ( seSG, s );

			}
		}
	}

	protected TypeGrammar getTypeGrammar ( String namespaceURI, String name )
	{
		ExpandedName en = new ExpandedName ( namespaceURI, name );
		return grammarTypes.get ( en );
	}

	protected void translatElementDeclarationToFSA ( XSElementDeclaration xsElementDeclaration, ExpandedName scope )
	{
		// handle element recursion
		if ( this.handledElements.contains ( xsElementDeclaration ) )
		{
			return;
		}
		this.handledElements.add ( xsElementDeclaration );

		// expanded name
		ExpandedName elementName = new ExpandedName ( xsElementDeclaration.getNamespace ( ), xsElementDeclaration
				.getName ( ) );

		// add local name entry for string table pre-population
		addLocalNameStringEntry ( elementName );

		// new scope for *inner* elements
		enclosingElements.push ( elementName );

		// type definition
		XSTypeDefinition td = xsElementDeclaration.getTypeDefinition ( );

		TypeGrammar typeGrammar;

		if ( td.getAnonymous ( ) )
		{
			// create new type grammar for an anonymous type
			typeGrammar = translateTypeDefinitionToFSA ( td );
		}
		else
		{
			// fetch existing grammar from pre-processed type
			TypeGrammar tg = getTypeGrammar ( td.getNamespace ( ), td.getName ( ) );

			// *duplicate* first productions to allow different behavior
			// (e.g. property nillable element not type dependent)

			SchemaInformedRule sir = tg.getType ( ).clone ( );
			typeGrammar = new TypeGrammar ( sir, tg.typeEmpty );
		}

		SchemaInformedRule type_i = typeGrammar.getType ( );
		SchemaInformedRule typeEmpty_i = typeGrammar.getTypeEmpty ( );

		// first rule is different in the sense of xsi:type, xsi:nil, NS & SC
		type_i.setFirstElementRule ( );
		type_i.setNillable ( xsElementDeclaration.getNillable ( ), typeEmpty_i );

		// add rule to dispatcher
		ExpandedName scopeType = null;
		XSComplexTypeDefinition enclosingType = xsElementDeclaration.getEnclosingCTDefinition ( );
		if ( enclosingType != null && !enclosingType.getAnonymous ( ) )
		{
			scopeType = new ExpandedName ( enclosingType.getNamespace ( ), enclosingType.getName ( ) );
		}
		addRuleFor ( elementName, scope, scopeType, type_i );

		// remove scope
		enclosingElements.pop ( );
	}

	/**
	 * Given an XML Schema type definition T i , two type grammars are created,
	 * which are denoted by Type i and TypeEmpty i . Type i is a grammar that
	 * fully reflects the type definition of T i , whereas TypeEmpty i is a
	 * grammar that accepts only the attribute uses and attribute wildcards of T
	 * i , if any.
	 * 
	 * @param td
	 * @return
	 */
	protected TypeGrammar translateTypeDefinitionToFSA ( XSTypeDefinition td )
	{
		SchemaInformedRule type_i = null;
		SchemaInformedRule typeEmpty_i = null;

		// simple vs. complex type handling
		if ( td.getTypeCategory ( ) == XSTypeDefinition.COMPLEX_TYPE )
		{
			if ( Constants.XSD_ANY_TYPE.equals ( td.getName ( ) ) && XMLConstants.W3C_XML_SCHEMA_NS_URI.equals ( td.getNamespace ( ) ) )
			{
				//	ur-type
				TypeGrammar urType = SchemaInformedGrammar.getUrTypeRule ( );
				type_i = urType.type;
				typeEmpty_i = urType.typeEmpty;
			}
			else
			{
				XSComplexTypeDefinition ctd = (XSComplexTypeDefinition) td;
				
				SchemaInformedRule ruleContent = translateComplexTypeDefinitionToFSA ( ctd );

				// resolve lambdas
				if ( RESOLVE_LAMBDAS )
				{
					ruleContent.resolveLambdaTransitions ( new ArrayList<EventRule> ( ), new ArrayList<Rule> ( ) );
				}

				// create copy of Element_i_content --> Element_i_content_2
				// (used for content schema-deviations in start-tags, direct jumps)
				SchemaInformedRule ruleContent2 = ruleContent.clone ( );

				// attributes
				XSObjectList attributes = ctd.getAttributeUses ( );

				// TODO attribute wildcard AT(*) plus AT(uri, *)
				// XSWildcard attributeWC = ctd.getAttributeWildcard ( );

				// type_i (start tag)
				type_i = handleAttributes ( ruleContent, ruleContent2, attributes );
				type_i.setHasNamedSubtypes ( hasNamedSubTypes ( ctd ) );

				// typeEmpty_i
				SchemaInformedRule ruleEnd = new SchemaInformedRuleElement ( );
				ruleEnd.addTerminalRule ( END_ELEMENT );
				typeEmpty_i = handleAttributes ( ruleEnd, ruleEnd, attributes );				
			}
		}
		else if ( td.getTypeCategory ( ) == XSTypeDefinition.SIMPLE_TYPE )
		{
			// Type i
			XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) td;
			SchemaInformedRuleElement simpleContent = translateSimpleTypeDefinitionToFSA ( std );
			type_i = handleAttributes ( simpleContent, simpleContent, null );
			type_i.setHasNamedSubtypes ( hasNamedSubTypes ( std ) );
			// TypeEmpty i
			SchemaInformedRule ruleEnd = new SchemaInformedRuleElement ( );
			ruleEnd.addTerminalRule ( END_ELEMENT );
			typeEmpty_i = handleAttributes ( ruleEnd, ruleEnd, null );
		}

		if ( !td.getAnonymous ( ) )
		{
			// add to localName table for string table pre-population
			ExpandedName typeName = new ExpandedName ( td.getNamespace ( ), td.getName ( ) );
			addLocalNameStringEntry ( typeName );

			// add to type-list (e.g. xsi:type)
			ruleForType.put ( typeName, type_i );
		}

		return new TypeGrammar ( type_i, typeEmpty_i );
	}

	private boolean hasNamedSubTypes ( XSTypeDefinition td )
	{
		XSNamedMap types = this.xsModel.getComponents ( XSConstants.TYPE_DEFINITION );

		for ( int i = 0; i < types.getLength ( ); i++ )
		{
			XSTypeDefinition td2 = (XSTypeDefinition) types.item ( i );

			if ( td.equals ( td2.getBaseType ( ) ) )
			{
				return true;
			}
		}

		return false;
	}

	protected SchemaInformedRule translateComplexTypeDefinitionToFSA ( XSComplexTypeDefinition ctd )
	{
		SchemaInformedRule ruleContent = null;

		switch ( ctd.getContentType ( ) )
		{
			case XSComplexTypeDefinition.CONTENTTYPE_EMPTY:
				// Represents an empty content type.
				// A content type with the distinguished value empty validates
				// elements
				// with no character or element information item children.
				// (attributes only, no content allowed)
				ruleContent = new SchemaInformedRuleElement ( );
				ruleContent.addTerminalRule ( END_ELEMENT );
				break;
			case XSComplexTypeDefinition.CONTENTTYPE_SIMPLE:
				// Represents a simple content type.
				// A content type which is simple validates elements with
				// character-only children.
				XSSimpleTypeDefinition std = ctd.getSimpleType ( );
				ruleContent = translateSimpleTypeDefinitionToFSA ( std );
				break;
			case XSComplexTypeDefinition.CONTENTTYPE_ELEMENT:
				// Represents an element-only content type.
				// An element-only content type validates elements with children
				// that
				// conform to the supplied content model.

				// The {content model} of a complex type definition is a single
				// particle
				XSParticle xsParticleElement = ctd.getParticle ( );

				// additional content, sub elements etc, final EE
				SchemaInformedRule ruleEE = new SchemaInformedRuleElement ( );
				ruleEE.addTerminalRule ( END_ELEMENT );
				ruleContent = translateParticleToFSA ( xsParticleElement, ruleEE );

				break;
			case XSComplexTypeDefinition.CONTENTTYPE_MIXED:
				// Represents a mixed content type

				// The {content model} of a complex type definition is a single
				// particle
				XSParticle xsParticleMixed = ctd.getParticle ( );

				// content, final EE
				SchemaInformedRule ruleEE3 = new SchemaInformedRuleElement ( );
				ruleEE3.addTerminalRule ( END_ELEMENT );
				ruleContent = translateParticleToFSA ( xsParticleMixed, ruleEE3 );

				// mixed transition
				addMixedTransitions ( ruleContent );
				ruleContent.setLabel ( "MixedContent" );

				break;
			default:
				throw new RuntimeException ( );
		}

		return ruleContent;

	}

	private void addMixedTransitions ( Rule ruleMixedContent )
	{
		addMixedTransitions ( ruleMixedContent, new ArrayList<Rule> ( ) );
	}

	private void addMixedTransitions ( Rule ruleMixedContent, List<Rule> handled )
	{
		if ( handled.contains ( ruleMixedContent ) )
		{
			// abort
			return;
		}
		handled.add ( ruleMixedContent );

		// mixed --> generic characters events
		ruleMixedContent.addRule ( new CharactersGeneric ( ), ruleMixedContent );

		for ( int i = 0; i < ruleMixedContent.getNumberOfEvents ( ); i++ )
		{
			Rule r = ruleMixedContent.get1stLevelRule ( i );
			if ( !r.isTerminalRule ( ) )
			{
				addMixedTransitions ( r, handled );
			}
		}
	}

	protected SchemaInformedRuleElement translateSimpleTypeDefinitionToFSA ( XSSimpleTypeDefinition std )
	{

		ExpandedName nameValueType;
		if ( std.getAnonymous ( ) )
		{
			nameValueType = new ExpandedName ( null, "Anonymous" );
		}
		else
		{
			nameValueType = new ExpandedName ( std.getNamespace ( ), std.getName ( ) );
		}

		Characters chSchemaValid = new Characters ( nameValueType, BuiltIn.getDatatype ( std ) );

		SchemaInformedRuleElement type_i_1 = new SchemaInformedRuleElement ( );

		SchemaInformedRuleElement type_i_0 = new SchemaInformedRuleElement ( );
		type_i_0.addRule ( chSchemaValid, type_i_1 );

		type_i_1.addTerminalRule ( END_ELEMENT );

		// TODO TypeEmpty

		return type_i_0;
	}

	/*
	 * Algorithm Tp(S) To translate a particle to an FSA ending at a state S
	 */
	protected SchemaInformedRule translateParticleToFSA ( XSParticle particle, SchemaInformedRule s )
	{
		// 1. Set n to S
		SchemaInformedRule n = s;

		assert ( particle != null );

		// 2. # If the particle's {max occurs} is unbounded
		//
		// 2.1. Set t to a new state; Set b to the result of translating {term}
		// to an FSM ending at t using Tt(t); Add lambda (also known as epsilon,
		// or empty) edges from t to b and from b to n; Set n to b.
		// 2.2. This builds a fragment as follows:
		//
		// /<----- lambda -------<\
		// 
		// n >-- [term machine] --> t S
		// 
		// \>--------- lambda -------->/
		if ( particle.getMaxOccursUnbounded ( ) )
		{
			// Set t to a new state;
			SchemaInformedRule t = new SchemaInformedRuleElement ( );

			// Set b to the result of translating {term}
			// to an FSM ending at t using Tt(t);
			XSTerm xsTerm = particle.getTerm ( );
			// SchemaInformedRule b = this.translateTermToFSA ( xsTerm, scope, t
			// );
			SchemaInformedRule b = this.translateTermToFSA ( xsTerm, t );

			// Add lambda (also known as epsilon,
			// or empty) edges from t to b and from b to n
			t.addRule ( LAMBDA, b );
			b.addRule ( LAMBDA, n );

			// Set n to b
			n = b;
		}

		// 3. # otherwise ({max occurs} is numeric)
		//
		// 3.1. Build a chain of {max occurs}-{min occurs} copies of the
		// translation
		// of {term} backwards from S, with lambda transitions from the
		// beginning
		// of each step to S, and set n to the beginning of the chain.
		// 3.2. This builds e.g. a fragment as follows, for min=2 max=4:
		//
		// n --> [term machine] --> x >-- [term machine] --> S
		// \
		// \ >----- lambda -----> /
		// \
		// \ >-------------- lambda --------------> /
		if ( !particle.getMaxOccursUnbounded ( ) )
		{
			int numberOfCopies = particle.getMaxOccurs ( ) - particle.getMinOccurs ( );

			for ( int i = 0; i < numberOfCopies; i++ )
			{
				XSTerm xsTerm = particle.getTerm ( );
				// SchemaInformedRule ruleTerm = this.translateTermToFSA (
				// xsTerm, scope, n );
				SchemaInformedRule ruleTerm = this.translateTermToFSA ( xsTerm, n );
				n = ruleTerm;
				n.addRule ( LAMBDA, s );
			}
		}

		// 4. Now build a chain of {min occurs} copies of the translation of
		// {term} back from n, and return (the start state of) the resulting
		// machine.
		for ( int i = 0; i < particle.getMinOccurs ( ); i++ )
		{
			XSTerm xsTerm = particle.getTerm ( );
			// SchemaInformedRule ruleTerm = this.translateTermToFSA ( xsTerm,
			// scope, n );
			SchemaInformedRule ruleTerm = this.translateTermToFSA ( xsTerm, n );

			n = ruleTerm;
		}

		return n;

	}

	/*
	 * Algorithm Tt(S) To translate a term to an FSA ending at a state S
	 */
	// private SchemaInformedRule translateTermToFSA ( XSTerm xsTerm,
	// ExpandedName scope, SchemaInformedRule s )
	protected SchemaInformedRule translateTermToFSA ( XSTerm xsTerm, SchemaInformedRule s )
	{
		if ( xsTerm instanceof XSElementDeclaration )
		{
			// Element declaration
			// If the term is an element declaration, create a new state b,
			// then for each element declaration in its substitution group
			// create an edge
			// from b to S labelled with that element declaration, and return b
			//
			// An element declaration has three properties of relevance:
			// {local name}, {namespace name} and substitution group
			XSElementDeclaration xsElementDeclaration = (XSElementDeclaration) xsTerm;

			// start new element, not of interest for our grammar (for content
			// dispatcher only)
			// translateXSElementDeclarationToFSA ( xsElementDeclaration, scope
			// );
			// translatElementDeclarationToFSA ( xsElementDeclaration,
			// enclosingElements.peek ( ) );

			if ( enclosingElements.peek ( ) != null )
			{
				if ( outstandingElements.contains ( xsElementDeclaration ) )
				{
					outstandingElements.remove ( xsElementDeclaration );
					// System.out.println( "<< outstanding element removed: " +
					// xsElementDeclaration );

				}
				translatElementDeclarationToFSA ( xsElementDeclaration, enclosingElements.peek ( ) );
				// enclosingElement4Element.put ( xsElementDeclaration,
				// enclosingElements.peek ( ) );
			}
			else
			{
				if ( !outstandingElements.contains ( xsElementDeclaration ) )
				{
					outstandingElements.add ( xsElementDeclaration );
					// System.out.println( ">> outstanding element: " +
					// xsElementDeclaration + " (enclosingType= " +
					// xsElementDeclaration.getEnclosingCTDefinition ( ) );
				}
			}

			// NOTE: element has ONE type, schema type can be used for several
			// elements
			if ( !type4Element.containsKey ( xsElementDeclaration ) )
			{
				type4Element.put ( xsElementDeclaration, xsElementDeclaration.getTypeDefinition ( ) );
			}

			// AbstractRule ruleSubElement = new RuleElementContent();
			SchemaInformedRule ruleSubElement = new SchemaInformedRuleElement ( );
			StartElement se = new StartElement ( xsElementDeclaration.getNamespace ( ), xsElementDeclaration.getName ( ) );

			ruleSubElement.addRule ( se, s );

			// SubstitutionGroups
			this.handleSubstitutionGroups ( xsElementDeclaration, ruleSubElement, s );

			return ruleSubElement;

		}
		else if ( xsTerm instanceof XSModelGroup )
		{
			// XSModelGroup
			// sequence, choice or all
			XSModelGroup xsModelGroup = (XSModelGroup) xsTerm;

			if ( xsModelGroup.getCompositor ( ) == XSModelGroup.COMPOSITOR_SEQUENCE )
			{
				// A sequence has one property: particles
				//
				// If the term is a sequence, create chain translations of each
				// particle in its particles, in reverse order, back from S and
				// return the first state in the chain.

				XSObjectList particles = xsModelGroup.getParticles ( );

				SchemaInformedRule b = s;

				for ( int k = particles.getLength ( ) - 1; k >= 0; k-- )
				{
					XSParticle xsParticle = (XSParticle) particles.item ( k );
					// SchemaInformedRule ruleParticle =
					// this.translateParticleToFSA ( xsParticle, scope, b );
					SchemaInformedRule ruleParticle = translateParticleToFSA ( xsParticle, b );

					b = ruleParticle;
				}

				// b contains start rule
				return b;

			}
			else if ( xsModelGroup.getCompositor ( ) == XSModelGroup.COMPOSITOR_CHOICE )
			{
				// A choice has one property: particles
				//
				// If the term is a choice, create a new state b, translate each
				// particle in its particles into an FSA ending at S using
				// Tp(S),
				// connect b to the start state of all the results with a lambda
				// edge and return b;
				XSObjectList particles = xsModelGroup.getParticles ( );

				// AbstractRule b = new RuleElementContent();
				SchemaInformedRule b = new SchemaInformedRuleElement ( );

				for ( int i = 0; i < particles.getLength ( ); i++ )
				{
					XSParticle xsParticle = (XSParticle) particles.item ( i );
					// SchemaInformedRule ruleParticle =
					// this.translateParticleToFSA ( xsParticle, scope, s );
					SchemaInformedRule ruleParticle = this.translateParticleToFSA ( xsParticle, s );

					b.joinRules ( ruleParticle );
				}

				return b;
			}
			else if ( xsModelGroup.getCompositor ( ) == XSModelGroup.COMPOSITOR_ALL )
			{
				// The all element specifies that the child elements can appear
				// in any order and that each child element can occur zero or
				// one time.

				XSObjectList particles = xsModelGroup.getParticles ( );

				SchemaInformedRule b = new SchemaInformedRuleContentAll ( );

				b.joinRules ( s );

				for ( int k = 0; k < particles.getLength ( ); k++ )
				{
					XSParticle xsParticle = (XSParticle) particles.item ( k );
					// SchemaInformedRule ruleParticle =
					// this.translateParticleToFSA ( xsParticle, scope, b );
					SchemaInformedRule ruleParticle = this.translateParticleToFSA ( xsParticle, b );

					b.joinRules ( ruleParticle );
				}

				// Note:
				// "All" is treated as a choice with the difference that at
				// runtime choices are removed step by step.
				// Beyond that, the EndElement event is added at runtime if no
				// choice remains.

				// b contains start rule
				return b;
			}
			else
			{
				throw new RuntimeException ( "Unknown ModelGroup Compositor" );
			}

		}
		else if ( xsTerm instanceof XSWildcard )
		{
			// Wildcard
			// If the term is a wildcard, connect a new state b to S with an
			// edge labelled with the term itself and return b;
			//
			// A wildcard has two properties: {namespace constraint} and
			// {process contents}
			XSWildcard xsWildcard = (XSWildcard) xsTerm;

			SchemaInformedRule b = s;

			// SchemaInformedRule particleTerm_i_1 = s;
			// particleTerm_i_1.addTerminalRule ( END_ELEMENT );
			// SchemaInformedRule particleTerm_i_0 = new
			// SchemaInformedRuleElement ( );

			SchemaInformedRule urType = SchemaInformedGrammar.getUrTypeRule ( ).type;
			b.joinRules ( urType );

			short constraintType = xsWildcard.getConstraintType ( );
			if ( constraintType == XSWildcard.NSCONSTRAINT_ANY || constraintType == XSWildcard.NSCONSTRAINT_NOT )
			{
				// SE (*)
				// b.addRule ( new StartElementGeneric ( ), urType );
				// particleTerm_i_0.addRule ( new StartElementGeneric ( ),
				// particleTerm_i_1 );
			}
			else
			{
				// ns list ?
				// TODO SE(uri, *) --> StartElementNS( * )
				// b.addRule ( new StartElementGeneric ( ), urType );
				// particleTerm_i_0.addRule ( new StartElementGeneric ( ),
				// urType );
			}

			// return particleTerm_i_0;
			return b;
		}
		else
		{
			throw new IllegalArgumentException (
					"Unexpected XSTerm, neither ElementDeclaration, ModelGroup nor Wildcard" );
		}
	}

}
