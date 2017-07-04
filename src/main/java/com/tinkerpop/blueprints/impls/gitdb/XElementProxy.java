// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.util.ElementHelper;
import lombok.ToString;

public abstract class XElementProxy implements Element, Keyed {
    XElementProxy(int id_, final GitGraph graph_) {
        id = id_;
        graph = graph_;
    }
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        return (T) getImpl().properties.get(key);
    }
    @Override
    public Set<String> getPropertyKeys() {
        return newHashSet(getImpl().properties.keySet());
    }
    @Override
    public void setProperty(String key, Object value) {
        ElementHelper.validateProperty(this, key, value);
        // TODO: update indices?
        getMutableImpl().properties.put(key, value);
    }
    @Override
    @SuppressWarnings("unchecked")
    public <T> T removeProperty(String key) {
        // TODO: update indices?
        return (T) getMutableImpl().properties.remove(key);
    }
    @Override
    public abstract void remove();
    @Override
    public Object getId() {
        return id;
    }
    @Override
    public int key() {
        return id;
    }
    protected abstract XElement getImpl();
    protected abstract XElement getMutableImpl();
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
        XElement(final XElement xe) {
            this.id = xe.id;
            this.properties = newHashMap(xe.properties);
        }
        XElement(int id) {
            this.id = id;
            this.properties = newHashMap();
        }
        @Override
        public int key() {
            return id;
        }

        private final int id;
        private final Map<String, Object> properties;
    }
    // =================================
    protected final int id;
    protected final GitGraph graph;
}
