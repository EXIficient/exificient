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

package com.siemens.ct.exi.datatype.stringtable;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081009
 */

public class StringTablePartitionDecoderImpl implements
		StringTablePartitionDecoder {
	/**
	 * The contents of the table.
	 */
	protected List<String> valueList;

	/**
	 * Create a new string table.
	 */
	public StringTablePartitionDecoderImpl() {
		valueList = new ArrayList<String>();
	}

	/**
	 * Add a record to the table
	 * 
	 * @param value
	 *            - value to insert.
	 */
	public void add(final String value) {
		valueList.add(value);
	}

	/**
	 * Get the current size (number of strings) in the table.
	 */
	public int getSize() {
		return valueList.size();
	}

	/**
	 * Get string at given index.
	 */
	public String getValue(int index) {
		return valueList.get(index);
	}
}
