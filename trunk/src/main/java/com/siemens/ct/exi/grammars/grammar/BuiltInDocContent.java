/*
 * Copyright (C) 2007-2015 Siemens AG
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

import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.event.EventType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

/*
 * <Built-in Document Grammar>
 * 
 * DocContent : SE () DocEnd 0 DT DocContent 1.0 CM DocContent 1.1.0 PI
 * DocContent 1.1.1
 */

public class BuiltInDocContent extends AbstractBuiltInGrammar {

	private static final long serialVersionUID = 3223520398225946713L;

	protected Grammar docEnd;

	public BuiltInDocContent(Grammar docEnd) {
		super();
		this.docEnd = docEnd;
		// SE(*) --> DocEnd
		this.addProduction(START_ELEMENT_GENERIC, docEnd);
	}

	public BuiltInDocContent(Grammar docEnd, String label) {
		this(docEnd);
		this.setLabel(label);
	}
	
	public GrammarType getGrammarType() {
		return GrammarType.BUILT_IN_DOC_CONTENT;
	}
	
	@Override
	public final void addProduction(Event event, Grammar grammar) {
		if(!event.isEventType(EventType.START_ELEMENT_GENERIC) || this.getNumberOfEvents() > 0) {
			throw new RuntimeException("Mis-use of BuiltInDocContent grammar");
		}
		super.addProduction(event, grammar);
	}
	
	@Override
	public int get1stLevelEventCodeLength(boolean withFidelityOptionsOrNonStrict) {
		// Note: cannot use variable this.ec1Length because does not have always 2nd level production
		//return MethodsBag.getCodingLength(containers.size() + (withFidelityOptionsOrNonStrict ? 1 : 0));
		return withFidelityOptionsOrNonStrict ? 1 : 0;
	}
}
