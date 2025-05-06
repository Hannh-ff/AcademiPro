package com.center.academipro.utils;


import com.sun.net.httpserver.HttpServer;
import com.center.academipro.controller.service.PaymentService;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class IPNHandler {
    private HttpServer server;
    private final PaymentService paymentService;

    public IPNHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/vnpay-ipn", exchange -> {
            try {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parseQuery(query);

                String response;
                if (verifyResponse(params)) {
                    String status = "00".equals(params.get("vnp_ResponseCode")) ? "Completed" : "Failed";
                    paymentService.updatePaymentStatus(
                            params.get("vnp_TxnRef"),
                            status,
                            params.get("vnp_TransactionNo")
                    );
                    response = "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
                } else {
                    response = "{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}";
                }

                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
            }
        });

        server.setExecutor(Executors.newSingleThreadExecutor());
        server.start();
        System.out.println("IPN Server started on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("IPN Server stopped");
        }
    }

    private boolean verifyResponse(Map<String, String> params) {
        // Triển khai kiểm tra checksum (tương tự VNPayService)
        return true;
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    result.put(pair[0], pair[1]);
                }
            }
        }
        return result;
    }
}