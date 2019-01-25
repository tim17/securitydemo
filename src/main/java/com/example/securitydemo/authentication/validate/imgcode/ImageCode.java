package com.example.securitydemo.authentication.validate.imgcode;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ImageCode implements Serializable {

    private static final long serialVersionUID = -2305420851635095851L;

    private BufferedImage image;

    private String code;

    private Date expireTime;

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public ImageCode() {
    }

    public ImageCode(BufferedImage image, String code, int expireIn) {
        LocalDateTime expireTime_LocalDateTime = LocalDateTime.now().plusSeconds(expireIn);
        this.image = image;
        this.code = code;
        this.expireTime = Date.from(expireTime_LocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public ImageCode(BufferedImage image, String code, Date expireTime) {
        this.image = image;
        this.code = code;
        this.expireTime = expireTime;

    }

    boolean isExpire() {
        LocalDateTime localDateTime_expireTime = this.expireTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return LocalDateTime.now().isAfter(localDateTime_expireTime);
    }

    public BufferedImage getImage() {
        return image;
    }


    public void setImage(BufferedImage image) {
        this.image = image;
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
}
