package com.example.dto.api;

import lombok.Data;
import java.time.LocalDateTime;

public class ApiKeyDto {
    
    @Data
    public static class Request {
        private String keyName;
    }
    
    @Data
    public static class Response {
        private String purpose;
        private String apiKey;
        private LocalDateTime issuedDt;
        private LocalDateTime expiresDt;
        private String useYn;
    }

    @Data
    public static class ListResponse {
        private boolean success;
        private java.util.List<Response> data;
    }
}