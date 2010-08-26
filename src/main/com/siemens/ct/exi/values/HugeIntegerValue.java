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

package com.siemens.ct.exi.values;

import java.math.BigInteger;

import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public final class HugeIntegerValue extends AbstractIntegerValue implements
		Comparable<HugeIntegerValue> {

	private static final long serialVersionUID = 4938301238683583609L;

	public static final HugeIntegerValue ZERO = new HugeIntegerValue(0l);

	public final boolean isLongValue;
	public final long longValue;
	public final BigInteger bigIntegerValue;

	public HugeIntegerValue(long l) {
		this.longValue = l;
		this.bigIntegerValue = null;
		this.isLongValue = true;
	}

	public HugeIntegerValue(BigInteger bi) {
		this.bigIntegerValue = bi;
		this.longValue = -1;
		this.isLongValue = false;
	}

	public boolean isPositive() {
		if (isLongValue) {
			return (longValue >= 0);
		} else {
			return (bigIntegerValue.signum() != -1);
		}
	}

	public BigInteger toBigInteger() {
		if (isLongValue) {
			return BigInteger.valueOf(longValue);
		} else {
			return bigIntegerValue;
		}
	}
	
	public int toInteger() {
		if (isLongValue) {
			return (int)longValue;
		} else {
			return bigIntegerValue.intValue();
		}
	}

	public static HugeIntegerValue parse(String val) {
		try {
			// trim leading and trailing whitespaces
			val = getAdjustedValue(val);
			// try long
			long sl = Long.parseLong(val);
			return new HugeIntegerValue(sl);
		} catch (NumberFormatException e) {
			try {
				// try big integer
				BigInteger sbi = new BigInteger(val);
				return new HugeIntegerValue(sbi);
			} catch (Exception e1) {
				// no integer value at all
				return null;
			}
		}
	}
	
	public static HugeIntegerValue parse(BigInteger bi) {
		if (bi.bitLength() <= 63) {
			// fits into long
			return new HugeIntegerValue(bi.longValue());
		} else {
			// need to use BigInteger
			return new HugeIntegerValue(bi);
		}
	}
	
    /**
     * Returns a HugeIntegerValue whose value is <tt>(this + val)</tt>.
     *
     * @param  val value to be added to this HugeIntegerValue.
     * @return <tt>this + val</tt>
     */
    public HugeIntegerValue add(HugeIntegerValue val) {
    	if (isLongValue && val.isLongValue) {
    		return new HugeIntegerValue(longValue+val.longValue);
    	} else {
    		return new HugeIntegerValue(toBigInteger().add(val.toBigInteger()));
    	}
    }
	
    /**
     * Returns a HugeIntegerValue whose value is <tt>(this - val)</tt>.
     *
     * @param  val value to be subtracted from this HugeIntegerValue.
     * @return <tt>this - val</tt>
     */
    public HugeIntegerValue subtract(HugeIntegerValue val) {
    	if (isLongValue && val.isLongValue) {
    		return new HugeIntegerValue(longValue-val.longValue);
    	} else {
    		return new HugeIntegerValue(toBigInteger().subtract(val.toBigInteger()));
    	}
    }

	public int getCharactersLength() {
		if (slen == -1) {
			slen = isLongValue ? MethodsBag.getStringSize(longValue)
					: bigIntegerValue.toString().length();
		}
		return slen;
	}

	public char[] toCharacters(char[] cbuffer, int offset) {
		if (isLongValue) {
			MethodsBag.itos(longValue, getCharactersLength() + offset, cbuffer);
		} else {
			// TODO look for a more suitable way, big integer
			char[] bi = bigIntegerValue.toString().toCharArray();
			System.arraycopy(bi, 0, cbuffer, offset, bi.length);
		}

		return cbuffer;
	}

	public void toCharactersReverse(char[] cbuffer, int offset) {
		if (isLongValue) {
			MethodsBag.itosReverse(longValue, offset, cbuffer);
		} else {
			// TODO look for a more suitable way, big integer
			StringBuilder sb = new StringBuilder(bigIntegerValue.toString());
			char[] bi = sb.reverse().toString().toCharArray();
			System.arraycopy(bi, 0, cbuffer, offset, bi.length);
		}
	}

	@Override
	public String toString() {
		if (isLongValue) {
			return super.toString();
		} else {
			return bigIntegerValue.toString();
		}
	}

	@Override
	public String toString(char[] cbuffer, int offset) {
		if (isLongValue) {
			return super.toString(cbuffer, offset);
		} else {
			return bigIntegerValue.toString();
		}
	}

	public int compareTo(HugeIntegerValue o) {
		// IF both long do long comparison
		if (isLongValue && o.isLongValue) {
			long anotherVal = o.longValue;
			return (longValue < anotherVal ? -1 : (longValue == anotherVal ? 0
					: 1));
		} else {
			return toBigInteger().compareTo(o.toBigInteger());
		}
	}
	
	protected final boolean _equals(HugeIntegerValue o) {
		if (isLongValue && o.isLongValue) {
			return (longValue == o.longValue);
		} else {
			return toBigInteger().equals(o.toBigInteger());
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof HugeIntegerValue) {
			return _equals((HugeIntegerValue)o);
		} else if (o instanceof String ) {
			HugeIntegerValue hi = HugeIntegerValue.parse((String) o);
			if (hi == null) {
				return false;
			} else {
				return _equals(hi);	
			}
		} else {
			return false;	
		}
	}

}
