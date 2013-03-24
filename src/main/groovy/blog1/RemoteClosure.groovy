package blog1

class RemoteClosure {

    Object call(Closure param) {
        param.call()
    }
}
