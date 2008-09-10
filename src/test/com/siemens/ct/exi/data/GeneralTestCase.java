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

public class GeneralTestCase extends AbstractTestCase
{
	public GeneralTestCase ()
	{
		super ( "General Test Cases" );
	}
	
	public static void setupQuickTest ()
	{
		// GeneralTestCase.setConfigurationPerson ( );
		// GeneralTestCase.setConfigurationPersonAdjusted ( );
		// GeneralTestCase.setConfigurationPersonal ( );
		// GeneralTestCase.setConfigurationUnbounded ( );
		// GeneralTestCase.setConfigurationDatatypes ( );
		// GeneralTestCase.setConfigurationDatatypes2 ( );
		// GeneralTestCase.setConfigurationOrder ( );
		// GeneralTestCase.setConfigurationRandj ( );
		// GeneralTestCase.setConfigurationPurchaseOrder ( );
		// GeneralTestCase.setConfigurationTest1 ( );
		// GeneralTestCase.setConfigurationTest2 ( );
		// GeneralTestCase.setConfigurationTest3 ( );
		// GeneralTestCase.setConfigurationTest4 ( );
		// GeneralTestCase.setConfigurationTest5 ( );
		// GeneralTestCase.setConfigurationComplexStructure ( );
		// GeneralTestCase.setConfigurationSimpleContent ( );
		// GeneralTestCase.setConfigurationEmptyContent ( );
		// GeneralTestCase.setConfigurationAttributes ( );
		// GeneralTestCase.setConfigurationPI1 ( );
		GeneralTestCase.setConfigurationDocType ( );
	}

