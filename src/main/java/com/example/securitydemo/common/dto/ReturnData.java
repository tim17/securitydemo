package com.example.securitydemo.common.dto;

import com.github.pagehelper.PageInfo;

import java.io.Serializable;

public class ReturnData<T> implements Serializable {
    private static final long serialVersionUID = 71846877475846866L;
    private String respCode;
    private String respMessage;
    private T data;
    private PageInfo pageInfo;

    public ReturnData() {

    }

    public ReturnData(String respCode, String respMessage) {
        this.respCode = respCode;
        this.respMessage = respMessage;
    }

    public ReturnData(String respCode, String respMessage, PageInfo pageInfo) {
        this.respCode = respCode;
        this.respMessage = respMessage;
        this.pageInfo = pageInfo;
    }

    public ReturnData(String respCode, String respMessage, T data) {
        this.respCode = respCode;
        this.respMessage = respMessage;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }
}
