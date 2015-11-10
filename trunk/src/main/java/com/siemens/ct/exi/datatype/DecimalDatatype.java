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

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.DecimalValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class DecimalDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -1045398309238670727L;

	protected DecimalValue lastValidDecimal;

	public DecimalDatatype(QNameContext schemaType) {
		super(BuiltInType.DECIMAL, schemaType);
	}
	
	public DatatypeID getDatatypeID() {
		return DatatypeID.exi_decimal;
	}

	protected boolean isValidString(String value) {
		lastValidDecimal = DecimalValue.parse(value);
		return (lastValidDecimal != null);
	}

	public boolean isValid(Value value) {
		if (value instanceof DecimalValue) {
			lastValidDecimal = ((DecimalValue) value);
			return true;
		} else {
			return isValidString(value.toString());
		}
	}

	public void writeValue(QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {
		valueChannel.encodeDecimal(lastValidDecimal.isNegative(),
				lastValidDecimal.getIntegral(),
				lastValidDecimal.getRevFractional());
	}

	public Value readValue(QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {
		return valueChannel.decodeDecimalValue();
	}
}