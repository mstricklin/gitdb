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

import com.google.common.base.Predicate;
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

    XVertex getVertex(int id) throws XCache.NotFoundException {
        if (deletedVertices.contains(id)) {
            // TODO: throw?
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
            // TODO: throw?
            return null;
        }
        XVertex v = addedVertices.get(id);
        if (null == v) {
            log.error("TODO: pull from baseline");
        }
        return v;
    }

    Iterator<XVertex> getVertices() {
        Predicate<XVertex> notDeletedP = new Predicate<XVertex>() {
            @Override
            public boolean apply(XVertex xv) {
                return (! deletedVertices.contains(xv.key()) );
            }
        };
        // TODO: pull from baseline
        Iterator<XVertex> it0 = addedVertices.values().iterator();
        return Iterators.filter(it0, notDeletedP);
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

    XEdge getEdge(int id) throws XCache.NotFoundException {
        if (deletedEdges.contains(id)) {
            return null;
        }
        XEdge e = addedEdges.get(id);
        if (null == e) {
            log.error("TODO: pull from baseline");
        }
        return e;
    }
    XEdge getMutableEdge(int id) throws XCache.NotFoundException {
        if (deletedEdges.contains(id)) {
            return null;
        }
        XEdge e = addedEdges.get(id);
        if (null == e) {
            log.error("TODO: pull from baseline");
        }
        return e;
    }

    Iterator<XEdge> getEdges() {

        Predicate<XEdge> notDeletedP = new Predicate<XEdge>() {
            @Override
            public boolean apply(XEdge xe) {
                return (! deletedEdges.contains(xe.key()) );
            }
        };
        // TODO: pull from baseline
        Iterator<XEdge> it0 = addedEdges.values().iterator();
        return Iterators.filter(it0, notDeletedP);

        // pull from baseline
//        Iterator<Integer> it = Iterators.filter(addedEdges.keySet().iterator(), not(in(deletedEdges)));
//        return it;
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
