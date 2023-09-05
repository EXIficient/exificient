# EXIficient

EXIficient - open source implementation of the W3C [Efficient XML Interchange](http://www.w3.org/TR/exi/) (EXI) format specification.

The EXI format is a very compact representation for the Extensible Markup Language (XML) Information Set that is intended to simultaneously optimize performance and the utilization of computational resources.

[![Java CI](https://github.com/EXIficient/exificient/actions/workflows/java-ci.yml/badge.svg)](https://github.com/EXIficient/exificient/actions/workflows/java-ci.yml)

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
   <version>1.0.7</version>
</dependency>
```

## Requirements

* Java 1.5 or higher
* Xerces2 Java Parser


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


## EXIFactory Settings

Note: in general all options are set in a way that a small EXI stream is produced. However for larger XML files (e.g., COMPRESSION) or desired fidelity options (e.g., preserver comments) different settings might be chosen.

| General                | Information                                                         | Default   | Hint                                                                                                                                                                                       |
|------------------------|---------------------------------------------------------------------|-----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| blockSize              | Specifies the block size used for EXI compression                   | 1,000,000 | The default blockSize is intentionally large but can be reduced                                                                                                                            |
| valueMaxLength         | Specifies the total capacity of value partitions in a string table  | unbounded | Reducing the number reduces the possible required memory usage                                                                                                                             |
| valuePartitionCapacity | Specifies the total capacity of value partitions in a string table  | unbounded | Often larger strings (> 16 characters) are unlikely to be a string table hit. Hence setting a lower value may reduce memory usage and speed up processing for no string table hits |


| com.siemens.ct.exi.core.FidelityOptions |                                                                 |         |
|-----------------------------------------|-----------------------------------------------------------------|---------|
| Option                                  | Information                                                     | Default |
| FEATURE_COMMENT                         | Comments are preserved                                          | false   |
| FEATURE_PI                              | Processing Instructions are preserved                           | false   |
| FEATURE_DTD                             | DTDs and Entity References are preserved                        | false   |
| FEATURE_PREFIX                          | Namespace Prefixes are preserved                                | false   |
| FEATURE_LEXICAL_VALUE                   | Lexical form of values is be preserved  (e.g., float 1.00 vs 1) | false   |


| com.siemens.ct.exi.core.CodingMode | Information (default is BIT_PACKED)                                                                                                                                                     | Hint                                                  |
|------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| BIT_PACKED                         | Alignment option value bit-packed indicates that the the event codes and associated content are packed in bits without any paddings in-between                                          | Small files                                           |
| BYTE_PACKED                        | Alignment option value byte-alignment indicates that the event codes and associated content are aligned on byte boundaries                                                              | Small files                                           |
| PRE_COMPRESSION                    | Alignment option value pre-compression alignment indicates that all steps involved in compression are to be done with the exception of the final step of applying the DEFLATE algorithm | Large Files (e.g., compression built-in in transport) |
| COMPRESSION                        | The compression option is used to increase compactness using additional computational resources (DEFLATE algorithm)                                                                     | Large Files                                           |


| com.siemens.ct.exi.core.EncodingOptions | Information                                                                                         | Default | Hint                                                   |
|-----------------------------------------|-----------------------------------------------------------------------------------------------------|---------|--------------------------------------------------------|
| INCLUDE_COOKIE                          | EXI Cookie, which is a four byte field that serves to indicate an EXI stream                        | false   | Useful if stream can be of any other type than EXI     |
| INCLUDE_OPTIONS                         | EXI Options, which provides a way to specify the options used to encode the body of the EXI stream. | false   | Useful if options may vary or are unknown to recipient |
| INCLUDE_SCHEMA_ID                       | Identify the schema information, if any, used to encode the body                                    | false   | Useful if schema information are unknwon to recipient  |
