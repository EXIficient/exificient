/*
 * Copyright (C) 2007-2010 Siemens AG
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

import java.util.ArrayList;
import java.util.List;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.EventTypeInformation;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20080718
 */

public abstract class AbstractSchemaInformedContent extends
		AbstractSchemaInformedRule {

	// second level events according to fidelity options
	// NOTE: events on second level are produced once. Setting new fidelity
	// option does not make any difference! A new fidelityOptions object is needed.
	protected List<EventTypeInformation> events2;
	protected FidelityOptions fidelityOptions2;

	public AbstractSchemaInformedContent() {
		super();
		events2 = new ArrayList<EventTypeInformation>();
	}

	abstract protected void buildEvents2(FidelityOptions fidelityOptions);
	
	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions) {
		if(fidelityOptions != fidelityOptions2) {
			buildEvents2(fidelityOptions);
		}
		
		return (get3rdLevelCharacteristics(fidelityOptions) > 0 ? events2.size() + 1 : events2.size());
	}
	
	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions) {
		if(fidelityOptions != fidelityOptions2) {
			buildEvents2(fidelityOptions);
		}
		
		for(EventTypeInformation eti : events2) {
			if (eti.eventType == eventType) {
				return eti.eventCode2;
			}
		}
		
		return Constants.NOT_FOUND;
	}
	
	public EventType get2ndLevelEvent(int eventCode2,
			FidelityOptions fidelityOptions) {
		if(fidelityOptions != fidelityOptions2) {
			buildEvents2(fidelityOptions);
		}
		
		assert (eventCode2 >= 0 && eventCode2 < events2.size());
		return events2.get(eventCode2).eventType;
	}

}
