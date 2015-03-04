/*
 * Copyright (C) 2007-2015 Siemens AG
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
 * This class allows one to specify decode behavior.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.4-SNAPSHOT
 */

public class DecodingOptions {

	/** SchemaId in EXI header is not used */
	public static final String IGNORE_SCHEMA_ID = "IGNORE_SCHEMA_ID";

	/* contains options and according values */
	protected Set<String> options;

	protected DecodingOptions() {
		options = new HashSet<String>();
	}

	/**
	 * Creates encoding options using default options
	 * 
	 * @return default encoding options
	 */
	public static DecodingOptions createDefault() {
		DecodingOptions ho = new DecodingOptions();
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
		if (key.equals(IGNORE_SCHEMA_ID)) {
			options.add(key);
		} else {
			throw new UnsupportedOption("DecodingOption '" + key
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
		if (o instanceof DecodingOptions) {
			DecodingOptions other = (DecodingOptions) o;
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
