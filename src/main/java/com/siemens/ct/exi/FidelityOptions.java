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

package com.siemens.ct.exi;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTagGrammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedGrammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedStartTagGrammar;

/**
 * Some XML applications do not require the entire XML feature set and would
 * prefer to eliminate the overhead associated with unused features.
 * 
 * Applications can use a set of fidelity options to specify the XML features
 * they require.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

public class FidelityOptions implements Serializable {

	private static final long serialVersionUID = 2403291078846997571L;

	/* Comments, ProcessingInstructions, DTDs and Prefixes are preserved */
	public static final String FEATURE_COMMENT = "PRESERVE_COMMENTS";
	public static final String FEATURE_PI = "PRESERVE_PIS";
	public static final String FEATURE_DTD = "PRESERVE_DTDS";
	public static final String FEATURE_PREFIX = "PRESERVE_PREFIXES";

	/*
	 * Lexical form of element and attribute values is preserved in value
	 * content items
	 */
	public static final String FEATURE_LEXICAL_VALUE = "PRESERVE_LEXICAL_VALUES";

	/* Enable the use of self contained elements in the EXI stream */
	public static final String FEATURE_SC = "SELF_CONTAINED";

	/* Strict interpretation of schemas is used to achieve better compactness */
	public static final String FEATURE_STRICT = "STRICT";

	/* contains options set to TRUE */
	protected Set<String> options;

	/* special strict handling */
	protected boolean isStrict = false;
	
	/* quick fidelity booleans */
	protected boolean isComment = false;
	protected boolean isPI = false;
	protected boolean isDTD = false;
	protected boolean isPrefix = false;
	protected boolean isLexicalValue = false;
	protected boolean isSC = false;
	

	protected FidelityOptions() {
		options = new HashSet<String>();
	}

	/**
	 * Creates fidelity options using default options
	 * 
	 * @return default fidelity options
	 */
	public static FidelityOptions createDefault() {
		FidelityOptions fo = new FidelityOptions();

		return fo;
	}

	/**
	 * Creates fidelity options using strict option. Note: no namespace
	 * prefixes, comments etc are preserved nor schema deviations are allowed.
	 * 
	 * @return default fidelity options
	 */
	public static FidelityOptions createStrict() {
		FidelityOptions fo = new FidelityOptions();

		fo.options.add(FEATURE_STRICT);
		fo.isStrict = true;

		return fo;
	}

	/**
	 * Creates fidelity options using the maximum XML compatibility mode, e.g.
	 * preserving comments, unsignificant whitespaces et cetera.
	 * 
	 * <p>
	 * Note: Per default SelfContained Element support is not set to TRUE. It
	 * cannot work together with (Pre-)Compression!
	 * </p>
	 * 
	 * @return default fidelity options
	 */
	public static FidelityOptions createAll() {
		FidelityOptions fo = new FidelityOptions();

		fo.options.add(FEATURE_COMMENT);
		fo.isComment = true;
		fo.options.add(FEATURE_PI);
		fo.isPI = true;
		fo.options.add(FEATURE_DTD);
		fo.isDTD = true;
		fo.options.add(FEATURE_PREFIX);
		fo.isPrefix = true;
		fo.options.add(FEATURE_LEXICAL_VALUE);
		fo.isLexicalValue = true;
		
		// fo.options.add(FEATURE_SC);

		return fo;
	}

	/**
	 * Enables or disables the specified fidelity feature.
	 * 
	 * @param key
	 *            referring to a specific feature
	 * @param decision
	 *            enabling or disabling feature
	 * @throws UnsupportedOption if option is not supported
	 */
	public void setFidelity(String key, boolean decision)
			throws UnsupportedOption {
		if (key.equals(FEATURE_STRICT)) {
			if (decision) {
				// no other features allowed
				// (LEXICAL_VALUE is an exception)
				boolean prevContainedLexVal = options
						.contains(FEATURE_LEXICAL_VALUE);

				options.clear();
				isComment = false;
				isPI = false;
				isDTD = false;
				isPrefix = false;
				isLexicalValue = false;
				isSC = false;
				
				if (prevContainedLexVal) {
					options.add(FEATURE_LEXICAL_VALUE);
					isLexicalValue = true;
				}
				options.add(FEATURE_STRICT);

				isStrict = true;
			} else {
				// remove strict (if present)
				options.remove(key);
				isStrict = false;
			}
		} else if (key.equals(FEATURE_LEXICAL_VALUE)) {
			// LEXICAL_VALUE is special --> does affect grammars
			if (decision) {
				options.add(key);
				isLexicalValue = true;
			} else {
				// remove option (if present)
				options.remove(key);
				isLexicalValue = false;
			}
		} else if (key.equals(FEATURE_COMMENT) || key.equals(FEATURE_PI)
				|| key.equals(FEATURE_DTD) || key.equals(FEATURE_PREFIX)
				|| key.equals(FEATURE_SC)) {
			if (decision) {
				if (isStrict()) {
					options.remove(FEATURE_STRICT);
					this.isStrict = false;
					// TODO inform user that STRICT mode is de-activated
					// throw new UnsupportedOption(
					// "StrictMode is exclusive and does not allow any other option.");
				}
				options.add(key);
				if (key.equals(FEATURE_COMMENT)) {
					isComment = true;
				}
				if (key.equals(FEATURE_PI)) {
					isPI = true;
				}
				if (key.equals(FEATURE_DTD)) {
					isDTD = true;
				}
				if (key.equals(FEATURE_PREFIX)) {
					isPrefix = true;
				}
				if (key.equals(FEATURE_SC)) {
					isSC = true;
				}	
			} else {
				// remove option (if present)
				options.remove(key);
				if (key.equals(FEATURE_COMMENT)) {
					isComment = false;
				}
				if (key.equals(FEATURE_PI)) {
					isPI = false;
				}
				if (key.equals(FEATURE_DTD)) {
					isDTD = false;
				}
				if (key.equals(FEATURE_PREFIX)) {
					isPrefix = false;
				}
				if (key.equals(FEATURE_SC)) {
					isSC = false;
				}				
			}
		} else {
			throw new UnsupportedOption("FidelityOption '" + key
					+ "' is unknown!");
		}
	}

	/**
	 * Informs whether the specified feature is enabled.
	 * 
	 * @param key
	 *            feature
	 * @return whether option is turned on
	 */
	public boolean isFidelityEnabled(String key) {
		return options.contains(key);
	}

	/**
	 * Convenience method returning whether all fidelity options that affect
	 * grammars are turned off (e.g. Preserve.LEXICAL_VALUE is still allowed).
	 * 
	 * @return boolean whether strict mode is in play
	 */
	public final boolean isStrict() {
		return isStrict;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FidelityOptions) {
			FidelityOptions other = (FidelityOptions) o;
			return options.equals(other.options);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return options.hashCode();
	}

	@Override
	public String toString() {
		return options.toString();
	}
	
