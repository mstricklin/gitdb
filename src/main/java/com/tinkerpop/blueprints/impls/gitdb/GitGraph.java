// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.tinkerpop.blueprints.impls.gitdb.IterableUtil.once;
import static com.tinkerpop.blueprints.impls.gitdb.XEdgeProxy.*;
import static com.tinkerpop.blueprints.impls.gitdb.XVertexProxy.XVertex;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.tinkerpop.blueprints.*;

import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.util.PropertyFilteredIterable;
import com.tinkerpop.blueprints.util.StringFactory;
import lombok.extern.slf4j.Slf4j;

// Actions
// 1. vertex: add, remove, get, list [getEdges, get neighbors] (4+2)
//     1a. vertexProperty: set (add/change), remove, list (3)
// 2. edge: add, remove, get, list [getEnds, getLabel] (4+2)
//     2a. edgeProperty: set (add/change), remove, list (3)
// 3. keyIndex: create, remove, list (3)
//     3a.  (implicit) put, get, remove, count [getName] (4+1)
// 4. index: create, get, list, drop (4)
//     4a.  put, get, remove, count [getName] (4+1)


@Slf4j
public class GitGraph implements TransactionalGraph, IndexableGraph, KeyIndexableGraph {

    private static final Features FEATURES = new Features();

    static {
        // TODO: revisit this...
        FEATURES.supportsSerializableObjectProperty = false;
        FEATURES.supportsBooleanProperty = true;
        FEATURES.supportsDoubleProperty = true;
        FEATURES.supportsFloatProperty = true;
        FEATURES.supportsIntegerProperty = true;
        FEATURES.supportsPrimitiveArrayProperty = true;
        FEATURES.supportsUniformListProperty = true;
        FEATURES.supportsMixedListProperty = false;
        FEATURES.supportsLongProperty = true;
        FEATURES.supportsMapProperty = false;
        FEATURES.supportsStringProperty = true;

        FEATURES.supportsDuplicateEdges = true;
        FEATURES.supportsSelfLoops = true;
        FEATURES.isPersistent = false; //true; // TODO
        FEATURES.isWrapper = false;
        FEATURES.supportsVertexIteration = true;
        FEATURES.supportsEdgeIteration = true;
        FEATURES.supportsVertexIndex = true;
        FEATURES.supportsEdgeIndex = true;
        FEATURES.ignoresSuppliedIds = true;
        FEATURES.supportsTransactions = false; // true
        FEATURES.supportsIndices = true;
        FEATURES.supportsKeyIndices = true;
        FEATURES.supportsVertexKeyIndex = true;
        FEATURES.supportsEdgeKeyIndex = true;
        FEATURES.supportsEdgeRetrieval = true;
        FEATURES.supportsVertexProperties = true;
        FEATURES.supportsEdgeProperties = true;
        FEATURES.supportsThreadedTransactions = false;
    }

    // =================================
    /*
    74fc730e803e0bfca94aa9ae7f7f156cd9773502
    80ac59cdf2c3d79693e2140b2717fe707ba43e5e
    f3f4be74e50c9cf036cc7d6b26f558f5c5f3fa51
    */
    public static final String anOID = "74fc730e803e0bfca94aa9ae7f7f156cd9773502";

    public static GitGraph of() {
        return new GitGraph();
    }

    public GitGraph() {
        vertexCounter.set(1);
        edgeCounter.set(1);

    }

    @Override
    public Features getFeatures() {
        return FEATURES;
    }

    public void baselineDump() {

    }

    public void dump() {
        if (!log.isInfoEnabled())
            return;
        log.info(" === vertices ===");
        for (Vertex v: getVertices())
            log.info("\t{}", v);
        log.info(" === edges ===");
        for (Edge e: getEdges())
            log.info("\t{}", e);
    }
    public void txDump() {
        if (!log.isInfoEnabled())
            return;
        tx().dump();
    }

    XTransaction tx() {
        return threadTransaction.get();
    }

    private ThreadLocal<XTransaction> threadTransaction = new ThreadLocal<XTransaction>() {
        @Override
        protected XTransaction initialValue() {
            return new XTransaction(anOID);
        }
    };

    // =================================
    @Override
    public Vertex addVertex(Object id) {
        XVertex xv = new XVertex(vertexCounter.getAndIncrement());
        tx().addVertex(xv);
        return XVertexProxy.of(xv, this);
    }

    @Override
    public Vertex getVertex(Object id) {
        if (null == id)
            throw ExceptionFactory.vertexIdCanNotBeNull();
        try {
            final Integer intId;
            if (id instanceof Integer)
                intId = (Integer) id;
            else if (id instanceof Number)
                intId = ((Number) id).intValue();
            else
                intId = Double.valueOf(id.toString()).intValue();
            XVertex xv = tx().getVertex(intId);
            return (null == xv) ? null : XVertexProxy.of(xv, this);
        } catch (XCache.NotFoundException e) {
            log.error("could not find vertex by id {}", id);
        } catch (NumberFormatException e) {
            log.error("could not use vertex id {}", id);
        }
        return null;
    }

