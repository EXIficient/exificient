/*
 * Copyright (C) 2007-2015 Siemens AG
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

package com.siemens.ct.exi.grammars;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.xerces.impl.xpath.regex.EXIRegularExpression;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.GrammarUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.context.UriContext;
import com.siemens.ct.exi.datatype.BinaryBase64Datatype;
import com.siemens.ct.exi.datatype.BinaryHexDatatype;
import com.siemens.ct.exi.datatype.BooleanDatatype;
import com.siemens.ct.exi.datatype.BooleanFacetDatatype;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatetimeDatatype;
import com.siemens.ct.exi.datatype.DecimalDatatype;
import com.siemens.ct.exi.datatype.EnumerationDatatype;
import com.siemens.ct.exi.datatype.FloatDatatype;
import com.siemens.ct.exi.datatype.IntegerDatatype;
import com.siemens.ct.exi.datatype.ListDatatype;
import com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype;
import com.siemens.ct.exi.datatype.RestrictedCharacterSetDatatype;
import com.siemens.ct.exi.datatype.StringDatatype;
import com.siemens.ct.exi.datatype.UnsignedIntegerDatatype;
import com.siemens.ct.exi.datatype.charset.CodePointCharacterSet;
import com.siemens.ct.exi.datatype.charset.RestrictedCharacterSet;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.AttributeNS;
import com.siemens.ct.exi.grammars.event.Characters;
import com.siemens.ct.exi.grammars.event.EndDocument;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartDocument;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.event.StartElementNS;
import com.siemens.ct.exi.grammars.grammar.DocEnd;
import com.siemens.ct.exi.grammars.grammar.Document;
import com.siemens.ct.exi.grammars.grammar.Fragment;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedDocContent;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedElement;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTagGrammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFragmentContent;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedGrammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedStartTag;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedStartTagGrammar;
import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.types.BuiltIn;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.types.DateTimeType;
import com.siemens.ct.exi.types.IntegerType;
import com.siemens.ct.exi.values.BinaryBase64Value;
import com.siemens.ct.exi.values.BinaryHexValue;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.DateTimeValue;
import com.siemens.ct.exi.values.DecimalValue;
import com.siemens.ct.exi.values.FloatValue;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class XSDGrammarsBuilder extends EXIContentModelBuilder {

	public static final int MAX_BOUNDED_NBIT_INTEGER_RANGE = 4096;

	protected final SchemaInformedGrammar SIMPLE_END_ELEMENT_RULE;

	protected final SchemaInformedFirstStartTagGrammar SIMPLE_END_ELEMENT_EMPTY_RULE;

	protected final SchemaInformedFirstStartTagGrammar SIMPLE_END_ELEMENT_EMPTY_RULE_TYPABLE;

	protected Map<QName, SchemaInformedFirstStartTagGrammar> grammarTypes;

	// local-names (pre-initializing LocalName Partition)
	// uri -> localNames
	protected Map<String, List<String>> schemaLocalNames;

	// pool for attribute-declaration of Attribute events
	protected Map<XSAttributeDeclaration, Attribute> attributePool;
	
	// pool for datatypes
	protected Map<XSSimpleTypeDefinition, Datatype> datatypePool;

	// when schema information is available to describe the contents of an EXI
	// stream and more than one element is declared with the same qname
	protected SchemaInformedFirstStartTagGrammar elementFragment0;

	// built-In mapping
	protected Map<QName, QName> datatypeMapping;

	//
	GrammarUriContext[] grammarUriContexts;

	protected XSDGrammarsBuilder() {
		super();

		SIMPLE_END_ELEMENT_RULE = new SchemaInformedElement();
		SIMPLE_END_ELEMENT_RULE.addTerminalProduction(END_ELEMENT);
		//
		SIMPLE_END_ELEMENT_EMPTY_RULE = new SchemaInformedFirstStartTag(
				SIMPLE_END_ELEMENT_RULE);
		SIMPLE_END_ELEMENT_EMPTY_RULE.addTerminalProduction(END_ELEMENT);
		SIMPLE_END_ELEMENT_EMPTY_RULE_TYPABLE = new SchemaInformedFirstStartTag(
				SIMPLE_END_ELEMENT_RULE);
		//
		SIMPLE_END_ELEMENT_EMPTY_RULE_TYPABLE.addTerminalProduction(END_ELEMENT);
		SIMPLE_END_ELEMENT_EMPTY_RULE_TYPABLE.setTypeCastable(true);

		initOnce();
	}

	public static XSDGrammarsBuilder newInstance() {
		return new XSDGrammarsBuilder();
	}

	@Override
	protected void initOnce() {
		super.initOnce();

		grammarTypes = new HashMap<QName, SchemaInformedFirstStartTagGrammar>();
		schemaLocalNames = new HashMap<String, List<String>>();
		attributePool = new HashMap<XSAttributeDeclaration, Attribute>();
		datatypePool = new HashMap<XSSimpleTypeDefinition, Datatype>();		

		/*
		 * Datatype mappings
		 */
		datatypeMapping = new HashMap<QName, QName>();
		// Binary
		datatypeMapping.put(BuiltIn.XSD_BASE64BINARY, BuiltIn.XSD_BASE64BINARY);
		datatypeMapping.put(BuiltIn.XSD_HEXBINARY, BuiltIn.XSD_HEXBINARY);
		// Boolean
		datatypeMapping.put(BuiltIn.XSD_BOOLEAN, BuiltIn.XSD_BOOLEAN);
		// Date-Time
		datatypeMapping.put(BuiltIn.XSD_DATETIME, BuiltIn.XSD_DATETIME);
		datatypeMapping.put(BuiltIn.XSD_TIME, BuiltIn.XSD_DATETIME);
		datatypeMapping.put(BuiltIn.XSD_DATE, BuiltIn.XSD_DATETIME);
		datatypeMapping.put(BuiltIn.XSD_GYEARMONTH, BuiltIn.XSD_DATETIME);
		datatypeMapping.put(BuiltIn.XSD_GYEAR, BuiltIn.XSD_DATETIME);
		datatypeMapping.put(BuiltIn.XSD_GMONTHDAY, BuiltIn.XSD_DATETIME);
		datatypeMapping.put(BuiltIn.XSD_GDAY, BuiltIn.XSD_DATETIME);
		datatypeMapping.put(BuiltIn.XSD_GMONTH, BuiltIn.XSD_DATETIME);
		// Decimal
		datatypeMapping.put(BuiltIn.XSD_DECIMAL, BuiltIn.XSD_DECIMAL);
		// Double/Float
		datatypeMapping.put(BuiltIn.XSD_FLOAT, BuiltIn.XSD_FLOAT);
		datatypeMapping.put(BuiltIn.XSD_DOUBLE, BuiltIn.XSD_DOUBLE);
		// Integer
		datatypeMapping.put(BuiltIn.XSD_INTEGER, BuiltIn.XSD_INTEGER);
		// String
		datatypeMapping.put(BuiltIn.XSD_STRING, BuiltIn.XSD_STRING);
		// unknown
		datatypeMapping.put(BuiltIn.XSD_ANY_SIMPLE_TYPE, BuiltIn.XSD_STRING);
	}

	@Override
	protected void initEachRun() {
		super.initEachRun();

		grammarTypes.clear();
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

	protected StartElement createStartElement(QName qname) {
		QNameContext qnameContext = getQNameContext(qname.getNamespaceURI(),
				qname.getLocalPart(), grammarUriContexts);
		StartElement se = new StartElement(qnameContext);
		return se;
	}

	protected StartElementNS createStartElementNS(String uri) {
		GrammarUriContext uriContext = getUriContext(uri, grammarUriContexts);
		StartElementNS seNS = new StartElementNS(
				uriContext.getNamespaceUriID(), uriContext.getNamespaceUri());
		return seNS;
	}

	protected static QNameContext getQNameContext(String namespaceUri,
			String localName, GrammarUriContext[] grammarUriContexts) {
		namespaceUri = namespaceUri == null ? XMLConstants.NULL_NS_URI
				: namespaceUri;
		// uri context
		UriContext guc = getUriContext(namespaceUri, grammarUriContexts);

		if (guc == null) {
			throw new RuntimeException("No known uri : " + namespaceUri);
		} else {
			// qname context
			QNameContext qnameContext = guc.getQNameContext(localName);
			if (qnameContext == null) {
				throw new RuntimeException("No known qname local-name: "
						+ localName);
			}
			return qnameContext;
		}

	}

	public static GrammarUriContext getUriContext(String namespaceUri,
			GrammarUriContext[] grammarUriContexts) {
		namespaceUri = namespaceUri == null ? XMLConstants.NULL_NS_URI
				: namespaceUri;
		assert(grammarUriContexts != null);

		for (GrammarUriContext guc : grammarUriContexts) {
			if (guc.getNamespaceUri().equals(namespaceUri)) {
				return guc;
			}
		}

		throw new RuntimeException("No known uri context for: " + namespaceUri);
	}

	// QName valueType
	protected Attribute createAttribute(QName qname, 
			Datatype datatype) {
		QNameContext qnameContext = getQNameContext(qname.getNamespaceURI(),
				qname.getLocalPart(), grammarUriContexts);
		Attribute at = new Attribute(qnameContext, datatype); // valueType, 

		return at;
	}

	protected AttributeNS createAttributeNS(String uri) {
		GrammarUriContext uriContext = getUriContext(uri, grammarUriContexts);
		AttributeNS atNS = new AttributeNS(uriContext.getNamespaceUriID(),
				uriContext.getNamespaceUri());
		return atNS;
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
			// if (t0.getName() != t1.getName()
			if (!t0.getName().equals(t1.getName())
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

	protected List<StartElement> getFragmentElements() throws EXIException {
		List<StartElement> fragmentElements = new ArrayList<StartElement>();

		// create unique qname map
		Map<QName, List<XSElementDeclaration>> uniqueNamedElements = new HashMap<QName, List<XSElementDeclaration>>();
		for (XSElementDeclaration elDecl : elementPool.keySet()) {
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
				// fragmentElements.add(getStartElement(elements.get(0)));
				StartElement se = translatElementDeclarationToFSA(elements
						.get(0));
				fragmentElements.add(se);
			} else {
				// multiple elements
				if (isSameElementGrammar(elements)) {
					// fragmentElements.add(getStartElement(elements.get(0)));
					StartElement se = translatElementDeclarationToFSA(elements
							.get(0));
					fragmentElements.add(se);
				} else {
					StartElement se = createStartElement(qname); // new
																	// StartElement(qname);
					Grammar elementFragmentGrammar = getSchemaInformedElementFragmentGrammar(uniqueNamedElements);
					se.setGrammar(elementFragmentGrammar);
					fragmentElements.add(se);
					// System.out.println("ambiguous elements " + elements +
					// ", " + qname);
				}
			}
		}

		return fragmentElements;
	}

	// http://www.w3.org/TR/exi/#informedElementFragGrammar
	protected Grammar getSchemaInformedElementFragmentGrammar(
			Map<QName, List<XSElementDeclaration>> uniqueNamedElements)
			throws EXIException {

		if (elementFragment0 != null) {
			return elementFragment0;
		}

		// 8.5.3 Schema-informed Element Fragment Grammar
		SchemaInformedGrammar elementFragment1 = new SchemaInformedElement();
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
				// se = getStartElement(elements.get(0));
				se = translatElementDeclarationToFSA(elements.get(0));
			} else {
				// content is evaluated according to the relaxed Element
				// Fragment grammar
				se = createStartElement(fm); // new StartElement(fm);
				se.setGrammar(elementFragment0);
			}
			elementFragment1.addProduction(se, elementFragment1);
		}

		// SE ( * ) ElementFragment 1 m
		elementFragment1.addProduction(START_ELEMENT_GENERIC, elementFragment1);
		// EE m+1
		elementFragment1.addTerminalProduction(END_ELEMENT);
		// CH [untyped value] ElementFragment 1 m+2
		elementFragment1.addProduction(CHARACTERS_GENERIC, elementFragment1);

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
		// add global attributes
		XSNamedMap nm = xsModel
				.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
		for (int i = 0; i < nm.getLength(); i++) {
			XSAttributeDeclaration atDecl = (XSAttributeDeclaration) nm.item(i);
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
				// at = new Attribute(an);
				at = createAttribute(an, BuiltIn.DEFAULT_DATATYPE); // BuiltIn.DEFAULT_VALUE_NAME,
			}
			elementFragment0.addProduction(at, elementFragment0);
		}
		// AT ( * ) ElementFragment 0 n
		elementFragment0.addProduction(ATTRIBUTE_GENERIC, elementFragment0);

		// SE ( F0 ) ElementFragment 1 n+1
		// ..
		for (QName fm : uniqueNamedElementsList) {
			StartElement se;
			List<XSElementDeclaration> elements = uniqueNamedElements.get(fm);
			if (elements.size() == 1 || isSameElementGrammar(elements)) {
				// se = getStartElement(elements.get(0));
				se = translatElementDeclarationToFSA(elements.get(0));
			} else {
				// content is evaluated according to the relaxed Element
				// Fragment grammar
				se = createStartElement(fm); // new StartElement(fm);
				se.setGrammar(elementFragment0);
			}
			elementFragment0.addProduction(se, elementFragment1);
		}

		// SE ( * ) ElementFragment 1 n+m+1
		elementFragment0.addProduction(START_ELEMENT_GENERIC, elementFragment1);
		// EE n+m+2
		elementFragment0.addTerminalProduction(END_ELEMENT);
		// CH [untyped value] ElementFragment 1 n+m+3
		elementFragment0.addProduction(CHARACTERS_GENERIC, elementFragment1);

		SchemaInformedGrammar elementFragmentEmpty1 = new SchemaInformedElement();
		SchemaInformedFirstStartTagGrammar elementFragmentEmpty0 = new SchemaInformedFirstStartTag(
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
				// at = new Attribute(an);
				at = createAttribute(an, BuiltIn.DEFAULT_DATATYPE); //  BuiltIn.DEFAULT_VALUE_NAME,
			}
			elementFragmentEmpty0.addProduction(at, elementFragmentEmpty0);
		}
		elementFragmentEmpty0.addProduction(ATTRIBUTE_GENERIC, elementFragmentEmpty0);
		elementFragmentEmpty0.addTerminalProduction(END_ELEMENT);

		// ElementFragmentTypeEmpty 1 :
		// EE 0
		elementFragmentEmpty1.addTerminalProduction(END_ELEMENT);

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

	static class NamespaceUriEntry implements Comparable<NamespaceUriEntry> {
		public final String namespaceUri;
		public final List<String> localNames;

		public NamespaceUriEntry(String namespaceUri) {
			this.namespaceUri = namespaceUri;
			this.localNames = new ArrayList<String>();
		}

		public int compareTo(NamespaceUriEntry o) {
			// URI 0 "" [empty string]
			// URI 1 "http://www.w3.org/XML/1998/namespace"
			// URI 2 "http://www.w3.org/2001/XMLSchema-instance"
			// URI 3 "http://www.w3.org/2001/XMLSchema"
			// URI ? <sorted URI list>
			if (XMLConstants.NULL_NS_URI.equals(this.namespaceUri)) {
				return -1;
			} else if (XMLConstants.NULL_NS_URI.equals(o.namespaceUri)) {
				return +1;
			} else if (XMLConstants.XML_NS_URI.equals(this.namespaceUri)) {
				return -1;
			} else if (XMLConstants.XML_NS_URI.equals(o.namespaceUri)) {
				return +1;
			} else if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(this.namespaceUri)) {
				return -1;
			} else if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(o.namespaceUri)) {
				return +1;
			} else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(this.namespaceUri)) {
				return -1;
			} else if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(o.namespaceUri)) {
				return +1;
			} else {
				return this.namespaceUri.compareTo(o.namespaceUri);
			}
		}
	}

	static class StringTableEntries extends ArrayList<NamespaceUriEntry> {
		private static final long serialVersionUID = 1L;
		private final XSModel xsModel;

		public StringTableEntries(XSModel xsModel) {
			super();
			this.xsModel = xsModel;
			// init default entries
			this.add(new NamespaceUriEntry(XMLConstants.NULL_NS_URI));
			NamespaceUriEntry nsue1 = new NamespaceUriEntry(
					XMLConstants.XML_NS_URI);
			nsue1.localNames.add("base");
			nsue1.localNames.add("id");
			nsue1.localNames.add("lang");
			nsue1.localNames.add("space");
			this.add(nsue1);
			NamespaceUriEntry nsue2 = new NamespaceUriEntry(
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
			nsue2.localNames.add("nil");
			nsue2.localNames.add("type");
			this.add(nsue2);
			this.add(new NamespaceUriEntry(XMLConstants.W3C_XML_SCHEMA_NS_URI));
			// init entries
			this.initializeEntries();
			// sort entries
			Collections.sort(this);
			for (NamespaceUriEntry nsue : this) {
				Collections.sort(nsue.localNames);
			}
		}

		private void initializeEntries() {
			// namespaces
			StringList nss = xsModel.getNamespaces();
			for (int i = 0; i < nss.size(); i++) {
				String ns = (String) nss.get(i);
				checkNamespaceUriEntry(ns);
			}

			// simple types
			XSNamedMap nm = xsModel.getComponents(XSTypeDefinition.SIMPLE_TYPE);
			for (int k = 0; k < nm.size(); k++) {
				XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) nm
						.item(k);
				handleSimpleType(std);
			}

			// complex types
			nm = xsModel.getComponents(XSTypeDefinition.COMPLEX_TYPE);
			for (int k = 0; k < nm.size(); k++) {
				XSComplexTypeDefinition ctd = (XSComplexTypeDefinition) nm
						.item(k);
				this.handleComplexType(ctd);
			}

			// global elements
			nm = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
			for (int k = 0; k < nm.size(); k++) {
				XSElementDeclaration it = (XSElementDeclaration) nm.item(k);
				this.handleElementDeclaration(it);
			}

			// global attributes
			nm = xsModel.getComponents(XSConstants.ATTRIBUTE_DECLARATION);
			for (int k = 0; k < nm.size(); k++) {
				XSAttributeDeclaration it = (XSAttributeDeclaration) nm.item(k);
				this.handleAttributeDeclaration(it);
			}
		}

		private NamespaceUriEntry checkNamespaceUriEntry(String namespaceUri) {
			namespaceUri = namespaceUri == null ? XMLConstants.NULL_NS_URI
					: namespaceUri;
			for (NamespaceUriEntry nsue : this) {
				if (nsue.namespaceUri.equals(namespaceUri)) {
					return nsue;
				}
			}

			// not found
			NamespaceUriEntry nsue = new NamespaceUriEntry(namespaceUri);
			this.add(nsue);
			return nsue;
		}

		private void checkEntry(String namespaceUri, String localName) {
			NamespaceUriEntry nsue = checkNamespaceUriEntry(namespaceUri);

			assert (localName != null);
			if (!nsue.localNames.contains(localName)) {
				nsue.localNames.add(localName);
			}
		}

		private void handleAttributeDeclaration(XSAttributeDeclaration ad) {
			// element names
			checkEntry(ad.getNamespace(), ad.getName());
			// element type
			handleType(ad.getTypeDefinition());
		}

		private void handleElementDeclaration(XSElementDeclaration ed) {
			// element names
			checkEntry(ed.getNamespace(), ed.getName());
			// element type
			handleType(ed.getTypeDefinition());

			// substitution group
			XSNamedMap globalElements = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
			// Note: no global elements in XSD cause error
			if(globalElements!= null && globalElements.size() > 0) {
				XSObjectList subs = xsModel.getSubstitutionGroup(ed);
				if (subs != null) {
					for (int s = 0; s < subs.getLength(); s++) {
						XSElementDeclaration sub = (XSElementDeclaration) subs
								.get(s);
						// name
						checkEntry(sub.getNamespace(), sub.getName());
						// type
						handleType(sub.getTypeDefinition());
					}
				}				
			}

		}

		private void handleType(XSTypeDefinition td) {
			if (td.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
				this.handleComplexType((XSComplexTypeDefinition) td);
			} else {
				this.handleSimpleType((XSSimpleTypeDefinition) td);
			}
		}

		private void handleSimpleType(XSSimpleTypeDefinition std) {
			if (!std.getAnonymous()) {
				checkEntry(std.getNamespace(), std.getName());
			}
		}

		private void handleTerm(XSTerm t) {
			switch (t.getType()) {
			case XSConstants.ELEMENT_DECLARATION:
				XSElementDeclaration ed = (XSElementDeclaration) t;
				this.handleElementDeclaration(ed);
				break;
			case XSConstants.MODEL_GROUP:
				XSModelGroup mg = (XSModelGroup) t;
				XSObjectList particles = mg.getParticles();
				// handle particles
				for (int l = 0; l < particles.getLength(); l++) {
					XSParticle part = (XSParticle) particles.get(l);
					XSTerm tt = part.getTerm();
					this.handleTerm(tt);
				}
				break;
			case XSConstants.WILDCARD:
				this.handleWildcard((XSWildcard) t);
				break;
			default:
				throw new RuntimeException("Unexpected term");
			}
		}

		private void handleWildcard(XSWildcard wc) {
			if (wc != null) {
				switch (wc.getConstraintType()) {
				case XSWildcard.NSCONSTRAINT_LIST:
					// namespaces in the list are allowed
					StringList sl = wc.getNsConstraintList();
					for (int k = 0; k < sl.getLength(); k++) {
						String namespace = sl.item(k);
						this.checkNamespaceUriEntry(namespace);
					}
					break;
				default:
					// no namespace declared
				}
			}
		}

		Set<XSComplexTypeDefinition> cTypes = new HashSet<XSComplexTypeDefinition>();

		private void handleComplexType(XSComplexTypeDefinition ctd) {
			// stop processing?
			if (cTypes.contains(ctd)) {
				// abort
				return;
			}
			cTypes.add(ctd);

			// complex type names
			if (!ctd.getAnonymous()) {
				checkEntry(ctd.getNamespace(), ctd.getName());
			}

			// attributes
			XSObjectList attributes = ctd.getAttributeUses();
			for (int i = 0; i < attributes.getLength(); i++) {
				XSAttributeUse at = (XSAttributeUse) attributes.get(i);
				XSAttributeDeclaration atd = at.getAttrDeclaration();
				checkEntry(atd.getNamespace(), atd.getName());
			}

			// attribute wildcard
			XSWildcard attributeWC = ctd.getAttributeWildcard();
			this.handleWildcard(attributeWC);

			// term
			XSParticle particle = ctd.getParticle();
			if (particle != null) {
				XSTerm t = particle.getTerm();
				this.handleTerm(t);
			}

		}
	}

	public SchemaInformedGrammars toGrammars() throws EXIException {
		if (xsModel == null || schemaParsingErrors.size() > 0) {
			StringBuffer sb = new StringBuffer(
					"Problem occured while building XML Schema Model (XSModel)!");

			for (int i = 0; i < schemaParsingErrors.size(); i++) {
				sb.append("\n. " + schemaParsingErrors.get(i));
			}

			throw new EXIException(sb.toString());
		}

		// grammar string entries
		StringTableEntries ste = new StringTableEntries(xsModel);
		// System.out.println("UriSize: " + ste.size());
		grammarUriContexts = new GrammarUriContext[ste.size()];
		int qNameID = 0;
		for (int i = 0; i < ste.size(); i++) {
			NamespaceUriEntry nsue = ste.get(i);
			String namespaceUri = nsue.namespaceUri;
			// prefixes
			String[] prefixes;
			if (XMLConstants.NULL_NS_URI.equals(namespaceUri)) {
				prefixes = Constants.PREFIXES_EMPTY;
			} else if (XMLConstants.XML_NS_URI.equals(namespaceUri)) {
				prefixes = Constants.PREFIXES_XML;
			} else if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI
					.equals(namespaceUri)) {
				prefixes = Constants.PREFIXES_XSI;
			} else {
				prefixes = GrammarUriContext.EMPTY_PREFIXES;
			}

			// localName contexts
			List<String> localNames = nsue.localNames;
			QNameContext[] grammarLocalNames = new QNameContext[localNames
					.size()];

			for (int k = 0; k < localNames.size(); k++) {
				String localName = localNames.get(k);
				// add entry
				QName qname = new QName(namespaceUri, localName);
				grammarLocalNames[k] = new QNameContext(i, k, qname, qNameID++); // ,
																					// grammarGlobalElement,
																					// grammarGlobalAttribute);
			}

			// create grammar uri context
			grammarUriContexts[i] = new GrammarUriContext(i, namespaceUri,
					grammarLocalNames, prefixes);

		}

		// updates global elements, attributes and types
		for (GrammarUriContext guc : grammarUriContexts) {
			for (int k = 0; k < guc.getNumberOfQNames(); k++) {
				QNameContext qnc = guc.getQNameContext(k);
				String localName = qnc.getLocalName();
				String namespace = guc.getNamespaceUri();

				// global element
				XSElementDeclaration globalElementDecl = xsModel
						.getElementDeclaration(localName, namespace);
				if (globalElementDecl != null) {
					StartElement grammarGlobalElement = translatElementDeclarationToFSA(globalElementDecl);
					qnc.setGlobalStartElement(grammarGlobalElement);
				}

				// global attribute
				XSAttributeDeclaration globalAttributeDecl = xsModel
						.getAttributeDeclaration(localName, namespace);
				if (globalAttributeDecl != null) {
					Attribute grammarGlobalAttribute = this
							.getAttribute(globalAttributeDecl);
					qnc.setGlobalAttribute(grammarGlobalAttribute);
				}

				// global types
				XSTypeDefinition typeDef = xsModel.getTypeDefinition(localName,
						namespace);
				if (typeDef != null) {
					SchemaInformedFirstStartTagGrammar fstr = this
							.translateTypeDefinitionToFSA(typeDef);
					qnc.setTypeGrammar(fstr);
					

//					// simple datatype
//					if (typeDef.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
//						XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) typeDef;
//						//type_i = translateSimpleTypeDefinitionToFSA(std);
//						Datatype dt = getDatatype(std);
//						
//						qnc.setSimpleDatatype(dt);
//						//System.out.println(qnc.getQName() + " --> " +  dt);
//					}
				}
				

//				// (direct) simple sub-types vs. baseType
//				if (typeDef != null
//						&& typeDef.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE
//						&& !typeDef.getAnonymous()) {
//
//					XSTypeDefinition baseType = getBaseType(typeDef);
//					while (baseType != null && baseType.getAnonymous()) {
//						baseType = getBaseType(baseType);
//					}
//
////					// subtypes
////					if (baseType == null) {
////						// http://www.w3.org/2001/XMLSchema,anySimpleType
////					} else {
////						// Note: according to EXI errata enumerations are not
////						// handled by simple DTR maps
////						// There are only other enum types in hierarchy
////						Datatype dtBase = this
////								.getDatatype((XSSimpleTypeDefinition) baseType);
////						Datatype dt = this
////								.getDatatype((XSSimpleTypeDefinition) typeDef);
////
////						if (dt.getBuiltInType() == BuiltInType.ENUMERATION
////								&& dtBase.getBuiltInType() != BuiltInType.ENUMERATION) {
////							// not added as sub-type
////						} else if (XMLConstants.W3C_XML_SCHEMA_NS_URI
////								.equals(baseType.getNamespace())
////								&& baseType.getName() == null) {
////							// e.g., xsd:ENTITIES
////						} else {
////							// Set simple baseType
////							QNameContext base = getQNameContext(
////									baseType.getNamespace(),
////									baseType.getName(), grammarUriContexts);
////							assert(base != null);
////							qnc.setSimpleBaseType(base);
////							
////							qnc.setSimpleBaseDatatype(dtBase);
////							
////							
//////							// TODO subtypes needed anymore?!
//////							// List<QName> sub = subtypes.get(baseTypeQName);
//////							// System.out.println(baseType);
//////							List<QNameContext> subTypes = base
//////									.getSimpleTypeSubtypes();
//////							// List<QNameContext> subTypes =
//////							// qnc.getSimpleTypeSubtypes();
//////							if (subTypes == null) {
//////								subTypes = new ArrayList<QNameContext>();
//////								// qnc.setSimpleTypeSubtypes(subTypes);
//////								base.setSimpleTypeSubtypes(subTypes);
//////							}
//////							// QName baseTypeQName = getValueType(baseType);
//////							// subTypes.add(base);
//////							subTypes.add(qnc);
////						}
////					}
//				}
			}
		}

		// initialize grammars --> global element)
		List<StartElement> globalElements = initGrammars();

		// schema declared elements --> fragment grammars
		List<StartElement> fragmentElements = getFragmentElements();

		// sort both lists (declared & global elements)
		Collections.sort(globalElements, lexSort);
		Collections.sort(fragmentElements, lexSort);

		/*
		 * Global elements declared in the schema. G 0, G 1, ... G n-1 represent
		 * all the qnames of global elements sorted lexicographically, first by
		 * localName, then by uri. http://www.w3.org/TR/exi/#informedDocGrammars
		 */
		// DocEnd rule
		DocEnd builtInDocEndGrammar = new DocEnd("DocEnd");
		builtInDocEndGrammar.addTerminalProduction(new EndDocument());
		// DocContent rule
		SchemaInformedGrammar builtInDocContentGrammar = new SchemaInformedDocContent("DocContent");
		builtInDocContentGrammar.addProduction(START_ELEMENT_GENERIC, builtInDocEndGrammar);
		
		// DocContent rule & add global elements (sorted)
		for (StartElement globalElement : globalElements) {
			builtInDocContentGrammar.addProduction(globalElement,
					builtInDocEndGrammar);
		}
		// Document rule
		Document documentGrammar = new Document("Document");
		documentGrammar.addProduction(new StartDocument(), builtInDocContentGrammar);

		/*
		 * FragmentContent grammar represents the number of unique element
		 * qnames declared in the schema sorted lexicographically, first by
		 * localName, then by uri.
		 * http://www.w3.org/TR/exi/#informedElementFragGrammar
		 */
		// Fragment Content
		SchemaInformedGrammar builtInFragmentContentGrammar = new SchemaInformedFragmentContent(
				"FragmentContent");
		// SE(*) --> FragmentContent
		builtInFragmentContentGrammar.addProduction(START_ELEMENT_GENERIC, builtInFragmentContentGrammar);
		// ED
		builtInFragmentContentGrammar.addTerminalProduction(new EndDocument());
		
		
		for (StartElement fragmentElement : fragmentElements) {
			builtInFragmentContentGrammar.addProduction(fragmentElement,
					builtInFragmentContentGrammar);
		}
		// Fragment
		Fragment fragmentGrammar = new Fragment( "Fragment");
		fragmentGrammar.addProduction(new StartDocument(), builtInFragmentContentGrammar);

		/*
		 * create schema informed grammar
		 */
		GrammarContext grammarContext = new GrammarContext(grammarUriContexts,
				qNameID);
		SchemaInformedGrammars sig = new SchemaInformedGrammars(grammarContext,
				documentGrammar, fragmentGrammar);

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

			// QName name = new QName(td.getNamespace(), td.getName());
			SchemaInformedFirstStartTagGrammar sir = translateTypeDefinitionToFSA(td);
			// types cannot be nillable nor typable (only elements!)
			assert (!sir.isNillable());
			assert (!sir.isTypeCastable());
		}

		// global elements
		XSNamedMap xsGlobalElements = xsModel
				.getComponents(XSConstants.ELEMENT_DECLARATION);
		for (int i = 0; i < xsGlobalElements.getLength(); i++) {
			XSElementDeclaration globalElementDecl = (XSElementDeclaration) xsGlobalElements
					.item(i);
			// create rule for global element (do not have scope)
			StartElement seGlobalElement = translatElementDeclarationToFSA(globalElementDecl);

			// collect global elements (for DocContent)
			globalElements.add(seGlobalElement);
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
			XSSimpleTypeDefinition std = attrDecl.getTypeDefinition();
			// QName valueType = getValueType(std);
			// create new Attribute event
			QName qname = new QName(attrDecl.getNamespace(), attrDecl.getName());
			at = createAttribute(qname, getDatatype(std)); // new
																		// Attribute(qname,
																		// valueType,
																		// getDatatype(std));
			attributePool.put(attrDecl, at);
		}

		return at;
	}

	protected XSTypeDefinition getBaseType(XSTypeDefinition td) {
		// avoid Xerces bug
		// Xerces reports integer as base-type for negativeInteger instead of
		// nonPositiveInteger
		if (td.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
			if ("negativeInteger".equals(td.getName())
					&& XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(td
							.getNamespace())) {
				XSTypeDefinition td2 = xsModel.getTypeDefinition(
						"nonPositiveInteger",
						XMLConstants.W3C_XML_SCHEMA_NS_URI);
				return td2;
			} else {
				return td.getBaseType();
			}
		} else {
			return td.getBaseType();
		}
	}

	protected QName getValueType(XSTypeDefinition typeDefinition) {

		while (typeDefinition.getAnonymous()) {
			typeDefinition = getBaseType(typeDefinition);
		}

		QName valueType = new QName(typeDefinition.getNamespace(),
				typeDefinition.getName());
		return valueType;
	}

	protected SchemaInformedStartTagGrammar handleAttributes(
			SchemaInformedGrammar ruleContent, SchemaInformedGrammar ruleContent2,
			XSObjectList attributes, XSWildcard attributeWC)
			throws EXIException {

		// Attribute Uses
		// http://www.w3.org/TR/exi/#attributeUses

		SchemaInformedStartTagGrammar ruleStart = new SchemaInformedStartTag(
				ruleContent2);
		// join top level events
		for (int i = 0; i < ruleContent.getNumberOfEvents(); i++) {
			Production ei = ruleContent.getProduction(i);
			ruleStart.addProduction(ei.getEvent(), ei.getNextGrammar());
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

				SchemaInformedStartTagGrammar newCurrent = new SchemaInformedStartTag(
						ruleContent2);
				newCurrent.addProduction(at, ruleStart);

				// Attribute Wildcard
				// http://www.w3.org/TR/exi/#complexTypeGrammars
				if (attributeWC != null) {
					handleAttributeWildCard(attributeWC, newCurrent);
				}

				// required attribute ?
				if (!attrUse.getRequired()) {
					// optional --> join top level events
					for (int k = 0; k < ruleStart.getNumberOfEvents(); k++) {
						Production ei = ruleStart.getProduction(k);
						if (ei.getEvent().isEventType(EventType.ATTRIBUTE_GENERIC)
								|| ei.getEvent().isEventType(EventType.ATTRIBUTE_NS)) {
							// AT(*) & AT(uri:*) wilcards added before
						} else {
							newCurrent.addProduction(ei.getEvent(), ei.getNextGrammar());
						}
					}
				}
				ruleStart = newCurrent;
			}
		}

		return ruleStart;

	}

	protected void handleAttributeWildCard(XSWildcard attributeWC,
			SchemaInformedGrammar rule) {

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
			rule.addProduction(ATTRIBUTE_GENERIC, rule);
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
				// rule.addRule(new AttributeNS(namespace), rule);
				rule.addProduction(createAttributeNS(namespace), rule);
				// add attribute wildcard URI
				addNamespaceStringEntry(namespace);
				// if (!atWildcardNamespaces.contains(namespace)) {
				// atWildcardNamespaces.add(namespace);
				// }
			}
		}
	}

	protected SchemaInformedFirstStartTagGrammar getTypeGrammar(
			String namespaceURI, String name) {
		QName en = new QName(namespaceURI, name);
		return grammarTypes.get(en);
	}

	protected StartElement translatElementDeclarationToFSA(
			XSElementDeclaration xsElementDeclaration) throws EXIException {

		StartElement se = null;

		// handle element recursion
		if (elementPool.containsKey(xsElementDeclaration)) {
			return elementPool.get(xsElementDeclaration);
		} else {
			String namespaceURI = xsElementDeclaration.getNamespace();
			String localName = xsElementDeclaration.getName();
			javax.xml.namespace.QName qname = new javax.xml.namespace.QName(
					namespaceURI, localName);
			se = createStartElement(qname); // new StartElement(qname);
			addLocalNameStringEntry(namespaceURI, localName);
			elementPool.put(xsElementDeclaration, se);
		}

		// add local name entry for string table pre-population
		addLocalNameStringEntry(xsElementDeclaration.getNamespace(),
				xsElementDeclaration.getName());

		// type definition --> type grammar
		XSTypeDefinition td = xsElementDeclaration.getTypeDefinition();
		SchemaInformedFirstStartTagGrammar type = translateTypeDefinitionToFSA(td);

		if (type.isNillable() || type.isTypeCastable()) {
			throw new RuntimeException("Type grammar is nillable or typable, "
					+ type + "\t" + td);
		}

		// create element grammar
		if (td.getAnonymous()) {
			// can use anonymous grammar so set nillable and typable
			type.setNillable(xsElementDeclaration.getNillable());
			type.setTypeCastable(isTypeCastable(td));
			se.setGrammar(type);
		} else {
			// ONLY if element is neither nillable nor typable existing grammar
			// can be used
			if (xsElementDeclaration.getNillable() || isTypeCastable(td)) {
				// new top grammar
				SchemaInformedFirstStartTagGrammar element = (SchemaInformedFirstStartTagGrammar) type
						.duplicate();
				element.setNillable(xsElementDeclaration.getNillable());
				element.setTypeCastable(isTypeCastable(td));
				se.setGrammar(element);
			} else {
				// same grammar
				se.setGrammar(type);
			}
		}

		return se;
	}

