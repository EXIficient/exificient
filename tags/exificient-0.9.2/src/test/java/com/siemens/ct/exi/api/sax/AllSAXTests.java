package com.siemens.ct.exi.api.sax;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	com.siemens.ct.exi.api.sax.SchemaLessProperties.class,
	com.siemens.ct.exi.api.sax.SchemaProperties.class
})
public class AllSAXTests {
}
