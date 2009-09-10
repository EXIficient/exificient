/*
 * Copyright (C) 2007-2009 Siemens AG
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

import com.siemens.ct.exi.data.DeviationsTestCase;
import com.siemens.ct.exi.data.FragmentTestCase;
import com.siemens.ct.exi.data.GeneralTestCase;
import com.siemens.ct.exi.data.SchemaTestCase;
import com.siemens.ct.exi.data.W3CTestCase;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.types.DatatypeRepresentation;

@SuppressWarnings("unused")
public class QuickTestConfiguration {
	// schema-informed / schema-less case
	public static final boolean USE_SCHEMA = true;

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
	public static DatatypeRepresentation[] datatypeRepresentations;

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
		// fidelityOptions = FidelityOptions.createStrict ( );
		// fidelityOptions = FidelityOptions.createAll();
	}

	// ///////////////////////////////////////////////////
	// TEST CASE (GROUP)
	static {
		// SchemaTestCase.setupQuickTest ( );
		// GeneralTestCase.setupQuickTest();
		W3CTestCase.setupQuickTest();
		// FragmentTestCase.setupQuickTest ( );
		// DeviationsTestCase.setupQuickTest();
		// EXIOptionsHeaderTestCase.setupQuickTest ( );
	}

}
