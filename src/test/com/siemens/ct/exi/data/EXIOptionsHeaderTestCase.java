/*
 * Copyright (C) 2007-2011 Siemens AG
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

public class EXIOptionsHeaderTestCase extends AbstractTestCase {

	public EXIOptionsHeaderTestCase() {
		super("XML Test Cases");
	}

	public static void setupQuickTest() {
		// EXIOptionsHeaderTestCase.setConfigurationEXIOptionsHeaderStrict();
		// EXIOptionsHeaderTestCase.setConfigurationEXIOptionsHeaderCommon ( );
		// EXIOptionsHeaderTestCase.setConfigurationEXIOptionsHeaderLessCommon();
		EXIOptionsHeaderTestCase.setConfigurationEXIOptionsHeaderLessCommon2 ();
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

}
