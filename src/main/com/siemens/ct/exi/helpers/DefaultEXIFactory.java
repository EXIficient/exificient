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

package com.siemens.ct.exi.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xml.sax.XMLReader;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXIWriter;
import com.siemens.ct.exi.core.EXIDecoderInOrder;
import com.siemens.ct.exi.core.EXIDecoderReordered;
import com.siemens.ct.exi.core.EXIEncoderPrefixAware;
import com.siemens.ct.exi.core.EXIEncoderPrefixLess;
import com.siemens.ct.exi.core.sax.SAXDecoder;
import com.siemens.ct.exi.core.sax.SAXEncoderPrefixAware;
import com.siemens.ct.exi.core.sax.SAXEncoderPrefixLess;
import com.siemens.ct.exi.datatype.DatatypeRepresentation;
import com.siemens.ct.exi.datatype.decoder.TypeDecoder;
import com.siemens.ct.exi.datatype.decoder.TypeDecoderDatatypeRepresentationMap;
import com.siemens.ct.exi.datatype.decoder.TypeDecoderLexical;
import com.siemens.ct.exi.datatype.decoder.TypeDecoderRepresentationMap;
import com.siemens.ct.exi.datatype.decoder.TypeDecoderString;
import com.siemens.ct.exi.datatype.decoder.TypeDecoderTyped;
import com.siemens.ct.exi.datatype.encoder.TypeEncoder;
import com.siemens.ct.exi.datatype.encoder.TypeEncoderDatatypeRespresentationMap;
import com.siemens.ct.exi.datatype.encoder.TypeEncoderLexical;
import com.siemens.ct.exi.datatype.encoder.TypeEncoderString;
import com.siemens.ct.exi.datatype.encoder.TypeEncoderTyped;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.io.block.DecoderBitBlock;
import com.siemens.ct.exi.io.block.DecoderBlock;
import com.siemens.ct.exi.io.block.DecoderByteBlock;
import com.siemens.ct.exi.io.block.DecoderByteBlockCompression;
import com.siemens.ct.exi.io.block.DecoderByteBlockPreCompression;
import com.siemens.ct.exi.io.block.EncoderBitBlock;
import com.siemens.ct.exi.io.block.EncoderBlock;
import com.siemens.ct.exi.io.block.EncoderByteBlock;
import com.siemens.ct.exi.io.block.EncoderByteBlockCompression;
import com.siemens.ct.exi.io.block.EncoderByteBlockPreCompression;

/**
 * 
 * This is the default implementation of an <code>EXIFactory</code> class.
 * 
 * @see EXIFactory
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081112
 */

public class DefaultEXIFactory implements EXIFactory {

	protected Grammar grammar;
	protected boolean isFragment;
	protected CodingMode codingMode;

	protected FidelityOptions fidelityOptions;

	protected DatatypeRepresentation[] userDefinedDatatypeRepresentations;

	protected boolean exiBodyOnly = false; // default: false

	protected DefaultEXIFactory() {
	}

	protected static void setDefaultValues(EXIFactory factory) {
		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setFragment(false);
		factory.setGrammar(GrammarFactory.newInstance()
				.createSchemaLessGrammar());
	}

	public static EXIFactory newInstance() {
		EXIFactory factory = new DefaultEXIFactory();

		// set default values
		setDefaultValues(factory);

		return factory;
	}

	public void setFidelityOptions(FidelityOptions fidelityOptions) {
		this.fidelityOptions = fidelityOptions;
	}

	public FidelityOptions getFidelityOptions() {
		return fidelityOptions;
	}

	public void setDatatypeRepresentationMap(
			DatatypeRepresentation[] datatypeRepresentations) {
		this.userDefinedDatatypeRepresentations = datatypeRepresentations;
	}

	public void setGrammar(Grammar grammar) {
		assert (grammar != null);

		this.grammar = grammar;
	}

	public Grammar getGrammar() {
		return this.grammar;
	}

	protected boolean isSchemaInformed() {
		return grammar.isSchemaInformed();
	}

	public void setFragment(boolean isFragment) {
		this.isFragment = isFragment;
	}

	public boolean isFragment() {
		return isFragment;
	}

	public void setCodingMode(CodingMode codingMode) {
		this.codingMode = codingMode;
	}

	public CodingMode getCodingMode() {
		return this.codingMode;
	}

	public void setEXIBodyOnly(boolean exiBodyOnly) {
		this.exiBodyOnly = exiBodyOnly;
	}

