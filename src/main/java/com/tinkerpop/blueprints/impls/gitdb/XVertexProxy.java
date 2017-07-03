// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.base.Function;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.util.StringFactory;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XVertexProxy extends XElementProxy implements Vertex {
    public static Function<Long, Vertex> makeVertex(final GitGraph graph) {
        return new Function<Long, Vertex>() {
            @Override
            public Vertex apply(Long id) {
                return XVertexProxy.of(id, graph);
            }
        };
    }
    public static XVertexProxy of(final XVertex xv, final GitGraph gg) {
        return new XVertexProxy(xv.key(), gg);
    }
    public static XVertexProxy of(long id, final GitGraph graph) {
        return new XVertexProxy(id, graph);
    }

    XVertexProxy(long id, final GitGraph graph) {
        super(id, graph);
    }
    @Override
    public Iterable<Edge> getEdges(Direction direction, String... labels) {
        // get XVertex
        log.info("getEdges for {} {}", this, direction);
        if (direction.equals(Direction.OUT)) {
//            return newArrayList(getEdges(impl.outEdges, asList(labels)));
        }

        return Collections.emptyList();
    }
    // =================================
    // annoyingly, an empty label values is a special case for 'all'
    private Iterable<Edge> getEdges(Iterable<Integer> edgeIDs, final Collection<String> labels) {
//        Iterable<Edge> i = transform(edgeIDs, lookupEdge);
//        if (labels.isEmpty())
//            return i;
//        return filter(i, new Predicate<Edge>() {
//            @Override
//            public boolean apply(Edge e) {
//                return labels.contains(e.getLabel());
//            }
//        });

        return Collections.emptyList();
    }
    // =================================
    @Override
    public Iterable<Vertex> getVertices(Direction direction, String... labels) {
        return null;
    }
    @Override
    public VertexQuery query() {
        return null;
    }
    @Override
    public Edge addEdge(String label, Vertex inVertex) {
        return graph.addEdge(null, this, inVertex, label);
    }
    // =================================
    void addOutEdge(long edgeId) {
//        qOutEdges.add(edgeId);
    }
    // =================================
    void addInEdge(long edgeId) {
//        qInEdges.add(edgeId);
    }
    // =================================
    @Override
    public String toString() {
        return StringFactory.vertexString(this);
    }
    // =================================
    @ToString(callSuper = true)
    public static class XVertex extends XElement {
        private XVertex() { // no-arg ctor for jackson-json rehydration
            this(-1L);
        }
        public XVertex(long id) {
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
