/*
 * // CLASSIFICATION NOTICE: This file is UNCLASSIFIED
 */

package edu.utexas.arlut.ciads;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkerpop.blueprints.impls.gitdb.XVertexProxy;
import com.tinkerpop.blueprints.impls.gitdb.repository.Repo;
import com.tinkerpop.blueprints.impls.gitdb.repository.Serializer;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class Example2 {

    public static void foo() throws IOException, GitAPIException {
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
        XVertexProxy.XVertex xv = new XVertexProxy.XVertex(1);
        log.info("new XVertex {}", xv);
        final String serXV = ser.serialize(xv);
        log.info("Serialized {}", ser.serialize(xv));

        Repo repo = new Repo();
//        ObjectId oid = ObjectId.fromString("3a3c883ced54f2c7e8f7a1aa51e486e60d9ec0aa");
        ObjectId oid = repo.insertBlob(serXV.getBytes());
        log.info("OID: {}", oid);

        String ds = new String(repo.readBlob(oid));
        log.info("reloaded object {}", ds);
        XVertexProxy.XVertex xv0 = ser.deserialize(ds, XVertexProxy.XVertex.class);
        log.info("reconstituted {}", xv0);
//        Examples e = new Examples();
//        e.foo();

        // b40f66a0fbadf7a47c4883537eb409ca8702685c
//        byte[]

        log.info("=== TreeWalk ====");
        final ObjectId o0 = ObjectId.fromString("2c1ae1e602faa529b36d0e2cf42c810061baa8af");
        final ObjectId o1 = ObjectId.fromString("3c5fd00b20adac83e4c47895ae8d9fd6cd0e779d");
        final ObjectId o2 = ObjectId.fromString("cdf3250ebe10437f02eb9c118075042f48b479db");


        TreeWalk tw = new TreeWalk(repo.getRepo());
        tw.setRecursive(false);
        tw.reset(o2);
        log.info("getTreeCount {}", tw.getTreeCount());
        while (tw.next()) {
            log.info("\trawPath {} {}", tw.getPathString(), tw.getObjectId(0));
        }
        tw.release();

        ObjectId head = ObjectId.fromString("61ac5cc1bd4788ff159c7f5bad5e4734b00f083c");


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

        Map<String, Ref> refs = repo.getRepo().getAllRefs();
        for (Map.Entry<String, Ref> e: refs.entrySet()) {
            log.info("{} => {}", e.getKey(), e.getValue());
        }

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
