package com.siemens.ct.exi.api.xmlpull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class ParserExample {
 
	public static void main(final String[] args) throws XmlPullParserException,
			MalformedURLException, IOException, EXIException, SAXException {
		
		String xml = "./data/W3C/PrimerNotebook/notebook.xml";

		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		// Encode to EXI
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		EXIResult exiResult = new EXIResult(factory);
		exiResult.setOutputStream(baos);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler( exiResult.getHandler() );
		xmlReader.parse(xml); // parse XML input
		
		// read from EXI through EXIPullParser
		System.out.println("### E X I ###");
		XmlPullParser xppEXI = new EXIPullParser(factory);
		xppEXI.setInput(new ByteArrayInputStream(baos.toByteArray()), null);
		read(xppEXI);
		
		// XML Pull parser
		System.out.println("### X M L ###");
		XmlPullParser xppXML = XmlPullParserFactory.newInstance().newPullParser();
		BufferedReader in = new BufferedReader(new FileReader(xml));
		xppXML.setInput(in);
		read(xppXML);

	}
	
	
	static void read(XmlPullParser xpp) throws XmlPullParserException, IOException {
		while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
			int ev = xpp.next();
			switch(ev) {
			case XmlPullParser.START_TAG:
				System.out.println("SE > " + xpp.getName());
				int atCnt = xpp.getAttributeCount();
				if(atCnt > 0) {
					System.out.println("\t" + atCnt + "# Attributes ");
					for(int i=0;i<atCnt; i++) {
						System.out.println("\t" + i + "\t" + xpp.getAttributeName(i) + " : " + xpp.getAttributeValue(i));
					}
				}
				// xpp.getNamespaceCount(depth)
				break;
			case XmlPullParser.END_TAG:
				System.out.println("EE < " + xpp.getName());
				break;
			case XmlPullParser.TEXT:
				System.out.println("CH  " + xpp.getText());
				break;
			}
		}
	}
}