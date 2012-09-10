package com.siemens.ct.exi.context;

import java.io.IOException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

public class EncoderContextImpl extends AbstractCoderContext implements
		EncoderContext {

	StringEncoder stringEncoder;

	public EncoderContextImpl(GrammarContext grammarContext,
			StringEncoder stringEncoder) {
		super(grammarContext);
		this.stringEncoder = stringEncoder;
	}

	public StringEncoder getStringEncoder() {
		return stringEncoder;
	}

	@Override
	public void clear() {
		super.clear();

		// re-set string encoder
		stringEncoder.clear();

	}

	public QNameContext encodeQName(String namespaceUri, String localName,
			EncoderChannel channel) throws IOException {
		// uri
		EvolvingUriContext uc = encodeUri(namespaceUri, channel);
		// local-name
		return encodeLocalName(localName, uc, channel);
	}

	public QNameContext encodeLocalName(String localName,
			EvolvingUriContext uc, EncoderChannel channel) throws IOException {
		// Note: URI context has to be known before writing localName
		assert (uc != null);

		// look for localNameID
		QNameContext qnc = uc.getQNameContext(localName);

		if (qnc == null) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			channel.encodeUnsignedInteger(localName.length() + 1);
			channel.encodeStringOnly(localName);
			// After encoding the string value, it is added to the string
			// table partition and assigned the next available compact
			// identifier.
			qnc = addQNameContext(uc, localName);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer followed by an the compact identifier of the
			// string value as an n-bit unsigned integer n is log2 m and m is
			// the number of entries in the string table partition
			channel.encodeUnsignedInteger(0);
			int n = MethodsBag.getCodingLength(uc.getNumberOfQNames());
			channel.encodeNBitUnsignedInteger(qnc.getLocalNameID(), n);
		}

		return qnc;
	}

	public EvolvingUriContext encodeUri(final String namespaceUri,
			EncoderChannel channel) throws IOException {
		int numberBitsUri = MethodsBag.getCodingLength(getNumberOfUris() + 1); // numberEntries+1
		EvolvingUriContext uc = this.getUriContext(namespaceUri);
		if (uc == null) {
			// uri string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			channel.encodeNBitUnsignedInteger(0, numberBitsUri);
			channel.encodeString(namespaceUri);
			// after encoding string value is added to table
			uc = this.addUriContext(namespaceUri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			channel.encodeNBitUnsignedInteger(uc.getNamespaceUriID() + 1, numberBitsUri);
		}
		return uc;
	}

	public void encodeQNamePrefix(QNameContext qnContext, String prefix,
			EncoderChannel channel) throws IOException {
		int namespaceUriID = qnContext.getNamespaceUriID();
		
		if (namespaceUriID == 0) {
			// XMLConstants.NULL_NS_URI
			// default namespace --> DEFAULT_NS_PREFIX
		} else {
			EvolvingUriContext uriContext = this.getUriContext(namespaceUriID);
			int numberOfPrefixes = uriContext.getNumberOfPrefixes();
			if (numberOfPrefixes > 1) {
				int pfxID = uriContext.getPrefixID(prefix);
				if (pfxID == Constants.NOT_FOUND) {
					// choose *one* prefix which gets modified by
					// local-element-ns anyway ?
					pfxID = 0;
				}

				// overlapping URIs
				channel.encodeNBitUnsignedInteger(pfxID,
						MethodsBag.getCodingLength(numberOfPrefixes));
			} else {
				/*
				 * #1# Possibility If there are no prefixes specified for the
				 * URI of the QName by preceding NS events in the EXI stream,
				 * the prefix is undefined. An undefined prefix is represented
				 * using zero bits (i.e., omitted).
				 * 
				 * #2# Possibility If there is only one prefix, the prefix is
				 * implicit
				 */
			}
		}
	}

	public void encodeNamespacePrefix(EvolvingUriContext uriContext,
			String prefix, EncoderChannel channel) throws IOException {

		int nPfx = MethodsBag
				.getCodingLength(uriContext.getNumberOfPrefixes() + 1); // n-bit
		int pfxID = uriContext.getPrefixID(prefix);

		if (pfxID == Constants.NOT_FOUND) {
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

}