//	// http://www.w3.org/TR/exi/#anyTypeGrammar
//	public static SchemaInformedFirstStartTagGrammar getUrTypeRule() {
//
//		SchemaInformedGrammar urType1 = new SchemaInformedElement();
//		SchemaInformedFirstStartTag urType0 = new SchemaInformedFirstStartTag(
//				urType1);
//		urType0.setLabel("ur-type");
//
//		// Type ur-type, 0 :
//		// AT (*) Type ur-type, 0
//		// SE(*) Type ur-type, 1
//		// EE
//		// CH Type ur-type, 1
//		urType0.addProduction(ATTRIBUTE_GENERIC, urType0);
//		urType0.addProduction(START_ELEMENT_GENERIC, urType1);
//		urType0.addTerminalProduction(END_ELEMENT);
//		urType0.addProduction(CHARACTERS_GENERIC, urType1);
//		// // anyType is castable
//		// urType0.setTypeCastable(true);
//		// // types are NOT nillable
//		// urType0.setNillable(false);
//
//		// Type ur-type, 1 :
//		// SE(*) Type ur-type, 1
//		// EE
//		// CH Type ur-type, 1
//		urType1.addProduction(START_ELEMENT_GENERIC, urType1);
//		urType1.addTerminalProduction(END_ELEMENT);
//		urType1.addProduction(CHARACTERS_GENERIC, urType1);
//
//		// empty types
//		SchemaInformedGrammar emptyUrType1 = new SchemaInformedElement();
//		SchemaInformedFirstStartTagGrammar emptyUrType0 = new SchemaInformedFirstStartTag(
//				emptyUrType1);
//		// set type empty
//		urType0.setTypeEmpty(emptyUrType0);
//
//		// TypeEmpty ur-type, 0 :
//		// AT (*) TypeEmpty ur-type, 0
//		// EE
//		emptyUrType0.addProduction(ATTRIBUTE_GENERIC, emptyUrType0);
//		emptyUrType0.addTerminalProduction(END_ELEMENT);
//
//		// // anyType is castable
//		// emptyUrType0.setTypeCastable(true);
//
//		// TypeEmpty ur-type, 1 :
//		// EE
//		emptyUrType1.addTerminalProduction(END_ELEMENT);
//
//		return urType0;
//	}

	protected boolean isTypeCastable(XSTypeDefinition td) {

		boolean isTypeCastable = false;

		// has named sub-types
		XSNamedMap types = this.xsModel
				.getComponents(XSConstants.TYPE_DEFINITION);
		for (int i = 0; i < types.getLength(); i++) {
			XSTypeDefinition td2 = (XSTypeDefinition) types.item(i);

			// if (td.equals(td2.getBaseType())) {
			if (td.equals(getBaseType(td2))) {
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

	/**
	 * Given an XML Schema type definition T i , two type grammars are created,
	 * which are denoted by Type i and TypeEmpty i . Type i is a grammar that
	 * fully reflects the type definition of T i , whereas TypeEmpty i is a
	 * grammar that accepts only the attribute uses and attribute wildcards of T
	 * i , if any.
	 * 
	 * @param td
	 * @return schema-informed first start tag grammar
	 * @throws EXIException
	 */
	protected SchemaInformedFirstStartTagGrammar translateTypeDefinitionToFSA(
			XSTypeDefinition td) throws EXIException {
		SchemaInformedFirstStartTagGrammar type_i = null;
		QName typeName = null;

		// type rule already created?
		if (!td.getAnonymous()) {
			typeName = new QName(td.getNamespace(), td.getName());
			if ((type_i = grammarTypes.get(typeName)) != null) {
				return type_i;
			}
		}

		// simple vs. complex type handling
		if (td.getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE) {
			XSComplexTypeDefinition ctd = (XSComplexTypeDefinition) td;
			type_i = translateComplexTypeDefinitionToFSA(ctd);
		} else {
			assert (td.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE);
			XSSimpleTypeDefinition std = (XSSimpleTypeDefinition) td;
			type_i = translateSimpleTypeDefinitionToFSA(std);
		}

		if (!td.getAnonymous()) {
			// add to localName table for string table pre-population
			addLocalNameStringEntry(td.getNamespace(), td.getName());
			type_i.setTypeName(typeName);
			grammarTypes.put(typeName, type_i);
		}

		return type_i;
	}

	/**
	 * Given an XML Schema type definition T i , two type grammars are created,
	 * which are denoted by Type i and TypeEmpty i . Type i is a grammar that
	 * fully reflects the type definition of T i , whereas TypeEmpty i is a
	 * grammar that accepts only the attribute uses and attribute wildcards of T
	 * i , if any.
	 * 
	 * @param ctd
	 * @return schema-informed first start tag grammar
	 * @throws EXIException
	 */
	protected SchemaInformedFirstStartTagGrammar translateComplexTypeDefinitionToFSA(
			XSComplexTypeDefinition ctd) throws EXIException {

//		/*
//		 * anyType is special
//		 */
//		if (Constants.XSD_ANY_TYPE.equals(ctd.getName())
//				&& XMLConstants.W3C_XML_SCHEMA_NS_URI
//						.equals(ctd.getNamespace())) {
//			// ur-type
//			SchemaInformedFirstStartTagGrammar urType = getUrTypeRule();
//			return urType;
//		}

		/*
		 * Rule Content
		 */
		SchemaInformedGrammar ruleContent = null;

		switch (ctd.getContentType()) {
		case XSComplexTypeDefinition.CONTENTTYPE_EMPTY:
			// Represents an empty content type.
			// A content type with the distinguished value empty validates
			// elements
			// with no character or element information item children.
			// (attributes only, no content allowed)
			ruleContent = new SchemaInformedElement();
			ruleContent.addTerminalProduction(END_ELEMENT);
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

		// create copy of Element_i_content --> Element_i_content_2
		// (used for content schema-deviations in start-tags, direct
		// jumps)
		SchemaInformedGrammar ruleContent2 = ruleContent.duplicate();

		// attributes
		XSObjectList attributes = ctd.getAttributeUses();
		XSWildcard attributeWC = ctd.getAttributeWildcard();

		// boolean isTypeCastable = isTypeCastable(ctd);

		// type_i (start tag)
		SchemaInformedStartTagGrammar sistr = handleAttributes(ruleContent,
				ruleContent2, attributes, attributeWC);
		SchemaInformedFirstStartTagGrammar type_i = new SchemaInformedFirstStartTag(
				sistr);
		// type_i.setTypeCastable(isTypeCastable);

		// typeEmpty_i
		SchemaInformedGrammar ruleEnd = new SchemaInformedElement();
		ruleEnd.addTerminalProduction(END_ELEMENT);
		SchemaInformedFirstStartTagGrammar typeEmpty_i = new SchemaInformedFirstStartTag(
				handleAttributes(ruleEnd, ruleEnd, attributes, attributeWC));
		// typeEmpty_i.setTypeCastable(isTypeCastable);
		type_i.setTypeEmpty(typeEmpty_i);

		return type_i;
		// return ruleContent;
	}

	/**
	 * Given an XML Schema type definition T i , two type grammars are created,
	 * which are denoted by Type i and TypeEmpty i . Type i is a grammar that
	 * fully reflects the type definition of T i , whereas TypeEmpty i is a
	 * grammar that accepts only the attribute uses and attribute wildcards of T
	 * i , if any.
	 * 
	 * @param std
	 * @return schema-informed first start tag grammar
	 * @throws EXIException
	 */
	// protected SchemaInformedElement translateSimpleTypeDefinitionToFSA(
	protected SchemaInformedFirstStartTagGrammar translateSimpleTypeDefinitionToFSA(
			XSSimpleTypeDefinition std) throws EXIException {

		/*
		 * Simple content
		 */
		// QName valueType = this.getValueType(std);
		Characters chSchemaValid = new Characters(getDatatype(std)); // valueType, 

		SchemaInformedGrammar simpleContentEnd = SIMPLE_END_ELEMENT_RULE;

		SchemaInformedElement simpleContent = new SchemaInformedElement();
		simpleContent.addProduction(chSchemaValid, simpleContentEnd);

		// Type i
		SchemaInformedFirstStartTagGrammar type_i = new SchemaInformedFirstStartTag(
				handleAttributes(simpleContent, simpleContent, null, null));

		type_i.setTypeEmpty(SIMPLE_END_ELEMENT_EMPTY_RULE);

		return type_i;
	}


	public Datatype getDatatype(XSSimpleTypeDefinition std) {
		Datatype datatype = datatypePool.get(std);
		
		if(datatype!= null) {
			return datatype;
		}

		// used for dtr map
		QName schemaType = getSchemaType(std);
		QNameContext qncSchemaType = getQNameContext(schemaType.getNamespaceURI(), schemaType.getLocalPart(), this.grammarUriContexts);
		
		// is enumeration ?
		if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_ENUMERATION)) {
			// datatype = getDatatypeOfEnumeration ( std );
			XSObjectList facetList = std.getMultiValueFacets();
			for (int i = 0; i < facetList.getLength(); i++) {
				XSObject facet = facetList.item(i);
				if (facet.getType() == XSConstants.MULTIVALUE_FACET) {
					XSMultiValueFacet enumer = (XSMultiValueFacet) facet;
					if (enumer.getFacetKind() == XSSimpleTypeDefinition.FACET_ENUMERATION) {
						StringList enumList = enumer.getLexicalFacetValues();
						// avoid enumeration of enumeration
						XSSimpleTypeDefinition stdEnum = (XSSimpleTypeDefinition) std.getBaseType();
						while ( this.getDatatype(stdEnum).getBuiltInType() == BuiltInType.ENUMERATION ) {
							stdEnum = (XSSimpleTypeDefinition) stdEnum.getBaseType();
						}
						/*
						 * Exceptions are for schema types derived from others
						 * by union and their subtypes, QName or Notation and
						 * types derived therefrom by restriction. The values of
						 * such types are processed by their respective built-in
						 * EXI datatype representations instead of being
						 * represented as enumerations.
						 */
						if (stdEnum.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION) {
							datatype = new StringDatatype(qncSchemaType, true);
						} else if (BuiltIn.XSD_QNAME
								.equals(getSchemaType(stdEnum))
								|| BuiltIn.XSD_NOTATION
										.equals(getSchemaType(stdEnum))) {
							datatype = new StringDatatype(qncSchemaType);
						} else {
							Datatype dtEnumValues = getDatatype(stdEnum);
							Value[] values = new Value[enumList.getLength()];

							BuiltInType enumBIT = dtEnumValues.getBuiltInType();
							
							// EXI errata item
							if(enumBIT == BuiltInType.LIST) {
								ListDatatype listDT = (ListDatatype) dtEnumValues;
								Datatype dtL = listDT.getListDatatype();
								datatype = new ListDatatype(dtL, qncSchemaType);
							} else {
								for (int k = 0; k < enumList.getLength(); k++) {
									String tok = enumList.item(k);
									Value enumValue;

									switch (enumBIT) {
									/* Binary */
									case BINARY_BASE64:
										enumValue = BinaryBase64Value.parse(tok);
										break;
									case BINARY_HEX:
										enumValue = BinaryHexValue.parse(tok);
										break;
									/* Boolean */
									case BOOLEAN:
										// case BOOLEAN_PATTERN:
										enumValue = BooleanValue.parse(tok);
										break;
									/* Decimal */
									case DECIMAL:
										enumValue = DecimalValue.parse(tok);
										break;
									/* Float */
									case FLOAT:
										enumValue = FloatValue.parse(tok);
										break;
									/* int */
									case NBIT_UNSIGNED_INTEGER:
									case UNSIGNED_INTEGER:
									case INTEGER:
										enumValue = IntegerValue.parse(tok);
										break;
									/* Datetime */
									case DATETIME:
										DatetimeDatatype datetimeDT = (DatetimeDatatype) dtEnumValues;
										enumValue = DateTimeValue.parse(tok,
												datetimeDT.getDatetimeType());
										break;
									/* List*/
									case LIST:
										// forbidden with errata item
										throw new RuntimeException("Enumerated values not possible as part of a list");
										// ListDatatype listDT = (ListDatatype) dtEnumValues;
										// enumValue = ListValue.parse(tok, listDT.getListDatatype());
										// break;
									default:
										enumValue = new StringValue(tok); // String
										enumBIT = BuiltInType.STRING; // override
									}

									if (enumValue == null) {
										throw new RuntimeException(
												"Enum value cannot be parsed properly, "
														+ enumValue + "', "
														+ stdEnum);
									}

									boolean valid = dtEnumValues.isValid(enumValue);
									if (!valid) {
										throw new RuntimeException(
												"No valid enumeration value '"
														+ enumValue + "', "
														+ stdEnum);
									}
									values[k] = enumValue;
								}

								datatype = new EnumerationDatatype(values, dtEnumValues,
										qncSchemaType);
							}
							



						}
					}
				}
			}
		// is list ?
		} else if (std.getVariety() == XSSimpleTypeDefinition.VARIETY_LIST) {
			XSSimpleTypeDefinition listSTD = std.getItemType();

			Datatype dtList = getDatatype(listSTD);

			datatype = new ListDatatype(dtList, qncSchemaType);
		// is union ?
		} else if (std.getVariety() == XSSimpleTypeDefinition.VARIETY_UNION) {
			datatype = new StringDatatype(qncSchemaType, true);
		} else {
			datatype = getDatatypeOfType(std, schemaType);
		}
		
		// base datatype
		XSTypeDefinition baseType = this.getBaseType(std);
		if(baseType != null && baseType.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
			XSSimpleTypeDefinition stdBaseType = (XSSimpleTypeDefinition)baseType;
			datatype.setBaseDatatype(this.getDatatype(stdBaseType));
		}
		
		datatypePool.put(std, datatype);

		return datatype;
	}

	private QName getXMLSchemaDatatype(XSSimpleTypeDefinition std) {
		// primitive
		QName primitive = getPrimitive(std);

		QName exiDatatypeID;

		if (primitive.equals(BuiltIn.XSD_DECIMAL)) {
			// check whether on the "way up" (nonNegative) integer appears -->
			// (Unsigned)Integer
			XSTypeDefinition xmlSchemaType = std;

			while (xmlSchemaType != null
					&& !(xmlSchemaType.getName() != null && (BuiltIn.XSD_INTEGER
							.equals(getName(xmlSchemaType)) || BuiltIn.XSD_NON_NEGATIVE_INTEGER
							.equals(getName(xmlSchemaType))))) {
				xmlSchemaType = xmlSchemaType.getBaseType();
			}

			if (xmlSchemaType == null) {
				// xsd:decimal
				exiDatatypeID = BuiltIn.XSD_DECIMAL;
			} else {
				// xsd:integer
				exiDatatypeID = BuiltIn.XSD_INTEGER;
			}
		} else {
			exiDatatypeID = getBuiltInOfPrimitiveMapping(primitive);
		}

		return exiDatatypeID;
	}

	private QName getSchemaType(XSSimpleTypeDefinition std) {
		// used for dtr map
		// Note: if type is anonymous the "closest" type is used as schema-type
		String name, uri;
		if (std.getAnonymous()) {
			XSTypeDefinition baseType = std;
			while ((baseType = baseType.getBaseType()).getAnonymous()) {
			}
			uri = baseType.getNamespace();
			name = baseType.getName();
		} else {
			uri = std.getNamespace();
			name = std.getName();
		}

		return new QName(uri, name);
	}

	private Datatype getIntegerDatatype(XSSimpleTypeDefinition std,
			QName schemaType) {
		QNameContext qncSchemaType = getQNameContext(schemaType.getNamespaceURI(), schemaType.getLocalPart(), this.grammarUriContexts);
		/*
		 * detect base integer type (e.g. int, long, BigInteger)
		 */
		// walk up the hierarchy till we find xsd simple integer types
		XSTypeDefinition xsdSTD = std;
		while (!XMLConstants.W3C_XML_SCHEMA_NS_URI
				.equals(xsdSTD.getNamespace())) {
			xsdSTD = xsdSTD.getBaseType();
		}

		// set appropriate integer type
		IntegerType intType;

		// big
		if (xsdSTD.getName().equals("integer")
				|| xsdSTD.getName().equals("nonPositiveInteger")
				|| xsdSTD.getName().equals("negativeInteger")) {
			intType = IntegerType.INTEGER_BIG;
		}
		// unsigned big
		else if (xsdSTD.getName().equals("nonNegativeInteger")
				|| xsdSTD.getName().equals("positiveInteger")) {
			intType = IntegerType.UNSIGNED_INTEGER_BIG;
		}
		// int 64
		else if (xsdSTD.getName().equals("long")) {
			intType = IntegerType.INTEGER_64;
		}
		// unsigned int 64
		else if (xsdSTD.getName().equals("unsignedLong")) {
			intType = IntegerType.UNSIGNED_INTEGER_64;
		}
		// int 32
		else if (xsdSTD.getName().equals("int")) {
			intType = IntegerType.INTEGER_32;
		}
		// unsigned int 32
		else if (xsdSTD.getName().equals("unsignedInt")) {
			intType = IntegerType.UNSIGNED_INTEGER_32;
		}
		// int 16
		else if (xsdSTD.getName().equals("short")) {
			intType = IntegerType.INTEGER_16;
		}
		// unsigned int 16
		else if (xsdSTD.getName().equals("unsignedShort")) {
			intType = IntegerType.UNSIGNED_INTEGER_16;
		}
		// int 8
		else if (xsdSTD.getName().equals("byte")) {
			intType = IntegerType.INTEGER_8;
		}
		// unsigned int 8
		else if (xsdSTD.getName().equals("unsignedByte")) {
			intType = IntegerType.UNSIGNED_INTEGER_8;
		}
		// ERROR ??
		else {
			throw new RuntimeException("Unexpected Integer Type: " + xsdSTD);
		}

		/*
		 * identify lower & upper bound
		 */
		BigInteger min = new BigInteger(
				"-9999999999999999999999999999999999999999");
		BigInteger max = new BigInteger(
				"9999999999999999999999999999999999999999");
		// minimum
		if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_MININCLUSIVE)) {
			String sMinInclusive = std
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MININCLUSIVE);
			min = min.max(new BigInteger(sMinInclusive));
		}
		if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_MINEXCLUSIVE)) {
			String sMinExclusive = std
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MINEXCLUSIVE);
			min = min.max((new BigInteger(sMinExclusive)).add(BigInteger.ONE));
		}
		// maximum
		if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_MAXINCLUSIVE)) {
			String sMaxInclusive = std
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MAXINCLUSIVE);
			max = max.min(new BigInteger(sMaxInclusive));
		}
		if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE)) {
			String sMaxExclusive = std
					.getLexicalFacetValue(XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE);
			max = max.min((new BigInteger(sMaxExclusive))
					.subtract(BigInteger.ONE));
		}
		// ( max >= min)
		assert (max.compareTo(min) >= 0);

		/*
		 * calculate bounded range;
		 */
		// max - min + 1 --- e.g., [-1 .. -1] = 3 OR [2 .. 4] = 3
		BigInteger boundedRange = max.subtract(min).add(BigInteger.ONE);

		/*
		 * Set-up appropriate datatype
		 */
		Datatype datatype;

		if (boundedRange.compareTo(BigInteger
				.valueOf(MAX_BOUNDED_NBIT_INTEGER_RANGE)) <= 0) {
			/*
			 * When the bounded range of integer is 4095 or smaller as
			 * determined by the values of minInclusiveXS2, minExclusiveXS2,
			 * maxInclusiveXS2 and maxExclusiveXS2 facets, use n-bit Unsigned
			 * Integer representation.
			 */
			switch (intType) {
			case UNSIGNED_INTEGER_BIG:
			case UNSIGNED_INTEGER_64:
			case UNSIGNED_INTEGER_32:
			case UNSIGNED_INTEGER_16:
			case UNSIGNED_INTEGER_8:
			case INTEGER_BIG:
			case INTEGER_64:
			case INTEGER_32:
			case INTEGER_16:
			case INTEGER_8:
				datatype = new NBitUnsignedIntegerDatatype(
						IntegerValue.valueOf(min), IntegerValue.valueOf(max),
						qncSchemaType);
				break;
			default:
				throw new RuntimeException("Unexpected n-Bit Integer Type: "
						+ intType);
			}
		} else if (min.signum() >= 0) {
			/*
			 * Otherwise, when the integer satisfies one of the followings, use
			 * Unsigned Integer representation.
			 * 
			 * + It is nonNegativeInteger. + Either minInclusiveXS2 facet is
			 * specified with a value equal to or greater than 0, or
			 * minExclusiveXS2 facet is specified with a value equal to or
			 * greater than -1.
			 */

			/*
			 * update int-type according to facet restrictions, val >= 0
			 */
			switch (intType) {
			case INTEGER_BIG:
				intType = IntegerType.UNSIGNED_INTEGER_BIG;
				break;
			case INTEGER_64:
				intType = IntegerType.UNSIGNED_INTEGER_64;
				break;
			case INTEGER_32:
				intType = IntegerType.UNSIGNED_INTEGER_32;
				break;
			case INTEGER_16:
				intType = IntegerType.UNSIGNED_INTEGER_16;
				break;
			case INTEGER_8:
				intType = IntegerType.UNSIGNED_INTEGER_8;
				break;
			default:
				// no action	
			}

			switch (intType) {
			case UNSIGNED_INTEGER_BIG:
			case UNSIGNED_INTEGER_64:
			case UNSIGNED_INTEGER_32:
			case UNSIGNED_INTEGER_16:
			case UNSIGNED_INTEGER_8:
				datatype = new UnsignedIntegerDatatype(qncSchemaType);
				break;
			default:
				throw new RuntimeException("Unexpected Unsigned Integer Type: "
						+ intType);
			}
		} else {
			/*
			 * Otherwise, use Integer representation.
			 */
			switch (intType) {
			case INTEGER_BIG:
			case INTEGER_64:
			case INTEGER_32:
			case INTEGER_16:
			case INTEGER_8:
				datatype = new IntegerDatatype(qncSchemaType);
				break;
			default:
				throw new RuntimeException("Unexpected Integer Type: "
						+ intType);
			}
		}

		return datatype;
	}

	private QName getName(XSTypeDefinition type) {
		return new QName(type.getNamespace(), type.getName());
	}

	private Datatype getDatatypeOfType(XSSimpleTypeDefinition std,
			final QName schemaType) {
		final QNameContext qncSchemaType = getQNameContext(schemaType.getNamespaceURI(), schemaType.getLocalPart(), this.grammarUriContexts);
		
		//
		Datatype datatype;
		QName schemaDatatype = getXMLSchemaDatatype(std);

		if (BuiltIn.XSD_BASE64BINARY.equals(schemaDatatype)) {
			datatype = new BinaryBase64Datatype(qncSchemaType);
		} else if (BuiltIn.XSD_HEXBINARY.equals(schemaDatatype)) {
			datatype = new BinaryHexDatatype(qncSchemaType);
		} else if (BuiltIn.XSD_BOOLEAN.equals(schemaDatatype)) {
			if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_PATTERN)) {
				datatype = new BooleanFacetDatatype(qncSchemaType);
			} else {
				datatype = new BooleanDatatype(qncSchemaType);
			}
		} else if (BuiltIn.XSD_DATETIME.equals(schemaDatatype)) {
			QName primitive = getPrimitive(std);

			if (BuiltIn.XSD_DATETIME.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.dateTime,
						qncSchemaType);
			} else if (BuiltIn.XSD_TIME.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.time, qncSchemaType);
			} else if (BuiltIn.XSD_DATE.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.date, qncSchemaType);
			} else if (BuiltIn.XSD_GYEARMONTH.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.gYearMonth,
						qncSchemaType);
			} else if (BuiltIn.XSD_GYEAR.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.gYear, qncSchemaType);
			} else if (BuiltIn.XSD_GMONTHDAY.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.gMonthDay,
						qncSchemaType);
			} else if (BuiltIn.XSD_GDAY.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.gDay, qncSchemaType);
			} else if (BuiltIn.XSD_GMONTH.equals(primitive)) {
				datatype = new DatetimeDatatype(DateTimeType.gMonth, qncSchemaType);
			} else {
				throw new RuntimeException();
			}
		} else if (BuiltIn.XSD_DECIMAL.equals(schemaDatatype)) {
			datatype = new DecimalDatatype(qncSchemaType);
		} else if (BuiltIn.XSD_FLOAT.equals(schemaDatatype)
				|| BuiltIn.XSD_DOUBLE.equals(schemaDatatype)) {
			datatype = new FloatDatatype(qncSchemaType);
		} else if (BuiltIn.XSD_INTEGER.equals(schemaDatatype)) {
			// returns integer type (nbit, unsigned, int) according to facets
			datatype = getIntegerDatatype(std, schemaType);
		} else {
			// XSD_STRING with or without pattern
			if (std.isDefinedFacet(XSSimpleTypeDefinition.FACET_PATTERN)) {
				StringList sl = std.getLexicalPattern();

				if (isBuiltInTypeFacet(std, sl.getLength())) {
					// *normal* string
					datatype = new StringDatatype(qncSchemaType);
				} else {
					// analyze most-derived datatype facet only
					String regexPattern = sl.item(0);
					EXIRegularExpression re = new EXIRegularExpression(
							regexPattern);

					if (re.isEntireSetOfXMLCharacters()) {
						// *normal* string
						datatype = new StringDatatype(qncSchemaType);
					} else {
						// restricted char set
						RestrictedCharacterSet rcs = new CodePointCharacterSet(
								re.getCodePoints());
						datatype = new RestrictedCharacterSetDatatype(rcs,
								qncSchemaType);
					}
				}
			} else {
				datatype = new StringDatatype(qncSchemaType);
			}
		}

		return datatype;
	}

	private boolean isBuiltInTypeFacet(XSSimpleTypeDefinition std,
			int patternListLength) {
		// Note: only the most derived type is of interest
		XSSimpleTypeDefinition baseType = (XSSimpleTypeDefinition) std
				.getBaseType();
		boolean isBuiltInTypeFacet;

		if (baseType == null
				|| !baseType
						.isDefinedFacet(XSSimpleTypeDefinition.FACET_PATTERN)) {
			// check std type
			isBuiltInTypeFacet = XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(std
					.getNamespace());
		} else {
			if (baseType.getLexicalPattern().getLength() < patternListLength) {
				/*
				 * --> std defines the last pattern (check whether it is a
				 * built-in type)
				 */
				isBuiltInTypeFacet = XMLConstants.W3C_XML_SCHEMA_NS_URI
						.equals(std.getNamespace());
			} else {
				// call again base type
				isBuiltInTypeFacet = isBuiltInTypeFacet(baseType,
						patternListLength);
			}
		}

		return isBuiltInTypeFacet;
	}

	private QName getPrimitive(XSSimpleTypeDefinition std) {
		QName primitiveQName;
		XSSimpleTypeDefinition primitiveType = std.getPrimitiveType();

		if (primitiveType == null) {
			// TODO correct ?
			primitiveQName = BuiltIn.XSD_ANY_SIMPLE_TYPE;
		} else {
			primitiveQName = new QName(primitiveType.getNamespace(),
					primitiveType.getName());
		}

		return primitiveQName;
	}

	private QName getBuiltInOfPrimitiveMapping(QName qnamePrimitive) {
		if (datatypeMapping.containsKey(qnamePrimitive)) {
			return datatypeMapping.get(qnamePrimitive);
		} else {
			return BuiltIn.DEFAULT_VALUE_NAME;
		}
	}

}
