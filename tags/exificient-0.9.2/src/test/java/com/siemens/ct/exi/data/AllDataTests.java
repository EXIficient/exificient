package com.siemens.ct.exi.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	com.siemens.ct.exi.data.BuiltInXSDTestCase.class,
	com.siemens.ct.exi.data.DeviationsTestCase.class,
	com.siemens.ct.exi.data.EXIOptionsHeaderTestCase.class,
	com.siemens.ct.exi.data.FragmentTestCase.class,
	com.siemens.ct.exi.data.GeneralTestCase.class,
	com.siemens.ct.exi.data.SchemaTestCase.class,
	com.siemens.ct.exi.data.W3CTestCase.class
})
public class AllDataTests {
}
