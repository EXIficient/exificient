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

import java.util.HashMap;
import java.util.List;
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
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.util.xml.QNameUtilities;
import com.siemens.ct.exi.values.DateTimeType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public abstract class AbstractRepresentationMapTypeCoder implements TypeCoder {

	protected final Grammar grammar;

	protected Map<QName, Datatype> dtrMap;

	protected Datatype recentDtrDataype;

	public AbstractRepresentationMapTypeCoder(QName[] dtrMapTypes,
			QName[] dtrMapRepresentations, Grammar grammar) throws EXIException {
		this.grammar = grammar;
		dtrMap = new HashMap<QName, Datatype>();
		
		assert (dtrMapTypes.length == dtrMapRepresentations.length);
		/*
		 * When there are built-in or user-defined datatype representations
		 * associated with more than one XML Schema datatype in the type
		 * hierarchy of a particular datatype, the closest ancestor with an
		 * associated datatype representation is used to determine the EXI
		 * datatype representation.
		 */
		for(int i=0; i<dtrMapTypes.length; i++) {
			Datatype datatypeRep = getDatatypeRepresentation(dtrMapRepresentations[i]);
			registerDatatype(datatypeRep, dtrMapTypes[i], dtrMapTypes);
		}
		
		
//		int[] ancestorOrder = getAncestorOrder(new AncestorTypeComparator(
//				grammar), dtrMapTypes);
//		assert (ancestorOrder.length == dtrMapTypes.length);
//
//		
//
//		// detect all subtypes and map datatype representation
//		for (int i = 0; i < ancestorOrder.length; i++) {
//			int ancIndex = ancestorOrder[i];
//
//			Datatype datatypeRep = getDatatypeRepresentation(dtrMapRepresentations[ancIndex]);
//			registerDatatype(datatypeRep, dtrMapTypes[ancIndex], dtrMapTypes);
//		}
	}

	protected Datatype getRecentDtrMapDatatype() {
		return recentDtrDataype;
	}
	
	private boolean isDerivedFrom(QName type, QName ancestor) {
		// type itself
		if (type.equals(ancestor)) {
			return true;
		}
		
		// subtypes
		List<QName> subtypes = grammar.getSimpleTypeSubtypes(ancestor);
		if (subtypes != null) {
			for (QName subtype : subtypes) {
				if ( isDerivedFrom(type, subtype) ) {
					return true;
				}
			}	
		}
		
		return false;
	}

	protected void registerDatatype(Datatype representation, QName type, QName[] dtrMapTypes) {
		// Integer types are special (exi:integer)
		if (representation.getBuiltInType() == BuiltInType.INTEGER_BIG) {
			// Note: exi:integer == BuiltInType.INTEGER_BIG
			
			// Detect whether type is derived from xsd:integer
			boolean isDerivedFromXsdInteger = isDerivedFrom(type, BuiltIn.XSD_INTEGER);
			
			if (isDerivedFromXsdInteger) {
				// built-in types set already (n-bit and unsigned integers)
				return;
			}
			
			//  other types uses default full exi:integer coding
		}
		
		dtrMap.put(type, representation);
		
		List<QName> subtypes = grammar.getSimpleTypeSubtypes(type);
		if (subtypes != null) {
			for (QName subtype : subtypes) {
				// register subtypes unless a default mapping is 
				// is present OR there is another DTR map for this type
				// see http://www.w3.org/XML/Group/EXI/docs/format/exi.html#builtInEXITypes
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
				} else if (contains(subtype, dtrMapTypes)) {
					// another mapping exists
				} else {
					registerDatatype(representation, subtype, dtrMapTypes);
				}
			}
		}
	}
	
	protected boolean contains(QName q, QName[] qnames) {
		for(QName qn: qnames) {
			if (qn.equals(q)) {
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
					datatype = new FloatDatatype(BuiltInType.DOUBLE, null);
				} else if ("integer".equals(localPart)) {
					datatype = new BigIntegerDatatype(BuiltInType.INTEGER_BIG,
							null);
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

//	protected int[] getAncestorOrder(AncestorTypeComparator atc,
//			QName[] dtrMapTypes) {
//		int order[] = new int[dtrMapTypes.length];
//
//		QName[] copyTypes = new QName[dtrMapTypes.length];
//		System.arraycopy(dtrMapTypes, 0, copyTypes, 0, dtrMapTypes.length);
//
//		// sort types in ancestor-type order
//		Arrays.sort(copyTypes, atc);
//
//		// save Index order
//		for (int i = 0; i < order.length; i++) {
//			QName ctype = copyTypes[i];
//			for (int k = 0; k < dtrMapTypes.length; k++) {
//				if (ctype.equals(dtrMapTypes[k])) {
//					// found "k" as index position
//					order[i] = k;
//				}
//			}
//		}
//
//		return order;
//	}
//
//	// inverse type hierarchy
//	// [int, long, integer] --> [integer, long, int]
//	class AncestorTypeComparator implements Comparator<QName> {
//
//		protected Grammar grammar;
//
//		public AncestorTypeComparator(Grammar grammar) {
//			this.grammar = grammar;
//		}
//
//		public int compare(QName o1, QName o2) {
//			if (o1.equals(o2)) {
//				return 0;
//			} else {
//				if (isSubType(o1, o2)) {
//					// return -1;
//					return 1;
//				} else {
//					// return 1;
//					return -1;
//				}
//			}
//		}
//
//		// is q1 subytpe of q2
//		public boolean isSubType(QName q1, QName q2) {
//			List<QName> subtypes = grammar.getSimpleTypeSubtypes(q2);
//			if (subtypes == null || subtypes.size() == 0) {
//				return false;
//			}
//
//			for (QName stype : subtypes) {
//				if (q1.equals(stype)) {
//					return true;
//				}
//
//				if (isSubType(q1, stype)) {
//					return true;
//				}
//			}
//
//			return false;
//		}
//	}
}
