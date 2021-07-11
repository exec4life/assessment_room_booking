package com.user.arb.service.generic;

public class TripleGRO<F, S, T> extends CoupleGRO<F, S> {
    private T thirdObject;

    public TripleGRO() {
    }

    public T getThirdObject() {
        return thirdObject;
    }

    public void setThirdObject(T thirdObject) {
        this.thirdObject = thirdObject;
    }
}
