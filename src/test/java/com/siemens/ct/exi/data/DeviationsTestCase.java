/*
 * Copyright (c) 2007-2015 Siemens AG
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

public class DeviationsTestCase extends AbstractTestCase {

	public DeviationsTestCase() {
		super("Deviations Test Cases");
	}

	public static void setupQuickTest() {
		// DeviationsTestCase.setConfigurationDeviationAt1 ( );
		// DeviationsTestCase.setConfigurationDeviationEl1 ( );
		// DeviationsTestCase.setConfigurationDeviationDatatype1 ( );
		// DeviationsTestCase.setConfigurationDeviationDatatype2 ( );
		// DeviationsTestCase.setConfigurationDeviationDatatype3();
		// DeviationsTestCase.setConfigurationDeviationXsiType( );
		DeviationsTestCase.setConfigurationDeviationXsiNil();
		// DeviationsTestCase.setConfigurationDeviationGlobalAttribute1();
		// DeviationsTestCase.setConfigurationDeviationInvalidQName();
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
		
		// #5 (default + non-evolving grammars)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);
		testCaseOptions.get(testCaseOptions.size()-1).setUsingNonEvolvingGrammars(true);

	}

	@Test
	public void testDeviationInvalidQName() throws Exception {
		// set up configuration
		setConfigurationDeviationInvalidQName();

		// execute test
		_test();
	}

	public static void setConfigurationDeviationAt1() {
		QuickTestConfiguration
				.setXsdLocation("./data/deviations/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/deviations/XMLSampleDevAt1.xml");
		QuickTestConfiguration
				.setExiLocation("./out/deviations/XMLSampleDevAt1.exi");
	}

	@Test
	public void testDeviationEl1() throws Exception {
		// set up configuration
		setConfigurationDeviationEl1();

		// execute test
		_test();
	}

	public static void setConfigurationDeviationEl1() {
		QuickTestConfiguration
				.setXsdLocation("./data/deviations/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/deviations/XMLSampleDevEl1.xml");
		QuickTestConfiguration
				.setExiLocation("./out/deviations/XMLSampleDevEl1.exi");
	}

	@Test
	public void testDeviationDatatype1() throws Exception {
		// set up configuration
		setConfigurationDeviationDatatype1();

		// execute test
		_test();
	}

	public static void setConfigurationDeviationDatatype1() {
		QuickTestConfiguration
				.setXsdLocation("./data/deviations/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/deviations/XMLSampleDevDatatype1.xml");
		QuickTestConfiguration
				.setExiLocation("./out/deviations/XMLSampleDevDatatype1.exi");
	}

	@Test
	public void testDeviationDatatype2() throws Exception {
		// set up configuration
		setConfigurationDeviationDatatype2();

		// execute test
		_test();
	}

	public static void setConfigurationDeviationDatatype2() {
		QuickTestConfiguration
				.setXsdLocation("./data/deviations/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/deviations/XMLSampleDevDatatype2.xml");
		QuickTestConfiguration
				.setExiLocation("./out/deviations/XMLSampleDevDatatype2.exi");
	}

	@Test
	public void testDeviationDatatype3() throws Exception {
		// set up configuration
		setConfigurationDeviationDatatype3();

		// execute test
		_test();
	}

	public static void setConfigurationDeviationDatatype3() {
		QuickTestConfiguration.setXsdLocation("./data/deviations/order.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/deviations/orderDevDatatype3.xml");
		QuickTestConfiguration
				.setExiLocation("./out/deviations/orderDevDatatype3.exi");
	}

	@Test
	public void testDeviationXsiType() throws Exception {
		// set up configuration
		setConfigurationDeviationXsiType();

		// execute test
		_test();
	}

	public static void setConfigurationDeviationXsiType() {
		QuickTestConfiguration
				.setXsdLocation("./data/deviations/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/deviations/XMLSampleDevXsiType.xml");
		QuickTestConfiguration
				.setExiLocation("./out/deviations/XMLSampleDevXsiType.xml.exi");
	}

	@Test
	public void testDeviationXsiNil() throws Exception {
		// set up configuration
		setConfigurationDeviationXsiNil();

		// execute test
		_test();
	}

	public static void setConfigurationDeviationXsiNil() {
		QuickTestConfiguration
				.setXsdLocation("./data/deviations/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/deviations/XMLSampleDevXsiNil.xml");
		QuickTestConfiguration
				.setExiLocation("./out/deviations/XMLSampleDevXsiNil.xml.exi");
	}

	@Test
	public void testDeviationGlobalAttribute1() throws Exception {
		// set up configuration
		setConfigurationDeviationGlobalAttribute1();

		// execute test
		_test();
	}

	public static void setConfigurationDeviationGlobalAttribute1() {
		QuickTestConfiguration
				.setXsdLocation("./data/deviations/globalAttribute.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/deviations/globalAttributeDev1.xml");
		QuickTestConfiguration
				.setExiLocation("./out/deviations/globalAttributeDev1.xml.exi");
	}

	public static void setConfigurationDeviationInvalidQName() {
		QuickTestConfiguration
				.setXsdLocation("./data/deviations/invalidQName.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/deviations/invalidQName.xml");
		QuickTestConfiguration
				.setExiLocation("./out/deviations/invalidQName.xml.exi");
	}

}