	public boolean isEXIBodyOnly() {
		return exiBodyOnly;
	}

	public EXIEncoder createEXIEncoder() {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)) {
			return new EXIEncoderPrefixAware(this);
		} else {
			return new EXIEncoderPrefixLess(this);
		}
	}

	public EXIWriter createEXIWriter() {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)) {
			return new SAXEncoderPrefixAware(this);
		} else {
			return new SAXEncoderPrefixLess(this);
		}
	}

	public EXIDecoder createEXIDecoder() {
		if (codingMode.usesRechanneling()) {
			return new EXIDecoderReordered(this);
		} else {
			return new EXIDecoderInOrder(this);
		}
	}

	public XMLReader createEXIReader() {
		return new SAXDecoder(this, exiBodyOnly);
	}

	public TypeEncoder createTypeEncoder() {
		TypeEncoder typeEncoder;

		// create new type encoder
		if (isSchemaInformed()) {
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
				// use restricted characters sets
				typeEncoder = new TypeEncoderLexical(this);
			} else {
				if (userDefinedDatatypeRepresentations != null
						&& userDefinedDatatypeRepresentations.length > 0) {
					TypeEncoderDatatypeRespresentationMap enc = new TypeEncoderDatatypeRespresentationMap(
							this);

					for (int i = 0; i < userDefinedDatatypeRepresentations.length; i++) {
						enc
								.registerDatatypeRepresentation(userDefinedDatatypeRepresentations[i]);
					}

					typeEncoder = enc;
				} else {
					// use default type encoders
					typeEncoder = new TypeEncoderTyped(this);
				}
			}

		} else {
			// use string only
			typeEncoder = new TypeEncoderString(this);
		}
		
		// populate type-encoder string table
		getGrammar().populateStringTable(typeEncoder.getStringTable());

		return typeEncoder;
	}

	public TypeDecoder createTypeDecoder() {
		TypeDecoder typeDecoder;

		// create new type-decoder
		if (isSchemaInformed()) {
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
				typeDecoder = new TypeDecoderLexical(this);
			} else {
				if (userDefinedDatatypeRepresentations != null
						&& userDefinedDatatypeRepresentations.length > 0) {
					TypeDecoderRepresentationMap dec = new TypeDecoderDatatypeRepresentationMap(
							this);

					for (int i = 0; i < userDefinedDatatypeRepresentations.length; i++) {
						dec
								.registerDatatypeRepresentation(userDefinedDatatypeRepresentations[i]);
					}

					typeDecoder = dec;
				} else {
					// use default type decoders
					typeDecoder = new TypeDecoderTyped(this);
				}
			}
		} else {
			// strings only
			typeDecoder = new TypeDecoderString(this);
		}
		
		// populate type-encoder string table
		getGrammar().populateStringTable(typeDecoder.getStringTable());

		return typeDecoder;
	}

	public EncoderBlock createEncoderBlock(OutputStream outputStream) {
		EncoderBlock encBlock;

		TypeEncoder typeEncoder = this.createTypeEncoder();

		switch (codingMode) {
		case BIT_PACKED:
			encBlock = new EncoderBitBlock(outputStream, typeEncoder);
			break;
		case BYTE_PACKED:
			encBlock = new EncoderByteBlock(outputStream, typeEncoder);
			break;
		case PRE_COMPRESSION:
			encBlock = new EncoderByteBlockPreCompression(outputStream,
					typeEncoder);
			break;
		case COMPRESSION:
			encBlock = new EncoderByteBlockCompression(outputStream,
					typeEncoder);
			break;
		default:
			throw new IllegalArgumentException("Unknown CodingMode!");
		}

		return encBlock;
	}

	public DecoderBlock createDecoderBlock(InputStream inputStream)
			throws IOException {
		DecoderBlock decBlock;

		TypeDecoder typeDecoder = this.createTypeDecoder();

		switch (codingMode) {
		case BIT_PACKED:
			decBlock = new DecoderBitBlock(inputStream, typeDecoder);
			break;
		case BYTE_PACKED:
			decBlock = new DecoderByteBlock(inputStream, typeDecoder);
			break;
		case PRE_COMPRESSION:
			decBlock = new DecoderByteBlockPreCompression(inputStream,
					typeDecoder);
			break;
		case COMPRESSION:
			decBlock = new DecoderByteBlockCompression(inputStream, typeDecoder);
			break;
		default:
			throw new IllegalArgumentException("Unknown CodingMode!");
		}

		return decBlock;
	}
}
