/*
 * Copyright (C) 2007-2014 Siemens AG
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
 * @version 0.9.3-SNAPSHOT
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
