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

package com.siemens.ct.exi.values;

import java.io.IOException;

import com.siemens.ct.exi.Constants;

/**
 * A Float represented as two consecutive Integers. The first Integer represents
 * the mantissa of the floating point number and the second Integer represents
 * the 10-based exponent of the floating point number
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class FloatValue extends AbstractValue {

	protected final IntegerValue mantissa;
	protected final IntegerValue exponent;

	protected int slenMantissa = -1;

	public static IntegerValue FLOAT_SPECIAL_VALUES = IntegerValue
			.valueOf(Constants.FLOAT_SPECIAL_VALUES);
	public static IntegerValue FLOAT_NEGATIVE_INFINITY = IntegerValue
			.valueOf(-1);
	public static IntegerValue FLOAT_POSITIVE_INFINITY = IntegerValue
			.valueOf(1);
	public static IntegerValue FLOAT_NaN = IntegerValue.ZERO;

	protected Double f;

	public FloatValue(IntegerValue mantissa, IntegerValue exponent) {
		super(ValueType.FLOAT);

		// http://www.w3.org/TR/exi-c14n/#dt-float
		if (IntegerValue.ZERO.equals(mantissa)) {
			// If the mantissa is 0 and the exponent value is not
			// -(2<sup>14</sup>) to indicate one of the
			// special values then the exponent MUST be 0
			if (!FLOAT_SPECIAL_VALUES.equals(exponent)) {
				exponent = IntegerValue.ZERO;
			}
		} else {
			// If the mantissa is not 0, mantissas MUST have no trailing zeros
			// e.g., 12300E0 --> 123E2
			long lm = mantissa.longValue();
			long le = exponent.longValue();
			boolean modified = false;
			while (lm % 10 == 0) {
				// multiple of 10
				lm /= 10;
				le++;
				modified = true;
			}
			if (modified) {
				mantissa = IntegerValue.valueOf(lm);
				exponent = IntegerValue.valueOf(le);
			}
		}
		// If the exponent value is -(2^14) and the mantissa value is neither 1
		// nor -1, to indicate the special value not-a-number (NaN), the
		// mantissa MUST be 0.
		if (FLOAT_SPECIAL_VALUES.equals(exponent)
				&& !FLOAT_NEGATIVE_INFINITY.equals(mantissa)
				&& !FLOAT_POSITIVE_INFINITY.equals(mantissa)) {
			mantissa = FLOAT_NaN; // 0
		}

		this.mantissa = mantissa;
		this.exponent = exponent;
	}

	public FloatValue(long mantissa, long exponent) {
		this(IntegerValue.valueOf(mantissa), IntegerValue.valueOf(exponent));
	}

	/**
	 * Integer represents the mantissa of the floating point number
	 * 
	 * @return integer value
	 */
	public IntegerValue getMantissa() {
		return mantissa;
	}

	/**
	 * Integer represents the 10-based exponent of the floating point number
	 * 
	 * @return integer value
	 */
	public IntegerValue getExponent() {
		return exponent;
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

				{
					int indexE = value.indexOf('E');
					if (indexE == -1) {
						indexE = value.indexOf('e');
					}

					char c;
					char[] chars = value.toCharArray();

					// status: detecting sign
					boolean negative = ((c = chars[0]) == '-') ? true : false;

					int lenMantissa = (indexE == -1) ? chars.length : indexE;
					int startMantissa = negative || c == '+' ? 1 : 0;

					boolean decPoint = false;
					int decimalDigits = 0;
					sMantissa = 0;
					sExponent = 0;

					// invalid floats
					if (lenMantissa == 0) {
						return null;
					}

					// parsing mantissa
					for (int i = startMantissa; i < lenMantissa; i++) {
						c = chars[i];
						switch (c) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							sMantissa = 10 * sMantissa + (c - '0');
							if (decPoint) {
								decimalDigits++;
							}
							break;
						case '.':
							if (decPoint) {
								// decimal point twice
								return null;
							}
							decPoint = true;
							break;
						default:
							return null;
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

					if (negative) {
						sMantissa = (-1) * sMantissa;
					}

					// parsing exponent
					boolean negativeExp = false;
					if (indexE != -1) {
						for (int i = indexE + 1; i < chars.length; i++) {
							c = chars[i];
							switch (c) {
							case '0':
								sExponent = 10 * sExponent;
								break;
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								sExponent = 10 * sExponent + (c - '0');
								break;
							case '-':
								if (negativeExp) {
									// twice
									return null;
								}
								negativeExp = true;
								break;
							case '+':
								break;
							default:
								return null;
							}
						}
					}

					sExponent = negativeExp ? (-1) * sExponent : sExponent;
					sExponent -= decimalDigits;

					// too large ranges
					if (sMantissa < Constants.FLOAT_MANTISSA_MIN_RANGE
							|| sMantissa > Constants.FLOAT_MANTISSA_MAX_RANGE
							|| sExponent < Constants.FLOAT_EXPONENT_MIN_RANGE
							|| sExponent > Constants.FLOAT_EXPONENT_MAX_RANGE) {
						return null;
					}

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
			if (exponent.equals(FLOAT_SPECIAL_VALUES)) {
				if (mantissa.equals(FLOAT_NEGATIVE_INFINITY)) {
					f = Double.NEGATIVE_INFINITY;
				} else if (mantissa.equals(FLOAT_POSITIVE_INFINITY)) {
					f = Double.POSITIVE_INFINITY;
				} else {
					f = Double.NaN;
				}
			} else {
				// f = mantissa * (double) (Math.pow(10, exponent));
				long lMantissa = mantissa.longValue();
				long lExponent = exponent.longValue();

				f = lMantissa * (double) (Math.pow(10, lExponent));
			}
		}
		return f;
	}

	public int getCharactersLength() {
		if (slen == -1) {
			if (exponent.equals(FLOAT_SPECIAL_VALUES)) {
				if (mantissa.equals(FLOAT_NEGATIVE_INFINITY)) {
					slen = Constants.FLOAT_MINUS_INFINITY_CHARARRAY.length;
				} else if (mantissa.equals(FLOAT_POSITIVE_INFINITY)) {
					slen = Constants.FLOAT_INFINITY_CHARARRAY.length;
				} else {
					assert (mantissa.equals(FLOAT_NaN));
					slen = Constants.FLOAT_NOT_A_NUMBER_CHARARRAY.length;
				}
			} else {
				// iMantissa + "E" + iExponent;
				slenMantissa = mantissa.getCharactersLength(); // MethodsBag.getStringSize(mantissa);
				slen = slenMantissa + 1 + exponent.getCharactersLength(); // MethodsBag.getStringSize(exponent);
			}
		}
		return slen;
	}

	public void getCharacters(char[] cbuffer, int offset) {
		if (exponent.equals(FLOAT_SPECIAL_VALUES)) {
			char[] a2copy;
			if (mantissa.equals(FLOAT_NEGATIVE_INFINITY)) {
				// return Constants.FLOAT_MINUS_INFINITY_CHARARRAY;
				a2copy = Constants.FLOAT_MINUS_INFINITY_CHARARRAY;
			} else if (mantissa.equals(FLOAT_POSITIVE_INFINITY)) {
				// return Constants.FLOAT_INFINITY_CHARARRAY;
				a2copy = Constants.FLOAT_INFINITY_CHARARRAY;
			} else {
				assert (mantissa.equals(FLOAT_NaN));
				// return Constants.FLOAT_NOT_A_NUMBER_CHARARRAY;
				a2copy = Constants.FLOAT_NOT_A_NUMBER_CHARARRAY;
			}
			System.arraycopy(a2copy, 0, cbuffer, offset, a2copy.length);
		} else {
			mantissa.getCharacters(cbuffer, offset);
			offset += +slenMantissa;
			cbuffer[offset++] = 'E';
			exponent.getCharacters(cbuffer, offset);
			// return cbuffer;
		}
	}

	@Override
	public String toString() {
		if (exponent.equals(FLOAT_SPECIAL_VALUES)) {
			if (mantissa.equals(FLOAT_NEGATIVE_INFINITY)) {
				return Constants.FLOAT_MINUS_INFINITY;
			} else if (mantissa.equals(FLOAT_POSITIVE_INFINITY)) {
				return Constants.FLOAT_INFINITY;
			} else {
				assert (mantissa.equals(FLOAT_NaN));
				return Constants.FLOAT_NOT_A_NUMBER;
			}
		} else {
			char[] cbuffer = new char[getCharactersLength()];
			getCharacters(cbuffer, 0);
			return new String(cbuffer);
		}
	}

	@Override
	public String toString(char[] cbuffer, int offset) {
		if (exponent.equals(FLOAT_SPECIAL_VALUES)) {
			if (mantissa.equals(FLOAT_NEGATIVE_INFINITY)) {
				return Constants.FLOAT_MINUS_INFINITY;
			} else if (mantissa.equals(FLOAT_POSITIVE_INFINITY)) {
				return Constants.FLOAT_INFINITY;
			} else {
				assert (mantissa.equals(FLOAT_NaN));
				return Constants.FLOAT_NOT_A_NUMBER;
			}
		} else {
			return super.toString(cbuffer, offset);
		}
	}

	static long multiply(long a, long b) throws ArithmeticException {
		long result = a * b;
		// Note: fails for a = -1, b = Long.MIN_VALUE.
		if (a != 0 && result / a != b) {
			// overflow
			throw new ArithmeticException("Overflow");
		}

		return result;
	}

	protected final boolean _equals(FloatValue o) {
		// e.g. 10E-1 vs. 1000E-3
		if (mantissa == o.mantissa && exponent == o.exponent) {
			return true;
		} else {
			if (this.mantissa.equals(o.mantissa)
					&& this.exponent.equals(o.exponent)) {
				return true;
			} else {
				long tExponent = exponent.longValue();
				long oExponent = o.exponent.longValue();
				long tMantissa = mantissa.longValue();
				long oMantissa = o.mantissa.longValue();

				try {
					if (tExponent > oExponent) {
						// e.g. 234E2 vs. 2340E1
						long diff = tExponent - oExponent;
						for (int i = 0; i < diff; i++) {
							// tMantissa *= 10;
							tMantissa = multiply(tMantissa, 10);
						}
					} else {
						// e.g. 30E0 vs. 3E1
						long diff = oExponent - tExponent;
						for (int i = 0; i < diff; i++) {
							// oMantissa *= 10;
							oMantissa = multiply(oMantissa, 10);
						}
					}
					return (tMantissa == oMantissa);
				} catch (ArithmeticException e) {
					// overflow
					return false;
				}
			}

		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof FloatValue) {
			return _equals((FloatValue) o);
		} else {
			FloatValue fv = FloatValue.parse(o.toString());
			return fv == null ? false : _equals(fv);
		}
	}

	@Override
	public int hashCode() {
		// Long hashCode
		// return (int)(mantissa ^ (mantissa >>> 32)) ^ (int)(exponent ^
		// (exponent >>> 32));
		return mantissa.hashCode() ^ exponent.hashCode();
	}

}
