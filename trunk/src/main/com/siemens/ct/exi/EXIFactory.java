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

package com.siemens.ct.exi;

import java.io.OutputStream;

import javax.xml.namespace.QName;

import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.types.TypeDecoder;
import com.siemens.ct.exi.types.TypeEncoder;

/**
 * An EXI Factory is used for setting EXI coding options on one hand and
 * retrieving the according reader and writer classes on the other hand.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090224
 */

public interface EXIFactory extends Cloneable {
	/**
	 * Sets the fidelity options used by the EXI factory (e.g. preserving XML
	 * comments or DTDs).
	 * 
	 * @param fidelityOptions
	 *            new fidelity options
	 * @throws EXIException 
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

	/**
	 * Informs the factory that we are dealing with an XML fragment instead of
	 * an XML document
	 */
	public void setFragment(boolean isFragment);

	/**
	 * Returns whether we deal with a fragment
	 * 
	 * @return is fragment
	 */
	public boolean isFragment();

	/**
	 * Sets the EXI <code>Grammar</code> used for coding.
	 */
	public void setGrammar(Grammar grammar);

	/**
	 * Returns the currently used EXI <code>Grammar</code>. By default a
	 * <code>SchemaLessGrammar</code> is used.
	 * 
	 * @return grammar used by the factory
	 */
	public Grammar getGrammar();

	/**
	 * Re-sets the coding mode used by the factory.
	 * 
	 * @param codingMode
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
	 * Sets whether an EXI Body is preceded by an EXI Header. By default any EXI
	 * stream consists of an EXI header followed by an EXI Body. e.g.
	 * SelfContained Fragments are treated differently and no additional header
	 * is added.
	 * 
	 * @param exiBodyOnly
	 */
	public void setEXIBodyOnly(boolean exiBodyOnly);

	public boolean isEXIBodyOnly();

	/**
	 * The default blockSize is intentionally large (1,000,000) but can be
	 * reduced for processing large documents on devices with limited memory.
	 * 
	 * @param blockSize
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
	 * See {@link http://www.w3.org/TR/exi/#key-valueMaxLengthOption}
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
	 * See {@link http://www.w3.org/TR/exi/#key-valuePartitionCapacityOption}
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
	 */
	public void setDatatypeRepresentationMap(QName[] dtrMapTypes,
			QName[] dtrMapRepresentations);

	/**
	 * Self-contained elements may be read independently from the rest of the
	 * EXI body, allowing them to be indexed for random access. The
	 * "selfContained" element MUST NOT appear in an EXI options document when
	 * one of "compression", "pre-compression" or "strict" elements are present
	 * in the same options document.
	 * 
	 * @param scElements
	 */
	public void setSelfContainedElements(QName[] scElements);

	/**
	 * Returns boolean value telling whether a certain element is encoded as
	 * selfContained fragment.
	 * 
	 * @param element
	 */
	public boolean isSelfContainedElement(QName element);

	/**
	 * Returns an <code>EXIEncoder</code>
	 * 
	 * @return encoder using the previously set coding options.
	 * @throws EXIException 
	 * 
	 */
	public EXIEncoder createEXIEncoder() throws EXIException;

	/**
	 * Returns an <code>DefaultHandler2</code>
	 * 
	 * @return writer using the previously set coding options.
	 * @throws EXIException 
	 * 
	 */
	public DefaultHandler2 createEXIWriter(OutputStream os) throws EXIException;

	/**
	 * Returns an <code>EXIDecoder</code>
	 * 
	 * @return decoder using the previously set coding options.
	 * @throws EXIException 
	 * 
	 */
	public EXIDecoder createEXIDecoder() throws EXIException;

	/**
	 * Returns an <code>EXIReader</code>
	 * 
	 * @return reader using the previously set coding options.
	 * @throws EXIException 
	 * 
	 */
	public XMLReader createEXIReader() throws EXIException;

	/**
	 * Returns an EXI <code>TypeEncoder</code> according coding options such as
	 * schema-informed or schema-less grammar and options like
	 * Preserve.LexicalValues
	 * 
	 * @return type encoder according given EXI options
	 * @throws EXIException 
	 * @see TypeEncoder
	 */
	public TypeEncoder createTypeEncoder() throws EXIException;

	/**
	 * Returns an EXI <code>TypeDecoder</code> according coding options such as
	 * schema-informed or schema-less grammar and options like
	 * Preserve.LexicalValues
	 * 
	 * @return type decoder according given EXI options
	 * @throws EXIException 
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
