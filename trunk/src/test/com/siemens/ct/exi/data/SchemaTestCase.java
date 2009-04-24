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

package com.siemens.ct.exi.data;

import org.junit.Test;

import com.siemens.ct.exi.CodingMode;
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
		// SchemaTestCase.setConfigurationSchemaOccurrences1 ( );
		// SchemaTestCase.setConfigurationSchemaOccurrences2 ( );
		// SchemaTestCase.setConfigurationSchemaAll ( );
		// SchemaTestCase.setConfigurationSchemaAll2 ( );
		// SchemaTestCase.setConfigurationSchemaMixed ( );
		// SchemaTestCase.setConfigurationSchemaWildcard ( );
		// SchemaTestCase.setConfigurationSchemaWildcard2();
		// SchemaTestCase.setConfigurationSchemaWildcard3();
		// SchemaTestCase.setConfigurationSchemaNillable1 ( );
		// SchemaTestCase.setConfigurationSchemaNillable2 ( );
		// SchemaTestCase.setConfigurationSchemaXsiType();
		// SchemaTestCase.setConfigurationSchemaXsiType2 ( );
		// SchemaTestCase.setConfigurationSchemaXsiType3 ( );
		// SchemaTestCase.setConfigurationSchemaXsiType4 ( );
		SchemaTestCase.setConfigurationSchemaAnyAttributes ( );
		// SchemaTestCase.setConfigurationSchemaAny0 ( );
		// SchemaTestCase.setConfigurationSchemaEnumeration ( );
		// SchemaTestCase.setConfigurationSchemaList ( );
		// SchemaTestCase.setConfigurationSchemaUnsignedInteger ( );
		// SchemaTestCase.setConfigurationSchemaIdenticalQName ( );
		// SchemaTestCase.setConfigurationSchemaIdenticalQName2 ( );
		// SchemaTestCase.setConfigurationAttributeSpace ( );
		// SchemaTestCase.setConfigurationBooleanPattern();
		// SchemaTestCase.setConfigurationPattern1();
	}

	protected void setUp() {
		// #1 (default)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);

		// #2
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);

		// #3 (all)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(true);

		// #4
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(true);

		// #5 (strict)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setSchemaInformedOnly(true);

		// #6
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setSchemaInformedOnly(true);
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
	public void testSchemaAny() throws Exception {
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
