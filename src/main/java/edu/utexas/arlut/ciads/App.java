package edu.utexas.arlut.ciads;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.FS;

import java.io.*;

import static org.eclipse.jgit.lib.Constants.*;

@Slf4j
public class App {
    public static void main(String[] args) throws IOException, IllegalStateException, GitAPIException {
        log.info(" Foo! ");



        File gitDBDir = new File("gitdb.git");

        Git git = Git.init().setBare(true).setDirectory( gitDBDir ).call();
        Repository repo = git.getRepository();



        // hash-object -w
        ObjectInserter objectInserter = repo.newObjectInserter();
        byte[] bytes = "Shazam".getBytes( "utf-8" );
        ObjectId oid = objectInserter.insert( OBJ_BLOB, bytes );
        objectInserter.flush();
        log.info("blob oid {}", oid.getName());

        TreeFormatter treeFormatter = new TreeFormatter();
        // update-index
        treeFormatter.append("shazam.txt", FileMode.REGULAR_FILE, oid);
        // ... append rest of files...?
        // write-tree
        ObjectId treeId = objectInserter.insert(treeFormatter);
        objectInserter.flush();

        log.info("treeId oid {}", treeId.getName());

        CommitBuilder commitBuilder = new CommitBuilder();
        commitBuilder.setTreeId( treeId );
        commitBuilder.setMessage( "Shazam commit!" );
        PersonIdent person = new PersonIdent( "me", "me@example.com" );
        commitBuilder.setAuthor( person );
        commitBuilder.setCommitter( person );
        ObjectId commitId = objectInserter.insert( commitBuilder );
        objectInserter.flush();
        log.info("commit oid {}", commitId.getName());


        RefUpdate refUpdate = repo.getRefDatabase().newUpdate(R_HEADS + MASTER, true);
        refUpdate.setForceUpdate(true);
        refUpdate.setNewObjectId(commitId);
        refUpdate.update();





//        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder()
//                .setMustExist(false)
//                .setBare()
//                .setGitDir( gitDBDir );
//        Repository repo = repositoryBuilder.build();
//
//        Git g = new Git( repo );
//
//        log.info("git dir {}", repositoryBuilder.getGitDir().getPath());
//        log.info("repository {}", repo);
//
//        Ref head = repo.exactRef("refs/heads/master");
//        log.info("Ref of refs/heads/master: {}", head);

        log.info("Git {}", git);
        log.info("Repo {}", git.getRepository().toString());

    }
}
