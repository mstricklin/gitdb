// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb.repository;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;

public class Tree {

    public Tree(final ObjectId oid) {

    }

    void add(final String name, final ObjectId oid) {
        tree.put(name, oid);
    }
    ObjectId lookup(final String name) {
        return ObjectId.zeroId();
    }

    Map<String, ObjectId> tree = newHashMap();
}
