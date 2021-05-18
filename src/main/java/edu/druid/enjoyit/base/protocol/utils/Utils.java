package edu.druid.enjoyit.base.protocol.utils;

import java.util.Base64;

public final class Utils {
    private Utils(){}

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    public static String encode(String source) {
        if (source == null) {
            return null;
        }

        return source;
        //return new String(encoder.encode(source.getBytes(CHARSET)), CHARSET);
    }

    public static String decode(String source) {
        if (source == null) {
            return null;
        }

        return source;
        //return new String(decoder.decode(source.getBytes(CHARSET)), CHARSET);
    }


    public static String getCallerSimpleClassName() {
        try {
            throw new Exception();
        } catch (Exception ex) {
            String className = ex.getStackTrace()[1].getClassName();
            return className.substring(className.lastIndexOf('.') + 1);
        }
    }
}
