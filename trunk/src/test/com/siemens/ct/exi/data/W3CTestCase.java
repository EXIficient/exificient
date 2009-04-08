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

public class W3CTestCase extends AbstractTestCase {

	public W3CTestCase() {
		super("W3C Test Cases");
	}

	public static void setupQuickTest() {
		// W3CTestCase.setConfigurationW3CXMLSample ( );
		// W3CTestCase.setConfigurationW3CXMLSample_Pfx();
		W3CTestCase.setConfigurationW3CXMLSample_Pfx2();
		// W3CTestCase.setConfigurationW3CEXIbyExample ( );
		// W3CTestCase.setConfigurationW3CPrimerNotebook();
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

	@Test
	public void testW3CEXIbyExample() throws Exception {
		// set up configuration
		setConfigurationW3CEXIbyExample();

		// execute test
		_test();
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

}
