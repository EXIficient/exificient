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

package com.siemens.ct.exi.helpers;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.SchemaIdResolver;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;

/**
 * 
 * This is the default implementation of an <code>SchemaIdResolver</code> class.
 * 
 * <p>SchemaId is interpreted as file location</p>
 * 
 * @see EXIFactory
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.3
 */

public class DefaultSchemaIdResolver implements SchemaIdResolver {
	
	protected GrammarFactory gf;
	
	public DefaultSchemaIdResolver() {
		
	}
	
	protected GrammarFactory getGrammarFactory() {
		if (gf == null) {
			gf = GrammarFactory.newInstance();
		}
		return gf;
	}
	
	public Grammars resolveSchemaId(String schemaId) throws EXIException {
		if (schemaId == null) {
			return getGrammarFactory().createSchemaLessGrammars();
		} else if ("".equals(schemaId)) {
			return getGrammarFactory().createXSDTypesOnlyGrammars();
		} else {
			// interpret schemaId as location
			try {
				return getGrammarFactory().createGrammars(schemaId);
			} catch (Exception e) {
				throw new EXIException(this.getClass().getName() + " failed to retrieve schemaId == " + schemaId, e);
			}
		}
	}
	
}
