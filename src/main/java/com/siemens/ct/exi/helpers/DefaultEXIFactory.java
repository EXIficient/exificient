/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.helpers;

import java.util.Arrays;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.DecodingOptions;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamDecoder;
import com.siemens.ct.exi.EXIStreamEncoder;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.SchemaIdResolver;
import com.siemens.ct.exi.SelfContainedHandler;
import com.siemens.ct.exi.core.EXIBodyDecoderInOrder;
import com.siemens.ct.exi.core.EXIBodyDecoderInOrderSC;
import com.siemens.ct.exi.core.EXIBodyDecoderReordered;
import com.siemens.ct.exi.core.EXIBodyEncoderInOrder;
import com.siemens.ct.exi.core.EXIBodyEncoderInOrderSC;
import com.siemens.ct.exi.core.EXIBodyEncoderReordered;
import com.siemens.ct.exi.core.EXIStreamDecoderImpl;
import com.siemens.ct.exi.core.EXIStreamEncoderImpl;
import com.siemens.ct.exi.datatype.strings.BoundedStringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.BoundedStringEncoderImpl;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringDecoderImpl;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.datatype.strings.StringEncoderImpl;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
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
import com.siemens.ct.exi.util.sort.QNameSort;

/**
 * 
 * This is the default implementation of an <code>EXIFactory</code> class.
 * 
 * @see EXIFactory
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
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
	protected SelfContainedHandler scHandler;

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

	/* default: use no specify body coder */
	protected EXIBodyEncoder bodyEncoder;
	protected EXIBodyDecoder bodyDecoder;
	
	protected static final QNameSort qnameSort = new QNameSort();

	protected DefaultEXIFactory() {
	}

	protected static void setDefaultValues(EXIFactory factory) {
		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setEncodingOptions(EncodingOptions.createDefault());
		factory.setDecodingOptions(DecodingOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setFragment(false);
		factory.setGrammars(new SchemaLessGrammars());

		// factory.setSchemaIdResolver(new DefaultSchemaIdResolver());
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
		setSelfContainedElements(scElements, null);
	}

	public void setSelfContainedElements(QName[] scElements,
			SelfContainedHandler scHandler) {
		this.scElements = scElements;
		this.scHandler = scHandler;
	}

	public boolean isSelfContainedElement(QName element) {
		assert (element != null);
		String elementNS = element.getNamespaceURI();
		String elementLP = element.getLocalPart();
		if (scElements != null && scElements.length > 0) {
			for (int i = 0; i < scElements.length; i++) {
				QName qname = scElements[i];
				assert (qname != null);
				if (elementNS.matches(qname.getNamespaceURI())
						&& elementLP.matches(qname.getLocalPart())) {
					return true;
				}
				// if (qname.equals(element)) {
				// return true;
				// }
			}
		}
		return false;
	}

	public SelfContainedHandler getSelfContainedHandler() {
		return this.scHandler;
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
		
		
		// canonical EXI (http://www.w3.org/TR/exi-c14n/)
		if (this.getEncodingOptions().isOptionEnabled(
				EncodingOptions.CANONICAL_EXI)) {
			updateFactoryAccordingCanonicalEXI(EncodingOptions.CANONICAL_EXI);
		} else if (this.getEncodingOptions().isOptionEnabled(
				EncodingOptions.CANONICAL_EXI_WITHOUT_EXI_OPTIONS)) {
			updateFactoryAccordingCanonicalEXI(EncodingOptions.CANONICAL_EXI_WITHOUT_EXI_OPTIONS);
		}
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

		if (codingMode.usesRechanneling()) {
			return new EXIBodyEncoderReordered(this);
		} else {
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)) {
				return new EXIBodyEncoderInOrderSC(this);
			} else {
				return new EXIBodyEncoderInOrder(this);
			}
		}
	}

	public EXIStreamEncoder createEXIStreamEncoder() throws EXIException {
		doSanityCheck();

		return new EXIStreamEncoderImpl(this);
	}

	protected void updateFactoryAccordingCanonicalEXI(String canonicalOption)
			throws UnsupportedOption {
		// update canonical options according to canonical EXI rules
		assert (this.getEncodingOptions().isOptionEnabled(canonicalOption));
		// * A Canonical EXI Header MUST NOT begin with the optional EXI Cookie
		this.getEncodingOptions().unsetOption(EncodingOptions.INCLUDE_COOKIE);
		// * Presence Bit for EXI Options to indicate whether the fifth part
		// of the EXI Header, the EXI Options document, is present or absent.
		if (this.getEncodingOptions().isOptionEnabled(
				EncodingOptions.CANONICAL_EXI)) {
			this.getEncodingOptions()
					.setOption(EncodingOptions.INCLUDE_OPTIONS);
		} else {
			this.getEncodingOptions().unsetOption(
					EncodingOptions.INCLUDE_OPTIONS);
		}
		// * When the alignment option compression is set, pre-compress MUST be
		// used instead of compression.
		if (this.getCodingMode() == CodingMode.COMPRESSION) {
			this.setCodingMode(CodingMode.PRE_COMPRESSION);
		}
		// * The element schemaId MUST always be present to indicate which
		// schema information is used.
		this.getEncodingOptions().setOption(EncodingOptions.INCLUDE_SCHEMA_ID);
		// * datatypeRepresentationMap: the tuples are to be sorted
		// lexicographically according to the schema datatype first by {name}
		// then by {namespace}
		if (this.dtrMapTypes != null && this.dtrMapTypes.length > 0) {
			bubbleSort(this.dtrMapTypes, this.dtrMapRepresentations);
		}
	}

	protected void bubbleSort(QName[] dtrMapTypes, QName[] dtrMapRepresentations) {
		boolean swapped = true;
		int j = 0;
		QName tmpType;
		QName tmpRep;
		while (swapped) {
			swapped = false;
			j++;
			for (int i = 0; i < dtrMapTypes.length - j; i++) {
				// if (array[i] > array[i + 1]) {
				if (qnameSort.compare(dtrMapTypes[i],
						dtrMapTypes[i + 1]) > 0) {
					tmpType = dtrMapTypes[i];
					dtrMapTypes[i] = dtrMapTypes[i + 1];
					dtrMapTypes[i + 1] = tmpType;
					tmpRep = dtrMapRepresentations[i];
					dtrMapRepresentations[i] = dtrMapRepresentations[i + 1];
					dtrMapRepresentations[i + 1] = tmpRep;
					swapped = true;
				}
			}
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

	public EXIStreamDecoder createEXIStreamDecoder() throws EXIException {
		doSanityCheck();

		return new EXIStreamDecoderImpl(this);
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
			for (int i = 0; i < dtrMapTypes.length; i++) {
				QName dtrMapType = dtrMapTypes[i];
				sb.append(dtrMapType + " ");
			}
			sb.append(", Representation=");
			for (int i = 0; i < dtrMapRepresentations.length; i++) {
				QName dtrMapRepresentation = dtrMapRepresentations[i];
				sb.append(dtrMapRepresentation + " ");
			}
			sb.append("]");
		}
		// sc elements
		if (this.scElements != null && this.scElements.length > 0) {
			sb.append("[SCElements=");
			for (int i = 0; i < scElements.length; i++) {
				QName scElement = scElements[i];
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
