package blog2


import blog1.RemoteClosure
import groovyx.gpars.pa.CallClosure
import groovyx.gpars.pa.ClosureMapper
import groovyx.gpars.pa.GParsPoolUtilHelper
import jsr166y.ForkJoinPool
import org.objectweb.proactive.api.PAActiveObject
import org.objectweb.proactive.api.PALifeCycle
import org.objectweb.proactive.core.body.future.Future
import org.objectweb.proactive.core.mop.StubObject
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment
import org.objectweb.proactive.gcmdeployment.GCMApplication
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode

class ProActivePool {
    static ForkJoinPool pool

    static withPool(Closure cl) {
        // from gpars itself
        pool = new ForkJoinPool(1, ForkJoinPool.defaultForkJoinWorkerThreadFactory, { Thread failedThread, Throwable throwable ->
            System.err.println "Error processing background thread ${failedThread.name}: ${throwable.message}"
            throwable.printStackTrace(System.err)
        } as Thread.UncaughtExceptionHandler, false)
        use(ProActivePoolUtil) {
            cl(pool)
        }
        try {
            PALifeCycle.exitSuccess()
        } catch (Exception ignored) {
            // silent errors
        }
    }
}

class ProActivePoolUtil {

    public static <T> Collection<T> collectParallel(final Collection collection, final Closure<? extends T> cl) {

        Closure remoteClosure = {
            OneNodeDeployment.start()
            def executor = OneNodeDeployment.active(RemoteClosure.class)
            try {
                def result = executor.call(cl.dehydrate(), it)
                Future f = (Future) (((StubObject) result).getProxy());
                return f.getResult()
            } finally {
                OneNodeDeployment.stop(executor)
            }
        }

        return GParsPoolUtilHelper.createPAFromCollection(collection, ProActivePool.pool).withMapping(new ClosureMapper(new CallClosure(remoteClosure))).all().asList()
    }

}

class OneNodeDeployment {
    static GCMVirtualNode node
    static GCMApplication gcmApp

    static void start() {
        gcmApp = PAGCMDeployment.loadApplicationDescriptor(new File("src/main/resources/LocalDeployment.xml"))
        gcmApp.startDeployment()
        gcmApp.waitReady()
        node = gcmApp.getVirtualNodes().values().iterator().next()
    }

    static void stop(Object activeObject) {
        try {
            PAActiveObject.terminateActiveObject(activeObject, true)
            gcmApp.kill()
        } catch (ignored) {
            // silent errors
        }
    }

    static def active(Class clazz) {
        return PAActiveObject.newActive(clazz.getName(), null, node.getANode())
    }
}
