package com.example.securitydemo.authentication.validate.smscode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class SmsCode implements Serializable {

    private static final long serialVersionUID = -6545346079631557846L;
    private String code;
    private Date expireTime;


    public SmsCode() {
    }

    public SmsCode(String code, int expireIn) {
        LocalDateTime expireTime_LocalDateTime = LocalDateTime.now().plusSeconds(expireIn);
        this.code = code;
        this.expireTime = Date.from(expireTime_LocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public SmsCode(String code, Date expireTime) {
        this.code = code;
        this.expireTime = expireTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
