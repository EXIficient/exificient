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

package com.siemens.ct.exi;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public abstract class AbstractTestCoder
{
	protected static EXIFactory getQuickTestEXIactory() throws Exception
	{
		return getQuickTestEXIactory( QuickTestConfiguration.getXsdLocation ( ) );
	}
	
	protected static EXIFactory getQuickTestEXIactory( String xsdLocation ) throws Exception
	{
		EXIFactory ef = DefaultEXIFactory.newInstance ( );
		ef.setCodingMode ( QuickTestConfiguration.CODING_MODE );
		ef.setFidelityOptions ( QuickTestConfiguration.fidelityOptions );
		ef.setFragment ( QuickTestConfiguration.FRAGMENTS );
		Grammar grammar;
		GrammarFactory grammarFactory = GrammarFactory.newInstance ( );
		if ( QuickTestConfiguration.USE_SCHEMA )
		{
			grammar = grammarFactory.createGrammar ( xsdLocation );	
		}
		else
		{
			grammar = grammarFactory.createSchemaLessGrammar( );	
		}
		ef.setGrammar ( grammar );
		
		return ef;
	}
}
