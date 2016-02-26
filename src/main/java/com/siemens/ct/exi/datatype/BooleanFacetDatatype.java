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

package com.siemens.ct.exi.datatype;

import java.io.IOException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class BooleanFacetDatatype extends AbstractDatatype {

	private int lastValidBooleanID;
	private boolean lastValidBoolean;

	public BooleanFacetDatatype(QNameContext schemaType) {
		super(BuiltInType.BOOLEAN_FACET, schemaType);
	}
	
	public DatatypeID getDatatypeID() {
		return DatatypeID.exi_boolean;
	}

	protected boolean isValidString(String value) {
		value = value.trim();
		boolean retValue = true;

		if (value.equals(Constants.XSD_BOOLEAN_FALSE)) {
			lastValidBooleanID = 0;
			lastValidBoolean = false;
		} else if (value.equals(Constants.XSD_BOOLEAN_0)) {
			lastValidBooleanID = 1;
			lastValidBoolean = false;
		} else if (value.equals(Constants.XSD_BOOLEAN_TRUE)) {
			lastValidBooleanID = 2;
			lastValidBoolean = true;
		} else if (value.equals(Constants.XSD_BOOLEAN_1)) {
			lastValidBooleanID = 3;
			lastValidBoolean = true;
		} else {
			retValue = false;
		}

		return retValue;
	}

	public boolean isValid(Value value) {
		if (value instanceof BooleanValue) {
			lastValidBoolean = ((BooleanValue) value).toBoolean();
			// TODO not fully correct
			lastValidBooleanID = lastValidBoolean ? 2 : 0;
			return true;
		} else {
			return isValidString(value.toString());
		}
	}

	public void writeValue(QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastValidBooleanID, 2);
	}
	
	

	public Value readValue(QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {
		int booleanID = valueChannel.decodeNBitUnsignedInteger(2);
		return BooleanValue.getBooleanValue(booleanID);
	}
}