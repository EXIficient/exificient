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

package com.siemens.ct.exi;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.data.W3CTestCase;
import com.siemens.ct.exi.exceptions.UnsupportedOption;

// @SuppressWarnings("unused")
public class QuickTestConfiguration {
	// schema-informed / schema-less case
	public static final boolean USE_SCHEMA = true;
	
	// profile
	public static final String PROFILE = null;
	// public static final String PROFILE = EXIFactory.ULTRA_CONSTRAINED_DEVICE_PROFILE;
	
	
	// encoding options: include Cookie, EXI Options, SchemaId
	public static final boolean INCLUDE_COOKIE = false;
	public static final boolean INCLUDE_OPTIONS = false;
	public static final boolean INCLUDE_SCHEMA_ID = false;
	public static final boolean RETAIN_ENTITY_REFERENCE = false;

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
		fidelityOptions = FidelityOptions.createDefault();
		// fidelityOptions = FidelityOptions.createStrict();
		// fidelityOptions = FidelityOptions.createAll();
		try {
//			fidelityOptions.setFidelity(FidelityOptions.FEATURE_DTD, true);
//			fidelityOptions.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, true);
// 			fidelityOptions.setFidelity(FidelityOptions.FEATURE_SC, true);
 			fidelityOptions.setFidelity(FidelityOptions.FEATURE_PI, true);
//			fidelityOptions.setFidelity(FidelityOptions.FEATURE_DTD, true);
			fidelityOptions.setFidelity(FidelityOptions.FEATURE_PREFIX, true);
		} catch (UnsupportedOption e) {
		}
	}
	
	// ///////////////////////////////////////////////////
	// OTHER OPTIONS
	static {
//		selfContainedElements = new QName[1];
//		selfContainedElements[0] = new QName("http://www.foo.com", "person");
		// blockSize = 9013;
		// blockSize = 40;
//		blockSize = 200;
//		valueMaxLength = 0;
//		valuePartitionCapacity = 5;
//		dtrMapTypes = new QName[1];
//		dtrMapTypes[0] = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "decimal");
//		dtrMapRepresentations = new QName[1];
//		dtrMapRepresentations[0] = new QName(Constants.W3C_EXI_NS_URI, "string");
//		assert(dtrMapTypes.length == dtrMapRepresentations.length);
	}
	
	// ///////////////////////////////////////////////////
	// TEST CASE (GROUP)
	static {
		// SchemaTestCase.setupQuickTest ( );
		// BuiltInXSDTestCase.setupQuickTest ( );
		// GeneralTestCase.setupQuickTest();
		W3CTestCase.setupQuickTest();
		// FragmentTestCase.setupQuickTest ( );
		// DeviationsTestCase.setupQuickTest();
		// EXIOptionsHeaderTestCase.setupQuickTest ( );
	}

}
