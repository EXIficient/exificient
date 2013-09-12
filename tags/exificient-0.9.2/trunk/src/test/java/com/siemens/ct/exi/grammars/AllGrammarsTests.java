package com.siemens.ct.exi.grammars;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	com.siemens.ct.exi.grammars.ElementFragmentGrammarTest.class,
	com.siemens.ct.exi.grammars.EventCodeTest.class,
	com.siemens.ct.exi.grammars.GrammarSerializeTest.class,
	com.siemens.ct.exi.grammars.GrammarTest.class
})
public class AllGrammarsTests {
	
}
