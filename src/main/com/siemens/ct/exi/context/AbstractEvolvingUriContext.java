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

package com.siemens.ct.exi.context;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

public abstract class AbstractEvolvingUriContext extends AbstractUriContext
		implements EvolvingUriContext {

	// runtime local-names
	List<QNameContext> runtimeQNames;

	// runtime prefixes
	List<String> runtimePrefixes;

	public AbstractEvolvingUriContext(int namespaceUriID, String namespaceUri) {
		super(namespaceUriID, namespaceUri);

		this.runtimeQNames = new ArrayList<QNameContext>();
		this.runtimePrefixes = new ArrayList<String>();
	}

	public void clear() {
		// runtime entries
		runtimeQNames.clear();
		runtimePrefixes.clear();
	}

	public QNameContext addQNameContext(String localName, int qNameID) {
		QNameContext rlnc = new QNameContext(namespaceUriID,
				this.getNumberOfQNames(), new QName(getNamespaceUri(),
						localName), qNameID);
		runtimeQNames.add(rlnc);
		return rlnc;
	}

	protected void addQNameContext(QNameContext qnc) {
		runtimeQNames.add(qnc);
	}

	public int addPrefix(String prefix) {
		int prefixID = this.getNumberOfPrefixes();
		runtimePrefixes.add(prefix);
		return prefixID;
	}

}
