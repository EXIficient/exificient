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
 * This class allows one to specify decode behavior.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

public class DecodingOptions {

	/** SchemaId in EXI header is not used */
	public static final String IGNORE_SCHEMA_ID = "IGNORE_SCHEMA_ID";

	/** Pushback size for multiple streams in one file */
	public static final int PUSHBACK_BUFFER_SIZE = 512;

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
	 *             if option is not supported
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
	 * @return informs whether option was set
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
