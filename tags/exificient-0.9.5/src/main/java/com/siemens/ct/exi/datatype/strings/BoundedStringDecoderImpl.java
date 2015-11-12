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

package com.siemens.ct.exi.datatype.strings;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

public class BoundedStringDecoderImpl extends StringDecoderImpl {

	/* maximum string length of value content items */
	protected final int valueMaxLength;

	/* maximum number of value content items in the string table */
	protected final int valuePartitionCapacity;

	/* global ID */
	protected int globalID;

	/* localID mapping, globalID index --> localID & values (in given context) */
	protected LocalIDMap[] localIdMapping;

	static class LocalIDMap {
		final int localID;
		final QNameContext context;

		public LocalIDMap(int localID, QNameContext context) {
			this.localID = localID;
			this.context = context;
		}
	}

	public BoundedStringDecoderImpl(boolean localValuePartitions,
			int valueMaxLength, int valuePartitionCapacity) {
		super(localValuePartitions);
		this.valueMaxLength = valueMaxLength;
		this.valuePartitionCapacity = valuePartitionCapacity;

		this.globalID = -1;
		if (valuePartitionCapacity > 0 && localValuePartitions) {
			localIdMapping = new LocalIDMap[valuePartitionCapacity];
		}
	}

	@Override
	public void addValue(QNameContext context,
			StringValue value) {
		// first: check "valueMaxLength"
		if (valueMaxLength < 0 || value.getCharactersLength() <= valueMaxLength) {
			// next: check "valuePartitionCapacity"
			if (valuePartitionCapacity < 0) {
				// no "valuePartitionCapacity" restriction
				super.addValue(context, value);
			} else
			// If valuePartitionCapacity is not zero the string S is added
			if (valuePartitionCapacity == 0) {
				// no values per partition
			} else {
				/*
				 * When S is added to the global value partition and there was
				 * already a string V in the global value partition associated
				 * with the compact identifier globalID, the string S replaces
				 * the string V in the global table, and the string V is removed
				 * from its associated local value partition by rendering its
				 * compact identifier permanently unassigned.
				 */
				assert (!globalValues.contains(value));

				/*
				 * When the string value is added to the global value partition,
				 * the value of globalID is incremented by one (1). If the
				 * resulting value of globalID is equal to
				 * valuePartitionCapacity, its value is reset to zero (0)
				 */
				if ((++globalID) == valuePartitionCapacity) {
					globalID = 0;
				}

				if (globalValues.size() > globalID) {
					Value prev = globalValues.set(globalID, value);
					if (prev != null) {
						// free memory
						if(localValuePartitions) {
							LocalIDMap lvsFree = localIdMapping[globalID];
							assert (lvsFree != null);	
						}
					}
				} else {
					assert (!globalValues.contains(value));
					globalValues.add(value);
				}

				if(localValuePartitions) {
					// update local ID mapping
					localIdMapping[globalID] = new LocalIDMap(getNumberOfStringValues(context), context);

					// local value
					addLocalValue(context, value);					
				}
			}
		}
	}

	@Override
	public void clear() {
		super.clear();
		globalID = -1;
	}

}
