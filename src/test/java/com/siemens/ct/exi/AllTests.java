package com.siemens.ct.exi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	com.siemens.ct.exi.api.dom.AllDOMTests.class,
	com.siemens.ct.exi.api.sax.AllSAXTests.class,
	com.siemens.ct.exi.api.stream.AllStAXTests.class,
	com.siemens.ct.exi.attributes.AllAttributesTests.class,
	com.siemens.ct.exi.core.AllCoreTests.class,
	com.siemens.ct.exi.core.sax.AllSAXTests.class,
	com.siemens.ct.exi.data.AllDataTests.class,
	com.siemens.ct.exi.datatype.AllDatatypeTests.class,
	com.siemens.ct.exi.grammars.AllGrammarsTests.class,
	com.siemens.ct.exi.types.AllTypesTests.class,
	com.siemens.ct.exi.util.AllUtilTests.class
})
public class AllTests {
	
}
