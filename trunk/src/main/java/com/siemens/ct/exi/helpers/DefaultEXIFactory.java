/*
 * Copyright (C) 2007-2015 Siemens AG
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

import java.util.Arrays;

import javax.xml.namespace.QName;

import org.xml.sax.XMLReader;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.DecodingOptions;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.SchemaIdResolver;
import com.siemens.ct.exi.api.sax.SAXDecoder;
import com.siemens.ct.exi.api.sax.SAXEncoder;
import com.siemens.ct.exi.api.sax.SAXEncoderExtendedHandler;
import com.siemens.ct.exi.core.EXIBodyDecoderInOrder;
import com.siemens.ct.exi.core.EXIBodyDecoderInOrderSC;
import com.siemens.ct.exi.core.EXIBodyDecoderReordered;
import com.siemens.ct.exi.core.EXIBodyEncoderInOrder;
import com.siemens.ct.exi.core.EXIBodyEncoderInOrderSC;
import com.siemens.ct.exi.core.EXIBodyEncoderReordered;
import com.siemens.ct.exi.datatype.strings.BoundedStringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.BoundedStringEncoderImpl;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.datatype.strings.StringEncoderImpl;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.SchemaInformedGrammars;
import com.siemens.ct.exi.grammars.SchemaLessGrammars;
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
 * @version 0.9.4-SNAPSHOT
 */

public class DefaultEXIFactory implements EXIFactory {

	protected Grammars grammar;
	protected boolean isFragment;
	protected CodingMode codingMode;

	protected FidelityOptions fidelityOptions;
	protected EncodingOptions encodingOptions;
	protected DecodingOptions decodingOptions;

	protected SchemaIdResolver schemaIdResolver;

	protected QName[] dtrMapTypes;
	protected QName[] dtrMapRepresentations;

	protected QName[] scElements;

	/* default: 1,000,000 */
	protected int blockSize = Constants.DEFAULT_BLOCK_SIZE;

	/* default: -1 == unbounded */
	protected int valueMaxLength = Constants.DEFAULT_VALUE_MAX_LENGTH;

	/* default: -1 == unbounded */
	protected int valuePartitionCapacity = Constants.DEFAULT_VALUE_PARTITON_CAPACITY;

	/* default: true */
	protected boolean localValuePartitions = true;

	/* default: unbounded (-1) */
	protected int maximumNumberOfBuiltInElementGrammars = -1;
	protected int maximumNumberOfBuiltInProductions = -1;
	/* default: false */
	protected boolean grammarLearningDisabled = false;

	// /* default: no profile */
	// protected String profile;

	/* default: use no specify bod coder */
	protected EXIBodyEncoder bodyEncoder;
	protected EXIBodyDecoder bodyDecoder;

	protected DefaultEXIFactory() {
	}

