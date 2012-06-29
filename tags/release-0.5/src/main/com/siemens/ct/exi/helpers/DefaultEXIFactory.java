/*
 * Copyright (C) 2007-2010 Siemens AG
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

import java.io.OutputStream;

import javax.xml.namespace.QName;

import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
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
import com.siemens.ct.exi.datatype.strings.BoundedStringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.BoundedStringEncoderImpl;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.datatype.strings.StringEncoderImpl;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.types.DatatypeRepresentationMapTypeDecoder;
import com.siemens.ct.exi.types.DatatypeRepresentationMapTypeEncoder;
import com.siemens.ct.exi.types.LexicalTypeDecoder;
import com.siemens.ct.exi.types.LexicalTypeEncoder;
import com.siemens.ct.exi.types.StringTypeDecoder;
import com.siemens.ct.exi.types.StringTypeEncoder;
import com.siemens.ct.exi.types.TypeDecoder;
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
 * @version 0.4.20090331
 */

public class DefaultEXIFactory implements EXIFactory {

	protected Grammar grammar;
	protected boolean isFragment;
	protected CodingMode codingMode;

	protected FidelityOptions fidelityOptions;

	protected QName[] dtrMapTypes;
	protected QName[] dtrMapRepresentations;
	// protected DatatypeRepresentation[] userDefinedDatatypeRepresentations;

	protected QName[] scElements;

	/* default: false */
	protected boolean exiBodyOnly = false;

	/* default: 1,000,000 */
	protected int blockSize = Constants.DEFAULT_BLOCK_SIZE;

	/* default: -1 == unbounded */
	protected int valueMaxLength = Constants.DEFAULT_VALUE_MAX_LENGTH;

	/* default: -1 == unbounded */
	protected int valuePartitionCapacity = Constants.DEFAULT_VALUE_PARTITON_CAPACITY;

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

	public void setFidelityOptions(FidelityOptions fidelityOptions)  {
		this.fidelityOptions = fidelityOptions;
	}

	public FidelityOptions getFidelityOptions() {
		return fidelityOptions;
	}

	public void setDatatypeRepresentationMap(QName[] dtrMapTypes,
			QName[] dtrMapRepresentations) {
		if (dtrMapTypes == null || dtrMapRepresentations == null
				|| dtrMapTypes.length != dtrMapRepresentations.length
				|| dtrMapTypes.length == 0) {
			// un-set dtrMap
			this.dtrMapTypes = null;
			this.dtrMapRepresentations = null;
		} else {
			this.dtrMapTypes = dtrMapTypes;
			this.dtrMapRepresentations = dtrMapRepresentations;
		}
	}

	public void setSelfContainedElements(QName[] scElements) {
		this.scElements = scElements;
	}

	public boolean isSelfContainedElement(QName element) {
		if (scElements != null && scElements.length > 0) {
			for (QName qname : scElements) {
				if (qname.equals(element)) {
					return true;
				}
			}
		}
		return false;
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

	public void setBlockSize(int blockSize) {
		if (blockSize < 0) {
			throw new RuntimeException(
					"EXI's blockSize has the be a positive number!");
		}
		this.blockSize = blockSize;
	}

	public int getBlockSize() {
		return blockSize;
	}

	public void setValueMaxLength(int valueMaxLength) {
		this.valueMaxLength = valueMaxLength;
	}

	public int getValueMaxLength() {
		return valueMaxLength;
	}

	public void setValuePartitionCapacity(int valuePartitionCapacity) {
		this.valuePartitionCapacity = valuePartitionCapacity;
	}

	public int getValuePartitionCapacity() {
		return valuePartitionCapacity;
	}
	
	protected void doSanityCheck() throws EXIException {
		// some consistency checks
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC) && codingMode.usesRechanneling()) {
			throw new EXIException("(Pre-)Compression and selfContained elements cannot work together");
		}
		// blockSize in NON compression mode? Just ignore it!
	}

	public EXIEncoder createEXIEncoder() throws EXIException {
		doSanityCheck();
		
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

	public DefaultHandler2 createEXIWriter(OutputStream os) throws EXIException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			return new SAXEncoderExtendedHandler(this, os);
		} else {
			return new SAXEncoder(this, os);
		}
	}

	public EXIDecoder createEXIDecoder() throws EXIException {
		doSanityCheck();
		
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

	public XMLReader createEXIReader() throws EXIException {
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

	public TypeEncoder createTypeEncoder() throws EXIException {
		// string encoder
		StringEncoder stringEncoder;
		if (getValueMaxLength() != Constants.DEFAULT_VALUE_MAX_LENGTH
				|| getValuePartitionCapacity() != Constants.DEFAULT_VALUE_PARTITON_CAPACITY) {
			stringEncoder = new BoundedStringEncoderImpl(getValueMaxLength(),
					getValuePartitionCapacity());
		} else {
			stringEncoder = new StringEncoderImpl();
		}

		TypeEncoder typeEncoder;

		// create new type encoder
		if (isSchemaInformed()) {
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
				// use restricted characters sets
				typeEncoder = new LexicalTypeEncoder(stringEncoder);
			} else {
				if (dtrMapTypes != null) {
					assert(dtrMapTypes.length == dtrMapRepresentations.length);
					typeEncoder = new DatatypeRepresentationMapTypeEncoder(
							stringEncoder, dtrMapTypes, dtrMapRepresentations, grammar);
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

	public TypeDecoder createTypeDecoder() throws EXIException {
		// string Decoder
		StringDecoder stringDecoder;
		if (getValueMaxLength() != Constants.DEFAULT_VALUE_MAX_LENGTH
				|| getValuePartitionCapacity() != Constants.DEFAULT_VALUE_PARTITON_CAPACITY) {
			stringDecoder = new BoundedStringDecoderImpl(getValueMaxLength(),
					getValuePartitionCapacity());
		} else {
			stringDecoder = new StringDecoderImpl();
		}

		TypeDecoder typeDecoder;

		// create new type-decoder
		if (isSchemaInformed()) {
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
				typeDecoder = new LexicalTypeDecoder(stringDecoder);
			} else {
				if (dtrMapTypes != null) {
					assert(dtrMapTypes.length == dtrMapRepresentations.length);
					typeDecoder = new DatatypeRepresentationMapTypeDecoder(
							stringDecoder, dtrMapTypes, dtrMapRepresentations, grammar);
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

	@Override
	public EXIFactory clone() {
		// create new instance
		EXIFactory copy = newInstance();
		// shallow copy
		copy.setCodingMode(codingMode);
		copy.setDatatypeRepresentationMap(dtrMapTypes, dtrMapRepresentations);
		copy.setEXIBodyOnly(exiBodyOnly);
		copy.setFidelityOptions(fidelityOptions);
		copy.setFragment(isFragment);
		copy.setGrammar(grammar);
		copy.setSelfContainedElements(scElements);
		// return...
		return copy;
	}

}