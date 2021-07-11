package com.user.arb.service.generic;

public class CoupleGRO<F, S> {

    private F firstObject;
    private S secondObject;

    public CoupleGRO() {
    }

    public F getFirstObject() {
        return firstObject;
    }

    public void setFirstObject(F firstObject) {
        this.firstObject = firstObject;
    }

    public S getSecondObject() {
        return secondObject;
    }

    public void setSecondObject(S secondObject) {
        this.secondObject = secondObject;
    }
}
