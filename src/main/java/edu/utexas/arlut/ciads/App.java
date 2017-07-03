package edu.utexas.arlut.ciads;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.gitdb.GitGraph;
import com.tinkerpop.blueprints.impls.gitdb.XVertexProxy;
import com.tinkerpop.blueprints.impls.gitdb.repository.Repo;
import com.tinkerpop.blueprints.impls.gitdb.repository.Serializer;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;

import java.io.*;

import static com.tinkerpop.blueprints.impls.gitdb.XVertexProxy.*;
import static org.eclipse.jgit.lib.Constants.*;

@Slf4j
public class App {
    public static void main(String[] args) throws IOException, IllegalStateException, GitAPIException {
        log.info(" Foo! ");
//        GitGraph g = GitGraph.of();
//
//        g.addVertex(null);
//        g.addVertex(null);
//        g.addVertex(null);
//        g.txDump();
//        Vertex v0 = g.getVertex(1L);
//        log.info("found vertex {}: {}", v0.getId(), v0);
//        g.removeVertex(v0);
//
//        g.txDump();
//        log.info("===========");
//        for (Vertex v: g.getVertices()) {
//           log.info("{} => {}", v.getId(), v);
//        }
        Foo f = new Foo("aa", "bb", 7);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String jsonInString = mapper.writeValueAsString(f);
        Foo f0 = mapper.readValue(jsonInString, Foo.class);

        log.info("f   to String {}", f);
        log.info("Foo to JSON   {}", jsonInString);
        log.info("Foo from JSON {}", f0);

        Serializer ser = Serializer.of();
        XVertex xv = new XVertex(1L);
        log.info("new XVertex {}", xv);
        final String serXV = ser.serialize(xv);
        log.info("Serialized {}", ser.serialize(xv));

        Repo r = new Repo();
        ObjectId oid = ObjectId.fromString("3a3c883ced54f2c7e8f7a1aa51e486e60d9ec0aa");
//        ObjectId oid = r.insertBlob(serXV.getBytes());
        log.info("OID: {}", oid);

        String ds = new String(r.readBlob(oid));
        log.info("reloaded object {}", ds);
        XVertex xv0 = ser.deserialize(ds, XVertex.class);
        log.info("reconstituted {}", xv0);
//        Examples e = new Examples();
//        e.foo();


    }

    @ToString
    static class Foo {
        Foo() {

        }
        Foo(final String aa_, final String bb_, int i_) {
            aa = aa_; bb = bb_; i = i_;
        }
        String aa;
        String bb;
        int i;
    }
}
