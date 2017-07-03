package com.tinkerpop.blueprints.impls.gitdb.cache;

import com.google.common.cache.CacheBuilder;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class CacheOfCaches {


    public static Map<Long, String> cache(Class<?> clazz) {
        return newHashMap();
    }
//    LoadingCache<Class<?>, Graph> graphs
//            = CacheBuilder.newBuilder()
//            .build(
//                    new CacheLoader<Class<?>, Graph>() {
//                        public Graph load(Class<?> key) {
//                            return createExpensiveGraph(key);
//                        }
//                    });
}
