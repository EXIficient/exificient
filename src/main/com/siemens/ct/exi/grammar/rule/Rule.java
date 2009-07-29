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

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.EventInformation;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090422
 */

public interface Rule {
	/*
	 * end-rule
	 */
	public boolean isTerminalRule();
	
	/*
	 * first-element
	 */
	public boolean isFirstElementRule();

	/*
	 * rule created according to schema information
	 */
	public boolean isSchemaRule();
	
	/*
	 * get empty type
	 */
	public Rule getTypeEmpty();

	/*
	 * number of events
	 */
	public int getNumberOfEvents();

	public boolean hasSecondOrThirdLevel(FidelityOptions fidelityOptions);

	/*
	 * fetch event-code
	 */

	public int get2ndLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions);

	public int get3rdLevelEventCode(EventType eventType,
			FidelityOptions fidelityOptions);

	/*
	 * events, rules
	 */

	public EventType get2ndLevelEvent(int eventCode,
			FidelityOptions fidelityOptions);

	public EventType get3rdLevelEvent(int eventCode,
			FidelityOptions fidelityOptions);


	public int get1stLevelEventCodeLength(FidelityOptions fidelityOptions);


	public int get2ndLevelCharacteristics(FidelityOptions fidelityOptions);

	public int get3rdLevelCharacteristics(FidelityOptions fidelityOptions);

	/*
	 * 
	 */
	public void addRule(Event event, Rule rule);

	public void addTerminalRule(Event event);

	/*
	 * learning grammar
	 */
	public void learnStartElement(String uri, String localName);

	public void learnEndElement();

	public void learnAttribute(String uri, String localName);

	public void learnCharacters();

	/*
	 * For moving to element grammar
	 */
	public Rule getElementContentRule();
	

	/*
	 * Schema-deviated attributes
	 */
	public int getNumberOfSchemaDeviatedAttributes();

	public int getLeastAttributeEventCode();

	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	// for encoder
	public EventInformation lookFor( Event event );
	
//	public EventInformation lookForStartElement(String uri, String localName);
//	public RuleContainer getRuleContainer4Attribute(String uri, String localName);
//	public RuleContainer getRuleContainer4EndElement();
	
	//	for decoder
	public EventInformation lookFor( int eventCode );
	
}
