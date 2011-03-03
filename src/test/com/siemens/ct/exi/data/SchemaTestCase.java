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

package com.siemens.ct.exi.data;

import org.junit.Test;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.QuickTestConfiguration;

public class SchemaTestCase extends AbstractTestCase {
	public SchemaTestCase() {
		super("Schema Test Cases");
	}

	public static void setupQuickTest() {
		// SchemaTestCase.setConfigurationSchemaChoice1 ( );
		// SchemaTestCase.setConfigurationSchemaChoice2 ( );
		// SchemaTestCase.setConfigurationSchemaChoice3 ( );
		// SchemaTestCase.setConfigurationSchemaChoiceN1 ( );
		// SchemaTestCase.setConfigurationSchemaSequence1 ( );
		// SchemaTestCase.setConfigurationSchemaSequence2 ( );
		// SchemaTestCase.setConfigurationSchemaGroup1 ( );
		// SchemaTestCase.setConfigurationSchemaGroup2 ( );
		// SchemaTestCase.setConfigurationSchemaGroup3 ( );
		// SchemaTestCase.setConfigurationSchemaUnion1();
		// SchemaTestCase.setConfigurationSchemaSubstitutionGroup1a ( );
		// SchemaTestCase.setConfigurationSchemaSubstitutionGroup1b ( );
		// SchemaTestCase.setConfigurationSchemaAttributeGroup1a ( );
		// SchemaTestCase.setConfigurationSchemaAttributeGroup1b ( );
		// SchemaTestCase.setConfigurationSchemaOccurrences1 ( );
		// SchemaTestCase.setConfigurationSchemaOccurrences2 ( );
		// SchemaTestCase.setConfigurationSchemaAll ( );
		// SchemaTestCase.setConfigurationSchemaAll2 ( );
		// SchemaTestCase.setConfigurationSchemaMixed ( );
		// SchemaTestCase.setConfigurationSchemaWildcard ( );
		// SchemaTestCase.setConfigurationSchemaWildcard2();
		// SchemaTestCase.setConfigurationSchemaWildcard3();
		SchemaTestCase.setConfigurationSchemaNillable1 ( );
		// SchemaTestCase.setConfigurationSchemaNillable2 ( );
		// SchemaTestCase.setConfigurationSchemaXsiType();
		// SchemaTestCase.setConfigurationSchemaXsiType2 ( );
		// SchemaTestCase.setConfigurationSchemaXsiType3 ( );
		// SchemaTestCase.setConfigurationSchemaXsiType4 ( );
		// SchemaTestCase.setConfigurationSchemaXsiType5 ( );
		// SchemaTestCase.setConfigurationSchemaAnyAttributes ( );
		// SchemaTestCase.setConfigurationGlobalAttribute1();
		// SchemaTestCase.setConfigurationSchemaAny0 ( );
		// SchemaTestCase.setConfigurationSchemaAny1 ( );
		// SchemaTestCase.setConfigurationSchemaEnumeration ( );
		// SchemaTestCase.setConfigurationSchemaList ( );
		// SchemaTestCase.setConfigurationSchemaUnsignedInteger ( );
		// SchemaTestCase.setConfigurationSchemaIdenticalQName ( );
		// SchemaTestCase.setConfigurationSchemaIdenticalQName2 ( );
		// SchemaTestCase.setConfigurationSchemaIdenticalQName3 ( );
		// SchemaTestCase.setConfigurationAttributeSpace ( );
		// SchemaTestCase.setConfigurationBooleanPattern();
		// SchemaTestCase.setConfigurationPattern1();
		// SchemaTestCase.setConfigurationSchemaVehicle();
	}

	protected void setUp() throws Exception {
		// #1 (default)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);

		// #2
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BYTE_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		
		// #3
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.PRE_COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		
		// #4
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);

		// #5 (all)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(true);

		// #6
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(true);

		// #7 (strict)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setSchemaInformedOnly(true);
		
//		// #7b (strict & lexical-values)
//		testCaseOptions.add(new TestCaseOption());
//		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
//		testCaseOptions.lastElement().setFidelityOptions(
//				FidelityOptions.createStrict());
//			testCaseOptions.lastElement().getFidelityOptions().setFidelity(
//					FidelityOptions.FEATURE_LEXICAL_VALUE, true);
//		testCaseOptions.lastElement().setFragments(false);
//		testCaseOptions.lastElement().setXmlEqual(false);
//		testCaseOptions.lastElement().setSchemaInformedOnly(true);

