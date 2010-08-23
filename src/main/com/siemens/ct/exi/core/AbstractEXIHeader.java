/*
 * Copyright (C) 2007-2010 Siemens AG
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

package com.siemens.ct.exi.core;

import java.io.InputStream;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

/**
 * EXI Header (see http://www.w3.org/TR/exi/#header)
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public abstract class AbstractEXIHeader {

	/* system resource for EXI Options XML schema document */
	public static final String RESOURCE_EXI_OPTIONS_XSD = "EXIOptionsHeader.xsd";

	public static final String HEADER = "header";
	public static final String LESSCOMMON = "lesscommon";
	public static final String UNCOMMON = "uncommon";
	public static final String ALIGNMENT = "alignment";
	public static final String BYTE = "byte";
	public static final String PRE_COMPRESS = "pre-compress";
	public static final String SELF_CONTAINED = "selfContained";
	public static final String VALUE_MAX_LENGTH = "valueMaxLength";
	public static final String VALUE_PARTITION_CAPACITY = "valuePartitionCapacity";
	public static final String DATATYPE_REPRESENTATION_MAP = "datatypeRepresentationMap";
	public static final String PRESERVE = "preserve";
	public static final String DTD = "dtd";
	public static final String PREFIXES = "prefixes";
	public static final String LEXICAL_VALUES = "lexicalValues";
	public static final String COMMENTS = "comments";
	public static final String PIS = "pis";
	public static final String BLOCK_SIZE = "blockSize";
	public static final String COMMON = "common";
	public static final String COMPRESSION = "compression";
	public static final String FRAGMENT = "fragment";
	public static final String SCHEMA_ID = "schemaId";
	public static final String STRICT = "strict";

	public static final int NUMBER_OF_DISTINGUISHING_BITS = 2;
	public static final int DISTINGUISHING_BITS_VALUE = 2;

	public static final int NUMBER_OF_FORMAT_VERSION_BITS = 4;
	public static final int FORMAT_VERSION_CONTINUE_VALUE = 15;

	protected EXIFactory headerFactory;
	protected GrammarFactory grammarFactory = GrammarFactory.newInstance();

	protected EXIFactory getHeaderFactory() throws EXIException {
		if (headerFactory == null) {
			InputStream isXSD = ClassLoader
					.getSystemResourceAsStream(RESOURCE_EXI_OPTIONS_XSD);
			Grammar headerGrammar = grammarFactory.createGrammar(isXSD);

			headerFactory = DefaultEXIFactory.newInstance();
			headerFactory.setGrammar(headerGrammar);
			headerFactory.setFidelityOptions(FidelityOptions.createStrict());
		}

		return headerFactory;
	}

}