	protected static void setDefaultValues(EXIFactory factory) {
		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setEncodingOptions(EncodingOptions.createDefault());
		factory.setDecodingOptions(DecodingOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setFragment(false);
		// factory.setGrammar(GrammarFactory.newInstance()
		// .createSchemaLessGrammar());
		factory.setGrammars(new SchemaLessGrammars());

		factory.setSchemaIdResolver(new DefaultSchemaIdResolver());
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

	// public void setProfile(String profileName) throws UnsupportedOption {
	// if (profileName == null) {
	// // un-set profile
	// this.profile = profileName;
	// // TODO profile(s)
	// } else if (UCD_PROFILE.equals(profileName)) {
	// this.profile = profileName;
	// // what does the profile define
	// // 1. valuePartitionCapacity == 0
	// this.setValuePartitionCapacity(0);
	// // 2. no built-in grammars --> no learning
	// // 3. no EXI Options in header
	// } else {
	// throw new UnsupportedOption("Profile '" + profileName
	// + "' unknown.");
	// }
	// }
	//
	// public boolean usesProfile(String profileName) {
	// return (profileName.equals(this.profile));
	// }

	public void setEncodingOptions(EncodingOptions encodingOptions) {
		this.encodingOptions = encodingOptions;
	}

	public EncodingOptions getEncodingOptions() {
		return encodingOptions;
	}
	
	public void setDecodingOptions(DecodingOptions decodingOptions) {
		this.decodingOptions = decodingOptions;
	}

	public DecodingOptions getDecodingOptions() {
		return decodingOptions;
	}

	public void setSchemaIdResolver(SchemaIdResolver schemaIdResolver) {
		this.schemaIdResolver = schemaIdResolver;
	}

	public SchemaIdResolver getSchemaIdResolver() {
		return this.schemaIdResolver;
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

	public QName[] getDatatypeRepresentationMapTypes() {
		return dtrMapTypes;
	}

	public QName[] getDatatypeRepresentationMapRepresentations() {
		return dtrMapRepresentations;
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

	public void setGrammars(Grammars grammar) {
		assert (grammar != null);

		this.grammar = grammar;
	}

	public Grammars getGrammars() {
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

	public void setLocalValuePartitions(boolean useLocalValuePartitions) {
		this.localValuePartitions = useLocalValuePartitions;
	}

	public boolean isLocalValuePartitions() {
		return localValuePartitions;
	}

	public void setMaximumNumberOfBuiltInElementGrammars(
			int maximumNumberOfBuiltInElementGrammars) {
		if (maximumNumberOfBuiltInElementGrammars >= 0) {
			this.maximumNumberOfBuiltInElementGrammars = maximumNumberOfBuiltInElementGrammars;
		} else {
			this.maximumNumberOfBuiltInElementGrammars = -1;
		}
		checkGrammarLearningDisabled();
	}

	public int getMaximumNumberOfBuiltInElementGrammars() {
		return this.maximumNumberOfBuiltInElementGrammars;
	}

	public void setMaximumNumberOfBuiltInProductions(
			int maximumNumberOfBuiltInProductions) {
		if (maximumNumberOfBuiltInProductions >= 0) {
			this.maximumNumberOfBuiltInProductions = maximumNumberOfBuiltInProductions;
		} else {
			this.maximumNumberOfBuiltInProductions = -1;
		}
		checkGrammarLearningDisabled();
	}

	public int getMaximumNumberOfBuiltInProductions() {
		return this.maximumNumberOfBuiltInProductions;
	}

	private void checkGrammarLearningDisabled() {
		if (maximumNumberOfBuiltInElementGrammars >= 0
				|| maximumNumberOfBuiltInProductions >= 0) {
			grammarLearningDisabled = true;
		} else {
			grammarLearningDisabled = false;
		}
	}

	public boolean isGrammarLearningDisabled() {
		return this.grammarLearningDisabled;
	}

	// some consistency and sanity checks
	protected void doSanityCheck() throws EXIException {

		// Self-contained elements do not work with re-ordered
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)
				&& codingMode.usesRechanneling()) {
			throw new EXIException(
					"(Pre-)Compression and selfContained elements cannot work together");
		}

		if (!this.grammar.isSchemaInformed()) {
			this.maximumNumberOfBuiltInElementGrammars = -1;
			this.maximumNumberOfBuiltInProductions = -1;
			this.grammarLearningDisabled = false;
			// TODO warn user?
		}

		// blockSize in NON compression mode? Just ignore it!
	}

	public void setEXIBodyEncoder(String className) throws EXIException {
		try {
			ClassLoader classLoader = DefaultEXIFactory.class.getClassLoader();
			Class<?> aClass = classLoader.loadClass(className);
			Object aObject = aClass.newInstance();
			if (!EXIBodyEncoder.class.isInstance(aObject)) {
				throw new EXIException("Class does not implemement "
						+ EXIBodyEncoder.class);
			}

			setEXIBodyEncoder((EXIBodyEncoder) aObject);

		} catch (ClassNotFoundException e) {
			throw new EXIException(e);
		} catch (InstantiationException e) {
			throw new EXIException(e);
		} catch (IllegalAccessException e) {
			throw new EXIException(e);
		}
	}

	public void setEXIBodyEncoder(EXIBodyEncoder bodyEncoder)
			throws EXIException {
		this.bodyEncoder = bodyEncoder;
	}

	public void setEXIBodyDecoder(String className) throws EXIException {
		try {
			ClassLoader classLoader = DefaultEXIFactory.class.getClassLoader();
			Class<?> aClass = classLoader.loadClass(className);
			Object aObject = aClass.newInstance();
			if (!EXIBodyDecoder.class.isInstance(aObject)) {
				throw new EXIException("Class does not implemement "
						+ EXIBodyDecoder.class);
			}

			setEXIBodyDecoder((EXIBodyDecoder) aObject);

		} catch (ClassNotFoundException e) {
			throw new EXIException(e);
		} catch (InstantiationException e) {
			throw new EXIException(e);
		} catch (IllegalAccessException e) {
			throw new EXIException(e);
		}
	}

	public void setEXIBodyDecoder(EXIBodyDecoder bodyDecoder)
			throws EXIException {
		this.bodyDecoder = bodyDecoder;
	}

	public EXIBodyEncoder createEXIBodyEncoder() throws EXIException {
		if (bodyEncoder != null) {
			return bodyEncoder;
		}

		doSanityCheck();

		// if (ContextEXIBodyEncoder.USE_CONTEXT_CODER) {
		// return new ContextEXIBodyEncoder(this);
		// } else {
		if (codingMode.usesRechanneling()) {
			return new EXIBodyEncoderReordered(this);
		} else {
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)) {
				return new EXIBodyEncoderInOrderSC(this);
			} else {
				return new EXIBodyEncoderInOrder(this);
			}
		}
		// }

	}

	public SAXEncoder createEXIWriter() throws EXIException {
		if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_PI)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			return new SAXEncoderExtendedHandler(this);
		} else {
			return new SAXEncoder(this);
		}
	}

	public EXIBodyDecoder createEXIBodyDecoder() throws EXIException {
		if (bodyDecoder != null) {
			return bodyDecoder;
		}

		doSanityCheck();

		if (codingMode.usesRechanneling()) {
			return new EXIBodyDecoderReordered(this);
		} else {
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)) {
				return new EXIBodyDecoderInOrderSC(this);
			} else {
				return new EXIBodyDecoderInOrder(this);
			}
		}
	}

	public XMLReader createEXIReader() throws EXIException {
		return new SAXDecoder(this);
	}

	public StringEncoder createStringEncoder() {
		// string encoder
		StringEncoder stringEncoder;
		if (getValueMaxLength() != Constants.DEFAULT_VALUE_MAX_LENGTH
				|| getValuePartitionCapacity() != Constants.DEFAULT_VALUE_PARTITON_CAPACITY) {
			stringEncoder = new BoundedStringEncoderImpl(
					isLocalValuePartitions(), getValueMaxLength(),
					getValuePartitionCapacity());
		} else {
			stringEncoder = new StringEncoderImpl(isLocalValuePartitions());
		}

		return stringEncoder;
	}

	public StringDecoder createStringDecoder() {
		// string Decoder
		StringDecoder stringDecoder;
		if (getValueMaxLength() != Constants.DEFAULT_VALUE_MAX_LENGTH
				|| getValuePartitionCapacity() != Constants.DEFAULT_VALUE_PARTITON_CAPACITY) {
			stringDecoder = new BoundedStringDecoderImpl(
					isLocalValuePartitions(), getValueMaxLength(),
					getValuePartitionCapacity());
		} else {
			stringDecoder = new StringDecoderImpl(isLocalValuePartitions());
		}

		return stringDecoder;
	}

	public TypeEncoder createTypeEncoder() throws EXIException {
		TypeEncoder typeEncoder;

		// create new type encoder
		if (isSchemaInformed()) {
			// type encoders
			checkDtrMap();

			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
				typeEncoder = new LexicalTypeEncoder(dtrMapTypes,
						dtrMapRepresentations);
			} else {
				typeEncoder = new TypedTypeEncoder(dtrMapTypes,
						dtrMapRepresentations);
			}

		} else {
			// use strings only
			typeEncoder = new StringTypeEncoder();
		}

		return typeEncoder;
	}

	private void checkDtrMap() throws EXIException {
		if (dtrMapTypes == null) {
			dtrMapRepresentations = null;
		} else {
			if (dtrMapRepresentations == null
					|| dtrMapTypes.length != dtrMapRepresentations.length) {
				throw new EXIException(
						"Number of arguments for DTR map must match.");
			}
		}
	}

	public TypeDecoder createTypeDecoder() throws EXIException {
		TypeDecoder typeDecoder;

		// create new type-decoder
		if (isSchemaInformed()) {
			// type decoders
			checkDtrMap();

			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_LEXICAL_VALUE)) {
				typeDecoder = new LexicalTypeDecoder(dtrMapTypes,
						dtrMapRepresentations);
			} else {
				typeDecoder = new TypedTypeDecoder(dtrMapTypes,
						dtrMapRepresentations);
			}
		} else {
			// strings only
			typeDecoder = new StringTypeDecoder();
		}

		return typeDecoder;
	}

	@Override
	public EXIFactory clone() {
		try {
			// shallow copy
			EXIFactory copy = (EXIFactory) super.clone();
			// return...
			return copy;

		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof EXIFactory) {
			EXIFactory other = (EXIFactory) o;
			// fidelity options
			if (!fidelityOptions.equals(other.getFidelityOptions())) {
				return false;
			}
			// fragment
			if (isFragment != other.isFragment()) {
				return false;
			}
			// datatype representation map
			if (!(Arrays.equals(this.dtrMapTypes,
					other.getDatatypeRepresentationMapTypes()) && Arrays
					.equals(this.dtrMapRepresentations,
							other.getDatatypeRepresentationMapRepresentations()))) {
				return false;
			}
			// coding mode
			if (getCodingMode() != other.getCodingMode()) {
				return false;
			}
			// block size
			if (getBlockSize() != other.getBlockSize()) {
				return false;
			}
			// value max length
			if (getValueMaxLength() != other.getValueMaxLength()) {
				return false;
			}
			// value partition capacity
			if (getValuePartitionCapacity() != other
					.getValuePartitionCapacity()) {
				return false;
			}

			// everything fine so far
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return fidelityOptions.hashCode() ^ (isFragment ? 1 : 0)
				^ codingMode.hashCode() ^ blockSize ^ valueMaxLength
				^ valuePartitionCapacity;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		// grammar
		if (grammar.isSchemaInformed()) {
			SchemaInformedGrammars sig = (SchemaInformedGrammars) grammar;
			sb.append("[Schema-Informed=" + sig.getSchemaId() + "]");
		} else {
			sb.append("[Schema-Less]");
		}
		// coding-mode
		sb.append("[" + codingMode + "]");
		// fidelity options
		sb.append(fidelityOptions.toString());
		// fragment
		if (isFragment()) {
			sb.append("[Fragment]");
		}
		// dtr
		if (this.dtrMapTypes != null && this.dtrMapTypes.length > 0) {
			sb.append("[DTR Types=");
			for (QName dtrMapType : dtrMapTypes) {
				sb.append(dtrMapType + " ");
			}
			sb.append(", Representation=");
			for (QName dtrMapRepresentation : dtrMapRepresentations) {
				sb.append(dtrMapRepresentation + " ");
			}
			sb.append("]");
		}
		// sc elements
		if (this.scElements != null && this.scElements.length > 0) {
			sb.append("[SCElements=");
			for (QName scElement : scElements) {
				sb.append(scElement + " ");
			}
			sb.append("]");
		}
		// blockSize, valueMaxLength, valuePartitionCapacity
		if (this.blockSize != Constants.DEFAULT_BLOCK_SIZE) {
			sb.append("[blockSize=" + blockSize + "]");
		}
		if (this.valueMaxLength != Constants.DEFAULT_VALUE_MAX_LENGTH) {
			sb.append("[valueMaxLength=" + valueMaxLength + "]");
		}
		if (this.valuePartitionCapacity != Constants.DEFAULT_VALUE_PARTITON_CAPACITY) {
			sb.append("[valuePartitionCapacity=" + valuePartitionCapacity + "]");
		}
		// localValuePartitions
		if (!isLocalValuePartitions()) {
			sb.append("[localValuePartitions=" + isLocalValuePartitions() + "]");
		}
		// maximumNumberOfBuiltInProductions
		if (this.getMaximumNumberOfBuiltInProductions() >= 0) {
			sb.append("[maximumNumberOfBuiltInProductions="
					+ getMaximumNumberOfBuiltInProductions() + "]");
		}
		// maximumNumberOfEvolvingBuiltInElementGrammars
		if (this.getMaximumNumberOfBuiltInElementGrammars() >= 0) {
			sb.append("[maximumNumberOfEvolvingBuiltInElementGrammars="
					+ this.getMaximumNumberOfBuiltInElementGrammars() + "]");
		}

		return sb.toString();
	}

}
