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
 * @version 0.6
 */

public class EncodingOptions {

	/* EXI Cookie */
	public static final String INCLUDE_COOKIE = "INCLUDE_COOKIE";
	/* EXI Options */
	public static final String INCLUDE_OPTIONS = "INCLUDE_OPTIONS";
	/* schemaID as part of EXI Options */
	public static final String INCLUDE_SCHEMA_ID = "INCLUDE_SCHEMA_ID";
	
	/* encode entity references as ER event instead of trying to resolve them */
	public static final String RETAIN_ENTITY_REFERENCE = "KEEP_ENTITY_REFERENCES_UNRESOLVED";
	
	/* attribute "schemaLocation" and "noNamespaceSchemaLocation" */
	public static final String INCLUDE_XSI_SCHEMALOCATION = "INCLUDE_XSI_SCHEMALOCATION";
	
	/* Insignificant xsi:nil values e.g., xsi:nil="false" */
	public static final String INCLUDE_INSIGNIFICANT_XSI_NIL = "INCLUDE_INSIGNIFICANT_XSI_NIL";

	/* Insignificant xsi:type values e.g., xsi:type="xs:string" where type is already string */
	public static final String INCLUDE_INSIGNIFICANT_XSI_TYPE = "INCLUDE_INSIGNIFICANT_XSI_TYPE";
	
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
		} else if (key.equals(INCLUDE_INSIGNIFICANT_XSI_TYPE)) {
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
	
//	/**
//	 * Reports the value for a given option key.
//	 * 
//	 * @param key option
//	 * @return value
//	 */
//	public Object getOptionValue(String key) {
//		return options.get(key);
//	}

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
