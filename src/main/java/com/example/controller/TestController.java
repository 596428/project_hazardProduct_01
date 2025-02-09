package com.example.controller;

import com.example.domain.DangerousGoods;
import com.example.service.DangerousGoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.dto.SearchCondition;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.domain.User;
import com.example.mapper.UserMapper;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final DangerousGoodsService dangerousGoodsService;
    private final UserMapper userMapper;

    @GetMapping("/test01")
    public String test01(Model model, 
                        @RequestParam(required = false) String keyword,
                        @RequestParam(defaultValue = "0") int page) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> pageData;
        if (keyword != null && !keyword.trim().isEmpty()) {
            SearchCondition condition = new SearchCondition();
            condition.setKeyword(keyword);
            condition.setPage(page);
            condition.setSize(40);
            List<DangerousGoods> goodsList = dangerousGoodsService.searchDangerousGoods(condition);
            model.addAttribute("goodsList", goodsList);
        } else {
            pageData = dangerousGoodsService.getPaginatedGoods(page);
            model.addAttribute("goodsList", pageData.get("items"));
            model.addAttribute("currentPage", pageData.get("currentPage"));
            model.addAttribute("totalPages", pageData.get("totalPages"));
        }
        
        model.addAttribute("keyword", keyword);
        model.addAttribute("auth", auth);
        
        return "test01";
    }

    @GetMapping("/test02")
    public String test02() {
        return "test02";
    }

    @GetMapping("/test03")
    public String test03(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        
        User userInfo = userMapper.findByEmail(userEmail).orElseThrow();
        System.out.println("User Email: " + userInfo.getEmail());
        System.out.println("User Name: " + userInfo.getUserName());
        System.out.println("User Role: " + userInfo.getRoleCd());
        
        model.addAttribute("user", userInfo);
        
        return "test03";
    }

    @GetMapping("/test04")
    public String test04(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", auth);
        return "test04";
    }

    @GetMapping("/test05")
    public String test05(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("auth", auth);
        return "test05";
    }
} 