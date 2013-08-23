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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammars.event.EndDocument;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2-SNAPSHOT
 */

/*
 * <Built-in Fragment Grammar>
 * 
 * FragmentContent : SE () FragmentContent 0 ED 1 CM FragmentContent 2.0 PI
 * FragmentContent 2.1
 */

public class BuiltInFragmentContent extends AbstractBuiltInGrammar {

	private static final long serialVersionUID = 5335067628889400319L;

	/*
	 * FragmentContent : SE (*) FragmentContent 0 ED 1 CM FragmentContent 2.0 PI
	 * FragmentContent 2.1
	 */
	public BuiltInFragmentContent() {
		super();
		// Note: Add in different order in schema-less grammars
		// EE
		addTerminalProduction(new EndDocument());
		// SE(*) --> FragmentContent
		addProduction(START_ELEMENT_GENERIC, this);
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.BUILT_IN_FRAGMENT_CONTENT;
	}
	
	@Override
	public int get1stLevelEventCodeLength(FidelityOptions fidelityOptions) {
		// Note: cannot use variable this.ec1Length because does not have always 2nd level production
		return MethodsBag.getCodingLength(containers.size() + (this.hasSecondOrThirdLevel(fidelityOptions) ? 1 : 0));
	}

	@Override
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return (fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT) || fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PI));
	}

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		return Constants.NOT_FOUND;
	}

	public EventType get2ndLevelEventType(int eventCode,
			FidelityOptions fidelityOptions) {
		return null;
	}

	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		return 0;
	}

	@Override
	// public void learnStartElement(String uri, String localName) {
	public void learnStartElement(StartElement se) {
		// a learned rule is added to the front, technically
		// it is added to the tail
		// StartElement se = new StartElement(uri, localName);
		if (!this.contains(se)) {
			// eventRules.add ( new EventRule ( event, this ) );
			this.addProduction(se, this);
		}
	}

}
