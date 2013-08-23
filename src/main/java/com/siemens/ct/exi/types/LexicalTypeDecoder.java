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
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2-SNAPSHOT
 */

public class LexicalTypeDecoder extends AbstractTypeDecoder {

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

	public LexicalTypeDecoder() {
		super();
	}

	public Value readValue(Datatype datatype, QNameContext qnContext,
			DecoderChannel valueChannel, StringDecoder stringDecoder)
			throws IOException {
		Value val;
		switch (datatype.getDatatypeID()) {
		case exi_base64Binary:
			val = rcsBase64Binary.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_hexBinary:
			val = rcsHexBinary.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_boolean:
			val = rcsBoolean.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_dateTime:
		case exi_time:
		case exi_date:
		case exi_gYearMonth:
		case exi_gYear:
		case exi_gMonthDay:
		case exi_gDay:
		case exi_gMonth:
			val = rcsDateTime.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_decimal:
			val = rcsDecimal.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_double:
			val = rcsDouble.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_integer:
			val = rcsInteger.readValue(qnContext, valueChannel, stringDecoder);
			break;
		case exi_string:
			val = rcsString.readValue(qnContext, valueChannel, stringDecoder);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		
		return val;
	}
}
