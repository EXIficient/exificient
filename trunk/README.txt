-------------------------------------------------------------------------
EXIficient - open source implementation of the W3C Efficient XML Interchange (EXI) format specification
Version 0.9.5, released November 11, 2015
http://exificient.sourceforge.net/

The EXI format is a very compact representation for the Extensible Markup Language (XML) Information Set that is intended to simultaneously optimize performance and the utilization of computational resources.

Please report bugs via the SourceForge bug tracking system at http://sourceforge.net/p/exificient/bugs/.
Thank you.

Copyright (c) 2007-2015 Siemens AG

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

-------------------------------------------------------------------------

EXIficient supports the following specifications:

* SAX 1
* SAX 2 
* DOM
* StAX

EXIficient requires:

* Java 1.5 or higher
* Xerces2 Java Parser 2.9.11 or higher for schema-informed mode

For usage information, please see the usage documentation available at
http://exificient.sourceforge.net/.

-------------------------------------------------------------------------
CHANGES from version 0.9.4:
-------------------------------------------------------------------------
* Premature Canonical EXI support (http://www.w3.org/TR/exi-c14n/)
* Bugfixes

-------------------------------------------------------------------------
CHANGES from version 0.9.3:
-------------------------------------------------------------------------
* Command-line Interface (CLI) added, see com.siemens.ct.exi.cmd.EXIficientCMD
* Graphical user interface (GUI) added, see com.siemens.ct.exi.gui.EXIficientGUI
* Some minor bugfixes and optimizations

-------------------------------------------------------------------------
CHANGES from version 0.9.2:
-------------------------------------------------------------------------
* EXI Profile support proven (http://www.w3.org/XML/EXI/implementation-report-profile/)
* Bugfixes and minor optimizations

-------------------------------------------------------------------------
CHANGES from version 0.9.1:
-------------------------------------------------------------------------
* EXI Profile support complete
* EXI errata fixes and code clean-up
* Maven repository setup

-------------------------------------------------------------------------
CHANGES from version 0.9:
-------------------------------------------------------------------------
* Fixes issues with Java7
* Minor bug-fixes and code clean-up

-------------------------------------------------------------------------
CHANGES from version 0.8:
-------------------------------------------------------------------------
* Support for EXI profile parameters
* Core parts (grammars & qname handling) revised to improve performance
* Bug-fixes and code clean-up

-------------------------------------------------------------------------
CHANGES from version 0.7:
-------------------------------------------------------------------------
* StAX API support
* Bug-fixes and performance improvements
* Code clean-up

-------------------------------------------------------------------------
CHANGES from version 0.6:
-------------------------------------------------------------------------
* Full support for Type-based encoder and decoder
* Includes option "schemaID" 
* Bug-fixes and performance improvements
* Code clean-up
* Uses latest Xerces 2.11.0 libs (xercesImpl.jar & xml-apis.jar)

-------------------------------------------------------------------------
CHANGES from version 0.5:
-------------------------------------------------------------------------
* Full support of EXI Cookie and Header
* Includes option "schemaID" 
* Bug-fixes (e.g., grammars, namespaces, ... )

-------------------------------------------------------------------------
CHANGES from version 0.4:
-------------------------------------------------------------------------
* Simplified all model group
* Support for EXI's blockSize when compressing
* Support for StringTable valueMaxLength and valuePartitionCapacity
* Bug-fixes (e.g., hexBinary, ... )

-------------------------------------------------------------------------
CHANGES from version 0.3:
-------------------------------------------------------------------------
* Datatypes facets/pattern for String values are taken into account
  for building restricted character sets
* XML Schema wildcards such as SE(ns:*) and AT(ns:*) are now encoded
  in a standard compliant way
* Processing performance boost

-------------------------------------------------------------------------
CHANGES from version 0.2:
-------------------------------------------------------------------------
* Document Object Model (DOM) API support fully integrated
* Multiple prefixes for same namespace URI can be preserved 
* EXI self-contained elements are enabled
* Preserve DocTYPE & EntityReference
* Processing performance has been improved

-------------------------------------------------------------------------
CHANGES from version 0.1:
-------------------------------------------------------------------------
* Expected but type-invalid attributes are handled correctly
* Restricted Character Sets for Built-in EXI Datatypes implemented
* Integer facets (n-bit Integer, Unsigned Integer, Integer)
* Boolean pattern facet (2bit Boolean) integrated
* more (all ?) schema-deviations handled
* Processing performance has been improved