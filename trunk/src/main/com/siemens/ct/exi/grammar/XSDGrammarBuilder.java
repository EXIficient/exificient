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

package com.siemens.ct.exi.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.xerces.impl.xs.models.EXIContentModelBuilder;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.AttributeNS;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedElement;
import com.siemens.ct.exi.grammar.rule.SchemaInformedFirstStartTag;
import com.siemens.ct.exi.grammar.rule.SchemaInformedFirstStartTagRule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedStartTag;
import com.siemens.ct.exi.grammar.rule.SchemaInformedStartTagRule;
import com.siemens.ct.exi.types.BuiltIn;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class XSDGrammarBuilder extends EXIContentModelBuilder {

	protected Map<QName, SchemaInformedFirstStartTagRule> grammarTypes;

	// local-names (pre-initializing LocalName Partition)
	// uri -> localNames
	protected Map<String, List<String>> schemaLocalNames;

//	// attribute wildcard namespaces
//	protected List<String> atWildcardNamespaces;

	// avoids recursive element handling
	protected Set<XSElementDeclaration> handledElements;

	// pool for attribute-declaration of Attribute events
	protected Map<XSAttributeDeclaration, Attribute> attributePool;

	// schema information is available to describe the contents of an EXI stream
	// and more than one element is declared with the same qname
	protected SchemaInformedFirstStartTagRule elementFragment0;

	protected XSDGrammarBuilder() {
		super();

		initOnce();
	}

	public static XSDGrammarBuilder newInstance() {
		return new XSDGrammarBuilder();
	}

	@Override
	protected void initOnce() {
		super.initOnce();

		handledElements = new HashSet<XSElementDeclaration>();
		grammarTypes = new HashMap<QName, SchemaInformedFirstStartTagRule>();
		schemaLocalNames = new HashMap<String, List<String>>();
		// atWildcardNamespaces = new ArrayList<String>();
		attributePool = new HashMap<XSAttributeDeclaration, Attribute>();
	}

	@Override
	protected void initEachRun() {
		super.initEachRun();

		handledElements.clear();
		grammarTypes.clear();
		// atWildcardNamespaces.clear();
		attributePool.clear();

		elementFragment0 = null;

		schemaLocalNames.clear();
		// "", empty string
		for (String localName : Constants.LOCAL_NAMES_EMPTY) {
			addLocalNameStringEntry(XMLConstants.NULL_NS_URI, localName);
		}
		// "http://www.w3.org/XML/1998/namespace"
		for (String localName : Constants.LOCAL_NAMES_XML) {
			addLocalNameStringEntry(XMLConstants.XML_NS_URI, localName);
		}
		// "http://www.w3.org/2001/XMLSchema-instance", xsi
		for (String localName : Constants.LOCAL_NAMES_XSI) {
			addLocalNameStringEntry(
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, localName);
		}
		// "http://www.w3.org/2001/XMLSchema", xsd
		for (String localName : Constants.LOCAL_NAMES_XSD) {
			addLocalNameStringEntry(XMLConstants.W3C_XML_SCHEMA_NS_URI,
					localName);
		}
	}

	protected boolean isSameElementGrammar(List<XSElementDeclaration> elements) {
		assert (elements.size() > 1);
		/*
		 * If all such elements have the same type name and {nillable} property
		 * value, their content is evaluated according to specific grammar for
		 * that element declaration
		 */
		for (int i = 1; i < elements.size(); i++) {
			XSElementDeclaration e0 = elements.get(0);
			XSTypeDefinition t0 = e0.getTypeDefinition();
			XSElementDeclaration e1 = elements.get(i);
			XSTypeDefinition t1 = e1.getTypeDefinition();
			if (t0.getAnonymous() || t1.getAnonymous()) {
				// cannot have same type name
				return false;
			}
			if (t0.getName() != t1.getName()
					|| t0.getNamespace() != t1.getNamespace()
					|| e0.getNillable() != e1.getNillable()) {
				return false;
			}
		}

		return true;
	}

	protected boolean isSameAttributeGrammar(
			List<XSAttributeDeclaration> attributes) {
		assert (attributes.size() > 1);
		/*
		 * If all such elements have the same type name and {nillable} property
		 * value, their content is evaluated according to specific grammar for
		 * that element declaration.
		 */
		for (int i = 1; i < attributes.size(); i++) {
			XSAttributeDeclaration e0 = attributes.get(0);
			XSAttributeDeclaration ei = attributes.get(i);
			if (e0.getTypeDefinition() != ei.getTypeDefinition()) {
				return false;
			}
		}

		return true;
	}

	protected List<StartElement> getFragmentGrammars() {
		List<StartElement> fragmentElements = new ArrayList<StartElement>();

		// create unique qname map
		Map<QName, List<XSElementDeclaration>> uniqueNamedElements = new HashMap<QName, List<XSElementDeclaration>>();
		for (XSElementDeclaration elDecl : handledElements) {
			QName en = new QName(elDecl.getNamespace(), elDecl.getName());
			if (uniqueNamedElements.containsKey(en)) {
				uniqueNamedElements.get(en).add(elDecl);
			} else {
				List<XSElementDeclaration> list = new ArrayList<XSElementDeclaration>();
				list.add(elDecl);
				uniqueNamedElements.put(en, list);
			}
		}

		Iterator<Entry<QName, List<XSElementDeclaration>>> iter = uniqueNamedElements
				.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<QName, List<XSElementDeclaration>> e = iter.next();
			QName qname = e.getKey();
			List<XSElementDeclaration> elements = e.getValue();
			// If there is more than one element declared with the same qname,
			// the qname is included only once.
			assert (elements.size() >= 1);
			if (elements.size() == 1) {
				// just one element for this qualified name --> simple task
				fragmentElements.add(getStartElement(elements.get(0)));
			} else {
				// multiple elements
				if (isSameElementGrammar(elements)) {
					fragmentElements.add(getStartElement(elements.get(0)));
				} else {
					StartElement se = new StartElement(qname);
					Rule elementFragmentGrammar = getSchemaInformedElementFragmentGrammar(uniqueNamedElements);
					se.setRule(elementFragmentGrammar);
					fragmentElements.add(se);
					// System.out.println("ambiguous elements " + elements +
					// ", " + qname);
				}
			}
		}

		return fragmentElements;
	}

	// http://www.w3.org/TR/exi/#informedElementFragGrammar
	protected Rule getSchemaInformedElementFragmentGrammar(
			Map<QName, List<XSElementDeclaration>> uniqueNamedElements) {

		if (elementFragment0 != null) {
			return elementFragment0;
		}

		// 8.5.3 Schema-informed Element Fragment Grammar
		SchemaInformedRule elementFragment1 = new SchemaInformedElement();
		elementFragment0 = new SchemaInformedFirstStartTag(elementFragment1);

		// 
		// ElementFragment 1 :
		// SE ( F0 ) ElementFragment 1 0
		// SE ( F1 ) ElementFragment 1 1
		// ...
		// SE ( Fm-1 ) ElementFragment 1 m-1
		// SE ( * ) ElementFragment 1 m
		// EE m+1
		// CH [untyped value] ElementFragment 1 m+2

		/*
		 * The variable m in the grammar above represents the number of unique
		 * element qnames declared in the schema. The variables F0 , F1 , ...
		 * Fm-1 represent these qnames sorted lexicographically, first by
		 * local-name, then by uri. If there is more than one element declared
		 * with the same qname, the qname is included only once. If all such
		 * elements have the same type name and {nillable} property value, their
		 * content is evaluated according to specific grammar for that element
		 * declaration. Otherwise, their content is evaluated according to the
		 * relaxed Element Fragment grammar described above.
		 */
		List<QName> uniqueNamedElementsList = new ArrayList<QName>();
		Iterator<QName> iter = uniqueNamedElements.keySet().iterator();
		while (iter.hasNext()) {
			uniqueNamedElementsList.add(iter.next());
		}
		Collections.sort(uniqueNamedElementsList, lexSort);

		for (QName fm : uniqueNamedElementsList) {
			StartElement se;
			List<XSElementDeclaration> elements = uniqueNamedElements.get(fm);
			if (elements.size() == 1 || isSameElementGrammar(elements)) {
				se = getStartElement(elements.get(0));
			} else {
				// content is evaluated according to the relaxed Element
				// Fragment grammar
				se = new StartElement(fm);
				se.setRule(elementFragment0);
			}
			elementFragment1.addRule(se, elementFragment1);
		}

		// SE ( * ) ElementFragment 1 m
		elementFragment1.addRule(START_ELEMENT_GENERIC, elementFragment1);
		// EE m+1
		elementFragment1.addTerminalRule(END_ELEMENT);
		// CH [untyped value] ElementFragment 1 m+2
		elementFragment1.addRule(CHARACTERS_GENERIC, elementFragment1);

		// ElementFragment 0 :
		// AT ( A 0 ) [schema-typed value] ElementFragment 0 0
		// AT ( A 1 ) [schema-typed value] ElementFragment 0 1
		// ...
		// AT (A n-1) [schema-typed value] ElementFragment 0 n-1
		// AT ( * ) ElementFragment 0 n
		// SE ( F0 ) ElementFragment 1 n+1
		// SE ( F1 ) ElementFragment 1 n+2
		// ...
		// SE ( Fm-1 ) ElementFragment 1 n+m
		// SE ( * ) ElementFragment 1 n+m+1
		// EE n+m+2
		// CH [untyped value] ElementFragment 1 n+m+3

		/*
		 * The variable n in the grammar above represents the number of unique
		 * qnames given to explicitly declared attributes in the schema. The
		 * variables A 0 , A 1 , ... A n-1 represent these qnames sorted
		 * lexicographically, first by local-name, then by uri. If there is more
		 * than one attribute declared with the same qname, the qname is
		 * included only once. If all such attributes have the same schema type
		 * name, their value is represented using that type. Otherwise, their
		 * value is represented as a String.
		 */

		List<QName> uniqueNamedAttributeList = new ArrayList<QName>();

		// create unique qname map
		Map<QName, List<XSAttributeDeclaration>> uniqueNamedAttributes = new HashMap<QName, List<XSAttributeDeclaration>>();
		Iterator<XSAttributeDeclaration> atts = attributePool.keySet()
				.iterator();
		while (atts.hasNext()) {
			XSAttributeDeclaration atDecl = atts.next();
			QName atQname = new QName(atDecl.getNamespace(), atDecl.getName());
			if (uniqueNamedAttributes.containsKey(atQname)) {
				uniqueNamedAttributes.get(atQname).add(atDecl);
			} else {
				List<XSAttributeDeclaration> list = new ArrayList<XSAttributeDeclaration>();
				list.add(atDecl);
				uniqueNamedAttributes.put(atQname, list);
				uniqueNamedAttributeList.add(atQname);
			}
		}
		Collections.sort(uniqueNamedAttributeList, lexSort);

		for (QName an : uniqueNamedAttributeList) {
			Attribute at;
			List<XSAttributeDeclaration> attributes = uniqueNamedAttributes
					.get(an);
			if (attributes.size() == 1 || isSameAttributeGrammar(attributes)) {
				at = getAttribute(attributes.get(0));
			} else {
				// represented as a String
				at = new Attribute(an);
			}
			elementFragment0.addRule(at, elementFragment0);
		}
		// AT ( * ) ElementFragment 0 n
		elementFragment0.addRule(ATTRIBUTE_GENERIC, elementFragment0);

		// SE ( F0 ) ElementFragment 1 n+1
		// ..
		for (QName fm : uniqueNamedElementsList) {
			StartElement se;
			List<XSElementDeclaration> elements = uniqueNamedElements.get(fm);
			if (elements.size() == 1 || isSameElementGrammar(elements)) {
				se = getStartElement(elements.get(0));
			} else {
				// content is evaluated according to the relaxed Element
				// Fragment grammar
				se = new StartElement(fm);
				se.setRule(elementFragment0);
			}
			elementFragment0.addRule(se, elementFragment1);
		}

		// SE ( * ) ElementFragment 1 n+m+1
		elementFragment0.addRule(START_ELEMENT_GENERIC, elementFragment1);
		// EE n+m+2
		elementFragment0.addTerminalRule(END_ELEMENT);
		// CH [untyped value] ElementFragment 1 n+m+3
		elementFragment0.addRule(CHARACTERS_GENERIC, elementFragment1);

		SchemaInformedRule elementFragmentEmpty1 = new SchemaInformedElement();
		SchemaInformedFirstStartTagRule elementFragmentEmpty0 = new SchemaInformedFirstStartTag(
				elementFragmentEmpty1);

		// ElementFragmentTypeEmpty 0 :
		// AT ( A 0 ) [schema-typed value] ElementFragmentTypeEmpty 0 0
		// AT ( A 1 ) [schema-typed value] ElementFragmentTypeEmpty 0 1
		// ...
		// AT ( A n-1 ) [schema-typed value] ElementFragmentTypeEmpty 0 n-1
		// AT ( * ) ElementFragmentTypeEmpty 0 n
		// EE n+1
		for (QName an : uniqueNamedAttributeList) {
			Attribute at;
			List<XSAttributeDeclaration> attributes = uniqueNamedAttributes
					.get(an);
			if (attributes.size() == 1 || isSameAttributeGrammar(attributes)) {
				at = getAttribute(attributes.get(0));
			} else {
				// represented as a String
				at = new Attribute(an);
			}
			elementFragmentEmpty0.addRule(at, elementFragmentEmpty0);
		}
		elementFragmentEmpty0.addRule(ATTRIBUTE_GENERIC, elementFragmentEmpty0);
		elementFragmentEmpty0.addTerminalRule(END_ELEMENT);

		// ElementFragmentTypeEmpty 1 :
		// EE 0
		elementFragmentEmpty1.addTerminalRule(END_ELEMENT);

		/*
		 * As with all schema informed element grammars, the schema-informed
		 * element fragment grammar is augmented with additional productions
		 * that describe events that may occur in an EXI stream, but are not
		 * explicity declared in the schema. The process for augmenting the
		 * grammar is described in 8.5.4.4 Undeclared Productions. For the
		 * purposes of this process, the schema-informed element fragment
		 * grammar is treated as though it is created from an element
		 * declaration with a {nillable} property value of true and a type
		 * declaration that has named sub-types, and ElementFragmentTypeEmpty is
		 * used to serve as the TypeEmpty of the type in the process.
		 */
		elementFragment0.setNillable(true);
		elementFragment0.setTypeEmpty(elementFragmentEmpty0);
		elementFragment0.setTypeCastable(true);

		return elementFragment0;
	}

	public SchemaInformedGrammar toGrammar() throws EXIException {
		if (xsModel == null || schemaParsingErrors.size() > 0) {
			StringBuffer sb = new StringBuffer(
					"Problem occured while building XML Schema Model (XSModel)!");

			for (int i = 0; i < schemaParsingErrors.size(); i++) {
				sb.append("\n. " + schemaParsingErrors.get(i));
			}

			throw new EXIException(sb.toString());
		}
		

		// initialize grammars --> global element)
		List<StartElement> globalElements = initGrammars();
		
		// schema declared elements --> fragment grammars
		List<StartElement> fragmentElements = getFragmentGrammars();

		// sort both lists (declared & global elements)
		Collections.sort(globalElements, lexSort);
		Collections.sort(fragmentElements, lexSort);

		/*
		 * Simple sub-type hierarchy
		 */
		Map<QName, List<QName>> subtypes = new HashMap<QName, List<QName>>();
		Iterator<QName> iterTypes = grammarTypes.keySet().iterator();
		while (iterTypes.hasNext()) {
			QName typeQName = iterTypes.next();
			XSTypeDefinition td = xsModel.getTypeDefinition(typeQName
					.getLocalPart(), typeQName.getNamespaceURI());
			if (td.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE
					&& !td.getAnonymous()) {
				// XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) td;
				XSTypeDefinition baseType = td.getBaseType();
				if (baseType == null) {
					// http://www.w3.org/2001/XMLSchema,anySimpleType
				} else {
					QName baseTypeQName = getQNameForType(baseType);
					List<QName> sub = subtypes.get(baseTypeQName);
					if (sub == null) {
						sub = new ArrayList<QName>();
						subtypes.put(baseTypeQName, sub);
					}
					sub.add(getQNameForType(td));
				}
			}
		}

		/*
		 * global attributes
		 */
		XSNamedMap nm = xsModel
				.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
		Map<QName, Attribute> globalAttributes = new HashMap<QName, Attribute>();
		for (int i = 0; i < nm.getLength(); i++) {
			XSAttributeDeclaration atDecl = (XSAttributeDeclaration) nm.item(i);
			Attribute at = getAttribute(atDecl);
			globalAttributes.put(at.getQName(), at);
		}
		
		// schema URIs and (sorted) localNames
		String[] uris = getURITableEntries();
		GrammarURIEntry[] grammarEntries = new GrammarURIEntry[uris.length];
		for (int i = 0; i < uris.length; i++) {
			String uri = uris[i];

			// local-names
			String[] localNamesArray;
			if (schemaLocalNames.containsKey(uri)) {
				List<String> localNames = schemaLocalNames.get(uri);
				// sort local-name list
				Collections.sort(localNames);
				// create sorted array out of it
				localNamesArray = new String[localNames.size()];
				localNames.toArray(localNamesArray);
			} else {
				// no entries, may happen for XMLConstants.NULL_NS_URI
				localNamesArray = new String[0];
			}

			// prefixes
			String[] prefixes;
			if (uri.equals(XMLConstants.NULL_NS_URI)) {
				prefixes = Constants.PREFIXES_EMPTY;
			} else if (uri.equals(XMLConstants.XML_NS_URI)) {
				prefixes = Constants.PREFIXES_XML;
			} else if (uri.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
				prefixes = Constants.PREFIXES_XSI;
			} else if (uri.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
				prefixes = Constants.PREFIXES_XSD;
			} else {
				prefixes = new String[0];
			}

			// add schema entry
			grammarEntries[i] = new GrammarURIEntry(uri, localNamesArray,
					prefixes);
		}
		
		/*
		 * create schema informed grammar
		 * (+set grammarTypes, simpleSubTypes and global attributes)
		 */
		SchemaInformedGrammar sig = new SchemaInformedGrammar(grammarEntries,
				fragmentElements, globalElements);
		
		sig.setTypeGrammars(grammarTypes);
		sig.setSimpleTypeSubtypes(subtypes);
		sig.setGlobalAttributes(globalAttributes);
		
		return sig;
	}

	// NOT EQUAL
	// "" [empty string],
	// "http://www.w3.org/XML/1998/namespace",
	// "http://www.w3.org/2001/XMLSchema-instance",
	// "http://www.w3.org/2001/XMLSchema"
	protected static boolean isAdditionalNamespace(String namespaceURI) {
		assert (namespaceURI != null);
		if (namespaceURI.equals(XMLConstants.NULL_NS_URI)
				|| namespaceURI.equals(XMLConstants.XML_NS_URI)
				|| namespaceURI
						.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)
				|| namespaceURI.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
			return false;
		} else {
			return true;
		}
	}

	protected String[] getURITableEntries() {
		StringList namespaces = xsModel.getNamespaces();
		TreeSet<String> sortedURIs = new TreeSet<String>();

		for (int i = 0; i < namespaces.getLength(); i++) {
			String uri = namespaces.item(i) == null ? XMLConstants.NULL_NS_URI
					: namespaces.item(i);
			if (isAdditionalNamespace(uri)) {
				sortedURIs.add(uri);
			}
		}
		
		// is this necessary? (but doesn't hurt either)
		Iterator<String> iterUris = schemaLocalNames.keySet().iterator();
		while (iterUris.hasNext()) {
			String uri = iterUris.next();
			if (isAdditionalNamespace(uri)) {
				sortedURIs.add(uri);
			}
		}
		

//		// any attribute namespaces
//		for (String atWildcardURI : this.atWildcardNamespaces) {
//			atWildcardURI = atWildcardURI == null ? XMLConstants.NULL_NS_URI
//					: atWildcardURI;
//			if (isAdditionalNamespace(atWildcardURI)) {
//				sortedURIs.add(atWildcardURI);
//			}
//		}

		// copy to array (in right order)
		String[] uris = new String[4 + sortedURIs.size()];
		uris[0] = XMLConstants.NULL_NS_URI;
		uris[1] = XMLConstants.XML_NS_URI;
		uris[2] = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;
		uris[3] = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		int compactID = 4;
		for (String addUri : sortedURIs) {
			uris[compactID] = addUri;
			compactID++;
		}

		return uris;
	}

	/*
	 * When a schema is provided, the string table (Local-name) is also
	 * pre-populated with the local name of each attribute, element and type
	 * declared in the schema, partitioned by namespace URI and sorted
	 * lexicographically.
	 */
	protected void addLocalNameStringEntry(String namespaceURI, String localName) {
		// fetch localName list
		List<String> localNameList = addNamespaceStringEntry(namespaceURI);
		
		// check localName value presence
		if (!localNameList.contains(localName)) {
			localNameList.add(localName);
			// System.out.println("LocalName=" + localName + " \t " + namespaceURI);
		}
	}
	
	protected List<String> addNamespaceStringEntry(String namespaceURI) {
		if (namespaceURI == null) {
			namespaceURI = XMLConstants.NULL_NS_URI;
		}
		// fetch localName list
		List<String> localNameList;
		if (schemaLocalNames.containsKey(namespaceURI)) {
			localNameList = schemaLocalNames.get(namespaceURI);
		} else {
			localNameList = new ArrayList<String>();
			schemaLocalNames.put(namespaceURI, localNameList);
		}
		
		return localNameList;
	}
	

	protected List<StartElement> initGrammars() throws EXIException {
		List<StartElement> globalElements = new ArrayList<StartElement>();

		// global type definitions
		XSNamedMap types = xsModel.getComponents(XSConstants.TYPE_DEFINITION);
		for (int i = 0; i < types.getLength(); i++) {
			XSTypeDefinition td = (XSTypeDefinition) types.item(i);

			QName name = new QName(td.getNamespace(), td.getName());
			SchemaInformedFirstStartTagRule sir = translateTypeDefinitionToFSA(td);
			// types cannot be nillable (only elements!)
			assert (!sir.isNillable());

			grammarTypes.put(name, sir);
		}

		// global elements
		XSNamedMap xsGlobalElements = xsModel
				.getComponents(XSConstants.ELEMENT_DECLARATION);
		for (int i = 0; i < xsGlobalElements.getLength(); i++) {
			XSElementDeclaration globalElementDecl = (XSElementDeclaration) xsGlobalElements
					.item(i);

			// collect global elements (for DocContent)
			StartElement seGlobalElement = getStartElement(globalElementDecl);
			globalElements.add(seGlobalElement);
			// globalElements.add(new ExpandedName(globalElement.getNamespace(),
			// globalElement.getName()));

			// create rules for global elements (do not have scope)
			translatElementDeclarationToFSA(globalElementDecl);
		}

		// any remaining elements ? (not global elements)
		for (int i = 0; i < remainingElements.size(); i++) {
			XSElementDeclaration remElement = remainingElements.get(i);
			translatElementDeclarationToFSA(remElement);
		}

		// check entire SE pool
		// Note: copy due to ConcurrentModificationException !?
		// Iterator<XSElementDeclaration> iterSE =
		// elementPool.keySet().iterator();
		Iterator<XSElementDeclaration> iterSE = (new HashMap<XSElementDeclaration, StartElement>(
				elementPool)).keySet().iterator();
		while (iterSE.hasNext()) {
			XSElementDeclaration elementDecl = iterSE.next();
			StartElement se = elementPool.get(elementDecl);

			// element-rule
			SchemaInformedFirstStartTagRule elementRule;

			XSTypeDefinition td = elementDecl.getTypeDefinition();
			if (td.getAnonymous()) {
				// create new type grammar for an anonymous type
				elementRule = translateTypeDefinitionToFSA(td);
				elementRule.setNillable(elementDecl.getNillable());
			} else {
				// fetch existing grammar from pre-processed type
				elementRule = getTypeGrammar(td.getNamespace(), td.getName());

				// *duplicate* first productions to allow different behavior
				// (e.g. property nillable is element dependent)
				if (elementDecl.getNillable()) {
					elementRule = (SchemaInformedFirstStartTagRule) elementRule
							.duplicate();
					elementRule.setNillable(true);
				} else {
					elementRule.setNillable(false);
				}
			}

			se.setRule(elementRule);
		}

		return globalElements;
	}

	protected Attribute getAttribute(XSAttributeDeclaration attrDecl) {
		// local name for string table pre-population
		addLocalNameStringEntry(attrDecl.getNamespace(), attrDecl.getName());

		Attribute at;
		if (attributePool.containsKey(attrDecl)) {
			at = attributePool.get(attrDecl);
		} else {
			// AT datatype
			XSSimpleTypeDefinition td = attrDecl.getTypeDefinition();
			QName qNameType = getQNameForType(td);
			// create new Attribute event
			QName qname = new QName(attrDecl.getNamespace(), attrDecl.getName());
			at = new Attribute(qname, qNameType, BuiltIn.getDatatype(td));
			attributePool.put(attrDecl, at);
		}

		return at;
	}

	protected QName getQNameForType(XSTypeDefinition typeDefinition) {
		QName qNameType;
		if (typeDefinition.getAnonymous()) {
			XSTypeDefinition tdBase = typeDefinition.getBaseType();
			if (tdBase.getName() == null) {
				qNameType = BuiltIn.DEFAULT_VALUE_NAME;
			} else {
				qNameType = new QName(tdBase.getNamespace(), tdBase.getName());
			}
		} else {
			qNameType = new QName(typeDefinition.getNamespace(), typeDefinition
					.getName());
		}
		return qNameType;
	}

	protected SchemaInformedStartTagRule handleAttributes(
			SchemaInformedRule ruleContent, SchemaInformedRule ruleContent2,
			XSObjectList attributes, XSWildcard attributeWC)
			throws EXIException {

		// Attribute Uses
		// http://www.w3.org/TR/exi/#attributeUses

		SchemaInformedStartTagRule ruleStart = new SchemaInformedStartTag(
				ruleContent2);
		// join top level events
		for (int i = 0; i < ruleContent.getNumberOfEvents(); i++) {
			EventInformation ei = ruleContent.lookFor(i);
			ruleStart.addRule(ei.event, ei.next);
		}

		// If an {attribute wildcard} is specified, increment n and generate an
		// additional attribute use grammar G n-1 as follows:
		// G n-1, 0 :
		// EE
		if (attributeWC != null) {
			// ruleStart.addTerminalRule(END_ELEMENT);
			handleAttributeWildCard(attributeWC, ruleStart);
		}

		if (attributes != null && attributes.getLength() > 0) {
			// attributes will occur sorted lexically by qname (in EXI Stream)
			List<XSAttributeUse> vSortedAttributes = new ArrayList<XSAttributeUse>();
			for (int i = 0; i < attributes.getLength(); i++) {
				assert (attributes.item(i).getType() == XSConstants.ATTRIBUTE_USE);
				XSAttributeUse attrUse = (XSAttributeUse) attributes.item(i);
				vSortedAttributes.add(attrUse);
			}
			Collections.sort(vSortedAttributes, lexSort);

			// traverse in reverse order
			for (int i = vSortedAttributes.size() - 1; i >= 0; i--) {
				XSAttributeUse attrUse = vSortedAttributes.get(i);

				Attribute at = getAttribute(attrUse.getAttrDeclaration());

				SchemaInformedStartTagRule newCurrent = new SchemaInformedStartTag(
						ruleContent2);
				newCurrent.addRule(at, ruleStart);

				// Attribute Wildcard
				// http://www.w3.org/TR/exi/#complexTypeGrammars
				if (attributeWC != null) {
					handleAttributeWildCard(attributeWC, newCurrent);
				}

				// required attribute ?
				if (!attrUse.getRequired()) {
					// optional --> join top level events
					for (int k = 0; k < ruleStart.getNumberOfEvents(); k++) {
						EventInformation ei = ruleStart.lookFor(k);
						if (ei.event.isEventType(EventType.ATTRIBUTE_GENERIC)
								|| ei.event.isEventType(EventType.ATTRIBUTE_NS)) {
							// AT(*) & AT(uri:*) wilcards added before
						} else {
							newCurrent.addRule(ei.event, ei.next);
						}
					}
				}
				ruleStart = newCurrent;
			}
		}

		return ruleStart;

	}

	protected void handleAttributeWildCard(XSWildcard attributeWC,
			SchemaInformedRule rule) {

		short constraintType = attributeWC.getConstraintType();
		if (constraintType == XSWildcard.NSCONSTRAINT_ANY
				|| constraintType == XSWildcard.NSCONSTRAINT_NOT) {
			// AT(*)
			// When the {attribute wildcard}'s {namespace
			// constraint} is any, or a pair of not and either a
			// namespace name or the special value absent indicating
			// no namespace, add the following production to each
			// grammar G i generated above:
			// G i, 0 :
			// AT(*) G i, 0
			rule.addRule(ATTRIBUTE_GENERIC, rule);
		} else {
			// AT(urix:*)
			// Otherwise, that is, when {namespace constraint} is a
			// set of values whose members are namespace names or
			// the special value absent indicating no namespace, add
			// the following production to each grammar G i
			// generated above:
			// G i, 0 :
			// AT(urix : *) G i, 0
			StringList sl = attributeWC.getNsConstraintList();
			for (int k = 0; k < sl.getLength(); k++) {
				String namespace = sl.item(k);
				rule.addRule(new AttributeNS(namespace), rule);
				// add attribute wildcard URI
				addNamespaceStringEntry(namespace);
//				if (!atWildcardNamespaces.contains(namespace)) {
//					atWildcardNamespaces.add(namespace);
//				}
			}
		}
	}

	protected SchemaInformedFirstStartTagRule getTypeGrammar(
			String namespaceURI, String name) {
		QName en = new QName(namespaceURI, name);
		return grammarTypes.get(en);
	}

	protected void translatElementDeclarationToFSA(
			XSElementDeclaration xsElementDeclaration) throws EXIException {

		// handle element recursion
		if (this.handledElements.contains(xsElementDeclaration)) {
			// element already handled
			return;
		}
		this.handledElements.add(xsElementDeclaration);
		
		// add local name entry for string table pre-population
		addLocalNameStringEntry(xsElementDeclaration.getNamespace(),
				xsElementDeclaration.getName());
		
		// type definition
		XSTypeDefinition td = xsElementDeclaration.getTypeDefinition();

		// type grammar
		if (td.getAnonymous()) {
			// create type grammar for anonymous type
			translateTypeDefinitionToFSA(td);
		}
	}

	// http://www.w3.org/TR/exi/#anyTypeGrammar
	public static SchemaInformedFirstStartTagRule getUrTypeRule() {

		SchemaInformedRule urType1 = new SchemaInformedElement();
		SchemaInformedFirstStartTag urType0 = new SchemaInformedFirstStartTag(
				urType1);
		urType0.setLabel("ur-type");

		// Type ur-type, 0 :
		// AT (*) Type ur-type, 0
		// SE(*) Type ur-type, 1
		// EE
		// CH Type ur-type, 1
		urType0.addRule(ATTRIBUTE_GENERIC, urType0);
		urType0.addRule(START_ELEMENT_GENERIC, urType1);
		urType0.addTerminalRule(END_ELEMENT);
		urType0.addRule(CHARACTERS_GENERIC, urType1);
		// anyType is castable
		urType0.setTypeCastable(true);
		// types are NOT nillable
		urType0.setNillable(false);

		// Type ur-type, 1 :
		// SE(*) Type ur-type, 1
		// EE
		// CH Type ur-type, 1
		urType1.addRule(START_ELEMENT_GENERIC, urType1);
		urType1.addTerminalRule(END_ELEMENT);
		urType1.addRule(CHARACTERS_GENERIC, urType1);

		// empty types
		SchemaInformedRule emptyUrType1 = new SchemaInformedElement();
		SchemaInformedFirstStartTagRule emptyUrType0 = new SchemaInformedFirstStartTag(
				emptyUrType1);
		// set type empty
		urType0.setTypeEmpty(emptyUrType0);
		
		// TypeEmpty ur-type, 0 :
		// AT (*) TypeEmpty ur-type, 0
		// EE
		emptyUrType0.addRule(ATTRIBUTE_GENERIC, emptyUrType0);
		emptyUrType0.addTerminalRule(END_ELEMENT);
		
		// TypeEmpty ur-type, 1 :
		// EE
		emptyUrType1.addTerminalRule(END_ELEMENT);

		return urType0;
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
	 * @throws EXIException
	 */
	protected SchemaInformedFirstStartTagRule translateTypeDefinitionToFSA(
			XSTypeDefinition td) throws EXIException {
		SchemaInformedFirstStartTagRule type_i = null;
		SchemaInformedFirstStartTagRule typeEmpty_i = null;

		// simple vs. complex type handling
		if (td.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
			if (Constants.XSD_ANY_TYPE.equals(td.getName())
					&& XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(td
							.getNamespace())) {
				// ur-type
				SchemaInformedFirstStartTagRule urType = getUrTypeRule();
				type_i = urType;
				typeEmpty_i = urType.getTypeEmpty();
			} else {
				XSComplexTypeDefinition ctd = (XSComplexTypeDefinition) td;

				SchemaInformedRule ruleContent = translateComplexTypeDefinitionToFSA(ctd);

				// create copy of Element_i_content --> Element_i_content_2
				// (used for content schema-deviations in start-tags, direct
				// jumps)
				SchemaInformedRule ruleContent2 = ruleContent.duplicate();

				// attributes
				XSObjectList attributes = ctd.getAttributeUses();
				XSWildcard attributeWC = ctd.getAttributeWildcard();

				// type_i (start tag)
				SchemaInformedStartTagRule sistr = handleAttributes(
						ruleContent, ruleContent2, attributes, attributeWC);
				type_i = new SchemaInformedFirstStartTag(sistr);
				type_i.setTypeCastable(isTypeCastable(ctd));

				// typeEmpty_i
				SchemaInformedRule ruleEnd = new SchemaInformedElement();
				ruleEnd.addTerminalRule(END_ELEMENT);
				typeEmpty_i = new SchemaInformedFirstStartTag(handleAttributes(
						ruleEnd, ruleEnd, attributes, attributeWC));
			}
		} else {
			assert (td.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE);
			// Type i
			XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) td;
			SchemaInformedElement simpleContent = translateSimpleTypeDefinitionToFSA(std);
			type_i = new SchemaInformedFirstStartTag(handleAttributes(
					simpleContent, simpleContent, null, null));
			type_i.setTypeCastable(isTypeCastable(std));
			// TypeEmpty i
			SchemaInformedRule ruleEnd = new SchemaInformedElement();
			ruleEnd.addTerminalRule(END_ELEMENT);
			typeEmpty_i = new SchemaInformedFirstStartTag(handleAttributes(
					ruleEnd, ruleEnd, null, null));
		}

		if (!td.getAnonymous()) {
			// add to localName table for string table pre-population
			addLocalNameStringEntry(td.getNamespace(), td.getName());
		}

		// type_i.setFirstElementRule();
		// typeEmpty_i.setFirstElementRule();
		type_i.setTypeEmpty(typeEmpty_i);

		return type_i;
		// return new TypeGrammar(type_i, typeEmpty_i);
	}

	protected boolean isTypeCastable(XSTypeDefinition td) {

		boolean isTypeCastable = false;

		// has named sub-types
		XSNamedMap types = this.xsModel
				.getComponents(XSConstants.TYPE_DEFINITION);
		for (int i = 0; i < types.getLength(); i++) {
			XSTypeDefinition td2 = (XSTypeDefinition) types.item(i);

			if (td.equals(td2.getBaseType())) {
				isTypeCastable = true;
			}
		}

		// is a simple type definition of which {variety} is union
		if (!isTypeCastable
				&& td.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
			XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) td;
			isTypeCastable = (std.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION);
		}

		return isTypeCastable;
	}

	protected SchemaInformedRule translateComplexTypeDefinitionToFSA(
			XSComplexTypeDefinition ctd) throws EXIException {
		SchemaInformedRule ruleContent = null;

		switch (ctd.getContentType()) {
		case XSComplexTypeDefinition.CONTENTTYPE_EMPTY:
			// Represents an empty content type.
			// A content type with the distinguished value empty validates
			// elements
			// with no character or element information item children.
			// (attributes only, no content allowed)
			ruleContent = new SchemaInformedElement();
			ruleContent.addTerminalRule(END_ELEMENT);
			break;
		case XSComplexTypeDefinition.CONTENTTYPE_SIMPLE:
			// Represents a simple content type.
			// A content type which is simple validates elements with
			// character-only children.
			XSSimpleTypeDefinition std = ctd.getSimpleType();
			ruleContent = translateSimpleTypeDefinitionToFSA(std);
			break;
		case XSComplexTypeDefinition.CONTENTTYPE_ELEMENT:
			// Represents an element-only content type.
			// An element-only content type validates elements with children
			// that conform to the supplied content model.

			// The {content model} of a complex type definition is a single
			// particle
			boolean isMixedContent = false;
			ruleContent = handleParticle(ctd, isMixedContent);
			break;
		default:
			assert (ctd.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_MIXED);
			// Represents a mixed content type
			// The {content model} of a complex type definition is a single
			// particle
			isMixedContent = true;
			ruleContent = handleParticle(ctd, isMixedContent);

			break;
		}

		return ruleContent;

	}

	protected SchemaInformedElement translateSimpleTypeDefinitionToFSA(
			XSSimpleTypeDefinition std) throws EXIException {

		QName nameValueType;
		if (std.getAnonymous()) {
			nameValueType = new QName(null, "Anonymous");
		} else {
			nameValueType = new QName(std.getNamespace(), std.getName());
		}

		Characters chSchemaValid = new Characters(nameValueType, BuiltIn
				.getDatatype(std));

		SchemaInformedElement type_i_1 = new SchemaInformedElement();

		SchemaInformedElement type_i_0 = new SchemaInformedElement();
		type_i_0.addRule(chSchemaValid, type_i_1);

		type_i_1.addTerminalRule(END_ELEMENT);

		// TODO TypeEmpty

		return type_i_0;
	}

}
