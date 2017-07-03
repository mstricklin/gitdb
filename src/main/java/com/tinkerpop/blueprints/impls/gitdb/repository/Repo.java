/*
 * // CLASSIFICATION NOTICE: This file is UNCLASSIFIED
 */

package com.tinkerpop.blueprints.impls.gitdb.repository;

import com.tinkerpop.blueprints.Vertex;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import static org.eclipse.jgit.lib.Constants.OBJ_BLOB;

@Slf4j
public class Repo {
    public Repo() throws GitAPIException {
        File gitDBDir = new File("gitdb.git");

        Git git = Git.init().setBare(true).setDirectory(gitDBDir).call();
        repo = git.getRepository();
    }
    public ObjectId insertBlob(byte[] b) throws IOException {
        return insertObject(OBJ_BLOB, b);
    }

    public ObjectId insertObject(int type, byte[] b) throws IOException {
        try (ReleasingObjectInserter oi = tlObjectInserter.get()) {
            ObjectId oid = oi.insert(type, b);
            log.info("saveBlob oid {}", oid.getName());
            return oid;
        }
    }
    public byte[] readBlob(ObjectId oid) throws IOException {
        return readObject(OBJ_BLOB, oid);
    }
    public byte[] readObject(int type, ObjectId oid) throws IOException {
        try (ReleasingObjectReader or = tlObjectReader.get()) {
            ObjectLoader ol = or.read(oid);
            assert(ol.getType() == type);
            return ol.getBytes();
        }
    }

    final Repository repo;

    // =================================
    private ThreadLocal<ReleasingObjectInserter> tlObjectInserter = new ThreadLocal<ReleasingObjectInserter>() {
        @Override protected ReleasingObjectInserter initialValue() {
            return new ReleasingObjectInserter( repo.newObjectInserter() );
        }
    };
    private static class ReleasingObjectInserter implements Closeable {
        ReleasingObjectInserter(ObjectInserter oi_) {
            oi = oi_;
        }
        ObjectId insert(int type, byte[] content) throws IOException {
            return oi.insert(type, content);
        }

        @Override
        public void close() throws IOException {
            oi.flush();
            oi.release();
        }
        ObjectInserter oi;
    }
    // =================================
    private ThreadLocal<ReleasingObjectReader> tlObjectReader = new ThreadLocal<ReleasingObjectReader>() {
        @Override protected ReleasingObjectReader initialValue() {
            return new ReleasingObjectReader( repo.newObjectReader() );
        }
    };
    private static class ReleasingObjectReader implements Closeable {
        ReleasingObjectReader(ObjectReader or_) {
            or = or_;
        }
        ObjectLoader read(ObjectId oid) throws IOException {
            return or.open(oid);
        }

        @Override
        public void close() throws IOException {
            or.release();
        }
        ObjectReader or;
    }
}
