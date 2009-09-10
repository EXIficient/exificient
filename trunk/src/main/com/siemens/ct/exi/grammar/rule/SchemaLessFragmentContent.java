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

package com.siemens.ct.exi.grammar.rule;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.event.EndDocument;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.event.StartElementGeneric;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081015
 */

/*
 * <Built-in Fragment Grammar>
 * 
 * FragmentContent : SE () FragmentContent 0 ED 1 CM FragmentContent 2.0 PI
 * FragmentContent 2.1
 */

public class SchemaLessFragmentContent extends AbstractSchemaLessRule {
	public SchemaLessFragmentContent() {
		super();
		//	SE(*) --> FragmentContent
		addRule(new StartElementGeneric(), this);
		//	EE 
		addTerminalRule(new EndDocument());
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

	public EventType get2ndLevelEvent(int eventCode,
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
			this.addRule(se, this);
		}
	}

}
