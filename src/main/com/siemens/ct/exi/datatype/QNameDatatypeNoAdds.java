/*
 * Copyright (C) 2007-2011 Siemens AG
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

package com.siemens.ct.exi.datatype;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.core.AbstractEXIBodyCoder;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class QNameDatatypeNoAdds extends QNameDatatype {

	private static final long serialVersionUID = -5388595112370214500L;

	public QNameDatatypeNoAdds(AbstractEXIBodyCoder namespaces, QName schemaType) {
		super(namespaces, schemaType);
	}

	// @Override
	// protected void addURI(final String namespaceURI) {
	// // no addtions
	// uriContext = null;
	// }
	//
	// protected void addURIForce(final String namespaceURI) {
	// super.addURI(namespaceURI);
	// }
	//
	//
	// @Override
	// protected QName addLocalName(final String uri, final String localName) {
	// // no additions
	// return new QName(uri, localName);
	// // return null;
	// }
	//
	// protected QName addLocalNameForce(final String uri, final String
	// localName) {
	// return super.addLocalName(uri,localName);
	// }
	//
	// @Override
	// protected void addPrefix(final String prefix) {
	// // no addtions
	// }
	//
	// protected void addPrefixForce(final String prefix) {
	// super.addPrefix(prefix);
	// }
	//
	// @Override
	// protected void updateURIContext(final String namespaceURI) {
	// // try to find right URI Entry
	// for (RuntimeURIEntry uc : this.runtimeURIEntries) {
	// if (uc.namespaceURI.equals(namespaceURI)) {
	// uriContext = uc;
	// return;
	// }
	// }
	// // URI unknown so far
	// uriContext = null;
	//
	//
	// // if (uriContext != null) {
	// // super.updateURIContext(namespaceURI);
	// // }
	// //// if (uriContext == null) {
	// // // pick one context
	// // uriContext = runtimeURIEntries.get(0);
	// //// } else {
	// //// super.updateURIContext(namespaceURI);
	// //// }
	// }

	// @Override
	// public void setGrammarURIEnties(GrammarURIEntry[] grammarURIEntries) {
	// this.grammarURIEntries = grammarURIEntries;
	// runtimeURIEntries.clear();
	// for (GrammarURIEntry grammarEntry : grammarURIEntries) {
	// addURIForce(grammarEntry.uri);
	// // prefixes
	// for (String prefix : grammarEntry.prefixes) {
	// addPrefixForce(prefix);
	// }
	// // local-names
	// for (String localName : grammarEntry.localNames) {
	// // uriContext.addLocalName(localName);
	// addLocalNameForce(grammarEntry.uri, localName);
	// }
	// }
	// }

}