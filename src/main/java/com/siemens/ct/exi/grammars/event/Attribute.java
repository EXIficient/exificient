/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.grammars.event;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.types.BuiltIn;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class Attribute extends AbstractDatatypeEvent {

	protected final QName qname;
	protected final QNameContext qnameContext;

	public Attribute(QNameContext qnc, Datatype datatype) {
		super(EventType.ATTRIBUTE, datatype);
		this.qnameContext = qnc;
		this.qname = qnameContext.getQName();
	}
	
	public Attribute(QNameContext qnc) {
		this(qnc, BuiltIn.DEFAULT_DATATYPE);
	}
	
	public QNameContext getQNameContext() {
		return this.qnameContext;	
	}

	public QName getQName() {
		return this.qname;
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
		} else if (obj instanceof Attribute) {
			Attribute otherAT = (Attribute) obj;
			return (qname.equals(otherAT.qname));
		} else {
			return false;
		}
	}

}
