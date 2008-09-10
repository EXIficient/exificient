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

package com.siemens.ct.exi.datatype.decoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeRepresentation;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.PreReadByteDecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public class TypeDecoderDatatypeRepresentationMap extends AbstractTypeDecoder
{
	//	fallback type decoder
	private TypeDecoderTyped defaultDecoder; 
	
	private Map<QName, DatatypeDecoder> userDefinedDatatypeRepresentations;
	
	public TypeDecoderDatatypeRepresentationMap ( boolean isSchemaInformed )
	{
		super ( isSchemaInformed );
		
		defaultDecoder = new TypeDecoderTyped( isSchemaInformed );
		userDefinedDatatypeRepresentations = new HashMap<QName, DatatypeDecoder>();
	}
	public void registerDatatypeRepresentation( DatatypeRepresentation datatypeRepresentation )
	{
		//pluggableCodecs.put ( datatypeIdentifier, datatypeDecoder );
		userDefinedDatatypeRepresentations.put ( datatypeRepresentation.getQName ( ), datatypeRepresentation );
	}

	public String decodeValue ( Datatype datatype, DecoderChannel dc,  final String namespaceURI, final String localName  )
			throws IOException
	{
		//System.out.println ( datatype.getDatatypeIdentifier ( ) );
		
		if ( userDefinedDatatypeRepresentations.containsKey ( datatype.getDatatypeIdentifier ( ) ) )
		{
			if ( dc instanceof PreReadByteDecoderChannel )
			{
				//	values decoded already (just return them)
				return ((PreReadByteDecoderChannel)dc).getNextValue ( );
			}
			else
			{
				// System.out.println ( "[DEC] Pluggable Codec in use!" );
				
				DatatypeDecoder dec = userDefinedDatatypeRepresentations.get ( datatype.getDatatypeIdentifier ( ) );
				return dec.decodeValue ( this, datatype, dc,  namespaceURI, localName  );				
			}

		}
		else
		{
			return defaultDecoder.decodeValue ( datatype, dc, namespaceURI, localName  );
		}
	}

}
