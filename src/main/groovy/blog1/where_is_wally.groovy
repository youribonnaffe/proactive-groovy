package blog1

import org.objectweb.proactive.api.PALifeCycle

import static blog1.ProActiveMagic.remote

remote(WallyFingerPointer) {
    println 'Wizard withebeard is here: ' + String.valueOf(it.foundHim())
}
new WallyFingerPointer().with {
    println 'Wally is here: ' + it.foundHim()
}

PALifeCycle.exitSuccess();
