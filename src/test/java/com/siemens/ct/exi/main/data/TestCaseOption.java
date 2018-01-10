/*
 * Copyright (c) 2007-2018 Siemens AG
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

package com.siemens.ct.exi.main.data;

import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.Constants;
import com.siemens.ct.exi.core.EncodingOptions;
import com.siemens.ct.exi.core.FidelityOptions;

public class TestCaseOption {
	private CodingMode codingMode;
	private FidelityOptions fidelityOptions;
	private List<String> sharedStrings;
	private boolean isNonEvolving;
	private boolean fragments;
	private String schemaLocation;
	private boolean xmlEqual;
	private boolean schemaInformedOnly;
	private QName[] scElements;
	private int blockSize = Constants.DEFAULT_BLOCK_SIZE;
	private int valueMaxLength = Constants.DEFAULT_VALUE_MAX_LENGTH;
	private int valuePartitionCapacity = Constants.DEFAULT_VALUE_PARTITON_CAPACITY;
	private boolean localValuePartitions = true;
	private int maximumNumberOfEvolvingBuiltInElementGrammars = -1;
	private int maximumNumberOfBuiltInProductions = -1;
	private QName[] dtrMapTypes;
	private QName[] dtrMapRepresentations;
	private EncodingOptions encodingOptions;

	// private String profile;

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
	
	public void setSharedStrings(List<String> sharedStrings) {
		this.sharedStrings = sharedStrings;
	}
	
	public List<String> getSharedStrings() {
		return this.sharedStrings;
	}
	
	public void setUsingNonEvolvingGrammars(boolean isNonEvolving) {
		this.isNonEvolving = isNonEvolving;
	}

	public boolean isUsingNonEvolvingGrammars() {
		return this.isNonEvolving;
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

	public void setLocalValuePartitions(boolean localValuePartitions) {
		this.localValuePartitions = localValuePartitions;
	}

	public boolean isLocalValuePartitions() {
		return localValuePartitions;
	}

	public void setMaximumNumberOfBuiltInProductions(
			int maximumNumberOfBuiltInProductions) {
		this.maximumNumberOfBuiltInProductions = maximumNumberOfBuiltInProductions;

	}

	public int getMaximumNumberOfBuiltInProductions() {
		return this.maximumNumberOfBuiltInProductions;

	}

	public void setMaximumNumberOfEvolvingBuiltInElementGrammars(
			int maximumNumberOfEvolvingBuiltInElementGrammars) {
		this.maximumNumberOfEvolvingBuiltInElementGrammars = maximumNumberOfEvolvingBuiltInElementGrammars;
	}

	public int getMaximumNumberOfEvolvingBuiltInElementGrammars() {
		return this.maximumNumberOfEvolvingBuiltInElementGrammars;
	}

	public void setEncodingOptions(EncodingOptions encodingOptions) {
		this.encodingOptions = encodingOptions;
	}

	public EncodingOptions getEncodingOptions() {
		return encodingOptions;
	}

	// public String getProfile() {
	// return profile;
	// }
	//
	// public void setProfile(String profile) {
	// this.profile = profile;
	// }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		// schemaLocation ?
		if (schemaLocation == null) {
			sb.append("noSchema, ");
		} else {
			sb.append("schema=" + schemaLocation + ", ");
		}
		// coding mode
		sb.append("codingMode=" + codingMode + ", ");
		// fidelityOptions
		sb.append("fidelityOptions=" + getFidelityOptions().toString());
		// selfContained elements
		if (this.scElements != null && this.scElements.length > 0) {
			sb.append(", scElements=[");
			for (int i = 0; i < scElements.length; i++) {
				QName sc = scElements[i];
				sb.append(sc + ",");
			}
			sb.append("]");
		}
		// blockSize
		if (blockSize != Constants.DEFAULT_BLOCK_SIZE) {
			sb.append(",bs=" + blockSize);
		}
		// valueMaxLength
		if (valueMaxLength != Constants.DEFAULT_VALUE_MAX_LENGTH) {
			sb.append(",vml=" + valueMaxLength);
		}
		// valuePartitionCapacity
		if (valuePartitionCapacity != Constants.DEFAULT_VALUE_PARTITON_CAPACITY) {
			sb.append(",vpc=" + valuePartitionCapacity);
		}
		// localValuePartitions
		if (this.localValuePartitions) {
			sb.append(",localValuePartitions=TRUE");
		}
		// maximumNumberOfBuiltInProductions
		if (this.maximumNumberOfBuiltInProductions >= 0) {
			sb.append(",maximumNumberOfBuiltInProductions="
					+ maximumNumberOfBuiltInProductions);
		}
		// maximumNumberOfEvolvingBuiltInElementGrammars
		if (this.maximumNumberOfEvolvingBuiltInElementGrammars >= 0) {
			sb.append(",maximumNumberOfEvolvingBuiltInElementGrammars="
					+ maximumNumberOfEvolvingBuiltInElementGrammars);
		}

		// // profile
		// if (this.profile != null) {
		// sb.append(",profile=" + profile);
		// }

		return sb.toString();
	}

}
