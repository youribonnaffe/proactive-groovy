package blog1;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.exceptions.ProActiveBadConfigurationException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import java.io.File;

public class WhereIsWally {

    public static void main(String[] args) throws ProActiveException, ProActiveBadConfigurationException {
        GCMApplication gcmApp = PAGCMDeployment.loadApplicationDescriptor(new File("src/main/resources/LocalDeployment.xml"));
        gcmApp.startDeployment();
        gcmApp.waitReady();
        GCMVirtualNode node = gcmApp.getVirtualNodes().values().iterator().next();

        WallyFingerPointer remoteWallyWorld = (WallyFingerPointer) PAActiveObject.newActive(WallyFingerPointer.class
                .getName(), null, node.getANode());

        System.out.println("Wally is here: " + new WallyFingerPointer().foundHim());
        System.out.println("Wizard whitebeard is here: " + remoteWallyWorld.foundHim());

        PAActiveObject.terminateActiveObject(remoteWallyWorld, true);
        gcmApp.kill();
        PALifeCycle.exitSuccess();
    }

}
