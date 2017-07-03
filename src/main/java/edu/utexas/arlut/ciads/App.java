package edu.utexas.arlut.ciads;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.gitdb.GitGraph;
import com.tinkerpop.blueprints.impls.gitdb.XEdgeProxy;
import com.tinkerpop.blueprints.impls.gitdb.XVertexProxy;
import com.tinkerpop.blueprints.impls.gitdb.repository.Repo;
import com.tinkerpop.blueprints.impls.gitdb.repository.Serializer;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Tree;
import org.eclipse.jgit.notes.NoteMap;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.*;
import java.util.*;

import static cern.clhep.Units.s;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.tinkerpop.blueprints.impls.gitdb.XVertexProxy.XVertex;
import static com.tinkerpop.blueprints.impls.gitdb.XEdgeProxy.XEdge;
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
//        ObjectId oid = ObjectId.fromString("3a3c883ced54f2c7e8f7a1aa51e486e60d9ec0aa");
        ObjectId oid = r.insertBlob(serXV.getBytes());
        log.info("OID: {}", oid);

        String ds = new String(r.readBlob(oid));
        log.info("reloaded object {}", ds);
        XVertex xv0 = ser.deserialize(ds, XVertex.class);
        log.info("reconstituted {}", xv0);
//        Examples e = new Examples();
//        e.foo();

        // b40f66a0fbadf7a47c4883537eb409ca8702685c
//        byte[]

        log.info("=== TreeWalk ====");
        final ObjectId o0 = ObjectId.fromString("a0112068a16b203f59372821d75fff61f41df6a7");
        final ObjectId o1 = ObjectId.fromString("b40f66a0fbadf7a47c4883537eb409ca8702685c");
        final ObjectId o2 = ObjectId.fromString("0eb688e1dc87868973a36d503f1e065447225795");


        TreeWalk tw = new TreeWalk(r.getRepo());
        tw.setRecursive(false);
        tw.reset(o2);
        log.info("getTreeCount {}", tw.getTreeCount());
        while (tw.next()) {
            log.info("\trawPath {} {}", tw.getPathString(), tw.getObjectId(0));
        }
        tw.release();

//        RevWalk walk = new RevWalk(r.getRepo());
//        RevTree revTree = walk.parseTree(o0);
//        TreeWalk tw = TreeWalk.forPath(r.getRepo(), "/00/00/00", revTree);
//        while (tw.next()) {
//            log.info("\trawPath {}", tw.getPathString());
//        }
//        tw.release();

//        Collection<String> pathNames = newArrayList();
//        TreeWalk treeWalk = new TreeWalk(r.getRepo());
//        treeWalk.setRecursive(false);
//        treeWalk.setPostOrderTraversal(false);


//        NoteMap nm;
//        Tree t;
//        treeWalk.addTree(ObjectId.fromString(o0));
//        while (treeWalk.next()) {
//            log.info("isSubTree {}", treeWalk.isSubtree());
//
//            log.info("\trawPath {}", treeWalk.getPathString());
//        }
//        pathNames.add(treeWalk.getPathString());
//        treeWalk.release();
//        return (String[])pathNames.toArray(new String[pathNames.size()]);

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

    private static interface Lazy<T> {
        T get();
    }

    private Lazy<Map<Long, XVertex>> m0 = bar();
    private Lazy<Map<Long, XEdge>> m1 = bar();
    static int count = 0;
    private static <T> Lazy<Map<Long, T>> bar() {
        return new Lazy<Map<Long, T>>() {
            Map<Long, T> map = newHashMap();

            int instance = count++;
            {
                log.info("Lazy ctor {}", instance);
            }
            @Override
            public Map<Long, T> get() {
                log.info("Lazy.get {} {}", map.hashCode(), instance);
                return map;
            }
        };
    }
    private static <T> Lazy<Set<Long>> baz() {
        return new Lazy<Set<Long>>() {
            Set<Long> set = newHashSet();
            @Override
            public Set<Long> get() {
                return set;
            }
        };
    }
}
