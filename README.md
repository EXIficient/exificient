# EXIficient

EXIficient - open source implementation of the W3C [Efficient XML Interchange](http://www.w3.org/TR/exi/) (EXI) format specification.

The EXI format is a very compact representation for the Extensible Markup Language (XML) Information Set that is intended to simultaneously optimize performance and the utilization of computational resources.

[![Build Status](https://travis-ci.org/EXIficient/exificient.svg?branch=master)](https://travis-ci.org/EXIficient/exificient)

## API support

* SAX 1
* SAX 2 
* DOM
* StAX

## Requirements

* Java 1.5 or higher
* Xerces2 Java Parser 2.9.11 or higher for schema-informed mode


## Library - Code Sample

```java
/*
* e.g. SAX
* A parser which implements SAX (ie, a SAX Parser) functions as a stream
* parser, with an event-driven API.
*/

// encode
EXIResult exiResult = new EXIResult( ... );
exiResult.setOutputStream( ... );
XMLReader xmlReader = XMLReaderFactory.createXMLReader();
xmlReader.setContentHandler( exiResult.getHandler() );
xmlReader.parse( ... );

// decode
EXISource exiSource = new EXISource( ... );
XMLReader exiReader = exiSource.getXMLReader();
exiReader.setContentHandler ( ... );
exiReader.parse ( new InputSource( ... ) ); 
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
