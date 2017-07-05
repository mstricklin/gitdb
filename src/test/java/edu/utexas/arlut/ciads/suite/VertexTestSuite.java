package edu.utexas.arlut.ciads.suite;

import com.google.common.base.Stopwatch;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.impls.GraphTest;
import com.tinkerpop.blueprints.impls.gitdb.GitGraph;
import com.tinkerpop.blueprints.util.StringFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.rules.TestName;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static com.tinkerpop.blueprints.Direction.BOTH;
import static com.tinkerpop.blueprints.Direction.IN;
import static com.tinkerpop.blueprints.Direction.OUT;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
public class VertexTestSuite extends TestSuite {


    @Test
    public void testVertexEquality() {
        if (!graph.getFeatures().ignoresSuppliedIds) {
            Vertex v = graph.addVertex(convertId("1"));
            Vertex u = graph.getVertex(convertId("1"));
            Assert.assertEquals(v, u);
        }

        sw.reset();

        resetAndStart();
        Vertex v = graph.addVertex(null);
        Assert.assertNotNull(v);
        Vertex u = graph.getVertex(v.getId());
        Assert.assertNotNull(u);
        Assert.assertEquals(v, u);
        printPerformance(graph.toString(), 1, "vertex added and retrieved");

        Assert.assertEquals(graph.getVertex(u.getId()), graph.getVertex(u.getId()));
        Assert.assertEquals(graph.getVertex(v.getId()), graph.getVertex(u.getId()));
        Assert.assertEquals(graph.getVertex(v.getId()), graph.getVertex(v.getId()));
    }

    @Test
    public void testVertexEqualityForSuppliedIdsAndHashCode() {
        if (!graph.getFeatures().ignoresSuppliedIds) {

            Vertex v = graph.addVertex(convertId("1"));
            Vertex u = graph.getVertex(convertId("1"));
            Set<Vertex> set = new HashSet<Vertex>();
            set.add(v);
            set.add(v);
            set.add(u);
            set.add(u);
            set.add(graph.getVertex(convertId("1")));
            set.add(graph.getVertex(convertId("1")));
            if (graph.getFeatures().supportsVertexIndex)
                set.add(graph.getVertices().iterator().next());
            Assert.assertEquals(1, set.size());
            Assert.assertEquals(v.hashCode(), u.hashCode());
        }
    }

