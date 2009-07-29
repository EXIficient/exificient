package com.siemens.ct.exi.grammar;

import com.siemens.ct.exi.grammar.event.EventType;

public class EventTypeInformation {
	
	//	second level event code
	public final int eventCode2;
	//	second level event type (e.g. undeclared SE or AT events etc.)
	public final EventType eventType;

	public EventTypeInformation(EventType eventType, int eventCode2) {
		this.eventType = eventType;
		this.eventCode2 = eventCode2;
	}
	
	@Override
	public String toString() {
		return "[" + eventCode2 + "] " + eventType;
	}
}