package com.example.dto.common;

import lombok.Data;

@Data
public class ResponseDto<T> {
    private String status;  // SUCCESS, ERROR
    private T data;
    private String message;
    private String errorCode;

    // 데이터 포함 성공 응답용 정적 팩토리 메서드
    public static <T> ResponseDto<T> successMessage(T data) {
        ResponseDto<T> response = new ResponseDto<>();
        response.setStatus("SUCCESS");
        response.setData(data);
        return response;
    }

    public static <T> ResponseDto<T> successWithDataAndMessage(T data, String message) {
        ResponseDto<T> response = new ResponseDto<>();
        response.setStatus("SUCCESS");
        response.setData(data);
        response.setMessage(message);
        return response;
    }

    // 에러 응답용 정적 팩토리 메서드
    public static <T> ResponseDto<T> error(String message, String errorCode) {
        ResponseDto<T> response = new ResponseDto<>();
        response.setStatus("ERROR");
        response.setMessage(message);
        response.setErrorCode(errorCode);
        return response;
    }

    // private 기본 생성자
    private ResponseDto() {}
}