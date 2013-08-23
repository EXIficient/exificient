package com.siemens.ct.exi.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	com.siemens.ct.exi.core.BlockSizeTestCase.class,
	com.siemens.ct.exi.core.BoundedTableTestCase.class,
	com.siemens.ct.exi.core.EXIHeaderTestCase.class,
	com.siemens.ct.exi.core.EXIProfileTest.class,
	com.siemens.ct.exi.core.FragmentsTestCase.class,
	com.siemens.ct.exi.core.InitialEntriesStringTablePartitionsTestCase.class,
	com.siemens.ct.exi.core.RoundtripTestCase.class,
	com.siemens.ct.exi.core.SchemaInformedTest.class,
	com.siemens.ct.exi.core.SchemaLessTest.class,
	com.siemens.ct.exi.core.SelfContainedTestCase.class
})
public class AllCoreTests {
}
