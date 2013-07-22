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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.BinaryBase64Datatype;
import com.siemens.ct.exi.datatype.BinaryHexDatatype;
import com.siemens.ct.exi.datatype.BooleanDatatype;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatetimeDatatype;
import com.siemens.ct.exi.datatype.DecimalDatatype;
import com.siemens.ct.exi.datatype.FloatDatatype;
import com.siemens.ct.exi.datatype.IntegerDatatype;
import com.siemens.ct.exi.datatype.ListDatatype;
import com.siemens.ct.exi.datatype.StringDatatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.util.xml.QNameUtilities;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
 */

public abstract class AbstractRepresentationMapTypeCoder implements TypeCoder {

	protected final QName[] dtrMapTypes;
	protected final QName[] dtrMapRepresentations;

	protected Map<QName, Datatype> dtrMap;

	protected Datatype recentDtrDataype;

	public AbstractRepresentationMapTypeCoder(QName[] dtrMapTypes,
			QName[] dtrMapRepresentations) throws EXIException {
		this.dtrMapTypes = dtrMapTypes;
		this.dtrMapRepresentations = dtrMapRepresentations;
		dtrMap = new HashMap<QName, Datatype>();

		assert (dtrMapTypes.length == dtrMapRepresentations.length);

		// binary
		dtrMap.put(
				BuiltIn.XSD_BASE64BINARY,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI,
						Constants.W3C_EXI_LN_BASE64BINARY));
		dtrMap.put(
				BuiltIn.XSD_HEXBINARY,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_HEXBINARY));
		// boolean
		dtrMap.put(BuiltIn.XSD_BOOLEAN,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_BOOLEAN));
		// date-times
		dtrMap.put(BuiltIn.XSD_DATETIME,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_DATETIME));
		dtrMap.put(BuiltIn.XSD_TIME,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_TIME));
		dtrMap.put(BuiltIn.XSD_DATE,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_DATE));
		dtrMap.put(
				BuiltIn.XSD_GYEARMONTH,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI,
						Constants.W3C_EXI_LN_GYEARMONTH));
		dtrMap.put(BuiltIn.XSD_GYEAR,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_GYEAR));
		dtrMap.put(
				BuiltIn.XSD_GMONTHDAY,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_GMONTHDAY));
		dtrMap.put(BuiltIn.XSD_GDAY,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_GDAY));
		dtrMap.put(BuiltIn.XSD_GMONTH,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_GMONTH));
		// decimal
		dtrMap.put(BuiltIn.XSD_DECIMAL,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_DECIMAL));
		// float
		dtrMap.put(BuiltIn.XSD_FLOAT,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_DOUBLE));
		dtrMap.put(BuiltIn.XSD_DOUBLE,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_DOUBLE));
		// integer
		dtrMap.put(BuiltIn.XSD_INTEGER,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_INTEGER));
		// string
		dtrMap.put(BuiltIn.XSD_STRING,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_STRING));
		dtrMap.put(BuiltIn.XSD_ANY_SIMPLE_TYPE,
				getDatatypeRepresentation(Constants.W3C_EXI_NS_URI, Constants.W3C_EXI_LN_STRING));
		// all types derived by union are done differently

		for (int i = 0; i < dtrMapTypes.length; i++) {
			QName dtrMapRepr = dtrMapRepresentations[i];
			Datatype representation = getDatatypeRepresentation(
					dtrMapRepr.getNamespaceURI(), dtrMapRepr.getLocalPart());
			QName type = dtrMapTypes[i];
			dtrMap.put(type, representation);
		}
	}

	protected void updateRecentDtr(Datatype datatype) {
		if (datatype == BuiltIn.DEFAULT_DATATYPE) {
			// e.g., untyped values are encoded always as String
			recentDtrDataype = datatype;
		} else {
			// check mappings
			QNameContext qncSchemaType = datatype.getSchemaType();
			QName schemaType = qncSchemaType.getQName();

			recentDtrDataype = dtrMap.get(schemaType);
			
			// unions
			if (recentDtrDataype == null
					&& datatype.getBuiltInType() == BuiltInType.STRING
					&& ((StringDatatype) datatype).isDerivedByUnion()) {
				recentDtrDataype = datatype;
				// union ancestors of interest
				QNameContext baseType = qncSchemaType.getSimpleBaseType();
				if (baseType != null) {
					Datatype dtBase = baseType.getSimpleDatatype();
					if (dtBase != null
							&& dtBase.getBuiltInType() == BuiltInType.STRING
							&& ((StringDatatype) dtBase).isDerivedByUnion()) {
						// check again
						recentDtrDataype = null;
					}
				}
			}
			// lists
			if (recentDtrDataype == null
					&& datatype.getBuiltInType() == BuiltInType.LIST) {
				recentDtrDataype = datatype;
				// list ancestors of interest
				QNameContext baseType = qncSchemaType.getSimpleBaseType();
				if (baseType != null) {
					Datatype dtBase = baseType.getSimpleDatatype();
					if (dtBase != null
							&& dtBase.getBuiltInType() == BuiltInType.LIST) {
						// check again
						recentDtrDataype = null;
					}
				}

			}
			// enums
			if (recentDtrDataype == null
					&& datatype.getBuiltInType() == BuiltInType.ENUMERATION) {
				recentDtrDataype = datatype;
				// only ancestor types that have enums are of interest
				QNameContext baseType = qncSchemaType.getSimpleBaseType();
				if (baseType != null) {
					Datatype dtBase = baseType.getSimpleDatatype();
					if (dtBase != null
							&& dtBase.getBuiltInType() == BuiltInType.ENUMERATION) {
						// check again
						recentDtrDataype = null;
					}
				}
			}
			if (recentDtrDataype == null) {
				// no mapping yet
				recentDtrDataype = updateDatatype(qncSchemaType);
				// special integer handling
				if (recentDtrDataype.getBuiltInType() == BuiltInType.INTEGER
						&& (datatype.getBuiltInType() == BuiltInType.NBIT_UNSIGNED_INTEGER || datatype
								.getBuiltInType() == BuiltInType.UNSIGNED_INTEGER)) {
					recentDtrDataype = datatype;
				}
			}
		}
		
		// list item types
		assert(recentDtrDataype != null);
		if(recentDtrDataype.getBuiltInType() == BuiltInType.LIST) {
			Datatype prev = recentDtrDataype;
			ListDatatype ldt = (ListDatatype) recentDtrDataype;
			Datatype dtList = ldt.getListDatatype();
			this.updateRecentDtr(dtList);
			if(recentDtrDataype != dtList) {
				// update item codec
				recentDtrDataype = new ListDatatype(recentDtrDataype, ldt.getSchemaType());
			} else {
				recentDtrDataype = prev;
			}
			
		}
	}

	protected Datatype updateDatatype(QNameContext qncSchemaType) {
		QNameContext simpleBaseType = qncSchemaType.getSimpleBaseType();
		Datatype dt = dtrMap.get(simpleBaseType.getQName());
		if (dt == null) {
			dt = updateDatatype(simpleBaseType);
		} else {
			dtrMap.put(simpleBaseType.getQName(), dt);
		}

		return dt;
	}

	protected Datatype getRecentDtrMapDatatype() {
		return recentDtrDataype;
	}

	protected Datatype getDatatypeRepresentation(String reprUri,
			String reprLocalPart) throws EXIException {
		try {
			// find datatype for given representation
			Datatype datatype = null;
			if (Constants.W3C_EXI_NS_URI.equals(reprUri)) {
				// EXI built-in datatypes
				// see http://www.w3.org/TR/exi/#builtInEXITypes
				if ("base64Binary".equals(reprLocalPart)) {
					datatype = new BinaryBase64Datatype(null);
				} else if ("hexBinary".equals(reprLocalPart)) {
					datatype = new BinaryHexDatatype(null);
				} else if ("boolean".equals(reprLocalPart)) {
					datatype = new BooleanDatatype(null);
				} else if ("dateTime".equals(reprLocalPart)) {
					datatype = new DatetimeDatatype(DateTimeType.dateTime, null);
				} else if ("time".equals(reprLocalPart)) {
					datatype = new DatetimeDatatype(DateTimeType.time, null);
				} else if ("date".equals(reprLocalPart)) {
					datatype = new DatetimeDatatype(DateTimeType.date, null);
				} else if ("gYearMonth".equals(reprLocalPart)) {
					datatype = new DatetimeDatatype(DateTimeType.gYearMonth,
							null);
				} else if ("gYear".equals(reprLocalPart)) {
					datatype = new DatetimeDatatype(DateTimeType.gYear, null);
				} else if ("gMonthDay".equals(reprLocalPart)) {
					datatype = new DatetimeDatatype(DateTimeType.gMonthDay,
							null);
				} else if ("gDay".equals(reprLocalPart)) {
					datatype = new DatetimeDatatype(DateTimeType.gDay, null);
				} else if ("gMonth".equals(reprLocalPart)) {
					datatype = new DatetimeDatatype(DateTimeType.gMonth, null);
				} else if ("decimal".equals(reprLocalPart)) {
					datatype = new DecimalDatatype(null);
				} else if ("double".equals(reprLocalPart)) {
					datatype = new FloatDatatype(null);
				} else if ("integer".equals(reprLocalPart)) {
					datatype = new IntegerDatatype(null);
				} else if ("string".equals(reprLocalPart)) {
					datatype = new StringDatatype(null);
				} else {
					throw new EXIException(
							"[EXI] Unsupported datatype representation: {"
									+ reprUri + "}" + reprLocalPart);
				}
			} else {
				// try to load datatype
				String className = QNameUtilities.getClassName(new QName(
						reprUri, reprLocalPart));
				@SuppressWarnings("rawtypes")
				Class c = Class.forName(className);
				Object o = c.newInstance();
				if (o instanceof Datatype) {
					datatype = (Datatype) o;
				} else {
					throw new Exception("[EXI] no Datatype instance");
				}
			}

			return datatype;
		} catch (Exception e) {
			throw new EXIException(e);
		}
	}

}
