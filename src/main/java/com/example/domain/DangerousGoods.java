package com.example.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DangerousGoods {
    private String mstId;
    private String mstDataId;
    private String inspInstCd;
    private String docNo;
    private String docCycl;
    private String inspInstNm;
    private String ntfctnRtrcnYn;
    private String rptTypeCd;
    private String rptTypeNm;
    private String prdctNm;
    private String prdctTypeNm;
    private String rtrvlRsnNm;
    private LocalDateTime regDt;
    private LocalDateTime ntfctnDt;    // 고시일자
    private String mnftrYmd;           // 제조일자
    private String rtlTermCn;          // 유통기한
    private String cmdBgngDdCn;    // 고시일자
    private String bzentyTypeNm;    // 업체유형명
    private String bzentyNm;        // 업체명
    private String icptInspArtclCn; // 부적합 검사 항목
    private String icptInspSpcfctCn;// 부적합 검사 규격
    private String icptInspRsltCn;  // 부적합 검사 결과
    // ... 필요한 필드 추가
} 