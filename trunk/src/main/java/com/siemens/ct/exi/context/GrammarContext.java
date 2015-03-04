package com.siemens.ct.exi.context;

import java.io.Serializable;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.4
 */

public class GrammarContext implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3384221885278934477L;
	
	protected final GrammarUriContext[] grammarUriContexts;
	protected final int numberofQNamesContexts;

	public GrammarContext(GrammarUriContext[] grammarUriContexts,
			int numberofQNamesContexts) {
		this.grammarUriContexts = grammarUriContexts;
		this.numberofQNamesContexts = numberofQNamesContexts;
	}

	public int getNumberOfGrammarUriContexts() {
		return grammarUriContexts.length;
	}

	public GrammarUriContext getGrammarUriContext(int id) {
		return grammarUriContexts[id];
	}

	public GrammarUriContext getGrammarUriContext(String namespaceUri) {
		for (GrammarUriContext uc : grammarUriContexts) {
			if (uc.namespaceUri.equals(namespaceUri)) {
				return uc;
			}
		}
		return null;
	}

	public int getNumberOfGrammarQNameContexts() {
		return numberofQNamesContexts;
	}
}
