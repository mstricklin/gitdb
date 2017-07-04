// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.tinkerpop.blueprints.impls.gitdb.XVertexProxy.*;

import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.util.StringFactory;
import lombok.ToString;

public class XEdgeProxy extends XElementProxy implements Edge {
    public static XEdgeProxy of(final XEdge xe, final GitGraph gg) {
        return new XEdgeProxy(xe.key(), xe.outId, xe.inId, xe.label, gg);
    }

    public static XEdgeProxy of(int id, long outVertexId, long inVertexId, String label, final GitGraph graph) {
        return new XEdgeProxy(id, outVertexId, inVertexId, label, graph);
    }

    XEdgeProxy(int id_, long outVertexId, long inVertexId, String label_, GitGraph graph_) {
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
        return (XVertexProxy) graph.getVertex(outId);
    }

    XVertexProxy getInVertex() {
        return (XVertexProxy) graph.getVertex(inId);
    }

    public void remove() {
        this.graph.removeEdge(this);
    }

    // =================================
    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return StringFactory.edgeString(this);
    }

    @Override
    protected XEdge getImpl() {
        return null;
    }
    @Override
    protected XEdge getMutableImpl() {
        return null;
    }

    // =================================
    @ToString(callSuper = true)
    public static class XEdge extends XElement {
        XEdge(final XEdge ie) {
            super(ie);
            this.outId = ie.outId;
            this.inId = ie.inId;
            this.label = ie.label;
        }

        XEdge(int id, int outId, int inId, String label) {
            super(id);
            this.outId = outId;
            this.inId = inId;
            this.label = label;
        }

        private final int outId, inId;
        private final String label;
    }

    // =================================
    private final long outId, inId;
    private final String label;


}
