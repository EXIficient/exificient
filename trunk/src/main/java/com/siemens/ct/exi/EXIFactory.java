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

package com.siemens.ct.exi;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.xml.sax.XMLReader;

import com.siemens.ct.exi.api.sax.SAXEncoder;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.types.TypeDecoder;
import com.siemens.ct.exi.types.TypeEncoder;

/**
 * An EXI Factory is used for setting EXI coding options on one hand and
 * retrieving the according reader and writer classes on the other hand.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public interface EXIFactory extends Cloneable {

	/**
	 * Sets the fidelity options used by the EXI factory (e.g. preserving XML
	 * comments or DTDs).
	 * 
	 * @param fidelityOptions
	 *            new fidelity options
	 *            
	 * @see FidelityOptions
	 */
	public void setFidelityOptions(FidelityOptions fidelityOptions);

	/**
	 * Returns the fidelity options used by the EXI factory (e.g. preserving XML
	 * comments or DTDs).
	 * 
	 * @return fidelity options currently used by the factory
	 * @see FidelityOptions
	 */
	public FidelityOptions getFidelityOptions();

	// /**
	// * Sets an EXI profile that configures the factory.
	// *
	// * @param profileName
	// * @throws UnsupportedOption
	// * if profile is not supported
	// */
	// public void setProfile(String profileName) throws UnsupportedOption;
	//
	// /**
	// * Returns whether a certain profile name is in use.
	// *
	// * @param profileName
	// * @return boolean value indicating the use of the profile
	// */
	// public boolean usesProfile(String profileName);

	/**
	 * Sets the header options used by the EXI Encoder(e.g., include EXI Cookie,
	 * EXI Options document).
	 * 
	 * @param headerOptions
	 *            header options
	 *            
	 * @see EncodingOptions
	 */
	public void setEncodingOptions(EncodingOptions headerOptions);

	/**
	 * Returns the header options used by the EXI encoder.
	 * 
	 * @return header options currently used by the factory
	 * @see EncodingOptions
	 */
	public EncodingOptions getEncodingOptions();
	
	/**
	 * Sets the options used by the EXI Decoder(e.g., ignore schemaId).
	 * 
	 * @param options decoding options
	 * @see DecodingOptions decoding options
	 */
	public void setDecodingOptions(DecodingOptions options);

	/**
	 * Returns the options used by the EXI decoder.
	 * 
	 * @return options currently used by the factory
	 * @see DecodingOptions
	 */
	public DecodingOptions getDecodingOptions();

	/**
	 * Sets specific schemaId resolver.
	 * 
	 * @param schemaIdResolver schemaId resolver
	 * @see SchemaIdResolver
	 */
	public void setSchemaIdResolver(SchemaIdResolver schemaIdResolver);

	/**
	 * Returns schemaId resolver for this factory;
	 * 
	 * @return Schema Id Resolver
	 */
	public SchemaIdResolver getSchemaIdResolver();

	/**
	 * Informs the factory that we are dealing with an XML fragment instead of
	 * an XML document
	 * 
	 * @param isFragment true if is fragment
	 * 
	 */
	public void setFragment(boolean isFragment);

	/**
	 * Returns whether we deal with a fragment
	 * 
	 * @return is fragment
	 */
	public boolean isFragment();

	/**
	 * Sets the EXI <code>Grammars</code> used for coding.
	 * 
	 * @param grammar grammar
	 * 
	 */
	public void setGrammars(Grammars grammar);

	/**
	 * Returns the currently used EXI <code>Grammars</code>. By default a
	 * <code>SchemaLessGrammars</code> is used.
	 * 
	 * @return grammar used by the factory
	 */
	public Grammars getGrammars();

	/**
	 * Re-sets the coding mode used by the factory.
	 * 
	 * @param codingMode coding mode
	 */
	public void setCodingMode(CodingMode codingMode);

	/**
	 * Returns the currently used <code>CodingMode</code>. By default BIT_PACKED
	 * is used.
	 * 
	 * @return coding-mode used by the factory
	 */
	public CodingMode getCodingMode();

	/**
	 * The default blockSize is intentionally large (1,000,000) but can be
	 * reduced for processing large documents on devices with limited memory.
	 * 
	 * @param blockSize blockSize
	 */
	public void setBlockSize(int blockSize);

	/**
	 * The blockSize option specifies the block size used for EXI compression.
	 * When the "blockSize" element is absent in the EXI Options document, the
	 * default blocksize of 1,000,000 is used.
	 * 
	 * @return blockSize
	 */
	public int getBlockSize();

	/**
	 * The valueMaxLength option specifies the maximum length of value content
	 * items to be considered for addition to the string table. The default
	 * value "unbounded" is assumed when the "valueMaxLength" element is absent
	 * in the EXI Options document.
	 * <p>
	 * See http://www.w3.org/TR/exi/#key-valueMaxLengthOption
	 * </p>
	 * 
	 * @param valueMaxLength
	 *            the maximum string length of value content items to be
	 *            considered for addition to the string table
	 */
	public void setValueMaxLength(int valueMaxLength);

	/**
	 * The default value "unbounded" is assumed when the "valueMaxLength"
	 * element is absent.
	 * 
	 * @return value OR negative for unbounded
	 */
	public int getValueMaxLength();

	/**
	 * The valuePartitionCapacity option specifies the maximum number of value
	 * content items in the string table at any given time. The default value
	 * "unbounded" is assumed when the "valuePartitionCapacity" element is
	 * absent.
	 * 
	 * <p>
	 * See http://www.w3.org/TR/exi/#key-valuePartitionCapacityOption
	 * </p>
	 * 
	 * @param valuePartitionCapacity
	 *            the total capacity of value partitions in a string table
	 */
	public void setValuePartitionCapacity(int valuePartitionCapacity);

	/**
	 * The default value "unbounded" is assumed when the
	 * "valuePartitionCapacity" element is absent
	 * 
	 * @return value OR negative for unbounded
	 */
	public int getValuePartitionCapacity();

	/**
	 * By default, each typed value in an EXI stream is represented by the
	 * associated built-in EXI datatype representation. However, EXI processors
	 * MAY provide the capability to specify different built-in EXI datatype
	 * representations or user-defined datatype representations for representing
	 * specific schema datatypes. This capability is called Datatype
	 * Representation Map.
	 * 
	 * @param dtrMapTypes dtrMap types
	 * @param dtrMapRepresentations dtrMap representations
	 */
	public void setDatatypeRepresentationMap(QName[] dtrMapTypes,
			QName[] dtrMapRepresentations);

	/**
	 * EXI processors MAY provide the capability to specify different built-in
	 * EXI datatype representations or user-defined datatype representations for
	 * representing specific schema datatypes.
	 * 
	 * @return qualified name array for dtr types OR <code>null</code>
	 */
	public QName[] getDatatypeRepresentationMapTypes();

	/**
	 * EXI processors MAY provide the capability to specify different built-in
	 * EXI datatype representations or user-defined datatype representations for
	 * representing specific schema datatypes.
	 * 
	 * @return qualified name array for dtr representations OR <code>null</code>
	 */
	public QName[] getDatatypeRepresentationMapRepresentations();

	/**
	 * Self-contained elements may be read independently from the rest of the
	 * EXI body, allowing them to be indexed for random access. The
	 * "selfContained" element MUST NOT appear in an EXI options document when
	 * one of "compression", "pre-compression" or "strict" elements are present
	 * in the same options document.
	 * 
	 * @param scElements selfContained elements
	 */
	public void setSelfContainedElements(QName[] scElements);
	
	/**
	 * Self-contained elements may be read independently from the rest of the
	 * EXI body, allowing them to be indexed for random access. The
	 * "selfContained" element MUST NOT appear in an EXI options document when
	 * one of "compression", "pre-compression" or "strict" elements are present
	 * in the same options document.
	 * 
	 * @param scElements selfContained elements
	 * @param scHandler handler for SC elements
	 */
	public void setSelfContainedElements(QName[] scElements, SelfContainedHandler scHandler);

	/**
	 * Returns boolean value telling whether a certain element is encoded as
	 * selfContained fragment.
	 * 
	 * @param element qualified element name
	 * @return true if a certain element is selfContained
	 */
	public boolean isSelfContainedElement(QName element);
	
	
	/**
	 * Returns selfContained element handler.
	 * 
	 * @return selfContained element handler or null
	 */
	public SelfContainedHandler getSelfContainedHandler();

	/**
	 * The EXI profile defines a parameter that can disable the use of local
	 * value references. Global value indexing may be controlled using the
	 * options defined in the EXI 1.0 specification
	 * 
	 * <p>
	 * The localValuePartitions option of the EXI profile is a Boolean used to
	 * indicate whether local value partitions are used. ] The value "0"
	 * indicates that no local value partition is used while "1" represents the
	 * behavior of the EXI 1.0 specification
	 * </p>
	 * 
	 * @param useLocalValuePartitions whether to use localValue partitions
	 */
	public void setLocalValuePartitions(boolean useLocalValuePartitions);

	/**
	 * The localValuePartitions option of the EXI profile is a Boolean used to
	 * indicate whether local value partitions are used. ] The value "0"
	 * indicates that no local value partition is used while "1" represents the
	 * behavior of the EXI 1.0 specification
	 * 
	 * @return whether local value partitions are used
	 */
	public boolean isLocalValuePartitions();

	/**
	 * The EXI profile defines a parameter that restricts the maximum number of
	 * elements for which evolving built-in element grammars can be
	 * instantiated.
	 * 
	 * <p>
	 * The value "unbounded" (-1) indicates that no restrictions are used and
	 * represents the behavior of the EXI 1.0 specification
	 * </p>
	 * 
	 * @param maximumNumberOfBuiltInElementGrammars maximum number of Built-In element grammars
	 */
	public void setMaximumNumberOfBuiltInElementGrammars(
			int maximumNumberOfBuiltInElementGrammars);

	/**
	 * The EXI profile defines a parameter that restricts the maximum number of
	 * elements for which evolving built-in element grammars can be
	 * instantiated.
	 * 
	 * @return maximum number of evolving built-in element grammars
	 */
	public int getMaximumNumberOfBuiltInElementGrammars();

	/**
	 * The EXI profile defines a parameter that restricts the maximum number of
	 * top-level productions that can be dynamically inserted in built-in
	 * element grammars.
	 * 
	 * <p>
	 * The value "unbounded" (-1) indicates that no restrictions are used and
	 * represents the behavior of the EXI 1.0 specification
	 * </p>
	 * 
	 * @param maximumNumberOfBuiltInProductions maximum number of Built-In productions
	 */
	public void setMaximumNumberOfBuiltInProductions(
			int maximumNumberOfBuiltInProductions);

	/**
	 * The EXI profile defines a parameter that restricts the maximum number of
	 * top-level productions that can be dynamically inserted in built-in
	 * element grammars.
	 * 
	 * @return maximum number of built-in productions
	 */
	public int getMaximumNumberOfBuiltInProductions();

	/**
	 * The EXI profile defines parameters that restrict grammar learning. This
	 * is a convenience method to indicate whether grammar restriction is in
	 * use.
	 * 
	 * @return whether schema learning is disabled
	 */
	public boolean isGrammarLearningDisabled();

	/**
	 * Allows to use another body encoder implementation. The provided class
	 * needs to implement the EXIBodyEncoder interface.
	 * 
	 * @see EXIBodyEncoder
	 * @param className class name
	 * @throws EXIException EXI exception
	 */
	public void setEXIBodyEncoder(String className) throws EXIException;

	/**
	 * Allows to use another body encoder implementation.
	 * 
	 * @see EXIBodyEncoder
	 * @param bodyEncoder body encoder
	 * @throws EXIException EXI exception
	 */
	public void setEXIBodyEncoder(EXIBodyEncoder bodyEncoder)
			throws EXIException;

	/**
	 * Allows to use another body decoder implementation. The provided class
	 * needs to implement the EXIBodyDecoder interface.
	 * 
	 * @see EXIBodyDecoder
	 * @param className class name
	 * @throws EXIException EXI exception
	 */
	public void setEXIBodyDecoder(String className) throws EXIException;

	/**
	 * Allows to use another body decoder implementation.
	 * 
	 * @see EXIBodyDecoder
	 * @param bodyDecoder body decoder
	 * @throws EXIException EXI exception
	 */
	public void setEXIBodyDecoder(EXIBodyDecoder bodyDecoder)
			throws EXIException;

	/**
	 * Returns an <code>EXIBodyEncoder</code>
	 * 
	 * @return encoder using the previously set coding options.
	 * @throws EXIException EXI exception
	 * 
	 */
	public EXIBodyEncoder createEXIBodyEncoder() throws EXIException;

	/**
	 * Returns a <code>SAXEncoder</code> that implements
	 * <code>DefaultHandler2</code>
	 * 
	 * <p>
	 * Note that the output stream MUST be set.
	 * </p>
	 * 
	 * @return writer using the previously set coding options.
	 * @throws EXIException EXI exception
	 * 
	 */
	public SAXEncoder createEXIWriter() throws EXIException;

	/**
	 * Returns an <code>EXIBodyDecoder</code>
	 * 
	 * @return decoder using the previously set coding options.
	 * @throws EXIException EXI exception
	 * 
	 */
	public EXIBodyDecoder createEXIBodyDecoder() throws EXIException;

	/**
	 * Returns an <code>EXIReader</code>
	 * 
	 * @return reader using the previously set coding options.
	 * @throws EXIException EXI exception
	 * 
	 */
	public XMLReader createEXIReader() throws EXIException;

	/**
	 * Returns an EXI <code>StringEncoder</code> according coding options
	 * 
	 * @return String Encoder
	 */
	public StringEncoder createStringEncoder();

	/**
	 * Returns an EXI <code>TypeEncoder</code> according coding options such as
	 * schema-informed or schema-less grammar and options like
	 * Preserve.LexicalValues
	 * 
	 * @return type encoder according given EXI options
	 * @throws EXIException EXI exception
	 * @see TypeEncoder
	 */
	public TypeEncoder createTypeEncoder() throws EXIException;

	/**
	 * Returns an EXI {@link StringDecoder} according coding options
	 * 
	 * @return String Decoder
	 */
	public StringDecoder createStringDecoder();

	/**
	 * Returns an EXI <code>TypeDecoder</code> according coding options such as
	 * schema-informed or schema-less grammar and options like
	 * Preserve.LexicalValues
	 * 
	 * @return type decoder according given EXI options
	 * @throws EXIException EXI exception
	 * @see TypeDecoder
	 */
	public TypeDecoder createTypeDecoder() throws EXIException;

	/**
	 * Returns a shallow copy of this EXI factory.
	 * 
	 * @return EXIFactory
	 */
	public EXIFactory clone();
}
