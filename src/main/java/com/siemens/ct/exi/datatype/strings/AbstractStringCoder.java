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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.values.StringValue;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public abstract class AbstractStringCoder implements StringCoder {

	// indicate whether local value partitions are used
	protected final boolean localValuePartitions;
	
	// strings (local)
	protected Map<QNameContext, List<StringValue>> localValues;
	
	public AbstractStringCoder(boolean localValuePartitions, int initialQNameLists) {
		this.localValuePartitions = localValuePartitions;
		localValues = new HashMap<QNameContext, List<StringValue>>(initialQNameLists);
	}
	
	public int getNumberOfStringValues(QNameContext qnc) {
		int n = 0;
		List<StringValue> lvs = localValues.get(qnc);
		if(lvs != null) {
			n = lvs.size();
		}
		
		return n;
	}
	
	protected void addLocalValue(QNameContext qnc, StringValue value) {
		if(localValuePartitions) {
			List<StringValue> lvs = this.localValues.get(qnc);
			if(lvs == null) {
				lvs = new ArrayList<StringValue>();
				localValues.put(qnc, lvs);
			}
			lvs.add(value);
		}
	}

	public void clear() {
		// local context
		if(localValuePartitions) {
			// free strings only, not destroy lists itself
			for(List<StringValue> lvs : this.localValues.values()) {
				lvs.clear();
			}
		}
	}

}
