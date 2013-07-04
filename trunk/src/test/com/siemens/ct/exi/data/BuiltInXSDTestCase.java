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

import org.junit.Test;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.QuickTestConfiguration;

public class BuiltInXSDTestCase extends AbstractTestCase {
	public BuiltInXSDTestCase() {
		super("BuiltInXSD Test Cases");
	}

	public static void setupQuickTest() {
		// setConfigurationBuiltInXSDIntVal ( );
		setConfigurationBuiltInXSDIntVal2();
		// setConfigurationBuiltInXSDFloatVal ( );
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

		// #8
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.lastElement().setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.lastElement().setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.lastElement().setFragments(false);
		testCaseOptions.lastElement().setXmlEqual(false);
		testCaseOptions.lastElement().setSchemaInformedOnly(true);
	}

	@Test
	public void testBuiltInXSDIntVal() throws Exception {
		// set up configuration
		setConfigurationBuiltInXSDIntVal();

		// execute test
		_test();
	}

	public static void setConfigurationBuiltInXSDIntVal() {
		QuickTestConfiguration.setXsdLocation(""); // built-in XSD
		QuickTestConfiguration.setXmlLocation("./data/builtInXSD/intVal.xml");
		QuickTestConfiguration
				.setExiLocation("./out/builtInXSD/intVal.xml.exi");
	}

	@Test
	public void testBuiltInXSDIntVal2() throws Exception {
		// set up configuration
		setConfigurationBuiltInXSDIntVal2();

		// execute test
		_test();
	}

	public static void setConfigurationBuiltInXSDIntVal2() {
		QuickTestConfiguration.setXsdLocation(""); // built-in XSD
		// QuickTestConfiguration.setXsdLocation("./data/builtInXSD/intVal2.xsd");
		QuickTestConfiguration.setXmlLocation("./data/builtInXSD/intVal2.xml");
		QuickTestConfiguration
				.setExiLocation("./out/builtInXSD/intVal2.xml.exi");
	}

	@Test
	public void testBuiltInXSDFloatVal() throws Exception {
		// set up configuration
		setConfigurationBuiltInXSDFloatVal();

		// execute test
		_test();
	}

	public static void setConfigurationBuiltInXSDFloatVal() {
		QuickTestConfiguration.setXsdLocation(""); // built-in XSD
		QuickTestConfiguration.setXmlLocation("./data/builtInXSD/floatVal.xml");
		QuickTestConfiguration
				.setExiLocation("./out/builtInXSD/floatVal.xml.exi");
	}

}
