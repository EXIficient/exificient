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

package com.siemens.ct.exi.main;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.Constants;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.main.data.GeneralTestCase;

// @SuppressWarnings("unused")
public class QuickTestConfiguration {
	// schema-informed / schema-less case
	public static final boolean USE_SCHEMA = true;
	public static boolean XSD_TYPES_ONLY = false; /* default: false */

	// encoding options: include Cookie, EXI Options, SchemaId
	public static final boolean INCLUDE_COOKIE = false;
	public static final boolean INCLUDE_OPTIONS = false;
	public static final boolean INCLUDE_PROFILE_VALUES = false;
	public static final boolean INCLUDE_SCHEMA_ID = false;
	public static final boolean RETAIN_ENTITY_REFERENCE = false;
	public static final boolean INCLUDE_XSI_SCHEMALOCATION = false;

	// fragments
	public static boolean FRAGMENTS = false;

	// coding mode
	public static CodingMode CODING_MODE;

	// input / output files
	static String XSD_FILE_LOCATION;
	static String XML_FILE_LOCATION;
	static String EXI_FILE_LOCATION;

	// Options
	public static FidelityOptions fidelityOptions;
	public static int blockSize = Constants.DEFAULT_BLOCK_SIZE;
	public static int valueMaxLength = Constants.DEFAULT_VALUE_MAX_LENGTH;
	public static int valuePartitionCapacity = Constants.DEFAULT_VALUE_PARTITON_CAPACITY;
	public static boolean localValuePartitions = true;
	public static int maximumNumberOfBuiltInElementGrammars = -1;
	public static int maximumNumberOfBuiltInProductions = -1;
	
	public static QName[] selfContainedElements;

	public static QName[] dtrMapTypes;
	public static QName[] dtrMapRepresentations;

	public static void setXsdLocation(String xsdLocation) {
		XSD_FILE_LOCATION = xsdLocation;
	}

	public static String getXsdLocation() {
		return XSD_FILE_LOCATION;
	}

	public static void setXmlLocation(String xmlLocation) {
		XML_FILE_LOCATION = xmlLocation;
	}

	public static String getXmlLocation() {
		return XML_FILE_LOCATION;
	}

	public static void setExiLocation(String exiLocation) {
		EXI_FILE_LOCATION = exiLocation;
	}

	public static String getExiLocation() {
		return EXI_FILE_LOCATION;
	}

	// ///////////////////////////////////////////////////
	// CODING MODE
	static {
		CODING_MODE = CodingMode.BIT_PACKED;
		// CODING_MODE = CodingMode.BYTE_PACKED;
		// CODING_MODE = CodingMode.PRE_COMPRESSION;
		// CODING_MODE = CodingMode.COMPRESSION;
	}

	// ///////////////////////////////////////////////////
	// FIDELITY OPTIONS
	static {
		// fidelityOptions = FidelityOptions.createDefault();
		// fidelityOptions = FidelityOptions.createStrict();
		fidelityOptions = FidelityOptions.createAll();

		// try {
		// fidelityOptions.setFidelity(FidelityOptions.FEATURE_DTD, true);
		// fidelityOptions.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE,
		// true);
		// fidelityOptions.setFidelity(FidelityOptions.FEATURE_SC, true);
		// fidelityOptions.setFidelity(FidelityOptions.FEATURE_PI, true);
		// fidelityOptions.setFidelity(FidelityOptions.FEATURE_DTD, true);
		// fidelityOptions.setFidelity(FidelityOptions.FEATURE_PREFIX, true);
		// } catch (UnsupportedOption e) {
		// }
	}

	// ///////////////////////////////////////////////////
	// OTHER OPTIONS
	static {
		// selfContainedElements = new QName[1];
		// selfContainedElements[0] = new QName("http://www.foo.com", "person");
		// selfContainedElements = new QName[1];
		// selfContainedElements[0] = new QName("", "note");
		// blockSize = 9013;
		// blockSize = 40;
		// blockSize = 200;
		// valueMaxLength = 0;
		// valuePartitionCapacity = 0;
		// localValuePartitions = false;
		// XSD_TYPES_ONLY = true;
		// maximumNumberOfBuiltInProductions = 0;
		// maximumNumberOfBuiltInElementGrammars = 0;
//		dtrMapTypes = new QName[1];
//		dtrMapTypes[0] = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
//				"decimal");
//		dtrMapRepresentations = new QName[1];
//		dtrMapRepresentations[0] = new QName(Constants.W3C_EXI_NS_URI, "string");
//		assert (dtrMapTypes.length == dtrMapRepresentations.length);
	}

	// ///////////////////////////////////////////////////
	// TEST CASE (GROUP)
	static {
		// SchemaTestCase.setupQuickTest();
		// BuiltInXSDTestCase.setupQuickTest ( );
		GeneralTestCase.setupQuickTest();
		// W3CTestCase.setupQuickTest();
		// FragmentTestCase.setupQuickTest ( );
		// DeviationsTestCase.setupQuickTest();
		// EXIOptionsHeaderTestCase.setupQuickTest ( );
	}

}
