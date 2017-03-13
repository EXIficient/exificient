package com.siemens.ct.exi.api.xmlpull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.xmlpull.mxp1_serializer.MXSerializer;
import org.xmlpull.v1.XmlSerializer;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class SerializerExample {
 
	public static void main(final String[] args) throws 
			MalformedURLException, IOException, EXIException {
		
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		// Write EXI
		XmlSerializer xppEXI = new EXISerializer(factory);
		ByteArrayOutputStream baosEXI = new  ByteArrayOutputStream();
		xppEXI.setOutput(baosEXI, null);
		write(xppEXI);
		System.out.println("EXI size=" + baosEXI.size());	
		
		// Write XML
		XmlSerializer xppXML = new MXSerializer();
		ByteArrayOutputStream baosXML = new  ByteArrayOutputStream();
		xppXML.setOutput(baosXML, null);
		write(xppXML);
		System.out.println("XML size=" + baosXML.size() + " \t" + new String(baosXML.toByteArray()));	
	}
	
	static void write(XmlSerializer xpp) throws IllegalArgumentException, IllegalStateException, IOException {
		xpp.startDocument(null, null);
		
		xpp.setPrefix("foo", "urn:foo"); // first prefix
		xpp.startTag("", "root");
		
		xpp.attribute("", "atRoot", "atValue");
		{
			xpp.comment("my comment");
			
			xpp.startTag("", "el1");
			xpp.text("el1 text");
			xpp.endTag("", "el1");
		}
		xpp.endTag("", "root");
		xpp.endDocument();
	}
}