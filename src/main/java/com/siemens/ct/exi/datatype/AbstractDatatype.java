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

package com.siemens.ct.exi.datatype;

import java.io.IOException;

import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public abstract class AbstractDatatype implements Datatype {

	private static final long serialVersionUID = 682257950812949619L;

	// default built-in datatype (no dtr map used)
	protected final BuiltInType builtInType;

	// for codec map
	protected final QNameContext schemaType;
	
	// base datatype
	protected Datatype baseDatatype;
	
	// whiteSpace
	protected WhiteSpace whiteSpace;

	public AbstractDatatype() {
		this(null, null);
	}

	public AbstractDatatype(BuiltInType builtInType, QNameContext schemaType) {
		this.builtInType = builtInType;
		this.schemaType = schemaType;
		// For all atomic datatypes other than string the value of whiteSpace is collapse
		whiteSpace = WhiteSpace.collapse;
	}

	public BuiltInType getBuiltInType() {
		return builtInType;
	}

	public QNameContext getSchemaType() {
		return schemaType;
	}
	
	public Datatype getBaseDatatype() {
		return baseDatatype;
	}
	
	public void setBaseDatatype(Datatype baseDatatype) {
		this.baseDatatype = baseDatatype;
	}
	
	public WhiteSpace getWhiteSpace() {
		return this.whiteSpace;
	}
	
	public void writeValueCanonical(QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {
		// most of the types are canonical per se
		this.writeValue(qnContext, valueChannel, stringEncoder);
	}

	public boolean equals(Object o) {
		if (o instanceof Datatype) {
			if(builtInType == ((Datatype) o).getBuiltInType()) {
				if(schemaType == null) {
					return (((Datatype) o).getSchemaType() == null);
				} else {
					return (schemaType.equals(((Datatype) o).getSchemaType()));
				}
			}
		}
		return false;
	}

	public int hashCode() {
		return builtInType.ordinal();
	}

	public String toString() {
		return builtInType.toString();
	}
}
