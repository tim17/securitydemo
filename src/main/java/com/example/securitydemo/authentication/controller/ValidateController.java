package com.example.securitydemo.authentication.controller;

import com.example.securitydemo.authentication.validate.smscode.SmsCode;
import com.example.securitydemo.authentication.validate.imgcode.ImageCode;
import com.example.securitydemo.common.dto.ReturnData;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
public class ValidateController {

    public final static String KEY_IMAGE_CODE = "IMAGE_CODE";

    public final static String KEY_SMS_CODE = "SMS_CODE";

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/code/imageCode")
    public ReturnData imageCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("==== createCode  ");
        ImageCode imageCode = createImageCode();
        String imageCodeKey = UUID.randomUUID().toString();
        ImageCode imageCodeInCache = new ImageCode(null, imageCode.getCode(), imageCode.getExpireTime());
        // 生成图片验证码
        ByteArrayOutputStream outputStream = null;
        outputStream = new ByteArrayOutputStream();
        ImageIO.write(imageCode.getImage(), "jpg", outputStream);
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("imageCode", encoder.encode(outputStream.toByteArray()).replaceAll("\r|\n", ""));
        map.put("imageCodeKey", imageCodeKey);
        redisTemplate.opsForValue().set(KEY_IMAGE_CODE + "_" + imageCodeKey, imageCodeInCache);
        return new ReturnData("200", "", map);
    }

    @GetMapping("/code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("==== createCode  ");
        ImageCode imageCode = createImageCode();
        String imageCodeKey = UUID.randomUUID().toString();
        ImageCode imageCodeInCache = new ImageCode(null, imageCode.getCode(), imageCode.getExpireTime());
        redisTemplate.opsForValue().set(KEY_IMAGE_CODE + "_" + imageCodeKey, imageCodeInCache);
        sessionStrategy.setAttribute(new ServletWebRequest(request), KEY_IMAGE_CODE + "_" + imageCodeKey, imageCodeInCache);
        ImageIO.write(imageCode.getImage(), "jpeg", response.getOutputStream());
    }

    @GetMapping("/code/sms")
    public void createSmsCode(HttpServletRequest request, HttpServletResponse response, String mobile) {
        System.out.println(" =====  /code/sms ");
//        SmsCode codeInSession = (SmsCode) sessionStrategy.getAttribute(new ServletWebRequest(request), KEY_SMS_CODE + mobile);
        SmsCode codeInCache = (SmsCode) redisTemplate.opsForValue().get(KEY_SMS_CODE + "_" + mobile);
        if (codeInCache != null && !LocalDateTime.now().isAfter(codeInCache.getExpireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            System.out.println("您的登录验证码为：" + codeInCache.getCode() + "，有效时间为60秒");
        } else {
            SmsCode smsCode = createSMSCode();
            redisTemplate.opsForValue().set(KEY_SMS_CODE + "_" + mobile, smsCode);
//            sessionStrategy.setAttribute(new ServletWebRequest(request), KEY_SMS_CODE+"-" + mobile, smsCode);
            // 输出验证码到控制台代替短信发送服务
            System.out.println("您的登录验证码为：" + smsCode.getCode() + "，有效时间为60秒");
        }
    }

    private SmsCode createSMSCode() {
        String code = RandomStringUtils.randomNumeric(6);
        return new SmsCode(code, 60);
    }

    private ImageCode createImageCode() {
        int width = 100; // 验证码图片宽度
        int height = 36; // 验证码图片长度
        int length = 4; // 验证码位数
        int expireIn = 60; // 验证码有效时间 60s

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand.append(rand);
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 16);
        }

        g.dispose();

        return new ImageCode(image, sRand.toString(), expireIn);
    }

    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;

        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

}
