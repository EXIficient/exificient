-------------------------------------------------------------------------
EXIficient - open source implementation of the W3C Efficient XML Interchange (EXI) format specification
Version 0.9, released July 5, 2012
http://exificient.sourceforge.net/

The EXI format is a very compact representation for the Extensible Markup Language (XML) Information Set that is intended to simultaneously optimize performance and the utilization of computational resources.

Please report bugs via the SourceForge bug tracking system at http://sourceforge.net/tracker/?group_id=236860.
Thank you.

Copyright (C) 2007-2012 Siemens AG

This program and its interfaces are free software;
you can redistribute it and/or modify
it under the terms of the GNU General Public License version 2
as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

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