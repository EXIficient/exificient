package com.siemens.ct.exi.api.stream;

import javax.xml.stream.Location;

public final class EmptyLocation implements Location {
    
    /**
     * Singleton instance.
     */
    private static final EmptyLocation EMPTY_LOCATION_INSTANCE 
        = new EmptyLocation();
    
    private EmptyLocation() {}
    
    /** Returns the one and only instance of this class. */
    public static EmptyLocation getInstance() {
        return EMPTY_LOCATION_INSTANCE;
    }
    
    /**
     * Return the line number where the current event ends,
     * returns -1 if none is available.
     * @return the current line number
     */
    public int getLineNumber() {
        return -1;
    }
    
    /**
     * Return the column number where the current event ends,
     * returns -1 if none is available.
     * @return the current column number
     */
    public int getColumnNumber() {
        return -1;
    }
    
    /**
     * Return the byte or character offset into the input source this location
     * is pointing to. If the input source is a file or a byte stream then 
     * this is the byte offset into that stream, but if the input source is 
     * a character media then the offset is the character offset. 
     * Returns -1 if there is no offset available.
     * @return the current offset
     */
    public int getCharacterOffset() {
        return -1;
    }
    
    /**
     * Returns the public ID of the XML
     * @return the public ID, or null if not available
     */
    public String getPublicId() {
        return null;
    }
    
    /**
     * Returns the system ID of the XML
     * @return the system ID, or null if not available
     */
    public String getSystemId() {
        return null;
    }
}
