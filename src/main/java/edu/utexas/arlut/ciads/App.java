package edu.utexas.arlut.ciads;

import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.gitdb.GitGraph;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;
import java.util.*;

@Slf4j
public class App {
    public static void main(String[] args) throws IOException, IllegalStateException, GitAPIException {
        log.info(" Foo! ");

        GitGraph g = GitGraph.of();

        List<Vertex> vl = Lists.newArrayList(g.addVertex(null),
                g.addVertex(null),
                g.addVertex(null));
        for (Vertex v: vl)
            log.info("{}", v);
        g.txDump();

        Vertex v1 = g.getVertex(1);
        Vertex v2 = g.getVertex(2);
        Vertex v3 = g.getVertex(3);
        v1.setProperty("aaa", "000");
        g.addEdge(null, v1, v2, "foo");

        g.txDump();
        log.info("v0 keys {}", v1.getPropertyKeys());
        log.info("v0->(aaa) {}", v1.getProperty("aaa"));
        log.info("v0->(bbb) {}", v1.getProperty("bbb"));

        g.dump();
        g.removeVertex(v3);
        g.dump();



        log.info("=== add edge ====");
        v2.addEdge("FooEdge", v1);
        g.dump();


        log.info("v2 edges OUT  {}", v2.getEdges(Direction.OUT));
        log.info("v2 edges IN   {}", v2.getEdges(Direction.IN));
        log.info("v2 edges BOTH {}", v2.getEdges(Direction.BOTH));

//        log.info("=== remove v2 ====");
//        g.removeVertex(v2);


        log.info("=== self edge ====");
        v1.addEdge("self-edge", v1);
        g.dump();

        log.info("v1 edges OUT  {}", v1.getEdges(Direction.OUT));
        log.info("v1 edges IN   {}", v1.getEdges(Direction.IN));
        log.info("v1 edges BOTH {}", v1.getEdges(Direction.BOTH));

        log.info("=== remove self vertex====");
        g.removeVertex(v1);

//        log.info("===========");
//        for (Vertex v: g.getVertices()) {
//           log.info("{} => {}", v.getId(), v);
//        }
    }

}
