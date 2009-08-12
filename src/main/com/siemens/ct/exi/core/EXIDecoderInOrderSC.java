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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.rule.Rule;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090414
 */

public class EXIDecoderInOrderSC extends EXIDecoderInOrder {
	// selfContained fragments
//	protected List<StringTableDecoder> scStringTables;
	protected List<Map<String, Map<String, Rule>>> scRuntimeDispatchers;

	public EXIDecoderInOrderSC(EXIFactory exiFactory) {
		super(exiFactory);
		
		assert(fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC));
//		scStringTables = new ArrayList<StringTableDecoder>();
		scRuntimeDispatchers = new ArrayList<Map<String, Map<String, Rule>>>();
	}

	/*
	 * SELF_CONTAINED
	 */
	public void decodeStartFragmentSelfContained() throws EXIException {
		throw new RuntimeException("[EXI] SC");
//		try {
//			// 1. Save the string table, grammars, namespace prefixes and any
//			// implementation-specific state learned while processing this EXI
//			// Body.
//			// 2. Initialize the string table, grammars, namespace prefixes and
//			// any implementation-specific state learned while processing this
//			// EXI Body to the state they held just prior to processing this EXI
//			// Body.
//			// 3. Skip to the next byte-aligned boundary in the stream.
////			block.skipToNextByteBoundary();
////			// string tables
////			TypeDecoder td = this.block.getTypeDecoder();
//////			scStringTables.add(td.getStringTable());
//////			td.setStringTable(exiFactory.createTypeDecoder().getStringTable());
////			// runtime-rules
////			scRuntimeDispatchers.add(this.runtimeDispatcher);
////			this.runtimeDispatcher = new HashMap<String, Map<String, Rule>>();
//
//			// 4. Let qname be the qname of the SE event immediately preceding
//			// this SC event.
//			// 5. Let content be the sequence of events following this SC event
//			// that match the grammar for element qname, up to and including the
//			// terminating EE event.
//			// 6. Evaluate the sequence of events (SD, SE(qname), content, ED)
//			// according to the Fragment grammar.
//			replaceRuleAtTheTop(grammar.getBuiltInFragmentGrammar());
//			replaceRuleAtTheTop(currentRule.lookFor(0).next);
//
//			// inspect stream and detect next event
//			// inspectStream();
//			hasNext();
//			this.decodeStartElement();
//
//			// remove the *duplicate* scope due to the additional SE
//			// this.popScope();
//		} catch (IOException e) {
//			throw new EXIException(e);
//		}
	}

	public void decodeEndFragmentSelfContained() throws EXIException {
		throw new RuntimeException("[EXI] SC");
		
//		decodeEndDocument();
//
//		// 7. Restore the string table, grammars, namespace prefixes and
//		// implementation-specific state learned while processing this EXI
//		// Body to that saved in step 1 above.
//		TypeDecoder td = this.block.getTypeDecoder();
////		td.setStringTable(scStringTables.remove(scStringTables.size() - 1));
////		this.runtimeDispatcher = scRuntimeDispatchers
////				.remove(scRuntimeDispatchers.size() - 1);
	}

}
