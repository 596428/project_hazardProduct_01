package com.example.controller;

import com.example.domain.DangerousGoods;
import com.example.dto.SearchCondition;
import com.example.service.DangerousGoodsService;
import com.example.dto.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dangerous-goods")
@RequiredArgsConstructor
public class DangerousGoodsController {

    private final DangerousGoodsService dangerousGoodsService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchDangerousGoods(SearchCondition condition) {
        if (condition.getPage() == null) condition.setPage(0);
        if (condition.getSize() == null) condition.setSize(10);

        List<DangerousGoods> items = dangerousGoodsService.searchDangerousGoods(condition);
        int totalCount = dangerousGoodsService.countDangerousGoods(condition);

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("totalCount", totalCount);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseDto<String> updateGoods(@RequestBody List<Map<String, Object>> changes) {
        try {
            System.out.println("Received changes: " + changes);  // 받은 데이터 출력
            dangerousGoodsService.updateGoods(changes);
            return ResponseDto.successMessage("수정이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();  // 스택 트레이스 출력
            return ResponseDto.error(e.getMessage(), "UPDATE_FAIL");
        }
    }
} 