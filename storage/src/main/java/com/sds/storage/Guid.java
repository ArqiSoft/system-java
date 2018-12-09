package com.sds.storage;

import java.util.Arrays;
import java.util.UUID;

public final class Guid {
    private final static int[] MAPPING = { 3, 2, 1, 0, 5, 4, 7, 6, 8, 9, 10, 11, 12, 13, 14, 15 };
    private UUID uid;
    
    public Guid(UUID uid) {
        this.uid = uid;
    }

    /**
     * Creates Guid for a given byte array
     * @param data the byte array representation of the string
     * @return a created Guid
     */
    public static Guid fromByteArray(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(String.format("%02x", data[MAPPING[i]]));
            if (i == 3 || i == 5 || i == 7 || i == 9) {
                sb.append('-');
            }
        }

        return new Guid(UUID.fromString(sb.toString()));
    }

    public static Guid fromString(String input) {
        return new Guid(UUID.fromString(input));
    }

    public static Guid newGuid() {
        return new Guid(UUID.randomUUID());
    }
    
    public UUID getUUID() {
        return uid;
    }
    
    public byte[] toByteArray() {
        long longOne = uid.getMostSignificantBits();
        long longTwo = uid.getLeastSignificantBits();

        return new byte[] {
             (byte)(longOne >>> 32),
             (byte)(longOne >>> 40),
             (byte)(longOne >>> 48),
             (byte)(longOne >>> 56),   
             (byte)(longOne >>> 16),
             (byte)(longOne >>> 24),
             (byte) longOne,
             (byte)(longOne >>> 8),
             
             (byte)(longTwo >>> 56),
             (byte)(longTwo >>> 48),
             (byte)(longTwo >>> 40),
             (byte)(longTwo >>> 32),   
             (byte)(longTwo >>> 24),
             (byte)(longTwo >>> 16),
             (byte)(longTwo >>> 8 ),
             (byte) longTwo
              };        
    }
    
    @Override
    public String toString() {
        return uid.toString().toLowerCase();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uid == null) ? 0 : uid.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Guid other = (Guid) obj;
        if (uid == null) {
            if (other.uid != null)
                return false;
        } else if (!uid.equals(other.uid))
            return false;
        return true;
    }
    
    

}
