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

import javax.xml.namespace.QName;

import org.junit.Test;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.main.QuickTestConfiguration;

public class BugTestCase extends AbstractTestCase {

	public BugTestCase() {
		super("Bug Test Cases");
	}

	public static void setupQuickTest() {
		setConfigurationBug29();
	}

	protected void setUp() throws Exception {
		// #1 (default)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size() - 1).setCodingMode(
				CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size() - 1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size() - 1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size() - 1).setXmlEqual(false);

		// #2
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size() - 1).setCodingMode(
				CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size() - 1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size() - 1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size() - 1).setXmlEqual(false);

		// #3 (all)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size() - 1).setCodingMode(
				CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size() - 1).setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.get(testCaseOptions.size() - 1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size() - 1).setXmlEqual(true);

		// #4
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size() - 1).setCodingMode(
				CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size() - 1).setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.get(testCaseOptions.size() - 1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size() - 1).setXmlEqual(true);

		// #5 (strict)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size() - 1).setCodingMode(
				CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size() - 1).setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.get(testCaseOptions.size() - 1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size() - 1).setXmlEqual(false);
		testCaseOptions.get(testCaseOptions.size() - 1).setSchemaInformedOnly(
				true);

		// #6
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size() - 1).setCodingMode(
				CodingMode.COMPRESSION);
		testCaseOptions.get(testCaseOptions.size() - 1).setFidelityOptions(
				FidelityOptions.createStrict());
		testCaseOptions.get(testCaseOptions.size() - 1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size() - 1).setXmlEqual(false);
		testCaseOptions.get(testCaseOptions.size() - 1).setSchemaInformedOnly(
				true);

		// #7 (SelfContained)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size() - 1).setCodingMode(
				CodingMode.BIT_PACKED);
		FidelityOptions scFo = FidelityOptions.createAll();
		scFo.setFidelity(FidelityOptions.FEATURE_SC, true);
		QName[] scElements = new QName[3];
		scElements[0] = new QName("", "note"); // notebook
		scElements[1] = new QName("", "body"); // notebook, nested
		scElements[2] = new QName("http://www.foo.com", "person"); // XMLSample
		testCaseOptions.get(testCaseOptions.size() - 1)
				.setSelfContainedElements(scElements);
		testCaseOptions.get(testCaseOptions.size() - 1)
				.setFidelityOptions(scFo);
		testCaseOptions.get(testCaseOptions.size() - 1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size() - 1).setXmlEqual(true);

		// #8 valuePartitionCapacity
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size() - 1).setCodingMode(
				CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size() - 1).setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.get(testCaseOptions.size() - 1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size() - 1).setXmlEqual(true);
		testCaseOptions.get(testCaseOptions.size() - 1)
				.setValuePartitionCapacity(9);

	}

	@Test
	public void testBug29() throws Exception {
		// set up configuration
		setConfigurationBug29();

		// execute test
		_test();
	}

	public static void setConfigurationBug29() {
		QuickTestConfiguration
				.setXsdLocation("./data/bugs/ID29/main/calendar.xsd");
		QuickTestConfiguration.setXmlLocation("./data/bugs/ID29/instance1.xml");
		QuickTestConfiguration.setExiLocation("./out/bugs/ID29/instance1.exi");
	}

	@Test
	/*
	 * Using the EXIficient GUI, with Alignment set to "COMPRESSED" I can create
	 * an exi file from the attached XML. However it is unable to decode the
	 * created file back into XML, getting an error message about
	 * "invalid stored block lengths".
	 * 
	 * I have tried decoding the generated EXI file using the OpenEXI library
	 * and it works there, so I think the generated EXI file is ok, and it is
	 * probably a problem with the way EXIficient is decoding it.
	 */
	public void testBug33() throws Exception {
		// set up configuration
		setConfigurationBug33();

		// execute test
		_test();
	}

	public static void setConfigurationBug33() {
		QuickTestConfiguration.setXsdLocation(null);
		QuickTestConfiguration.setXmlLocation("./data/bugs/ID33/useme.xml");
		QuickTestConfiguration.setExiLocation("./out/bugs/ID33/useme.exi");
	}

}
