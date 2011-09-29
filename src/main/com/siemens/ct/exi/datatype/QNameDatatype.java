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
import com.siemens.ct.exi.EnhancedQName;
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
 * @version 0.7
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

	public void setFactoryInformation(boolean preservePrefix,
			GrammarURIEntry[] grammarURIEntries) {
		this.preservePrefix = preservePrefix;
		this.grammarURIEntries = grammarURIEntries;
		runtimeURIEntries.clear();
		for (int i = 0; i < grammarURIEntries.length; i++) {
			RuntimeURIEntry rue = new RuntimeURIEntry(
					grammarURIEntries[i].namespaceURI, i,
					grammarURIEntries[i].eQNames,
					grammarURIEntries[i].prefixes, preservePrefix);
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

	public final int getUriID(final String uri) {
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

	public EnhancedQName getEnhancedQName(String localName, int uriID) {
		assert (uriID >= 0 && uriID < runtimeURIEntries.size());
		// grammar entries
		if (uriID < grammarURIEntries.length) {
			GrammarURIEntry gue = grammarURIEntries[uriID];
			// binary search given that localName grammar entries are sorted
			int localNameID = Arrays.binarySearch(gue.localNames, localName);
			if (localNameID >= 0) {
				return gue.eQNames[localNameID];
			}
		}

		// runtime entries
		RuntimeURIEntry rue = runtimeURIEntries.get(uriID);
		// linear search, runtime entries are not sorted
		for (int i = 0; i < rue.getLocalNameSize(); i++) {
			EnhancedQName eqn = rue.getEnhancedQName(i);
			if (eqn.getQName().getLocalPart().equals(localName)) {
				return eqn;
			}
		}

		// return Constants.NOT_FOUND;
		return null;
	}

	public EnhancedQName getEnhancedQName(String uri, String localName) {
		int namespaceUriID = getUriID(uri);
		if (namespaceUriID == Constants.NOT_FOUND) {
			return null;
		}
		return getEnhancedQName(localName, namespaceUriID);
	}

	public EnhancedQName getEnhancedQName(int uriID, int localNameID) {
		return runtimeURIEntries.get(uriID).getEnhancedQName(localNameID);
	}

	protected int addURI(final String namespaceURI) {
		int uriID = runtimeURIEntries.size();
		runtimeURIEntries.add(new RuntimeURIEntry(namespaceURI, uriID,
				preservePrefix));
		return uriID;
	}

	protected EnhancedQName addLocalName(final String localName, int uriID) {
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

	public String getURI(int uriID) {
		return runtimeURIEntries.get(uriID).namespaceURI;
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
		String qnameLocalName;

		/*
		 * If there is no namespace in scope for the specified qname prefix, the
		 * QName uri is set to empty ("") and the QName localName is set to the
		 * full lexical value of the QName, including the prefix.
		 */
		if (qnameURI == null) {
			/* uri in scope for prefix */
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
			// eqname = qv.toEnhancedQName();
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
		EnhancedQName eqname = encodeQName(this.qname.getNamespaceURI(),
				this.qname.getLocalPart(), valueChannel);
		encodeQNamePrefix(qnamePrefix, eqname.getNamespaceUriID(), valueChannel);
	}

	public EnhancedQName encodeQName(String uri, String localName,
			EncoderChannel channel) throws IOException {
		// encode expanded name (uri followed by localName)
		int uriID = encodeUri(uri, channel);
		return encodeLocalName(localName, uriID, channel);
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

	public EnhancedQName encodeLocalName(String localName, String uri,
			String prefix, EncoderChannel channel) throws IOException {
		// local-name
		int uriID = requireUriID(uri);
		return encodeLocalName(localName, uriID, channel);
	}

	protected EnhancedQName encodeLocalName(String localName, int uriID,
			EncoderChannel channel) throws IOException {
		// Note: URI context has to be known before writing localName
		assert (uriID >= 0 && uriID < runtimeURIEntries.size());

		// look for localNameID
		EnhancedQName eqname = getEnhancedQName(localName, uriID);

		if (eqname == null) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			channel.encodeUnsignedInteger(localName.length() + 1);
			channel.encodeStringOnly(localName);
			// After encoding the string value, it is added to the string
			// table partition and assigned the next available compact
			// identifier.
			eqname = addLocalName(localName, uriID);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer followed by an the compact identifier of the
			// string value as an n-bit unsigned integer n is log2 m and m is
			// the number of entries in the string table partition
			channel.encodeUnsignedInteger(0);
			int n = MethodsBag.getCodingLength(getLocalNameSize(uriID));
			channel.encodeNBitUnsignedInteger(eqname.getLocalNameID(), n);
		}

		return eqname;
	}

	public void encodeQNamePrefix(String prefix, String uri,
			EncoderChannel channel) throws IOException {
		encodeQNamePrefix(prefix, requireUriID(uri), channel);
	}

	protected void encodeQNamePrefix(String prefix, int uriID,
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

	public EnhancedQName decodeQName(DecoderChannel channel) throws IOException {
		// decode uri & local-name
		return decodeLocalName(decodeUri(channel), channel);
	}

	public EnhancedQName decodeLocalName(String uri, DecoderChannel channel)
			throws IOException {
		return decodeLocalName(requireUriID(uri), channel);
	}

	public EnhancedQName decodeLocalName(int uriID, DecoderChannel channel)
			throws IOException {

		int length = channel.decodeUnsignedInteger();

		EnhancedQName eqname;
		if (length > 0) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			String localName = new String(channel.decodeStringOnly(length - 1));
			// After encoding the string value, it is added to the string table
			// partition and assigned the next available compact identifier.
			eqname = addLocalName(localName, uriID);
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
			eqname = this.getEnhancedQName(uriID, localNameID);
		}

		return eqname;
	}

	public String getQNameAsString(EnhancedQName eqname, String prefix) {
		RuntimeURIEntry rue = this.runtimeURIEntries.get(eqname
				.getNamespaceUriID());
		return rue.getQNameAsString(eqname, prefix);
	}

	public String decodeQNamePrefix(int uriID, DecoderChannel channel)
			throws IOException {

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
		EnhancedQName eqname = decodeQName(valueChannel);
		String prefix;
		if (preservePrefix) {
			prefix = decodeQNamePrefix(eqname.getNamespaceUriID(), valueChannel);
		} else {
			prefix = null;
		}

		return new QNameValue(eqname.getQName(), prefix);
	}
}