package com.siemens.ct.exi.datatype;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	com.siemens.ct.exi.datatype.BinaryTest.class,
	com.siemens.ct.exi.datatype.BooleanTest.class,
	com.siemens.ct.exi.datatype.DatetimeTest.class,
	com.siemens.ct.exi.datatype.DecimalTest.class,
	com.siemens.ct.exi.datatype.EnumerationTest.class,
	com.siemens.ct.exi.datatype.FloatTest.class,
	com.siemens.ct.exi.datatype.IntegerTest.class,
	com.siemens.ct.exi.datatype.ListTest.class,
	com.siemens.ct.exi.datatype.NBitUnsignedIntegerTest.class,
	com.siemens.ct.exi.datatype.RegularExpressionTest.class,
	com.siemens.ct.exi.datatype.StringTableTest.class,
	com.siemens.ct.exi.datatype.StringTest.class,
	com.siemens.ct.exi.datatype.UnsignedIntegerTest.class
})
public class AllDatatypeTests {
}
