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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.core.CompileConfiguration;
import com.siemens.ct.exi.datatype.encoder.TypeEncoder;
import com.siemens.ct.exi.io.channel.ByteEncoderChannelChannelized;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannelChannelized;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080718
 */

public abstract class AbstractEncoderByteBlockChannelized extends AbstractEncoderBlock
{
	// structure channel
	protected EncoderChannelChannelized										structureChannel;

	// valueChannels: uri -> ( localName + EncoderChannelChannelized )
	protected Map<String, Map<String, EncoderChannelChannelized>>	valueChannels;

	// order of channels in XML document
	protected List<String>												orderValueChannelUri;
	protected List<String>												orderValueChannelLocalName;

	public AbstractEncoderByteBlockChannelized ( OutputStream outputStream, TypeEncoder typeEncoder )
	{
		super ( outputStream, typeEncoder );
	}

	protected void init ()
	{
		structureChannel = createEncoderChannel ( );
		valueChannels = new HashMap<String, Map<String, EncoderChannelChannelized>> ( );
		orderValueChannelUri = new ArrayList<String> ( );
		orderValueChannelLocalName = new ArrayList<String> ( );
	}

	protected void addNewExpandedNameChannel ( String uri, String localName )
	{
		orderValueChannelUri.add ( uri );
		orderValueChannelLocalName.add ( localName );
	}

	private static EncoderChannelChannelized createEncoderChannel ()
	{
		return new ByteEncoderChannelChannelized ( );
	}

	protected EncoderChannel getStructureChannel ()
	{
		return structureChannel;
	}

	protected EncoderChannel getValueChannel ( String uri, String localName )
	{
		// fetch existing or create new channel

		// uri known ?
		if ( valueChannels.containsKey ( uri ) )
		{
			Map<String, EncoderChannelChannelized> valueChannelsLocalNamePerURI = valueChannels.get ( uri );

			// localName present in URI context ?
			if ( !valueChannelsLocalNamePerURI.containsKey ( localName ) )
			{
				// unknown localName -> create new channel
				valueChannelsLocalNamePerURI.put ( localName, createEncoderChannel ( ) );

				// memorize channel order
				addNewExpandedNameChannel ( uri, localName );
			}
			return valueChannelsLocalNamePerURI.get ( localName );
		}
		else
		{
			// neither uri nor localName present
			HashMap<String, EncoderChannelChannelized> valueChannelsLocalNamePerURI = new HashMap<String, EncoderChannelChannelized> ( );
			valueChannelsLocalNamePerURI.put ( localName, createEncoderChannel ( ) );
			valueChannels.put ( uri, valueChannelsLocalNamePerURI );

			// memorize channel order
			addNewExpandedNameChannel ( uri, localName );

			return valueChannelsLocalNamePerURI.get ( localName );
		}
	}

	protected abstract OutputStream getStream () throws IOException;

	protected abstract void finalizeStream () throws IOException;

	public void flush () throws IOException
	{
		assert ( orderValueChannelUri.size ( ) == orderValueChannelLocalName.size ( ) );
		/*
		 * If the block contains at most 100 values, the block will contain only
		 * 1 compressed stream containing the structure channel followed by all
		 * of the value channels. The order of the value channels within the
		 * compressed stream is defined by the order in which the first value in
		 * each channel occurs in the EXI event sequence.
		 */
		if ( getNumberOfBlockValues ( ) <= CompileConfiguration.MAX_NUMBER_OF_VALUES )
		{
			// write structure stream
			OutputStream singleStream = getStream ( );
			singleStream.write ( structureChannel.toByteArray ( ) );

			// write all value channels in the order they appear
			for ( int i = 0; i < orderValueChannelUri.size ( ); i++ )
			{
				singleStream.write ( valueChannels.get ( orderValueChannelUri.get ( i ) ).get (
						orderValueChannelLocalName.get ( i ) ).toByteArray ( ) );
			}

			// finalize
			finalizeStream ( );
		}
		/*
		 * If the block contains more than 100 values, the first compressed
		 * stream contains only the structure channel. The second compressed
		 * stream contains all value channels that contain less than 100 values.
		 * And the remaining compressed streams each contain only one channel,
		 * each having more than 100 values. The order of the value channels
		 * within the second compressed stream is defined by the order in which
		 * the first value in each channel occurs in the EXI event sequence.
		 * Similarly, the order of the compressed streams following the second
		 * compressed stream in the block is defined by the order in which the
		 * first value of the channel inside each compressed stream occurs in
		 * the EXI event sequence.
		 */
		else
		{
			// structure stream first (as a single stream)
			OutputStream structureStream = this.getStream ( );
			structureStream.write ( structureChannel.toByteArray ( ) );
			finalizeStream ( );

			// all value channels that contain less (and equal) than 100 values
			// (as a single stream )
			OutputStream lessEq100Stream = this.getStream ( );
			boolean wasThereLess100 = false;
			for ( int i = 0; i < orderValueChannelUri.size ( ); i++ )
			{
				EncoderChannelChannelized channelLEQ100 = valueChannels.get ( orderValueChannelUri.get ( i ) ).get (
						orderValueChannelLocalName.get ( i ) );
				if ( channelLEQ100.getNumberOfChannelValues ( ) <= CompileConfiguration.MAX_NUMBER_OF_VALUES )
				{
					// encode *content*
					lessEq100Stream.write ( channelLEQ100.toByteArray ( ) );
					wasThereLess100 = true;
					
//					System.out.println( "LEQ " +  orderValueChannelLocalName.get ( i ) + " --> " +channelLEQ100.getNumberOfChannelValues ( ) );
				}
			}
			// to avoid writing (deflate) header for no data
			if ( wasThereLess100 )
			{
				finalizeStream ( );
			}

			// all value channels having more than 100 values
			for ( int i = 0; i < orderValueChannelUri.size ( ); i++ )
			{
				EncoderChannelChannelized channelGre100 = valueChannels.get ( orderValueChannelUri.get ( i ) ).get (
						orderValueChannelLocalName.get ( i ) );
				if ( channelGre100.getNumberOfChannelValues ( ) > CompileConfiguration.MAX_NUMBER_OF_VALUES )
				{
					OutputStream great100Stream = this.getStream ( );

					great100Stream.write ( channelGre100.toByteArray ( ) );
					finalizeStream ( );
					
//					System.out.println( "GRE " +  orderValueChannelLocalName.get ( i ) + " --> " +channelGre100.getNumberOfChannelValues ( ) );
					
				}
			}
		}

		// finalizeDocument( );
		this.outputStream.flush ( );
	}
}
