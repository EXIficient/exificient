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
