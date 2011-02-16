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

package com.siemens.ct.exi.datatype.charset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Utility class to build a RestrictedCharacterSet by a given Set.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class CodePointCharacterSet extends AbstractRestrictedCharacterSet {
	
	private static final long serialVersionUID = -1326543125848570370L;

	public CodePointCharacterSet(Set<Integer> codePoints) {
		super();
		//	sort code-points by UCS
		List<Integer> sortedCodePoints = new ArrayList<Integer>();
		sortedCodePoints.addAll(codePoints);
		Collections.sort(sortedCodePoints);
		
		// iterate over characters
		Iterator<Integer> iter = sortedCodePoints.iterator();
		while(iter.hasNext()) {
			addValue(iter.next());
		}
	}
}
