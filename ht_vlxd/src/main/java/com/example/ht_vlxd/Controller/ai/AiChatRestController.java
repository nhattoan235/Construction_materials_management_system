package com.example.ht_vlxd.Controller.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiChatRestController {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        Map<String, String> response = new HashMap<>();

        if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
            response.put("reply", "Chào bạn! Tôi là Trợ lý AI của Vật liệu Xây dựng Sài Gòn CMC. Cửa hàng hỗ trợ các loại xi măng, cát, đá, thép, gạch chất lượng cao. Hiện tại API Key của Gemini chưa được cấu hình trong `application.properties` (thuộc tính `gemini.api.key`). Hãy bổ sung API Key của bạn để tôi có thể hỗ trợ thông minh hơn nhé!");
            return ResponseEntity.ok(response);
        }

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

            // Setup professional system context to guide the assistant
            String systemContext = "Bạn là trợ lý AI thông minh của cửa hàng Vật liệu Xây dựng Sài Gòn CMC. Hãy trả lời ngắn gọn, chuyên nghiệp bằng tiếng Việt, tập trung tư vấn vật liệu xây dựng (xi măng, cát, đá, thép, gạch...) và giải đáp thắc mắc của khách hàng.";
            String fullPrompt = systemContext + "\nKhách hàng hỏi: " + userMessage;

            Map<String, Object> parts = new HashMap<>();
            parts.put("text", fullPrompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(parts));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> geminiResponse = restTemplate.postForEntity(url, entity, Map.class);

            if (geminiResponse.getStatusCode() == HttpStatus.OK && geminiResponse.getBody() != null) {
                Map body = geminiResponse.getBody();
                List candidates = (List) body.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map firstCandidate = (Map) candidates.get(0);
                    Map contentObj = (Map) firstCandidate.get("content");
                    if (contentObj != null) {
                        List partsList = (List) contentObj.get("parts");
                        if (partsList != null && !partsList.isEmpty()) {
                            Map firstPart = (Map) partsList.get(0);
                            String aiReply = (String) firstPart.get("text");
                            response.put("reply", aiReply);
                            return ResponseEntity.ok(response);
                        }
                    }
                }
            }
            
            response.put("reply", "Rất tiếc, tôi gặp sự cố khi giải mã phản hồi từ hệ thống AI.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        } catch (Exception e) {
            response.put("reply", "Lỗi kết nối tới Trợ lý AI: " + e.getMessage() + ". Vui lòng kiểm tra lại cấu hình API Key.");
            return ResponseEntity.ok(response);
        }
    }
}
