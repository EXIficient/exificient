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

package com.siemens.ct.exi.grammars.event;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.grammars.grammar.Grammar;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class StartElement extends AbstractEvent {

	private static final long serialVersionUID = -874684674312937990L;

	protected final QName qname;
	protected final QNameContext qnameContext;

	private Grammar grammar;

	public StartElement(QNameContext qnc) {
		super(EventType.START_ELEMENT);
		this.qnameContext = qnc;
		this.qname = qnameContext.getQName();
	}
	
	public StartElement(QNameContext qnc, Grammar grammar) {
		this(qnc);
		this.setGrammar(grammar);
	}
	
	public QNameContext getQNameContext() {
		return this.qnameContext;	
	}

	public QName getQName() {
		return this.qname;
	}
	public void setGrammar(Grammar grammar) {
		this.grammar = grammar;
	}

	public Grammar getGrammar() {
		return grammar;
	}

	public String toString() {
		return super.toString() + "(" + qname.toString() + ")";
	}

	@Override
	public int hashCode() {
		return qname.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof StartElement) {
			StartElement otherSE = (StartElement) obj;
			return (qnameContext.getQNameID() == otherSE.qnameContext.getQNameID() );
//			if (qname.equals(otherSE.qname)) {
//				return grammar.equals(otherSE.grammar);
//			} else {
//				return false;
//			}
		} else {
			return false;
		}
	}

}
