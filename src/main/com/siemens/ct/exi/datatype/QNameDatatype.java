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
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.core.AbstractEXIBody;
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
	
//	protected NamespaceSupport namespaces;
	protected AbstractEXIBody namespaces;
	protected boolean preservePrefix;

	// Grammar entries
	protected GrammarURIEntry[] grammarURIEntries;
	
	// URI context
	protected RuntimeURIEntry uriContext;
	protected List<RuntimeURIEntry> runtimeURIEntries;

	// public QNameDatatype(NamespaceSupport namespaces, boolean preservePrefix, QName schemaType) {
	public QNameDatatype(AbstractEXIBody namespaces, QName schemaType) {
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
//		long start = System.currentTimeMillis();
		this.grammarURIEntries = grammarURIEntries;
		runtimeURIEntries.clear();
		for (GrammarURIEntry grammarEntry : grammarURIEntries) {
			addURI(grammarEntry.uri);
			// prefixes
			for (String prefix : grammarEntry.prefixes) {
				uriContext.addPrefix(prefix);
			}
			// local-names
			for (String localName : grammarEntry.localNames) {
				uriContext.addLocalName(localName);
			}
		}
//		long end = System.currentTimeMillis();
//		System.out.println("Init StringTable " + (end-start));
	}
	
	public void initForEachRun()  {
		/*
		 * Remove entries from runtime lists that may have been added
		 * from a previous coding step
		 */
		// GrammarURIEntry[] grammarURIEntries = grammar.getGrammarEntries();
		//	remove URIs 
		assert(grammarURIEntries.length <= runtimeURIEntries.size() );
		int uriSize;
		while(grammarURIEntries.length < (uriSize = runtimeURIEntries.size())) {
			runtimeURIEntries.remove(uriSize-1);
		}
		//	remove localNames & prefixes
		for(int i=0;i<grammarURIEntries.length; i++) {
			RuntimeURIEntry rue = runtimeURIEntries.get(i);
			//	local-names
			String[] grammarLocalNames = grammarURIEntries[i].localNames;
			int localNameSize;
			while(grammarLocalNames.length < (localNameSize = rue.getLocalNameSize() )) {
				rue.removeLocalName(localNameSize-1);
			}
			// prefixes
			String[] grammarPrefixes = grammarURIEntries[i].prefixes;
			int prefixSize;
			while(grammarPrefixes.length < (prefixSize = rue.getPrefixSize() )) {
				rue.removePrefix(prefixSize-1);
			}
		}
		
		uriContext = runtimeURIEntries.get(0);
	}
	

	/*
	 * URIs
	 */
	public void updateURIContext(final String namespaceURI) {
		if (uriContext.namespaceURI != namespaceURI) {
			// try to find right URI Entry
			for (RuntimeURIEntry uc : this.runtimeURIEntries) {
				if (uc.namespaceURI.equals(namespaceURI)) {
					uriContext = uc;
					return;
				}
			}
			// URI unknown so far
			uriContext = null;
		}
	}

	public void addURI(final String namespaceURI) {
		// assert (!uris.containsKey(namespaceURI));
		uriContext = new RuntimeURIEntry(namespaceURI, runtimeURIEntries.size());
		runtimeURIEntries.add(uriContext);
	}
	
	
	///////////////////////////////////////////////////

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
		} else {
			return false;
		}
	}
	
	public Value getValue() {
		return new QNameValue(qname, qnamePrefix);
	}
	
