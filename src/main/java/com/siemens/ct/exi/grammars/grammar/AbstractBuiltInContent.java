/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.grammars.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammars.event.Characters;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.types.BuiltIn;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public abstract class AbstractBuiltInContent extends AbstractBuiltInGrammar {

	private static final long serialVersionUID = -354500199734740496L;

	protected static final Map<FidelityOptions, List<EventType>> optionsStartTag;
	protected static final Map<FidelityOptions, List<EventType>> optionsChildContent;

	static {
		optionsStartTag = new HashMap<FidelityOptions, List<EventType>>();
		optionsChildContent = new HashMap<FidelityOptions, List<EventType>>();
	}
	
	protected boolean learnedCH = false;

	protected static List<EventType> get2ndLevelEventsStartTagItems(
			FidelityOptions fidelityOptions) {
		if (!optionsStartTag.containsKey(fidelityOptions)) {
			List<EventType> events = new ArrayList<EventType>();

			// if (!fidelityOptions.isStrict()) {
			// extensibility: EE, AT(*)
			events.add(EventType.END_ELEMENT_UNDECLARED);
			events.add(EventType.ATTRIBUTE_GENERIC_UNDECLARED);

			// NS
			if (fidelityOptions
					.isFidelityEnabled(FidelityOptions.FEATURE_PREFIX)) {
				events.add(EventType.NAMESPACE_DECLARATION);
			}
			// SC
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_SC)) {
				events.add(EventType.SELF_CONTAINED);
			}
			// }

			optionsStartTag.put(fidelityOptions, events);
		}

		return optionsStartTag.get(fidelityOptions);
	}

	protected static List<EventType> get2ndLevelEventsChildContentItems(
			FidelityOptions fidelityOptions) {
		if (!optionsChildContent.containsKey(fidelityOptions)) {
			List<EventType> events = new ArrayList<EventType>();

			// if (!fidelityOptions.isStrict()) {
			// extensibility: SE(*), CH
			events.add(EventType.START_ELEMENT_GENERIC_UNDECLARED);
			events.add(EventType.CHARACTERS_GENERIC_UNDECLARED);

			// ER
			if (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
				events.add(EventType.ENTITY_REFERENCE);
			}

			// }

			optionsChildContent.put(fidelityOptions, events);
		}

		return optionsChildContent.get(fidelityOptions);
	}
	
	@Override
	public void learnCharacters() {
		/*
		 * If a production CH with an event code of length 1 does not exist in
		 * the current element grammar create one add the production created
		 */
		if (!learnedCH) {
			addProduction(new Characters(BuiltIn.DEFAULT_DATATYPE), getElementContentGrammar()); // BuiltIn.DEFAULT_VALUE_NAME,
			learnedCH = true;
		}
	}


}
