/*
 * Copyright (C) 2007-2012 Siemens AG
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
 * @version 0.9
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
			addProduction(new Characters(BuiltIn.DEFAULT_VALUE_NAME,
					BuiltIn.DEFAULT_DATATYPE), getElementContentGrammar());
			learnedCH = true;
		}
	}


}
