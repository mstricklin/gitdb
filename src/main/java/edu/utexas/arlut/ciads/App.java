package edu.utexas.arlut.ciads;

import com.google.common.collect.Lists;
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
        v1.setProperty("aaa", "000");
        g.addEdge(null, v1, v2, "foo");

        g.txDump();
        log.info("v0 keys {}", v1.getPropertyKeys());
        log.info("v0->(aaa) {}", v1.getProperty("aaa"));
        log.info("v0->(bbb) {}", v1.getProperty("bbb"));

        for (Vertex v: g.getVertices())
            log.info("\t{}", v);
        g.removeVertex(v1);
        for (Vertex v: g.getVertices())
            log.info("\t{}", v);

//        log.info("===========");
//        for (Vertex v: g.getVertices()) {
//           log.info("{} => {}", v.getId(), v);
//        }
    }

}
