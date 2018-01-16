/*
 * Copyright (c) 2007-2018 Siemens AG
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

public class FragmentTestCase extends AbstractTestCase {
	public FragmentTestCase() {
		super("Fragment Test Cases");
	}

	public static void setupQuickTest() {
		// QuickTestConfiguration.FRAGMENTS = true;

		// FragmentTestCase.setConfigurationFragment1 ( );
		// FragmentTestCase.setConfigurationFragment2();
		FragmentTestCase.setConfigurationFragment3a();
		// FragmentTestCase.setConfigurationFragment3b ( );
	}

	protected void setUp() {
		// #1 (default)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(true);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);

		// #2
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(true);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);

		// #3 (all)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(true);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(true);

		// #4
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(true);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(true);

		// #5 (strict)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(true);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);
		testCaseOptions.get(testCaseOptions.size()-1).setSchemaInformedOnly(true);

		// #6
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(true);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);
		testCaseOptions.get(testCaseOptions.size()-1).setSchemaInformedOnly(true);
	}

	@Test
	public void testFragment1() throws Exception {
		// set up configuration
		setConfigurationFragment1();

		// execute test
		_test();
	}

	public static void setConfigurationFragment1() {
		QuickTestConfiguration.setXsdLocation("./data/fragment/fragment.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/fragment/fragment1.xml.frag");
		QuickTestConfiguration
				.setExiLocation("./out/fragment/fragment1.xml.frag.exi");
	}

	public void testFragment2() throws Exception {
		// set up configuration
		setConfigurationFragment2();

		// execute test
		_test();
	}

	public static void setConfigurationFragment2() {
		QuickTestConfiguration.setXsdLocation("./data/fragment/fragment.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/fragment/fragment2.xml.frag");
		QuickTestConfiguration
				.setExiLocation("./out/fragment/fragment2.xml.frag.exi");
	}

	public void testFragment3a() throws Exception {
		// set up configuration
		setConfigurationFragment3a();

		// execute test
		_test();
	}

	public static void setConfigurationFragment3a() {
		QuickTestConfiguration.setXsdLocation("./data/fragment/fragment3.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/fragment/fragment3a.xml.frag");
		QuickTestConfiguration
				.setExiLocation("./out/fragment/fragment3a.xml.frag.exi");
	}

	public void testFragment3b() throws Exception {
		// set up configuration
		setConfigurationFragment3b();

		// execute test
		_test();
	}

	public static void setConfigurationFragment3b() {
		QuickTestConfiguration.setXsdLocation("./data/fragment/fragment3.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/fragment/fragment3b.xml.frag");
		QuickTestConfiguration
				.setExiLocation("./out/fragment/fragment3b.xml.frag.exi");
	}
}
