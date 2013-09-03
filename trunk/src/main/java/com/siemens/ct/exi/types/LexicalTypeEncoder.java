/*
 * Copyright (C) 2007-2012 Siemens AG
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

package com.siemens.ct.exi.types;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.RestrictedCharacterSetDatatype;
import com.siemens.ct.exi.datatype.charset.XSDBase64CharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDBooleanCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDateTimeCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDecimalCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDoubleCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDHexBinaryCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDIntegerCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDStringCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2-SNAPSHOT
 */

public class LexicalTypeEncoder extends AbstractTypeEncoder {

	protected RestrictedCharacterSetDatatype rcsBase64Binary = new RestrictedCharacterSetDatatype(
			new XSDBase64CharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsHexBinary = new RestrictedCharacterSetDatatype(
			new XSDHexBinaryCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsBoolean = new RestrictedCharacterSetDatatype(
			new XSDBooleanCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsDateTime = new RestrictedCharacterSetDatatype(
			new XSDDateTimeCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsDecimal = new RestrictedCharacterSetDatatype(
			new XSDDecimalCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsDouble = new RestrictedCharacterSetDatatype(
			new XSDDoubleCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsInteger = new RestrictedCharacterSetDatatype(
			new XSDIntegerCharacterSet(), null);
	protected RestrictedCharacterSetDatatype rcsString = new RestrictedCharacterSetDatatype(
			new XSDStringCharacterSet(), null);

	protected Value lastValue;
	protected Datatype lastDatatype;

	public LexicalTypeEncoder() throws EXIException {
		this(null, null);
	}

	public LexicalTypeEncoder(QName[] dtrMapTypes, QName[] dtrMapRepresentations)
			throws EXIException {
		super(dtrMapTypes, dtrMapRepresentations);
	}

	public boolean isValid(Datatype datatype, Value value) {
		if (this.dtrMapInUse) {
			lastDatatype = this.getDtrDatatype(datatype);
		} else {
			lastDatatype = datatype;
		}

		lastValue = value;
		return true;
	}

	public void writeValue(QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {
		switch (lastDatatype.getDatatypeID()) {
		case exi_base64Binary:
			rcsBase64Binary.isValid(lastValue);
			rcsBase64Binary.writeValue(qnContext, valueChannel, stringEncoder);
			break;
		case exi_hexBinary:
			rcsHexBinary.isValid(lastValue);
			rcsHexBinary.writeValue(qnContext, valueChannel, stringEncoder);
			break;
		case exi_boolean:
			rcsBoolean.isValid(lastValue);
			rcsBoolean.writeValue(qnContext, valueChannel, stringEncoder);
			break;
		case exi_dateTime:
		case exi_time:
		case exi_date:
		case exi_gYearMonth:
		case exi_gYear:
		case exi_gMonthDay:
		case exi_gDay:
		case exi_gMonth:
			rcsDateTime.isValid(lastValue);
			rcsDateTime.writeValue(qnContext, valueChannel, stringEncoder);
			break;
		case exi_decimal:
			rcsDecimal.isValid(lastValue);
			rcsDecimal.writeValue(qnContext, valueChannel, stringEncoder);
			break;
		case exi_double:
			rcsDouble.isValid(lastValue);
			rcsDouble.writeValue(qnContext, valueChannel, stringEncoder);
			break;
		case exi_integer:
			rcsInteger.isValid(lastValue);
			rcsInteger.writeValue(qnContext, valueChannel, stringEncoder);
			break;
		case exi_string:
			rcsString.isValid(lastValue);
			rcsString.writeValue(qnContext, valueChannel, stringEncoder);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

}
