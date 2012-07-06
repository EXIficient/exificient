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

package com.siemens.ct.exi.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.GrammarUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.BinaryBase64Datatype;
import com.siemens.ct.exi.datatype.BinaryHexDatatype;
import com.siemens.ct.exi.datatype.BooleanDatatype;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatetimeDatatype;
import com.siemens.ct.exi.datatype.DecimalDatatype;
import com.siemens.ct.exi.datatype.FloatDatatype;
import com.siemens.ct.exi.datatype.IntegerDatatype;
import com.siemens.ct.exi.datatype.StringDatatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.util.xml.QNameUtilities;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public abstract class AbstractRepresentationMapTypeCoder implements TypeCoder {

	protected final Grammars grammar;

	protected Map<QName, Datatype> dtrMap;

	protected Datatype recentDtrDataype;

	protected final QNameContext qncXsdInteger;

	public AbstractRepresentationMapTypeCoder(QName[] dtrMapTypes,
			QName[] dtrMapRepresentations, Grammars grammar) throws EXIException {
		this.grammar = grammar;
		dtrMap = new HashMap<QName, Datatype>();

		GrammarContext gc = grammar.getGrammarContext();
		qncXsdInteger = gc.getGrammarUriContext(
				BuiltIn.XSD_INTEGER.getNamespaceURI()).getQNameContext(
				BuiltIn.XSD_INTEGER.getLocalPart());

		assert (dtrMapTypes.length == dtrMapRepresentations.length);
		/*
		 * When there are built-in or user-defined datatype representations
		 * associated with more than one XML Schema datatype in the type
		 * hierarchy of a particular datatype, the closest ancestor with an
		 * associated datatype representation is used to determine the EXI
		 * datatype representation.
		 */
		for (int i = 0; i < dtrMapTypes.length; i++) {
			Datatype representation = getDatatypeRepresentation(dtrMapRepresentations[i]);
			QName type = dtrMapTypes[i];
			GrammarUriContext guc = gc.getGrammarUriContext(type
					.getNamespaceURI());
			if (guc != null) {
				QNameContext qncType = guc.getQNameContext(type.getLocalPart());
				if (qncType != null) {
					registerDatatype(representation, qncType, dtrMapTypes);
				}
			}
		}
	}

	protected Datatype getRecentDtrMapDatatype() {
		return recentDtrDataype;
	}

	private boolean isDerivedFrom(QNameContext type, QNameContext ancestor) {
		// type itself
		if (type.equals(ancestor)) {
			return true;
		}

		List<QNameContext> subtypes = ancestor.getSimpleTypeSubtypes();
		if (subtypes != null) {
			for (QNameContext subtype : subtypes) {
				if (isDerivedFrom(type, subtype)) {
					return true;
				}
			}
		}

		return false;
	}

	protected void registerDatatype(Datatype representation, QNameContext type,
			QName[] dtrMapTypes) {
		// Integer types are special (exi:integer)
		if (representation.getBuiltInType() == BuiltInType.INTEGER) {
			// Note: exi:integer == BuiltInType.INTEGER_BIG

			// Detect whether type is derived from xsd:integer
			boolean isDerivedFromXsdInteger = isDerivedFrom(type, qncXsdInteger);

			if (isDerivedFromXsdInteger) {
				// built-in types set already (n-bit and unsigned integers)
				return;
			}

			// other types use default full exi:integer coding
		}

		dtrMap.put(type.getQName(), representation);

		List<QNameContext> subtypes = type.getSimpleTypeSubtypes();

		if (subtypes != null) {
			// for (QName subtype : subtypes) {
			for (QNameContext qncSubtype : subtypes) {
				QName subtype = qncSubtype.getQName();
				// register subtypes unless a default mapping is
				// is present OR there is another DTR map for this type
				// see
				// http://www.w3.org/XML/Group/EXI/docs/format/exi.html#builtInEXITypes
				if (BuiltIn.XSD_BASE64BINARY.equals(subtype)
						|| BuiltIn.XSD_HEXBINARY.equals(subtype)) {
					// Binary built-In
				} else if (BuiltIn.XSD_BOOLEAN.equals(subtype)) {
					// Boolean built-In
				} else if (BuiltIn.XSD_DATETIME.equals(subtype)
						|| BuiltIn.XSD_TIME.equals(subtype)
						|| BuiltIn.XSD_DATE.equals(subtype)
						|| BuiltIn.XSD_GYEARMONTH.equals(subtype)
						|| BuiltIn.XSD_GYEAR.equals(subtype)
						|| BuiltIn.XSD_GMONTHDAY.equals(subtype)
						|| BuiltIn.XSD_GDAY.equals(subtype)
						|| BuiltIn.XSD_GMONTH.equals(subtype)) {
					// Date-Time built-In
				} else if (BuiltIn.XSD_DECIMAL.equals(subtype)) {
					// Decimal built-In
				} else if (BuiltIn.XSD_FLOAT.equals(subtype)
						|| BuiltIn.XSD_DOUBLE.equals(subtype)) {
					// Float built-In
				} else if (BuiltIn.XSD_INTEGER.equals(subtype)) {
					// Integer built-In
				} else if (BuiltIn.XSD_STRING.equals(subtype)
						|| BuiltIn.XSD_ANY_SIMPLE_TYPE.equals(subtype)) {
					// String built-In
				} else if (contains(qncSubtype, dtrMapTypes)) {
					// another mapping exists
				} else {
					registerDatatype(representation, qncSubtype, dtrMapTypes);
				}
			}
		}
	}

	protected boolean contains(QNameContext q, QName[] qnames) {
		for (QName qn : qnames) {
			if (qn.equals(q.getQName())) {
				return true;
			}
		}

		return false;
	}

	protected Datatype getDatatypeRepresentation(QName representation)
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
					datatype = new FloatDatatype(null);
				} else if ("integer".equals(localPart)) {
					datatype = new IntegerDatatype(null);
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