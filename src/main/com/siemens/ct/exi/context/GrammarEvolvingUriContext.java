/*
 * Copyright (C) 2007-2012 Siemens AG
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

package com.siemens.ct.exi.context;

public class GrammarEvolvingUriContext extends RuntimeEvolvingUriContext {
	// static uri context
	final GrammarUriContext grammarUriContext;

	public GrammarEvolvingUriContext(GrammarUriContext grammarUriContext) {
		super(grammarUriContext.getNamespaceUriID(), grammarUriContext
				.getNamespaceUri());
		this.grammarUriContext = grammarUriContext;

		// qnames
		for (int i = 0; i < grammarUriContext.getNumberOfQNames(); i++) {
			QNameContext qnc = grammarUriContext.getQNameContext(i);
			this.addQNameContext(qnc);
		}

		// prefixes
		for (int i = 0; i < grammarUriContext.getNumberOfPrefixes(); i++) {
			String pfx = grammarUriContext.getPrefix(i);
			this.addPrefix(pfx);
		}
	}

	public void clear() {
		// remove newly added runtime entries
		while (runtimeQNames.size() > grammarUriContext.getNumberOfQNames()) {
			runtimeQNames.remove(runtimeQNames.size() - 1);
		}

		// clear runtime prefixes (leave grammar prefixes)
		while (runtimePrefixes.size() > grammarUriContext.getNumberOfPrefixes()) {
			runtimePrefixes.remove(runtimePrefixes.size() - 1);
		}

	}

}
