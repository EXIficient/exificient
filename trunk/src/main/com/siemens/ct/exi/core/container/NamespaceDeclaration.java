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

package com.siemens.ct.exi.core.container;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class NamespaceDeclaration {
	public final String namespaceURI;
	public final String prefix;

	public NamespaceDeclaration(String namespaceURI, String prefix) {
		this.namespaceURI = namespaceURI;
		this.prefix = prefix;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NamespaceDeclaration) {
			NamespaceDeclaration other = (NamespaceDeclaration) o;
			return (namespaceURI.equals(other.namespaceURI) && prefix
					.equals(other.prefix));
		}
		return false;
	}
}
