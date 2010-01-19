package com.siemens.ct.exi.grammar;

import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.rule.Rule;

public abstract class EventInformation {
	
	public final Rule next;
	final int eventCode;
	public final Event event;

	public EventInformation(Rule next, Event event, int eventCode) {
		this.next = next;
		this.event = event;
		this.eventCode = eventCode;
	}
	
	abstract public int getEventCode();
	
	@Override
	public String toString() {
		return "[" + eventCode + "] " + event + " -> " + next;
	}
}