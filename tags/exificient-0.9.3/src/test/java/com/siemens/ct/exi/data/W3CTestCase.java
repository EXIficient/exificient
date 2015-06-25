/*
 * Copyright (C) 2007-2014 Siemens AG
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

import javax.xml.namespace.QName;

import org.junit.Test;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.QuickTestConfiguration;

public class W3CTestCase extends AbstractTestCase {

	public W3CTestCase() {
		super("W3C Test Cases");
	}

	public static void setupQuickTest() {
		// W3CTestCase.setConfigurationW3CXMLSample ( );
		// W3CTestCase.setConfigurationW3CXMLSample_Pfx();
		// W3CTestCase.setConfigurationW3CXMLSample_Pfx2();
		// W3CTestCase.setConfigurationW3CEXIbyExample();
		// W3CTestCase.setConfigurationW3CPrimerNotebook();
		W3CTestCase.setConfigurationW3CXHTMLStrict1();
		// W3CTestCase.setConfigurationW3CXHTMLTransitional1();
		// W3CTestCase.setConfigurationW3CXsdWsdl1();
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

		// #7 (SelfContained)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		FidelityOptions scFo = FidelityOptions.createAll();
		scFo.setFidelity(FidelityOptions.FEATURE_SC, true);
		QName[] scElements = new QName[3];
		scElements[0] = new QName("", "note"); // notebook
		scElements[1] = new QName("", "body"); // notebook, nested
		scElements[2] = new QName("http://www.foo.com", "person"); // XMLSample
		testCaseOptions.lastElement().setSelfContainedElements(scElements);
		testCaseOptions.lastElement().setFidelityOptions(scFo);
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(true);

		// #8 valuePartitionCapacity
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(true);
		testCaseOptions.lastElement().setValuePartitionCapacity(9);

	}

	@Test
	public void testW3CEXIbyExample() throws Exception {
		// set up configuration
		setConfigurationW3CEXIbyExample();

		// execute test
		_test();
	}

	@Test
	public void testW3CXMLSample() throws Exception {
		// set up configuration
		setConfigurationW3CXMLSample();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXMLSample() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/XMLSample/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/XMLSample/XMLSample.xml");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/XMLSample/XMLSample.exi");
	}

	@Test
	public void testW3CXMLSample_Pfx() throws Exception {
		// set up configuration
		setConfigurationW3CXMLSample_Pfx();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXMLSample_Pfx() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/XMLSample/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/XMLSample/XMLSample_pfx.xml");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/XMLSample/XMLSample_pfx.exi");
	}

	@Test
	public void testW3CXMLSample_Pfx2() throws Exception {
		// set up configuration
		setConfigurationW3CXMLSample_Pfx2();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXMLSample_Pfx2() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/XMLSample/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/XMLSample/XMLSample_pfx2.xml");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/XMLSample/XMLSample_pfx2.exi");
	}

	public static void setConfigurationW3CEXIbyExample() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/EXIbyExample/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/EXIbyExample/XMLSample.xml");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/EXIbyExample/XMLSample.exi");
	}

	@Test
	public void testW3CPrimerNotebook() throws Exception {
		// set up configuration
		setConfigurationW3CPrimerNotebook();

		// execute test
		_test();
	}

	public static void setConfigurationW3CPrimerNotebook() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/PrimerNotebook/notebook.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/PrimerNotebook/notebook.xml");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/PrimerNotebook/notebook.exi");
	}

	/*
	 * XHTML examples
	 */

	@Test
	public void xtestW3CXHTMLStrict1() throws Exception {
		// set up configuration
		setConfigurationW3CXHTMLStrict1();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXHTMLStrict1() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/xhtml/xhtml1-strict.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/xhtml/www.w3.org.htm");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/xhtml/www.w3.org.htm.exi");
	}

	@Test
	public void xtestW3CXHTMLTransitional1() throws Exception {
		// set up configuration
		setConfigurationW3CXHTMLTransitional1();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXHTMLTransitional1() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/xhtml/xhtml1-transitional.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/xhtml/en.wikipedia.org-wiki-EXI.htm");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/xhtml/en.wikipedia.org-wiki-EXI.htm.exi");
	}

	@Test
	public void xtestW3CXsdWsdl1() throws Exception {
		// set up configuration
		setConfigurationW3CXsdWsdl1();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXsdWsdl1() {
		QuickTestConfiguration.setXsdLocation("./data/W3C/xsd/XMLSchema.xsd");
		QuickTestConfiguration.setXmlLocation("./data/W3C/xsd/wsdl.xsd");
		QuickTestConfiguration.setExiLocation("./out/W3C/xsd/wsdl.xsd.exi");
	}

}