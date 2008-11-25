/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.datatype.stringtable;

import java.util.HashMap;
import java.util.Map;

import com.siemens.ct.exi.Constants;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

public class StringTablePartitionEncoderImpl implements
		StringTablePartitionEncoder {
	/**
	 * The contents of the table.
	 */
	protected Map<String, Integer> hmIndex;

	/**
	 * Create a new string table.
	 */
	public StringTablePartitionEncoderImpl() {
		hmIndex = new HashMap<String, Integer>();
	}

	/**
	 * Add a record to the table
	 * 
	 * @param value
	 *            - value to insert.
	 */
	public void add(String value) {
		// TODO autoboxing slows down ?
		hmIndex.put(value, hmIndex.size());
	}

	/**
	 * Get the current size (number of strings) in the table.
	 */
	public int getSize() {
		return hmIndex.size();
	}

	/**
	 * Get index of given string value in table.
	 * 
	 * @return Index of string in the array or -1 if not found.
	 */
	public int getIndex(final String value) {
		Integer index;
		return ((index = hmIndex.get(value)) == null ? Constants.NOT_FOUND
				: index);
	}

}
