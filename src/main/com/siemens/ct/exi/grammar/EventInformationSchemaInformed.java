package com.siemens.ct.exi.grammar;

import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.rule.Rule;

public class EventInformationSchemaInformed extends EventInformation {

	public EventInformationSchemaInformed(Rule next, Event event, int eventCode) {
		super(next, event, eventCode);
	}

	@Override
	public int getEventCode() {
		return eventCode;
	}

}
