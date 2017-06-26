package edu.utexas.arlut.ciads;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;

import static org.eclipse.jgit.lib.Constants.OBJ_BLOB;

public class Examples {

//    http://www.codeaffine.com/2014/10/20/git-internals/
    // write an object
//    ObjectInserter objectInserter = repo.newObjectInserter();
//    byte[] bytes = "Shazam".getBytes( "utf-8" );
//    ObjectId oid = objectInserter.insert( OBJ_BLOB, bytes );
//    objectInserter.flush();
//
    // read an object
//    ObjectReader objectReader = repository.newObjectReader();
//    ObjectLoader objectLoader = objectReader.open( blobId );
//    int type = objectLoader.getType(); // Constants.OBJ_BLOB
//    byte[] bytes = objectLoader.getBytes();
//    String helloWorld = new String( bytes, "utf-8" ) // Hello World!

    // write tree
//    TreeFormatter treeFormatter = new TreeFormatter();
//    treeFormatter.append( "hello-world.txt", FileMode.REGULAR_FILE, blobId );
//    ObjectId treeId = objectInserter.insert( treeFormatter );
//    objectInserter.flush();

}
