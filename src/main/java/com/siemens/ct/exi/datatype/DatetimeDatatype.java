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

package com.siemens.ct.exi.datatype;

import java.io.IOException;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.types.DateTimeType;
import com.siemens.ct.exi.values.DateTimeValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2
 */

public class DatetimeDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -3235729895220215225L;

	DateTimeType datetimeType;

	private DateTimeValue lastValidDatetime;

	public DatetimeDatatype(DateTimeType dateType, QNameContext schemaType) {
		super(BuiltInType.DATETIME, schemaType);
		this.datetimeType = dateType;
	}
	
	public DatatypeID getDatatypeID() {
		DatatypeID dtID;
		switch(datetimeType) {
		case dateTime:
			dtID = DatatypeID.exi_dateTime;
			break;
		case time:
			dtID = DatatypeID.exi_time;
			break;
		case date:
			dtID = DatatypeID.exi_date;
			break;
		case gYearMonth:
			dtID = DatatypeID.exi_gYearMonth;
			break;
		case gYear:
			dtID = DatatypeID.exi_gYear;
			break;
		case gMonthDay:
			dtID = DatatypeID.exi_gMonthDay;
			break;
		case gDay:
			dtID = DatatypeID.exi_gDay;
			break;
		case gMonth:
			dtID = DatatypeID.exi_gMonth;
			break;
		default:
			throw new UnsupportedOperationException();
		}
		return dtID;
	}

	public DateTimeType getDatetimeType() {
		return datetimeType;
	}

	protected boolean isValidString(String value) {
		lastValidDatetime = DateTimeValue.parse(value, datetimeType);
		return (lastValidDatetime != null);
	}

	public boolean isValid(Value value) {
		if (value instanceof DateTimeValue) {
			lastValidDatetime = ((DateTimeValue) value);
			return true;
		} else {
			return isValidString(value.toString());
		}
	}

	public void writeValue(QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {
		valueChannel.encodeDateTime(lastValidDatetime);
	}

	public Value readValue(QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {
		return valueChannel.decodeDateTimeValue(datetimeType);
	}

}