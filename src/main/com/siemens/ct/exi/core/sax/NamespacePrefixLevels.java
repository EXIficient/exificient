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

package com.siemens.ct.exi.core.sax;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.XMLConstants;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081014
 */

public class NamespacePrefixLevels
{
	private static final String NAMESPACE_PFX 	= "ns";
	
	//protected UnsynchronizedStack<PrefixMapping> prefixLevels;
	protected Stack<PrefixMapping> prefixLevels;
	
	int pfxCount = 0;
	
	public NamespacePrefixLevels()
	{
		//prefixLevels = new UnsynchronizedStack<PrefixMapping>();
		prefixLevels = new Stack<PrefixMapping>();
		
		//	default pefixes
		this.addLevel ( );
		
		//	special prefixes, etc "http://www.w3.org/XML/1998/namespace" --> xml
		addPrefix( XMLConstants.XML_NS_URI , XMLConstants.XML_NS_PREFIX );
		addPrefix( XMLConstants.NULL_NS_URI , XMLConstants.DEFAULT_NS_PREFIX );
	}
	
	public void clear()
	{
		assert( prefixLevels != null );
		
		prefixLevels.clear ( );
	}
	
	public void addLevel()
	{
		prefixLevels.push ( new PrefixMapping() );	//	new element
	}
	
	public void removeLevel()
	{
		prefixLevels.pop( );
	}
	
	public PrefixMapping getCurrentMapping()
	{
		return prefixLevels.peek ( );
	}
	
	//	given prefix
	public void addPrefix( final String uri, final String pfx )
	{
		assert( prefixLevels != null && ! prefixLevels.isEmpty ( ) );
		
		prefixLevels.peek ( ).addPrefix ( uri, pfx );
		
		pfxCount++;
	}

	//	other *unique* pfx
	public void createPrefix( final String uri )
	{
		addPrefix ( uri, NAMESPACE_PFX + pfxCount );
	}
	
	
	public boolean hasPrefixForURI( String uri )
	{
		//	from inner element to outer
		for ( int i = ( prefixLevels.size ( ) - 1 ); i >= 0; i-- )
		{
			PrefixMapping levelMapping = prefixLevels.get ( i );
			if ( levelMapping.containsPrefix ( uri ) )
			{
				return true;
			}
		}
		
		
		return false;
	}
	
	public String getPrefix( final String uri )
	{
		//	from inner element to outer
		for ( int i = ( prefixLevels.size ( ) - 1 ); i >= 0; i-- )
		{
			PrefixMapping levelMapping = prefixLevels.get ( i );
			if ( levelMapping.containsPrefix ( uri ) )
			{
				return levelMapping.getPrefix ( uri );
			}
		}
		
		return null;
	}

}


class PrefixMapping
{
	protected Map<String, String> mapping;
	
	public PrefixMapping ()
	{
		mapping = new HashMap<String, String>();
	}
	
	public void addPrefix( final String uri, final String pfx )
	{
		mapping.put ( uri, pfx );
	}
	
	public boolean containsPrefix( final String uri )
	{
		return mapping.containsKey ( uri );
	}
	
	public String getPrefix( final String uri )
	{
		return mapping.get ( uri );
	}
	
	
}