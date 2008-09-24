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

package com.siemens.ct.exi.data;


import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.FidelityOptions;

public class TestCaseOption
{
	private CodingMode codingMode;
	private FidelityOptions fidelityOptions;
	private boolean fragments;
	private String schemaLocation;
	private boolean xmlEqual;
	private boolean schemaInformedOnly;
	
	public CodingMode getCodingMode ()
	{
		return codingMode;
	}
	public void setCodingMode ( CodingMode codingMode )
	{
		this.codingMode = codingMode;
	}
	public FidelityOptions getFidelityOptions ()
	{
		return fidelityOptions;
	}
	public void setFidelityOptions ( FidelityOptions fidelityOptions )
	{
		this.fidelityOptions = fidelityOptions;
	}
	
	public boolean isFragments ()
	{
		return fragments;
	}
	public void setFragments ( boolean fragments )
	{
		this.fragments = fragments;
	}
	public String getSchemaLocation ()
	{
		return schemaLocation;
	}
	public void setSchemaLocation ( String schemaLocation )
	{
		this.schemaLocation = schemaLocation;
	}
	public boolean isXmlEqual ()
	{
		return xmlEqual;
	}
	public void setXmlEqual ( boolean xmlEqual )
	{
		this.xmlEqual = xmlEqual;
	}
	public boolean isSchemaInformedOnly ()
	{
		return schemaInformedOnly;
	}
	public void setSchemaInformedOnly ( boolean schemaInformedOnly )
	{
		this.schemaInformedOnly = schemaInformedOnly;
	}
	
	@Override
	public String toString()
	{
		String s = new String();
		//	schemaLocation ?
		if ( schemaLocation == null )
		{
			s += "noSchema, ";
		}
		else
		{
			s += "schema=" + schemaLocation +", ";
		}
		//	coding mode
		s += "codingMode=" + codingMode +", ";
		//	fidelityOptions
		s += "fidelityOptions=" + getFidelityOptions ( ).toString ( );

		return s;
	}
	
}
