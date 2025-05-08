//package com.center.academipro.controller.service;
//
//
//import com.center.academipro.controller.config.VNPayConfig;
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class VNPayService {
//    public String createPaymentUrl(double amount, String orderId, String orderInfo, String ipAddress)
//            throws UnsupportedEncodingException {
//
//        Map<String, String> vnpParams = new LinkedHashMap<>();
//        vnpParams.put("vnp_Version", VNPayConfig.VNP_VERSION);
//        vnpParams.put("vnp_Command", VNPayConfig.VNP_COMMAND);
//        vnpParams.put("vnp_TmnCode", VNPayConfig.VNP_TMN_CODE);
//        vnpParams.put("vnp_Amount", String.valueOf((int)(amount * 100)));
//        vnpParams.put("vnp_CurrCode", VNPayConfig.VNP_CURR_CODE);
//        vnpParams.put("vnp_TxnRef", orderId);
//        vnpParams.put("vnp_OrderInfo", orderInfo);
//        vnpParams.put("vnp_OrderType", "other");
//        vnpParams.put("vnp_ReturnUrl", VNPayConfig.VNP_RETURN_URL);
//        vnpParams.put("vnp_IpAddr", ipAddress);
//        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
//        vnpParams.put("vnp_Locale", VNPayConfig.VNP_LOCALE);
//
//        // Thêm thời gian hết hạn (15 phút)
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.MINUTE, 15);
//        vnpParams.put("vnp_ExpireDate", new SimpleDateFormat("yyyyMMddHHmmss").format(cal.getTime()));
//
//        return buildPaymentUrl(vnpParams);
//    }
//
//    private String buildPaymentUrl(Map<String, String> params) throws UnsupportedEncodingException {
//        Map<String, String> sortedParams = new TreeMap<>(params);
//        StringBuilder hashData = new StringBuilder();
//        StringBuilder query = new StringBuilder();
//
//        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
//            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
//                if (hashData.length() > 0) hashData.append('&');
//                hashData.append(entry.getKey()).append('=').append(entry.getValue());
//
//                if (query.length() > 0) query.append('&');
//                query.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
//                        .append('=')
//                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
//            }
//        }
//
//        String secureHash = hmacSHA512(VNPayConfig.VNP_HASH_SECRET, hashData.toString());
//        return VNPayConfig.VNP_URL + "?" + query + "&vnp_SecureHash=" + secureHash;
//    }
//
//    private String hmacSHA512(String key, String data) {
//        try {
//            Mac hmac512 = Mac.getInstance("HmacSHA512");
//            hmac512.init(new SecretKeySpec(key.getBytes(), "HmacSHA512"));
//            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
//            StringBuilder sb = new StringBuilder();
//            for (byte b : result) sb.append(String.format("%02x", b));
//            return sb.toString();
//        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
//            throw new RuntimeException("Security signature error", e);
//        }
//    }
//}