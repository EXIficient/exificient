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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

/**
 * EXI Header (see http://www.w3.org/TR/exi/#header)
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public abstract class AbstractEXIHeader {

	public static final String HEADER = "header";
	public static final String LESSCOMMON = "lesscommon";
	public static final String UNCOMMON = "uncommon";
	public static final String ALIGNMENT = "alignment";
	public static final String BYTE = "byte";
	public static final String PRE_COMPRESS = "pre-compress";
	public static final String SELF_CONTAINED = "selfContained";
	public static final String VALUE_MAX_LENGTH = "valueMaxLength";
	public static final String VALUE_PARTITION_CAPACITY = "valuePartitionCapacity";
	public static final String DATATYPE_REPRESENTATION_MAP = "datatypeRepresentationMap";
	public static final String PRESERVE = "preserve";
	public static final String DTD = "dtd";
	public static final String PREFIXES = "prefixes";
	public static final String LEXICAL_VALUES = "lexicalValues";
	public static final String COMMENTS = "comments";
	public static final String PIS = "pis";
	public static final String BLOCK_SIZE = "blockSize";
	public static final String COMMON = "common";
	public static final String COMPRESSION = "compression";
	public static final String FRAGMENT = "fragment";
	public static final String SCHEMA_ID = "schemaId";
	public static final String STRICT = "strict";

	public static final int NUMBER_OF_DISTINGUISHING_BITS = 2;
	public static final int DISTINGUISHING_BITS_VALUE = 2;

	public static final int NUMBER_OF_FORMAT_VERSION_BITS = 4;
	public static final int FORMAT_VERSION_CONTINUE_VALUE = 15;

	protected EXIFactory headerFactory;
	private GrammarFactory grammarFactory;

	protected EXIFactory getHeaderFactory() throws EXIException {
		if (headerFactory == null) {
			InputStream is = new ByteArrayInputStream(
					EXI_OPTIONS_XSD.getBytes());
			Grammar headerGrammar = getGrammarFactory().createGrammar(is);

			headerFactory = DefaultEXIFactory.newInstance();
			headerFactory.setGrammar(headerGrammar);
			headerFactory.setFidelityOptions(FidelityOptions.createStrict());
		}

		return headerFactory;
	}
	
	protected GrammarFactory getGrammarFactory() {
		if (grammarFactory == null) {
			grammarFactory = GrammarFactory.newInstance();
		}
		return this.grammarFactory;
	}

	static final String EXI_OPTIONS_XSD = "<xsd:schema targetNamespace='http://www.w3.org/2009/exi' xmlns:xsd='http://www.w3.org/2001/XMLSchema' elementFormDefault='qualified'><xsd:element name='header'><xsd:complexType><xsd:sequence><xsd:element name='lesscommon' minOccurs='0'><xsd:complexType><xsd:sequence><xsd:element name='uncommon' minOccurs='0'><xsd:complexType><xsd:sequence><xsd:any namespace='##other' minOccurs='0' maxOccurs='unbounded' processContents='skip' /><xsd:element name='alignment' minOccurs='0'><xsd:complexType><xsd:choice><xsd:element name='byte'><xsd:complexType /></xsd:element><xsd:element name='pre-compress'><xsd:complexType /></xsd:element></xsd:choice></xsd:complexType></xsd:element><xsd:element name='selfContained' minOccurs='0'><xsd:complexType /></xsd:element><xsd:element name='valueMaxLength' minOccurs='0'><xsd:simpleType><xsd:restriction base='xsd:unsignedInt' /></xsd:simpleType></xsd:element><xsd:element name='valuePartitionCapacity' minOccurs='0'><xsd:simpleType><xsd:restriction base='xsd:unsignedInt' /></xsd:simpleType></xsd:element><xsd:element name='datatypeRepresentationMap' minOccurs='0' maxOccurs='unbounded'><xsd:complexType><xsd:sequence><xsd:any namespace='##other' processContents='skip' /><xsd:any processContents='skip' /></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType></xsd:element><xsd:element name='preserve' minOccurs='0'><xsd:complexType><xsd:sequence><xsd:element name='dtd' minOccurs='0'><xsd:complexType /></xsd:element><xsd:element name='prefixes' minOccurs='0'><xsd:complexType /></xsd:element><xsd:element name='lexicalValues' minOccurs='0'><xsd:complexType /></xsd:element><xsd:element name='comments' minOccurs='0'><xsd:complexType /></xsd:element><xsd:element name='pis' minOccurs='0'><xsd:complexType /></xsd:element></xsd:sequence></xsd:complexType></xsd:element><xsd:element name='blockSize' minOccurs='0'><xsd:simpleType><xsd:restriction base='xsd:unsignedInt' /></xsd:simpleType></xsd:element></xsd:sequence></xsd:complexType></xsd:element><xsd:element name='common' minOccurs='0'><xsd:complexType><xsd:sequence><xsd:element name='compression' minOccurs='0'><xsd:complexType /></xsd:element><xsd:element name='fragment' minOccurs='0'><xsd:complexType /></xsd:element><xsd:element name='schemaId' minOccurs='0' nillable='true'><xsd:simpleType><xsd:restriction base='xsd:string' /></xsd:simpleType></xsd:element></xsd:sequence></xsd:complexType></xsd:element><xsd:element name='strict' minOccurs='0'><xsd:complexType /></xsd:element></xsd:sequence></xsd:complexType></xsd:element><xsd:simpleType name='base64Binary'><xsd:restriction base='xsd:base64Binary'/></xsd:simpleType><xsd:simpleType name='hexBinary' ><xsd:restriction base='xsd:hexBinary'/></xsd:simpleType><xsd:simpleType name='boolean' ><xsd:restriction base='xsd:boolean'/></xsd:simpleType><xsd:simpleType name='decimal' ><xsd:restriction base='xsd:decimal'/></xsd:simpleType><xsd:simpleType name='double' ><xsd:restriction base='xsd:double'/></xsd:simpleType><xsd:simpleType name='integer' ><xsd:restriction base='xsd:integer'/></xsd:simpleType><xsd:simpleType name='string' ><xsd:restriction base='xsd:string'/></xsd:simpleType><xsd:simpleType name='dateTime' ><xsd:restriction base='xsd:dateTime'/></xsd:simpleType><xsd:simpleType name='date' ><xsd:restriction base='xsd:date'/></xsd:simpleType><xsd:simpleType name='time' ><xsd:restriction base='xsd:time'/></xsd:simpleType><xsd:simpleType name='gYearMonth' ><xsd:restriction base='xsd:gYearMonth'/></xsd:simpleType><xsd:simpleType name='gMonthDay' ><xsd:restriction base='xsd:gMonthDay'/></xsd:simpleType><xsd:simpleType name='gYear' ><xsd:restriction base='xsd:gYear'/></xsd:simpleType><xsd:simpleType name='gMonth' ><xsd:restriction base='xsd:gMonth'/></xsd:simpleType><xsd:simpleType name='gDay' ><xsd:restriction base='xsd:gDay'/></xsd:simpleType><xsd:simpleType name='ieeeBinary32' ><xsd:restriction base='xsd:float'/></xsd:simpleType><xsd:simpleType name='ieeeBinary64' ><xsd:restriction base='xsd:double'/></xsd:simpleType></xsd:schema>";

}
