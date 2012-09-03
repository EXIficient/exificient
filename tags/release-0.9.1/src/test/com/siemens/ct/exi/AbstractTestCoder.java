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

package com.siemens.ct.exi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public abstract class AbstractTestCoder {
	protected static GrammarFactory grammarFactory;

	static {
		grammarFactory = GrammarFactory.newInstance();
	}

	public AbstractTestCoder() {
	}

	protected static EXIFactory getQuickTestEXIactory() throws Exception {
		EXIFactory ef;
		if (QuickTestConfiguration.USE_SCHEMA) {
			ef = getFactorySchema();
		} else {
			ef = getFactoryNoSchema();
		}
		
		setupEncodingOptions(ef);
		
		return ef;
	}
	
	protected static void setupEncodingOptions(EXIFactory ef) throws UnsupportedOption {
		if (QuickTestConfiguration.INCLUDE_COOKIE) {
			ef.getEncodingOptions().setOption(EncodingOptions.INCLUDE_COOKIE);
		}
		if (QuickTestConfiguration.INCLUDE_OPTIONS) {
			ef.getEncodingOptions().setOption(EncodingOptions.INCLUDE_OPTIONS);
		}
		if (QuickTestConfiguration.INCLUDE_SCHEMA_ID) {
			ef.getEncodingOptions().setOption(EncodingOptions.INCLUDE_SCHEMA_ID);
		}
		if (QuickTestConfiguration.RETAIN_ENTITY_REFERENCE) {
			ef.getEncodingOptions().setOption(EncodingOptions.RETAIN_ENTITY_REFERENCE);
		}
		if (QuickTestConfiguration.INCLUDE_XSI_SCHEMALOCATION) {
			ef.getEncodingOptions().setOption(EncodingOptions.INCLUDE_XSI_SCHEMALOCATION);
		}
	}
	

	protected static EXIFactory getFactory() {
		return DefaultEXIFactory.newInstance();
	}

	protected static EXIFactory getFactoryNoSchema() throws UnsupportedOption {
		EXIFactory ef = getFactory();
		ef.setCodingMode(QuickTestConfiguration.CODING_MODE);
		ef.setFidelityOptions(QuickTestConfiguration.fidelityOptions);
		ef.setBlockSize(QuickTestConfiguration.blockSize);
		if (QuickTestConfiguration.valueMaxLength != Constants.DEFAULT_VALUE_MAX_LENGTH) {
			ef.setValueMaxLength(QuickTestConfiguration.valueMaxLength);
		}
		if (QuickTestConfiguration.valuePartitionCapacity != Constants.DEFAULT_VALUE_PARTITON_CAPACITY) {
			ef.setValuePartitionCapacity(QuickTestConfiguration.valuePartitionCapacity);
		}
		ef.setLocalValuePartitions(QuickTestConfiguration.localValuePartitions);
		ef.setMaximumNumberOfBuiltInProductions(QuickTestConfiguration.maximumNumberOfBuiltInProductions);
		ef.setMaximumNumberOfEvolvingBuiltInElementGrammars(QuickTestConfiguration.maximumNumberOfEvolvingBuiltInElementGrammars);
		// ef.setProfile(QuickTestConfiguration.PROFILE);
		ef.setFragment(QuickTestConfiguration.FRAGMENTS);
		ef.setSelfContainedElements(QuickTestConfiguration.selfContainedElements);
		ef.setDatatypeRepresentationMap(QuickTestConfiguration.dtrMapTypes, QuickTestConfiguration.dtrMapRepresentations);

		return ef;
	}

	protected static EXIFactory getFactorySchema() throws EXIException {
		EXIFactory ef = getFactoryNoSchema();
		String xsdLocation = QuickTestConfiguration.getXsdLocation();
		if (QuickTestConfiguration.XSD_TYPES_ONLY || xsdLocation == null || xsdLocation.length() == 0) {
			ef.setGrammars(getXSDTypesOnlyGrammar());
		} else {
			ef.setGrammars(getGrammar(xsdLocation));	
		}

		return ef;
	}

	public static Grammars getXSDTypesOnlyGrammar() throws EXIException {
		return grammarFactory.createXSDTypesOnlyGrammars();
	}
	
	public static Grammars getGrammar(String xsdLocation) throws EXIException {
		if (xsdLocation == null) {
			return grammarFactory.createSchemaLessGrammars();
		} else {
			return grammarFactory.createGrammars(xsdLocation);	
		}
	}

	protected static OutputStream getOutputStream(String exiLocation)
			throws FileNotFoundException {
		File fileEXI = new File(exiLocation);

		File path = fileEXI.getParentFile();
		if (!path.exists()) {
			boolean bool = path.mkdirs();
			assert(bool);
		}

		return new BufferedOutputStream(new FileOutputStream(fileEXI));
	}
}
