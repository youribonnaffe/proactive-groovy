package blog1

class RemoteClosure {

    Object call(Closure param) {
        return param.call();
    }
}
