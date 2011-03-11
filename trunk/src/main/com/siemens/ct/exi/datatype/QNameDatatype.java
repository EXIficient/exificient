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

package com.siemens.ct.exi.datatype;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.core.AbstractEXIBodyCoder;
import com.siemens.ct.exi.core.RuntimeURIEntry;
import com.siemens.ct.exi.datatype.charset.XSDStringCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.grammar.GrammarURIEntry;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.xml.QNameUtilities;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class QNameDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -5388595112370214500L;

	protected QName qname;
	protected String qnamePrefix;

	protected AbstractEXIBodyCoder namespaces;
	protected boolean preservePrefix;

	// Grammar & Runtime entries
	protected GrammarURIEntry[] grammarURIEntries;
	protected List<RuntimeURIEntry> runtimeURIEntries;

	public QNameDatatype(AbstractEXIBodyCoder namespaces, QName schemaType) {
		super(BuiltInType.QNAME, schemaType);
		this.rcs = new XSDStringCharacterSet();
		//
		this.namespaces = namespaces;
		//
		runtimeURIEntries = new ArrayList<RuntimeURIEntry>();
	}

	public void setPreservePrefix(boolean preservePrefix) {
		this.preservePrefix = preservePrefix;
	}

	public void setGrammarURIEnties(GrammarURIEntry[] grammarURIEntries) {
		this.grammarURIEntries = grammarURIEntries;
		runtimeURIEntries.clear();
		for (int i = 0; i < grammarURIEntries.length; i++) {
			RuntimeURIEntry rue = new RuntimeURIEntry(
					grammarURIEntries[i].namespaceURI, i,
					grammarURIEntries[i].prefixes);
			runtimeURIEntries.add(rue);
		}
	}

	public void initForEachRun() {
		assert (runtimeURIEntries.size() >= grammarURIEntries.length);
		/*
		 * Remove entries from runtime lists that may have been added from a
		 * previous coding step
		 */
		while (runtimeURIEntries.size() > grammarURIEntries.length) {
			runtimeURIEntries.remove(runtimeURIEntries.size() - 1);
		}
		/*
		 * Clear remaining runtime entries (derived from grammars)
		 */
		for (int i = 0; i < grammarURIEntries.length; i++) {
			runtimeURIEntries.get(i).clear();
		}
	}

	protected final int getUriID(final String uri) {
		// grammar uris
		for (int i = 0; i < grammarURIEntries.length; i++) {
			if (grammarURIEntries[i].namespaceURI.equals(uri)) {
				return i;
			}
		}
		// runtime uris
		for (int i = grammarURIEntries.length; i < runtimeURIEntries.size(); i++) {
			if (runtimeURIEntries.get(i).namespaceURI.equals(uri)) {
				return i;
			}
		}

		return Constants.NOT_FOUND;
	}

	protected int getLocalNameID(String localName, int uriID) {
		assert (uriID >= 0 && uriID < runtimeURIEntries.size());

		int grammarEntries = 0;

		// grammar entries
		if (uriID < grammarURIEntries.length) {
			GrammarURIEntry gue = grammarURIEntries[uriID];
			grammarEntries = gue.localNames.length;
			// binary search given that localName grammar entries are sorted
			int bs = Arrays.binarySearch(gue.localNames, localName);
			if (bs >= 0) {
				return bs;
			}
		}

		// runtime entries
		RuntimeURIEntry rue = runtimeURIEntries.get(uriID);
		// linear search, runtime entries are not sorted
		for (int i = 0; i < rue.getLocalNameSize(); i++) {
			QName qn = rue.getQName(i);
			if (qn.getLocalPart().equals(localName)) {
				return i + grammarEntries;
			}
		}

		return Constants.NOT_FOUND;
	}

	protected int addURI(final String namespaceURI) {
		int uriID = runtimeURIEntries.size();
		runtimeURIEntries.add(new RuntimeURIEntry(namespaceURI, uriID));
		return uriID;
	}

	protected QName addLocalName(final String localName, int uriID) {
		return runtimeURIEntries.get(uriID).addLocalName(localName);
	}

	protected int getLocalNameSize(int uriID) {
		int grammarLocalNameSize = 0;
		// grammar entries
		if (uriID < grammarURIEntries.length) {
			GrammarURIEntry gue = grammarURIEntries[uriID];
			grammarLocalNameSize = gue.localNames.length;
		}
		// runtime entries
		return runtimeURIEntries.get(uriID).getLocalNameSize()
				+ grammarLocalNameSize;
	}

	protected QName getLocalName(int uriID, int localNameID) {
		int grammarLocalNameSize = 0;

		// grammar entries
		if (uriID < grammarURIEntries.length) {
			GrammarURIEntry gue = grammarURIEntries[uriID];
			grammarLocalNameSize = gue.localNames.length;
			if (localNameID < grammarLocalNameSize) {
				return gue.qNames[localNameID];
			}
		}

		// runtime entries
		return runtimeURIEntries.get(uriID).getQName(
				localNameID - grammarLocalNameSize);
	}

	protected void addPrefix(final String prefix, int uriID) {
		runtimeURIEntries.get(uriID).addPrefix(prefix);
	}

	public boolean isValid(String value) {
		super.isValidRCS(value);

		// extract prefix
		qnamePrefix = QNameUtilities.getPrefixPart(value);
		// retrieve uri
		String qnameURI = namespaces.getURI(qnamePrefix);

		/*
		 * If there is no namespace in scope for the specified qname prefix, the
		 * QName uri is set to empty ("") and the QName localName is set to the
		 * full lexical value of the QName, including the prefix.
		 */
		String qnameLocalName;
		if (qnameURI == null) {
			qnameURI = XMLConstants.NULL_NS_URI;
			qnameLocalName = value;
		} else {
			qnameLocalName = QNameUtilities.getLocalPart(value);
		}

		qname = new QName(qnameURI, qnameLocalName);
		return true;
	}

	public boolean isValid(Value value) {
		if (value instanceof QNameValue) {
			QNameValue qv = ((QNameValue) value);
			qname = qv.toQName();
			qnamePrefix = qv.getPrefix();
			return true;
		} else if (isValid(value.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public QName getQName() {
		return qname;
	}

	public String getPrefix() {
		return qnamePrefix;
	}

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		encodeQName(qname.getNamespaceURI(), qname.getLocalPart(), qnamePrefix,
				valueChannel);
	}

	public QName encodeQName(String uri, String localName, String prefix,
			EncoderChannel channel) throws IOException {
		// encode expanded name (uri followed by localName)
		return encodeLocalName(localName, encodeUri(uri, channel), prefix,
				channel);
	}

	public int encodeUri(final String uri, EncoderChannel channel)
			throws IOException {
		int nUri = MethodsBag.getCodingLength(runtimeURIEntries.size() + 1); // numberEntries+1
		int uriID = getUriID(uri);
		if (uriID == Constants.NOT_FOUND) {
			// uri string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			channel.encodeNBitUnsignedInteger(0, nUri);
			channel.encodeString(uri);
			// after encoding string value is added to table
			uriID = addURI(uri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			channel.encodeNBitUnsignedInteger(uriID + 1, nUri);
		}
		return uriID;
	}

	// throws error if URI is unknown
	protected final int requireUriID(String uri) {
		int uriID = getUriID(uri);
		if (uriID == Constants.NOT_FOUND) {
			throw new RuntimeException("URI unknown: " + uri);
		}
		return uriID;
	}

	public QName encodeLocalName(String localName, String uri, String prefix,
			EncoderChannel channel) throws IOException {
		return encodeLocalName(localName, requireUriID(uri), prefix, channel);
	}

	public QName encodeLocalName(String localName, int uriID, String prefix,
			EncoderChannel channel) throws IOException {
		// Note: URI context has to be known before writing localName
		assert (uriID >= 0 && uriID < runtimeURIEntries.size());

		// look for localNameID
		int localNameID = getLocalNameID(localName, uriID);

		QName qname;
		if (localNameID == Constants.NOT_FOUND) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			channel.encodeUnsignedInteger(localName.length() + 1);
			channel.encodeStringOnly(localName);
			// After encoding the string value, it is added to the string
			// table partition and assigned the next available compact
			// identifier.
			qname = addLocalName(localName, uriID);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer followed by an the compact identifier of the
			// string value as an n-bit unsigned integer n is log2 m and m is
			// the number of entries in the string table partition
			channel.encodeUnsignedInteger(0);
			// int n =
			// MethodsBag.getCodingLength(uriContext.getLocalNameSize());
			int n = MethodsBag.getCodingLength(getLocalNameSize(uriID));
			channel.encodeNBitUnsignedInteger(localNameID, n);
			// qname = uriContext.getNameContext(localNameID);
			qname = getLocalName(uriID, localNameID);
		}

		// writing qname prefix ?
		encodeQNamePrefix(prefix, uriID, channel);

		return qname;
	}

	public void encodeQNamePrefix(String prefix, String uri,
			EncoderChannel channel) throws IOException {
		encodeQNamePrefix(prefix, requireUriID(uri), channel);
	}

	public void encodeQNamePrefix(String prefix, int uriID,
			EncoderChannel channel) throws IOException {
		if (preservePrefix) {
			if (uriID == 0) {
				// XMLConstants.NULL_NS_URI
				// default namespace --> DEFAULT_NS_PREFIX
			} else {
				List<String> prefixes = runtimeURIEntries.get(uriID)
						.getPrefixes();
				int numberOfPrefixes = prefixes.size();
				if (numberOfPrefixes > 1) {

					int id = Constants.NOT_FOUND;

					for (int i = 0; i < numberOfPrefixes; i++) {
						if (prefixes.get(i).equals(prefix)) {
							id = i;
							i = numberOfPrefixes; // abort loop
						}
					}

					if (id == Constants.NOT_FOUND) {
						// choose *one* prefix which gets modified by
						// local-element-ns anyway ?
						id = 0;
					}

					// overlapping URIs
					channel.encodeNBitUnsignedInteger(id,
							MethodsBag.getCodingLength(numberOfPrefixes));
				} else {
					/*
					 * #1# Possibility If there are no prefixes specified for
					 * the URI of the QName by preceding NS events in the EXI
					 * stream, the prefix is undefined. An undefined prefix is
					 * represented using zero bits (i.e., omitted).
					 * 
					 * #2# Possibility If there is only one prefix, the prefix
					 * is implicit
					 */
				}
			}
		}
	}

	public void encodeNamespacePrefix(String prefix, int uriID,
			EncoderChannel channel) throws IOException {

		int pfxID = runtimeURIEntries.get(uriID).getPrefixID(prefix);

		int nPfx = MethodsBag.getCodingLength(runtimeURIEntries.get(uriID)
				.getPrefixSize() + 1); // n-bit
		if (pfxID == Constants.NOT_FOUND) {
			// string value was not found
			// ==> zero (0) as an n-bit unsigned integer
			// followed by pfx encoded as string
			channel.encodeNBitUnsignedInteger(0, nPfx);
			channel.encodeString(prefix);
			// after encoding string value is added to table
			addPrefix(prefix, uriID);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			channel.encodeNBitUnsignedInteger(pfxID + 1, nPfx);
		}
	}

	public String getUriForID(int uriID) throws IOException {
		return runtimeURIEntries.get(uriID).namespaceURI;
	}

	public int decodeUri(DecoderChannel channel) throws IOException {
		int numberBitsUri = MethodsBag
				.getCodingLength(runtimeURIEntries.size() + 1); // numberEntries+1
		int uriID = channel.decodeNBitUnsignedInteger(numberBitsUri);

		if (uriID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			String uri = new String(channel.decodeString());
			// after encoding string value is added to table
			uriID = addURI(uri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			uriID--;
		}

		return uriID;
	}

	public QName decodeQName(DecoderChannel channel) throws IOException {
		// decode uri & local-name
		return decodeLocalName(decodeUri(channel), channel);
	}

	public QName decodeLocalName(String uri, DecoderChannel channel)
			throws IOException {
		return decodeLocalName(requireUriID(uri), channel);
	}

	public QName decodeLocalName(int uriID, DecoderChannel channel)
			throws IOException {

		int length = channel.decodeUnsignedInteger();

		QName qname;
		if (length > 0) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			String localName = new String(channel.decodeStringOnly(length - 1));
			// After encoding the string value, it is added to the string table
			// partition and assigned the next available compact identifier.
			qname = addLocalName(localName, uriID);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer
			// followed by an the compact identifier of the string value as an
			// n-bit unsigned integer
			// n is log2 m and m is the number of entries in the string table
			// partition
			int n = MethodsBag.getCodingLength(getLocalNameSize(uriID));
			int localNameID = channel.decodeNBitUnsignedInteger(n);
			// qname = uriContext.getNameContext(localNameID);
			qname = getLocalName(uriID, localNameID);
		}

		return qname;
	}

	public String decodeQNamePrefix(String uri, DecoderChannel channel)
			throws IOException {
		if (preservePrefix) {
			return decodeQNamePrefix(requireUriID(uri), channel);
		} else {
			return null;
		}
	}

	protected String decodeQNamePrefix(int uriID, DecoderChannel channel)
			throws IOException {
		// if (preservePrefix) {
		String prefix = null;
		if (uriID == 0) {
			// XMLConstants.DEFAULT_NS_PREFIX
			prefix = XMLConstants.NULL_NS_URI;
		} else {
			List<String> prefixes = runtimeURIEntries.get(uriID).getPrefixes();
			int numberOfPrefixes = prefixes.size();

			if (numberOfPrefixes > 0) {
				int id = 0;
				if (numberOfPrefixes > 1) {
					id = channel.decodeNBitUnsignedInteger(MethodsBag
							.getCodingLength(numberOfPrefixes));
				}
				prefix = prefixes.get(id);
			} else {
				// no previous NS mapping in charge
				// Note: should only happen for SE events where NS appears
				// afterwards
			}
		}
		return prefix;
		// } else {
		// return null;
		// }
	}

	public String decodeNamespacePrefix(int uriID, DecoderChannel channel)
			throws IOException {
		String prefix;

		int nPfx = MethodsBag.getCodingLength(runtimeURIEntries.get(uriID)
				.getPrefixSize() + 1); // n-bit
		int pfxID = channel.decodeNBitUnsignedInteger(nPfx);
		if (pfxID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by pfx encoded as string
			prefix = new String(channel.decodeString());
			// after decoding pfx value is added to table
			addPrefix(prefix, uriID);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			prefix = runtimeURIEntries.get(uriID).getPrefix(pfxID - 1);
		}

		return prefix;
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		qname = decodeQName(valueChannel);
		String prefix;
		if (preservePrefix) {
			prefix = decodeQNamePrefix(qname.getNamespaceURI(), valueChannel);
		} else {
			prefix = null;
		}

		return new QNameValue(qname, prefix);
	}
}