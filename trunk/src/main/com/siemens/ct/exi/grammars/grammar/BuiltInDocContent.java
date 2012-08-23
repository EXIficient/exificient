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
import com.siemens.ct.exi.grammars.event.EventType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
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

	@Override
	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions) {
		return (fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD)
				|| fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT) || fidelityOptions
				.isFidelityEnabled(FidelityOptions.FEATURE_PI));
	}

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		if (eventType == EventType.DOC_TYPE
				&& fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			return 0;
		}

		return Constants.NOT_FOUND;
	}

	public EventType get2ndLevelEventType(int eventCode,
			FidelityOptions fidelityOptions) {
		if (eventCode == 0
				&& fidelityOptions
						.isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			return EventType.DOC_TYPE;
		}

		return null;
	}

	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		int ch2 = get3rdLevelCharacteristics(fidelityOptions) > 0 ? 1 : 0;
		ch2 += fidelityOptions.isFidelityEnabled(FidelityOptions.FEATURE_DTD) ? 1
				: 0;

		return ch2;
	}

}
