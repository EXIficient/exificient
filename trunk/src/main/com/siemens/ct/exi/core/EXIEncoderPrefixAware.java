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

package com.siemens.ct.exi.core;

import java.io.IOException;

import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.core.sax.NamespacePrefixLevels;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20090225
 */

public class EXIEncoderPrefixAware extends EXIEncoderPrefixLess implements
		EXIEncoder {
	// namespace prefixes are related to elements
	protected NamespacePrefixLevels nsPrefixes;

	protected String lastSEprefix = null;

	public EXIEncoderPrefixAware(EXIFactory exiFactory) {
		super(exiFactory);

		nsPrefixes = new NamespacePrefixLevels();
	}

	@Override
	protected void initForEachRun() throws EXIException {
		super.initForEachRun();

		// re-set prefixes
		nsPrefixes.clear();
	}

	public void encodeStartElementPrefixMapping(String uri, String prefix)
			throws EXIException {
		//	push prefix level
		nsPrefixes.addLevel();

		// prefix
		lastSEprefix = prefix;

		if (this.nsPrefixes.hasPrefixForURI(uri)) {
			// TODO *overlapping* prefixes: same namespace BUT different
			// prefixes
			// System.out.println ( "SE_PFX uri found for " + uri + " --> " +
			// prefix );
		} else {
			/*
			 * If there are no prefixes specified for the URI of the QName by
			 * preceding NS events in the EXI stream, the prefix is undefined.
			 * An undefined prefix is represented using zero bits (i.e.,
			 * omitted).
			 */
		}
	}

	public void encodeEndElement() throws EXIException {
		// call super-method
		super.encodeEndElement();

		//	pop prefix level
		nsPrefixes.removeLevel();
	}

	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException {
		assert (fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX));

		try {
			// event code
			int ec2 = currentRule.get2ndLevelEventCode(
					EventType.NAMESPACE_DECLARATION, fidelityOptions);
			encode2ndLevelEventCode(ec2);

			// prefix mapping
			block.writeUri(uri);
			block.writePrefix(prefix, uri);

			// local-element-ns
			if (prefix.equals(lastSEprefix)) {
				// System.out.println ( "Prefix '" + prefix + "' is part of
				// an SE event followed by an associated NS event");
				block.writeBoolean(true);
			} else {
				block.writeBoolean(false);
			}
			nsPrefixes.addPrefix(uri, prefix);
		} catch (IOException e) {
			throw new EXIException(e);
		}

	}

}
