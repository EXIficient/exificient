package com.siemens.ct.exi.context;

import java.util.List;

import com.siemens.ct.exi.core.container.ValueAndDatatype;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.values.StringValue;

public interface CoderContext {

	// --------------------------------
	public int getNumberOfUris();

	public EvolvingUriContext getUriContext(String namespaceUri);

	public EvolvingUriContext getUriContext(int namespaceUriID);

	public EvolvingUriContext addUriContext(String namespaceUri);

	public QNameContext getXsiTypeContext();

	public QNameContext getXsiNilContext();

	public int getNumberOfStringValues(QNameContext qnc);

	public void addStringValue(QNameContext qnc, StringValue value);

	public StringValue getStringValue(QNameContext context, int localID);

	public StringValue freeStringValue(QNameContext qnc, int localValueID);

	// --------------------------------

	public StartElement getGlobalStartElement(QNameContext qnc);

	// --------------------------------
	public void clear();

	public List<QNameContext> getChannelOrders();

	public void addValueAndDatatype(QNameContext qnc, ValueAndDatatype vd);

	public List<ValueAndDatatype> getValueAndDatatypes(QNameContext qnc);

	public void initCompressionBlock();

	public RuntimeQNameContextEntries getRuntimeQNameContextEntries(
			QNameContext qnc);

}
