/*
 * Copyright (C) 2007-2012 Siemens AG
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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.junit.Test;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.QuickTestConfiguration;

public class GeneralTestCase extends AbstractTestCase {
	public GeneralTestCase() {
		super("General Test Cases");
	}

	public static void setupQuickTest() {
		// GeneralTestCase.setConfigurationPerson ( );
		// GeneralTestCase.setConfigurationPersonAdjusted ( );
		// GeneralTestCase.setConfigurationPersonal ( );
		// GeneralTestCase.setConfigurationUnbounded ( );
		// GeneralTestCase.setConfigurationDatatypeInteger();
		// GeneralTestCase.setConfigurationDatatypeFloat();
		// GeneralTestCase.setConfigurationDatatypes();
		// GeneralTestCase.setConfigurationDatatypes2 ( );
		// GeneralTestCase.setConfigurationOrder ( );
		// GeneralTestCase.setConfigurationRandj();
		// GeneralTestCase.setConfigurationPurchaseOrder ( );
		// GeneralTestCase.setConfigurationTest1 ( );
		// GeneralTestCase.setConfigurationTest1Pfx ( );
		// GeneralTestCase.setConfigurationTest2 ( );
		GeneralTestCase.setConfigurationTest3();
		// GeneralTestCase.setConfigurationTest4 ( );
		// GeneralTestCase.setConfigurationTest5 ( );
		// GeneralTestCase.setConfigurationComplexStructure ( );
		// GeneralTestCase.setConfigurationSimpleContent ( );
		// GeneralTestCase.setConfigurationEmptyContent ( );
		// GeneralTestCase.setConfigurationAttributes ( );
		// GeneralTestCase.setConfigurationPI1 ( );
		// GeneralTestCase.setConfigurationDocType();
		// GeneralTestCase.setConfigurationDocType0 ( );
		// GeneralTestCase.setConfigurationDocType1 ( );
		// GeneralTestCase.setConfigurationDocType2 ( );
		// GeneralTestCase.setConfigurationDocType3 ( );
		// GeneralTestCase.setConfigurationEntityReference1();
		// GeneralTestCase.setConfigurationEntityReference2();
		// GeneralTestCase.setConfigurationEntityReferenceUnresolved1();
		// GeneralTestCase.setConfigurationCData1();
		// GeneralTestCase.setConfigurationPatterns ( );
		// GeneralTestCase.setConfigurationStringTable1 ( );
		// GeneralTestCase.setConfigurationStringTable2 ( );
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
		testCaseOptions.lastElement().getEncodingOptions()
				.setOption(EncodingOptions.INCLUDE_XSI_SCHEMALOCATION);
		// testCaseOptions.lastElement().getFidelityOptions().setFidelity(
		// FidelityOptions.FEATURE_XSI_SCHEMALOCATION, true);
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(true);

		// #6
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().getEncodingOptions()
				.setOption(EncodingOptions.INCLUDE_XSI_SCHEMALOCATION);
		// testCaseOptions.lastElement().getFidelityOptions().setFidelity(
		// FidelityOptions.FEATURE_XSI_SCHEMALOCATION, true);
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

		// #7b (strict & lexical-values)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.lastElement().getFidelityOptions()
				.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, true);
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setSchemaInformedOnly(true);

		// #8
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setSchemaInformedOnly(true);

		// #9 BlockSize
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setSchemaInformedOnly(true);
		testCaseOptions.lastElement().setBlockSize(200);

		// #9 BlockSize
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setSchemaInformedOnly(true);
		testCaseOptions.lastElement().setBlockSize(200);

		// #10 BlockSize & valueMaxLength & valuePartitionCapacity
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setBlockSize(200);
		testCaseOptions.lastElement().setValueMaxLength(8);
		testCaseOptions.lastElement().setValuePartitionCapacity(16);

		// #11 valuePartitionCapacity
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setValuePartitionCapacity(5);

		// #11a valuePartitionCapacity + Header Cookie & Options & SchemaId
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setValuePartitionCapacity(4);
		testCaseOptions.lastElement().getEncodingOptions()
				.setOption(EncodingOptions.INCLUDE_COOKIE);
		testCaseOptions.lastElement().getEncodingOptions()
				.setOption(EncodingOptions.INCLUDE_OPTIONS);
		testCaseOptions.lastElement().getEncodingOptions()
				.setOption(EncodingOptions.INCLUDE_SCHEMA_ID);

		// #12 dtr map
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createDefault());
		QName[] dtrTypes = { new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
				"integer") };
		QName[] dtrRepresentations = { new QName(Constants.W3C_EXI_NS_URI,
				"string") };
		testCaseOptions.lastElement().setDatatypeRepresentationMap(dtrTypes,
				dtrRepresentations);

		// #13a localValuePartitions
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setLocalValuePartitions(false);

		// #13b localValuePartitions + valuePartitionCapacity
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setValuePartitionCapacity(4);
		testCaseOptions.lastElement().setLocalValuePartitions(false);

		// #14 grammar restrictions, needs schema-informed mode!
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setMaximumNumberOfBuiltInProductions(0);
		testCaseOptions.lastElement().setSchemaLocation(""); // xsd-types only

		// // #13 UCD Profile
		// testCaseOptions.add(new TestCaseOption());
		// testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		// testCaseOptions.lastElement().setFidelityOptions(
		// FidelityOptions.createDefault());
		// testCaseOptions.lastElement().setFragments(false);
		// testCaseOptions.lastElement().setXmlEqual(false);
		// testCaseOptions.lastElement().setProfile(EXIFactory.UCD_PROFILE);
		//
		// // #13 UCD Profile Byte-Aligned
		// testCaseOptions.add(new TestCaseOption());
		// testCaseOptions.lastElement().setCodingMode(CodingMode.BYTE_PACKED);
		// testCaseOptions.lastElement().setFidelityOptions(
		// FidelityOptions.createDefault());
		// testCaseOptions.lastElement().setFragments(false);
		// testCaseOptions.lastElement().setXmlEqual(false);
		// testCaseOptions.lastElement().setProfile(EXIFactory.UCD_PROFILE);

	}

	@Test
	public void testEntityReference1() throws Exception {
		// set up configuration
		setConfigurationEntityReference1();

		// Strict & LexicalValues is not working (Prefixes required)
		FidelityOptions noValidOptions = FidelityOptions.createStrict();
		noValidOptions.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, true);

		// execute test
		_test(noValidOptions);
	}

	@Test
	public void testPerson() throws Exception {
		// set up configuration
		setConfigurationPerson();

		// execute test
		_test();
	}

	public static void setConfigurationPerson() {
		QuickTestConfiguration.setXsdLocation("./data/general/person.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/person.xml");
		QuickTestConfiguration.setExiLocation("./out/general/person.exi");
	}

	@Test
	public void testPersonAdjusted() throws Exception {
		// set up configuration
		setConfigurationPersonAdjusted();

		// execute test
		_test();
	}

	public static void setConfigurationPersonAdjusted() {
		QuickTestConfiguration.setXsdLocation("./data/general/person.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/general/person_adjusted.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/person_adjusted.exi");
	}

	@Test
	public void testPersonal() throws Exception {
		// set up configuration
		setConfigurationPersonal();

		// execute test
		_test();
	}

	public static void setConfigurationPersonal() {
		QuickTestConfiguration.setXsdLocation("./data/general/personal.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/personal.xml");
		QuickTestConfiguration.setExiLocation("./out/general/personal.exi");
	}

	@Test
	public void testUnbounded() throws Exception {
		// set up configuration
		setConfigurationUnbounded();

		// execute test
		_test();
	}

	public static void setConfigurationUnbounded() {
		QuickTestConfiguration.setXsdLocation("./data/general/unbounded.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/unbounded.xml");
		QuickTestConfiguration.setExiLocation("./out/general/unbounded.exi");
	}

	@Test
	public void testDatatypeInteger() throws Exception {
		// set up configuration
		setConfigurationDatatypeInteger();

		// execute test
		_test();
	}

	public static void setConfigurationDatatypeInteger() {
		QuickTestConfiguration
				.setXsdLocation("./data/general/datatypeInteger.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/general/datatypeInteger.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/datatypeInteger.exi");
	}

	@Test
	public void testDatatypeFloat() throws Exception {
		// set up configuration
		setConfigurationDatatypeFloat();

		// execute test
		_test();
	}

	public static void setConfigurationDatatypeFloat() {
		QuickTestConfiguration
				.setXsdLocation("./data/general/datatypeFloat.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/general/datatypeFloat.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/datatypeFloat.exi");
	}

	@Test
	public void testDatatypes() throws Exception {
		// set up configuration
		setConfigurationDatatypes();

		// execute test
		_test();
	}

	public static void setConfigurationDatatypes() {
		QuickTestConfiguration.setXsdLocation("./data/general/datatypes.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/datatypes.xml");
		QuickTestConfiguration.setExiLocation("./out/general/datatypes.exi");
	}

	@Test
	public void testDatatypes2() throws Exception {
		// set up configuration
		setConfigurationDatatypes2();

		// execute test
		_test();
	}

	public static void setConfigurationDatatypes2() {
		QuickTestConfiguration.setXsdLocation("./data/general/datatypes2.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/datatypes2.xml");
		QuickTestConfiguration.setExiLocation("./out/general/datatypes2.exi");
	}

	@Test
	public void testOrder() throws Exception {
		// set up configuration
		setConfigurationOrder();

		// execute test
		_test();
	}

	public static void setConfigurationOrder() {
		QuickTestConfiguration.setXsdLocation("./data/general/order.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/order.xml");
		QuickTestConfiguration.setExiLocation("./out/general/order.exi");
	}

	@Test
	public void testComplexStructure() throws Exception {
		// set up configuration
		setConfigurationComplexStructure();

		// execute test
		_test();
	}

	public static void setConfigurationComplexStructure() {
		QuickTestConfiguration
				.setXsdLocation("./data/general/complex-structure.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/general/complex-structure.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/complex-structure.exi");
	}

	@Test
	public void testSimpleContent() throws Exception {
		// set up configuration
		setConfigurationSimpleContent();

		// execute test
		_test();
	}

	public static void setConfigurationSimpleContent() {
		QuickTestConfiguration
				.setXsdLocation("./data/general/simpleContent.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/general/simpleContent.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/simpleContent.xml.exi");
	}

	@Test
	public void testEmptyContent() throws Exception {
		// set up configuration
		setConfigurationEmptyContent();

		// execute test
		_test();
	}

	public static void setConfigurationEmptyContent() {
		QuickTestConfiguration
				.setXsdLocation("./data/general/emptyContent.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/general/emptyContent.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/emptyContent.xml.exi");
	}

	@Test
	public void testAttributes() throws Exception {
		// set up configuration
		setConfigurationAttributes();

		// execute test
		_test();
	}

	public static void setConfigurationAttributes() {
		QuickTestConfiguration.setXsdLocation("./data/general/attributes.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/attributes.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/attributes.xml.exi");
	}

	@Test
	public void testPostalRandj() throws Exception {
		// set up configuration
		setConfigurationRandj();

		// execute test
		_test();
	}

	public static void setConfigurationRandj() {
		QuickTestConfiguration.setXsdLocation("./data/general/randj.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/randj.xml");
		QuickTestConfiguration.setExiLocation("./out/general/randj.exi");
	}

	@Test
	public void testPurchaseOrder() throws Exception {
		// set up configuration
		setConfigurationPurchaseOrder();

		// execute test
		_test();
	}

	public static void setConfigurationPurchaseOrder() {
		QuickTestConfiguration.setXsdLocation("./data/general/po.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/po.xml");
		QuickTestConfiguration.setExiLocation("./out/general/po.xml.exi");
	}

	@Test
	public void testXMLTest1() throws Exception {
		// set up configuration
		setConfigurationTest1();

		// execute test
		_test();
	}

	public static void setConfigurationTest1() {
		QuickTestConfiguration.setXsdLocation("./data/general/test1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/test1.xml");
		QuickTestConfiguration.setExiLocation("./out/general/test1.exi");
	}

	@Test
	public void testXMLTest1Pfx() throws Exception {
		// set up configuration
		setConfigurationTest1Pfx();

		// execute test
		_test();
	}

	public static void setConfigurationTest1Pfx() {
		QuickTestConfiguration.setXsdLocation("./data/general/test1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/test1_pfx.xml");
		QuickTestConfiguration.setExiLocation("./out/general/test1_pfx.exi");
	}

	@Test
	public void testXMLTest2() throws Exception {
		// set up configuration
		setConfigurationTest2();

		// execute test
		_test();
	}

	public static void setConfigurationTest2() {
		QuickTestConfiguration.setXsdLocation("./data/general/test2.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/test2.xml");
		QuickTestConfiguration.setExiLocation("./out/general/test2.exi");
	}

	@Test
	public void testXMLTest3() throws Exception {
		// set up configuration
		setConfigurationTest3();

		// execute test
		_test();
	}

	public static void setConfigurationTest3() {
		QuickTestConfiguration.setXsdLocation("./data/general/test3.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/test3.xml");
		QuickTestConfiguration.setExiLocation("./out/general/test3.exi");
	}

	@Test
	public void testXMLTest4() throws Exception {
		// set up configuration
		setConfigurationTest4();

		// execute test
		_test();
	}

	public static void setConfigurationTest4() {
		QuickTestConfiguration.setXsdLocation("./data/general/test4.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/test4.xml");
		QuickTestConfiguration.setExiLocation("./out/general/test4.xml.exi");
	}

	@Test
	public void testXMLTest5() throws Exception {
		// set up configuration
		setConfigurationTest5();

		// execute test
		_test();
	}

	public static void setConfigurationTest5() {
		QuickTestConfiguration.setXsdLocation("./data/general/test5.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/test5.xml");
		QuickTestConfiguration.setExiLocation("./out/general/test5.xml.exi");
	}

	@Test
	public void testPI1() throws Exception {
		// set up configuration
		setConfigurationPI1();

		// execute test
		_test();
	}

	public static void setConfigurationPI1() {
		QuickTestConfiguration.setXsdLocation("./data/general/pi1.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/pi1.xml");
		QuickTestConfiguration.setExiLocation("./out/general/pi1.xml.exi");
	}

	@Test
	public void testDocType() throws Exception {
		// set up configuration
		setConfigurationDocType();

		// execute test
		_test();
	}

	public static void setConfigurationDocType() {
		QuickTestConfiguration.setXsdLocation("./data/general/docType.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/docType.xml");
		QuickTestConfiguration.setExiLocation("./out/general/docType.xml.exi");
	}

	@Test
	public void testDocType0() throws Exception {
		// set up configuration
		setConfigurationDocType0();

		// execute test
		_test();
	}

	public static void setConfigurationDocType0() {
		QuickTestConfiguration.setXsdLocation("./data/general/docType.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/docType0.xml");
		QuickTestConfiguration.setExiLocation("./out/general/docType0.xml.exi");
	}

	@Test
	public void testDocType1() throws Exception {
		// set up configuration
		setConfigurationDocType1();

		// execute test
		_test();
	}

	public static void setConfigurationDocType1() {
		QuickTestConfiguration.setXsdLocation("./data/general/docType.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/docType1.xml");
		QuickTestConfiguration.setExiLocation("./out/general/docType1.xml.exi");
	}

	@Test
	public void testDocType2() throws Exception {
		// set up configuration
		setConfigurationDocType2();

		// execute test
		_test();
	}

	public static void setConfigurationDocType2() {
		QuickTestConfiguration.setXsdLocation("./data/general/docType.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/docType2.xml");
		QuickTestConfiguration.setExiLocation("./out/general/docType2.xml.exi");
	}

	@Test
	public void testDocType3() throws Exception {
		// set up configuration
		setConfigurationDocType3();

		// execute test
		_test();
	}

	public static void setConfigurationDocType3() {
		QuickTestConfiguration.setXsdLocation("./data/general/docType.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/docType3.xml");
		QuickTestConfiguration.setExiLocation("./out/general/docType3.xml.exi");
	}

	public static void setConfigurationEntityReference1() {
		QuickTestConfiguration.setXsdLocation("./data/general/empty.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/general/entityReference1.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/entityReference1.xml.exi");
	}

	public static void setConfigurationEntityReference2() {
		QuickTestConfiguration.setXsdLocation("./data/general/empty.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/general/entityReference2.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/entityReference2.xml.exi");
	}

	@Test
	public void testEntityReference2() throws Exception {
		// set up configuration
		setConfigurationEntityReference2();

		// Strict & LexicalValues is not working (Prefixes required)
		FidelityOptions noValidOptions = FidelityOptions.createStrict();
		noValidOptions.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, true);

		// execute test
		_test(noValidOptions);
	}

	// @Test
	// public void testEntityReferenceUnresolved1() throws Exception {
	// // set up configuration
	// setConfigurationEntityReferenceUnresolved1();
	//
	// // execute test
	// _test();
	// }
	//
	// public static void setConfigurationEntityReferenceUnresolved1() {
	// QuickTestConfiguration.setXsdLocation("./data/general/empty.xsd");
	// QuickTestConfiguration
	// .setXmlLocation("./data/general/entityReferenceUnresolved1.xml");
	// QuickTestConfiguration
	// .setExiLocation("./out/general/entityReferenceUnresolved1.xml.exi");
	// }

	@Test
	public void xtestCData1() throws Exception {
		// set up configuration
		setConfigurationCData1();

		// execute test
		_test();
	}

	public static void setConfigurationCData1() {
		QuickTestConfiguration.setXsdLocation("./data/general/empty.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/cdata1.xml");
		QuickTestConfiguration.setExiLocation("./out/general/cdata1.xml.exi");
	}

	@Test
	public void testPatterns() throws Exception {
		// set up configuration
		setConfigurationPatterns();

		// execute test
		_test();
	}

	public static void setConfigurationPatterns() {
		QuickTestConfiguration.setXsdLocation("./data/general/patterns.xsd");
		QuickTestConfiguration.setXmlLocation("./data/general/patterns.xml");
		QuickTestConfiguration.setExiLocation("./out/general/patterns.xml.exi");
	}

	@Test
	public void testStringTable1() throws Exception {
		// set up configuration
		setConfigurationStringTable1();

		// execute test
		_test();
	}

	public static void setConfigurationStringTable1() {
		QuickTestConfiguration
				.setXsdLocation("./data/general/stringTable1.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/general/stringTable1.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/stringTable1.xml.exi");
	}

	@Test
	public void testStringTable2() throws Exception {
		// set up configuration
		setConfigurationStringTable2();

		// execute test
		_test();
	}

	public static void setConfigurationStringTable2() {
		QuickTestConfiguration
				.setXsdLocation("./data/general/stringTable2.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/general/stringTable2.xml");
		QuickTestConfiguration
				.setExiLocation("./out/general/stringTable2.xml.exi");
	}

}