//	@Override
//	public boolean isValidRCS(String value) {
//		// Note: boolean really needs to do a check since it can be used for xsi:nil
//		super.isValidRCS(value);
//		return isValid(value);
//	}
	
	public QName getQName() {
		return qname;
	}

	public String getPrefix() {
		return qnamePrefix;
	}
	
	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		encodeQName(qname.getNamespaceURI(), qname.getLocalPart(), qnamePrefix, valueChannel);
	}

	public QName encodeQName(String uri, String localName, String prefix,
			EncoderChannel channel) throws IOException {
		// encode expanded name (uri followed by localName)
		writeUri(uri, channel);
		return writeLocalName(localName, uri,prefix,
				channel);
	}

	public void writeUri(String uri, EncoderChannel channel) throws IOException {
		int nUri = MethodsBag.getCodingLength(runtimeURIEntries.size() + 1); // numberEntries+1

		updateURIContext(uri);
		if (uriContext == null) {
			// uri string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			channel.encodeNBitUnsignedInteger(0, nUri);
			channel.encodeString(uri);
			// after encoding string value is added to table
			addURI(uri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			channel.encodeNBitUnsignedInteger(uriContext.id + 1, nUri);
		}
	}

	public QName writeLocalName(String localName, String uri, String prefix,
			EncoderChannel channel) throws IOException {
		// Note: URI context has to be known before writing localName
		updateURIContext(uri);
		assert (uriContext != null);

		// look for localNameID
		Integer localNameID = uriContext.getLocalNameID(localName);

		QName qname;

		if (localNameID == null) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			channel.encodeUnsignedInteger(localName.length() + 1);
			channel.encodeStringOnly(localName);
			// After encoding the string value, it is added to the string
			// table partition and assigned the next available compact
			// identifier.
			qname = uriContext.addLocalName(localName);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer followed by an the compact identifier of the
			// string value as an n-bit unsigned integer n is log2 m and m is
			// the number of entries in the string table partition
			channel.encodeUnsignedInteger(0);
			int n = MethodsBag.getCodingLength(uriContext.getLocalNameSize());
			channel.encodeNBitUnsignedInteger(localNameID, n);
			qname = uriContext.getNameContext(localNameID);
		}
		
		// writing qname prefix ?
		encodeQNamePrefix(uri, prefix, channel);		
		
		return qname;
	}

	public void encodeQNamePrefix(String uri, String prefix, EncoderChannel channel)
			throws IOException {
		if (preservePrefix) {
			if (uri.equals(XMLConstants.NULL_NS_URI)) {
				// default namespace --> DEFAULT_NS_PREFIX
				// } else if (prefixes4GivenURI.hasMoreElements()) {
			} else {
				updateURIContext(uri);
				// List<String> prefixes = uriContext.prefixes;
				List<String> prefixes = uriContext.getPrefixes();
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
					
//					System.out.println("Pfx " + prefix + " --> " + id + " --> " + numberOfPrefixes);
					
					// overlapping URIs
					channel.encodeNBitUnsignedInteger(id, MethodsBag
							.getCodingLength(numberOfPrefixes));
				} else {
					/*
					 * #1# Possibility If there are no prefixes specified for
					 * the URI of the QName by preceding NS events in the EXI
					 * stream, the prefix is undefined. An undefined prefix is
					 * represented using zero bits (i.e., omitted).
					 * 
					 * #1# Possibility If there is only one prefix, the prefix
					 * is implicit
					 */
				}
			}
		}
	}

	public void writePrefix(String prefix, String uri, EncoderChannel channel)
			throws IOException {
		Integer pfxID = uriContext.getPrefixID(prefix);
		int nPfx = MethodsBag.getCodingLength(uriContext.getPrefixSize() + 1); // n-bit
		if (pfxID == null) {
			// string value was not found
			// ==> zero (0) as an n-bit unsigned integer
			// followed by pfx encoded as string
			channel.encodeNBitUnsignedInteger(0, nPfx);
			channel.encodeString(prefix);
			// after encoding string value is added to table
			uriContext.addPrefix(prefix);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			channel.encodeNBitUnsignedInteger(pfxID + 1, nPfx);
		}
	}
	
	/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////
	
	public String readUri(DecoderChannel channel) throws IOException {
		int numberBitsUri = MethodsBag.getCodingLength(runtimeURIEntries.size() + 1); // numberEntries+1
		int uriID = channel.decodeNBitUnsignedInteger(numberBitsUri);

		String uri;

		if (uriID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			uri = new String(channel.decodeString());
			// after encoding string value is added to table
			addURI(uri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			uri = runtimeURIEntries.get(uriID - 1).namespaceURI;
			updateURIContext(uri);
		}

		return uri;
	}
	
	public QName readQName(DecoderChannel channel) throws IOException {
		// decode uri & local-name
		return readLocalName(readUri(channel), channel);
	}
	
	public QName readLocalName(String uri, DecoderChannel channel) throws IOException {
		updateURIContext(uri);
		int length = channel.decodeUnsignedInteger();

		QName qname;
		if (length > 0) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			String localName = new String(channel.decodeStringOnly(length - 1));
			// After encoding the string value, it is added to the string table
			// partition and assigned the next available compact identifier.
			qname = uriContext.addLocalName(localName);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer
			// followed by an the compact identifier of the string value as an
			// n-bit unsigned integer
			// n is log2 m and m is the number of entries in the string table
			// partition
			int n = MethodsBag.getCodingLength(uriContext.getLocalNameSize());
			int localNameID = channel.decodeNBitUnsignedInteger(n);
			qname = uriContext.getNameContext(localNameID);
		}

		return qname;
	}
	
	public String decodeQNamePrefix(QName qname, DecoderChannel channel) throws IOException {
		String uri = qname.getNamespaceURI();
		if (preservePrefix) {
			String prefix = null;
			if (uri.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
				prefix = XMLConstants.NULL_NS_URI;
			} else {			
				updateURIContext(uri);
				// List<String> prefixes = uriContext.prefixes;
				List<String> prefixes = uriContext.getPrefixes();
				int numberOfPrefixes = prefixes.size();

				if (numberOfPrefixes > 0) {
					int id = 0;
					if (numberOfPrefixes > 1 ) {
						id = channel.decodeNBitUnsignedInteger(MethodsBag
								.getCodingLength(numberOfPrefixes));
						
						
//						System.out.println("Pfx " + prefixes.get(id) + " --> " + id + " --> " + numberOfPrefixes);
					}
					prefix = prefixes.get(id);
				} else {
					// no previous NS mapping in charge
					// Note: should only happen for SE events where NS appears afterwards
				}
			
			}
			return prefix;
		} else {
			return null;
		}
	}
	
	public String readPrefix(String uri, DecoderChannel channel) throws IOException {
		String prefix;

		int nPfx = MethodsBag.getCodingLength(uriContext.getPrefixSize() + 1); // n-bit
		int pfxID = channel.decodeNBitUnsignedInteger(nPfx);
		if (pfxID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by pfx encoded as string
			prefix = new String(channel.decodeString());
			// after decoding pfx value is added to table
			uriContext.addPrefix(prefix);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			prefix = uriContext.getPrefix(pfxID - 1);
		}

		return prefix;
	}
	

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		qname = readQName(valueChannel);
		String prefix;
		if (preservePrefix) {
			prefix = decodeQNamePrefix(qname, valueChannel);
		} else {
			// prefix = namespaces.getPrefix(qname.getNamespaceURI());
			prefix = null;
		}

		return new QNameValue(qname, prefix);
	}
}