package com.siemens.ct.exi.core;

import java.util.HashMap;
import java.util.Map;


public class URIContext {
	public final String namespaceURI;
	public final int id;
	
	public final Map<String, Integer> prefixes;
	public final Map<String, NameContext> localNames;
	
	public URIContext(String namespaceURI, int id) {
		this.namespaceURI = namespaceURI;
		this.id = id;
		prefixes = new HashMap<String, Integer>();
		localNames = new HashMap<String, NameContext>();
	}
	
	public void addLocalName(final String localName) {
		assert(!localNames.containsKey(localName));
		NameContext lnc = new NameContext(localName, this);
		localNames.put(localName, lnc);
	}
	
	public int getLocalNameSize() {
		return localNames.size();
	}
	
	
	public void addPrefix(final String prefix) {
		assert(!prefixes.containsKey(prefix));
		prefixes.put(prefix, prefixes.size());
	}
	
	public int getPrefixSize() {
		return prefixes.size();
	}
	
	protected Integer getPrefixID(final String prefix) {
		return prefixes.get(prefix);
	}


	public NameContext getNameContext(final String localName) {
		// assert(localNames.containsKey(localName));
		return localNames.get(localName);
	}
	
	public int getNumberOfLocalNames() {
		return localNames.size();
	}
	
	@Override
	public String toString() {
		return namespaceURI + "(" + id + ")";
	}

}
