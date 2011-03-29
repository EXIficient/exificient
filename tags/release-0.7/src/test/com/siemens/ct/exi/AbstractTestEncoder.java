package com.siemens.ct.exi;

import java.io.InputStream;

public abstract class AbstractTestEncoder extends AbstractTestCoder {

	abstract public void encodeTo(EXIFactory ef, InputStream xmlInput)
			throws Exception;

}
