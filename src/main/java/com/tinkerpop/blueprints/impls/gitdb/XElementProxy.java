// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.util.ElementHelper;
import lombok.ToString;

public abstract class XElementProxy implements Element, Keyed {
    XElementProxy(long id_, final GitGraph graph_) {
        id = id_;
        graph = graph_;
    }
    @Override
    public <T> T getProperty(String key) {
        return null;
    }
    @Override
    public Set<String> getPropertyKeys() {
        // get mutable
        return null;
    }
    @Override
    public void setProperty(String key, Object value) {

    }
    @Override
    public <T> T removeProperty(String key) {
        return null;
    }
    @Override
    public void remove() {

    }
    @Override
    public Object getId() {
        return id;
    }
    @Override
    public long key() {
        return id;
    }
    @Override
    public boolean equals(final Object object) {
        return ElementHelper.areEqual(this, object);
    }
    @Override
    public int hashCode() {
        return (int)id;
    }
    // =================================
    @ToString
    public static abstract class XElement implements Keyed {
        XElement(final XElement ye) {
            this.id = ye.id;
            this.properties = newHashMap(ye.properties);
        }
        XElement(long id) {
            this.id = id;
            this.properties = newHashMap();
        }
        @Override
        public long key() {
            return id;
        }

        private final long id;
        private final Map<String, Object> properties;
    }
    // =================================
    protected final long id;
    protected final GitGraph graph;
}
