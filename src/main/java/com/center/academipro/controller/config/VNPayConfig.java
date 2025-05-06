package com.center.academipro.controller.config;


public class VNPayConfig {
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_TMN_CODE = "DEMOV210"; // Thay bằng mã của bạn
    public static final String VNP_HASH_SECRET = "UIHVD0YFF3V9FXRNIS6FXJ62VH7UPVD6"; // Thay bằng key của bạn
    public static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNP_RETURN_URL = "http://localhost:8080/vnpay-return";
    public static final String VNP_API_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
    public static final String VNP_IPN_URL = "http://academipro.test/ipn_handler.php";
    public static final String VNP_LOCALE = "vn";
    public static final String VNP_CURR_CODE = "VND";
}