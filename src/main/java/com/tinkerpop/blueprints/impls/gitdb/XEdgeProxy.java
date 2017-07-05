// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.tinkerpop.blueprints.impls.gitdb.XVertexProxy.*;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.util.StringFactory;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XEdgeProxy extends XElementProxy implements Edge {
    public static Function<Integer, XEdge> lookupEdge(final GitGraph graph) {
        return new Function<Integer, XEdge>() {
            @Override
            public XEdge apply(Integer id) {
                return graph.tx().getEdge(id);
            }
        };
    }
    public static Function<XEdge, XEdgeProxy> makeEdge(final GitGraph graph) {
        return new Function<XEdge, XEdgeProxy>() {
            @Override
            public XEdgeProxy apply(XEdge xe) {
                return XEdgeProxy.of(xe, graph);
            }
        };
    }
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
//        log.info("self: {}", this);
        XEdge e = getImpl();
        return XVertexProxy.of(getImpl().outId, graph);
    }

    XVertexProxy getInVertex() {
        return XVertexProxy.of(getImpl().inId, graph);
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
        return graph.tx().getEdge(key());
    }
    @Override
    protected XEdge getMutableImpl() {
        return graph.tx().getMutableEdge(key());
    }

    // =================================
    @ToString(callSuper = true)
    public static class XEdge extends XElement {
        private XEdge() {
            this(-1, -1, -1, "");
        }
        XEdge(final XEdge xe) {
            super(xe);
            this.outId = xe.outId;
            this.inId = xe.inId;
            this.label = xe.label;
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
