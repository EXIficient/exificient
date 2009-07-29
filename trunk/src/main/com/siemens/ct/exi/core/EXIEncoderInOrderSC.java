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

package com.siemens.ct.exi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.stringtable.StringTableEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.rule.Rule;

/**
 * Encoder for SELF_CONTAINED elements
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090414
 */

public class EXIEncoderInOrderSC extends EXIEncoderInOrder {

	// selfContained fragments
	protected List<StringTableEncoder> scStringTables;
	protected List<Map<String, Map<String, Rule>>> scRuntimeDispatchers;
	
	public EXIEncoderInOrderSC(EXIFactory exiFactory) {
		super(exiFactory);
		
		// selfContained
		scStringTables = new ArrayList<StringTableEncoder>();
		scRuntimeDispatchers = new ArrayList<Map<String, Map<String, Rule>>>();
	}	
	

	@Override
	public int encodeStartFragmentSelfContained(String uri, String localName,
			String prefix) throws EXIException {
		try {
			int skipBytesSC = -1;

			// SC Fragment
			int ec2 = currentRule.get2ndLevelEventCode(
					EventType.SELF_CONTAINED, fidelityOptions);

			if (ec2 == Constants.NOT_FOUND) {
				// throw error
				throw new EXIException(
						"SelfContained fragments need to be supported by EXI's Options. Please revise your configuration.");
			} else {

				this.encode2ndLevelEventCode(ec2);

				// 1. Save the string table, grammars, namespace prefixes and
				// any
				// implementation-specific state learned while processing this
				// EXI
				// Body.
				// 2. Initialize the string table, grammars, namespace prefixes
				// and
				// any implementation-specific state learned while processing
				// this
				// EXI Body to the state they held just prior to processing this
				// EXI
				// Body.
				// 3. Skip to the next byte-aligned boundary in the stream.
				channel.flush();
//				block.skipToNextByteBoundary();

				//	TODO byte position
				
//				if (block.bytePositionSupported()) {
//					skipBytesSC = block.getBytePosition();
//				}

				// string tables
				// TypeEncoder te = this.block.getTypeEncoder();
				// scStringTables.add(typeEncoder.getStringTable());
				// TODO create *just* string table and not whole TypeEncoder
				// again
//				typeEncoder.setStringTable(exiFactory.createTypeEncoder()
//						.getStringTable());
				// runtime-rules
				scRuntimeDispatchers.add(this.runtimeDispatcher);
				this.runtimeDispatcher = new HashMap<String, Map<String, Rule>>();
				// TODO namespace prefixes

				// 4. Let qname be the qname of the SE event immediately
				// preceding
				// this SC event.
				// 5. Let content be the sequence of events following this SC
				// event
				// that match the grammar for element qname, up to and including
				// the
				// terminating EE event.
				// 6. Evaluate the sequence of events (SD, SE(qname), content,
				// ED)
				// according to the Fragment grammar.
				replaceRuleAtTheTop(grammar.getBuiltInFragmentGrammar());
				replaceRuleAtTheTop(currentRule.lookFor(eventSD).next);
				encodeStartElement(uri, localName, prefix);
			}

			return skipBytesSC;
		} catch (IOException e) {
			throw new EXIException(e);
		}

	}


	@Override
	public void encodeEndFragmentSelfContained() throws EXIException {
		try {
			// close SC fragment
			EventInformation ei = currentRule.lookFor(eventED);

			if (ei != null) {
				// encode EventCode
				this.encode1stLevelEventCode(ei.getEventCode());
			} else {
				throw new EXIException("No EXI Event found for endDocument");
			}

			popElementContext();
			popElementRule();

			// 7. Restore the string table, grammars, namespace prefixes and
			// implementation-specific state learned while processing this EXI
			// Body to that saved in step 1 above.
			//TypeEncoder te = this.block.getTypeEncoder();
//			typeEncoder.setStringTable(scStringTables.remove(scStringTables.size() - 1));
			this.runtimeDispatcher = scRuntimeDispatchers
					.remove(scRuntimeDispatchers.size() - 1);
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}
}
