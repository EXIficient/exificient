/*
 * Copyright (C) 2007, 2008 Siemens AG
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xml.sax.XMLReader;

import com.siemens.ct.exi.api.sax.EXIWriter;
import com.siemens.ct.exi.datatype.DatatypeRepresentation;
import com.siemens.ct.exi.datatype.decoder.TypeDecoder;
import com.siemens.ct.exi.datatype.encoder.TypeEncoder;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.io.block.DecoderBlock;
import com.siemens.ct.exi.io.block.EncoderBlock;

/**
 * An EXI Factory is used for setting EXI coding options on one hand and
 * retrieving the according reader and writer classes on the other hand.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081112
 */

public interface EXIFactory {
	/**
	 * Sets the fidelity options used by the EXI factory (e.g. preserving XML
	 * comments or DTDs).
	 * 
	 * @param fidelityOptions
	 *            new fidelity options
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
	 * By default, each typed value in an EXI stream is represented by the
	 * associated built-in EXI datatype representation. However, EXI processors
	 * MAY provide the capability to specify different built-in EXI datatype
	 * representations or user-defined datatype representations for representing
	 * specific schema datatypes. This capability is called Datatype
	 * Representation Map.
	 */
	public void setDatatypeRepresentationMap(
			DatatypeRepresentation[] datatypeRepresentations );

	/**
	 * Returns an <code>EXIEncoder</code>
	 * 
	 * @return encoder using the previously set coding options.
	 * 
	 */
	public EXIEncoder createEXIEncoder();

	/**
	 * Returns an <code>EXIWriter</code>
	 * 
	 * @return writer using the previously set coding options.
	 * 
	 */
	public EXIWriter createEXIWriter();

	/**
	 * Returns an <code>EXIDecoder</code>
	 * 
	 * @return decoder using the previously set coding options.
	 * 
	 */
	public EXIDecoder createEXIDecoder();

	/**
	 * Returns an <code>EXIReader</code>
	 * 
	 * @return reader using the previously set coding options.
	 * 
	 */
	public XMLReader createEXIReader();

	// TODO offer functionality to set CodingBlock
	// e.g. setCodingBlockFactory
	// # CodingBlockFactory
	// . getEncoderBlock
	// . getDecoderBlock

	/**
	 * Returns an EXI <code>EncoderBlock</code> according coding options like
	 * CodingMode etc.
	 * 
	 * @return encoder block according given EXI options
	 * @see EncoderBlock
	 */
	public EncoderBlock createEncoderBlock(OutputStream outputStream);

	/**
	 * Returns an EXI <code>DecoderBlock</code> according coding options like
	 * CodingMode etc.
	 * 
	 * @return decoder block according given EXI options
	 * @see DecoderBlock
	 */
	public DecoderBlock createDecoderBlock(InputStream inputStream)
			throws IOException;

	/**
	 * Returns an EXI <code>TypeEncoder</code> according coding options such as
	 * schema-informed or schema-less grammar and options like
	 * Preserve.LexicalValues
	 * 
	 * @return type encoder according given EXI options
	 * @see TypeEncoder
	 */
	public TypeEncoder createTypeEncoder();

	/**
	 * Returns an EXI <code>TypeDecoder</code> according coding options such as
	 * schema-informed or schema-less grammar and options like
	 * Preserve.LexicalValues
	 * 
	 * @return type decoder according given EXI options
	 * @see TypeDecoder
	 */
	public TypeDecoder createTypeDecoder();
}
