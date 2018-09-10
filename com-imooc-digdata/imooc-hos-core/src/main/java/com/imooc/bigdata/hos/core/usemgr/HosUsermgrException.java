package com.imooc.bigdata.hos.core.usemgr;

import com.imooc.bigdata.hos.core.HosException;

public class HosUsermgrException extends HosException {
    private int code;
    private String message;

    public HosUsermgrException(int code, String message, Throwable cause){
        super(message,cause);
        this.code = code;
        this.message = message;
    }

    public HosUsermgrException(int code, String message){
        super(message,null);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int errorCode(){
        return this.code;
    }
}
