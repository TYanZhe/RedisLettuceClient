package cn.org.tpeach.nosql.controller;

import lombok.Getter;

@Getter
public class ResultRes<T> {

    private boolean ret;
    private T data;
    private String msg;
    public ResultRes(boolean ret,T data,String msg) {
        this.ret = ret;
        this.data = data;
        this.msg = msg;
    }

}
