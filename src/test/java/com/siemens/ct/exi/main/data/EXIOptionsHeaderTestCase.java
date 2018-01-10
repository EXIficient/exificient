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

package com.siemens.ct.exi.main.data;

import org.junit.Test;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.main.QuickTestConfiguration;

public class EXIOptionsHeaderTestCase extends AbstractTestCase {

	public EXIOptionsHeaderTestCase() {
		super("XML Test Cases");
	}

	public static void setupQuickTest() {
		// EXIOptionsHeaderTestCase.setConfigurationEXIOptionsHeaderStrict();
		// EXIOptionsHeaderTestCase.setConfigurationEXIOptionsHeaderCommon ( );
		// EXIOptionsHeaderTestCase.setConfigurationEXIOptionsHeaderLessCommon();
		EXIOptionsHeaderTestCase.setConfigurationEXIOptionsHeaderLessCommon2();
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
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);

		// #3 (all)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(true);

		// #4
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(true);

		// #5 (strict)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);
		testCaseOptions.get(testCaseOptions.size()-1).setSchemaInformedOnly(true);

		// #6
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);
		testCaseOptions.get(testCaseOptions.size()-1).setSchemaInformedOnly(true);
	}

	@Test
	public void testEXIOptionsHeaderStrict() throws Exception {
		// set up configuration
		setConfigurationEXIOptionsHeaderStrict();

		// execute test
		_test();
	}

	public static void setConfigurationEXIOptionsHeaderStrict() {
		QuickTestConfiguration
				.setXsdLocation("./data/EXIOptionsHeader/EXIOptionsHeader.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/EXIOptionsHeader/EXIOptionsHeaderStrict.xml");
		QuickTestConfiguration
				.setExiLocation("./out/EXIOptionsHeader/EXIOptionsHeaderStrict.exi");
	}

	@Test
	public void testEXIOptionsHeaderCommon() throws Exception {
		// set up configuration
		setConfigurationEXIOptionsHeaderCommon();

		// execute test
		_test();
	}

	public static void setConfigurationEXIOptionsHeaderCommon() {
		QuickTestConfiguration
				.setXsdLocation("./data/EXIOptionsHeader/EXIOptionsHeader.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/EXIOptionsHeader/EXIOptionsHeaderCommon.xml");
		QuickTestConfiguration
				.setExiLocation("./out/EXIOptionsHeader/EXIOptionsHeaderCommon.exi");
	}

	@Test
	public void testEXIOptionsHeaderLessCommon() throws Exception {
		// set up configuration
		setConfigurationEXIOptionsHeaderLessCommon();

		// execute test
		_test();
	}

	public static void setConfigurationEXIOptionsHeaderLessCommon() {
		QuickTestConfiguration
				.setXsdLocation("./data/EXIOptionsHeader/EXIOptionsHeader.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/EXIOptionsHeader/EXIOptionsHeaderLessCommon.xml");
		QuickTestConfiguration
				.setExiLocation("./out/EXIOptionsHeader/EXIOptionsHeaderLessCommon.exi");
	}

	@Test
	public void testEXIOptionsHeaderLessCommon2() throws Exception {
		// set up configuration
		setConfigurationEXIOptionsHeaderLessCommon2();

		// execute test
		_test();
	}

	public static void setConfigurationEXIOptionsHeaderLessCommon2() {
		QuickTestConfiguration
				.setXsdLocation("./data/EXIOptionsHeader/EXIOptionsHeader.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/EXIOptionsHeader/EXIOptionsHeaderLessCommon2.xml");
		QuickTestConfiguration
				.setExiLocation("./out/EXIOptionsHeader/EXIOptionsHeaderLessCommon2.xml.exi");
	}

	@Test
	public void testEXIOptionsHeaderProfile1() throws Exception {
		// set up configuration
		setConfigurationEXIOptionsHeaderProfile1();

		// execute test
		_test();
	}

	public static void setConfigurationEXIOptionsHeaderProfile1() {
		QuickTestConfiguration
				.setXsdLocation("./data/EXIOptionsHeader/EXIOptionsHeader.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/EXIOptionsHeader/EXIOptionsHeaderProfile1.xml");
		QuickTestConfiguration
				.setExiLocation("./out/EXIOptionsHeader/EXIOptionsHeaderProfile1.xml.exi");
	}

	@Test
	public void testEXIOptionsHeaderProfile2() throws Exception {
		// set up configuration
		setConfigurationEXIOptionsHeaderProfile2();

		// execute test
		_test();
	}

	public static void setConfigurationEXIOptionsHeaderProfile2() {
		QuickTestConfiguration
				.setXsdLocation("./data/EXIOptionsHeader/EXIOptionsHeader.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/EXIOptionsHeader/EXIOptionsHeaderProfile2.xml");
		QuickTestConfiguration
				.setExiLocation("./out/EXIOptionsHeader/EXIOptionsHeaderProfile2.xml.exi");
	}

}
