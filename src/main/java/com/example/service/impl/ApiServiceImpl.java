package com.example.service.impl;

import com.example.service.ApiService;
import com.example.mapper.ApiServiceMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.dto.api.ApiServiceDto;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {
    
    private static final Logger log = LoggerFactory.getLogger(ApiServiceImpl.class);
    private static final String DEFAULT_ERROR_CODE = "500";  // 기본 에러 코드
    
    private final ObjectMapper objectMapper;
    private final ApiServiceMapper apiServiceMapper;
    
    @Override
    public ApiServiceDto.CodeResponse getCode(ApiServiceDto.CodeRequest request) {
        String apiKey = request.getServiceCrtfcKey();
        log.info("Service received API key: '{}'", apiKey);  // 작은따옴표로 감싸서 공백 확인 가능
        
        try {
            boolean isValid = apiServiceMapper.isValidApiKey(apiKey);
            log.info("API key validation query result for key '{}': {}", apiKey, isValid);
            
            if (!isValid) {
                return createErrorResponse("300"); // 테이블의 코드 사용
            }
            
            // API 호출 이력 기록
            ApiServiceDto.CallHistory history = new ApiServiceDto.CallHistory();
            Integer apiKeyId = apiServiceMapper.getApiKeyId(apiKey);
            log.info("Found API key ID: {}", apiKeyId);
            
            history.setApiKeyId(apiKeyId);
            history.setReqUrl("/api/v1/code");
            history.setReqParam(objectMapper.writeValueAsString(request));
            history.setResCode("200");
            
            apiServiceMapper.insertCallHistory(history);
            
            // 코드 데이터 조회
            List<ApiServiceDto.CodeData> codeList = apiServiceMapper.getAllResCodes();
            
            // 성공 응답 생성 - 테이블의 코드 사용
            ApiServiceDto.CodeResponse response = createResponse("200");
            response.setData(codeList);
            return response;
            
        } catch (Exception e) {
            log.error("Error processing request", e);
            return createErrorResponse("500"); // 테이블의 코드 사용
        }
    }
    
    @Override
    public ApiServiceDto.DateSearchResponse searchByDate(ApiServiceDto.DateSearchRequest request) {
        String apiKey = request.getServiceCrtfcKey();
        log.info("Service received date search with API key: '{}'", apiKey);
        
        try {
            boolean isValid = apiServiceMapper.isValidApiKey(apiKey);
            log.info("API key validation query result for key '{}': {}", apiKey, isValid);
            
            if (!isValid) {
                return createDateSearchErrorResponse("300");
            }
            
            // API 호출 이력 기록
            ApiServiceDto.CallHistory history = new ApiServiceDto.CallHistory();
            Integer apiKeyId = apiServiceMapper.getApiKeyId(apiKey);
            history.setApiKeyId(apiKeyId);
            history.setReqUrl("/api/v1/date-search");
            history.setReqParam(objectMapper.writeValueAsString(request));
            history.setResCode("200");
            apiServiceMapper.insertCallHistory(history);
            
            // 날짜 처리
            LocalDate searchDate;
            if (request.getReqDocDt() != null && !request.getReqDocDt().trim().isEmpty()) {
                searchDate = LocalDate.parse(request.getReqDocDt());
            } else {
                searchDate = LocalDate.now().minusDays(90);
            }
            
            // 데이터 조회
            List<ApiServiceDto.DangerousGoodsInfo> goodsList = 
                apiServiceMapper.searchDangerousGoodsByDate(searchDate, searchDate.plusDays(90));
            
            // 응답 생성
            ApiServiceDto.DateSearchResponse response = new ApiServiceDto.DateSearchResponse();
            response.setResultCd("200");
            response.setResultMsg("정상 처리되었습니다");
            response.setResultProcDt(LocalDateTime.now());
            response.setDangerousGoods(goodsList);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error processing date search request", e);
            return createDateSearchErrorResponse("500");
        }
    }
    
    @Override
    public ApiServiceDto.ProductSearchResponse searchByProduct(ApiServiceDto.ProductSearchRequest request) {
        String apiKey = request.getServiceCrtfcKey();
        log.info("Service received product search with API key: '{}'", apiKey);
        
        try {
            boolean isValid = apiServiceMapper.isValidApiKey(apiKey);
            log.info("API key validation query result for key '{}': {}", apiKey, isValid);
            
            if (!isValid) {
                return createProductSearchErrorResponse("300");
            }
            
            // API 호출 이력 기록
            ApiServiceDto.CallHistory history = new ApiServiceDto.CallHistory();
            Integer apiKeyId = apiServiceMapper.getApiKeyId(apiKey);
            history.setApiKeyId(apiKeyId);
            history.setReqUrl("/api/v1/product-search");
            history.setReqParam(objectMapper.writeValueAsString(request));
            history.setResCode("200");
            apiServiceMapper.insertCallHistory(history);
            
            // 데이터 조회
            List<ApiServiceDto.DangerousGoodsInfo> goodsList = 
                apiServiceMapper.searchDangerousGoodsByProduct(
                    request.getReqPrdctNm(),
                    request.getReqBzentyNm()
                );
            
            // 응답 생성
            ApiServiceDto.ProductSearchResponse response = new ApiServiceDto.ProductSearchResponse();
            response.setResultCd("200");
            response.setResultMsg("정상 처리되었습니다");
            response.setResultProcDt(LocalDateTime.now());
            response.setDangerousGoods(goodsList);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error processing product search request", e);
            return createProductSearchErrorResponse("500");
        }
    }
    
    private ApiServiceDto.CodeResponse createResponse(String code) {
        try {
            ApiServiceDto.CodeData resCode = apiServiceMapper.getResCode(code);
            if (resCode == null) {
                log.warn("Response code not found in database: {}, using default error code", code);
                resCode = apiServiceMapper.getResCode(DEFAULT_ERROR_CODE);
            }
            
            ApiServiceDto.CodeResponse response = new ApiServiceDto.CodeResponse();
            response.setResultCd(resCode.getResCode());
            response.setResultMsg(resCode.getMessage());
            response.setResultProcDt(LocalDateTime.now());
            return response;
            
        } catch (Exception e) {
            log.error("Error while getting response code: {}", code, e);
            // 하드코딩된 기본 에러 응답
            ApiServiceDto.CodeResponse response = new ApiServiceDto.CodeResponse();
            response.setResultCd("500");
            response.setResultMsg("시스템 오류가 발생하였습니다");
            response.setResultProcDt(LocalDateTime.now());
            return response;
        }
    }
    
    private ApiServiceDto.CodeResponse createErrorResponse(String code) {
        return createResponse(code);
    }
    
    private ApiServiceDto.DateSearchResponse createDateSearchErrorResponse(String code) {
        ApiServiceDto.CodeData resCode = apiServiceMapper.getResCode(code);
        ApiServiceDto.DateSearchResponse response = new ApiServiceDto.DateSearchResponse();
        response.setResultCd(resCode.getResCode());
        response.setResultMsg(resCode.getMessage());
        response.setResultProcDt(LocalDateTime.now());
        return response;
    }
    
    private ApiServiceDto.ProductSearchResponse createProductSearchErrorResponse(String code) {
        ApiServiceDto.CodeData resCode = apiServiceMapper.getResCode(code);
        ApiServiceDto.ProductSearchResponse response = new ApiServiceDto.ProductSearchResponse();
        response.setResultCd(resCode.getResCode());
        response.setResultMsg(resCode.getMessage());
        response.setResultProcDt(LocalDateTime.now());
        return response;
    }
} 