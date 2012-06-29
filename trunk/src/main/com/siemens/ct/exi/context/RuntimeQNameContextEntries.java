package com.siemens.ct.exi.context;

import java.util.List;

import com.siemens.ct.exi.core.container.PreReadValue;
import com.siemens.ct.exi.core.container.ValueAndDatatype;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.values.StringValue;

public class RuntimeQNameContextEntries {

	// local string values
	List<StringValue> strings;
	// runtime element rule
	StartElement globalStartElement;
	// re-ordered encoder && re-ordered encoder datatype container
	List<ValueAndDatatype> valuesAndDataypes;

	// re-ordered decoder
	PreReadValue preReadValue;

	public PreReadValue getPreReadValue() {
		return preReadValue;
	}

	public void setPreReadValue(PreReadValue prrReadValue) {
		this.preReadValue = prrReadValue;
	}

	public RuntimeQNameContextEntries() {
	}

	public void clear() {
		strings = null;
		globalStartElement = null;
		valuesAndDataypes = null;
		preReadValue = null;
	}

	public List<ValueAndDatatype> getValuesAndDataypes() {
		return valuesAndDataypes;
	}
}
