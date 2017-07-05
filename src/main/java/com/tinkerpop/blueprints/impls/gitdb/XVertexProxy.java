// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.collect.Iterators.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;

import java.util.*;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.impls.gitdb.XEdgeProxy.XEdge;
import com.tinkerpop.blueprints.util.DefaultVertexQuery;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.util.StringFactory;
import com.tinkerpop.blueprints.util.VerticesFromEdgesIterable;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XVertexProxy extends XElementProxy implements Vertex {
    public static Function<XVertex, XVertexProxy> makeVertex(final GitGraph graph) {
        return new Function<XVertex, XVertexProxy>() {
            @Override
            public XVertexProxy apply(XVertex xv) {
                return XVertexProxy.of(xv, graph);
            }
        };
    }
    public static XVertexProxy of(final XVertex xv, final GitGraph gg) {
        return new XVertexProxy(xv.key(), gg);
    }
    public static XVertexProxy of(int id, final GitGraph graph) {
        return new XVertexProxy(id, graph);
    }

    XVertexProxy(int id, final GitGraph graph) {
        super(id, graph);
    }
    @Override
    public Iterable<Edge> getEdges(Direction direction, String... labels) {
//        log.debug("getEdges for {} {}", this, direction);
        XVertex xv = getImpl();
        if (direction.equals(Direction.OUT)) {
            return getEdges(xv.outEdges, asList(labels));
        } else if (direction.equals((Direction.IN))) {
            return getEdges(xv.inEdges, asList(labels));
        } else if (direction.equals((Direction.BOTH))) {
            Iterable<Integer> edges = Sets.union( xv.inEdges, xv.outEdges );
            return getEdges(edges, asList(labels));
        }
        return Collections.emptyList();
    }
    // =================================
    // annoyingly, an empty label values is a special case for 'all'
    private List<Edge> getEdges(Iterable<Integer> edgeIDs, final Collection<String> labels) {
        Iterator<Integer> i0 = edgeIDs.iterator();
        Iterator<XEdge> i1 = Iterators.transform(i0, XEdgeProxy.lookupEdge(graph));
        Iterator<XEdgeProxy> i2 = Iterators.transform(i1, XEdgeProxy.makeEdge(graph));

        if (labels.isEmpty()) {
            return new ImmutableList.Builder<Edge>()
                    .addAll(i2)
                    .build();
        }
        Predicate<XEdgeProxy> labelled = new Predicate<XEdgeProxy>() {
            @Override
            public boolean apply(XEdgeProxy ep) {
                return labels.contains(ep.getLabel());
            }
        };
        Iterator<XEdgeProxy> i3 = Iterators.filter(i2, labelled);
        return new ImmutableList.Builder<Edge>()
                .addAll(i3)
                .build();
    }
    // =================================
    @Override
    public Iterable<Vertex> getVertices(Direction direction, String... labels) {
        return new VerticesFromEdgesIterable(this, direction, labels);
    }
    @Override
    public VertexQuery query() {
        return new DefaultVertexQuery(this);
    }
    @Override
    public Edge addEdge(String label, Vertex inVertex) {
        return graph.addEdge(null, this, inVertex, label);
    }
    // =================================
    void addOutEdge(int edgeId) {
        getMutableImpl().outEdges.add(edgeId);
    }
    void addInEdge(int edgeId) {
        log.info("addInEdge mutable impls {}", getMutableImpl());
        log.info("addInEdge impls {}", getImpl());
        getMutableImpl().inEdges.add(edgeId);
    }
    void removeEdge(int edgeID) {
        getMutableImpl().outEdges.remove(edgeID);
        getMutableImpl().inEdges.remove(edgeID);
    }
    public void remove() {
        this.graph.removeVertex(this);
    }
    // =================================
    @Override
    public String toString() {
        return StringFactory.vertexString(this);
    }
    // =================================
    @Override
    protected XVertex getImpl() {
        return graph.tx().getVertex(key());
    }
    @Override
    protected XVertex getMutableImpl() {
        return graph.tx().getMutableVertex(key());
    }
    @ToString(callSuper = true)
    public static class XVertex extends XElementProxy.XElement {
        private XVertex() { // no-arg ctor for jackson-json rehydration
            this(-1);
        }
        public XVertex(int id) {
            super(id);
            outEdges = newHashSet();
            inEdges = newHashSet();
        }
        XVertex(final XVertex xv) {
            super(xv);
            outEdges = newHashSet(xv.outEdges);
            inEdges = newHashSet(xv.inEdges);
        }

        private final Set<Integer> outEdges;
        private final Set<Integer> inEdges;
    }
    // =================================
}