    @Override
    public void removeVertex(Vertex vertex) {
        // TODO: remove from indices
        try {
            checkNotNull(vertex);
            for (Edge e : vertex.getEdges(Direction.OUT)) {
                removeEdge(e);
            }
            for (Edge e : vertex.getEdges(Direction.IN)) {
                removeEdge(e);
            }

            XVertexProxy vp = (XVertexProxy) vertex;
            tx().removeVertex(vp.key());
        } catch (XCache.NotFoundException e) {
            log.error("Vertex {} not found", vertex);
        }
    }

    @Override
    public Iterable<Vertex> getVertices() {
        Iterator<XVertex> vIdL = tx().getVertices();
        Iterator<XVertexProxy> vL = Iterators.transform(vIdL, XVertexProxy.makeVertex(this));
        return new ImmutableList.Builder<Vertex>()
                        .addAll(vL)
                        .build();
    }

    @Override
    public Iterable<Vertex> getVertices(String key, Object value) {
        // TODO: use indices
        return new PropertyFilteredIterable<>(key, value, this.getVertices());
    }

    // =================================
    @Override
    public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex, String label) {
        // TODO: add to indices
        if (label == null)
            throw ExceptionFactory.edgeLabelCanNotBeNull();
        XVertexProxy oV = (XVertexProxy) outVertex;
        XVertexProxy iV = (XVertexProxy) inVertex;
        // Are these real vertices?
        XEdge xe = new XEdge(edgeCounter.getAndIncrement(),
                oV.key(), iV.key(), label);
        tx().addEdge(xe);
        XEdgeProxy e = XEdgeProxy.of(xe, this);
        oV.addOutEdge(e.key());
        iV.addInEdge(e.key());
        return e;
    }

    @Override
    public Edge getEdge(Object id) {
        if (null == id)
            throw ExceptionFactory.edgeIdCanNotBeNull();
        try {
            final Integer intId;
            if (id instanceof Integer)
                intId = (Integer) id;
            else if (id instanceof Number)
                intId = ((Number) id).intValue();
            else
                intId = Double.valueOf(id.toString()).intValue();
            XEdge xe = tx().getEdge(intId);
            return (null == xe) ? null : XEdgeProxy.of(xe, this);
        } catch (XCache.NotFoundException e) {
            log.error("could not find vertex by id {}", id);
        } catch (NumberFormatException e) {
            log.error("could not use vertex id {}", id);
        }
        return null;
    }

    @Override
    public void removeEdge(Edge edge) {
        // TODO: remove from indices
        try {
            checkNotNull(edge);

            XEdgeProxy ep = (XEdgeProxy) edge;
            log.info("Edge Proxy {}", ep);

            XVertexProxy vOut = ep.getOutVertex();
            vOut.removeEdge(ep.key());

            XVertexProxy vIn  = ep.getInVertex();
            vIn.removeEdge(ep.key());

            tx().removeEdge(ep.key());
        } catch (XCache.NotFoundException e) {
            log.error("Edge {} not found", edge);
        }
    }

    @Override
    public Iterable<Edge> getEdges() {
        Iterator<XEdge> vIdL = tx().getEdges();
        Iterator<XEdgeProxy> vL = Iterators.transform(vIdL, XEdgeProxy.makeEdge(this));
        return new ImmutableList.Builder<Edge>()
                .addAll(vL)
                .build();
    }

    @Override
    public Iterable<Edge> getEdges(String key, Object value) {
        // TODO: use indices
        return new PropertyFilteredIterable<>(key, value, this.getEdges());
    }

    // =================================
    @Override
    public GraphQuery query() {
        return null;
    }

    public void begin() {

    }

    // =================================
    @Override
    public void shutdown() {
        // commit...
    }

    @Override
    public void commit() {
        tx().clear();
    }

    @Override
    public void rollback() {
        tx().clear();
    }

    @Deprecated
    @Override
    public void stopTransaction(Conclusion conclusion) {
    }

    // =================================
    // Index is an external structure, independent of vertices/edges.
    @Override
    public <T extends Element> Index<T> createIndex(String indexName, Class<T> indexClass, Parameter[] indexParameters) {
        return null;
    }

    @Override
    public <T extends Element> Index<T> getIndex(String indexName, Class<T> indexClass) {
        return null;
    }

    @Override
    public Iterable<Index<? extends Element>> getIndices() {
        return null;
    }

    @Override
    public void dropIndex(String indexName) {

    }

    // KeyIndex add index to existing key/value properties
    @Override
    public <T extends Element> void dropKeyIndex(String key, Class<T> elementClass) {

    }

    @Override
    public <T extends Element> void createKeyIndex(String key, Class<T> elementClass, Parameter[] indexParameters) {

    }

    @Override
    public <T extends Element> Set<String> getIndexedKeys(Class<T> elementClass) {
        return null;
    }

    // =================================
    public String toString() {
//        return StringFactory.graphString(this, "vertices:" + vertex.size() + " edges:" + edge.size());
        return StringFactory.graphString(this, "foo");
    }

    // =================================
    private final AtomicInteger vertexCounter = new AtomicInteger();
    private final AtomicInteger edgeCounter = new AtomicInteger();


}
