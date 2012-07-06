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

package com.siemens.ct.exi.data;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;

public class TestCaseOption {
	private CodingMode codingMode;
	private FidelityOptions fidelityOptions;
	private boolean fragments;
	private String schemaLocation;
	private boolean xmlEqual;
	private boolean schemaInformedOnly;
	private QName[] scElements;
	private int blockSize = Constants.DEFAULT_BLOCK_SIZE;
	private int valueMaxLength = Constants.DEFAULT_VALUE_MAX_LENGTH;
	private int valuePartitionCapacity = Constants.DEFAULT_VALUE_PARTITON_CAPACITY;
	private QName[] dtrMapTypes;
	private QName[] dtrMapRepresentations;
	private EncodingOptions encodingOptions;
//	private boolean includeCookie;
//	private boolean includeOptions;
//	private boolean includeSchemaId;
	
	public TestCaseOption() {
		encodingOptions = EncodingOptions.createDefault();
	}
	
	public CodingMode getCodingMode() {
		return codingMode;
	}

	public void setCodingMode(CodingMode codingMode) {
		this.codingMode = codingMode;
	}

	public FidelityOptions getFidelityOptions() {
		return fidelityOptions;
	}

	public void setFidelityOptions(FidelityOptions fidelityOptions) {
		this.fidelityOptions = fidelityOptions;
	}

	public boolean isFragments() {
		return fragments;
	}

	public void setFragments(boolean fragments) {
		this.fragments = fragments;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	public boolean isXmlEqual() {
		return xmlEqual;
	}

	public void setXmlEqual(boolean xmlEqual) {
		this.xmlEqual = xmlEqual;
	}

	public boolean isSchemaInformedOnly() {
		return schemaInformedOnly;
	}

	public void setSchemaInformedOnly(boolean schemaInformedOnly) {
		this.schemaInformedOnly = schemaInformedOnly;
	}

	public QName[] getDtrMapTypes() {
		return dtrMapTypes;
	}

	public QName[] getDtrMapRepresentations() {
		return dtrMapRepresentations;
	}
	
	public void setDatatypeRepresentationMap(QName[] dtrMapTypes,
			QName[] dtrMapRepresentations) {
		this.dtrMapTypes = dtrMapTypes;
		this.dtrMapRepresentations = dtrMapRepresentations;
	}
	
	public void setSelfContainedElements(QName[] scElements) {
		this.scElements = scElements;
	}
	
	public QName[] getSelfContainedElements() {
		return scElements;
	}

	public void setBlockSize(int blockSize) {
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

	public void setEncodingOptions(EncodingOptions encodingOptions) {
		this.encodingOptions = encodingOptions;
	}
	
	public EncodingOptions getEncodingOptions() {
		return encodingOptions;
	}
	
//	public void setIncludeCookie(boolean includeCookie) {
//		this.includeCookie = includeCookie;
//	}
//	
//	public boolean isIncludeCookie() {
//		return includeCookie;
//	}
//	
//	public void setIncludeOptions(boolean includeOptions) {
//		this.includeOptions = includeOptions;
//	}
//	
//	public boolean isIncludeOptions() {
//		return includeOptions;
//	}
//	
//	public void setIncludeSchemaId(boolean includeSchemaId) {
//		this.includeSchemaId = includeSchemaId;
//	}
//	
//	public boolean isIncludeSchemaId() {
//		return includeSchemaId;
//	}

	@Override
	public String toString() {
		String s = new String();
		// schemaLocation ?
		if (schemaLocation == null) {
			s += "noSchema, ";
		} else {
			s += "schema=" + schemaLocation + ", ";
		}
		// coding mode
		s += "codingMode=" + codingMode + ", ";
		// fidelityOptions
		s += "fidelityOptions=" + getFidelityOptions().toString();
		// blockSize
		if (blockSize != Constants.DEFAULT_BLOCK_SIZE) {
			s += ",bs=" + blockSize;
		}
		//  valueMaxLength
		if (valueMaxLength != Constants.DEFAULT_VALUE_MAX_LENGTH) {
			s += ",vml=" + valueMaxLength;
		}
		// valuePartitionCapacity
		if (valuePartitionCapacity != Constants.DEFAULT_VALUE_PARTITON_CAPACITY ) {
			s += ",vpc=" + valuePartitionCapacity;
		}

		return s;
	}

}