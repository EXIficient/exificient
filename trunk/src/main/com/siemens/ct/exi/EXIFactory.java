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

package com.siemens.ct.exi;

import javax.xml.namespace.QName;

import org.xml.sax.XMLReader;

import com.siemens.ct.exi.api.sax.EXIWriter;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.types.DatatypeRepresentation;
import com.siemens.ct.exi.types.TypeDecoder;
import com.siemens.ct.exi.types.TypeEncoder;

/**
 * An EXI Factory is used for setting EXI coding options on one hand and
 * retrieving the according reader and writer classes on the other hand.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090224
 */

public interface EXIFactory extends Cloneable {
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
	 * By default, each typed value in an EXI stream is represented by the
	 * associated built-in EXI datatype representation. However, EXI processors
	 * MAY provide the capability to specify different built-in EXI datatype
	 * representations or user-defined datatype representations for representing
	 * specific schema datatypes. This capability is called Datatype
	 * Representation Map.
	 */
	public void setDatatypeRepresentationMap(
			DatatypeRepresentation[] datatypeRepresentations);

	/**
	 * Self-contained elements may be read independently from the rest of the
	 * EXI body, allowing them to be indexed for random access. The
	 * "selfContained" element MUST NOT appear in an EXI options document when
	 * one of "compression", "pre-compression" or "strict" elements are present
	 * in the same options document.
	 * 
	 * @param elements
	 */
	public void setSelfContainedElements(QName[] scElements);
	
	public boolean isSelfContainedElement(QName element);

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

	/**
	 * Returns a shallow copy of this EXI factory.
	 * 
	 * @return EXIFactory
	 */
	public EXIFactory clone();
}
