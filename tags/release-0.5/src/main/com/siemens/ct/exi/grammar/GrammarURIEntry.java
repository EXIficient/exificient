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

package com.siemens.ct.exi.grammar;

public class GrammarURIEntry {
	
	public final String uri;
	public final String[] localNames;
	public final String[] prefixes;
	
	public GrammarURIEntry(String uri, String[] localNames, String[] prefixes) {
		assert(uri != null);
		assert(localNames != null);
		assert(prefixes != null);
		this.uri = uri;
		this.localNames = localNames;
		this.prefixes = prefixes;
	}
	
	@Override
	public String toString() {
		return "{" + uri + "}(localNames#" + localNames.length + ", prefixes#" + prefixes.length +")";
	}

}