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

package com.siemens.ct.exi;

import java.util.HashSet;
import java.util.Set;

import com.siemens.ct.exi.exceptions.UnsupportedOption;

/**
 * Some applications may require EXI coding options shared via the EXI Header
 * (e.g., EXI Cookie). This class allows one to specify which coding options are
 * needed.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class EncodingOptions {

	/** EXI Cookie */
	public static final String INCLUDE_COOKIE = "INCLUDE_COOKIE";

	/** EXI Options */
	public static final String INCLUDE_OPTIONS = "INCLUDE_OPTIONS";

	/** schemaID as part of EXI Options */
	public static final String INCLUDE_SCHEMA_ID = "INCLUDE_SCHEMA_ID";

	/** encode entity references as ER event instead of trying to resolve them */
	public static final String RETAIN_ENTITY_REFERENCE = "KEEP_ENTITY_REFERENCES_UNRESOLVED";

	/** attribute "schemaLocation" and "noNamespaceSchemaLocation" */
	public static final String INCLUDE_XSI_SCHEMALOCATION = "INCLUDE_XSI_SCHEMALOCATION";

	/** Insignificant xsi:nil values e.g., xsi:nil="false" */
	public static final String INCLUDE_INSIGNIFICANT_XSI_NIL = "INCLUDE_INSIGNIFICANT_XSI_NIL";

	/**
	 * To indicate that the EXI profile is in use and advertising each parameter
	 * value (exi:p element)
	 */
	public static final String INCLUDE_PROFILE_VALUES = "INCLUDE_PROFILE_VALUES";

	/**
	 * To indicate that the EXI stream should respect the Canonical EXI rules
	 * see http://www.w3.org/TR/exi-c14n
	 * */
	public static final String CANONICAL_EXI = "http://www.w3.org/TR/exi-c14n";

	/* contains options and according values */
	protected Set<String> options;

	protected EncodingOptions() {
		options = new HashSet<String>();
	}

	/**
	 * Creates encoding options using default options (NO Cookie, option or
	 * schemaID).
	 * 
	 * @return default encoding options
	 */
	public static EncodingOptions createDefault() {
		EncodingOptions ho = new EncodingOptions();
		return ho;
	}

	/**
	 * Enables given option.
	 * 
	 * <p>
	 * Note: Some options (e.g. INCLUDE_SCHEMA_ID) will only take effect if the
	 * EXI options document is set to encode options in general (see
	 * INCLUDE_OPTIONS).
	 * </p>
	 * 
	 * @param key
	 *            referring to a specific option
	 * 
	 * @throws UnsupportedOption
	 */
	public void setOption(String key) throws UnsupportedOption {
		if (key.equals(INCLUDE_COOKIE)) {
			options.add(key);
		} else if (key.equals(INCLUDE_OPTIONS)) {
			options.add(key);
		} else if (key.equals(INCLUDE_SCHEMA_ID)) {
			options.add(key);
		} else if (key.equals(RETAIN_ENTITY_REFERENCE)) {
			options.add(key);
		} else if (key.equals(INCLUDE_XSI_SCHEMALOCATION)) {
			options.add(key);
		} else if (key.equals(INCLUDE_INSIGNIFICANT_XSI_NIL)) {
			options.add(key);
			// } else if (key.equals(INCLUDE_INSIGNIFICANT_XSI_TYPE)) {
			// options.add(key);
		} else if (key.equals(INCLUDE_PROFILE_VALUES)) {
			options.add(key);
		} else if (key.equals(CANONICAL_EXI)) {
			options.add(key);
		} else {
			throw new UnsupportedOption("EncodingOption '" + key
					+ "' is unknown!");
		}
	}

	/**
	 * Disables given option.
	 * 
	 * @param key
	 *            referring to a specific option
	 * 
	 */
	public boolean unsetOption(String key) {
		return options.remove(key);
	}

	/**
	 * Informs whether the specified option is enabled.
	 * 
	 * @param key
	 *            feature
	 * @return whether option is turned on
	 */
	public boolean isOptionEnabled(String key) {
		return options.contains(key);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof EncodingOptions) {
			EncodingOptions other = (EncodingOptions) o;
			return options.equals(other.options);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return options.hashCode();
	}

	@Override
	public String toString() {
		return options.toString();
	}

}
