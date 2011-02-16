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

package com.siemens.ct.exi.values;

import java.io.IOException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class FloatValue extends AbstractValue {

	private static final long serialVersionUID = 5799093635881195073L;
	
//	public final long mantissa;
//	public final long exponent;
	public long mantissa;
	public long exponent;
	
	protected int slenMantissa = -1;

	protected Double f;

	public FloatValue(long mantissa, long exponent) {
		setValues(mantissa, exponent);
//		this.mantissa = mantissa;
//		this.exponent = exponent;
	}
	
	public void setValues(long mantissa, long exponent) {
		this.mantissa = mantissa;
		this.exponent = exponent;
	}

	public static FloatValue parse(String value) {
		try {
			long sMantissa, sExponent;
			value = value.trim();
			if (value.length() == 0) {
				return null;
			} else if (value.equals(Constants.FLOAT_INFINITY)) {
				sMantissa = Constants.FLOAT_MANTISSA_INFINITY;
				sExponent = Constants.FLOAT_SPECIAL_VALUES;
			} else if (value.equals(Constants.FLOAT_MINUS_INFINITY)) {
				sMantissa = Constants.FLOAT_MANTISSA_MINUS_INFINITY;
				sExponent = Constants.FLOAT_SPECIAL_VALUES;
			} else if (value.equals(Constants.FLOAT_NOT_A_NUMBER)) {
				sMantissa = Constants.FLOAT_MANTISSA_NOT_A_NUMBER;
				sExponent = Constants.FLOAT_SPECIAL_VALUES;
			} else {
				char[] chars = value.toCharArray();

				int decimalDigits = 0;
				int len = chars.length;
				int pos = 0;
				sMantissa = 0;
				sExponent = 0;
				char c;
				boolean negative = false;
				boolean negativeExponent = false;

				// status: detecting sign
				if ((c = chars[pos]) == '+') {
					pos++;
				} else if (c == '-') {
					negative = true;
					pos++;
				}

				// status: parsing mantissa before decimal point
				while (pos < len && (c = chars[pos++]) != '.' && c != 'e'
						&& c != 'E') {
					if (c == '0') {
						sMantissa = 10 * sMantissa;
					} else if (c > '0' && c <= '9') {
						sMantissa = 10 * sMantissa + (c - '0');
					} else {
						return null;
					}
				}

				// status: parsing mantissa after decimal point
				if (c == '.') {
					while (pos < len && (c = chars[pos++]) != 'e' && c != 'E') {
						if (c == '0') {
							sMantissa = 10 * sMantissa;
							decimalDigits++;
						} else if (c > '0' && c <= '9') {
							sMantissa = 10 * sMantissa + (c - '0');
							decimalDigits++;
						} else {
							return null;
						}
					}
				}

				// mantissa overflow ?
				if (sMantissa < 0) {
					if (negative) {
						if (sMantissa != Long.MIN_VALUE) {
							return null;
						}
					} else {
						return null;
					}
				}
				
				
				// status: parsing exponent after e or E
				if (c == 'e' || c == 'E') {
					// status: checking sign of exponent
					if ((c = chars[pos]) == '-') {
						negativeExponent = true;
						pos++;
					} else if (c == '+') {
						pos++;
					}

					while (pos < len) {
						// c = s.charAt ( pos++ );
						c = chars[pos++];

						if (c >= '0' && c <= '9') {
							sExponent = 10 * sExponent + (c - '0');
						} else {
							return null;
						}
					}

					if (negativeExponent) {
						sExponent = -sExponent;
					}
				}

				// check whether whole string has been parsed successfully
				if (pos != len) {
					return null;
				}

				// adjust exponent and mantissa
				sExponent -= decimalDigits;

				if (negative) {
					sMantissa = -sMantissa;
				}

				// too large ranges
				if (sMantissa < Constants.FLOAT_MANTISSA_MIN_RANGE
						|| sMantissa > Constants.FLOAT_MANTISSA_MAX_RANGE
						|| sExponent < Constants.FLOAT_EXPONENT_MIN_RANGE
						|| sExponent > Constants.FLOAT_EXPONENT_MAX_RANGE) {
					return null;
				}

				// always encode zero as 0E0
				if (sMantissa == 0) {
					sExponent = 0;
				}
			}

			return new FloatValue(sMantissa, sExponent);
		} catch (Exception e) {
			// e.g. out-of-bound exception
			return null;
		}
	}

	public static FloatValue parse(float f) throws IOException {
		int sMantissa, sExponent;
		// infinity & not a number
		if (Float.isInfinite(f) || Float.isNaN(f)) {
			// exponent value is -(2^14),
			// . the mantissa value 1 represents INF,
			// . the mantissa value -1 represents -INF
			// . any other mantissa value represents NaN
			if (Float.isNaN(f)) {
				sMantissa = Constants.FLOAT_MANTISSA_NOT_A_NUMBER; // m
			} else if (f < 0) {
				sMantissa = Constants.FLOAT_MANTISSA_MINUS_INFINITY; // m
			} else {
				sMantissa = Constants.FLOAT_MANTISSA_INFINITY; // m
			}
			// exponent (special value)
			sExponent = Constants.FLOAT_SPECIAL_VALUES; // e == -(2^14)
		} else {
			/*
			 * floating-point according to the IEEE 754 floating-point
			 * "single format" bit layout.
			 */
			sExponent = 0;
			while (f - (int) f != 0.0f) {
				f *= 10;
				sExponent--;
			}
			sMantissa = (int) f;
		}
		return new FloatValue(sMantissa, sExponent);
	}

	public static FloatValue parse(double d) throws IOException {
		long sMantissa, sExponent;
		// infinity & not a number
		if (Double.isInfinite(d) || Double.isNaN(d)) {
			// exponent value is -(2^14),
			// . the mantissa value 1 represents INF,
			// . the mantissa value -1 represents -INF
			// . any other mantissa value represents NaN
			if (Double.isNaN(d)) {
				sMantissa = Constants.FLOAT_MANTISSA_NOT_A_NUMBER; // m
			} else if (d < 0) {
				sMantissa = Constants.FLOAT_MANTISSA_MINUS_INFINITY; // m
			} else {
				sMantissa = Constants.FLOAT_MANTISSA_INFINITY; // m
			}
			// exponent (special value)
			sExponent = Constants.FLOAT_SPECIAL_VALUES; // e == -(2^14)
		} else {
			/*
			 * floating-point according to the IEEE 754 floating-point
			 * "double format" bit layout.
			 */
			sExponent = 0;
			while (d - (long) d != 0.0d) {
				d *= 10;
				sExponent--;
			}
			sMantissa = (long) d;
		}
		return new FloatValue(sMantissa, sExponent);
	}

	public Float toFloat() {
		if (f == null) {
			toDouble();
		}
		return f.floatValue();
	}

	public Double toDouble() {
		if (f == null) {
			if (exponent == Constants.FLOAT_SPECIAL_VALUES) {
				if (mantissa == -1L) {
					f = Double.NEGATIVE_INFINITY;
				} else if (mantissa == 1) {
					f = Double.POSITIVE_INFINITY;
				} else {
					f = Double.NaN;
				}
			} else {
				f = mantissa * (double) (Math.pow(10, exponent));
			}
		}
		return f;
	}

	public int getCharactersLength() {
		if (slen == -1) {
			if (exponent == Constants.FLOAT_SPECIAL_VALUES) {
				if (mantissa == -1) {
					slen = Constants.FLOAT_MINUS_INFINITY_CHARARRAY.length;
				} else if (mantissa == 1) {
					slen = Constants.FLOAT_INFINITY_CHARARRAY.length;
				} else {
					slen = Constants.FLOAT_NOT_A_NUMBER_CHARARRAY.length;
				}
			} else {
				// iMantissa + "E" + iExponent;
				slenMantissa = MethodsBag.getStringSize(mantissa);
				slen = slenMantissa + 1 + MethodsBag.getStringSize(exponent);
			}
		}
		return slen;
	}

	public char[] toCharacters(char[] cbuffer, int offset) {
		if (exponent == Constants.FLOAT_SPECIAL_VALUES) {
			if (mantissa == -1) {
				return Constants.FLOAT_MINUS_INFINITY_CHARARRAY;
			} else if (mantissa == 1) {
				return Constants.FLOAT_INFINITY_CHARARRAY;
			} else {
				return Constants.FLOAT_NOT_A_NUMBER_CHARARRAY;
			}
		} else {
			MethodsBag.itos(exponent, offset + getCharactersLength(), cbuffer);
			cbuffer[offset + slenMantissa] = 'E';
			MethodsBag.itos(mantissa, offset + slenMantissa, cbuffer);

			return cbuffer;
		}
	}

	@Override
	public String toString() {
		if (exponent == Constants.FLOAT_SPECIAL_VALUES) {
			if (mantissa == -1) {
				return Constants.FLOAT_MINUS_INFINITY;
			} else if (mantissa == 1) {
				return Constants.FLOAT_INFINITY;
			} else {
				return Constants.FLOAT_NOT_A_NUMBER;
			}
		} else {
			char[] cbuffer = new char[getCharactersLength()];
			return new String(toCharacters(cbuffer, 0));
		}
	}

	@Override
	public String toString(char[] cbuffer, int offset) {
		if (exponent == Constants.FLOAT_SPECIAL_VALUES) {
			if (mantissa == -1) {
				return Constants.FLOAT_MINUS_INFINITY;
			} else if (mantissa == 1) {
				return Constants.FLOAT_INFINITY;
			} else {
				return Constants.FLOAT_NOT_A_NUMBER;
			}
		} else {
			return super.toString(cbuffer, offset);
		}
	}

	protected final boolean _equals(FloatValue o) {
		// e.g. 10E-1 vs. 1000E-3
		if (mantissa == o.mantissa && exponent == o.exponent) {
			return true;
		} else {
			
			if (exponent > o.exponent) {
				// e.g. 234E2 vs. 2340E1
				long diff = exponent - o.exponent;
				long eMantissa = mantissa;
				for(int i=0; i<diff; i++) {
					eMantissa *= 10;
				}
				return (eMantissa == o.mantissa );
			} else {
				// e.g. 30E0 vs. 3E1
				long diff = o.exponent - exponent;
				long eMantissa = o.mantissa;
				for(int i=0; i<diff; i++) {
					eMantissa *= 10;
				}
				return (mantissa == eMantissa );
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FloatValue) {
			return _equals((FloatValue) o);
		} else if (o instanceof String) {
			FloatValue f = FloatValue.parse((String) o);
			if (f == null) {
				return false;
			} else {
				return _equals(f);	
			}
		} else {
			return false;
		}
	}

}
