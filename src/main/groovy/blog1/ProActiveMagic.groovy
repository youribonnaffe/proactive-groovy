package blog1

import org.objectweb.proactive.api.PAActiveObject
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment
import org.objectweb.proactive.gcmdeployment.GCMApplication
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode

class ProActiveMagic {

    static void remote(Class clazz, Closure closure) {
        ProActiveMagic.OneNodeDeployment.start()
        def ao = ProActiveMagic.OneNodeDeployment.active(clazz)
        try {
            use(ProActiveMagic.OneNodeDeployment) {
                closure(ao)
            }
        } finally {
            ProActiveMagic.OneNodeDeployment.stop(ao)
        }
    }

    static def remote(Closure closure) {
        ProActiveMagic.OneNodeDeployment.start()
        def ao = ProActiveMagic.OneNodeDeployment.active(RemoteClosure.class)
        try {
            return ao.call(closure.dehydrate())
        } finally {
            try {
                ProActiveMagic.OneNodeDeployment.stop(ao)
            } catch (Throwable t) {
                t.printStackTrace()
            }
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
            PAActiveObject.terminateActiveObject(activeObject, true)
            gcmApp.kill()
        }

        static def active(Class clazz) {
            return PAActiveObject.newActive(clazz.getName(), null, node.getANode())
        }
    }
}
