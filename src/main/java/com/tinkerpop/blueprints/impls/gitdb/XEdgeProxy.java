// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.util.StringFactory;

public class XEdgeProxy extends XElementProxy implements Edge {
    public static XEdgeProxy of(long id, long outVertexId, long inVertexId, String label, final GitGraph graph) {
        return new XEdgeProxy(id, outVertexId, inVertexId, label, graph);
    }

    XEdgeProxy(long id_, long outVertexId, long inVertexId, String label_, GitGraph graph_) {
        super(id_, graph_);
        outId = outVertexId;
        inId = inVertexId;
        label = label_;
    }
    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
        if (direction.equals(Direction.OUT))
            return getOutVertex();
        if (direction.equals(Direction.IN))
            return getInVertex();
        throw ExceptionFactory.bothIsNotSupported();
    }
    XVertexProxy getOutVertex() {
        return (XVertexProxy)graph.getVertex(outId);
    }
    XVertexProxy getInVertex() {
        return (XVertexProxy)graph.getVertex(inId);
    }
    // =================================
    @Override
    public String getLabel() {
        return label;
    }
    @Override
    public <T> T getProperty(String key) {
        return null;
    }
    @Override
    public Set<String> getPropertyKeys() {
        return null;
    }
    @Override
    public void setProperty(String key, Object value) {
    }
    @Override
    public <T> T removeProperty(String key) {
        return null;
    }
    @Override
    public void remove() {

    }
    // =================================
    @Override
    public int hashCode() {
        return (int)id;
    }
    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }
    // =================================
    static class Mutable {

        private final Map<String, Object> properties = newHashMap();
    }

    private final long outId, inId;
    private final String label;
}