	protected void setUp ()
	{	
		//	#1 (default)
		testCaseOptions.add ( new TestCaseOption() );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.BIT_PACKED );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createDefault() );
		testCaseOptions.lastElement ( ).setFragments ( false );
		testCaseOptions.lastElement ( ).setXmlEqual ( false );
		
		//	#2
		testCaseOptions.add ( new TestCaseOption() );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.COMPRESSION );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createDefault() );
		testCaseOptions.lastElement ( ).setFragments ( false );
		testCaseOptions.lastElement ( ).setXmlEqual ( false );
		
		//	#3 (all)
		testCaseOptions.add ( new TestCaseOption() );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.BIT_PACKED );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createAll() );
		testCaseOptions.lastElement ( ).setFragments ( false );
		testCaseOptions.lastElement ( ).setXmlEqual ( true );
		
		//	#4
		testCaseOptions.add ( new TestCaseOption() );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.COMPRESSION );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createAll() );
		testCaseOptions.lastElement ( ).setFragments ( false );
		testCaseOptions.lastElement ( ).setXmlEqual ( true );
		
		//	#5 (strict)
		testCaseOptions.add ( new TestCaseOption() );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.BIT_PACKED );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createStrict ( ) );
		testCaseOptions.lastElement ( ).setFragments ( false );
		testCaseOptions.lastElement ( ).setXmlEqual ( false );
		testCaseOptions.lastElement ( ).setSchemaInformedOnly ( true );
		
		//	#6
		testCaseOptions.add ( new TestCaseOption() );
		testCaseOptions.lastElement ( ).setCodingMode ( CodingMode.COMPRESSION );
		testCaseOptions.lastElement ( ).setFidelityOptions ( FidelityOptions.createStrict ( ) );
		testCaseOptions.lastElement ( ).setFragments ( false );
		testCaseOptions.lastElement ( ).setXmlEqual ( false );
		testCaseOptions.lastElement ( ).setSchemaInformedOnly ( true );
	}

		
	@Test
	public void testPerson () throws Exception
	{
		// set up configuration
		setConfigurationPerson ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationPerson ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/person.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/person.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/person.exi" );
	}
	

	@Test
	public void testPersonAdjusted () throws Exception
	{
		// set up configuration
		setConfigurationPersonAdjusted ( );
		
		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationPersonAdjusted ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/person.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/person_adjusted.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/person_adjusted.exi" );
	}
	
	@Test
	public void testPersonal() throws Exception
	{
		// set up configuration
		setConfigurationPersonal ( );
		
		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationPersonal ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/personal.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/personal.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/personal.exi" );
	}

	@Test
	public void testUnbounded () throws Exception
	{
		// set up configuration
		setConfigurationUnbounded ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationUnbounded ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/unbounded.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/unbounded.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/unbounded.exi" );
	}

	@Test
	public void testDatatypes () throws Exception
	{
		// set up configuration
		setConfigurationDatatypes ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationDatatypes ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/datatypes.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/datatypes.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/datatypes.exi" );
	}

	@Test
	public void testDatatypes2 () throws Exception
	{
		// set up configuration
		setConfigurationDatatypes2 ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationDatatypes2 ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/datatypes2.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/datatypes2.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/datatypes2.exi" );
	}

	@Test
	public void testOrder () throws Exception
	{
		// set up configuration
		setConfigurationOrder ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationOrder ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/order.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/order.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/order.exi" );
	}

	@Test
	public void testComplexStructure () throws Exception
	{
		// set up configuration
		setConfigurationComplexStructure ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationComplexStructure ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/complex-structure.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/complex-structure.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/complex-structure.exi" );
	}


	@Test
	public void testSimpleContent () throws Exception
	{
		// set up configuration
		setConfigurationSimpleContent ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationSimpleContent ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/simpleContent.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/simpleContent.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/simpleContent.xml.exi" );
	}

	@Test
	public void testEmptyContent () throws Exception
	{
		// set up configuration
		setConfigurationEmptyContent ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationEmptyContent ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/emptyContent.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/emptyContent.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/emptyContent.xml.exi" );
	}

	@Test
	public void testAttributes () throws Exception
	{
		// set up configuration
		setConfigurationAttributes ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationAttributes ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/attributes.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/attributes.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/attributes.xml.exi" );
	}
	
	@Test
	public void testPostalRandj () throws Exception
	{
		// set up configuration
		setConfigurationRandj ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationRandj ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/randj.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/randj.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/randj.exi" );
	}
	
	@Test
	public void testPurchaseOrder () throws Exception
	{
		// set up configuration
		setConfigurationPurchaseOrder ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationPurchaseOrder ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/po.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/po.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/po.xml.exi" );
	}

	@Test
	public void testXMLTest1 () throws Exception
	{
		// set up configuration
		setConfigurationTest1 ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationTest1 ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/test1.xsd" ); 
		QuickTestConfiguration.setXmlLocation( "./data/general/test1.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/test1.exi" );
	}

	@Test
	public void testXMLTest2 () throws Exception
	{
		// set up configuration
		setConfigurationTest2 ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationTest2 ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/test2.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/test2.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/test2.exi" );
	}

	@Test
	public void testXMLTest3 () throws Exception
	{
		// set up configuration
		setConfigurationTest3 ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationTest3 ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/test3.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/test3.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/test3.exi" );
	}
	
	@Test
	public void testXMLTest4 () throws Exception
	{
		// set up configuration
		setConfigurationTest4 ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationTest4 ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/test4.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/test4.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/test4.xml.exi" );
	}
	
	
	@Test
	public void testXMLTest5 () throws Exception
	{
		// set up configuration
		setConfigurationTest5 ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationTest5 ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/test5.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/test5.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/test5.xml.exi" );
	}
	
	
	@Test
	public void testPI1 () throws Exception
	{
		// set up configuration
		setConfigurationPI1 ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationPI1 ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/pi1.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/pi1.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/pi1.xml.exi" );
	}

	@Test
	public void XXtestDocType () throws Exception
	{
		// set up configuration
		setConfigurationDocType ( );

		//	execute test
		_test( );
	}

	@SuppressWarnings("unused")
	public static void setConfigurationDocType ()
	{
		QuickTestConfiguration.setXsdLocation( "./data/general/empty.xsd" );
		QuickTestConfiguration.setXmlLocation( "./data/general/docType.xml" );
		QuickTestConfiguration.setExiLocation( "./out/general/docType.xml.exi" );
	}
	
}
