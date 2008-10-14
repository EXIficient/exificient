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

package com.siemens.ct.exi.io.block;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.core.CompileConfiguration;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.decoder.TypeDecoder;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.PreReadByteDecoderChannel;
import com.siemens.ct.exi.util.ExpandedName;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081014
 */

public abstract class AbstractDecoderByteBlockChannelized extends AbstractDecoderBlock
{	
	protected ByteDecoderChannel					structureChannel;

	// valueChannels: uri -> ( localName + EncoderChannelChannelized )
	protected Map<String, Map<String, DecoderChannel>>	valueChannels;

	protected abstract InputStream getStream () throws IOException;
	
	protected abstract ByteDecoderChannel getNextChannel( ) throws IOException;

	
	public AbstractDecoderByteBlockChannelized ( InputStream is, TypeDecoder typeDecoder  ) throws IOException
	{
		super( is, typeDecoder );
		
		valueChannels = new HashMap<String, Map<String, DecoderChannel>>();
	}

	private void addDecoderChannel( DecoderChannel dc, String uri, String localName )
	{
		if ( ! valueChannels.containsKey ( uri ))
		{
			valueChannels.put ( uri, new HashMap<String, DecoderChannel>() );
			
		}
		valueChannels.get ( uri ).put ( localName, dc );
	}
	

	public void reconstructChannels ( int values, 
			List<ExpandedName> valueQNames, Map<ExpandedName, List<Datatype>> dataTypes,
			Map<ExpandedName, Integer> occurrences ) throws IOException
	{
		
		if ( values <= CompileConfiguration.MAX_NUMBER_OF_VALUES )
		{
			// single compressed stream (incl. structure)
			for ( int i = 0; i < valueQNames.size ( ); i++ )
			{
				ExpandedName qname = valueQNames.get ( i );
				
				PreReadByteDecoderChannel preDC = 
					new PreReadByteDecoderChannel( typeDecoder, structureChannel, qname, dataTypes.get ( qname ), occurrences.get ( qname )  );
				
				//valueChannels.put ( qname, preDC );
				addDecoderChannel( preDC, qname.getNamespaceURI ( ), qname.getLocalName ( ) );
			}
		}
		else
		{
			// first stream structure (already read)
			ByteDecoderChannel bdcLess100 = null;

			// second stream (if any), values <= 100
			if ( areThereAnyLessThan100 ( valueQNames, occurrences ) )
			{
				bdcLess100 = getNextChannel ( );
			}
			for ( int i = 0; i < valueQNames.size ( ); i++ )
			{
				ExpandedName qname = valueQNames.get ( i );
				
				if ( occurrences.get ( qname ) <= CompileConfiguration.MAX_NUMBER_OF_VALUES )
				{
					// System.out.println ( qname + " <100: " + occurrences.get ( qname ) );
					
					//PreReadByteDecoderChannel preDC = new PreReadByteDecoderChannel( decoder, bdcLess100, qname, dataTypes.get ( qname ), occurrences.get ( qname )  );
					PreReadByteDecoderChannel preDC = new PreReadByteDecoderChannel( typeDecoder, bdcLess100, qname, dataTypes.get ( qname ), occurrences.get ( qname )  );
					
					
					//valueChannels.put ( qname, preDC );
					addDecoderChannel( preDC, qname.getNamespaceURI ( ), qname.getLocalName ( ) );
				}
			}
			
			//	proper stream for greater100
			for ( int i = 0; i < valueQNames.size ( ); i++ )
			{
				ExpandedName qname = valueQNames.get ( i );
				
				if ( occurrences.get ( qname ) > CompileConfiguration.MAX_NUMBER_OF_VALUES )
				{
					// System.out.println ( qname + ">100: " + occurrences.get ( qname ) );
					
					//PreReadByteDecoderChannel preDC = new PreReadByteDecoderChannel( decoder, getNextChannel( ), qname, dataTypes.get ( qname ), occurrences.get ( qname )  );
					PreReadByteDecoderChannel preDC = new PreReadByteDecoderChannel( typeDecoder, getNextChannel( ), qname, dataTypes.get ( qname ), occurrences.get ( qname )  );

					
					//valueChannels.put ( qname, preDC );
					addDecoderChannel( preDC, qname.getNamespaceURI ( ), qname.getLocalName ( ) );
				}
			}
		}
	}

	private static boolean areThereAnyLessThan100 ( List<ExpandedName> qnames,
			Map<ExpandedName, Integer> occurrences )
	{
		for ( int i = 0; i < qnames.size ( ); i++ )
		{
			if ( occurrences.get ( qnames.get ( i ) ).intValue ( ) <= CompileConfiguration.MAX_NUMBER_OF_VALUES )
			{
				return true;
			}
		}
		return false;
	}

	public DecoderChannel getStructureChannel ()
	{
		return structureChannel;
	}

	public DecoderChannel getValueChannel ( String namespaceURI, String localName  ) throws IOException
	{
		return valueChannels.get ( namespaceURI ).get ( localName );
	}
}
