package com.siemens.ct.exi.main.api.jaxb;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.xml.sax.InputSource;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.api.sax.EXIResult;
import com.siemens.ct.exi.main.api.sax.EXISource;
import com.siemens.ct.exi.main.api.stream.StAXDecoder;
import com.siemens.ct.exi.main.api.stream.StAXEncoder;
import com.siemens.ct.exi.main.generated.Notebook;
import com.siemens.ct.exi.main.generated.ObjectFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;

public class JAXBNotebookTest {

	@Test
	public void testSAX() throws JAXBException, EXIException, IOException, DatatypeConfigurationException {
		String xsd = "./data/W3C/PrimerNotebook/notebook.xsd";
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(GrammarFactory.newInstance().createGrammars(xsd));	
	
		_testSAX(exiFactory);
		
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		_testSAX(exiFactory);
	}
	
	void _testSAX(EXIFactory exiFactory) throws JAXBException, EXIException, IOException, DatatypeConfigurationException {
		String xml = "./data/W3C/PrimerNotebook/notebook.xml";
		
		// create JAXB context
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
		
		// read xml by creating unmarshaller
		Unmarshaller u = context.createUnmarshaller();
		Notebook notebook = (Notebook) u.unmarshal(new FileInputStream(xml));	
		
		// create EXI marshaller
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Marshaller mExi = context.createMarshaller();
		EXIResult result = new EXIResult(exiFactory);
		result.setOutputStream(baos);
		mExi.marshal(notebook, result);
		
		// create EXI unmarshaller
		Unmarshaller uEXI = context.createUnmarshaller();
		EXISource source = new EXISource(exiFactory);
		source.setInputSource(new InputSource(new ByteArrayInputStream(baos.toByteArray())));
		Object o = uEXI.unmarshal(source);
		assertTrue(o instanceof Notebook);
		Notebook notebook2 = (Notebook) o;
		
		assertTrue(notebook.getDate().equals(notebook2.getDate()));
	}
	
	
	
	@Test
	public void testStAX() throws JAXBException, EXIException, IOException, DatatypeConfigurationException, XMLStreamException {
		String xsd = "./data/W3C/PrimerNotebook/notebook.xsd";
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(GrammarFactory.newInstance().createGrammars(xsd));	
		
		_testStAX(exiFactory);
		
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		_testStAX(exiFactory);
	}
	
	
	void _testStAX(EXIFactory exiFactory) throws JAXBException, EXIException, IOException, DatatypeConfigurationException, XMLStreamException {
		String xml = "./data/W3C/PrimerNotebook/notebook.xml";
		
		// create JAXB context
		JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
		
		// read xml by creating unmarshaller
		Unmarshaller u = context.createUnmarshaller();
		Notebook notebook = (Notebook) u.unmarshal(new FileInputStream(xml));
		
		// create EXI marshaller
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Marshaller mExi = context.createMarshaller();
		StAXEncoder staxEncoder = new StAXEncoder(exiFactory);
		staxEncoder.setOutputStream(baos);
		mExi.marshal(notebook, staxEncoder);
		
		// create EXI unmarshaller
		Unmarshaller uEXI = context.createUnmarshaller();
		StAXDecoder staxDecoder = new StAXDecoder(exiFactory);
		staxDecoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
		Object o = uEXI.unmarshal(staxDecoder);
		assertTrue(o instanceof Notebook);
		Notebook notebook2 = (Notebook) o;
		
		assertTrue(notebook.getDate().equals(notebook2.getDate()));
	}

}
