package com.siemens.ct.exi.grammar;

import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.rule.Rule;

public class EventInformationSchemaLess extends EventInformation {
	
	protected final Rule father;
	
	public EventInformationSchemaLess(Rule father, Rule next, Event event, int eventCode) {
		super(next, event, eventCode);
		this.father = father;
	}

	@Override
	public int getEventCode() {
		//	internal eventCodes in schema-less do have the reverse order
		return father.getNumberOfEvents() - 1 - this.eventCode;

	}

}
