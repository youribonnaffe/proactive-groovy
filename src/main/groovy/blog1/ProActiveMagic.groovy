package blog1

import org.objectweb.proactive.api.PAActiveObject
import org.objectweb.proactive.api.PALifeCycle
import org.objectweb.proactive.core.body.future.Future
import org.objectweb.proactive.core.mop.StubObject
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment
import org.objectweb.proactive.gcmdeployment.GCMApplication
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode

class ProActiveMagic {

    static void remote(Class clazz, Closure closure) {
        OneNodeDeployment.start()
        def ao = OneNodeDeployment.active(clazz)
        try {
            closure(ao)
        } finally {
            OneNodeDeployment.stop(ao)
            PALifeCycle.exitSuccess()
        }
    }

    static def remote(Closure closure) {
        OneNodeDeployment.start()
        RemoteClosure ao = OneNodeDeployment.active(RemoteClosure.class)
        try {
            def dehydrated = closure.dehydrate()
            def result = ao.call(dehydrated)
            Future f = (Future) (((StubObject) result).getProxy());
            return f.getResult()
        } finally {
            OneNodeDeployment.stop(ao)
        }
    }

    static class OneNodeDeployment {
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
                // silent error
            }
        }

        static <T> T active(Class<T> clazz) {
            return PAActiveObject.newActive(clazz.getName(), null, node.getANode()) as T
        }
    }
}