    @Test
    public void testBasicAddVertex() {
        if (graph.getFeatures().supportsVertexIteration) {
            graph.addVertex(convertId("1"));
            graph.addVertex(convertId("2"));
            Assert.assertEquals(2, size(graph.getVertices()));
            graph.addVertex(convertId("3"));
            Assert.assertEquals(3, size(graph.getVertices()));
        }
    }
    @Test
    public void testGetVertexWithNull() {
        try {
            graph.getVertex(null);
            Assert.assertFalse(true);
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }
    @Test
    public void testRemoveVertex() {

        Vertex v1 = graph.addVertex(convertId("1"));
        if (!graph.getFeatures().ignoresSuppliedIds)
            Assert.assertEquals(graph.getVertex(convertId("1")), v1);

        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(1, size(graph.getVertices()));
        if (graph.getFeatures().supportsEdgeIteration)
            Assert.assertEquals(0, size(graph.getEdges()));

        graph.removeVertex(v1);
        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(0, size(graph.getVertices()));
        if (graph.getFeatures().supportsEdgeIteration)
            Assert.assertEquals(0, size(graph.getEdges()));

        Set<Vertex> vertices = new HashSet<Vertex>();
        for (int i = 0; i < 100; i++) {
            vertices.add(graph.addVertex(null));
        }
        Assert.assertEquals(vertices.size(), 100);
        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(100, size(graph.getVertices()));
        if (graph.getFeatures().supportsEdgeIteration)
            Assert.assertEquals(0, size(graph.getEdges()));

        for (Vertex v : vertices) {
            graph.removeVertex(v);
        }
        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(0, size(graph.getVertices()));
        if (graph.getFeatures().supportsEdgeIteration)
            Assert.assertEquals(0, size(graph.getEdges()));

    }
    @Test
    public void testRemoveVertexWithEdges() {
        Vertex v1 = graph.addVertex(convertId("1"));
        Vertex v2 = graph.addVertex(convertId("2"));
        graph.addEdge(null, v1, v2, convertLabel("knows"));
        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(2, size(graph.getVertices()));
        if (graph.getFeatures().supportsEdgeIteration)
            Assert.assertEquals(1, size(graph.getEdges()));

        graph.removeVertex(v1);
        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(1, size(graph.getVertices()));
        if (graph.getFeatures().supportsEdgeIteration)
            Assert.assertEquals(0, size(graph.getEdges()));

        graph.removeVertex(v2);
        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(0, size(graph.getVertices()));
        if (graph.getFeatures().supportsEdgeIteration)
            Assert.assertEquals(0, size(graph.getEdges()));

    }
    @Test
    public void testGetNonExistantVertices() {
        Assert.assertNull(graph.getVertex("asbv"));
        Assert.assertNull(graph.getVertex(12.0d));
    }
    @Test
    public void testRemoveVertexNullId() {

        Vertex v1 = graph.addVertex(null);
        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(1, size(graph.getVertices()));
        graph.removeVertex(v1);
        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(0, size(graph.getVertices()));

        Set<Vertex> vertices = new HashSet<Vertex>();

        resetAndStart();
        int vertexCount = 100;
        for (int i = 0; i < vertexCount; i++) {
            vertices.add(graph.addVertex(null));
        }
        printPerformance(graph.toString(), vertexCount, "vertices added");
        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(vertexCount, size(graph.getVertices()));

        resetAndStart();
        for (Vertex v : vertices) {
            graph.removeVertex(v);
        }
        printPerformance(graph.toString(), vertexCount, "vertices removed");
        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(0, size(graph.getVertices()));
    }
    @Test
    public void testVertexIterator() {
        if (graph.getFeatures().supportsVertexIteration) {
            resetAndStart();
            int vertexCount = 1000;
            Set<Integer> ids = newHashSetWithExpectedSize(1000);
            for (int i = 0; i < vertexCount; i++) {
                ids.add((Integer)graph.addVertex(null).getId());
            }
            printPerformance(graph.toString(), vertexCount, "vertices added");
            resetAndStart();
            Assert.assertEquals(vertexCount, size(graph.getVertices()));
            printPerformance(graph.toString(), vertexCount, "vertices counted");
            // must create unique ids
            Assert.assertEquals(vertexCount, ids.size());
        }
    }
    @Test
    public void testLegalVertexEdgeIterables() {
        Vertex v1 = graph.addVertex(null);
        for (int i = 0; i < 10; i++) {
            graph.addEdge(null, v1, graph.addVertex(null), convertLabel("knows"));
        }
        Iterable<Edge> edges = v1.getEdges(Direction.OUT, convertLabel("knows"));
        Assert.assertEquals(size(edges), 10);
        Assert.assertEquals(size(edges), 10);
        Assert.assertEquals(size(edges), 10);
    }
    @Test
    public void testAddVertexProperties() {
        if (graph.getFeatures().supportsVertexProperties) {
            Vertex v1 = graph.addVertex(convertId("1"));
            Vertex v2 = graph.addVertex(convertId("2"));

            if (graph.getFeatures().supportsStringProperty) {
                v1.setProperty("key1", "value1");
                Assert.assertEquals("value1", v1.getProperty("key1"));
            }

            if (graph.getFeatures().supportsIntegerProperty) {
                v1.setProperty("key2", 10);
                v2.setProperty("key2", 20);

                Assert.assertEquals(10, v1.getProperty("key2"));
                Assert.assertEquals(20, v2.getProperty("key2"));
            }

        }
    }

    @Test
    public void testAddManyVertexProperties() {
        if (graph.getFeatures().supportsVertexProperties && graph.getFeatures().supportsStringProperty) {
            Set<Vertex> vertices = newHashSet();
            resetAndStart();
            for (int i = 0; i < 50; i++) {
                Vertex vertex = graph.addVertex(null);
                for (int j = 0; j < 15; j++) {
                    vertex.setProperty(UUID.randomUUID().toString(), UUID.randomUUID().toString());
                }
                vertices.add(vertex);
            }
            printPerformance(graph.toString(), 15 * 50, "vertex properties added (with vertices being added too)");

            if (graph.getFeatures().supportsVertexIteration)
                Assert.assertEquals(50, size(graph.getVertices()));
            Assert.assertEquals(50, vertices.size());
            for (Vertex vertex : vertices) {
                Assert.assertEquals(15, vertex.getPropertyKeys().size());
            }
        }
    }

    @Test
    public void testRemoveVertexProperties() {
        if (graph.getFeatures().supportsVertexProperties) {

            Vertex v1 = graph.addVertex("1");
            Vertex v2 = graph.addVertex("2");

            Assert.assertNull(v1.removeProperty("key1"));
            Assert.assertNull(v1.removeProperty("key2"));
            Assert.assertNull(v2.removeProperty("key2"));

            if (graph.getFeatures().supportsStringProperty) {
                v1.setProperty("key1", "value1");
                Assert.assertEquals("value1", v1.removeProperty("key1"));
            }

            if (graph.getFeatures().supportsIntegerProperty) {
                v1.setProperty("key2", 10);
                v2.setProperty("key2", 20);

                Assert.assertEquals(10, v1.removeProperty("key2"));
                Assert.assertEquals(20, v2.removeProperty("key2"));
            }

            Assert.assertNull(v1.removeProperty("key1"));
            Assert.assertNull(v1.removeProperty("key2"));
            Assert.assertNull(v2.removeProperty("key2"));

            if (graph.getFeatures().supportsStringProperty) {
                v1.setProperty("key1", "value1");
            }

            if (graph.getFeatures().supportsIntegerProperty) {
                v1.setProperty("key2", 10);
                v2.setProperty("key2", 20);
            }

            if (!graph.getFeatures().ignoresSuppliedIds) {
                v1 = graph.getVertex("1");
                v2 = graph.getVertex("2");

                if (graph.getFeatures().supportsStringProperty) {
                    Assert.assertEquals("value1", v1.removeProperty("key1"));
                }

                if (graph.getFeatures().supportsIntegerProperty) {
                    Assert.assertEquals(10, v1.removeProperty("key2"));
                    Assert.assertEquals(20, v2.removeProperty("key2"));
                }

                Assert.assertNull(v1.removeProperty("key1"));
                Assert.assertNull(v1.removeProperty("key2"));
                Assert.assertNull(v2.removeProperty("key2"));

                v1 = graph.getVertex("1");
                v2 = graph.getVertex("2");

                if (graph.getFeatures().supportsStringProperty) {
                    v1.setProperty("key1", "value2");
                    Assert.assertEquals("value2", v1.removeProperty("key1"));
                }

                if (graph.getFeatures().supportsIntegerProperty) {
                    v1.setProperty("key2", 20);
                    v2.setProperty("key2", 30);

                    Assert.assertEquals(20, v1.removeProperty("key2"));
                    Assert.assertEquals(30, v2.removeProperty("key2"));
                }

                Assert.assertNull(v1.removeProperty("key1"));
                Assert.assertNull(v1.removeProperty("key2"));
                Assert.assertNull(v2.removeProperty("key2"));
            }
        }
    }

    @Test
    public void testAddingIdProperty() {
        if (graph.getFeatures().supportsVertexProperties) {
            Vertex vertex = graph.addVertex(null);
            try {
                vertex.setProperty("id", "123");
                Assert.assertTrue(false);
            } catch (IllegalArgumentException e) {
                Assert.assertTrue(true);
            }
        }
    }


    @Test
    public void testNoConcurrentModificationException() {
        if (graph.getFeatures().supportsVertexIteration) {

            for (int i = 0; i < 25; i++) {
                graph.addVertex(null);
            }
            Assert.assertEquals(size(graph.getVertices()), 25);
            for (final Vertex vertex : graph.getVertices()) {
                graph.removeVertex(vertex);
            }
            Assert.assertEquals(size(graph.getVertices()), 0);
        }
    }

    @Test
    public void testGettingEdgesAndVertices() {
        Vertex a = graph.addVertex(null);
        Vertex b = graph.addVertex(null);
        Vertex c = graph.addVertex(null);
        Edge w = graph.addEdge(null, a, b, convertLabel("knows"));
        Edge x = graph.addEdge(null, b, c, convertLabel("knows"));
        Edge y = graph.addEdge(null, a, c, convertLabel("hates"));
        Edge z = graph.addEdge(null, a, b, convertLabel("hates"));
        Edge zz = graph.addEdge(null, c, c, convertLabel("hates"));

        Assert.assertEquals(size(a.getEdges(OUT)), 3);
        Assert.assertEquals(size(a.getEdges(OUT, convertLabel("hates"))), 2);
        Assert.assertEquals(size(a.getEdges(OUT, convertLabel("knows"))), 1);
        Assert.assertEquals(size(a.getVertices(OUT)), 3);
        Assert.assertEquals(size(a.getVertices(OUT, convertLabel("hates"))), 2);
        Assert.assertEquals(size(a.getVertices(OUT, convertLabel("knows"))), 1);
        Assert.assertEquals(size(a.getVertices(BOTH)), 3);
        Assert.assertEquals(size(a.getVertices(BOTH, convertLabel("hates"))), 2);
        Assert.assertEquals(size(a.getVertices(BOTH, convertLabel("knows"))), 1);

        Assert.assertTrue(newArrayList(a.getEdges(OUT)).contains(w));
        Assert.assertTrue(newArrayList(a.getEdges(OUT)).contains(y));
        Assert.assertTrue(newArrayList(a.getEdges(OUT)).contains(z));
        Assert.assertTrue(newArrayList(a.getVertices(OUT)).contains(b));
        Assert.assertTrue(newArrayList(a.getVertices(OUT)).contains(c));

        Assert.assertTrue(newArrayList(a.getEdges(OUT, convertLabel("knows"))).contains(w));
        Assert.assertFalse(newArrayList(a.getEdges(OUT, convertLabel("knows"))).contains(y));
        Assert.assertFalse(newArrayList(a.getEdges(OUT, convertLabel("knows"))).contains(z));
        Assert.assertTrue(newArrayList(a.getVertices(OUT, convertLabel("knows"))).contains(b));
        Assert.assertFalse(newArrayList(a.getVertices(OUT, convertLabel("knows"))).contains(c));

        Assert.assertFalse(newArrayList(a.getEdges(OUT, convertLabel("hates"))).contains(w));
        Assert.assertTrue(newArrayList(a.getEdges(OUT, convertLabel("hates"))).contains(y));
        Assert.assertTrue(newArrayList(a.getEdges(OUT, convertLabel("hates"))).contains(z));
        Assert.assertTrue(newArrayList(a.getVertices(OUT, convertLabel("hates"))).contains(b));
        Assert.assertTrue(newArrayList(a.getVertices(OUT, convertLabel("hates"))).contains(c));

        Assert.assertEquals(size(a.getVertices(IN)), 0);
        Assert.assertEquals(size(a.getVertices(IN, convertLabel("knows"))), 0);
        Assert.assertEquals(size(a.getVertices(IN, convertLabel("hates"))), 0);
        Assert.assertTrue(newArrayList(a.getEdges(OUT)).contains(w));
        Assert.assertTrue(newArrayList(a.getEdges(OUT)).contains(y));
        Assert.assertTrue(newArrayList(a.getEdges(OUT)).contains(z));

        Assert.assertEquals(size(b.getEdges(BOTH)), 3);
        Assert.assertEquals(size(b.getEdges(BOTH, convertLabel("knows"))), 2);
        Assert.assertTrue(newArrayList(b.getEdges(BOTH, convertLabel("knows"))).contains(x));
        Assert.assertTrue(newArrayList(b.getEdges(BOTH, convertLabel("knows"))).contains(w));
        Assert.assertTrue(newArrayList(b.getVertices(BOTH, convertLabel("knows"))).contains(a));
        Assert.assertTrue(newArrayList(b.getVertices(BOTH, convertLabel("knows"))).contains(c));

        Assert.assertEquals(size(c.getEdges(BOTH, convertLabel("hates"))), 3);
        Assert.assertEquals(size(c.getVertices(BOTH, convertLabel("hates"))), 3);
        Assert.assertEquals(size(c.getEdges(BOTH, convertLabel("knows"))), 1);
        Assert.assertTrue(newArrayList(c.getEdges(BOTH, convertLabel("hates"))).contains(y));
        Assert.assertTrue(newArrayList(c.getEdges(BOTH, convertLabel("hates"))).contains(zz));
        Assert.assertTrue(newArrayList(c.getVertices(BOTH, convertLabel("hates"))).contains(a));
        Assert.assertTrue(newArrayList(c.getVertices(BOTH, convertLabel("hates"))).contains(c));
        Assert.assertEquals(size(c.getEdges(IN, convertLabel("hates"))), 2);
        Assert.assertEquals(size(c.getEdges(OUT, convertLabel("hates"))), 1);

        try {
            x.getVertex(BOTH);
            Assert.fail("Getting edge vertex with direction BOTH should fail");
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            Assert.fail("Getting edge vertex with direction BOTH should should throw " +
                    IllegalArgumentException.class.getSimpleName());
        }

    }

    @Test
    public void testEmptyKeyProperty() {

        // no point in testing graph features for setting string properties because the intent is for it to
        // fail based on the empty key.
        if (graph.getFeatures().supportsVertexProperties) {
            final Vertex v = graph.addVertex(null);
            try {
                v.setProperty("", "value");
                Assert.fail("Setting a vertex property with an empty string key should fail");
            } catch (IllegalArgumentException e) {
            }
        }
    }

    @Test
    public void testVertexCentricLinking() {

        final Vertex v = graph.addVertex(null);
        final Vertex a = graph.addVertex(null);
        final Vertex b = graph.addVertex(null);

        v.addEdge(convertLabel("knows"), a);
        v.addEdge(convertLabel("knows"), b);

        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(size(graph.getVertices()), 3);
        if (graph.getFeatures().supportsEdgeIteration)
            Assert.assertEquals(size(graph.getEdges()), 2);

        Assert.assertEquals(size(v.getEdges(OUT, convertLabel("knows"))), 2);
        Assert.assertEquals(size(a.getEdges(OUT, convertLabel("knows"))), 0);
        Assert.assertEquals(size(a.getEdges(IN, convertLabel("knows"))), 1);

        Assert.assertEquals(size(b.getEdges(OUT, convertLabel("knows"))), 0);
        Assert.assertEquals(size(b.getEdges(IN, convertLabel("knows"))), 1);

    }

    @Test
    public void testVertexCentricRemoving() {

        final Vertex a = graph.addVertex(null);
        final Vertex b = graph.addVertex(null);
        final Vertex c = graph.addVertex(null);

        Object cId = c.getId();

        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(size(graph.getVertices()), 3);

        a.remove();
        b.remove();

        Assert.assertNotNull(graph.getVertex(cId));

        if (graph.getFeatures().supportsVertexIteration)
            Assert.assertEquals(size(graph.getVertices()), 1);


    }

    @Test
    public void testConcurrentModificationOnProperties() {
        if (graph.getFeatures().supportsVertexProperties) {
            Vertex a = graph.addVertex(null);
            a.setProperty("test1", 1);
            a.setProperty("test2", 2);
            a.setProperty("test3", 3);
            a.setProperty("test4", 4);
            for (String key : a.getPropertyKeys()) {
                a.removeProperty(key);
            }
        }
    }

    @Test
    public void testSettingBadVertexProperties() {
        if (graph.getFeatures().supportsVertexProperties) {
            Vertex v = graph.addVertex(null);
            try {
                v.setProperty(null, -1);
                Assert.assertFalse(true);
            } catch (RuntimeException e) {
                Assert.assertTrue(true);
            }
            try {
                v.setProperty("", -1);
                Assert.assertFalse(true);
            } catch (RuntimeException e) {
                Assert.assertTrue(true);
            }
            try {
                v.setProperty(StringFactory.ID, -1);
                Assert.assertFalse(true);
            } catch (RuntimeException e) {
                Assert.assertTrue(true);
            }
            try {
                v.setProperty("good", null);
                Assert.assertFalse(true);
            } catch (RuntimeException e) {
                Assert.assertTrue(true);
            }
        }
    }
}
