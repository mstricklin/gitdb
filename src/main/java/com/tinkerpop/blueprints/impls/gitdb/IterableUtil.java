// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package com.tinkerpop.blueprints.impls.gitdb;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.base.Preconditions;

public class IterableUtil {

    static <T> Iterable<T> once(final Iterator<T> source) {
        return new Iterable<T>() {
            private AtomicBoolean exhausted = new AtomicBoolean();
            @Override
            public Iterator<T> iterator() {
                Preconditions.checkState(!exhausted.getAndSet(true));
                return source;
            }
        };
    }
}
