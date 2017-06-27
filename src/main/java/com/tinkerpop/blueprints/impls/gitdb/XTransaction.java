// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.tinkerpop.blueprints.impls.gitdb.StringUtil.vertexKey;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.impls.gitdb.XVertexProxy.XVertex;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XTransaction {
    XTransaction(String baselineOID) {
        baseline = baselineOID;
    }

    void clear() {
        addedVertices.clear();
        deletedVertices.clear();
    }

    // =================================
    XVertex addVertex(final XVertex v) {
        addedVertices.put(v.key(), v);
        return v;
    }
    void removeVertex(final XVertex v) {
        addedVertices.remove(v.key());
        deletedVertices.add(v.key());
    }
    XVertex getVertex(long id) throws XCache.NotFoundException {
        if (deletedVertices.contains(id)) {
            return null;
        }
        XVertex v = addedVertices.get(id);
        if (null == v) {
            // pull from baseline cache
        }
        return v;
    }
    Iterator<Long> getVertices() {
        return addedVertices.keySet().iterator();
    }
    // =================================
    void dump() {
        log.info("Transaction (baseline {})", baseline);
        log.info("Added vertices");
        for (Map.Entry<Long, XVertex> e : addedVertices.entrySet()) {
            log.info("{} => {}", e.getKey(), e.getValue());
        }
    }
    // =================================
    private Map<Long, XVertex> addedVertices = newHashMap();
    private Set<Long> deletedVertices = newHashSet();
    private final String baseline;
}
