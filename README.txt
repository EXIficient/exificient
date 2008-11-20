-------------------------------------------------------------------------
EXIficient - open source implementation of the W3C Efficient XML Interchange (EXI) format specification
Version 0.2, released November 20, 2008
http://exificient.sourceforge.net/

The EXI format is a very compact representation for the Extensible Markup Language (XML) Information Set that is intended to simultaneously optimize performance and the utilization of computational resources.

Please report bugs via the SourceForge bug tracking system at http://sourceforge.net/tracker/?group_id=236860.
Thank you.

Copyright (C) 2007, 2008 Siemens AG

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

EXIficient requires:

* Java 1.5 or higher
* Xerces2 Java Parser 2.9 or higher (schema-informed mode only)

For usage information, please see the usage documentation available at
http://exificient.sourceforge.net/.

-------------------------------------------------------------------------
CHANGES from version 0.1:
-------------------------------------------------------------------------
* Expected but type-invalid attributes are handled correctly
* Restricted Character Sets for Built-in EXI Datatypes implemented
* Integer facets (n-bit Integer, Unsigned Integer, Integer)
* Boolean pattern facet (2bit Boolean) integrated
* more (all ?) schema-deviations handled
* Processing performance has been improved