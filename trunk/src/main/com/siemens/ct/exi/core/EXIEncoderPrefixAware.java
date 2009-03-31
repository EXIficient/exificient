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
import java.util.Enumeration;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.xml.QNameUtilities;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20090313
 */

public class EXIEncoderPrefixAware extends EXIEncoderPrefixLess implements
		EXIEncoder {

	protected String lastSEprefix = null;

	public EXIEncoderPrefixAware(EXIFactory exiFactory) {
		super(exiFactory);
	}

	@Override
	public void encodeStartElement(String uri, String localName, String raw)
			throws EXIException {

		// encode element only
		super.encodeStartElement(uri, localName);

		try {
			@SuppressWarnings("unchecked")
			Enumeration<String> prefixes4GivenURI = this.namespaces
					.getPrefixes(uri);

			String prefix = QNameUtilities.getPrefixPart(raw);

			if (uri.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
				// default namespace
			} else if (prefixes4GivenURI.hasMoreElements()) {

				int numberOfPrefixes = 0;
				int id = -1;

				do {
					if (prefixes4GivenURI.nextElement().equals(prefix)) {
						id = numberOfPrefixes;
					}
					numberOfPrefixes++;
				} while (prefixes4GivenURI.hasMoreElements());

				if (numberOfPrefixes > 1) {
					// overlapping URIs
					block.writeEventCode(id, MethodsBag
							.getCodingLength(numberOfPrefixes));
				}
			} else {
				/*
				 * If there are no prefixes specified for the URI of the QName
				 * by preceding NS events in the EXI stream, the prefix is
				 * undefined. An undefined prefix is represented using zero bits
				 * (i.e., omitted).
				 */
			}

			lastSEprefix = prefix;
			
		} catch (IOException e) {
			throw new EXIException(e);
		}		
	}

	@Override
	public void encodeNamespaceDeclaration(String uri, String prefix)
			throws EXIException {
		super.encodeNamespaceDeclaration(uri, prefix);

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
				block.writeBoolean(true);
			} else {
				block.writeBoolean(false);
			}
		} catch (IOException e) {
			throw new EXIException(e);
		}

	}

}
