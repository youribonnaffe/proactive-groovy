package blog1

import org.objectweb.proactive.api.PALifeCycle

import static blog1.ProActiveMagic.remote

new WallyFingerPointer().with {
    println 'Wally is here: ' + foundHim()
}

remote(WallyFingerPointer) {
    println 'Wizard withebeard is here: ' + it.foundHim()
}

PALifeCycle.exitSuccess()
