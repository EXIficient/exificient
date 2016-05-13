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

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.QuickTestConfiguration;

public class W3CTestCase extends AbstractTestCase {

	public W3CTestCase() {
		super("W3C Test Cases");
	}

	public static void setupQuickTest() {
		// W3CTestCase.setConfigurationW3CXMLSample ( );
		// W3CTestCase.setConfigurationW3CXMLSample_Pfx();
		// W3CTestCase.setConfigurationW3CXMLSample_Pfx2();
		// W3CTestCase.setConfigurationW3CEXIbyExample();
		W3CTestCase.setConfigurationW3CPrimerNotebook();
		// W3CTestCase.setConfigurationW3CXHTMLStrict1();
		// W3CTestCase.setConfigurationW3CXHTMLTransitional1();
		// W3CTestCase.setConfigurationW3CXsdWsdl1();
	}

	protected void setUp() throws Exception {
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

		// #7 (SelfContained)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		FidelityOptions scFo = FidelityOptions.createAll();
		scFo.setFidelity(FidelityOptions.FEATURE_SC, true);
		QName[] scElements = new QName[3];
		scElements[0] = new QName("", "note"); // notebook
		scElements[1] = new QName("", "body"); // notebook, nested
		scElements[2] = new QName("http://www.foo.com", "person"); // XMLSample
		testCaseOptions.get(testCaseOptions.size()-1).setSelfContainedElements(scElements);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(scFo);
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(true);

		// #8 valuePartitionCapacity
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createAll());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(true);
		testCaseOptions.get(testCaseOptions.size()-1).setValuePartitionCapacity(9);
		
		// #9 (default + shared strings)
		testCaseOptions.add(new TestCaseOption());
		testCaseOptions.get(testCaseOptions.size()-1).setCodingMode(CodingMode.BIT_PACKED);
		testCaseOptions.get(testCaseOptions.size()-1).setFidelityOptions(
				FidelityOptions.createDefault());
		testCaseOptions.get(testCaseOptions.size()-1).setFragments(false);
		testCaseOptions.get(testCaseOptions.size()-1).setXmlEqual(false);
		List<String> sharedStrings = Arrays.asList(new String[]{"EXI", "Do not forget it!", "shopping list", "milk, honey", "Boss", "worker"});
		testCaseOptions.get(testCaseOptions.size()-1).setSharedStrings(sharedStrings);
		
	}

	@Test
	public void testW3CEXIbyExample() throws Exception {
		// set up configuration
		setConfigurationW3CEXIbyExample();

		// execute test
		_test();
	}

	@Test
	public void testW3CXMLSample() throws Exception {
		// set up configuration
		setConfigurationW3CXMLSample();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXMLSample() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/XMLSample/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/XMLSample/XMLSample.xml");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/XMLSample/XMLSample.exi");
	}

	@Test
	public void testW3CXMLSample_Pfx() throws Exception {
		// set up configuration
		setConfigurationW3CXMLSample_Pfx();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXMLSample_Pfx() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/XMLSample/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/XMLSample/XMLSample_pfx.xml");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/XMLSample/XMLSample_pfx.exi");
	}

	@Test
	public void testW3CXMLSample_Pfx2() throws Exception {
		// set up configuration
		setConfigurationW3CXMLSample_Pfx2();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXMLSample_Pfx2() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/XMLSample/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/XMLSample/XMLSample_pfx2.xml");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/XMLSample/XMLSample_pfx2.exi");
	}

	public static void setConfigurationW3CEXIbyExample() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/EXIbyExample/XMLSample.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/EXIbyExample/XMLSample.xml");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/EXIbyExample/XMLSample.exi");
	}

	@Test
	public void testW3CPrimerNotebook() throws Exception {
		// set up configuration
		setConfigurationW3CPrimerNotebook();

		// execute test
		_test();
	}

	public static void setConfigurationW3CPrimerNotebook() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/PrimerNotebook/notebook.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/PrimerNotebook/notebook.xml");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/PrimerNotebook/notebook.exi");
	}

	/*
	 * XHTML examples
	 */

	@Test
	public void xtestW3CXHTMLStrict1() throws Exception {
		// set up configuration
		setConfigurationW3CXHTMLStrict1();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXHTMLStrict1() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/xhtml/xhtml1-strict.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/xhtml/www.w3.org.htm");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/xhtml/www.w3.org.htm.exi");
	}

	@Test
	public void xtestW3CXHTMLTransitional1() throws Exception {
		// set up configuration
		setConfigurationW3CXHTMLTransitional1();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXHTMLTransitional1() {
		QuickTestConfiguration
				.setXsdLocation("./data/W3C/xhtml/xhtml1-transitional.xsd");
		QuickTestConfiguration
				.setXmlLocation("./data/W3C/xhtml/en.wikipedia.org-wiki-EXI.htm");
		QuickTestConfiguration
				.setExiLocation("./out/W3C/xhtml/en.wikipedia.org-wiki-EXI.htm.exi");
	}

	@Test
	public void xtestW3CXsdWsdl1() throws Exception {
		// set up configuration
		setConfigurationW3CXsdWsdl1();

		// execute test
		_test();
	}

	public static void setConfigurationW3CXsdWsdl1() {
		QuickTestConfiguration.setXsdLocation("./data/W3C/xsd/XMLSchema.xsd");
		QuickTestConfiguration.setXmlLocation("./data/W3C/xsd/wsdl.xsd");
		QuickTestConfiguration.setExiLocation("./out/W3C/xsd/wsdl.xsd.exi");
	}

}
