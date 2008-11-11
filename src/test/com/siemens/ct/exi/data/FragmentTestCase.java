/*
 * Copyright (C) 2007, 2008 Siemens AG
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

public class FragmentTestCase extends AbstractTestCase
{
	public FragmentTestCase ()
	{
		super ( "Fragment Test Cases" );
	}

	public static void setupQuickTest ()
	{
		QuickTestConfiguration.FRAGMENTS = true;
		
		// FragmentTestCase.setConfigurationFragment1 ( );
		FragmentTestCase.setConfigurationFragment2 ( );
	}

	protected void setUp ()
	{
		// #1 (default)
		testCaseOptions.add ( new TestCaseOption ( ) );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.BIT_PACKED );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createDefault ( ) );
		testCaseOptions.lastElement ( ).setFragments ( true );
		testCaseOptions.lastElement ( ).setXmlEqual ( false );

		// #2
		testCaseOptions.add ( new TestCaseOption ( ) );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.COMPRESSION );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createDefault ( ) );
		testCaseOptions.lastElement ( ).setFragments ( true );
		testCaseOptions.lastElement ( ).setXmlEqual ( false );

		// #3 (all)
		testCaseOptions.add ( new TestCaseOption ( ) );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.BIT_PACKED );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createAll ( ) );
		testCaseOptions.lastElement ( ).setFragments ( true );
		testCaseOptions.lastElement ( ).setXmlEqual ( true );

		// #4
		testCaseOptions.add ( new TestCaseOption ( ) );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.COMPRESSION );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createAll ( ) );
		testCaseOptions.lastElement ( ).setFragments ( true );
		testCaseOptions.lastElement ( ).setXmlEqual ( true );

		// #5 (strict)
		testCaseOptions.add ( new TestCaseOption ( ) );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.BIT_PACKED );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createStrict ( ) );
		testCaseOptions.lastElement ( ).setFragments ( true );
		testCaseOptions.lastElement ( ).setXmlEqual ( false );
		testCaseOptions.lastElement ( ).setSchemaInformedOnly ( true );

		// #6
		testCaseOptions.add ( new TestCaseOption ( ) );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.COMPRESSION );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createStrict ( ) );
		testCaseOptions.lastElement ( ).setFragments ( true );
		testCaseOptions.lastElement ( ).setXmlEqual ( false );
		testCaseOptions.lastElement ( ).setSchemaInformedOnly ( true );
	}

	@Test
	public void testFragment1 () throws Exception
	{
		// set up configuration
		setConfigurationFragment1 ( );

		// execute test
		_test ( );
	}

	public static void setConfigurationFragment1 ()
	{
		QuickTestConfiguration.setXsdLocation ( "./data/fragment/fragment.xsd" );
		QuickTestConfiguration.setXmlLocation ( "./data/fragment/fragment1.xml.frag" );
		QuickTestConfiguration.setExiLocation ( "./out/fragment/fragment1.xml.frag.exi" );
	}

	public void testFragment2 () throws Exception
	{
		// set up configuration
		setConfigurationFragment2 ( );

		// execute test
		_test ( );
	}

	public static void setConfigurationFragment2 ()
	{
		QuickTestConfiguration.setXsdLocation ( "./data/fragment/fragment.xsd" );
		QuickTestConfiguration.setXmlLocation ( "./data/fragment/fragment2.xml.frag" );
		QuickTestConfiguration.setExiLocation ( "./out/fragment/fragment2.xml.frag.exi" );
	}
}
