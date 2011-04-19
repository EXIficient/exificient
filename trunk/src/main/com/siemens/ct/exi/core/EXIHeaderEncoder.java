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

package com.siemens.ct.exi.core;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.datatype.strings.StringCoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.io.channel.BitEncoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.StringValue;

/**
 * EXI Header (see http://www.w3.org/TR/exi/#header)
 * 
 * <p>
 * Encoder
 * </p>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public class EXIHeaderEncoder extends AbstractEXIHeader {

	protected static final BooleanValue BOOLEAN_VALUE_TRUE = new BooleanValue(
			true);

	public EXIHeaderEncoder() throws EXIException {
	}

	/**
	 * Writes the EXI header according to the header options with optional
	 * cookie, EXI options, ..
	 * 
	 * @param headerChannel
	 * @param f
	 * @throws EXIException
	 */
	public void write(BitEncoderChannel headerChannel, EXIFactory f)
			throws EXIException {
		try {
			EncodingOptions headerOptions = f.getEncodingOptions();
			CodingMode codingMode = f.getCodingMode();

			// EXI Cookie
			if (headerOptions.isOptionEnabled(EncodingOptions.INCLUDE_COOKIE)) {
				// four byte field consists of four characters " $ " , " E ",
				// " X " and " I " in that order
				headerChannel.encode('$');
				headerChannel.encode('E');
				headerChannel.encode('X');
				headerChannel.encode('I');
			}

			// Distinguishing Bits 10
			headerChannel.encodeNBitUnsignedInteger(2, 2);

			// Presence Bit for EXI Options 0
			boolean includeOptions = headerOptions
					.isOptionEnabled(EncodingOptions.INCLUDE_OPTIONS);
			headerChannel.encodeBoolean(includeOptions);

			// EXI Format Version 0-0000
			headerChannel.encodeBoolean(false); // preview
			headerChannel.encodeNBitUnsignedInteger(0, 4);

			// EXI Header options and so forth
			if (includeOptions) {
				writeEXIOptions(f, headerChannel);
			}

			// other than bit-packed requires [Padding Bits]
			if (codingMode != CodingMode.BIT_PACKED) {
				headerChannel.align();
				headerChannel.flush();
			}

		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	protected void writeEXIOptions(EXIFactory f, EncoderChannel encoderChannel)
			throws EXIException, IOException {

		EXIBodyEncoderInOrder encoder = (EXIBodyEncoderInOrder) getHeaderFactory()
				.createEXIBodyEncoder();
		encoder.setOutputChannel(encoderChannel);

		encoder.encodeStartDocument();
		encoder.encodeStartElement(Constants.W3C_EXI_NS_URI, HEADER, null);

		/*
		 * lesscommon
		 */
		if (isLessCommon(f)) {
			encoder.encodeStartElement(Constants.W3C_EXI_NS_URI, LESSCOMMON,
					null);
			/*
			 * uncommon
			 */
			if (isUncommon(f)) {
				encoder.encodeStartElement(Constants.W3C_EXI_NS_URI, UNCOMMON,
						null);
				/*
				 * isUserDefinedMetaData
				 */
				if (isUserDefinedMetaData(f)) {
					// TODO what could that be?
					throw new RuntimeException(
							"[EXI-Header] no support for user defined meta-data");
				}
				/*
				 * alignment
				 */
				if (isAlignment(f)) {
					encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
							ALIGNMENT, null);

					/*
					 * byte
					 */
					if (isByte(f)) {
						encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
								BYTE, null);
						encoder.encodeEndElement(); // byte
					}
					/*
					 * pre-compress
					 */
					if (isPreCompress(f)) {
						encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
								PRE_COMPRESS, null);
						encoder.encodeEndElement(); // pre-compress
					}

					encoder.encodeEndElement(); // alignment
				}

				/*
				 * selfContained
				 */
				if (isSelfContained(f)) {
					encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
							SELF_CONTAINED, null);
					encoder.encodeEndElement();
				}

				/*
				 * valueMaxLength
				 */
				if (isValueMaxLength(f)) {
					encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
							VALUE_MAX_LENGTH, null);
					// encoder.encodeCharacters(f.getValueMaxLength() + "");
					encoder.encodeCharacters(IntegerValue.valueOf(f
							.getValueMaxLength()));
					encoder.encodeEndElement();
				}

				/*
				 * valuePartitionCapacity
				 */
				if (isValuePartitionCapacity(f)) {
					encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
							VALUE_PARTITION_CAPACITY, null);
					// encoder.encodeCharacters(f.getValuePartitionCapacity()+
					// "");
					encoder.encodeCharacters(IntegerValue.valueOf(f
							.getValuePartitionCapacity()));
					encoder.encodeEndElement();
				}

				/*
				 * datatypeRepresentationMap
				 */
				if (isDatatypeRepresentationMap(f)) {

					QName[] types = f.getDatatypeRepresentationMapTypes();
					QName[] representations = f
							.getDatatypeRepresentationMapRepresentations();
					assert (types.length == representations.length);

					// sequence "schema datatype" + datatype representation
					for (int i = 0; i < types.length; i++) {
						encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
								DATATYPE_REPRESENTATION_MAP, null);

						// schema datatype
						QName type = types[i];
						encoder.encodeStartElement(type.getNamespaceURI(),
								type.getLocalPart(), null);
						encoder.encodeEndElement();

						// datatype representation
						QName representation = representations[i];
						encoder.encodeStartElement(
								representation.getNamespaceURI(),
								representation.getLocalPart(), null);
						encoder.encodeEndElement();

						encoder.encodeEndElement(); // datatypeRepresentationMap
					}

				}

				encoder.encodeEndElement(); // uncommon
			}

			/*
			 * preserve
			 */
			if (isPreserve(f)) {
				encoder.encodeStartElement(Constants.W3C_EXI_NS_URI, PRESERVE,
						null);

				FidelityOptions fo = f.getFidelityOptions();

				/*
				 * dtd
				 */
				if (fo.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
					encoder.encodeStartElement(Constants.W3C_EXI_NS_URI, DTD,
							null);
					encoder.encodeEndElement();
				}
				/*
				 * prefixes
				 */
				if (fo.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)) {
					encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
							PREFIXES, null);
					encoder.encodeEndElement();
				}
				/*
				 * lexicalValues
				 */
				if (fo.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
					encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
							LEXICAL_VALUES, null);
					encoder.encodeEndElement();
				}
				/*
				 * comments
				 */
				if (fo.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)) {
					encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
							COMMENTS, null);
					encoder.encodeEndElement();
				}
				/*
				 * pis
				 */
				if (fo.isFidelityEnabled(FidelityOptions.FEATURE_PI)) {
					encoder.encodeStartElement(Constants.W3C_EXI_NS_URI, PIS,
							null);
					encoder.encodeEndElement();
				}

				encoder.encodeEndElement(); // preserve
			}

			/*
			 * blockSize
			 */
			if (isBlockSize(f)) {
				encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
						BLOCK_SIZE, null);
				// TODO typed fashion
				// encoder.encodeCharacters(f.getBlockSize() + "");
				encoder.encodeCharacters(IntegerValue.valueOf(f.getBlockSize()));
				encoder.encodeEndElement();
			}

			encoder.encodeEndElement(); // lesscommon
		}

		/*
		 * common
		 */
		if (isCommon(f)) {
			encoder.encodeStartElement(Constants.W3C_EXI_NS_URI, COMMON, null);
			/*
			 * compression
			 */
			if (isCompression(f)) {
				encoder.encodeStartElement(Constants.W3C_EXI_NS_URI,
						COMPRESSION, null);
				encoder.encodeEndElement();
			}
			/*
			 * fragment
			 */
			if (isFragment(f)) {
				encoder.encodeStartElement(Constants.W3C_EXI_NS_URI, FRAGMENT,
						null);
				encoder.encodeEndElement();
			}
			/*
			 * schemaId
			 */
			if (isSchemaId(f)) {
				encoder.encodeStartElement(Constants.W3C_EXI_NS_URI, SCHEMA_ID,
						null);

				Grammar g = f.getGrammar();

				// When the value of the "schemaID" element is empty, no user
				// defined schema information is used for processing the EXI
				// body; however, the built-in XML schema types are available
				// for use in the EXI body.
				if (g.isBuiltInXMLSchemaTypesOnly()) {
					assert (Constants.EMPTY_STRING.equals(g.getSchemaId()));
					// encoder.encodeCharacters(Constants.EMPTY_STRING);
					encoder.encodeCharacters(StringCoder.EMPTY_STRING_VALUE);
				} else {
					if (g.isSchemaInformed()) {
						// schema-informed
						// An example schemaID scheme is the use of URI that is
						// apt for globally identifying schema resources on the
						// Web.

						// HeaderOptions ho = f.getHeaderOptions();
						// Object schemaId =
						// ho.getOptionValue(HeaderOptions.INCLUDE_SCHEMA_ID);
						String schemaId = g.getSchemaId();
						assert (schemaId != null && schemaId.length() > 0);
						// encoder.encodeCharacters(schemaId.toString());
						encoder.encodeCharacters(new StringValue(schemaId));
					} else {
						// schema-less
						// When the "schemaID" element in the EXI options
						// document
						// contains the xsi:nil attribute with its value set to
						// true, no
						// schema information is used for processing the EXI
						// body.
						// TODO typed fashion
						encoder.encodeAttributeXsiNil(BOOLEAN_VALUE_TRUE, null);
					}
				}

				encoder.encodeEndElement();
			}

			encoder.encodeEndElement(); // common
		}

		/*
		 * strict
		 */
		if (isStrict(f)) {
			encoder.encodeStartElement(Constants.W3C_EXI_NS_URI, STRICT, null);
			encoder.encodeEndElement();
		}

		encoder.encodeEndElement(); // header
		encoder.encodeEndDocument();
	}

	protected boolean isLessCommon(EXIFactory f) {
		return (isUncommon(f) || isPreserve(f) || isBlockSize(f));
	}

	protected boolean isUncommon(EXIFactory f) {
		// user defined meta-data, alignment, selfContained, valueMaxLength,
		// valuePartitionCapacity, datatypeRepresentationMap
		return (isUserDefinedMetaData(f) || isAlignment(f)
				|| isSelfContained(f) || isValueMaxLength(f)
				|| isValuePartitionCapacity(f) || isDatatypeRepresentationMap(f));
	}

	protected boolean isUserDefinedMetaData(EXIFactory f) {
		// TODO
		return false;
	}

	protected boolean isAlignment(EXIFactory f) {
		// byte, pre-compress
		return (isByte(f) || isPreCompress(f));
	}

	protected boolean isByte(EXIFactory f) {
		return (f.getCodingMode() == CodingMode.BYTE_PACKED);
	}

	protected boolean isPreCompress(EXIFactory f) {
		return (f.getCodingMode() == CodingMode.PRE_COMPRESSION);
	}

	protected boolean isSelfContained(EXIFactory f) {
		return f.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_SC);
	}

	protected boolean isValueMaxLength(EXIFactory f) {
		return (f.getValueMaxLength() != Constants.DEFAULT_VALUE_MAX_LENGTH);
	}

	protected boolean isValuePartitionCapacity(EXIFactory f) {
		return (f.getValuePartitionCapacity() >= 0);
	}

	protected boolean isDatatypeRepresentationMap(EXIFactory f) {
		return (f.getDatatypeRepresentationMapTypes() != null);
	}

	protected boolean isPreserve(EXIFactory f) {
		FidelityOptions fo = f.getFidelityOptions();
		// dtd, prefixes, lexicalValues, comments, pis
		return (fo.isFidelityEnabled(FidelityOptions.FEATURE_DTD)
				|| fo.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)
				|| fo.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)
				|| fo.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT) || fo
				.isFidelityEnabled(FidelityOptions.FEATURE_PI));
	}

	protected boolean isBlockSize(EXIFactory f) {
		return (f.getBlockSize() != Constants.DEFAULT_BLOCK_SIZE);
	}

	protected boolean isCommon(EXIFactory f) {
		// compression, fragment, schemaId
		return (isCompression(f) || isFragment(f) || isSchemaId(f));
	}

	protected boolean isCompression(EXIFactory f) {
		return (f.getCodingMode() == CodingMode.COMPRESSION);
	}

	protected boolean isFragment(EXIFactory f) {
		return f.isFragment();
	}

	protected boolean isSchemaId(EXIFactory f) {
		EncodingOptions ho = f.getEncodingOptions();
		return ho.isOptionEnabled(EncodingOptions.INCLUDE_SCHEMA_ID);
	}

	protected boolean isStrict(EXIFactory f) {
		return f.getFidelityOptions().isStrict();
	}

}
