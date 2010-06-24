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

package com.siemens.ct.exi.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.BigIntegerDatatype;
import com.siemens.ct.exi.datatype.BinaryBase64Datatype;
import com.siemens.ct.exi.datatype.BinaryHexDatatype;
import com.siemens.ct.exi.datatype.BooleanDatatype;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatetimeDatatype;
import com.siemens.ct.exi.datatype.DecimalDatatype;
import com.siemens.ct.exi.datatype.FloatDatatype;
import com.siemens.ct.exi.datatype.StringDatatype;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.xml.QNameUtilities;
import com.siemens.ct.exi.values.DateTimeType;
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20081105
 */

public class DatatypeRepresentationMapTypeDecoder extends AbstractTypeDecoder
		implements DatatypeRepresentationMap {
	
	// fallback type decoder
	protected TypedTypeDecoder defaultDecoder;

	protected Map<QName, Datatype> dtrMap;

	public DatatypeRepresentationMapTypeDecoder(StringDecoder stringDecoder) {
		super(stringDecoder);

		dtrMap = new HashMap<QName, Datatype>();
		
		// hand over "same" string table
		defaultDecoder = new TypedTypeDecoder(stringDecoder);
	}


	public void registerDatatypeRepresentation(QName type, QName representation)
			throws EXIException {
		try {
			// find datatype for given representation
			Datatype datatype = null;
			if (Constants.W3C_EXI_NS_URI.equals(representation
					.getNamespaceURI())) {
				// EXI built-in datatypes
				// see http://www.w3.org/TR/exi/#builtInEXITypes
				String localPart = representation.getLocalPart();
				if ("base64Binary".equals(localPart)) {
					datatype = new BinaryBase64Datatype(null);
				} else if ("hexBinary".equals(localPart)) {
					datatype = new BinaryHexDatatype(null);
				} else if ("boolean".equals(localPart)) {
					datatype = new BooleanDatatype(null);
				} else if ("dateTime".equals(localPart)) {
					datatype = new DatetimeDatatype(DateTimeType.dateTime, null);
				} else if ("time".equals(localPart)) {
					datatype = new DatetimeDatatype(DateTimeType.time, null);
				} else if ("date".equals(localPart)) {
					datatype = new DatetimeDatatype(DateTimeType.date, null);
				} else if ("gYearMonth".equals(localPart)) {
					datatype = new DatetimeDatatype(DateTimeType.gMonthDay,
							null);
				} else if ("gDay".equals(localPart)) {
					datatype = new DatetimeDatatype(DateTimeType.gDay, null);
				} else if ("gMonth".equals(localPart)) {
					datatype = new DatetimeDatatype(DateTimeType.gMonth, null);
				} else if ("decimal".equals(localPart)) {
					datatype = new DecimalDatatype(null);
				} else if ("double".equals(localPart)) {
					datatype = new FloatDatatype(BuiltInType.DOUBLE, null);
				} else if ("integer".equals(localPart)) {
					datatype = new BigIntegerDatatype(null);
				} else if ("string".equals(localPart)) {
					datatype = new StringDatatype(null);
				} else {
					throw new EXIException(
							"[EXI] Unsupported datatype representation: "
									+ representation);
				}
			} else {
				// try to load datatype
				String className = QNameUtilities.getClassName(representation);
				@SuppressWarnings("unchecked")
				Class c = Class.forName(className);
				Object o = c.newInstance();
				if (o instanceof Datatype) {
					datatype = (Datatype) o;
				} else {
					throw new Exception("[EXI] no Datatype instance");
				}
			}

			dtrMap.put(type, datatype);
		} catch (Exception e) {
			throw new EXIException(e);
		}
	}

	public Value readValue(Datatype datatype, QName context,
			DecoderChannel valueChannel) throws IOException {
		QName schemaType = datatype.getSchemaType();
		Datatype recentDtrDataype = dtrMap.get(schemaType);
		if (recentDtrDataype == null) {
			return defaultDecoder.readValue(datatype, context, valueChannel);
		} else {
			return recentDtrDataype.readValue(valueChannel, stringDecoder, context);
		}
	}


}
