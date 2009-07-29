/*
 * Copyright (C) 2007-2009 Siemens AG
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

package com.siemens.ct.exi.util;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081010
 */

public class MethodsBag {

	/////////////////////////////////////////////////////////
	//
	// C O D I N G _ L E N G T H  Operations
	//
	/////////////////////////////////////////////////////////
	
	
	static final int[] smallLengths = new int[] { 0, 0, 1, 2, 2, 3, 3, 3, 3, 4,
			4, 4, 4, 4, 4, 4, 4 };

	/**
	 * Returns the least number of 7 bit-blocks that is needed to represent the
	 * int <param>n</param>. Returns 1 if <param>n</param> is 0.
	 * 
	 * @param n
	 *            integer value
	 * 
	 */
	public static int numberOf7BitBlocksToRepresent(final int n) {
		assert (n >= 0);

		// 7 bits
		if (n < 128) {
			return 1;
		}
		// 14 bits
		else if (n < 16384) {
			return 2;
		}
		// 21 bits
		else if (n < 2097152) {
			return 3;
		}
		// 28 bits
		else if (n < 268435456) {
			return 4;
		}
		// 35 bits
		else {
			// int, 32 bits
			return 5;
		}
	}

	/**
	 * Returns the least number of 7 bit-blocks that is needed to represent the
	 * long <param>l</param>. Returns 1 if <param>l</param> is 0.
	 * 
	 * @param l
	 *            long value
	 * 
	 */
	public static int numberOf7BitBlocksToRepresent(final long l) {
		if (l < 0xffffffff) {
			return numberOf7BitBlocksToRepresent((int) l);
		}
		// 35 bits
		else if (l < 0x800000000L) {
			return 5;
		}
		// 42 bits
		else if (l < 0x40000000000L) {
			return 6;
		}
		// 49 bits
		else if (l < 0x2000000000000L) {
			return 7;
		}
		// 56 bits
		else if (l < 0x100000000000000L) {
			return 8;
		}
		// 63 bits
		else if (l < 0x8000000000000000L) {
			return 9;
		}
		// 70 bits
		else {
			// long, 64 bits
			return 10;
		}
	}

	static final public int getCodingLength(final int characteristics) {
		if (characteristics < 17) {
			return smallLengths[characteristics];
		} else if (characteristics < 33) {
			// 17 .. 32
			return 5;
		} else if (characteristics < 65) {
			// 33 .. 64
			return 6;
		} else if (characteristics < 129) {
			// 65 .. 128
			return 7;
		} else {
			return (int) Math.ceil(Math.log((double) (characteristics))
					/ Math.log(2.0));
		}
	}
	
	/////////////////////////////////////////////////////////
	//
	// I N T E G E R  &  L O N G _ T O _ S T R I N G  Operations
	//
	/////////////////////////////////////////////////////////
	final static char[] INTEGER_MIN_VALUE_CHARARRAY = "-2147483648".toCharArray();
	final static char[] LONG_MIN_VALUE_CHARARRAY = "-9223372036854775808".toCharArray();
	
	
	final static char[] DigitOnes = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', };

	final static char[] DigitTens = { '0', '0', '0', '0', '0', '0', '0', '0',
			'0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2',
			'2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3',
			'3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4',
			'4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
			'6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7',
			'7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8',
			'8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9',
			'9', };

	/**
	 * All possible chars for representing a number as a String
	 */
	final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z' };

	final static int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
			99999999, 999999999, Integer.MAX_VALUE };

	// Requires positive x
	static int stringSize(int x) {
		for (int i = 0;; i++)
			if (x <= sizeTable[i])
				return i + 1;
	}
	
    // Requires positive x
    static int stringSize(long x) {
        long p = 10;
        for (int i=1; i<19; i++) {
            if (x < p)
                return i;
            p = 10*p;
        }
        return 19;
    }

	/**
	 * Places characters representing the integer i into the character array
	 * buf. The characters are placed into the buffer backwards starting with
	 * the least significant digit at the specified index (exclusive), and
	 * working backwards from there.
	 * 
	 * Will fail if i == Integer.MIN_VALUE
	 */
	static void getChars(int i, int index, char[] buf) {
		int q, r;
		int charPos = index;
		char sign = 0;

		if (i < 0) {
			sign = '-';
			i = -i;
		}

		// Generate two digits per iteration
		while (i >= 65536) {
			q = i / 100;
			// really: r = i - (q * 100);
			r = i - ((q << 6) + (q << 5) + (q << 2));
			i = q;
			buf[--charPos] = DigitOnes[r];
			buf[--charPos] = DigitTens[r];
		}

		// Fall thru to fast mode for smaller numbers
		// assert(i <= 65536, i);
		for (;;) {
			q = (i * 52429) >>> (16 + 3);
			r = i - ((q << 3) + (q << 1)); // r = i-(q*10) ...
			buf[--charPos] = digits[r];
			i = q;
			if (i == 0)
				break;
		}
		if (sign != 0) {
			buf[--charPos] = sign;
		}
	}

    /**
     * Places characters representing the integer i into the
     * character array buf. The characters are placed into
     * the buffer backwards starting with the least significant
     * digit at the specified index (exclusive), and working
     * backwards from there.
     *
     * Will fail if i == Long.MIN_VALUE
     */
    static void getChars(long i, int index, char[] buf) {
        long q;
        int r;
        int charPos = index;
        char sign = 0;

        if (i < 0) {
            sign = '-';
            i = -i;
        }

        // Get 2 digits/iteration using longs until quotient fits into an int
        while (i > Integer.MAX_VALUE) { 
            q = i / 100;
            // really: r = i - (q * 100);
            r = (int)(i - ((q << 6) + (q << 5) + (q << 2)));
            i = q;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }

        // Get 2 digits/iteration using ints
        int q2;
        int i2 = (int)i;
        while (i2 >= 65536) {
            q2 = i2 / 100;
            // really: r = i2 - (q * 100);
            r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
            i2 = q2;
            buf[--charPos] = DigitOnes[r];
            buf[--charPos] = DigitTens[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i2 <= 65536, i2);
        for (;;) {
            q2 = (i2 * 52429) >>> (16+3);
            r = i2 - ((q2 << 3) + (q2 << 1));  // r = i2-(q2*10) ...
            buf[--charPos] = digits[r];
            i2 = q2;
            if (i2 == 0) break;
        }
        if (sign != 0) {
            buf[--charPos] = sign;
        }
    }
	
	
	public static char[] itos(int i) {
		if (i == Integer.MIN_VALUE) {
			return INTEGER_MIN_VALUE_CHARARRAY;
		} else {
			int size = (i < 0) ? stringSize(-i) + 1 : stringSize(i);
			char[] buf = new char[size];
			getChars(i, size, buf);

			return buf;			
		}
	}
	
	public static char[] itos(long i) {
        if (i == Long.MIN_VALUE) {
            return LONG_MIN_VALUE_CHARARRAY;
        } else {
            int size = (i < 0) ? stringSize(-i) + 1 : stringSize(i);
            char[] buf = new char[size];
            getChars(i, size, buf);
            return buf;        	
        }
	}
	

}
