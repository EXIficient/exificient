package com.siemens.ct.exi;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractTestEncoder extends AbstractTestCoder {

//	abstract public void setupEXIWriter(EXIFactory ef) throws EXIException;
	
	abstract public void encodeTo(InputStream xmlInput, OutputStream exiOutput)
			throws Exception;

}
