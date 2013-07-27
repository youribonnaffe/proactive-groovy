package blog1

import static blog1.ProActiveMagic.remote

new WallyFingerPointer().with {
    println 'Wally is here: ' + it.foundHim()
}

remote(WallyFingerPointer) {
    println 'Wizard whitebeard is here: ' + it.foundHim()
}
