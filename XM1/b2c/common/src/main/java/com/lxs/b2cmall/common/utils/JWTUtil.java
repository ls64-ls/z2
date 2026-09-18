package com.lxs.b2cmall.common.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {

    private static final String SECRET = "b2cmall-secret-key-2024";
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000L;

    public static String createToken(Map<String, Object> payload) {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> fullPayload = new HashMap<>(payload);
        fullPayload.put("exp", System.currentTimeMillis() + EXPIRE_TIME);

        String headerStr = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(toJson(header).getBytes(StandardCharsets.UTF_8));
        String payloadStr = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(toJson(fullPayload).getBytes(StandardCharsets.UTF_8));

        String signature = sign(headerStr + "." + payloadStr);
        return headerStr + "." + payloadStr + "." + signature;
    }

    public static boolean verify(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        String signature = sign(parts[0] + "." + parts[1]);
        if (!signature.equals(parts[2])) {
            return false;
        }
        try {
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Map<String, Object> payload = parseJson(payloadJson);
            Object exp = payload.get("exp");
            if (exp != null) {
                long expireTime = Long.parseLong(exp.toString());
                if (System.currentTimeMillis() > expireTime) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Map<String, Object> getPayload(String token) {
        if (!verify(token)) {
            return null;
        }
        String[] parts = token.split("\\.");
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        return parseJson(payloadJson);
    }

    private static String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException("JWT signature failed", e);
        }
    }

    private static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else if (value instanceof Date) {
                sb.append(((Date) value).getTime());
            } else {
                sb.append(value);
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private static Map<String, Object> parseJson(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            int depth = 0;
            StringBuilder current = new StringBuilder();
            String key = null;
            for (int i = 0; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '{') depth++;
                if (c == '}') depth--;
                if (c == ',' && depth == 0) {
                    if (key != null) {
                        result.put(key, parseValue(current.toString().trim()));
                        key = null;
                        current = new StringBuilder();
                    }
                } else if (c == ':' && depth == 0 && key == null) {
                    key = current.toString().trim().replaceAll("^\"|\"$", "");
                    current = new StringBuilder();
                } else {
                    current.append(c);
                }
            }
            if (key != null && current.length() > 0) {
                result.put(key, parseValue(current.toString().trim()));
            }
        }
        return result;
    }

    private static Object parseValue(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            }
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }
}
