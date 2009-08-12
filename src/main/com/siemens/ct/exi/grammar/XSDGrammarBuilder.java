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

package com.siemens.ct.exi.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.XMLConstants;

import org.apache.xerces.impl.xs.models.EXIContentModelBuilder;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.AttributeGeneric;
import com.siemens.ct.exi.grammar.event.AttributeNS;
import com.siemens.ct.exi.grammar.event.Characters;
import com.siemens.ct.exi.grammar.event.CharactersGeneric;
import com.siemens.ct.exi.grammar.event.EndElement;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElementGeneric;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedElement;
import com.siemens.ct.exi.grammar.rule.SchemaInformedStartTag;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */
public class XSDGrammarBuilder extends EXIContentModelBuilder {

	protected Map<ExpandedName, TypeGrammar> grammarTypes;

	// sorted LocalNames (pre-initializing LocalName Partition)
	protected Set<ExpandedName> sortedLocalNames;

	// avoids recursive element handling
	private Set<XSElementDeclaration> handledElements;

	// / ??????
	Map<ExpandedName, LNC> namedElementContainers;

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
		grammarTypes = new HashMap<ExpandedName, TypeGrammar>();
		sortedLocalNames = new TreeSet<ExpandedName>();
		namedElementContainers = new HashMap<ExpandedName, LNC>();
	}

	@Override
	protected void initEachRun() {
		super.initEachRun();

		handledElements.clear();
		grammarTypes.clear();
		sortedLocalNames.clear();

		namedElementContainers.clear();
	}

	public SchemaInformedGrammar toGrammar() throws EXIException {
		if (xsModel == null || schemaParsingErrors.size() > 0) {
			String exMsg = "Problem occured while building XML Schema Model (XSModel)!";

			for (int i = 0; i < schemaParsingErrors.size(); i++) {
				exMsg += "\n. " + schemaParsingErrors.get(i);
			}

			throw new EXIException(exMsg);
		}

		// initialize grammars (--> global elements)
		List<ExpandedName> globalElements = initGrammars();

		// (sorted) schema URIs and localNames
		String[] sortedURIs = initURITableEntries();
		SchemaEntry[] schemaEntries = new SchemaEntry[sortedURIs.length];
		for (int i = 0; i < sortedURIs.length; i++) {
			String uri = sortedURIs[i];

			List<String> localNames = new ArrayList<String>();
			for (ExpandedName ename : sortedLocalNames) {
				if (ename.getNamespaceURI().equals(uri)) {
					localNames.add(ename.getLocalName());
				}
			}
			String[] localNames2 = new String[localNames.size()];
			localNames.toArray(localNames2);
			schemaEntries[i] = new SchemaEntry(uri, localNames2);
		}

		// named elements
		List<ElementContainer> namedElements = new ArrayList<ElementContainer>();

		for (XSElementDeclaration el : handledElements) {
			ExpandedName ename = new ExpandedName(el.getNamespace(), el
					.getName());
			ElementContainer ec = new ElementContainer(ename);

			if (namedElements.contains(ec)) {
				continue;
			}
			// namedElements[i] = ec;
			namedElements.add(ec);

			// set lnc data
			LNC uuu = namedElementContainers.get(ename);

			/*
			 * Schema-Rules
			 */
			if (uuu.hasUniqueSchemaRule()) {
				ec.setUniqueRule(uuu.schemaRule);
			} else {
				Rule[] rules = new Rule[uuu.schemaRules.size()];
				uuu.schemaRules.toArray(rules);
				ExpandedName[][] scopes = new ExpandedName[uuu.schemaRuleScopes
						.size()][];
				uuu.schemaRuleScopes.toArray(scopes);
				ec.setAmbiguousRules(rules, scopes);
			}
			// schema-Informed ElementFragmentGrammar
			ec
					.setSchemaInformedElementFragmentGrammar(uuu.elementFragmentStartTag);

		}

		// named elements to array
		ElementContainer[] namedElementsB = new ElementContainer[namedElements
				.size()];
		namedElements.toArray(namedElementsB);

		// global elements (subset of named-elements)
		ExpandedName[] globalElementsB = new ExpandedName[globalElements.size()];
		globalElements.toArray(globalElementsB);

		SchemaInformedGrammar sig = new SchemaInformedGrammar(schemaEntries,
				namedElementsB, globalElementsB);

		/*
		 * type grammar
		 */
		sig.setTypeGrammars(grammarTypes);

		/*
		 * global attributes
		 */
		XSNamedMap nm = xsModel
				.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
		Attribute[] globalAttributes = new Attribute[nm.getLength()];
		for (int i = 0; i < nm.getLength(); i++) {
			XSAttributeDeclaration atDecl = (XSAttributeDeclaration) nm.item(i);
			Attribute at = getAttributeEvent(atDecl);
			globalAttributes[i] = at;
		}
		sig.setGlobalAttributes(globalAttributes);

		return sig;
	}

	protected static boolean isNamespacesOfInterest(String namespaceURI) {
		assert (namespaceURI != null);
		if (namespaceURI.equals(XMLConstants.XML_NS_URI)
				|| namespaceURI
						.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)
				|| namespaceURI.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
			return false;
		} else {
			return true;
		}
	}

	protected String[] initURITableEntries() {
		StringList namespaces = xsModel.getNamespaces();
		TreeSet<String> sortedURIs = new TreeSet<String>();

		for (int i = 0; i < namespaces.getLength(); i++) {
			String uri = namespaces.item(i) == null ? XMLConstants.NULL_NS_URI
					: namespaces.item(i);
			if (isNamespacesOfInterest(uri)) {
				sortedURIs.add(uri);
			}
		}

		// default namespace does not show up all the time ?
		if (!sortedURIs.contains(XMLConstants.NULL_NS_URI)) {
			sortedURIs.add(XMLConstants.NULL_NS_URI);
		}

		// copy to array
		String[] uris = new String[sortedURIs.size()];
		sortedURIs.toArray(uris);

		return uris;
	}

	/*
	 * When a schema is provided, the string table (Local-name) is also
	 * pre-populated with the local name of each attribute, element and type
	 * declared in the schema, partitioned by namespace URI and sorted
	 * lexicographically.
	 */
	protected void addLocalNameStringEntry(ExpandedName expName) {
		if (isNamespacesOfInterest(expName.getNamespaceURI())) {
			if (!sortedLocalNames.contains(expName)) {
				// new entry
				sortedLocalNames.add(expName);
			}
		}
	}

	protected List<ExpandedName> initGrammars() throws EXIException {
		List<ExpandedName> docElements = new ArrayList<ExpandedName>();

		// handle all known types
		XSNamedMap types = xsModel.getComponents(XSConstants.TYPE_DEFINITION);
		for (int i = 0; i < types.getLength(); i++) {
			XSTypeDefinition td = (XSTypeDefinition) types.item(i);

			ExpandedName name = new ExpandedName(td.getNamespace(), td
					.getName());
			TypeGrammar typeGrammar = translateTypeDefinitionToFSA(td);
			
			grammarTypes.put(name, typeGrammar);
		}

		// global elements
		XSNamedMap xsGlobalElements = xsModel
				.getComponents(XSConstants.ELEMENT_DECLARATION);
		for (int i = 0; i < xsGlobalElements.getLength(); i++) {
			XSElementDeclaration globalElement = (XSElementDeclaration) xsGlobalElements
					.item(i);
			
			// add global elements (DocContent)
			docElements.add(new ExpandedName(globalElement.getNamespace(),
					globalElement.getName()));

			// create rules for global elements (do not have scope)
			translatElementDeclarationToFSA(globalElement, new ExpandedName[0]);
		}

		// any remaining elements ? (not global elements)
		for (int i = 0; i < remainingElements.size(); i++) {
			XSElementDeclaration remElement = remainingElements.get(i);

			//	fetch all distinctive enclosing elements
			List<XSElementDeclaration> distinctiveEnclosingElements = new ArrayList<XSElementDeclaration>();
			addAllDistinctiveEnclosingElements(remElement, distinctiveEnclosingElements);
			// System.out.println(distinctiveEnclosingElements + " <-- " + remElement );
			
			//	convert element-declaration list to expanded-name array
			ExpandedName distinctiveENs[] = new ExpandedName[distinctiveEnclosingElements.size()];
			for(int k=0; k<distinctiveEnclosingElements.size(); k++) {
				XSElementDeclaration de = distinctiveEnclosingElements.get(k);
				distinctiveENs[k] = new ExpandedName(de.getNamespace(), de.getName());
			}
			
			translatElementDeclarationToFSA(remElement, distinctiveENs);
			
//			// fetch enclosing elements
//			List<XSElementDeclaration> directEnclosingElements = getDirectEnclosingElements(remElement);
//
//			// iterate over enclosing elements
//			if (directEnclosingElements.size() > 0) {
//				for (XSElementDeclaration enclElement : directEnclosingElements) {					
//					ExpandedName scopeElement = new ExpandedName(enclElement
//							.getNamespace(), enclElement.getName());
//					translatElementDeclarationToFSA(remElement, scopeElement);
//				}
//			} else {
//				System.err.println("XXXXXXXXXXX " + distinctiveEnclosingElements.size());
//				translatElementDeclarationToFSA(remElement, null);
//			}
		}

		return docElements;
	}
	
	protected void addAllDistinctiveEnclosingElements(XSElementDeclaration element, List<XSElementDeclaration> allEnclosingElements ) {
		XSComplexTypeDefinition ctd = element.getEnclosingCTDefinition();
		if(ctd == null) {
			//	global element --> STOP
		} else {
			List<XSElementDeclaration> directEnclElements =  getDirectEnclosingElements(element);
			if (directEnclElements.size() > 1 ) {
				//	distinction since we do have at least 2 different elements
			} else {
				for(XSElementDeclaration enclEl : directEnclElements ) {
//					if (enclEl.getScope() != XSConstants.SCOPE_GLOBAL) {
						allEnclosingElements.add(0, enclEl);	//	add to head
						addAllDistinctiveEnclosingElements(enclEl, allEnclosingElements );	
//					}
				}				
			}
		}
	}
	
	protected List<XSElementDeclaration> getDirectEnclosingElements(XSElementDeclaration element) {
		
		List<XSComplexTypeDefinition> enclosingCTDs = enclosingTypes.get(element);
		List<XSElementDeclaration> enclosingElements = new ArrayList<XSElementDeclaration>();
		
		// check already handled elements
		for (XSElementDeclaration he : handledElements) {
			XSTypeDefinition td = he.getTypeDefinition();
			if (enclosingCTDs.contains(td)) {
				if (!enclosingElements.contains(he)) {
					enclosingElements.add(he);
				}
			}
		}
		// check remaining elements
		for (XSElementDeclaration re : remainingElements) {
			if (re != element) { // unequal this remaining element
				XSTypeDefinition td = re.getTypeDefinition();
				if (enclosingCTDs.contains(td)) {
					if (!enclosingElements.contains(re)) {
						enclosingElements.add(re);	
					}
				}
			}
		}
		
		return enclosingElements;
	}

	protected void updateToFirstRule(TypeGrammar typeGrammar, boolean nillable) {
		// first rule is different in the sense of xsi:type, xsi:nil, NS & SC
		SchemaInformedRule type = typeGrammar.getType();
		SchemaInformedRule typeEmpty = typeGrammar.getTypeEmpty();
		type.setFirstElementRule();
		type.setNillable(nillable, typeEmpty);
	}

	protected void addRuleFor(ExpandedName name, ExpandedName distinctiveENs[],
			TypeGrammar typeGrammar,
			XSElementDeclaration elementDeclaration) {

		// first rule is different in the sense of xsi:type, xsi:nil, NS & SC
		updateToFirstRule(typeGrammar, elementDeclaration.getNillable());
		SchemaInformedRule type = typeGrammar.getType();

		LNC lnc = getLNC(name);
		lnc.addSchemaRule(type, elementDeclaration, distinctiveENs);
	}

	protected LNC getLNC(ExpandedName name) {
		LNC lnc;
		if (namedElementContainers.containsKey(name)) {
			lnc = namedElementContainers.get(name);
		} else {
			lnc = new LNC(name);
			namedElementContainers.put(name, lnc);
		}
		return lnc;
	}

	protected static List<XSAttributeUse> getSortedAttributes(
			XSObjectList attributes) {

		List<ExpandedName> sortedNames = new ArrayList<ExpandedName>();
		Map<ExpandedName, XSAttributeUse> ht = new HashMap<ExpandedName, XSAttributeUse>();

		// collect names and attributes
		for (int i = 0; i < attributes.getLength(); i++) {
			XSObject attr = attributes.item(i);
			assert (attr.getType() == XSConstants.ATTRIBUTE_USE);
			XSAttributeUse attrUse = (XSAttributeUse) attr;
			XSAttributeDeclaration attrDecl = attrUse.getAttrDeclaration();

			ExpandedName key = new ExpandedName(attrDecl.getNamespace(),
					attrDecl.getName());

			sortedNames.add(key);
			ht.put(key, attrUse);
		}
		
		// sort collected "names" list
		Collections.sort(sortedNames);

		// construct sorted attribute list
		List<XSAttributeUse> sortedAttributes = new ArrayList<XSAttributeUse>();
		for (ExpandedName key : sortedNames) {
			XSAttributeUse xsAttr = ht.get(key);
			sortedAttributes.add(xsAttr);
		}

		return sortedAttributes;
	}

	protected Attribute getAttributeEvent(XSAttributeDeclaration attrDecl)
			throws EXIException {
		XSSimpleTypeDefinition attrTypeDefinition = attrDecl
				.getTypeDefinition();

		// expanded name for string table pre-population
		ExpandedName atName = new ExpandedName(attrDecl.getNamespace(),
				attrDecl.getName());
		addLocalNameStringEntry(atName);

		ExpandedName qNameType;

		if (attrTypeDefinition.getAnonymous()) {
			XSTypeDefinition tdBase = attrTypeDefinition.getBaseType();

			if (tdBase.getName() == null) {
				//	
				// System.err.println ( "Abort processing " + tdBase + " (set "
				// + BuiltIn.DEFAULT_VALUE_NAME + ")" );
				qNameType = BuiltIn.DEFAULT_VALUE_NAME;
				// continue;
			} else {
				qNameType = new ExpandedName(tdBase.getNamespace(), tdBase
						.getName());
			}

		} else {
			qNameType = new ExpandedName(attrTypeDefinition.getNamespace(),
					attrTypeDefinition.getName());
		}

		return new Attribute(atName.getNamespaceURI(), atName.getLocalName(),
				qNameType, BuiltIn.getDatatype(attrTypeDefinition));
	}

	protected SchemaInformedRule handleAttributes(SchemaInformedRule ruleContent,
			SchemaInformedRule ruleContent2, XSObjectList attributes,
			XSWildcard attributeWC) throws EXIException {

		// Attribute Uses
		// http://www.w3.org/TR/exi/#attributeUses

		SchemaInformedRule ruleStart = new SchemaInformedStartTag(
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
			ruleStart.addTerminalRule(END_ELEMENT);
			// handleAttributeWildCard(attributeWC, ruleStart);
		}

		if (attributes != null && attributes.getLength() > 0) {
			// attributes will occur sorted lexically by qname (in EXI Stream)
			List<XSAttributeUse> vSortedAttributes = getSortedAttributes(attributes);

			// traverse in reverse order
			for (int i = vSortedAttributes.size() - 1; i >= 0; i--) {
				XSAttributeUse attrUse = vSortedAttributes.get(i);

				Attribute at = getAttributeEvent(attrUse.getAttrDeclaration());

				SchemaInformedRule newCurrent = new SchemaInformedStartTag(
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
						if (ei.event.isEventType(EventType.ATTRIBUTE_GENERIC)) {
							//	AT(*) wilcard added before
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
			rule.addRule(new AttributeGeneric(), rule);
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
				rule.addRule(new AttributeNS(sl.item(k)), rule);
			}
		}
	}

	protected TypeGrammar getTypeGrammar(String namespaceURI, String name) {
		ExpandedName en = new ExpandedName(namespaceURI, name);
		return grammarTypes.get(en);
	}

	protected void translatElementDeclarationToFSA(
			XSElementDeclaration xsElementDeclaration, ExpandedName distinctiveENs[])
			throws EXIException {

		
//		XSElementDecl[] ddd = subGroupHandler.getSubstitutionGroup((XSElementDecl) xsElementDeclaration);
//		System.out.println(xsElementDeclaration + " --> " + ddd.length);
//		XSObjectList l = xsModel.getSubstitutionGroup(xsElementDeclaration);
//		System.out.println("List" + " --> " + l.getLength());
//		if(l.getLength() > 0) {
//			
//		}
//		subGroupHandler.addSubstitutionGroup(arg0);
		
		// type definition
		XSTypeDefinition td = xsElementDeclaration.getTypeDefinition();

		// handle element recursion
		if (this.handledElements.contains(xsElementDeclaration)) {
			// element already handled
			return;
		}
		this.handledElements.add(xsElementDeclaration);

		// expanded name
		ExpandedName elementName = new ExpandedName(xsElementDeclaration
				.getNamespace(), xsElementDeclaration.getName());

		// add local name entry for string table pre-population
		addLocalNameStringEntry(elementName);

		// type grammar
		TypeGrammar typeGrammar;
		if (td.getAnonymous()) {
			// create new type grammar for an anonymous type
			typeGrammar = translateTypeDefinitionToFSA(td);
		} else {
			// fetch existing grammar from pre-processed type
			TypeGrammar tg = getTypeGrammar(td.getNamespace(), td.getName());

			// *duplicate* first productions to allow different behavior
			// (e.g. property nillable element not type dependent)

			SchemaInformedRule sir = tg.getType().duplicate();
			typeGrammar = new TypeGrammar(sir, tg.typeEmpty);
		}

		// set schema-rule for element
		addRuleFor(elementName, distinctiveENs, typeGrammar,
				xsElementDeclaration);
	}

	public static TypeGrammar getUrTypeRule() {
		// ur-Type
		SchemaInformedRule urType1 = new SchemaInformedElement();
		urType1.addRule(new StartElementGeneric(), urType1);
		urType1.addTerminalRule(new EndElement());
		urType1.addRule(new CharactersGeneric(), urType1);

		SchemaInformedRule urType0 = new SchemaInformedStartTag(urType1);
		urType0.addRule(new AttributeGeneric(), urType0);
		urType0.addRule(new StartElementGeneric(), urType1);
		urType0.addTerminalRule(new EndElement());
		urType0.addRule(new CharactersGeneric(), urType1);
		urType0.setTypeCastable(true);
		urType0.setFirstElementRule();

		// empty ur-Type
		SchemaInformedRule emptyUrType0 = new SchemaInformedElement();
		emptyUrType0.addRule(new AttributeGeneric(), emptyUrType0);
		emptyUrType0.addTerminalRule(new EndElement());
		// emptyUrType0.setFirstElementRule();

		// nillable ?
		urType0.setNillable(false, emptyUrType0);

		return new TypeGrammar(urType0, emptyUrType0);
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
	protected TypeGrammar translateTypeDefinitionToFSA(XSTypeDefinition td) throws EXIException {
		SchemaInformedRule type_i = null;
		SchemaInformedRule typeEmpty_i = null;

		// simple vs. complex type handling
		if (td.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
			if (Constants.XSD_ANY_TYPE.equals(td.getName())
					&& XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(td
							.getNamespace())) {
				// ur-type
				TypeGrammar urType = getUrTypeRule();
				type_i = urType.type;
				typeEmpty_i = urType.typeEmpty;
			} else {
				XSComplexTypeDefinition ctd = (XSComplexTypeDefinition) td;

				SchemaInformedRule ruleContent = translateComplexTypeDefinitionToFSA(
						ctd);

				// create copy of Element_i_content --> Element_i_content_2
				// (used for content schema-deviations in start-tags, direct
				// jumps)
				SchemaInformedRule ruleContent2 = ruleContent.duplicate();

				// attributes
				XSObjectList attributes = ctd.getAttributeUses();
				XSWildcard attributeWC = ctd.getAttributeWildcard();

				// type_i (start tag)
				type_i = handleAttributes(ruleContent, ruleContent2,
						attributes, attributeWC);
				type_i.setTypeCastable(isTypeCastable(ctd));

				// typeEmpty_i
				SchemaInformedRule ruleEnd = new SchemaInformedElement();
				ruleEnd.addTerminalRule(END_ELEMENT);
				typeEmpty_i = handleAttributes(ruleEnd, ruleEnd, attributes,
						attributeWC);
			}
		} else {
			assert (td.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE);
			// Type i
			XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) td;
			SchemaInformedElement simpleContent = translateSimpleTypeDefinitionToFSA(std);
			type_i = handleAttributes(simpleContent, simpleContent, null, null);
			type_i.setTypeCastable(isTypeCastable(std));
			// TypeEmpty i
			SchemaInformedRule ruleEnd = new SchemaInformedElement();
			ruleEnd.addTerminalRule(END_ELEMENT);
			typeEmpty_i = handleAttributes(ruleEnd, ruleEnd, null, null);
		}

		if (!td.getAnonymous()) {
			// add to localName table for string table pre-population
			ExpandedName typeName = new ExpandedName(td.getNamespace(), td
					.getName());
			addLocalNameStringEntry(typeName);
		}

		return new TypeGrammar(type_i, typeEmpty_i);
	}

	protected boolean isTypeCastable(XSTypeDefinition td) {
		
		boolean isTypeCastable = false;
		
		//	has named sub-types
		XSNamedMap types = this.xsModel
				.getComponents(XSConstants.TYPE_DEFINITION);
		for (int i = 0; i < types.getLength(); i++) {
			XSTypeDefinition td2 = (XSTypeDefinition) types.item(i);

			if (td.equals(td2.getBaseType())) {
				isTypeCastable = true;
			}
		}
		
		//  is a simple type definition of which {variety} is union
		if (!isTypeCastable && td.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
			XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) td;
			isTypeCastable = (std.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION);
		}

		return isTypeCastable;
	}

	protected SchemaInformedRule translateComplexTypeDefinitionToFSA(
			XSComplexTypeDefinition ctd)
			throws EXIException {
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
			ruleContent = handleParticle(ctd);
			break;
		default:
			assert (ctd.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_MIXED);
			// Represents a mixed content type

			// The {content model} of a complex type definition is a single
			// particle
			// XSParticle xsParticleMixed = ctd.getParticle();

			// content
			ruleContent = handleParticle(ctd);

			// mixed transitions
			addMixedTransitions(ruleContent, new ArrayList<Rule>());
			ruleContent.setLabel("MixedContent");
			break;
		}

		return ruleContent;

	}

	protected void addMixedTransitions(Rule ruleMixedContent, List<Rule> handled) {
		if (handled.contains(ruleMixedContent)) {
			// abort
			return;
		}
		handled.add(ruleMixedContent);

		// mixed --> generic characters events
		ruleMixedContent.addRule(new CharactersGeneric(), ruleMixedContent);

		for (int i = 0; i < ruleMixedContent.getNumberOfEvents(); i++) {
			Rule r = ruleMixedContent.lookFor(i).next;
			if (!r.isTerminalRule()) {
				addMixedTransitions(r, handled);
			}
		}
	}

	protected SchemaInformedElement translateSimpleTypeDefinitionToFSA(
			XSSimpleTypeDefinition std) throws EXIException {

		ExpandedName nameValueType;
		if (std.getAnonymous()) {
			nameValueType = new ExpandedName(null, "Anonymous");
		} else {
			nameValueType = new ExpandedName(std.getNamespace(), std.getName());
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

	class LNC implements Comparable<LNC> {
		protected final ExpandedName name;
		// schema-rule
		protected Rule schemaRule;
		XSElementDeclaration schemaRuleType;
		ExpandedName[] schemaRuleScope;
		protected List<Rule> schemaRules;
		protected List<XSElementDeclaration> schemaRuleElements;
		protected List<ExpandedName[]> schemaRuleScopes;
		// unique
		boolean unique = true;
		// fragment rule for several qnames
		protected SchemaInformedStartTag elementFragmentStartTag;

		public LNC(ExpandedName name) {
			this.name = name;

			schemaRules = new ArrayList<Rule>();
			schemaRuleElements = new ArrayList<XSElementDeclaration>();
			schemaRuleScopes = new ArrayList<ExpandedName[]>();
		}

		public void addSchemaRule(Rule rule, XSElementDeclaration el,
				ExpandedName distinctiveENs[]) {
			assert (rule.isFirstElementRule());

			if (schemaRule == null && unique) {
				// first entry (should be the default one)
				// --> set schema rule
				this.schemaRule = rule;
				this.schemaRuleType = el;
				this.schemaRuleScope = distinctiveENs;
				// add to rule list
				schemaRules.add(rule);
				schemaRuleElements.add(el);
				schemaRuleScopes.add(distinctiveENs);
			} else {
				// at least one rule present
				// --> check for each weather it is already done
				boolean doneAlready = false;
				for (XSElementDeclaration e : schemaRuleElements) {
					XSTypeDefinition t = e.getTypeDefinition();
					if (t == el.getTypeDefinition()
							&& e.getNillable() == el.getNillable()) {
						// same type
						// --> rules should be the same
						// assert(schemaRule.equals(rule));
						doneAlready = true;
					}
				}

				if (!doneAlready) {
					// add to list
					schemaRules.add(rule);
					schemaRuleElements.add(el);
					schemaRuleScopes.add(distinctiveENs);
					// un-set uniqueness
					unique = false;
					schemaRule = null;
					// merge grammars for Schema-informed Element
					// FragmentGrammar
					buildSchemaInformedElementFragmentGrammar();
				}
			}
		}

		protected void buildSchemaInformedElementFragmentGrammar() {
			boolean doFG = false;
			if (doFG) {
				// TODO
				// see http://www.w3.org/TR/exi/#informedElementFragGrammar

				// attributes sorted lexicographically, first by localName, then
				// by
				// uri
				List<ExpandedName> attributes = new ArrayList<ExpandedName>();

				for (XSElementDeclaration el : schemaRuleElements) {
					XSTypeDefinition td = el.getTypeDefinition();
					// TODO If all qname identical attributes have the same type
					// name, their
					// value is represented using that type. Otherwise, their
					// value
					// is
					// represented as a String.

					if (td.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
						XSComplexTypeDefinition ctd = (XSComplexTypeDefinition) td;
						XSObjectList atts = ctd.getAttributeUses();

						for (int i = 0; i < atts.getLength(); i++) {
							XSAttributeUse at = (XSAttributeUse) atts.item(i);
							ExpandedName en = new ExpandedName(at
									.getNamespace(), at.getName());
							if (!attributes.contains(en)) {
								attributes.add(en);
							}
						}
					} else {
						assert (td.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE);
						// XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) td;
					}
				}
			}

			// TODO at present simple BUT NOT standard compliant approach

			// ElementFragmentContent, ??? , SE(*), EE, CH
			SchemaInformedElement elementFragmentContent = new SchemaInformedElement();
			elementFragmentContent.addRule(new StartElementGeneric(),
					elementFragmentContent);
			elementFragmentContent.addTerminalRule(new EndElement());
			elementFragmentContent.addRule(new CharactersGeneric(),
					elementFragmentContent);

			// ElementFragmentStartTag, ???, AT(*), SE(*), EE, CH
			elementFragmentStartTag = new SchemaInformedStartTag(
					elementFragmentContent);
			elementFragmentStartTag.addRule(new AttributeGeneric(),
					elementFragmentStartTag);
			elementFragmentStartTag.addRule(new StartElementGeneric(),
					elementFragmentContent);
			elementFragmentStartTag.addTerminalRule(new EndElement());
			elementFragmentStartTag.addRule(new CharactersGeneric(),
					elementFragmentContent);

			// ElementFragmentTypeEmpty, ???, AT(*), EE
			SchemaInformedStartTag elementFragmentTypeEmpty = new SchemaInformedStartTag(
					elementFragmentContent);
			elementFragmentTypeEmpty.addRule(new AttributeGeneric(),
					elementFragmentStartTag);
			elementFragmentTypeEmpty.addTerminalRule(new EndElement());

			elementFragmentStartTag.setNillable(true, elementFragmentTypeEmpty);
			elementFragmentStartTag.setFirstElementRule();
		}

		public boolean hasUniqueSchemaRule() {
			return unique;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof LNC) {
				LNC other = (LNC) o;
				return (this.name.equals(other.name));
			}
			return false;
		}

		/*
		 * This method returns the hash code value as an integer and is
		 * supported for the benefit of hashing based collection classes such as
		 * Hashtable, HashMap, HashSet etc
		 * 
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		public final int hashCode() {
			return name.hashCode();
		}

		public String toString() {
			return "LNC(" + name.toString() + ")";
		}

		public int compareTo(LNC o) {
			return this.name.compareTo(o.name);
		}
	}

}
