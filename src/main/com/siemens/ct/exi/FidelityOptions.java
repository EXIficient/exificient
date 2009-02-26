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

import java.util.HashSet;
import java.util.Set;

import com.siemens.ct.exi.exceptions.UnsupportedOption;

/**
 * Some XML applications do not require the entire XML feature set and would
 * prefer to eliminate the overhead associated with unused features.
 * 
 * Applications can use a set of fidelity options to specify the XML features
 * they require.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080922
 */

public class FidelityOptions {
	/* Comments, ProcessingInstructions, DTDs and Prefixes are preserved */
	public static final String FEATURE_COMMENT = "PRESERVE_COMMENTS";
	public static final String FEATURE_PI = "PRESERVE_PIS";
	public static final String FEATURE_DTD = "PRESERVE_DTDS";
	public static final String FEATURE_PREFIX = "PRESERVE_PREFIXES";

	/*
	 * Lexical form of element and attribute values is preserved in value
	 * content items
	 */
	public static final String FEATURE_LEXICAL_VALUE = "PRESERVE_LEXICAL_VALUES";
	public static final String FEATURE_WS = "PRESERVE_WHITESPACES";

	/* Enable the use of self contained elements in the EXI stream */
	public static final String FEATURE_SC = "SELF_CONTAINED";

	/* Strict interpretation of schemas is used to achieve better compactness */
	public static final String FEATURE_STRICT = "STRICT";

	/* contains options set to TRUE */
	protected Set<String> options;

	/* special strict handling */
	protected boolean isStrict = false;

	protected FidelityOptions() {
		options = new HashSet<String>();
	}

	/**
	 * Creates fidelity options using default options
	 * 
	 * @return default fidelity options
	 */
	public static FidelityOptions createDefault() {
		FidelityOptions fo = new FidelityOptions();

		return fo;
	}

	/**
	 * Creates fidelity options using strict option. Note: no namespace
	 * prefixes, comments etc are preserved nor schema deviations are allowed.
	 * 
	 * @return default fidelity options
	 */
	public static FidelityOptions createStrict() {
		FidelityOptions fo = new FidelityOptions();

		fo.options.add(FEATURE_STRICT);
		fo.isStrict = true;

		return fo;
	}

	/**
	 * Creates fidelity options using the maximum compatibility mode, e.g.
	 * preserving unsignificant whitespaces.
	 * 
	 * @return default fidelity options
	 */
	public static FidelityOptions createAll() {
		FidelityOptions fo = new FidelityOptions();

		fo.options.add(FEATURE_COMMENT);
		fo.options.add(FEATURE_PI);
		fo.options.add(FEATURE_DTD);
		fo.options.add(FEATURE_PREFIX);
		fo.options.add(FEATURE_LEXICAL_VALUE);

		fo.options.add(FEATURE_WS);

		fo.options.add(FEATURE_SC);

		return fo;
	}

	/**
	 * Enables or disables the specified fidelity feature.
	 * 
	 * @param key
	 *            refering to a specific feature
	 * @param decision
	 *            enabling or disabling feature
	 * @throws UnsupportedOption
	 */
	public void setFidelity(String key, boolean decision)
			throws UnsupportedOption {
		if (key.equals(FEATURE_STRICT)) {
			if (decision) {
				// no other features allowed
				options.clear();

				options.add(key);
				isStrict = true;
			} else {
				// remove strict (if present)
				options.remove(key);
				isStrict = false;
			}
		} else if (key.equals(FEATURE_COMMENT) || key.equals(FEATURE_PI)
				|| key.equals(FEATURE_DTD) || key.equals(FEATURE_PREFIX)
				|| key.equals(FEATURE_LEXICAL_VALUE) || key.equals(FEATURE_WS)
				|| key.equals(FEATURE_SC)) {
			if (decision) {
				//	
				if (isStrict()) {
					throw new UnsupportedOption(
							"StrictMode is exclusive and does not allow any other option.");
				} else {
					options.add(key);
				}
			} else {
				// remove option (if present)
				options.remove(key);
			}
		} else {
			throw new UnsupportedOption("FidelityOption '" + key
					+ "' is unknown!");
		}
	}

	/**
	 * Informs whether the specified feature is enabled.
	 * 
	 * @param key
	 *            feature
	 * @return whether option is turned on
	 */
	public boolean isFidelityEnabled(String key) {
		return options.contains(key);
	}

	/**
	 * Convenience method returning whether all fidelity options are turned off.
	 * 
	 * @return boolean whether strict mode is in play
	 */
	public boolean isStrict() {
		return isStrict;
		// return options.contains ( FEATURE_STRICT );
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FidelityOptions) {
			FidelityOptions other = (FidelityOptions) o;
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
