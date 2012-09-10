/*
 * Copyright (C) 2007-2012 Siemens AG
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

package com.siemens.ct.exi.context;

import java.util.ArrayList;
import java.util.List;

import com.siemens.ct.exi.core.container.ValueAndDatatype;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.BuiltInStartTag;
import com.siemens.ct.exi.values.StringValue;

public abstract class AbstractCoderContext implements CoderContext {
	// grammar qname contexts
	final int numberOfGrammarQNameContexts;

	// grammar uri contexts
	final int numberOfGrammarUriContexts;

	// runtime uri contexts
	List<EvolvingUriContext> runtimeUriContexts; // contains also grammar uris

	QNameContext qncXsiNil;
	QNameContext qncXsiType;

	RuntimeQNameContextEntries[] grammarQNameContexts;
	List<RuntimeQNameContextEntries> runtimeQNameContexts;
	
	// protected int numberBitsUri;

	public AbstractCoderContext(GrammarContext grammarContext) {
		this.numberOfGrammarUriContexts = grammarContext
				.getNumberOfGrammarUriContexts();
		this.numberOfGrammarQNameContexts = grammarContext
				.getNumberOfGrammarQNameContexts();
		grammarQNameContexts = new RuntimeQNameContextEntries[numberOfGrammarQNameContexts];
		for (int i = 0; i < numberOfGrammarQNameContexts; i++) {
			grammarQNameContexts[i] = new RuntimeQNameContextEntries();
		}

		runtimeUriContexts = new ArrayList<EvolvingUriContext>();
		//
		for (int i = 0; i < numberOfGrammarUriContexts; i++) {
			GrammarUriContext guc = grammarContext.getGrammarUriContext(i);
			addUriContext(new GrammarEvolvingUriContext(guc));
			// runtimeUriContexts.add(new GrammarEvolvingUriContext(guc));
			
			if (i == 2) {
				// URI 2 "http://www.w3.org/2001/XMLSchema-instance"
				// "nil", "type"
				qncXsiNil = guc.getQNameContext(0);
				qncXsiType = guc.getQNameContext(1);
			}
		}

		runtimeQNameContexts = new ArrayList<RuntimeQNameContextEntries>();

		channelOrders = new ArrayList<QNameContext>();
	}

	public QNameContext getXsiTypeContext() {
		return qncXsiType;
	}

	public QNameContext getXsiNilContext() {
		return qncXsiNil;
	}

	public final void addStringValue(QNameContext qnc, StringValue value) {
		RuntimeQNameContextEntries rqne = getRuntimeQNameContextEntries(qnc);

		if (rqne.strings == null) {
			rqne.strings = new ArrayList<StringValue>();
		}
		rqne.strings.add(value);
	}

	public StringValue freeStringValue(QNameContext qnc, int localValueID) {
		RuntimeQNameContextEntries rqne = getRuntimeQNameContextEntries(qnc);
		StringValue prev = rqne.strings.set(localValueID, null);
		return prev;
	}

	public final int getNumberOfStringValues(QNameContext qnc) {
		RuntimeQNameContextEntries rqne = getRuntimeQNameContextEntries(qnc);
		if (rqne.strings == null) {
			return 0;
		} else {
			return rqne.strings.size();
		}
	}

	public final StringValue getStringValue(QNameContext context, int localID) {
		RuntimeQNameContextEntries rqne = getRuntimeQNameContextEntries(context);
		return rqne.strings.get(localID);
	}

	public StartElement getGlobalStartElement(QNameContext qnc) {
		StartElement se;
		int qNameID = qnc.getQNameID();
		if (qNameID < this.numberOfGrammarQNameContexts) {
			// grammar start element
			se = qnc.getGlobalStartElement();
			if (se == null) {
				// check runtime element
				se = grammarQNameContexts[qNameID].globalStartElement;
				if (se == null) {
					se = new StartElement(qnc);
					se.setGrammar(new BuiltInStartTag());
					grammarQNameContexts[qNameID].globalStartElement = se;
				}
			}

		} else {
			// runtime qnames
			int runtimeQNameID = qNameID - numberOfGrammarQNameContexts;
			se = runtimeQNameContexts.get(runtimeQNameID).globalStartElement;
			if (se == null) {
				se = new StartElement(qnc);
				se.setGrammar(new BuiltInStartTag());
				runtimeQNameContexts.get(runtimeQNameID).globalStartElement = se;
			}
		}

		assert (se != null);
		return se;
	}

	protected QNameContext addQNameContext(EvolvingUriContext uc,
			String localName) {
		int qNameID = numberOfGrammarQNameContexts
				+ runtimeQNameContexts.size();
		QNameContext qnc = uc.addQNameContext(localName, qNameID);
		runtimeQNameContexts.add(new RuntimeQNameContextEntries());
		return qnc;
	}

	public int getNumberOfUris() {
		return runtimeUriContexts.size();
	}

	public EvolvingUriContext getUriContext(int namespaceUriID) {
		return runtimeUriContexts.get(namespaceUriID);
	}

	public EvolvingUriContext getUriContext(String namespaceUri) {
		for (EvolvingUriContext ruc : runtimeUriContexts) {
			if (ruc.getNamespaceUri().equals(namespaceUri)) {
				return ruc;
			}
		}
		return null;
	}

	public final EvolvingUriContext addUriContext(String namespaceUri) {
		assert (getUriContext(namespaceUri) == null);
		EvolvingUriContext ruc = new RuntimeEvolvingUriContext(
				getNumberOfUris(), namespaceUri);
		return addUriContext(ruc);
	}
	
	protected final EvolvingUriContext addUriContext(EvolvingUriContext euc) {
		runtimeUriContexts.add(euc);
		// numberBitsUri = MethodsBag.getCodingLength(getNumberOfUris() + 1); // numberEntries+1
		return euc;
	}
	

	public void clear() {
		// remove any newly added uris (if any)
		while (runtimeUriContexts.size() > numberOfGrammarUriContexts) {
			// remove last entry
			runtimeUriContexts.remove(runtimeUriContexts.size() - 1);
		}
		// numberBitsUri = MethodsBag.getCodingLength(getNumberOfUris() + 1); // numberEntries+1
		
		// clear remaining entries from runtime uris
		for (EvolvingUriContext ruc : runtimeUriContexts) {
			ruc.clear();
		}

		// clear grammar entries
		for (RuntimeQNameContextEntries rqe : grammarQNameContexts) {
			rqe.clear();
		}

		runtimeQNameContexts.clear();

		// channels
		channelOrders.clear();
	}

	public RuntimeQNameContextEntries getRuntimeQNameContextEntries(
			QNameContext qnc) {
		int qNameID = qnc.getQNameID();
		RuntimeQNameContextEntries rqne;
		if (qNameID < this.numberOfGrammarQNameContexts) {
			// grammar
			rqne = grammarQNameContexts[qNameID];
		} else {
			// runtime
			rqne = runtimeQNameContexts.get(qNameID
					- numberOfGrammarQNameContexts);
		}

		return rqne;
	}

	public void addValueAndDatatype(QNameContext qnc, ValueAndDatatype vd) {
		RuntimeQNameContextEntries rqne = getRuntimeQNameContextEntries(qnc);
		if (rqne.valuesAndDataypes == null) {
			// new channel --> store order
			this.channelOrders.add(qnc);
			// create new list
			rqne.valuesAndDataypes = new ArrayList<ValueAndDatatype>();
		}

		rqne.valuesAndDataypes.add(vd);
	}

	public List<ValueAndDatatype> getValueAndDatatypes(QNameContext qnc) {
		RuntimeQNameContextEntries rqne = getRuntimeQNameContextEntries(qnc);
		return rqne.valuesAndDataypes;
	}

	public void initCompressionBlock() {
		// re-set order
		channelOrders.clear();
		// re-set all channels
		for (RuntimeQNameContextEntries rqe : this.grammarQNameContexts) {
			rqe.valuesAndDataypes = null;
		}
		for (RuntimeQNameContextEntries rqe : this.runtimeQNameContexts) {
			rqe.valuesAndDataypes = null;
		}
	}

	protected List<QNameContext> channelOrders;

	public List<QNameContext> getChannelOrders() {
		return this.channelOrders;
	}

}
