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

package com.siemens.ct.exi.grammars.grammar;


/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */
/*
 * Document : SD DocContent 0
 */
public class Document extends AbstractSchemaInformedGrammar {

	private static final long serialVersionUID = 2859986001661016733L;

	public Document() {
		super();
	}

	public Document(String label) {
		this();
		this.setLabel(label);
	}

	public GrammarType getGrammarType() {
		return GrammarType.DOCUMENT;
	}
	
	public String toString() {
		return "Document" + super.toString();
	}

}
