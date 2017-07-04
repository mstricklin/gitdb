// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.tinkerpop.blueprints.impls.gitdb.StringUtil.vertexKey;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterators;
import com.tinkerpop.blueprints.impls.gitdb.XVertexProxy.XVertex;
import com.tinkerpop.blueprints.impls.gitdb.XEdgeProxy.XEdge;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XTransaction {
    XTransaction(String baselineOID) {
        baseline = baselineOID;
    }


    // =================================
    void clear() {
        addedVertices.clear();
        deletedVertices.clear();
    }

    // =================================
    XVertex addVertex(final XVertex v) {
        addedVertices.put(v.key(), v);
        return v;
    }

    void removeVertex(int id) {
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

    XVertex getVertex(int id) throws XCache.NotFoundException {
        if (deletedVertices.contains(id)) {
            return null;
        }
        XVertex v = addedVertices.get(id);
        if (null == v) {
            log.error("TODO: pull from baseline");
        }
        return v;
    }

    XVertex getMutableVertex(int id) throws XCache.NotFoundException {
        if (deletedVertices.contains(id)) {
            return null;
        }
        XVertex v = addedVertices.get(id);
        if (null == v) {
            log.error("TODO: pull from baseline");
        }
        return v;
    }

    Iterator<Integer> getVertices() {
        // pull from baseline
        Iterator<Integer> it = Iterators.filter(addedVertices.keySet().iterator(), not(in(deletedVertices)));
        return it;
    }
    // =================================
    XEdge addEdge(final XEdge e) {
        addedEdges.put(e.key(), e);
        return e;
    }

    void removeEdge(int id) {
        addedEdges.remove(id);
        deletedEdges.add(id);
    }
    // =================================
    void dump() {
        if (!log.isInfoEnabled())
            return;
        log.info("Transaction {}", Thread.currentThread().getName());
        log.info("== Nodes");
        for (Map.Entry<Integer, XVertex> e : addedVertices.entrySet()) {
            log.info("\t{} => {}", e.getKey(), e.getValue());
        }
        for (Integer id : deletedVertices) {
            log.info("\tX {}", id);
        }
        log.info("== Edges");
        for (Map.Entry<Integer, XEdge> e : addedEdges.entrySet()) {
            log.info("\t{} => {}", e.getKey(), e.getValue());
        }
        for (Integer id : deletedEdges) {
            log.info("\tX {}", id);
        }

    }
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

    private Map<Integer, XVertex> addedVertices = newHashMap();
    private Set<Integer> deletedVertices = newHashSet();

    private Map<Integer, XEdge> addedEdges = newHashMap();
    private Set<Integer> deletedEdges = newHashSet();

    private final String baseline;


}
