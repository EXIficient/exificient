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

package com.siemens.ct.exi.datatype.encoder;

import java.io.IOException;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.stringtable.StringTableEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class TypeEncoderTyped extends AbstractTypeEncoder
{
	private DatatypeEncoder					lastDatatypeEncoder;

	private BinaryDatatypeEncoder			binaryDTE;
	private BooleanDatatypeEncoder			booleanDTE;
	private DecimalDatatypeEncoder			decimalDTE;
	private FloatDatatypeEncoder			floatDTE;
	private IntegerDatatypeEncoder			integerDTE;
	private UnsignedIntegerDatatypeEncoder	unsignedIntegerDTE;
	private DatetimeDatatypeEncoder			datetimeDTE;
	private EnumerationDatatypeEncoder		enumerationDTE;
	private ListDatatypeEncoder				listDTE;
	private StringDatatypeEncoder			stringDTE;
	
	//public TypeEncoderTyped( boolean isSchemaInformed )
	public TypeEncoderTyped( EXIFactory exiFactory )
	{
		super( exiFactory );
		
		binaryDTE			= new BinaryDatatypeEncoder ( this );
		booleanDTE			= new BooleanDatatypeEncoder ( this );
		decimalDTE			= new DecimalDatatypeEncoder ( this );
		floatDTE			= new FloatDatatypeEncoder ( this );
		integerDTE			= new IntegerDatatypeEncoder ( this );
		unsignedIntegerDTE	= new UnsignedIntegerDatatypeEncoder ( this );
		datetimeDTE			= new DatetimeDatatypeEncoder ( this );
		enumerationDTE		= new EnumerationDatatypeEncoder ( this );
		listDTE				= new ListDatatypeEncoder ( this );
		stringDTE			= new StringDatatypeEncoder ( this );
	}
	
	public TypeEncoderTyped( EXIFactory exiFactory, StringTableEncoder stringTable )
	{
		//	typed encoder needs to be schemaInformed
		this( exiFactory );
		
		this.stringTable = stringTable;
	}
	
	public boolean isTypeValid ( Datatype datatype, String value )
	{
		switch ( datatype.getDefaultBuiltInType ( ) )
		{
			case BUILTIN_BINARY:
				lastDatatypeEncoder = binaryDTE;
				break;
			case BUILTIN_BOOLEAN:
				lastDatatypeEncoder = booleanDTE;
				break;
			case BUILTIN_DECIMAL:
				lastDatatypeEncoder = decimalDTE;
				break;
			case BUILTIN_FLOAT:
				lastDatatypeEncoder = floatDTE;
				break;
			case BUILTIN_INTEGER:
				lastDatatypeEncoder = integerDTE;
				break;
			case BUILTIN_UNSIGNED_INTEGER:
				lastDatatypeEncoder = unsignedIntegerDTE;
				break;
			case BUILTIN_DATETIME:
				lastDatatypeEncoder = datetimeDTE;
				break;
			case BUILTIN_ENUMERATION:
				lastDatatypeEncoder = enumerationDTE;
				break;
			case BUILTIN_LIST:
				lastDatatypeEncoder = listDTE;
				break;
			case BUILTIN_STRING:
				 lastDatatypeEncoder = stringDTE;
				break;
			default:
				throw new RuntimeException ( "Unknown BuiltIn Type" );
		}
		
		return lastDatatypeEncoder.isValid ( datatype, value );	
	}

	

	// first isValueTypeValid has to be called
	public void writeTypeValidValue ( EncoderChannel valueChannel, String uri, String localName ) throws IOException
	{
		lastDatatypeEncoder.writeValue ( valueChannel, uri, localName );
	}


}
