package com.example.securitydemo.authentication.controller;

import com.example.securitydemo.authentication.validate.smscode.SmsCode;
import com.example.securitydemo.authentication.validate.imgcode.ImageCode;
import com.example.securitydemo.common.dto.ReturnData;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.*;
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
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "security")
public class ValidateController {

    @Value("${CACHE_KEY_IMAGE_CODE}")
    public String CACHE_KEY_IMAGE_CODE;
    @Value("${CACHE_KEY_SMS_CODE}")
    public String CACHE_KEY_SMS_CODE;
    @Value("${CACHE_IMAGE_CODE_TIMEOUT}")
    public Integer CACHE_IMAGE_CODE_TIMEOUT;
    @Value("${CACHE_SMS_CODE_TIMEOUT}")
    public Integer CACHE_SMS_CODE_TIMEOUT;
    @Value("${SEND_SMS_CODE_LIMIT_TIMES}")
    public Integer SEND_SMS_CODE_LIMIT_TIMES;
    @Value("${SEND_SMS_CODE_LIMIT_TIMES_TIMEOUT}")
    public Integer SEND_SMS_CODE_LIMIT_TIMES_TIMEOUT;
    @Value("${SEND_KEY_SMS_CODE_LIMIT_TIMES}")
    public String SEND_KEY_SMS_CODE_LIMIT_TIMES;

    @Value("${IMAGE_CODE_TIMEOUT}")
    public Integer IMAGE_CODE_TIMEOUT;
    @Value("${SMS_CODE_TIMEOUT}")
    public Integer SMS_CODE_TIMEOUT;


    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/code/imageCode")
    public @ResponseBody
    ReturnData imageCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

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
        redisTemplate.opsForValue().set(CACHE_KEY_IMAGE_CODE + "_" + imageCodeKey, imageCodeInCache, CACHE_IMAGE_CODE_TIMEOUT, TimeUnit.SECONDS);
        return new ReturnData("200", "", map);
    }

    @GetMapping("/code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("==== createCode  ");
        ImageCode imageCode = createImageCode();
        String imageCodeKey = UUID.randomUUID().toString();
        ImageCode imageCodeInCache = new ImageCode(null, imageCode.getCode(), imageCode.getExpireTime());
        redisTemplate.opsForValue().set(CACHE_KEY_IMAGE_CODE + "_" + imageCodeKey, imageCodeInCache, CACHE_IMAGE_CODE_TIMEOUT, TimeUnit.SECONDS);
        sessionStrategy.setAttribute(new ServletWebRequest(request), CACHE_KEY_IMAGE_CODE + "_" + imageCodeKey, imageCodeInCache);
        ImageIO.write(imageCode.getImage(), "jpeg", response.getOutputStream());
    }

    @PostMapping("/code/sms")
    public @ResponseBody
    ReturnData createSmsCode(HttpServletRequest request, HttpServletResponse response, String mobile) throws IOException {
        System.out.println(" =====  /code/sms ");
        //判断发送次数
        Integer sendTimes = null;
        if (redisTemplate.opsForValue().get(SEND_KEY_SMS_CODE_LIMIT_TIMES + "_" + mobile) != null) {
            System.out.println("======= SEND_KEY_SMS_CODE_LIMIT_TIMES : " + SEND_KEY_SMS_CODE_LIMIT_TIMES + "_" + mobile);
            sendTimes = (Integer) redisTemplate.opsForValue().get(SEND_KEY_SMS_CODE_LIMIT_TIMES + "_" + mobile);
        } else {
            sendTimes = 0;
        }
        System.out.println("===== sendTimes " + sendTimes);
        SmsCode codeInCache = (SmsCode) redisTemplate.opsForValue().get(CACHE_KEY_SMS_CODE + "_" + mobile);
        if (codeInCache != null && !LocalDateTime.now().isAfter(codeInCache.getExpireTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            System.out.println("您的登录验证码已发送：" + codeInCache.getCode() + "，有效时间为" + SMS_CODE_TIMEOUT + "秒");
            return new ReturnData("200", "发送成功，有效时间为" + SMS_CODE_TIMEOUT + "秒");
        } else {
            SmsCode smsCode = createSMSCode();

            redisTemplate.opsForValue().set(CACHE_KEY_SMS_CODE + "_" + mobile, smsCode, CACHE_SMS_CODE_TIMEOUT, TimeUnit.SECONDS);
            //判断发送次数
            if (sendTimes <= SEND_SMS_CODE_LIMIT_TIMES) {
                /***
                 * 此处添加发送短信
                 */
                System.out.println("您的登录验证码为：" + smsCode.getCode() + "，有效时间为" + SMS_CODE_TIMEOUT + "秒");
                System.out.println("===== sendTimes " + sendTimes);
                //记录发送次数
                System.out.println("======= SEND_KEY_SMS_CODE_LIMIT_TIMES : " + SEND_KEY_SMS_CODE_LIMIT_TIMES + "_" + mobile);
                if (sendTimes == 0) {
                    redisTemplate.opsForValue().set(SEND_KEY_SMS_CODE_LIMIT_TIMES + "_" + mobile, sendTimes + 1, SEND_SMS_CODE_LIMIT_TIMES_TIMEOUT, TimeUnit.SECONDS);
                } else {
                    System.out.println("==== 发送次数累加 ");
                    redisTemplate.getConnectionFactory().getConnection().incr(redisTemplate.getKeySerializer().serialize(SEND_KEY_SMS_CODE_LIMIT_TIMES + "_" + mobile));
//                    stringRedisTemplate.getConnectionFactory().getConnection().incr(stringRedisTemplate.getKeySerializer().serialize(SEND_KEY_SMS_CODE_LIMIT_TIMES + "_" + mobile));
//                    redisTemplate.opsForValue().set(SEND_KEY_SMS_CODE_LIMIT_TIMES + "_" + mobile, sendTimes + 1);
                }
                return new ReturnData("200", "发送成功，有效时间为" + SMS_CODE_TIMEOUT + "秒", smsCode);
            } else {
                return new ReturnData("500", "发送次数超限");
            }
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
