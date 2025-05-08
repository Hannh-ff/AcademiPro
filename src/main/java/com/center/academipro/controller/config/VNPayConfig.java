package com.center.academipro.controller.config;


//public class VNPayConfig {
//    public static final String VNP_VERSION = "2.1.0";
//    public static final String VNP_COMMAND = "pay";
//    public static final String VNP_TMN_CODE = "DEMOV210"; // Thay bằng mã của bạn
//    public static final String VNP_HASH_SECRET = "UIHVD0YFF3V9FXRNIS6FXJ62VH7UPVD6"; // Thay bằng key của bạn
//    public static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
//    public static final String VNP_RETURN_URL = "http://localhost:8080/vnpay-return";
//    public static final String VNP_API_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
//    public static final String VNP_IPN_URL = "http://academipro.test/ipn_handler.php";
//    public static final String VNP_LOCALE = "vn";
//    public static final String VNP_CURR_CODE = "VND";
//}
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class VNPayConfig {
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_ReturnUrl = "http://localhost:8080/payment/return"; // Thay đổi URL này nếu cần
    public static String vnp_TmnCode = "DEMOV210"; // Thay bằng mã TMN của bạn
    public static String secretKey = "UIHVD0YFF3V9FXRNIS6FXJ62VH7UPVD6"; // Thay bằng secret key của bạn
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}