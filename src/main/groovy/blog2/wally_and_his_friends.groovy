package blog2

import groovyx.gpars.GParsPool

GParsPool.withPool {
    def wallyAndHisFriends = ["Wally", "Wilma", "Woof"]
    print wallyAndHisFriends.collectParallel { it + " is here: " + Thread.currentThread().getName() }
}
