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

import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080808
 */

public class ElementKey
//implements HierarchicalKey<ExpandedName>
{
	protected ExpandedName name;
	protected ExpandedName scope;
	protected ExpandedName scopeType;

	public ElementKey( ExpandedName name )
	{
		this.name = name;
	}
	
	public ElementKey( ExpandedName name, ExpandedName scope, ExpandedName scopeType )
	{
		this( name );
		this.scope = scope;
		this.scopeType = scopeType;
	}
	
	public ExpandedName getName ()
	{
		return name;
	}

	public void setName ( ExpandedName name )
	{
		this.name = name;
	}

	public ExpandedName getScope ()
	{
		return scope;
	}

	public void setScope ( ExpandedName scope )
	{
		this.scope = scope;
	}

	public ExpandedName getScopeType ()
	{
		return scopeType;
	}

	public void setScopeType ( ExpandedName type )
	{
		this.scopeType = type;
	}
	
	public String toString()
	{
		String s = name.toString();
		
		if ( scope != null )
		{
			s += "[" + scope + "]";
		}
		if ( scopeType != null )
		{
			s += "{" + scopeType + "}";
		}
		
		return s;
	}
	
	public final int hashCode ()
	{
		//	simple hashCode
		return name.hashCode();
	}
	
	public boolean equals ( Object o )
	{
		if ( o instanceof ElementKey )
		{
			ElementKey otherES = ( (ElementKey) o );
			
			//	name 
			if ( name.equals( otherES.getName( ) ) )
			{
				if ( scope == null && scopeType == null )
				{
					return ( otherES.getScope( ) == null && otherES.getScopeType( ) == null );
				}
				else if ( scope == null )
				{
					return ( scopeType.equals ( otherES.getScopeType( ) ) );
				}
				else
				{
					//	scopeType == null
					return ( scope.equals ( otherES.getScope( ) ) );
				}
			}
		}
		
		return false;
	}

//	/*
//	 * (non-Javadoc)
//	 * @see com.siemens.ct.exi.grammar.rule.container2.HierarchicalListEntry#getHierarchyIdentifier(int)
//	 */
//	public ExpandedName getKey ( int hierarchyLevel )
//	{
//		switch( hierarchyLevel )
//		{
//			case 0:
//				return name;
//			case 1:
//				return scope;
//			case 2:
//				return scopeType;
//			default:
//				return null;
//		}
//	}
//
//	public int getHierarchyLevels ()
//	{
//		int levels = 1;	//	name
//		
//		if ( scope != null )
//		{
//			levels++;
//			
//			if ( scopeType != null )
//			{
//				levels++;
//			}
//		}
//		
//		return levels;
//	}
}
