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

import org.xml.sax.XMLReader;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXIWriter;
import com.siemens.ct.exi.core.EXIDecoderInOrder;
import com.siemens.ct.exi.core.EXIDecoderInOrderSC;
import com.siemens.ct.exi.core.EXIDecoderReordered;
import com.siemens.ct.exi.core.EXIEncoderInOrder;
import com.siemens.ct.exi.core.EXIEncoderInOrderSC;
import com.siemens.ct.exi.core.EXIEncoderReordered;
import com.siemens.ct.exi.core.sax.SAXDecoder;
import com.siemens.ct.exi.core.sax.SAXDecoderExtendedHandler;
import com.siemens.ct.exi.core.sax.SAXEncoder;
import com.siemens.ct.exi.core.sax.SAXEncoderExtendedHandler;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.datatype.strings.StringEncoderImpl;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.types.DatatypeRepresentation;
import com.siemens.ct.exi.types.DatatypeRepresentationMapTypeDecoder;
import com.siemens.ct.exi.types.DatatypeRespresentationMapTypeEncoder;
import com.siemens.ct.exi.types.LexicalTypeDecoder;
import com.siemens.ct.exi.types.LexicalTypeEncoder;
import com.siemens.ct.exi.types.StringTypeDecoder;
import com.siemens.ct.exi.types.StringTypeEncoder;
import com.siemens.ct.exi.types.TypeDecoder;
import com.siemens.ct.exi.types.TypeDecoderRepresentationMap;
import com.siemens.ct.exi.types.TypeEncoder;
import com.siemens.ct.exi.types.TypedTypeDecoder;
import com.siemens.ct.exi.types.TypedTypeEncoder;

/**
 * 
 * This is the default implementation of an <code>EXIFactory</code> class.
 * 
 * @see EXIFactory
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090331
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
		if (codingMode.usesRechanneling()) {
			return new EXIEncoderReordered(this);
		} else {
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)) {
				return new EXIEncoderInOrderSC(this);
			} else {
				return new EXIEncoderInOrder(this);
			}
		}
	}

	public EXIWriter createEXIWriter() {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			return new SAXEncoderExtendedHandler(this);
		} else {
			return new SAXEncoder(this);
		}
	}

	public EXIDecoder createEXIDecoder() {
		if (codingMode.usesRechanneling()) {
			return new EXIDecoderReordered(this);
		} else {
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)) {
				return new EXIDecoderInOrderSC(this);
			} else {
				return new EXIDecoderInOrder(this);
			}
		}
	}

	public XMLReader createEXIReader() {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			return new SAXDecoderExtendedHandler(this);
		} else {
			return new SAXDecoder(this);
		}
	}

	public TypeEncoder createTypeEncoder() {
		//	string encoder
		StringEncoder stringEncoder = new StringEncoderImpl();
		
		TypeEncoder typeEncoder;

		// create new type encoder
		if (isSchemaInformed()) {
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
				// use restricted characters sets
				typeEncoder = new LexicalTypeEncoder(stringEncoder);
			} else {
				if (userDefinedDatatypeRepresentations != null
						&& userDefinedDatatypeRepresentations.length > 0) {
					DatatypeRespresentationMapTypeEncoder enc = new DatatypeRespresentationMapTypeEncoder(stringEncoder);

					for (int i = 0; i < userDefinedDatatypeRepresentations.length; i++) {
						enc
								.registerDatatypeRepresentation(userDefinedDatatypeRepresentations[i]);
					}

					typeEncoder = enc;
				} else {
					// use default type encoders
					typeEncoder = new TypedTypeEncoder(stringEncoder);
				}
			}

		} else {
			// use strings only
			typeEncoder = new StringTypeEncoder(stringEncoder);
		}

		return typeEncoder;
	}

	public TypeDecoder createTypeDecoder() {
		
		//	string Decoder
		StringDecoder stringDecoder = new StringDecoderImpl();
		
		TypeDecoder typeDecoder;

		// create new type-decoder
		if (isSchemaInformed()) {
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
				typeDecoder = new LexicalTypeDecoder(stringDecoder);
			} else {
				if (userDefinedDatatypeRepresentations != null
						&& userDefinedDatatypeRepresentations.length > 0) {
					TypeDecoderRepresentationMap dec = new DatatypeRepresentationMapTypeDecoder(stringDecoder);

					for (int i = 0; i < userDefinedDatatypeRepresentations.length; i++) {
						dec
								.registerDatatypeRepresentation(userDefinedDatatypeRepresentations[i]);
					}

					typeDecoder = dec;
				} else {
					// use default type decoders
					typeDecoder = new TypedTypeDecoder(stringDecoder);
				}
			}
		} else {
			// strings only
			typeDecoder = new StringTypeDecoder(stringDecoder);
		}

		return typeDecoder;
	}
}
