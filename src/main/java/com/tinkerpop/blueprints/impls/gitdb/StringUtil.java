// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

public class StringUtil {
    public final static String VERTEX = ":/vertex/";
    public final static String EDGE = ":/edge/";


    static String vertexKey(String oid, long id) {
        return oid + VERTEX + Long.toString(id);
    }
    static String edgeKey(String oid, long id) {
        return oid + EDGE + Long.toString(id);
    }
}
