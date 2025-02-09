package com.example.dto.api;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

public class ApiServiceDto {
    
    @Data
    public static class Request {
        private String apiKey;        // API 키 (필수)
        private String searchType;    // 검색 유형 (date/product)
        private String startDate;     // 검색 시작일
        private String endDate;       // 검색 종료일
        private String productName;   // 제품명
    }
    
    @Data
    public static class Response {
        private String status;        // 응답 상태
        private String message;       // 응답 메시지
        private Object data;          // 응답 데이터
    }
    
    @Data
    public static class CallHistory {
        private Long callId;          // 호출 ID
        private Integer userId;       // 사용자 ID
        private Integer apiKeyId;     // API 키 ID
        private String reqUrl;        // 요청 URL
        private String reqParam;      // 요청 파라미터
        private String resCode;       // 응답 코드
        private LocalDateTime callDt; // 호출 일시
    }
    
    @Data
    public static class CodeRequest {
        private String serviceCrtfcKey;  // 서비스인증키
    }
    
    @Data
    public static class CodeResponse {
        private String resultCd;         // 결과코드
        private String resultMsg;        // 결과메시지
        private LocalDateTime resultProcDt;  // 결과처리일시
        private Object data;             // 코드 데이터
    }
    
    @Data
    public static class CodeData {
        private String resCode;     // 응답 코드
        private String message;     // 응답 메시지
    }
    
    @Data
    public static class DateSearchRequest {
        private String serviceCrtfcKey;  // 서비스인증키
        private String reqDocDt;         // 요청 문서 일시
    }
    
    @Data
    public static class DangerousGoodsInfo {
        private String inspInstNm;    // 검사기관명
        private String docNo;         // 문서번호
        private String docCycl;       // 문서차수
        private String rptTypeCd;     // 보고유형코드
        private String rptTypeNm;     // 보고유형명
        private String prdctNm;       // 제품명
        private String bzentyNm;      // 업체명
        private String mnftrYmd;      // 제조일자
        private String rtlTermCn;     // 유통기한
        private String rtrvlRsnCd;    // 회수사유코드
        private String cmdBgngDdCn;   // 고시일자
    }
    
    @Data
    public static class DateSearchResponse {
        private String resultCd;                          // 결과코드
        private String resultMsg;                         // 결과메시지
        private LocalDateTime resultProcDt;               // 결과처리일시
        private List<DangerousGoodsInfo> dangerousGoods; // 위해상품 정보 목록
    }
    
    @Data
    public static class ProductSearchRequest {
        private String serviceCrtfcKey;  // 서비스인증키
        private String reqPrdctNm;       // 제품명
        private String reqBzentyNm;      // 업체명
    }
    
    @Data
    public static class ProductSearchResponse {
        private String resultCd;                          // 결과코드
        private String resultMsg;                         // 결과메시지
        private LocalDateTime resultProcDt;               // 결과처리일시
        private List<DangerousGoodsInfo> dangerousGoods; // 위해상품 정보 목록
    }
} 