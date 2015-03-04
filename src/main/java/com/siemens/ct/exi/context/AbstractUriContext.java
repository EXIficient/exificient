/*
 * Copyright (C) 2007-2015 Siemens AG
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

import java.io.Serializable;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public abstract class AbstractUriContext implements UriContext, Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -228310211827981382L;
	
	// namespace
	final int namespaceUriID;
	final String namespaceUri;

	public AbstractUriContext(int namespaceUriID, String namespaceUri) {
		this.namespaceUriID = namespaceUriID;
		this.namespaceUri = namespaceUri;
	}

	public final int getNamespaceUriID() {
		return namespaceUriID;
	}

	public final String getNamespaceUri() {
		return namespaceUri;
	}

	@Override
	public final boolean equals(Object o) {
		if (o instanceof AbstractUriContext) {
			return ((AbstractUriContext) o).namespaceUriID == this.namespaceUriID;
		}
		return false;
	}

	public String toString() {
		return namespaceUriID + "," + namespaceUri;
	}

}
