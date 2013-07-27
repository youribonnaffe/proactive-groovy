package blog1;

import groovy.lang.Closure;

public class RemoteClosure {

    public Object call(Closure param, Object... params) {
        return param.call(params);
    }
}
