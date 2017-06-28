package edu.utexas.arlut.ciads;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.gitdb.GitGraph;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;

import static org.eclipse.jgit.lib.Constants.*;

@Slf4j
public class App {
    public static void main(String[] args) throws IOException, IllegalStateException, GitAPIException {
        log.info(" Foo! ");
        GitGraph g = GitGraph.of();

        g.addVertex(null);
        g.addVertex(null);
        g.addVertex(null);
        g.txDump();
        Vertex v0 = g.getVertex(1L);
        log.info("found vertex {}: {}", v0.getId(), v0);
        g.removeVertex(v0);

        g.txDump();
        log.info("===========");
        for (Vertex v: g.getVertices()) {
           log.info("{} => {}", v.getId(), v);
        }


    }
}
