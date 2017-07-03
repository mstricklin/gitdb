package java.edu.utexas.arlut.ciads;


/***************************************************************************************************
 * Copyright (c) 2014 Rüdiger Herrmann
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Rüdiger Herrmann - initial API and implementation
 **************************************************************************************************/

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TreeFormatter;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitInternalsLearningTest {

    /*
    Ref HEAD = repository.getRef("refs/heads/master");
    RevWalk walk = new RevWalk(repository);

    RevWalk walk = new RevWalk(repository);
    RevCommit commit = walk.parseCommit(objectIdOfCommit);

    RevWalk walk = new RevWalk(repository);
    RevTag tag = walk.parseTag(objectIdOfTag);

    RevWalk walk = new RevWalk(repository);
    RevTree tree = walk.parseTree(objectIdOfTree);
*/

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private Git git;
    private Repository repository;

    @Test
    public void testCreateBlob() throws IOException {
        String helloWorld = "Hello World!";
        ObjectId blobId = insertObject(Constants.OBJ_BLOB, helloWorld.getBytes("utf-8"));

        ObjectLoader objectLoader = loadObject(blobId);
        assertEquals(Constants.OBJ_BLOB, objectLoader.getType());
        assertEquals(helloWorld, new String(objectLoader.getBytes(), "utf-8"));
    }

    @Test
    public void testCreateTree() throws IOException {
        ObjectId blobId = insertObject(Constants.OBJ_BLOB, "Hello World!".getBytes("utf-8"));
        TreeFormatter treeFormatter = new TreeFormatter();
        treeFormatter.append("hello-world.txt", FileMode.REGULAR_FILE, blobId);
        ObjectId treeId = insertObject(Constants.OBJ_TREE, treeFormatter.toByteArray());

        assertEquals(Constants.OBJ_TREE, loadObject(treeId).getType());
        assertArrayEquals(new String[] {"hello-world.txt"}, listPathNames(treeId));
    }

    @Test
    public void testCreateCommit() throws IOException {
        TreeFormatter treeFormatter = new TreeFormatter();
        ObjectId treeId = insertObject(Constants.OBJ_TREE, treeFormatter.toByteArray());
        CommitBuilder commitBuilder = new CommitBuilder();
        commitBuilder.setTreeId(treeId);
        commitBuilder.setMessage("My first commit!");
        PersonIdent personIdent = new PersonIdent("me", "me@example.com");
        commitBuilder.setAuthor(personIdent);
        commitBuilder.setCommitter(personIdent);
        ObjectId commitId = insertObject(Constants.OBJ_COMMIT, commitBuilder.build());

        RevCommit commit = RevCommit.parse(loadObject(commitId).getBytes());
        assertEquals(commitBuilder.getMessage(), commit.getFullMessage());
        assertEquals(personIdent, commit.getAuthorIdent());
        assertEquals(personIdent, commit.getCommitterIdent());
        assertEquals(treeId, commit.getTree().getId());
        assertEquals(0, commit.getParentCount());
    }

    @Test
    public void testCreateSubTree() throws IOException {
        ObjectId blobId = insertObject(Constants.OBJ_BLOB, "content".getBytes("utf-8"));
        TreeFormatter subTreeFormatter = new TreeFormatter();
        subTreeFormatter.append("file.txt", FileMode.REGULAR_FILE, blobId);
        ObjectId subTreeId = insertObject(Constants.OBJ_TREE, subTreeFormatter.toByteArray());
        TreeFormatter treeFormatter = new TreeFormatter();
        treeFormatter.append("folder", FileMode.TREE, subTreeId);
        ObjectId treeId = insertObject(Constants.OBJ_TREE, treeFormatter.toByteArray());

        assertArrayEquals(new String[] {"folder/file.txt", "folder"}, listPathNames(treeId));
    }

    @Before
    public void setUp() throws GitAPIException {
        git = Git.init().setDirectory(tempFolder.getRoot()).call();
        repository = git.getRepository();
    }

    @After
    public void tearDown() {
        repository.close();
    }

    private ObjectId insertObject(int type, byte[] content) throws IOException {
//        try (ObjectInserter objectInserter = repository.newObjectInserter()) {
//            ObjectId result = objectInserter.insert(type, content);
//            objectInserter.flush();
//            return result;
//        }

        ObjectInserter objectInserter = repository.newObjectInserter();
        ObjectId result = objectInserter.insert(type, content);
        objectInserter.flush();
        objectInserter.release();
        return result;
    }

    private ObjectLoader loadObject(ObjectId objectId) throws IOException {
//        try (ObjectReader objectReader = repository.newObjectReader()) {
//            ObjectLoader result = objectReader.open(objectId);
//            return result;
//        }
        ObjectReader objectReader = repository.newObjectReader();
        ObjectLoader result = objectReader.open(objectId);
        objectReader.release();
        return result;
    }

    private String[] listPathNames(ObjectId treeId) throws IOException {
        Collection<String> pathNames = new ArrayList<>();
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.setRecursive(true);
        treeWalk.setPostOrderTraversal(true);
        treeWalk.addTree(treeId);
        while (treeWalk.next()) {
        }
        pathNames.add(treeWalk.getPathString());
        treeWalk.release();
        return (String[])pathNames.toArray(new String[pathNames.size()]);
    }

//        try (TreeWalk treeWalk = new TreeWalk(repository)) {
//            treeWalk.setRecursive(true);
//            treeWalk.setPostOrderTraversal(true);
//            treeWalk.addTree(treeId);
//            while (treeWalk.next()) {
//                pathNames.add(treeWalk.getPathString());
//            }
//            return (String[])pathNames.toArray(new String[pathNames.size()]);
//        }
}
