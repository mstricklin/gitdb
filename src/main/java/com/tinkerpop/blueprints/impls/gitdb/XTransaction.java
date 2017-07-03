// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.tinkerpop.blueprints.impls.gitdb.StringUtil.vertexKey;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.impls.gitdb.XVertexProxy.XVertex;
import com.tinkerpop.blueprints.impls.gitdb.XEdgeProxy.XEdge;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XTransaction {
    XTransaction(String baselineOID) {
        baseline = baselineOID;
    }

    void clear() {
        addedVertices.clear();
        deletedVertices.clear();
    }

    // =================================
    XVertex addVertex(final XVertex v) {
        addedVertices.put(v.key(), v);
        return v;
    }
    void removeVertex(long id) {
        addedVertices.remove(id);
        deletedVertices.add(id);
    }
    XVertex mutateVertex(final XVertex v) {
        if (deletedVertices.contains(v.key()))
            throw ExceptionFactory.vertexDeleted(v.key());
        XVertex xv = addedVertices.get(v.key());
        if (null == xv) {
            xv = new XVertex(v);
            addedVertices.put(xv.key(), xv);
        }
        return xv;
    }
    XVertex getVertex(long id) throws XCache.NotFoundException {
        if (deletedVertices.contains(id)) {
            return null;
        }
        XVertex v = addedVertices.get(id);
        if (null == v) {
            // pull from baseline cache
        }
        return v;
    }
//    Iterator<Long> getVertices() {
//        return addedVertices.keySet().iterator();
//    }
//    // =================================
//    void dump() {
//        log.info("Transaction (baseline {})", baseline);
//        log.info("Added vertices");
//        for (Map.Entry<Long, XVertex> e : addedVertices.entrySet()) {
//            log.info("{} => {}", e.getKey(), e.getValue());
//        }
//    }
    // =================================

//    private Map<Long, XVertex> addedEdges = newHashMap();
//    private Set<Long> deletedEdges = newHashSet();
//
//    private Lazy<Map<Long, XVertex>> addedVertices = new Lazy<Map<Long, XVertex>>() {
//        @Override public Map<Long, XVertex> get() {
//            return newHashMap();
//        }
//    };
//    private Lazy<Set<Long>> deletedVertices = new Lazy<Set<Long>>() {
//        @Override public Set<Long> get() {
//            return newHashSet();
//        }
//    };
//
//
//    private static <T> Map<Long, T> foo() {
//        return newHashMap();
//    }
//    private Map<Long, XVertex> z = foo();

    private Map<Long, XVertex> addedVertices = newHashMap();
    private Set<Long> deletedVertices = newHashSet();

    private Map<Long, XEdge> addedEdges = newHashMap();
    private Set<Long> deletedEdges = newHashSet();

    private final String baseline;


}
