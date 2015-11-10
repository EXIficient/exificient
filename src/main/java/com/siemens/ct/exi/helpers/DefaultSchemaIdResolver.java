/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
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
 * @version 0.9.5-SNAPSHOT
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
