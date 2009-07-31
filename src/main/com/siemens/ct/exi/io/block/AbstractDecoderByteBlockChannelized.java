/*
 * Copyright (C) 2007-2009 Siemens AG
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
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.core.container.PreReadValueContainer;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.decoder.TypeDecoder;
import com.siemens.ct.exi.io.channel.ByteDecoderChannel;
import com.siemens.ct.exi.io.channel.DecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081105
 */

public abstract class AbstractDecoderByteBlockChannelized extends
		AbstractDecoderBlock {
	protected ByteDecoderChannel structureChannel;

	protected abstract InputStream getStream() throws IOException;

	protected abstract ByteDecoderChannel getNextChannel() throws IOException;

	public AbstractDecoderByteBlockChannelized(InputStream is,
			TypeDecoder typeDecoder) throws IOException {
		super(is, typeDecoder);
	}


	public void reconstructChannels(int cntValues, List<NameContext> valueQNames,
			Map<NameContext, List<Datatype>> dataTypes,
			Map<NameContext, Integer> occurrences,
			Map<NameContext, PreReadValueContainer> contentValues)
			throws IOException {		
		
		if (cntValues <= Constants.MAX_NUMBER_OF_VALUES) {
			// single compressed stream (included structure)
			for (int i = 0; i < valueQNames.size(); i++) {
				NameContext channelContext = valueQNames.get(i);
				int occs = occurrences.get(channelContext);
				
				List<Datatype> datatypes = dataTypes.get(channelContext);
				
				char[][] decodedValues = getDecodedValues(structureChannel, channelContext, occs, datatypes);
				contentValues.put(channelContext, new PreReadValueContainer(decodedValues));
			}
		} else {
			// first stream structure (already read)

			// second stream (if any), values <= 100
			if (areThereAnyLessEqualThan100(valueQNames, occurrences)) {
				ByteDecoderChannel bdcLessEqual100 = getNextChannel();
				for (int i = 0; i < valueQNames.size(); i++) {
					NameContext channelContext = valueQNames.get(i);
					int occs = occurrences.get(channelContext);

					if (occs <= Constants.MAX_NUMBER_OF_VALUES) {
						List<Datatype> datatypes = dataTypes.get(channelContext);
						
						char[][] decodedValues = getDecodedValues(bdcLessEqual100, channelContext, occs, datatypes);
						contentValues.put(channelContext, new PreReadValueContainer(decodedValues));
					}
				}
			}

			// proper stream for greater100
			for (int i = 0; i < valueQNames.size(); i++) {
				NameContext channelContext = valueQNames.get(i);
				int occs = occurrences.get(channelContext);
				if (occs > Constants.MAX_NUMBER_OF_VALUES) {
					ByteDecoderChannel bdcGreater100 = getNextChannel();
					List<Datatype> datatypes = dataTypes.get(channelContext);
					
					char[][] decodedValues = getDecodedValues(bdcGreater100, channelContext, occs, datatypes);
					contentValues.put(channelContext, new PreReadValueContainer(decodedValues));
				}
			}
		}
	}
	
	protected char[][] getDecodedValues(ByteDecoderChannel bdc, NameContext channelContext, int occs,
			List<Datatype> datatypes) throws IOException {
		
		String namespaceURI = channelContext.getNamespaceURI();
		String localName = channelContext.getLocalName();
		
		assert (datatypes.size() == occs);
		char[][] decodedValues = new char[occs][];
		// char[][] decodedValues = prvc.getValues();
		
		for (int k = 0; k < occs; k++) {
			Datatype dt = datatypes.get(k);
			if (dt == null) {
				assert(namespaceURI.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
				assert(decodedValues[k] != null );
			} else {
				decodedValues[k] = typeDecoder.readTypeValidValue(dt,
						bdc, namespaceURI, localName);				
			}
		}
		
		return decodedValues;
	}
	

	private static boolean areThereAnyLessEqualThan100(
			List<NameContext> qnames, Map<NameContext, Integer> occurrences) {
		for (int i = 0; i < qnames.size(); i++) {
			if (occurrences.get(qnames.get(i)) <= Constants.MAX_NUMBER_OF_VALUES) {
				return true;
			}
		}
		return false;
	}


	public ByteDecoderChannel getStructureChannel() {
		return structureChannel;
	}

	public DecoderChannel getValueChannel(String namespaceURI, String localName)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public char[] readTypedValidValue(Datatype datatype,
			final String namespaceURI, final String localName)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public char[] readValueAsString(String namespaceURI, String localName)
			throws IOException {
		throw new UnsupportedOperationException();
	}
}
