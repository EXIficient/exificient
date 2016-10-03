/*
 * Copyright (c) 2007-2016 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
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
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);

		// #2
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BYTE_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);

		// #3
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.PRE_COMPRESSION);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);

		// #4
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);

		// #5 (all)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(true);

		// #6
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(true);

		// #7 (strict)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);
		testCaseOptions.get(testCaseOptions.size()-1).setSchemaInformedOnly(true);

		// #8
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);
		testCaseOptions.get(testCaseOptions.size()-1).setSchemaInformedOnly(true);
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
