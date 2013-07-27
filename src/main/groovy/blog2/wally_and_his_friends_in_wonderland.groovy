package blog2

ProActivePool.withPool {
    def wallyAndHisFriends = ["Wally", "Wilma", "Woof"]
    print wallyAndHisFriends.collectParallel { it + " is here: " + Thread.currentThread().getName() }
}