//	public boolean hasSecondOrThirdLevel(Grammar grammar) {
//		return(get2ndLevelCharacteristics(grammar) > 0); //  || get3rdLevelCharacteristics() > 0
//	}
	
	public int get1stLevelEventCodeLength(Grammar grammar){
		int cl1;
		
		switch(grammar.getGrammarType()) {
		/* Root grammars*/
		case DOCUMENT:
		case FRAGMENT:
			cl1 = 0;
			break;
		case DOC_END:
			if(isComment || isPI) {
				cl1 = 1;
			} else {
				cl1 = 0;
			}
			break;
		case SCHEMA_INFORMED_DOC_CONTENT:
		case BUILT_IN_DOC_CONTENT:
			cl1 = grammar.get1stLevelEventCodeLength(isDTD || isComment || isPI);
			break;
		case SCHEMA_INFORMED_FRAGMENT_CONTENT:
		case BUILT_IN_FRAGMENT_CONTENT:
			cl1 = grammar.get1stLevelEventCodeLength(isComment || isPI);
			break;
		/* Schema-informed Element and Type Grammars */
		case SCHEMA_INFORMED_FIRST_START_TAG_CONTENT:
		case SCHEMA_INFORMED_START_TAG_CONTENT:
		case SCHEMA_INFORMED_ELEMENT_CONTENT:
			cl1 = grammar.get1stLevelEventCodeLength(get2ndLevelCharacteristics(grammar) > 0);
			break;
		case BUILT_IN_START_TAG_CONTENT:
		case BUILT_IN_ELEMENT_CONTENT:
			cl1 = grammar.get1stLevelEventCodeLength(false); // boolean does not matter!
			break;
		default:
			cl1 = -1;	
		}
		
		return cl1;
	}
	
	
	public EventType get2ndLevelEventType(final int ec2, Grammar grammar) {
		EventType eventType = null;
		
		switch(grammar.getGrammarType()) {
		/* Root grammars*/
		case DOCUMENT:
		case FRAGMENT:
		case DOC_END:
		case SCHEMA_INFORMED_FRAGMENT_CONTENT:
		case BUILT_IN_FRAGMENT_CONTENT:
			break;
		/* Schema-informed Document and Fragment Grammars */
		/* Built-in Document and Fragment Grammars */
		case SCHEMA_INFORMED_DOC_CONTENT:
		case BUILT_IN_DOC_CONTENT:
			if(isDTD && ec2 == 0 ) {
				eventType = EventType.DOC_TYPE;
			}
			break;
		/* Schema-informed Element and Type Grammars */
		case SCHEMA_INFORMED_FIRST_START_TAG_CONTENT:
			SchemaInformedFirstStartTagGrammar sifst = (SchemaInformedFirstStartTagGrammar) grammar;
			if(isStrict) {
				// isTypeCastable, isNillable
				if (sifst.isTypeCastable()) {
					if(ec2 == 0) {
						eventType = EventType.ATTRIBUTE_XSI_TYPE;
					} else if (ec2 == 1) {
						eventType = EventType.ATTRIBUTE_XSI_NIL;
					}
				} else if (sifst.isNillable() && ec2 == 0) {
					eventType = EventType.ATTRIBUTE_XSI_NIL;
				}
			} else {
				// {0,EE?, 1,xsi:type, 2,xsi:nil, 3,AT*, 4,AT-untyped, 5,NS, 6,SC, 7,SE*, 8,CH, 9,ER, {CM, PI}}
				int dec = 0;
				if(sifst.hasEndElement()) {
					dec++;
				} 
				if(ec2 == 0 - dec) {
					eventType = EventType.END_ELEMENT_UNDECLARED; // EE
				} else {
					if(ec2 == 1 - dec) {
						eventType = EventType.ATTRIBUTE_XSI_TYPE; // xsi:type
					} else if (ec2 == 2 - dec) {
						eventType = EventType.ATTRIBUTE_XSI_NIL; // xsi:nil
					} else if (ec2 == 3 - dec) {
						eventType = EventType.ATTRIBUTE_GENERIC_UNDECLARED; // AT*
					} else if (ec2 == 4 - dec) {
						eventType = EventType.ATTRIBUTE_INVALID_VALUE; // AT-untyped
					} else {
						if(!isPrefix) {
							dec++;
						}
						if (ec2 == 5 - dec) {
							eventType = EventType.NAMESPACE_DECLARATION; // NS
						} else {
							if(!isSC) {
								dec++;
							}
							if (ec2 == 6 - dec) {
								eventType = EventType.SELF_CONTAINED; // SC
							} else {
								if(ec2 == 7 - dec) {
									eventType = EventType.START_ELEMENT_GENERIC_UNDECLARED; // SE*
								} else if(ec2 == 8 - dec) {
									eventType = EventType.CHARACTERS_GENERIC_UNDECLARED; // CH
								} else {
									if(!isDTD) {
										dec++;
									}
									if (ec2 == 9 - dec) {
										eventType = EventType.ENTITY_REFERENCE; // ER
									}
								}
							}
						}
					}
				}
			}
			break;
		case SCHEMA_INFORMED_START_TAG_CONTENT:
			SchemaInformedStartTagGrammar sist = (SchemaInformedStartTagGrammar) grammar;
			if(isStrict) {
				// no events
			} else {
				// {0,EE?, 1,AT*, 2,AT-untyped, 3,SE*, 4,CH, 5,ER, {CM, PI}}
				int dec = 0;
				if(sist.hasEndElement()) {
					dec++;
				} 
				if(ec2 == 0 - dec) {
					eventType = EventType.END_ELEMENT_UNDECLARED; // EE
				} else {
					if (ec2 == 1 - dec) {
						eventType = EventType.ATTRIBUTE_GENERIC_UNDECLARED; // AT*
					} else if (ec2 == 2 - dec) {
						eventType = EventType.ATTRIBUTE_INVALID_VALUE; // AT-untyped
					} else {
						if(ec2 == 3 - dec) {
							eventType = EventType.START_ELEMENT_GENERIC_UNDECLARED; // SE*
						} else if(ec2 == 4 - dec) {
							eventType = EventType.CHARACTERS_GENERIC_UNDECLARED; // CH
						} else {
							if(!isDTD) {
								dec++;
							}
							if (ec2 == 5 - dec) {
								eventType = EventType.ENTITY_REFERENCE; // ER
							}
						}
					}
				}
			}
			break;
		case SCHEMA_INFORMED_ELEMENT_CONTENT:
			SchemaInformedGrammar sig = (SchemaInformedGrammar) grammar;
			if(isStrict) {
				// no events
			} else {
				// {0,EE?, 1,SE*, 2,CH*, 3,ER?, {CM, PI}}
				int dec = 0;
				if(sig.hasEndElement()) {
					dec++;
				} 
				if(ec2 == 0 - dec) {
					eventType = EventType.END_ELEMENT_UNDECLARED; // EE
				} else {
					if(ec2 == 1 - dec) {
						eventType = EventType.START_ELEMENT_GENERIC_UNDECLARED; // SE*
					} else if(ec2 == 2 - dec) {
						eventType = EventType.CHARACTERS_GENERIC_UNDECLARED; // CH
					} else {
						if(!isDTD) {
							dec++;
						}
						if (ec2 == 3 - dec) {
							eventType = EventType.ENTITY_REFERENCE; // ER
						}
					}
				}
			}
			break;
		/* Built-in Element Grammars */
		case BUILT_IN_START_TAG_CONTENT:
			// {0,EE, 1,AT*, 2,NS, 3,SC, 4,SE*, 5,CH, 6,ER, {CM, PI}}
			if(ec2 == 0) {
				eventType = EventType.END_ELEMENT_UNDECLARED; // EE
			} else {
				if (ec2 == 1) {
					eventType = EventType.ATTRIBUTE_GENERIC_UNDECLARED; // AT*
				} else {
					int dec = 0;
					if(!isPrefix) {
						dec++;
					}
					if (ec2 == 2 - dec) {
						eventType = EventType.NAMESPACE_DECLARATION; // NS
					} else {
						if(!isSC) {
							dec++;
						}
						if (ec2 == 3 - dec) {
							eventType = EventType.SELF_CONTAINED; // SC
						} else {
							if(ec2 == 4 - dec) {
								eventType = EventType.START_ELEMENT_GENERIC_UNDECLARED; // SE*
							} else if(ec2 == 5 - dec) {
								eventType = EventType.CHARACTERS_GENERIC_UNDECLARED; // CH
							} else {
								if(!isDTD) {
									dec++;
								}
								if (ec2 == 6 - dec) {
									eventType = EventType.ENTITY_REFERENCE; // ER
								}
							}
						}
					}
				}
			}
			break;
		case BUILT_IN_ELEMENT_CONTENT:
			// {0,SE*, 1,CH, 2,ER, {CM, PI}}
			if(ec2 == 0) {
				eventType = EventType.START_ELEMENT_GENERIC_UNDECLARED; // SE*
			} else if(ec2 == 1) {
				eventType = EventType.CHARACTERS_GENERIC_UNDECLARED; // CH
			} else {
				if(isDTD && ec2 == 2) {
					eventType = EventType.ENTITY_REFERENCE; // ER
				}
			}
			break;
		}
		
		
		return eventType;
	}
	
	public int get2ndLevelEventCode(final EventType eventType, Grammar grammar) {
		int ec2 = Constants.NOT_FOUND;
		switch(grammar.getGrammarType()) {
		/* Root grammars*/
		case DOCUMENT:
		case FRAGMENT:
		case DOC_END:
		case SCHEMA_INFORMED_FRAGMENT_CONTENT:
		case BUILT_IN_FRAGMENT_CONTENT:
			break;
		/* Schema-informed Document and Fragment Grammars */
		/* Built-in Document and Fragment Grammars */
		case SCHEMA_INFORMED_DOC_CONTENT:
		case BUILT_IN_DOC_CONTENT:
			if(isDTD && eventType == EventType.DOC_TYPE) {
				ec2 = 0;
			}
			break;
		/* Schema-informed Element and Type Grammars */
		case SCHEMA_INFORMED_FIRST_START_TAG_CONTENT:
			SchemaInformedFirstStartTagGrammar sifst = (SchemaInformedFirstStartTagGrammar) grammar;
			if(isStrict) {
				// isTypeCastable, isNillable
				if (sifst.isTypeCastable()) {
					if(eventType == EventType.ATTRIBUTE_XSI_TYPE) {
						ec2 = 0;
					} else if (eventType == EventType.ATTRIBUTE_XSI_NIL) {
						ec2 = 1;
					}
				} else if (sifst.isNillable() && eventType == EventType.ATTRIBUTE_XSI_NIL) {
					ec2 = 0;
				}
			} else {
				// {0,EE?, 1,xsi:type, 2,xsi:nil, 3,AT*, 4,AT-untyped, 5,NS, 6,SC, 7,SE*, 8,CH, 9,ER, {CM, PI}}
				int dec = 0;
				if(sifst.hasEndElement()) {
					dec++;
				} 
				if(eventType == EventType.END_ELEMENT_UNDECLARED) {
					ec2 = 0 - dec; // EE
				} else {
					if(eventType == EventType.ATTRIBUTE_XSI_TYPE) {
						ec2 = 1 - dec; // xsi:type
					} else if (eventType == EventType.ATTRIBUTE_XSI_NIL) {
						ec2 = 2 - dec; // xsi:nil
					} else if (eventType == EventType.ATTRIBUTE_GENERIC_UNDECLARED) {
						ec2 = 3 - dec; // AT*
					} else if (eventType == EventType.ATTRIBUTE_INVALID_VALUE) {
						ec2 = 4 - dec; // AT-untyped
					} else {
						if(!isPrefix) {
							dec++;
						}
						if (eventType == EventType.NAMESPACE_DECLARATION) {
							ec2 = 5 - dec; // NS
						} else {
							if(!isSC) {
								dec++;
							}
							if (eventType == EventType.SELF_CONTAINED) {
								ec2 = 6 - dec; // SC
							} else {
								if(eventType == EventType.START_ELEMENT_GENERIC_UNDECLARED) {
									ec2 = 7 - dec; // SE*
								} else if(eventType == EventType.CHARACTERS_GENERIC_UNDECLARED) {
									ec2 = 8 - dec; // CH
								} else {
									if(!isDTD) {
										dec++;
									}
									if (eventType == EventType.ENTITY_REFERENCE) {
										ec2 = 9 - dec; // ER
									}
								}
							}
						}
					}
				}
			}
			break;
		case SCHEMA_INFORMED_START_TAG_CONTENT:
			SchemaInformedStartTagGrammar sist = (SchemaInformedStartTagGrammar) grammar;
			if(isStrict) {
				// no events
			} else {
				// {0,EE?, 1,AT*, 2,AT-untyped, 3,SE*, 4,CH, 5,ER, {CM, PI}}
				int dec = 0;
				if(sist.hasEndElement()) {
					dec++;
				} 
				if(eventType == EventType.END_ELEMENT_UNDECLARED) {
					ec2 = 0 - dec; // EE
				} else {
					if (eventType == EventType.ATTRIBUTE_GENERIC_UNDECLARED) {
						ec2 = 1 - dec; // AT*
					} else if (eventType == EventType.ATTRIBUTE_INVALID_VALUE) {
						ec2 = 2 - dec; // AT-untyped
					} else {
						if(eventType == EventType.START_ELEMENT_GENERIC_UNDECLARED) {
							ec2 = 3 - dec; // SE*
						} else if(eventType == EventType.CHARACTERS_GENERIC_UNDECLARED) {
							ec2 = 4 - dec; // CH
						} else {
							if(!isDTD) {
								dec++;
							}
							if (eventType == EventType.ENTITY_REFERENCE) {
								ec2 = 5 - dec; // ER
							}
						}
					}
				}
			}
			break;
		case SCHEMA_INFORMED_ELEMENT_CONTENT:
			SchemaInformedGrammar sig = (SchemaInformedGrammar) grammar;
			if(isStrict) {
				// no events
			} else {
				// {0,EE?, 1,SE*, 2,CH*, 3,ER?, {CM, PI}}
				int dec = 0;
				if(sig.hasEndElement()) {
					dec++;
				} 
				if(eventType == EventType.END_ELEMENT_UNDECLARED) {
					ec2 = 0 - dec; // EE
				} else {
					if(eventType == EventType.START_ELEMENT_GENERIC_UNDECLARED) {
						ec2 = 1 - dec; // SE*
					} else if(eventType == EventType.CHARACTERS_GENERIC_UNDECLARED) {
						ec2 = 2 - dec; // CH
					} else {
						if(!isDTD) {
							dec++;
						}
						if (eventType == EventType.ENTITY_REFERENCE) {
							ec2 = 3 - dec; // ER
						}
					}
				}
			}
			break;
		/* Built-in Element Grammars */
		case BUILT_IN_START_TAG_CONTENT:
			// {0,EE, 1,AT*, 2,NS, 3,SC, 4,SE*, 5,CH, 6,ER, {CM, PI}}
			if(eventType == EventType.END_ELEMENT_UNDECLARED) {
				ec2 = 0; // EE
			} else {
				if (eventType == EventType.ATTRIBUTE_GENERIC_UNDECLARED) {
					ec2 = 1; // AT*
				} else {
					int dec = 0;
					if(!isPrefix) {
						dec++;
					}
					if (eventType == EventType.NAMESPACE_DECLARATION) {
						ec2 = 2 - dec; // NS
					} else {
						if(!isSC) {
							dec++;
						}
						if (eventType == EventType.SELF_CONTAINED) {
							ec2 = 3 - dec; // SC
						} else {
							if(eventType == EventType.START_ELEMENT_GENERIC_UNDECLARED) {
								ec2 = 4 - dec; // SE*
							} else if(eventType == EventType.CHARACTERS_GENERIC_UNDECLARED) {
								ec2 = 5 - dec; // CH
							} else {
								if(!isDTD) {
									dec++;
								}
								if (eventType == EventType.ENTITY_REFERENCE) {
									ec2 = 6 - dec; // ER
								}
							}
						}
					}
				}
			}
			break;
		case BUILT_IN_ELEMENT_CONTENT:
			// {0,SE*, 1,CH, 2,ER, {CM, PI}}
			if(eventType == EventType.START_ELEMENT_GENERIC_UNDECLARED) {
				ec2 = 0; // SE*
			} else if(eventType == EventType.CHARACTERS_GENERIC_UNDECLARED) {
				ec2 = 1; // CH
			} else {
				if(isDTD && eventType == EventType.ENTITY_REFERENCE) {
					ec2 = 2; // ER
				}
			}
			break;
		}
		
		return ec2;
	}

	
	public int get2ndLevelCharacteristics(Grammar grammar) {
		int ch2 = 0;
		switch(grammar.getGrammarType()) {
		/* Root grammars*/
		case DOCUMENT:
		case FRAGMENT:
			//ch2 = 0;
			break;
		case DOC_END:
			if(get3rdLevelCharacteristics() > 0) {
				ch2++;
			}
			break;
		/* Schema-informed Document and Fragment Grammars */
		/* Built-in Document and Fragment Grammars */
		case SCHEMA_INFORMED_DOC_CONTENT:
		case BUILT_IN_DOC_CONTENT:
			if(isDTD) {
				ch2++;
			}
			if(get3rdLevelCharacteristics() > 0) {
				ch2++;
			}
			break;
		case SCHEMA_INFORMED_FRAGMENT_CONTENT:
		case BUILT_IN_FRAGMENT_CONTENT:
			if(get3rdLevelCharacteristics() > 0) {
				ch2++;
			}
			break;
		/* Schema-informed Element and Type Grammars */
		case SCHEMA_INFORMED_FIRST_START_TAG_CONTENT:
			SchemaInformedFirstStartTagGrammar sifst = (SchemaInformedFirstStartTagGrammar) grammar;
			if(isStrict) {
				// isTypeCastable, isNillable
				ch2 = (sifst.isTypeCastable() ? 1 : 0) + (sifst.isNillable() ? 1 : 0);
			} else {
				// {EE?, xsi:type, xsi:nil, AT*, AT-untyped, NS, SC, SE*, CH, ER, {CM, PI}}
				if(!sifst.hasEndElement()) { // EE
					ch2++;
				}
				ch2 += 4; // xsi:type, xsi:nil, AT*, AT-untyped,
				if(isPrefix) { // NS
					ch2++;
				}
				if(isSC) { // SC
					ch2++;
				}
				ch2 += 2; // SE*, CH
				if(isDTD) { // ER
					ch2++;
				}
				if(get3rdLevelCharacteristics() > 0) { // {CM, PI}
					ch2++;
				}
			}
			break;
		case SCHEMA_INFORMED_START_TAG_CONTENT:
			SchemaInformedStartTagGrammar sist = (SchemaInformedStartTagGrammar) grammar;
			if(isStrict) {
				// no events
			} else {
				// {EE?, AT*, AT-untyped, SE*, CH, ER, {CM, PI}}
				if(!sist.hasEndElement()) { // EE
					ch2++;
				}
				ch2 += 4; // AT*, AT-untyped, SE*, CH
				if(isDTD) { // ER
					ch2++;
				}
				if(get3rdLevelCharacteristics() > 0) { // {CM, PI}
					ch2++;
				}
			}
			break;
		case SCHEMA_INFORMED_ELEMENT_CONTENT:
			SchemaInformedGrammar sig = (SchemaInformedGrammar) grammar;
			if(isStrict) {
				// no events
			} else {
				// {EE?, SE*, CH*, ER?, {CM, PI}}
				if(!sig.hasEndElement()) { // EE
					ch2++;
				}
				ch2 += 2; // SE*, CH
				if(isDTD) { // ER
					ch2++;
				}
				if(get3rdLevelCharacteristics() > 0) { // {CM, PI}
					ch2++;
				}
			}
			break;
		/* Built-in Element Grammars */
		case BUILT_IN_START_TAG_CONTENT:
			// {EE, AT*, NS, SC, SE*, CH, ER, {CM, PI}}
			ch2 += 2; // EE, AT*
			if(isPrefix) { // NS
				ch2++;
			}
			if(isSC) { // SC
				ch2++;
			}
			ch2 += 2; // SE*, CH
			if(isDTD) { // ER
				ch2++;
			}
			if(get3rdLevelCharacteristics() > 0) { // {CM, PI}
				ch2++;
			}
			break;
		case BUILT_IN_ELEMENT_CONTENT:
			// {SE*, CH, ER, {CM, PI}}
			ch2 += 2; // SE*, CH
			if(isDTD) { // ER
				ch2++;
			}
			if(get3rdLevelCharacteristics() > 0) { // {CM, PI}
				ch2++;
			}
			break;
		}
		return ch2;
	}
	
	public EventType get3rdLevelEventType(final int ec3) {
		EventType eventType = null;
		
		if (ec3 == 0) {
			if (isComment) {
				return EventType.COMMENT;
			} else if (isPI) {
				eventType = EventType.PROCESSING_INSTRUCTION;
			}
		} else if (ec3 == 1) {
			assert(isFidelityEnabled(FidelityOptions.FEATURE_COMMENT));
			assert(isComment);
			assert(isFidelityEnabled(FidelityOptions.FEATURE_PI));
			assert(isPI);
			eventType = EventType.PROCESSING_INSTRUCTION;
		}
		
		return eventType;
	}
	
	
	public int get3rdLevelEventCode(EventType eventType) {
		int ec3 = Constants.NOT_FOUND;
		
		if (!isStrict) {
			// CM
			if (isComment) {
				if (EventType.COMMENT == eventType) {
					ec3 = 0;
				} else if (EventType.PROCESSING_INSTRUCTION == eventType) {
					ec3 = 1;
				}
			} else if (isPI) {
				if (EventType.PROCESSING_INSTRUCTION == eventType) {
					ec3 = 0;
				}
			}
		}

		return ec3;
	}
	

	public int get3rdLevelCharacteristics() {
		int ch = 0;
		if(isComment) {
			ch++;
		}
		if(isPI) {
			ch++;
		}
		return ch;
	}
	

}
