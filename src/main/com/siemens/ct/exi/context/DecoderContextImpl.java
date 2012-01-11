/*
 * Copyright (C) 2007-2011 Siemens AG
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

package com.siemens.ct.exi.context;

import java.io.IOException;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.core.container.PreReadValue;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

public class DecoderContextImpl extends AbstractCoderContext implements
		DecoderContext {

	// string decoder
	StringDecoder stringDecoder;
	public DecoderContextImpl(GrammarContext grammarContext,
			StringDecoder stringDecoder) {
		super(grammarContext);
		this.stringDecoder = stringDecoder;
	}

	public StringDecoder getStringDecoder() {
		return stringDecoder;
	}

	@Override
	public void clear() {
		super.clear();

		// re-set string decoder
		stringDecoder.clear();
	}

	public void setPreReadValue(QNameContext qnc, PreReadValue prrReadValue) {
		RuntimeQNameContextEntries rqne = getRuntimeQNameContextEntries(qnc);
		rqne.preReadValue = prrReadValue;
	}

	public QNameContext decodeQName(DecoderChannel channel) throws IOException {
		// decode uri & local-name
		return decodeLocalName(decodeUri(channel), channel);
	}

	public EvolvingUriContext decodeUri(DecoderChannel channel)
			throws IOException {
		int numberBitsUri = MethodsBag.getCodingLength(getNumberOfUris() + 1); // numberEntries+1
		int uriID = channel.decodeNBitUnsignedInteger(numberBitsUri);

		EvolvingUriContext uc;

		if (uriID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			String uri = new String(channel.decodeString());
			// after encoding string value is added to table
			uc = addUriContext(uri);
			// uriID = addURI(uri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			uc = getUriContext(--uriID);
			// uriID--;
		}

		// return uriID;
		return uc;
	}

	public QNameContext decodeLocalName(EvolvingUriContext uc,
			DecoderChannel channel) throws IOException {

		int length = channel.decodeUnsignedInteger();

		QNameContext qnc;

		if (length > 0) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			String localName = new String(channel.decodeStringOnly(length - 1));
			// After encoding the string value, it is added to the string table
			// partition and assigned the next available compact identifier.
			// qnc = uc.addQNameContext(localName);
			qnc = addQNameContext(uc, localName);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer
			// followed by an the compact identifier of the string value as an
			// n-bit unsigned integer
			// n is log2 m and m is the number of entries in the string table
			// partition
			int n = MethodsBag.getCodingLength(uc.getNumberOfQNames());
			int localNameID = channel.decodeNBitUnsignedInteger(n);
			qnc = uc.getQNameContext(localNameID);
		}

		return qnc;
	}

	public String decodeQNamePrefix(UriContext uc, DecoderChannel channel)
			throws IOException {

		String prefix = null;

		if (uc.getNamespaceUriID() == 0) {
			// XMLConstants.DEFAULT_NS_PREFIX
			prefix = XMLConstants.NULL_NS_URI;
		} else {
			int numberOfPrefixes = uc.getNumberOfPrefixes();
			if (numberOfPrefixes > 0) {
				int id = 0;
				if (numberOfPrefixes > 1) {
					id = channel.decodeNBitUnsignedInteger(MethodsBag
							.getCodingLength(numberOfPrefixes));
				}
				// prefix = prefixes.get(id);
				prefix = uc.getPrefix(id);
			} else {
				// no previous NS mapping in charge
				// Note: should only happen for SE events where NS appears
				// afterwards
			}
		}

		return prefix;
	}

	public String decodeNamespacePrefix(EvolvingUriContext uc,
			DecoderChannel channel) throws IOException {
		String prefix;

		int nPfx = MethodsBag.getCodingLength(uc.getNumberOfPrefixes() + 1); // n-bit
		int pfxID = channel.decodeNBitUnsignedInteger(nPfx);

		if (pfxID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by pfx encoded as string
			prefix = new String(channel.decodeString());
			// after decoding pfx value is added to table
			uc.addPrefix(prefix);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			prefix = uc.getPrefix(pfxID - 1);
		}

		return prefix;
	}
	
}