		// #8
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setSchemaInformedOnly(true);
		
		
		// #8 + Header Cookie & Options & SchemaId
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setSchemaInformedOnly(true);
		testCaseOptions.lastElement().getEncodingOptions().setOption(
				EncodingOptions.INCLUDE_COOKIE);
		testCaseOptions.lastElement().getEncodingOptions().setOption(
				EncodingOptions.INCLUDE_OPTIONS);
		testCaseOptions.lastElement().getEncodingOptions().setOption(
				EncodingOptions.INCLUDE_SCHEMA_ID);
//		testCaseOptions.lastElement().setIncludeCookie(true);
//		testCaseOptions.lastElement().setIncludeOptions(true);
//		testCaseOptions.lastElement().setIncludeSchemaId(true);
	}

	@Test
	public void testSchemaChoice1() throws Exception {
		// set up configuration
		setConfigurationSchemaChoice1();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaChoice1() {
		QuickTestConfiguration.setXsdLocation("./data/schema/choice.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/choice1.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/choice1.exi");
	}

	public void testSchemaChoice2() throws Exception {
		// set up configuration
		setConfigurationSchemaChoice2();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaChoice2() {
		QuickTestConfiguration.setXsdLocation("./data/schema/choice.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/choice2.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/choice2.exi");
	}

	public void testSchemaChoice3() throws Exception {
		// set up configuration
		setConfigurationSchemaChoice3();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaChoice3() {
		QuickTestConfiguration.setXsdLocation("./data/schema/choice.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/choice3.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/choice3.exi");
	}

	@Test
	public void testSchemaChoiceN1() throws Exception {
		// set up configuration
		setConfigurationSchemaChoiceN1();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaChoiceN1() {
		QuickTestConfiguration.setXsdLocation("./data/schema/choiceN.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/choiceN1.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/choiceN1.exi");
	}

	@Test
	public void testSchemaSequence1() throws Exception {
		// set up configuration
		setConfigurationSchemaSequence1();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaSequence1() {
		QuickTestConfiguration.setXsdLocation("./data/schema/sequence.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/sequence1.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/sequence1.exi");
	}

	@Test
	public void testSchemaSequence2() throws Exception {
		// set up configuration
		setConfigurationSchemaSequence2();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaSequence2() {
		QuickTestConfiguration.setXsdLocation("./data/schema/sequence.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/sequence2.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/sequence2.exi");
	}
	
	@Test
	public void testSchemaGroup1() throws Exception {
		// set up configuration
		setConfigurationSchemaGroup1();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaGroup1() {
		QuickTestConfiguration.setXsdLocation("./data/schema/group1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/group1.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/group1.exi");
	}
	
	@Test
	public void testSchemaGroup2() throws Exception {
		// set up configuration
		setConfigurationSchemaGroup2();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaGroup2() {
		QuickTestConfiguration.setXsdLocation("./data/schema/group2.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/group2.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/group2.exi");
	}

	@Test
	public void testSchemaGroup3() throws Exception {
		// set up configuration
		setConfigurationSchemaGroup3();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaGroup3() {
		QuickTestConfiguration.setXsdLocation("./data/schema/group3.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/group3.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/group3.exi");
	}
	
	
	@Test
	public void testSchemaUnion1() throws Exception {
		// set up configuration
		setConfigurationSchemaUnion1();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaUnion1() {
		QuickTestConfiguration.setXsdLocation("./data/schema/union1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/union1.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/union1.exi");
	}
	
	
	@Test
	public void testSchemaSubstitutionGroup1a() throws Exception {
		// set up configuration
		setConfigurationSchemaSubstitutionGroup1a();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaSubstitutionGroup1a() {
		QuickTestConfiguration.setXsdLocation("./data/schema/substitutionGroup1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/substitutionGroup1a.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/substitutionGroup1a.xml.exi");
	}
	
	@Test
	public void testSchemaSubstitutionGroup1b() throws Exception {
		// set up configuration
		setConfigurationSchemaSubstitutionGroup1b();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaSubstitutionGroup1b() {
		QuickTestConfiguration.setXsdLocation("./data/schema/substitutionGroup1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/substitutionGroup1b.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/substitutionGroup1b.xml.exi");
	}

	@Test
	public void testSchemaAttributeGroup1a() throws Exception {
		// set up configuration
		setConfigurationSchemaAttributeGroup1a();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaAttributeGroup1a() {
		QuickTestConfiguration.setXsdLocation("./data/schema/attributeGroup1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/attributeGroup1a.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/attributeGroup1a.xml.exi");
	}
	
	
	@Test
	public void testSchemaAttributeGroup1b() throws Exception {
		// set up configuration
		setConfigurationSchemaAttributeGroup1b();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaAttributeGroup1b() {
		QuickTestConfiguration.setXsdLocation("./data/schema/attributeGroup1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/attributeGroup1b.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/attributeGroup1b.xml.exi");
	}
	
	
	@Test
	public void testSchemaOccurrences1() throws Exception {
		// set up configuration
		setConfigurationSchemaOccurrences1();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaOccurrences1() {
		QuickTestConfiguration.setXsdLocation("./data/schema/occurrences1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/occurrences1.xml");
		QuickTestConfiguration
				.setExiLocation("./out/schema/occurrences1.xml.exi");
	}

	@Test
	public void testSchemaOccurrences2() throws Exception {
		// set up configuration
		setConfigurationSchemaOccurrences2();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaOccurrences2() {
		QuickTestConfiguration.setXsdLocation("./data/schema/occurrences2.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/occurrences2.xml");
		QuickTestConfiguration
				.setExiLocation("./out/schema/occurrences2.xml.exi");
	}

	@Test
	public void testSchemaAll() throws Exception {
		// set up configuration
		setConfigurationSchemaAll();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaAll() {
		QuickTestConfiguration.setXsdLocation("./data/schema/all.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/all.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/all.exi");
	}

	@Test
	public void testSchemaAll2() throws Exception {
		// set up configuration
		setConfigurationSchemaAll2();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaAll2() {
		QuickTestConfiguration.setXsdLocation("./data/schema/all2.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/all2.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/all2.exi");
	}

	@Test
	public void testSchemaEnumeration() throws Exception {
		// set up configuration
		setConfigurationSchemaEnumeration();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaEnumeration() {
		QuickTestConfiguration.setXsdLocation("./data/schema/enumeration.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/enumeration.xml");
		QuickTestConfiguration
				.setExiLocation("./out/schema/enumeration.xml.exi");
	}

	@Test
	public void testSchemaList() throws Exception {
		// set up configuration
		setConfigurationSchemaList();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaList() {
		QuickTestConfiguration.setXsdLocation("./data/schema/list.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/list.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/list.xml.exi");
	}

	@Test
	public void testSchemaUnsignedInteger() throws Exception {
		// set up configuration
		setConfigurationSchemaUnsignedInteger();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaUnsignedInteger() {
		QuickTestConfiguration
				.setXsdLocation("./data/schema/unsignedInteger.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/schema/unsignedInteger.xml");
		QuickTestConfiguration
				.setExiLocation("./out/schema/unsignedInteger.xml.exi");
	}

	@Test
	public void testSchemaMixed() throws Exception {
		// set up configuration
		setConfigurationSchemaMixed();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaMixed() {
		QuickTestConfiguration.setXsdLocation("./data/schema/mixed.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/mixed.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/mixed.exi");
	}

	@Test
	public void testSchemaWildcard() throws Exception {
		// set up configuration
		setConfigurationSchemaWildcard();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaWildcard() {
		QuickTestConfiguration.setXsdLocation("./data/schema/wildcard.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/wildcard.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/wildcard.exi");
	}

	@Test
	public void testSchemaWildcard2() throws Exception {
		// set up configuration
		setConfigurationSchemaWildcard2();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaWildcard2() {
		QuickTestConfiguration.setXsdLocation("./data/schema/wildcard2.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/wildcard2.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/wildcard2.exi");
	}
	

	@Test
	public void testSchemaWildcard3() throws Exception {
		// set up configuration
		setConfigurationSchemaWildcard3();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaWildcard3() {
		QuickTestConfiguration.setXsdLocation("./data/schema/wildcard3.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/wildcard3.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/wildcard3.exi");
	}

	@Test
	public void testSchemaNillable1() throws Exception {
		// set up configuration
		setConfigurationSchemaNillable1();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaNillable1() {
		QuickTestConfiguration.setXsdLocation("./data/schema/nillable.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/nillable1.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/nillable1.exi");
	}

	@Test
	public void testSchemaNillable2() throws Exception {
		// set up configuration
		setConfigurationSchemaNillable2();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaNillable2() {
		QuickTestConfiguration.setXsdLocation("./data/schema/nillable.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/nillable2.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/nillable2.exi");
	}

	@Test
	public void testSchemaXsiType() throws Exception {
		// set up configuration
		setConfigurationSchemaXsiType();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaXsiType() {
		QuickTestConfiguration.setXsdLocation("./data/schema/xsi-type.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/xsi-type.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/xsi-type.exi");
	}

	@Test
	public void testSchemaXsiType2() throws Exception {
		// set up configuration
		setConfigurationSchemaXsiType2();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaXsiType2() {
		QuickTestConfiguration.setXsdLocation("./data/schema/xsi-type2.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/xsi-type2.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/xsi-type2.exi");
	}

	@Test
	public void testSchemaXsiType3() throws Exception {
		// set up configuration
		setConfigurationSchemaXsiType3();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaXsiType3() {
		QuickTestConfiguration.setXsdLocation("./data/schema/xsi-type.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/xsi-type3.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/xsi-type3.exi");
	}

	@Test
	public void testSchemaXsiType4() throws Exception {
		// set up configuration
		setConfigurationSchemaXsiType4();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaXsiType4() {
		QuickTestConfiguration.setXsdLocation("./data/schema/xsi-type4.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/xsi-type4.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/xsi-type4.exi");
	}
	
	@Test
	public void testSchemaXsiType5() throws Exception {
		// set up configuration
		setConfigurationSchemaXsiType5();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaXsiType5() {
		QuickTestConfiguration.setXsdLocation("./data/schema/empty.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/xsi-type5.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/xsi-type5.exi");
	}

	@Test
	public void testSchemaVehicle() throws Exception {
		// set up configuration
		setConfigurationSchemaVehicle();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaVehicle() {
		QuickTestConfiguration.setXsdLocation("./data/schema/vehicle.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/vehicle.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/vehicle.xml.exi");
	}

	@Test
	public void testSchemaAnyAttributes() throws Exception {
		// set up configuration
		setConfigurationSchemaAnyAttributes();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaAnyAttributes() {
		QuickTestConfiguration
				.setXsdLocation("./data/schema/anyAttributes.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/schema/anyAttributes.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/anyAttributes.exi");
	}
	
	
	@Test
	public void testGlobalAttribute1() throws Exception {
		// set up configuration
		setConfigurationGlobalAttribute1();

		// execute test
		_test();
	}

	public static void setConfigurationGlobalAttribute1() {
		QuickTestConfiguration.setXsdLocation("./data/schema/globalAttribute.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/globalAttribute1.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/globalAttribute1.xml.exi");
	}

	@Test
	public void testSchemaAny0() throws Exception {
		// set up configuration
		setConfigurationSchemaAny0();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaAny0() {
		QuickTestConfiguration.setXsdLocation("./data/schema/any.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/any0.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/any0.exi");
	}
	
	@Test
	public void testSchemaAny1() throws Exception {
		// set up configuration
		setConfigurationSchemaAny1();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaAny1() {
		QuickTestConfiguration.setXsdLocation("./data/schema/any1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/any1.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/any1.exi");
	}

	@Test
	public void testAttributeSpace() throws Exception {
		// set up configuration
		setConfigurationAttributeSpace();

		// execute test
		_test();
	}

	public static void setConfigurationAttributeSpace() {
		QuickTestConfiguration
				.setXsdLocation("./data/schema/attributeSpace.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/schema/attributeSpace.xml");
		QuickTestConfiguration
				.setExiLocation("./out/schema/attributeSpace.exi");
	}

	@Test
	public void testSchemaIdenticalQName2() throws Exception {
		// set up configuration
		setConfigurationSchemaIdenticalQName2();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaIdenticalQName2() {
		QuickTestConfiguration
				.setXsdLocation("./data/schema/identicalQName2.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/schema/identicalQName2.xml");
		QuickTestConfiguration
				.setExiLocation("./out/schema/identicalQName2.xml.exi");
	}

	// TODO solve dependency between identicalQName 1 & 2 which causes trouble
	// !?
	@Test
	public void testSchemaIdenticalQName() throws Exception {
		// set up configuration
		setConfigurationSchemaIdenticalQName();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaIdenticalQName() {
		QuickTestConfiguration
				.setXsdLocation("./data/schema/identicalQName.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/schema/identicalQName.xml");
		QuickTestConfiguration
				.setExiLocation("./out/schema/identicalQName.xml.exi");
	}
	
	@Test
	public void testSchemaIdenticalQName3() throws Exception {
		// set up configuration
		setConfigurationSchemaIdenticalQName3();

		// execute test
		_test();
	}

	public static void setConfigurationSchemaIdenticalQName3() {
		QuickTestConfiguration
				.setXsdLocation("./data/schema/identicalQName3.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/schema/identicalQName3.xml");
		QuickTestConfiguration
				.setExiLocation("./out/schema/identicalQName3.xml.exi");
	}

	@Test
	public void testBooleanPattern() throws Exception {
		// set up configuration
		setConfigurationBooleanPattern();

		// execute test
		_test();
	}

	public static void setConfigurationBooleanPattern() {
		QuickTestConfiguration
				.setXsdLocation("./data/schema/booleanPattern.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/schema/booleanPattern.xml");
		QuickTestConfiguration
				.setExiLocation("./out/schema/booleanPattern.xml.exi");
	}

	@Test
	public void testPattern1() throws Exception {
		// set up configuration
		setConfigurationPattern1();

		// execute test
		_test();
	}

	public static void setConfigurationPattern1() {
		QuickTestConfiguration.setXsdLocation("./data/schema/pattern.xsd");
		QuickTestConfiguration.setXmlLocation("./data/schema/pattern.xml");
		QuickTestConfiguration.setExiLocation("./out/schema/pattern.xml.exi");
	}

}
