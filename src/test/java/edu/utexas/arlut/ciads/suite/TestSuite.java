/*
 * // CLASSIFICATION NOTICE: This file is UNCLASSIFIED
 */

package edu.utexas.arlut.ciads.suite;

import com.google.common.base.Stopwatch;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.gitdb.GitGraph;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
public class TestSuite {

    @Rule
    public TestName name = new TestName();
    public Stopwatch sw = Stopwatch.createUnstarted();

    protected Graph graph;

    @Before
    public void before() {
//        deleteDirectory(new File(workingDir));
        log.info("Testing {}...", name.getMethodName());
        graph = GitGraph.of();
//        sw = Stopwatch.createUnstarted();
        Stopwatch.createStarted();
    }

    @After
    public void after() {
        log.error("after");
        graph.shutdown();
        sw.reset();
        log.info("*** TOTAL TIME [{}]: {} ***", name.getMethodName(), sw.toString());
//        deleteDirectory(new File(workingDir));
    }
    public static Object convertId(final Object id) {
        return id;
    }

    public static String convertLabel(final String label) {
        return label;
    }

    protected void resetAndStart() {
        sw.reset();
        sw.start();
    }

    protected void printPerformance(String name, Integer events, String eventName) {
        //sw.stop();
        if (null != events) {
            log.info("\t{}: {} {} in {}ms", name, events, eventName, sw.elapsed(MILLISECONDS));
        } else {
            log.info("\t{}: {} in {}ms", name, eventName, sw.elapsed(MILLISECONDS));
        }
    }
}
