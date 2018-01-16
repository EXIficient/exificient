# EXIficient

EXIficient - open source implementation of the W3C [Efficient XML Interchange](http://www.w3.org/TR/exi/) (EXI) format specification.

The EXI format is a very compact representation for the Extensible Markup Language (XML) Information Set that is intended to simultaneously optimize performance and the utilization of computational resources.

[![Build Status](https://travis-ci.org/EXIficient/exificient.svg?branch=master)](https://travis-ci.org/EXIficient/exificient)

## API support

* SAX 1
* SAX 2 
* DOM
* StAX
* XmlPull

# Apache Maven Dependency

```
<dependency>
   <groupId>com.siemens.ct.exi</groupId>
   <artifactId>exificient</artifactId>
   <version>0.9.7</version>
</dependency>
```

## Requirements

* Java 1.5 or higher
* Xerces2 Java Parser 2.9.11 or higher for schema-informed mode


## Library - Code Sample

```java
/*
 *  Setup EXIFactory as required
 */
EXIFactory exiFactory = DefaultEXIFactory.newInstance();
// e.g., add additional settings beyond the default values
// exiFactory.setGrammars(GrammarFactory.newInstance().createGrammars("foo.xsd")); // use XML schema
// exiFactory.setCodingMode(CodingMode.COMPRESSION); // use deflate compression for larger XML files

/*
 *  encode XML to EXI
 */
String fileEXI = "foo.xml.exi"; // EXI output
OutputStream osEXI = new FileOutputStream(fileEXI);
EXIResult exiResult = new EXIResult(exiFactory);
exiResult.setOutputStream(osEXI);
XMLReader xmlReader = XMLReaderFactory.createXMLReader();
xmlReader.setContentHandler( exiResult.getHandler() );
xmlReader.parse("foo.xml"); // parse XML input
osEXI.close();

/*
 *  decode EXI to XML
 */
String fileXML = "foo.xml.exi.xml"; // XML output again
Result result = new StreamResult(fileXML);
InputSource is = new InputSource(fileEXI);
SAXSource exiSource = new EXISource(exiFactory);
exiSource.setInputSource(is);
TransformerFactory tf = TransformerFactory.newInstance();
Transformer transformer = tf.newTransformer();
transformer.transform(exiSource, result);
```

## Command-line Interface

EXIficient also provides a command-line interface.

```java
/* Class: com.siemens.ct.exi.cmd.EXIficientCMD */

      

######################################################################### 
###   EXIficient                                                      ### 
###   Command-Shell Options                                           ### 
######################################################################### 

 -h                               /* shows help */ 

 -encode 
 -decode 

 -i  
 -o  

 -schema  
 -xsdSchema                       /* XML schema datatypes only */ 
 -noSchema                        /* default */ 

 -strict 
 -preservePrefixes 
 -preserveComments 
 -preserveLexicalValues 
 -preservePIs                     /* processing instructions */ 
 -preserveDTDs                    /* DTDs & entity references */ 

 -bytePacked 
 -preCompression 
 -compression 

 -blockSize  
 -valueMaxLength  
 -valuePartitionCapacity  

 -noLocalValuePartitions          /* EXI Profile parameters */ 
 -maximumNumberOfBuiltInProductions  
 -maximumNumberOfBuiltInElementGrammars  
 
 -includeOptions 
 -includeCookie 
 -includeSchemaId 
 -includeSchemaLocation 
 -includeInsignificantXsiNil 
 -includeProfileValues 
 -retainEntityReference
 -fragment 
 -selfContained <{urn:foo}elWithNS,elDefNS>
 -datatypeRepresentationMap  

# Examples
 -encode -schema notebook.xsd -i notebook.xml
 -decode -schema notebook.xsd -i notebook.xml.exi -o notebookDec.xml
```
