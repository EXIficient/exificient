/*
 * Copyright (C) 2007, 2008 Siemens AG
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

import com.siemens.ct.exi.datatype.stringtable.StringTableCommon;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081014
 */

public abstract class AbstractGrammar implements Grammar {
	/*
	 * Built-in Grammars
	 */
	protected Rule builtInDocumentGrammar;
	protected Rule builtInDocContentGrammar;
	protected Rule builtInDocEndGrammar;

	// sorted URIs(pre-initializing URI partition)
	protected String[] uris;

	// sorted LocalNames (pre-initializing LocalName Partition)
	protected ExpandedName[] localNames;

	private final boolean isSchemaInformed;

	public AbstractGrammar(boolean isSchemaInformed) {
		this.isSchemaInformed = isSchemaInformed;

		uris = new String[0];
		localNames = new ExpandedName[0];
	}

	public boolean isSchemaInformed() {
		return isSchemaInformed;
	}

	public Rule getBuiltInDocumentGrammar() {
		return builtInDocumentGrammar;
	}

	public void populateStringTable(StringTableCommon stringTable) {
		/*
		 * When a schema is provided, the uri partition is also pre-populated
		 * with the name of each namespace URI declared in the schema, appended
		 * in lexicographical order.
		 */
		for (int i = 0; i < uris.length; i++) {
			stringTable.addURI(uris[i]);
		}

		/*
		 * When a schema is provided, the string table (Local-name) is also
		 * pre-populated with the local name of each attribute, element and type
		 * declared in the schema, partitioned by namespace URI and sorted
		 * lexicographically.
		 */
		for (int i = 0; i < localNames.length; i++) {
			stringTable.addLocalName(localNames[i].getNamespaceURI(),
					localNames[i].getLocalName());
		}

	}

}
