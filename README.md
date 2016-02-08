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


## Changes


### Changes from version 0.9.4
* Premature Canonical EXI support (http://www.w3.org/TR/exi-c14n/)
* Bugfixes

### Changes from version 0.9.3
* Command-line Interface (CLI) added, see com.siemens.ct.exi.cmd.EXIficientCMD
* Graphical user interface (GUI) added, see com.siemens.ct.exi.gui.EXIficientGUI
* Some minor bugfixes and optimizations

### Changes from version 0.9.2
* EXI Profile support proven (http://www.w3.org/XML/EXI/implementation-report-profile/)
* Bugfixes and minor optimizations

### Changes from version 0.9.1
* EXI Profile support complete
* EXI errata fixes and code clean-up
* Maven repository setup

### Changes from version 0.9
* Fixes issues with Java7
* Minor bug-fixes and code clean-up

### Changes from version 0.8
* Support for EXI profile parameters
* Core parts (grammars & qname handling) revised to improve performance
* Bug-fixes and code clean-up

### Changes from version 0.7
* StAX API support
* Bug-fixes and performance improvements
* Code clean-up

### Changes from version 0.6
* Full support for Type-based encoder and decoder
* Includes option "schemaID" 
* Bug-fixes and performance improvements
* Code clean-up
* Uses latest Xerces 2.11.0 libs (xercesImpl.jar & xml-apis.jar)

### Changes from version 0.5
* Full support of EXI Cookie and Header
* Includes option "schemaID" 
* Bug-fixes (e.g., grammars, namespaces, ... )

### Changes from version 0.4
* Simplified all model group
* Support for EXI's blockSize when compressing
* Support for StringTable valueMaxLength and valuePartitionCapacity
* Bug-fixes (e.g., hexBinary, ... )

### Changes from version 0.3
* Datatypes facets/pattern for String values are taken into account
  for building restricted character sets
* XML Schema wildcards such as SE(ns:*) and AT(ns:*) are now encoded
  in a standard compliant way
* Processing performance boost

### Changes from version 0.2
* Document Object Model (DOM) API support fully integrated
* Multiple prefixes for same namespace URI can be preserved 
* EXI self-contained elements are enabled
* Preserve DocTYPE & EntityReference
* Processing performance has been improved

### Changes from version 0.1
* Expected but type-invalid attributes are handled correctly
* Restricted Character Sets for Built-in EXI Datatypes implemented
* Integer facets (n-bit Integer, Unsigned Integer, Integer)
* Boolean pattern facet (2bit Boolean) integrated
* more (all ?) schema-deviations handled
* Processing performance has been improved
